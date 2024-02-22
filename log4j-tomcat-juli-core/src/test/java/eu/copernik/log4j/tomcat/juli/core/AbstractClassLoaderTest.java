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

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.juli.WebappProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

class AbstractClassLoaderTest {
    protected static final String ENGINE_NAME = "Catalina";
    protected static final String HOST_NAME = "localhost";
    protected static final String CONTEXT_NAME = "/myapp";
    private static ClassLoader originalTccl;

    @BeforeAll
    public static void setupContextClassloader() throws IOException {
        originalTccl = Thread.currentThread().getContextClassLoader();
        final ClassLoader tccl = new TestClassLoader();
        Thread.currentThread().setContextClassLoader(tccl);
    }

    @AfterAll
    public static void clearContextClassloader() {
        Thread.currentThread().setContextClassLoader(originalTccl);
    }

    private static class TestClassLoader extends URLClassLoader implements WebappProperties {

        public TestClassLoader() {
            super(new URL[0]);
        }

        @Override
        public String getWebappName() {
            return CONTEXT_NAME;
        }

        @Override
        public String getHostName() {
            return HOST_NAME;
        }

        @Override
        public String getServiceName() {
            return ENGINE_NAME;
        }

        @Override
        public boolean hasLoggingConfig() {
            return false;
        }
    }
}
