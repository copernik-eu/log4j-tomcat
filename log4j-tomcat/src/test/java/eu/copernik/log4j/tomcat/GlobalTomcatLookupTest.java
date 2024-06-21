/*
 * Copyright © 2024 Piotr P. Karwasz
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.stream.Stream;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class GlobalTomcatLookupTest {

    static Stream<String> lookupWorksProperly() {
        return TomcatLookupTest.lookupWorksProperly().map(args -> (String) args.get()[0]);
    }

    @ParameterizedTest
    @MethodSource
    void lookupWorksProperly(final String key) {
        final LogEvent event = mock(LogEvent.class);
        final StrLookup lookup = new TomcatLookup();
        assertThat(lookup.lookup(key)).isNull();
        assertThat(lookup.lookup(event, key)).isNull();
        verifyNoInteractions(event);
    }
}
