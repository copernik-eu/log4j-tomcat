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

import eu.copernik.log4j.tomcat.junit.ContextClassLoaderExtension;
import java.util.Map;
import java.util.function.BiFunction;
import org.apache.logging.log4j.core.util.ContextDataProvider;
import org.apache.logging.log4j.test.junit.SetTestProperty;
import org.apache.logging.log4j.util.StringMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ContextClassLoaderExtension.class)
class TomcatContextDataProviderTest {

    @Test
    @SetTestProperty(key = "log4j2.tomcatContextDataEnabled", value = "true")
    void supplyContextData() {
        assertData(new TomcatContextDataProvider().supplyContextData(), Map::get);
    }

    @Test
    @SetTestProperty(key = "log4j2.tomcatContextDataEnabled", value = "true")
    void supplyStringMap() {
        assertData(new TomcatContextDataProvider().supplyStringMap(), StringMap::getValue);
    }

    private static <T> void assertData(final T map, final BiFunction<T, ? super String, String> getValue) {
        assertThat(getValue.apply(map, "engine.name")).isEqualTo(ContextClassLoaderExtension.ENGINE_NAME);
        assertThat(getValue.apply(map, "host.name")).isEqualTo(ContextClassLoaderExtension.HOST_NAME);
        assertThat(getValue.apply(map, "context.name")).isEqualTo(ContextClassLoaderExtension.CONTEXT_NAME);
    }

    @Test
    void when_context_data_disabled_maps_are_empty() {
        final ContextDataProvider dataProvider = new TomcatContextDataProvider();
        assertThat(dataProvider.supplyContextData()).isEmpty();
        assertThat(dataProvider.supplyStringMap())
                .extracting(StringMap::isEmpty)
                .as("Check if context data is empty")
                .isEqualTo(true);
    }
}
