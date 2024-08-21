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
package eu.copernik.log4j.tomcat.junit;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.juli.WebappProperties;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

public class ContextClassLoaderExtension implements BeforeAllCallback {
    public static final String ENGINE_NAME = "Catalina";
    public static final String HOST_NAME = "localhost";
    public static final String CONTEXT_NAME = "/myapp";

    @Override
    public void beforeAll(final ExtensionContext context) throws IOException {
        final ClassLoader originalTccl = Thread.currentThread().getContextClassLoader();
        final ClassLoader tccl = new TestClassLoader();
        Thread.currentThread().setContextClassLoader(tccl);
        context.getStore(Namespace.GLOBAL).put(ContextClassLoaderExtension.class, (Store.CloseableResource)
                () -> Thread.currentThread().setContextClassLoader(originalTccl));
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
