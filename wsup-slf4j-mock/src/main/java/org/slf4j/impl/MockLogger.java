/*
 * Copyright (c) 2010 Greg Lindholm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slf4j.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

public final class MockLogger extends MarkerIgnoringBase
{
    private static final long              serialVersionUID   = -5434430959621654721L;

    public final static Level              DEFAULT_ROOT_LEVEL = Level.WARN;

    private final static MockLoggerContext context            = new MockLoggerContext();
    private final static List<LogEntry>    logEntries         = new ArrayList<LogEntry>();
    private static PrintStream             printStream        = System.out;

    /*
     * =================================================================
     * Static methods
     * =================================================================
     */

    /**
     * Configure MockLogger using a saved <code>context</code>.
     *
     * @param context the saved context from {@link MockLogger#copyContext()}
     */
    public static void configure(MockLoggerContext context)
    {
        context.configure(context);
    }

    /**
     * Build a copy of the current MockLoggerContext that can be used to configure MockLogger (
     * {@link MockLogger#configure(MockLogger)}) later.
     *
     * @return a copy of the current MockLoggerContext
     */
    public static MockLoggerContext copyContext()
    {
        MockLoggerContext copy = new MockLoggerContext();
        copy.configure(context);
        return copy;
    }

    /**
     * Clear the list of LogEntry's
     */
    public static void clear()
    {
        logEntries.clear();
    }

    /**
     * Reset all Loggers to there default setting (level=WARN, printing=true) and clear the
     * LogEntry's.
     */
    public static void resetAll()
    {
        logEntries.clear();
        context.setRootLevel(DEFAULT_ROOT_LEVEL);
        context.setPrinting(true);
        context.setAll(DEFAULT_ROOT_LEVEL, true);

        printStream = System.out;
    }

    /**
     * Set the defaults and all logger to the value of level and printing.
     *
     * @param level
     * @param printing
     */
    public static void setAll(Level level, boolean printing)
    {
        context.setRootLevel(level);
        context.setPrinting(printing);
        context.setAll(level, printing);
    }

    /**
     * Turn off all logging. Set the level=OFF and printing=false for the defaults and all loggers.
     */
    public static void setAllOff()
    {
        context.setRootLevel(Level.OFF);
        context.setPrinting(false);
        context.setAll(Level.OFF, false);
    }

    /**
     * Get the root level. New loggers are set to this level when they are created. This does not
     * effect loggers that already exist.
     *
     * @return the root (default) level
     */
    public static Level getRootLevel()
    {
        return context.getRootLevel();
    }

    /**
     * Sets the root (default) level. New loggers are set to this level when they are created. This
     * does not effect loggers that already exist.
     *
     * @param level The root (default) level
     */
    public static void setRootLevel(Level level)
    {
        context.setRootLevel(level);
    }

    /**
     * @return the default printing value
     */
    public static boolean isDefaultPrinting()
    {
        return context.isPrinting();
    }

    /**
     * Sets the default printing value. New loggers are set to this printing value. This does not
     * effect loggers that already exist.
     *
     * @param printing
     */
    public static void setDefaultPrinting(boolean printing)
    {
        context.setPrinting(printing);
    }

    /**
     * Sets the default to printing=false. New loggers are set to this printing value. This does not
     * effect loggers that already exist.
     */
    public static void defaultPrintingOff()
    {
        context.setPrinting(false);
    }

    /**
     * Retrieve the logger with <code>name</code>
     *
     * @param name the name of the logger
     * @return the logger with <code>name</code>
     */
    public static MockLogger get(String name)
    {
        return (MockLogger) MockLoggerFactory.get().getLogger(name);
    }

    /**
     * Retrieve the logger with <code>clazz</code>
     *
     * @param clazz the class of the logger
     * @return the logger with <code>clazz</code>
     */
    public static MockLogger get(Class<?> clazz)
    {
        return get(clazz.getName());
    }

    /**
     * @return all LogEntry's
     */
    public static List<LogEntry> getLogEntries()
    {
        return logEntries;
    }

    /**
     * @return the last LogEntry (or AssertionError() if none)
     */
    public static LogEntry last()
    {
        int size = logEntries.size();
        if (size == 0)
        {
            throw new AssertionError("No LogEntry available");
        }

        return logEntries.get(size - 1);
    }

    /**
     * Assert that logEntries is empty
     */
    public static void assertEmpty()
    {
        if (logEntries.size() != 0)
        {
            throw new AssertionError("logEntries is not empty");
        }
    }

    /**
     * @param clazz the logger name
     * @return all LogEntry's filtered by <code>clazz</code> name
     */
    public static List<LogEntry> getFilteredLogEntries(Class<?> clazz)
    {
        return getFilteredLogEntries(clazz.getName());
    }

    /**
     * @param name the logger name
     * @return all LogEntry's filtered by <code>name</code>
     */
    public static List<LogEntry> getFilteredLogEntries(String name)
    {
        List<LogEntry> filtered = new ArrayList<LogEntry>();

        for (LogEntry entry : getLogEntries())
        {
            if (entry.getName().equals(name))
            {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    /**
     * Set the printString for printing. (Defaults to System.out)
     *
     * @param printStream
     */
    public static void setPrintStream(PrintStream printStream)
    {
        MockLogger.printStream = printStream;
    }

    /*
     * =================================================================
     * MockLogger
     * =================================================================
     */

    private MockLogger(String name, Level level, boolean printing)
    {
        this.name = name;
        this.level = level;
        this.printing = printing;
    }

    private MockLogger(MockLogger other)
    {
        this.name = other.name;
        this.level = other.level;
        this.printing = other.printing;
    }

    private final String name;
    private Level        level;
    private boolean      printing;

    /**
     * Configure this logger with the same values as <code>other</code>
     *
     * @param other the other logger
     */
    private void configure(MockLogger other)
    {
        level = other.level;
        printing = other.printing;
    }

    /**
     * @return the level
     */
    public Level getLevel()
    {
        return level;
    }

    /**
     * Set the level
     *
     * @param level the level
     * @return this logger for chaining
     */
    public MockLogger setLevel(Level level)
    {
        this.level = level;
        return this;
    }

    /**
     * Set the level to {@link Level#TRACE}
     *
     * @return this logger for Chaining
     */
    public MockLogger setTrace()
    {
        return setLevel(Level.TRACE);
    }

    /**
     * Set the level to {@link Level#DEBUG}
     *
     * @return this logger for Chaining
     */
    public MockLogger setDebug()
    {
        return setLevel(Level.DEBUG);
    }

    /**
     * Set the level to {@link Level#INFO}
     *
     * @return this logger for Chaining
     */
    public MockLogger setInfo()
    {
        return setLevel(Level.INFO);
    }

    /**
     * Set the level to {@link Level#WARN}
     *
     * @return this logger for Chaining
     */
    public MockLogger setWarn()
    {
        return setLevel(Level.WARN);
    }

    /**
     * Set the level to {@link Level#ERROR}
     *
     * @return this logger for Chaining
     */
    public MockLogger setError()
    {
        return setLevel(Level.ERROR);
    }

    /**
     * Set the level to {@link Level#OFF}
     *
     * @return this logger for Chaining
     */
    public MockLogger setOff()
    {
        this.level = Level.OFF;
        return this;
    }

    /**
     * @return true if printing
     */
    public boolean isPrinting()
    {
        return printing;
    }

    /**
     * Sets the printing flag.
     *
     * @param printing the new value
     * @return this logger for chaining
     */
    public MockLogger setPrinting(boolean printing)
    {
        this.printing = printing;
        return this;
    }

    /**
     * Turns off printing for this logger.
     *
     * @return this logger for chaining
     */
    public MockLogger printOff()
    {
        return setPrinting(false);
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Is this logger instance enabled for the TRACE level?
     *
     * @return True if this Logger is enabled for level TRACE, false otherwise.
     */
    public boolean isTraceEnabled()
    {
        return Level.TRACE.isEnabled(level);
    }

    /**
     * Log a message object at level TRACE.
     *
     * @param msg
     *            - the message object to be logged
     */
    public void trace(String msg)
    {
        if (isTraceEnabled())
        {
            addLogEntry(Level.TRACE, msg, null);
        }
    }

    /**
     * Log a message at level TRACE according to the specified format and
     * argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for level TRACE.
     * </p>
     *
     * @param format
     *            the format string
     * @param arg
     *            the argument
     */
    public void trace(String format, Object arg)
    {
        if (isTraceEnabled())
        {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            addLogEntry(Level.TRACE, ft.getMessage(), null);
        }
    }

    /**
     * Log a message at level TRACE according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the TRACE level.
     * </p>
     *
     * @param format
     *            the format string
     * @param arg1
     *            the first argument
     * @param arg2
     *            the second argument
     */
    public void trace(String format, Object arg1, Object arg2)
    {
        if (isTraceEnabled())
        {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            addLogEntry(Level.TRACE, ft.getMessage(), null);
        }
    }

    /**
     * Log a message at level TRACE according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the TRACE level.
     * </p>
     *
     * @param format
     *            the format string
     * @param argArray
     *            an array of arguments
     */
    public void trace(String format, Object... argArray)
    {
        if (isTraceEnabled())
        {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            addLogEntry(Level.TRACE, ft.getMessage(), null);
        }
    }

    /**
     * Log an exception (throwable) at level TRACE with an accompanying message.
     *
     * @param msg
     *            the message accompanying the exception
     * @param t
     *            the exception (throwable) to log
     */
    public void trace(String msg, Throwable t)
    {
        if (isTraceEnabled())
        {
            addLogEntry(Level.TRACE, msg, null);
        }
    }

    /**
     * Is this logger instance enabled for the DEBUG level?
     *
     * @return True if this Logger is enabled for level DEBUG, false otherwise.
     */
    public boolean isDebugEnabled()
    {
        return Level.DEBUG.isEnabled(level);
    }

    /**
     * Log a message object at level DEBUG.
     *
     * @param msg
     *            - the message object to be logged
     */
    public void debug(String msg)
    {
        if (isDebugEnabled())
        {
            addLogEntry(Level.DEBUG, msg, null);
        }
    }

    /**
     * Log a message at level DEBUG according to the specified format and
     * argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for level DEBUG.
     * </p>
     *
     * @param format
     *            the format string
     * @param arg
     *            the argument
     */
    public void debug(String format, Object arg)
    {
        if (isDebugEnabled())
        {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            addLogEntry(Level.DEBUG, ft.getMessage(), null);
        }
    }

    /**
     * Log a message at level DEBUG according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the DEBUG level.
     * </p>
     *
     * @param format
     *            the format string
     * @param arg1
     *            the first argument
     * @param arg2
     *            the second argument
     */
    public void debug(String format, Object arg1, Object arg2)
    {
        if (isDebugEnabled())
        {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            addLogEntry(Level.DEBUG, ft.getMessage(), null);
        }
    }

    /**
     * Log a message at level DEBUG according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the DEBUG level.
     * </p>
     *
     * @param format
     *            the format string
     * @param argArray
     *            an array of arguments
     */
    public void debug(String format, Object... argArray)
    {
        if (isDebugEnabled())
        {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            addLogEntry(Level.DEBUG, ft.getMessage(), null);
        }
    }

    /**
     * Log an exception (throwable) at level DEBUG with an accompanying message.
     *
     * @param msg
     *            the message accompanying the exception
     * @param t
     *            the exception (throwable) to log
     */
    public void debug(String msg, Throwable t)
    {
        if (isDebugEnabled())
        {
            addLogEntry(Level.DEBUG, msg, t);
        }
    }

    /**
     * Is this logger instance enabled for the INFO level?
     *
     * @return True if this Logger is enabled for the INFO level, false otherwise.
     */
    public boolean isInfoEnabled()
    {
        return Level.INFO.isEnabled(level);
    }

    /**
     * Log a message object at the INFO level.
     *
     * @param msg
     *            - the message object to be logged
     */
    public void info(String msg)
    {
        if (isInfoEnabled())
        {
            addLogEntry(Level.INFO, msg, null);
        }
    }

    /**
     * Log a message at level INFO according to the specified format and argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the INFO level.
     * </p>
     *
     * @param format
     *            the format string
     * @param arg
     *            the argument
     */
    public void info(String format, Object arg)
    {
        if (isInfoEnabled())
        {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            addLogEntry(Level.INFO, ft.getMessage(), null);
        }
    }

    /**
     * Log a message at the INFO level according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the INFO level.
     * </p>
     *
     * @param format
     *            the format string
     * @param arg1
     *            the first argument
     * @param arg2
     *            the second argument
     */
    public void info(String format, Object arg1, Object arg2)
    {
        if (isInfoEnabled())
        {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            addLogEntry(Level.INFO, ft.getMessage(), null);
        }
    }

    /**
     * Log a message at level INFO according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the INFO level.
     * </p>
     *
     * @param format
     *            the format string
     * @param argArray
     *            an array of arguments
     */
    public void info(String format, Object... argArray)
    {
        if (isInfoEnabled())
        {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            addLogEntry(Level.INFO, ft.getMessage(), null);
        }
    }

    /**
     * Log an exception (throwable) at the INFO level with an accompanying
     * message.
     *
     * @param msg
     *            the message accompanying the exception
     * @param t
     *            the exception (throwable) to log
     */
    public void info(String msg, Throwable t)
    {
        if (isInfoEnabled())
        {
            addLogEntry(Level.INFO, msg, t);
        }

    }

    /**
     * Is this logger instance enabled for the WARN level?
     *
     * @return True if this Logger is enabled for the WARN level, false otherwise.
     */
    public boolean isWarnEnabled()
    {
        return Level.WARN.isEnabled(level);
    }

    /**
     * Log a message object at the WARN level.
     *
     * @param msg
     *            - the message object to be logged
     */
    public void warn(String msg)
    {
        if (isWarnEnabled())
        {
            addLogEntry(Level.WARN, msg, null);
        }

    }

    /**
     * Log a message at the WARN level according to the specified format and
     * argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the WARN level.
     * </p>
     *
     * @param format
     *            the format string
     * @param arg
     *            the argument
     */
    public void warn(String format, Object arg)
    {
        if (isWarnEnabled())
        {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            addLogEntry(Level.WARN, ft.getMessage(), null);
        }
    }

    /**
     * Log a message at the WARN level according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the WARN level.
     * </p>
     *
     * @param format
     *            the format string
     * @param arg1
     *            the first argument
     * @param arg2
     *            the second argument
     */
    public void warn(String format, Object arg1, Object arg2)
    {
        if (isWarnEnabled())
        {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            addLogEntry(Level.WARN, ft.getMessage(), null);
        }
    }

    /**
     * Log a message at level WARN according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the WARN level.
     * </p>
     *
     * @param format
     *            the format string
     * @param argArray
     *            an array of arguments
     */
    public void warn(String format, Object... argArray)
    {
        if (isWarnEnabled())
        {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            addLogEntry(Level.WARN, ft.getMessage(), null);
        }
    }

    /**
     * Log an exception (throwable) at the WARN level with an accompanying
     * message.
     *
     * @param msg
     *            the message accompanying the exception
     * @param t
     *            the exception (throwable) to log
     */
    public void warn(String msg, Throwable t)
    {
        if (isWarnEnabled())
        {
            addLogEntry(Level.WARN, msg, t);
        }
    }

    /**
     * Is this logger instance enabled for level ERROR?
     *
     * @return True if this Logger is enabled for level ERROR, false otherwise.
     */
    public boolean isErrorEnabled()
    {
        return Level.ERROR.isEnabled(level);
    }

    /**
     * Log a message object at the ERROR level.
     *
     * @param msg
     *            - the message object to be logged
     */
    public void error(String msg)
    {
        if (isErrorEnabled())
        {
            addLogEntry(Level.ERROR, msg, null);
        }
    }

    /**
     * Log a message at the ERROR level according to the specified format and
     * argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the ERROR level.
     * </p>
     *
     * @param format
     *            the format string
     * @param arg
     *            the argument
     */
    public void error(String format, Object arg)
    {
        if (isErrorEnabled())
        {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            addLogEntry(Level.ERROR, ft.getMessage(), null);
        }
    }

    /**
     * Log a message at the ERROR level according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the ERROR level.
     * </p>
     *
     * @param format
     *            the format string
     * @param arg1
     *            the first argument
     * @param arg2
     *            the second argument
     */
    public void error(String format, Object arg1, Object arg2)
    {
        if (isErrorEnabled())
        {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            addLogEntry(Level.ERROR, ft.getMessage(), null);
        }
    }

    /**
     * Log a message at level ERROR according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the ERROR level.
     * </p>
     *
     * @param format
     *            the format string
     * @param argArray
     *            an array of arguments
     */
    public void error(String format, Object... argArray)
    {
        if (isErrorEnabled())
        {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            addLogEntry(Level.ERROR, ft.getMessage(), null);
        }
    }

    /**
     * Log an exception (throwable) at the ERROR level with an accompanying
     * message.
     *
     * @param msg
     *            the message accompanying the exception
     * @param t
     *            the exception (throwable) to log
     */
    public void error(String msg, Throwable t)
    {
        if (isErrorEnabled())
        {
            addLogEntry(Level.ERROR, msg, t);
        }
    }

    /**
     * Create a LogEntry and add it to the appender and print it.
     *
     * @param level
     * @param message
     * @param throwable
     */
    private void addLogEntry(Level level, String message, Throwable throwable)
    {
        LogEntry entry = new LogEntry(name, level, message, throwable);
        logEntries.add(entry);

        if (printing)
        {
            entry.print(MockLogger.printStream);
        }
    }

    /**
     * An log entry
     *
     */
    public static class LogEntry
    {
        public LogEntry(String name, Level level, String message, Throwable throwable)
        {
            this.name = name;
            this.level = level;
            this.message = message;
            this.throwable = throwable;
        }

        private final String    name;
        private final Level     level;
        private final String    message;
        private final Throwable throwable;

        public String getName()
        {
            return name;
        }

        public Level getLevel()
        {
            return level;
        }

        public String getMessage()
        {
            return message;
        }

        public Throwable getThrowable()
        {
            return throwable;
        }

        @Override
        public String toString()
        {
            return String.format("LogEntry [level=%s, message=%s, name=%s, throwable=%s]",
                                 level,
                                 message,
                                 name,
                                 throwable);
        }

        public void print(PrintStream printStream)
        {
            printStream.println(String.format("MockLogger %s %s %s %s",
                                              level,
                                              name,
                                              message,
                                              throwable != null ? throwable.getMessage() : ""));

            if (throwable != null)
            {
                throwable.printStackTrace(printStream);
            }
        }
    }

    /**
     * The six SLF4J logging levels
     *
     */
    public static enum Level
    {
        TRACE(10), DEBUG(20), INFO(30), WARN(40), ERROR(50), OFF(100);
        Level(int value)
        {
            this.value = value;
        }
        private int value;

        /**
         * Determine is this Level is enabled for <code>other</code>
         *
         * <pre>
         *      Level level;
         *      level = DEBUG;  assertTrue(INFO.isEnabled(level);
         *      level = INFO;   assertTrue(INFO.isEnabled(level);
         *      level = WARN;   assertFalse(INFO.isEnabled(level);
         * </pre>
         *
         * @param other
         * @return true if this level is enable
         */
        public boolean isEnabled(Level other)
        {
            return value >= other.value;
        }
    };

    /**
     * A singleton factory for MockLogger's.
     *
     * @author Greg Lindholm
     *
     */
    public static class MockLoggerFactory implements ILoggerFactory
    {
        private static MockLoggerFactory SINGLETON = new MockLoggerFactory();

        public static MockLoggerFactory get()
        {
            return SINGLETON;
        }

        private MockLoggerFactory()
        {
        }

        @Override
        public Logger getLogger(String name)
        {

            MockLogger logger = MockLogger.context.getMockLogger(name);
            if (logger == null)
            {
                logger = new MockLogger(name, MockLogger.context.getRootLevel(), MockLogger.context
                    .isPrinting());
                MockLogger.context.addMockLogger(logger);
            }

            return logger;
        }
    }

    /**
     * The context for MockLogger contains all current configuration of all loggers.
     * Use {@link MockLogger#copyContext()} to retrieve a copy of the current context
     * that can be latter passed to {@link MockLogger#configure(MockLogger)} to restore
     * the context to a known state.<br>
     * <br>
     * All of the methods package level and are exposed for testing only.<br>
     * <br>
     *
     *
     */
    public static final class MockLoggerContext
    {
        private MockLoggerContext()
        {
            rootLevel = DEFAULT_ROOT_LEVEL;
            printing = true;
            loggers = new HashMap<String, MockLogger>();
        }

        private Level                         rootLevel;
        private boolean                       printing;

        /*
         * This map (in MockLogger#context) contains all real loggers.
         * Never allow MockLogger's to be removed from this map or static loggers can be lost.
         * Never let this map escape!
         */
        private final Map<String, MockLogger> loggers;

        void configure(MockLoggerContext other)
        {
            // copy the root level
            this.rootLevel = other.rootLevel;
            this.printing = other.printing;

            // Reset all the loggers to default level
            setAll(DEFAULT_ROOT_LEVEL, true);

            // Update this context to match other
            for (MockLogger otherLogger : other.loggers.values())
            {
                MockLogger currentLogger = getMockLogger(otherLogger.getName());

                if (currentLogger == null)
                {
                    // other logger doesn't exist so add a copy
                    addMockLogger(new MockLogger(otherLogger));
                }
                else
                {
                    // logger already exists so configure it to match.
                    currentLogger.configure(otherLogger);
                }
            }
        }

        Level getRootLevel()
        {
            return rootLevel;
        }

        void setRootLevel(Level rootLevel)
        {
            this.rootLevel = rootLevel;
        }

        boolean isPrinting()
        {
            return printing;
        }

        void setPrinting(boolean printing)
        {
            this.printing = printing;
        }

        void addMockLogger(MockLogger logger)
        {
            loggers.put(logger.getName(), logger);
        }

        MockLogger getMockLogger(String name)
        {
            return loggers.get(name);
        }

        void setAll(Level level, boolean printing)
        {
            for (MockLogger logger : loggers.values())
            {
                logger.setLevel(level);
                logger.setPrinting(printing);
            }
        }
    }
}
