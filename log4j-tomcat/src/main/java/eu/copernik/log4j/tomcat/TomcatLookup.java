/*
 * Copyright © 2022-2023 Piotr P. Karwasz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.copernik.log4j.tomcat;

import org.apache.juli.WebappProperties;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

/**
 * Resolves the names specific of Tomcat specific components:
 * <ul>
 *     <li>The current Tomcat Context.</li>
 *     <li>The current Tomcat Host.</li>
 *     <li>The current Tomcat Engine.</li>
 * </ul>
 * @see <a href="https://oss.copernik.eu/tomcat/3.x/components/log4j-tomcat#TomcatLookup">Tomcat Lookup</a>
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
                    return String.format("org.apache.catalina.core.ContainerBase.[%s]", props.getServiceName());
                case HOST_NAME:
                case HOST_NAME_COMPAT:
                    return props.getHostName();
                case HOST_LOGGER:
                    return String.format(
                            "org.apache.catalina.core.ContainerBase.[%s].[%s]",
                            props.getServiceName(), props.getHostName());
                case CONTEXT_NAME:
                case CONTEXT_NAME_COMPAT:
                    return props.getWebappName();
                case CONTEXT_LOGGER:
                    return String.format(
                            "org.apache.catalina.core.ContainerBase.[%s].[%s].[%s]",
                            props.getServiceName(), props.getHostName(), props.getWebappName());
            }
        }
        return null;
    }

    @Override
    public String lookup(LogEvent event, String key) {
        return lookup(key);
    }
}
