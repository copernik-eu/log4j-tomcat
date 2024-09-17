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
package eu.copernik.tomcat.juli.to.log4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.test.appender.ListAppender;
import org.junit.jupiter.api.Test;

class Log4jLogTest {

    private static final String MESSAGE = "MESSAGE";

    @Test
    void location() {
        Log log = LogFactory.getLog("location");
        Throwable t = new Throwable();
        int i = 0;
        int currentLine = 45;
        log.trace(MESSAGE + i++);
        log.trace(MESSAGE + i++, t);
        log.debug(MESSAGE + i++);
        log.debug(MESSAGE + i++, t);
        log.info(MESSAGE + i++);
        log.info(MESSAGE + i++, t);
        log.warn(MESSAGE + i++);
        log.warn(MESSAGE + i++, t);
        log.error(MESSAGE + i++);
        log.error(MESSAGE + i++, t);
        log.fatal(MESSAGE + i++);
        log.fatal(MESSAGE + i, t);
        // Verification
        final LoggerContext context = LoggerContext.getContext(false);
        final Configuration config = context.getConfiguration();
        final ListAppender list = config.getAppender("list");
        final List<LogEvent> events = list.getEvents();
        i = 0;
        assertThat(events).hasSize(12);
        assertLocation(events.get(i), Level.TRACE, MESSAGE + i++, null, ++currentLine);
        assertLocation(events.get(i), Level.TRACE, MESSAGE + i++, t, ++currentLine);
        assertLocation(events.get(i), Level.DEBUG, MESSAGE + i++, null, ++currentLine);
        assertLocation(events.get(i), Level.DEBUG, MESSAGE + i++, t, ++currentLine);
        assertLocation(events.get(i), Level.INFO, MESSAGE + i++, null, ++currentLine);
        assertLocation(events.get(i), Level.INFO, MESSAGE + i++, t, ++currentLine);
        assertLocation(events.get(i), Level.WARN, MESSAGE + i++, null, ++currentLine);
        assertLocation(events.get(i), Level.WARN, MESSAGE + i++, t, ++currentLine);
        assertLocation(events.get(i), Level.ERROR, MESSAGE + i++, null, ++currentLine);
        assertLocation(events.get(i), Level.ERROR, MESSAGE + i++, t, ++currentLine);
        assertLocation(events.get(i), Level.FATAL, MESSAGE + i++, null, ++currentLine);
        assertLocation(events.get(i), Level.FATAL, MESSAGE + i, t, ++currentLine);
    }

    private void assertLocation(
            final LogEvent event, final Level level, final String message, final Throwable t, final int lineNumber) {
        assertThat(event.getLevel()).isEqualTo(level);
        assertThat(event.getMessage().getFormattedMessage()).isEqualTo(message);
        assertThat(event.getThrown()).isEqualTo(t);
        final StackTraceElement location = event.getSource();
        assertThat(location.getClassName()).isEqualTo(Log4jLogTest.class.getName());
        assertThat(location.getMethodName()).isEqualTo("location");
        assertThat(location.getLineNumber()).isEqualTo(lineNumber);
    }

    @Test
    void checkFiltering() {
        final Configuration configuration = LoggerContext.getContext(false).getConfiguration();
        final Marker expectedMarker = MarkerManager.getMarker("TOMCAT");
        final AtomicReference<Level> expectedLevel = new AtomicReference<>();
        final Filter filter = new AbstractFilter() {
            @Override
            public Result filter(
                    final Logger logger,
                    final Level level,
                    final Marker marker,
                    final Object object,
                    final Throwable throwable) {
                return expectedMarker.equals(marker) && level.equals(expectedLevel.get()) ? Result.ACCEPT : Result.DENY;
            }
        };
        configuration.addFilter(filter);
        try {
            final Log log = LogFactory.getLog("levels");
            expectedLevel.set(Level.DEBUG);
            assertThat(log.isDebugEnabled()).isTrue();
            expectedLevel.set(Level.ERROR);
            assertThat(log.isErrorEnabled()).isTrue();
            expectedLevel.set(Level.FATAL);
            assertThat(log.isFatalEnabled()).isTrue();
            expectedLevel.set(Level.INFO);
            assertThat(log.isInfoEnabled()).isTrue();
            expectedLevel.set(Level.TRACE);
            assertThat(log.isTraceEnabled()).isTrue();
            expectedLevel.set(Level.WARN);
            assertThat(log.isWarnEnabled()).isTrue();
        } finally {
            configuration.removeFilter(filter);
        }
    }
}
