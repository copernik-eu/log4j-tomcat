/*
 * Copyright © 2022 Piotr P. Karwasz
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceRoot.ResourceSetType;
import org.apache.catalina.loader.WebappClassLoader;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.IOUtils;
import org.apache.logging.log4j.internal.DefaultLogBuilder;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.status.StatusListener;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.junit.jupiter.api.MethodOrderer.Random;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestMethodOrder(Random.class)
class Log4jWebappClassLoaderTest {

    private static final String LOG4J_API_LINK = "org.apache.logging.log4j.api.link";
    private static final String LOG4J_CORE_LINK = "org.apache.logging.log4j.core.link";

    private static URL findJar(final String link) throws IOException {
        final ClassLoader cl = Log4jWebappClassLoaderTest.class.getClassLoader();
        try (final InputStream is = cl.getResourceAsStream(link);
                final Reader reader = new InputStreamReader(is, StandardCharsets.US_ASCII)) {
            return new URL(IOUtils.toString(reader));
        }
    }

    private static WebResourceRoot createResources() {
        return assertDoesNotThrow(() -> {
            final WebResourceRoot resources = new StandardRoot(mock(Context.class));
            resources.createWebResourceSet(
                    ResourceSetType.CLASSES_JAR, "/WEB-INF/classes", findJar(LOG4J_API_LINK), "/");
            resources.createWebResourceSet(
                    ResourceSetType.CLASSES_JAR, "/WEB-INF/classes", findJar(LOG4J_CORE_LINK), "/");
            resources.start();
            return resources;
        });
    }

    private static <T extends WebappClassLoaderBase> T createClassLoader(
            final Class<T> clazz, final ClassLoader parent) {
        return createClassLoader(clazz, parent, createResources());
    }

    private static <T extends WebappClassLoaderBase> T createClassLoader(
            final Class<T> clazz, final ClassLoader parent, final WebResourceRoot resources) {
        try {
            final T cl;
            if (parent != null) {
                final Constructor<T> constructor = clazz.getConstructor(ClassLoader.class);
                cl = constructor.newInstance(parent);
            } else {
                final Constructor<T> constructor = clazz.getConstructor();
                cl = constructor.newInstance();
            }
            cl.setResources(resources);
            cl.start();
            return cl;
        } catch (final ReflectiveOperationException | LifecycleException e) {
            fail("Unable to instantiate classloader.", e);
        }
        // never reached
        return null;
    }

    static Stream<Arguments> classes() {
        return Stream.of(
                Arguments.of(LogManager.class, true, false),
                Arguments.of(DefaultLogBuilder.class, true, false),
                Arguments.of(Message.class, true, false),
                Arguments.of(SimpleLogger.class, true, false),
                Arguments.of(LoggerContext.class, true, false),
                Arguments.of(StatusListener.class, true, false),
                Arguments.of(PropertiesUtil.class, true, false),
                // Log4j Core is not delegated to parent
                Arguments.of(Configuration.class, false, false),
                // Class name under 25 characters (the prefix of all Log4j API classes)
                Arguments.of(Map.class, true, true),
                // Class name over 25 characters
                Arguments.of(WebResourceRoot.class, true, true));
    }

    @RepeatedTest(100)
    void testStandardClassloader() throws IOException {
        try (final URLClassLoader cl =
                createClassLoader(WebappClassLoader.class, getClass().getClassLoader())) {
            classes().forEach(arg -> assertLoadsCorrectClass((Class<?>) arg.get()[0], (boolean) arg.get()[2], cl));
        }
    }

    @RepeatedTest(100)
    void testLog4jClassloader() throws IOException {
        try (final URLClassLoader cl =
                createClassLoader(Log4jWebappClassLoader.class, getClass().getClassLoader())) {
            classes().forEach(arg -> assertLoadsCorrectClass((Class<?>) arg.get()[0], (boolean) arg.get()[1], cl));
        }
    }

    @RepeatedTest(100)
    void testParallelLog4jClassloader() throws IOException {
        try (final URLClassLoader cl = createClassLoader(
                Log4jParallelWebappClassLoader.class, getClass().getClassLoader())) {
            classes().forEach(arg -> assertLoadsCorrectClass((Class<?>) arg.get()[0], (boolean) arg.get()[1], cl));
        }
    }

    @Test
    void testLog4jClassloaderNoParent() throws IOException {
        try (final URLClassLoader cl = createClassLoader(Log4jWebappClassLoader.class, null)) {
            classes().forEach(arg -> assertLoadsCorrectClass((Class<?>) arg.get()[0], (boolean) arg.get()[1], cl));
        }
    }

    @Test
    void testParallelLog4jClassloaderNoParent() throws IOException {
        try (final URLClassLoader cl = createClassLoader(Log4jParallelWebappClassLoader.class, null)) {
            classes().forEach(arg -> assertLoadsCorrectClass((Class<?>) arg.get()[0], (boolean) arg.get()[1], cl));
        }
    }

    private void assertLoadsCorrectClass(
            final Class<?> clazz, final boolean shouldBeEqual, final URLClassLoader loader) {
        final String classResource = clazz.getName().replace('.', '/') + ".class";
        final URL clazzUrl = getClass().getClassLoader().getResource(classResource);
        final Class<?> otherClazz = assertDoesNotThrow(() -> Class.forName(clazz.getName(), true, loader));
        final URL otherClazzUrl = loader.getResource(classResource);
        if (shouldBeEqual) {
            assertThat(otherClazz).isEqualTo(clazz);
        } else {
            assertThat(otherClazz).isNotEqualTo(clazz);
        }
        // URLs are always equal
        assertThat(otherClazzUrl).isEqualTo(clazzUrl);
    }

    static Stream<Class<? extends WebappClassLoaderBase>> testCopyWithoutTransformers() {
        return Stream.of(Log4jWebappClassLoader.class, Log4jParallelWebappClassLoader.class);
    }

    @ParameterizedTest
    @MethodSource
    void testCopyWithoutTransformers(final Class<? extends WebappClassLoaderBase> loaderClass) throws IOException {
        try (final WebappClassLoaderBase loader =
                createClassLoader(loaderClass, getClass().getClassLoader(), createResources())) {
            final ClassFileTransformer transformer = mock(ClassFileTransformer.class);
            loader.addTransformer(transformer);
            assertThat(loader.toString()).contains("Class file transformers");
            // Successful copy
            final ClassLoader copy = loader.copyWithoutTransformers();
            assertThat(copy.toString()).doesNotContain("Class file transformers");
        }
    }
}
