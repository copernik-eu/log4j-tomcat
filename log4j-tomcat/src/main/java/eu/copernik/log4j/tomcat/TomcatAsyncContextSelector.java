/*
 * Copyright © 2023 Piotr P. Karwasz
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

import java.net.URI;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerContext;

/**
 * The asynchronous version of {@link TomcatContextSelector}:
 * <ul>
 *     <li>It assigns a different logger context to each web application running on Tomcat.</li>
 *     <li>It create asynchronous loggers.</li>
 * </ul>
 * @see <a href="https://oss.copernik.eu/tomcat/3.x/components/log4j-tomcat#TomcatContextSelector">Tomcat Context Selectors</a>
 */
public class TomcatAsyncContextSelector extends TomcatContextSelector {

    @Override
    protected LoggerContext createContext(final String name, final URI configLocation) {
        final AsyncLoggerContext context = new AsyncLoggerContext(name, null, configLocation);
        context.addShutdownListener(this);
        return context;
    }
}
