/*
 * Copyright © 2023 Piotr P. Karwasz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.copernik.log4j.tomcat.juli.core;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.juli.WebappProperties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.selector.ContextSelector;
import org.apache.logging.log4j.spi.LoggerContextShutdownAware;
import org.apache.logging.log4j.status.StatusLogger;

public class TomcatContextSelector implements ContextSelector, LoggerContextShutdownAware {

    private static final String GLOBAL_CONTEXT_NAME = "-tomcat";
    private static final Logger LOGGER = StatusLogger.getLogger();

    private final AtomicReference<LoggerContext> GLOBAL_CONTEXT = new AtomicReference<>();
    private final ConcurrentMap<String, AtomicReference<WeakReference<LoggerContext>>> CONTEXT_MAP =
            new ConcurrentHashMap<>();

    @Override
    public boolean hasContext(final String fqcn, final ClassLoader loader, final boolean currentContext) {
        if (currentContext && ContextAnchor.THREAD_CONTEXT.get() != null) {
            return true;
        }
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        if (tccl instanceof WebappProperties) {
            final String name = getContextName((WebappProperties) tccl);
            final AtomicReference<WeakReference<LoggerContext>> ref = CONTEXT_MAP.get(name);
            if (ref == null) {
                return false;
            }
            final WeakReference<LoggerContext> weakRef = ref.get();
            return weakRef != null && weakRef.get() != null;
        }
        return GLOBAL_CONTEXT.get() != null;
    }

    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext) {
        return getContext(fqcn, loader, null, currentContext);
    }

    @Override
    public LoggerContext getContext(
            final String fqcn,
            final ClassLoader loader,
            final Entry<String, Object> entry,
            final boolean currentContext) {
        return getContext(fqcn, loader, entry, currentContext, null);
    }

    @Override
    public LoggerContext getContext(
            final String fqcn, final ClassLoader loader, final boolean currentContext, final URI configLocation) {
        return getContext(fqcn, loader, null, currentContext, configLocation);
    }

    @Override
    public LoggerContext getContext(
            final String fqcn,
            final ClassLoader loader,
            final Entry<String, Object> entry,
            final boolean currentContext,
            final URI configLocation) {
        if (currentContext) {
            final LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
            if (ctx != null) {
                return ctx;
            }
        }
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final LoggerContext ctx;
        if (tccl instanceof WebappProperties) {
            final String name = getContextName((WebappProperties) tccl);
            final AtomicReference<WeakReference<LoggerContext>> ref =
                    CONTEXT_MAP.computeIfAbsent(name, ignored -> new AtomicReference<>());
            final WeakReference<LoggerContext> weakRef = ref.get();
            final LoggerContext oldCtx = weakRef != null ? weakRef.get() : null;
            if (oldCtx != null) {
                ctx = oldCtx;
            } else {
                ctx = createContext(name, configLocation);
                ref.compareAndSet(weakRef, new WeakReference<>(ctx));
            }
        } else {
            ctx = getGlobal();
        }
        if (entry != null) {
            final String key = entry.getKey();
            if (ctx.getObject(entry.getKey()) == null) {
                LOGGER.debug("Setting logger context key {} to {}.", entry.getKey(), entry.getValue());
                ctx.putObject(entry.getKey(), entry.getValue());
            } else if (!ctx.getObject(key).equals(entry.getValue())) {
                LOGGER.warn(
                        "Existing logger context has {} associated to the key {}. Can not change it to {}.",
                        ctx.getObject(key),
                        key,
                        entry.getValue());
            }
        }
        if (ctx.getConfigLocation() == null && configLocation != null) {
            LOGGER.debug("Setting configuration to {}.", configLocation);
            ctx.setConfigLocation(configLocation);
        } else if (ctx.getConfigLocation() != null
                && configLocation != null
                && !ctx.getConfigLocation().equals(configLocation)) {
            LOGGER.warn(
                    "Existing logger context has configuration {}. Can not change it to {}",
                    ctx.getConfigLocation(),
                    configLocation);
        }
        return ctx;
    }

    @Override
    public List<LoggerContext> getLoggerContexts() {
        final List<LoggerContext> loggerContexts = new ArrayList<>(CONTEXT_MAP.size() + 1);
        CONTEXT_MAP.values().forEach(ref -> {
            final WeakReference<LoggerContext> weakRef = ref.get();
            final LoggerContext ctx = weakRef != null ? weakRef.get() : null;
            if (ctx != null) {
                loggerContexts.add(ctx);
            }
        });
        final LoggerContext global = GLOBAL_CONTEXT.get();
        if (global != null) {
            loggerContexts.add(global);
        }
        return loggerContexts;
    }

    @Override
    public void removeContext(final LoggerContext context) {
        CONTEXT_MAP.remove(context.getName());
        GLOBAL_CONTEXT.compareAndSet(context, null);
    }

    @Override
    public boolean isClassLoaderDependent() {
        return false;
    }

    protected LoggerContext createContext(final String name, final URI configLocation) {
        final LoggerContext context = new LoggerContext(name, null, configLocation);
        context.addShutdownListener(this);
        return context;
    }

    private String getContextName(final WebappProperties props) {
        return String.format("/%s/%s/%s", props.getServiceName(), props.getHostName(), props.getWebappName());
    }

    private LoggerContext getGlobal() {
        final LoggerContext ctx = GLOBAL_CONTEXT.get();
        if (ctx != null) {
            return ctx;
        }
        GLOBAL_CONTEXT.compareAndSet(null, createContext(GLOBAL_CONTEXT_NAME, null));
        return GLOBAL_CONTEXT.get();
    }

    @Override
    public void contextShutdown(final org.apache.logging.log4j.spi.LoggerContext loggerContext) {
        if (loggerContext instanceof LoggerContext) {
            CONTEXT_MAP.remove(((LoggerContext) loggerContext).getName());
        }
    }
}
