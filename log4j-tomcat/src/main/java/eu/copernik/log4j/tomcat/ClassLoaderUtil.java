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

final class ClassLoaderUtil {

    private ClassLoaderUtil() {}

    static boolean isLog4jApiResource(final String name, final boolean isClassName) {
        if (isClassName && name.startsWith("org.apache.logging.log4j.")) {
            if (name.indexOf('.', 25) == -1
                    || name.startsWith("internal.", 25)
                    || name.startsWith("message.", 25)
                    || name.startsWith("simple.", 25)
                    || name.startsWith("spi.", 25)
                    || name.startsWith("status.", 25)
                    || name.startsWith("util.", 25)) {
                return true;
            }
        } else if (!isClassName && name.startsWith("org/apache/logging/log4j/")) {
            if (name.indexOf('/', 25) == -1
                    || name.startsWith("internal/", 25)
                    || name.startsWith("message/", 25)
                    || name.startsWith("simple/", 25)
                    || name.startsWith("spi/", 25)
                    || name.startsWith("status/", 25)
                    || name.startsWith("util/", 25)) {
                return true;
            }
        }
        return false;
    }
}
