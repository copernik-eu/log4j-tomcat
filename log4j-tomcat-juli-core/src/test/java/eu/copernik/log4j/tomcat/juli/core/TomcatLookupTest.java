/*
 * Copyright © 2022 Piotr P. Karwasz
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import eu.copernik.log4j.tomcat.juli.core.junit.ContextClassLoaderExtension;
import java.util.stream.Stream;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@ExtendWith(ContextClassLoaderExtension.class)
class TomcatLookupTest {

    private static final String ENGINE_LOGGERNAME = "org.apache.catalina.core.ContainerBase.[" + ENGINE_NAME + "]";
    private static final String HOST_LOGGERNAME = ENGINE_LOGGERNAME + ".[" + HOST_NAME + "]";
    private static final String CONTEXT_LOGGERNAME = HOST_LOGGERNAME + ".[" + CONTEXT_NAME + "]";

    public static Stream<Arguments> lookupWorksProperly() {
        return Stream.of(
                Arguments.of("classloader.serviceName", ENGINE_NAME),
                Arguments.of("engine.name", ENGINE_NAME),
                Arguments.of("engine.logger", ENGINE_LOGGERNAME),
                Arguments.of("classloader.hostName", HOST_NAME),
                Arguments.of("host.name", HOST_NAME),
                Arguments.of("host.logger", HOST_LOGGERNAME),
                Arguments.of("classloader.webappName", CONTEXT_NAME),
                Arguments.of("context.name", CONTEXT_NAME),
                Arguments.of("context.logger", CONTEXT_LOGGERNAME),
                Arguments.of(null, null),
                Arguments.of("unknown", null));
    }

    @ParameterizedTest
    @MethodSource
    void lookupWorksProperly(final String key, final String value) {
        final LogEvent event = mock(LogEvent.class);
        final StrLookup lookup = new TomcatLookup();
        assertThat(lookup.lookup(key)).isEqualTo(value);
        assertThat(lookup.lookup(event, key)).isEqualTo(value);
        verifyNoInteractions(event);
    }
}
