/*
 * Copyright © 2024 Piotr P. Karwasz
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

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import org.apache.logging.log4j.core.LifeCycle.State;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.selector.ContextSelector;
import org.junit.jupiter.api.Test;

class GlobalTomcatContextSelectorTest {

    private static final URL CONFIG = GlobalTomcatContextSelectorTest.class.getResource("/dummy-log4j2.test");
    private static final URL CONFIG2 = GlobalTomcatContextSelectorTest.class.getResource("/dummy2-log4j2.test");

    @Test
    void contextSelector() throws Exception {
        final TomcatContextSelector selector = new TomcatContextSelector();
        final LoggerContext context = selector.getContext(null, null, false);
        assertThat(context).isExactlyInstanceOf(LoggerContext.class);
        final LoggerContext currentContext = new LoggerContext("current");
        ContextAnchor.THREAD_CONTEXT.set(currentContext);
        assertThat(context.getName()).isEqualTo("-tomcat");
        // Caused both contexts to be removed
        checkContextMethods(selector, context, currentContext);
        // Checks shutdown of new context
        checkShutdown(selector, context);
    }

    @Test
    void asyncContextSelector() throws Exception {
        final TomcatAsyncContextSelector selector = new TomcatAsyncContextSelector();
        final LoggerContext context = selector.getContext(null, null, false);
        assertThat(context).isExactlyInstanceOf(AsyncLoggerContext.class);
        final LoggerContext currentContext = new AsyncLoggerContext("current");
        ContextAnchor.THREAD_CONTEXT.set(currentContext);
        assertThat(context.getName()).isEqualTo("-tomcat");
        // Caused both contexts to be removed
        checkContextMethods(selector, context, currentContext);
        // Checks shutdown of new context
        checkShutdown(selector, context);
    }

    static void checkContextMethods(
            final ContextSelector selector, final LoggerContext context, final LoggerContext currentContext)
            throws Exception {
        assertThat(context.getState()).isEqualTo(State.INITIALIZED);
        assertThat(currentContext.getState()).isEqualTo(State.INITIALIZED);

        assertThat(selector.getContext(null, null, null, false)).isEqualTo(context);

        assertThat(selector.getContext(null, null, true)).isEqualTo(currentContext);
        assertThat(selector.getContext(null, null, null, true)).isEqualTo(currentContext);
        // With config location
        assertThat(selector.getContext(null, null, false, null)).isEqualTo(context);
        assertThat(selector.getContext(null, null, null, false, null)).isEqualTo(context);

        assertThat(selector.getContext(null, null, true, null)).isEqualTo(currentContext);
        assertThat(selector.getContext(null, null, null, true, null)).isEqualTo(currentContext);

        // Provide a configuration
        assertThat(CONFIG).isNotNull();
        final URI configLocation = CONFIG.toURI();

        assertThat(context.getConfigLocation()).isNull();
        assertThat(selector.getContext(null, null, false, configLocation)).isEqualTo(context);
        assertThat(selector.getContext(null, null, null, false, configLocation)).isEqualTo(context);
        assertThat(context.getConfigLocation()).isEqualTo(configLocation);
        assertThat(context.getState()).isEqualTo(State.INITIALIZED);

        assertThat(currentContext.getConfigLocation()).isNull();
        assertThat(selector.getContext(null, null, true, configLocation)).isEqualTo(currentContext);
        assertThat(selector.getContext(null, null, null, true, configLocation)).isEqualTo(currentContext);
        assertThat(currentContext.getConfigLocation()).isEqualTo(configLocation);
        assertThat(currentContext.getState()).isEqualTo(State.INITIALIZED);

        // Try changing configuration (it shouldn't change)
        assertThat(CONFIG2).isNotNull();
        final URI configLocation2 = CONFIG2.toURI();

        assertThat(selector.getContext(null, null, false, configLocation2)).isEqualTo(context);
        assertThat(selector.getContext(null, null, null, false, configLocation2))
                .isEqualTo(context);
        assertThat(context.getConfigLocation()).isEqualTo(configLocation);
        assertThat(context.getState()).isEqualTo(State.INITIALIZED);

        assertThat(selector.getContext(null, null, true, configLocation2)).isEqualTo(currentContext);
        assertThat(selector.getContext(null, null, null, true, configLocation2)).isEqualTo(currentContext);
        assertThat(currentContext.getConfigLocation()).isEqualTo(configLocation);
        assertThat(currentContext.getState()).isEqualTo(State.INITIALIZED);

        // Provide an external context (deprecated?)
        final Object object = new Object();
        final Entry<String, Object> entry = new SimpleEntry<>("object", object);

        assertThat(context.getObject("object")).isNull();
        assertThat(selector.getContext(null, null, entry, false, configLocation))
                .isEqualTo(context);
        assertThat(context.getObject("object")).isEqualTo(object);

        assertThat(currentContext.getObject("object")).isNull();
        assertThat(selector.getContext(null, null, entry, true, configLocation)).isEqualTo(currentContext);
        assertThat(currentContext.getObject("object")).isEqualTo(object);

        // Provide a different external context (deprecated?) shouldn't change this one
        final Entry<String, Object> entry2 = new SimpleEntry<>("object", new Object());
        assertThat(selector.getContext(null, null, entry2, false, configLocation))
                .isEqualTo(context);
        assertThat(context.getObject("object")).isEqualTo(object);

        assertThat(selector.getContext(null, null, entry2, true, configLocation))
                .isEqualTo(currentContext);
        assertThat(currentContext.getObject("object")).isEqualTo(object);

        // Count contexts
        assertThat(selector.getLoggerContexts()).containsExactly(context);
        assertThat(selector.hasContext(null, null, false)).isTrue();
        assertThat(selector.hasContext(null, null, true)).isTrue();

        // Remove current context: the global one will be a fallback
        ContextAnchor.THREAD_CONTEXT.set(null);
        assertThat(selector.getLoggerContexts()).containsExactly(context);
        assertThat(selector.hasContext(null, null, false)).isTrue();
        assertThat(selector.hasContext(null, null, true)).isTrue();
        assertThat(selector.getContext(null, null, true)).isEqualTo(context);

        // Remove global context
        selector.removeContext(context);
        assertThat(selector.getLoggerContexts()).isEmpty();
        assertThat(selector.hasContext(null, null, false)).isFalse();
        assertThat(selector.hasContext(null, null, true)).isFalse();
    }

    static void checkShutdown(final ContextSelector selector, final LoggerContext oldContext) {
        // A new context
        final LoggerContext context = selector.getContext(null, null, false);
        assertThat(context).isNotEqualTo(oldContext);
        // Shutdown the context
        assertThat(selector.getLoggerContexts()).containsExactly(context);
        context.stop();
        assertThat(selector.getLoggerContexts()).isEmpty();
    }
}
