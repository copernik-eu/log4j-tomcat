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
package eu.copernik.log4j.tomcat.juli;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.test.appender.ListAppender;
import org.junit.jupiter.api.Test;

class Log4jLogTest {

    private static final String MESSAGE = "MESSAGE";
    private static final Throwable T = new RuntimeException();

    @Test
    void location() {
        final Log log = LogFactory.getLog("location");
        int i = 0;
        int currentLine = 37;
        log.trace(MESSAGE + i++);
        log.trace(MESSAGE + i++, T);
        log.debug(MESSAGE + i++);
        log.debug(MESSAGE + i++, T);
        log.info(MESSAGE + i++);
        log.info(MESSAGE + i++, T);
        log.warn(MESSAGE + i++);
        log.warn(MESSAGE + i++, T);
        log.error(MESSAGE + i++);
        log.error(MESSAGE + i++, T);
        log.fatal(MESSAGE + i++);
        log.fatal(MESSAGE + i, T);
        // Verification
        final LoggerContext context = LoggerContext.getContext(false);
        final Configuration config = context.getConfiguration();
        final ListAppender list = config.getAppender("list");
        final List<LogEvent> events = list.getEvents();
        i = 0;
        assertThat(events).hasSize(12);
        assertLocation(events.get(i), Level.TRACE, MESSAGE + i++, null, ++currentLine);
        assertLocation(events.get(i), Level.TRACE, MESSAGE + i++, T, ++currentLine);
        assertLocation(events.get(i), Level.DEBUG, MESSAGE + i++, null, ++currentLine);
        assertLocation(events.get(i), Level.DEBUG, MESSAGE + i++, T, ++currentLine);
        assertLocation(events.get(i), Level.INFO, MESSAGE + i++, null, ++currentLine);
        assertLocation(events.get(i), Level.INFO, MESSAGE + i++, T, ++currentLine);
        assertLocation(events.get(i), Level.WARN, MESSAGE + i++, null, ++currentLine);
        assertLocation(events.get(i), Level.WARN, MESSAGE + i++, T, ++currentLine);
        assertLocation(events.get(i), Level.ERROR, MESSAGE + i++, null, ++currentLine);
        assertLocation(events.get(i), Level.ERROR, MESSAGE + i++, T, ++currentLine);
        assertLocation(events.get(i), Level.FATAL, MESSAGE + i++, null, ++currentLine);
        assertLocation(events.get(i), Level.FATAL, MESSAGE + i, T, ++currentLine);
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
}
