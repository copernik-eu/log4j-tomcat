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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.AsyncLogger;
import org.apache.logging.log4j.core.selector.ContextSelector;
import org.junit.jupiter.api.Test;

class GlobalTomcatContextSelectorTest {

    @Test
    void contextSelector() {
        final ContextSelector selector = new TomcatContextSelector();
        final LoggerContext context = selector.getContext(null, null, false);
        assertThat(context.getName()).isEqualTo("-tomcat");
    }

    @Test
    void asyncContextSelector() {
        final ContextSelector selector = new TomcatAsyncContextSelector();
        final LoggerContext context = selector.getContext(null, null, false);
        assertThat(context.getName()).isEqualTo("-tomcat");
        assertThat(context.getLogger(LogManager.ROOT_LOGGER_NAME)).isInstanceOf(AsyncLogger.class);
    }
}