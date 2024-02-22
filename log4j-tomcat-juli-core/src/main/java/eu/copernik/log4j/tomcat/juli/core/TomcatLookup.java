/*
 * Copyright © 2022-2023 Piotr P. Karwasz
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

import org.apache.juli.WebappProperties;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

/**
 * Resolves the names specific to Tomcat's internal component structure. The names of the properties
 * starting with {@code classloader.} are kept for compatibility with the original Tomcat JULI
 * implementation.
 */
@Plugin(name = "tomcat", category = StrLookup.CATEGORY)
public class TomcatLookup implements StrLookup {

    static final String CONTEXT_NAME = "context.name";
    static final String CONTEXT_NAME_COMPAT = "classloader.webappName";
    static final String CONTEXT_LOGGER = "context.logger";
    static final String ENGINE_NAME = "engine.name";
    static final String ENGINE_NAME_COMPAT = "classloader.serviceName";
    static final String ENGINE_LOGGER = "engine.logger";
    static final String HOST_NAME = "host.name";
    static final String HOST_NAME_COMPAT = "classloader.hostName";
    static final String HOST_LOGGER = "host.logger";

    private static final String SERVICE_LOGGER_FORMAT = "org.apache.catalina.core.ContainerBase.[%s]";
    private static final String HOST_LOGGER_FORMAT = "org.apache.catalina.core.ContainerBase.[%s].[%s]";
    private static final String CONTEXT_LOGGER_FORMAT = "org.apache.catalina.core.ContainerBase.[%s].[%s].[%s]";

    public static final TomcatLookup INSTANCE = new TomcatLookup();

    @Override
    public String lookup(String key) {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl instanceof WebappProperties && key != null) {
            final WebappProperties props = (WebappProperties) cl;
            switch (key) {
                case ENGINE_NAME:
                case ENGINE_NAME_COMPAT:
                    return props.getServiceName();
                case ENGINE_LOGGER:
                    return String.format(SERVICE_LOGGER_FORMAT, props.getServiceName());
                case HOST_NAME:
                case HOST_NAME_COMPAT:
                    return props.getHostName();
                case HOST_LOGGER:
                    return String.format(HOST_LOGGER_FORMAT, props.getServiceName(), props.getHostName());
                case CONTEXT_NAME:
                case CONTEXT_NAME_COMPAT:
                    return props.getWebappName();
                case CONTEXT_LOGGER:
                    return String.format(
                            CONTEXT_LOGGER_FORMAT, props.getServiceName(), props.getHostName(), props.getWebappName());
            }
        }
        return null;
    }

    @Override
    public String lookup(LogEvent event, String key) {
        return lookup(key);
    }
}
