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

import static eu.copernik.log4j.tomcat.juli.core.junit.ContextClassLoaderExtension.CONTEXT_NAME;
import static eu.copernik.log4j.tomcat.juli.core.junit.ContextClassLoaderExtension.ENGINE_NAME;
import static eu.copernik.log4j.tomcat.juli.core.junit.ContextClassLoaderExtension.HOST_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import eu.copernik.log4j.tomcat.juli.core.junit.ContextClassLoaderExtension;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.selector.ContextSelector;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@ExtendWith(ContextClassLoaderExtension.class)
class TomcatContextSelectorTest extends GlobalTomcatContextSelectorTest {

    @Override
    protected String getExpectedContextName() {
        return "/" + ENGINE_NAME + "/" + HOST_NAME + "/" + CONTEXT_NAME;
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
}
