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
package eu.copernik.tomcat.log4j.loader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.junit.jupiter.api.Test;

class ClassLoaderUtilTest {

    @Test
    void when_lifecycle_exception_then_illegal_state_exception() throws Exception {
        final Lifecycle lifecycle = mock(Lifecycle.class);
        assertDoesNotThrow(() -> ClassLoaderUtil.startUnchecked(lifecycle));
        doThrow(new LifecycleException()).when(lifecycle).start();
        assertThrows(IllegalStateException.class, () -> ClassLoaderUtil.startUnchecked(lifecycle));
    }
}
