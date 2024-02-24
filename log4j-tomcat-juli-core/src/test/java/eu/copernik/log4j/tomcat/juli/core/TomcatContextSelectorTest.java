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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.stream.Stream;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.selector.ContextSelector;
import org.apache.logging.log4j.spi.LoggerContextShutdownAware;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class TomcatContextSelectorTest extends AbstractClassLoaderTest {

    static Stream<Arguments> selectors() {
        return Stream.of(
                Arguments.of(new TomcatContextSelector(), LoggerContext.class),
                Arguments.of(new TomcatAsyncContextSelector(), AsyncLoggerContext.class));
    }

    @ParameterizedTest
    @MethodSource("selectors")
    void allMethods(final ContextSelector selector, final Class<? extends LoggerContext> contextClass)
            throws Exception {
        final LoggerContext context = selector.getContext(null, null, false);
        assertThat(context).isExactlyInstanceOf(contextClass);
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

    @ParameterizedTest
    @MethodSource("selectors")
    void testContextGarbageCollection(final ContextSelector selector) throws InterruptedException {
        final ReferenceQueue<LoggerContext> queue = new ReferenceQueue<>();
        final WeakReference<LoggerContext> reference =
                new WeakReference<>(selector.getContext(null, null, false), queue);
        assertThat(reference.get()).isNotNull();
        // Perform GC
        System.gc();
        queue.remove(5000);
        assertThat(reference.get()).as("Context should be garbage collected").isNull();
        // Test methods the retrieve the context
        assertThat(selector.hasContext(null, null, false)).isFalse();
        assertThat(selector.getLoggerContexts()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("selectors")
    void testShutdownSupportOtherLoggerContextTypes(final LoggerContextShutdownAware selector) {
        assertDoesNotThrow(
                () -> selector.contextShutdown(Mockito.mock(org.apache.logging.log4j.spi.LoggerContext.class)));
    }
}
