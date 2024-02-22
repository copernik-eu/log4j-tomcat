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

import java.util.Map;
import java.util.function.BiFunction;
import org.apache.logging.log4j.util.StringMap;
import org.junit.jupiter.api.Test;

class TomcatContextDataProviderTest extends AbstractClassLoaderTest {

    @Test
    void supplyContextData() {
        assertData(new TomcatContextDataProvider().supplyContextData(), Map::get);
    }

    @Test
    void supplyStringMap() {
        assertData(new TomcatContextDataProvider().supplyStringMap(), StringMap::getValue);
    }

    private static <T> void assertData(final T map, final BiFunction<T, ? super String, String> getValue) {
        assertThat(getValue.apply(map, "engine.name")).isEqualTo(ENGINE_NAME);
        assertThat(getValue.apply(map, "host.name")).isEqualTo(HOST_NAME);
        assertThat(getValue.apply(map, "context.name")).isEqualTo(CONTEXT_NAME);
    }
}
