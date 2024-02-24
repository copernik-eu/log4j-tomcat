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

import aQute.bnd.annotation.Resolution;
import aQute.bnd.annotation.spi.ServiceProvider;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContextFactory;

/**
 * An implementation of {@link Log} that forwards everything to the appropriate Log4j API logger.
 */
@ServiceProvider(value = Log.class, resolution = Resolution.OPTIONAL)
public class Log4jLog implements Log {

    private static final String FQCN = Log4jLog.class.getName();
    private static final String LOG_FACTORY_FQCN = LogFactory.class.getName();
    private static final Marker TOMCAT_MARKER = MarkerManager.getMarker("TOMCAT");

    private ExtendedLogger logger;

    // Only used by ServiceLoader
    public Log4jLog() {
        this(LogManager.ROOT_LOGGER_NAME);
    }

    public Log4jLog(final String name) {
        this(LogManager.getFactory(), name);
    }

    Log4jLog(final LoggerContextFactory factory, final String name) {
        this.logger = factory.getContext(LOG_FACTORY_FQCN, null, null, false).getLogger(name);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled(TOMCAT_MARKER);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled(TOMCAT_MARKER);
    }

    @Override
    public boolean isFatalEnabled() {
        return logger.isFatalEnabled(TOMCAT_MARKER);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled(TOMCAT_MARKER);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled(TOMCAT_MARKER);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled(TOMCAT_MARKER);
    }

    @Override
    public void trace(final Object message) {
        logger.logIfEnabled(FQCN, Level.TRACE, TOMCAT_MARKER, message, null);
    }

    @Override
    public void trace(final Object message, final Throwable t) {
        logger.logIfEnabled(FQCN, Level.TRACE, TOMCAT_MARKER, message, t);
    }

    @Override
    public void debug(final Object message) {
        logger.logIfEnabled(FQCN, Level.DEBUG, TOMCAT_MARKER, message, null);
    }

    @Override
    public void debug(final Object message, final Throwable t) {
        logger.logIfEnabled(FQCN, Level.DEBUG, TOMCAT_MARKER, message, t);
    }

    @Override
    public void info(final Object message) {
        logger.logIfEnabled(FQCN, Level.INFO, TOMCAT_MARKER, message, null);
    }

    @Override
    public void info(final Object message, final Throwable t) {
        logger.logIfEnabled(FQCN, Level.INFO, TOMCAT_MARKER, message, t);
    }

    @Override
    public void warn(final Object message) {
        logger.logIfEnabled(FQCN, Level.WARN, TOMCAT_MARKER, message, null);
    }

    @Override
    public void warn(final Object message, final Throwable t) {
        logger.logIfEnabled(FQCN, Level.WARN, TOMCAT_MARKER, message, t);
    }

    @Override
    public void error(final Object message) {
        logger.logIfEnabled(FQCN, Level.ERROR, TOMCAT_MARKER, message, null);
    }

    @Override
    public void error(final Object message, final Throwable t) {
        logger.logIfEnabled(FQCN, Level.ERROR, TOMCAT_MARKER, message, t);
    }

    @Override
    public void fatal(final Object message) {
        logger.logIfEnabled(FQCN, Level.FATAL, TOMCAT_MARKER, message, null);
    }

    @Override
    public void fatal(final Object message, final Throwable t) {
        logger.logIfEnabled(FQCN, Level.FATAL, TOMCAT_MARKER, message, t);
    }
}
