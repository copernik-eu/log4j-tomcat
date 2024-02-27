/*
 * Copyright © 2023 Piotr P. Karwasz
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
package eu.copernik.log4j.tomcat;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;

final class ClassLoaderUtil {

    static final int PREFIX_LENGTH = 25;

    private ClassLoaderUtil() {}

    static boolean isLog4jApiResource(final String name, final boolean isClassName) {
        return isClassName
                ? name.startsWith("org.apache.logging.log4j.")
                        && (name.indexOf('.', PREFIX_LENGTH) == -1
                                || name.startsWith("internal.", PREFIX_LENGTH)
                                || name.startsWith("message.", PREFIX_LENGTH)
                                || name.startsWith("simple.", PREFIX_LENGTH)
                                || name.startsWith("spi.", PREFIX_LENGTH)
                                || name.startsWith("status.", PREFIX_LENGTH)
                                || name.startsWith("util.", PREFIX_LENGTH))
                : name.startsWith("org/apache/logging/log4j/")
                        && (name.indexOf('/', PREFIX_LENGTH) == -1
                                || name.startsWith("internal/", PREFIX_LENGTH)
                                || name.startsWith("message/", PREFIX_LENGTH)
                                || name.startsWith("simple/", PREFIX_LENGTH)
                                || name.startsWith("spi/", PREFIX_LENGTH)
                                || name.startsWith("status/", PREFIX_LENGTH)
                                || name.startsWith("util/", PREFIX_LENGTH));
    }

    static void startUnchecked(final Lifecycle lifecycle) {
        try {
            lifecycle.start();
        } catch (final LifecycleException e) {
            throw new IllegalStateException(e);
        }
    }
}
