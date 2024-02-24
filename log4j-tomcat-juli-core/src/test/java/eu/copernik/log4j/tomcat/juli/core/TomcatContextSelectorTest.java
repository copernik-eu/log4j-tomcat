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

import static eu.copernik.log4j.tomcat.juli.core.GlobalTomcatContextSelectorTest.checkContextMethods;
import static eu.copernik.log4j.tomcat.juli.core.GlobalTomcatContextSelectorTest.checkShutdown;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.junit.jupiter.api.Test;

class TomcatContextSelectorTest extends AbstractClassLoaderTest {

    @Test
    void contextSelector() throws Exception {
        final TomcatContextSelector selector = new TomcatContextSelector();
        final LoggerContext context = selector.getContext(null, null, false);
        assertThat(context).isExactlyInstanceOf(LoggerContext.class);
        final LoggerContext currentContext = new LoggerContext("current");
        ContextAnchor.THREAD_CONTEXT.set(currentContext);
        assertThat(context.getName()).isEqualTo("/" + ENGINE_NAME + "/" + HOST_NAME + "/" + CONTEXT_NAME);
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
        final LoggerContext currentContext = new LoggerContext("current");
        ContextAnchor.THREAD_CONTEXT.set(currentContext);
        assertThat(context.getName()).isEqualTo("/" + ENGINE_NAME + "/" + HOST_NAME + "/" + CONTEXT_NAME);
        // Caused both contexts to be removed
        checkContextMethods(selector, context, currentContext);
        // Checks shutdown of new context
        checkShutdown(selector, context);
    }

    @Test
    void isNotClassLoaderDependent() {
        assertThat(new TomcatContextSelector().isClassLoaderDependent()).isFalse();
        assertThat(new TomcatAsyncContextSelector().isClassLoaderDependent()).isFalse();
    }
}
