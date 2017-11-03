/*
 *  Copyright 2010 Kevin Hunter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.sourceforge.wsup.core.testSupport;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.wsup.core.Assert;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class is designed to be used with unit tests to suppress logging that
 * might otherwise occur. The normal pattern is to set a logger to a high enough
 * level to avoid unwanted log output at the beginning of a unit test, then
 * reset all the loggers back to their defaults at the end of the test.
 *
 * @author Kevin Hunter
 *
 */
public class Log4JHelper
{
	private static HashMap<String, Level> originalLevels = new HashMap<String, Level>();

	/**
	 * Set a particular logger (by its class) to <code>TRACE</code> level.
	 *
	 * @param loggerClass
	 *            <code>Class</code> whose logger is to be affected.
	 */
	public static void setLoggerToTrace(Class<?> loggerClass)
	{
		setLoggerToLevel(loggerClass.getName(), Level.TRACE);
	}

	/**
	 * Set a particular logger (by its class) to <code>TRACE</code> level.
	 *
	 * @param loggerName
	 *            Fully-qualified name of the class whose logger is to be
	 *            affected.
	 */
	public static void setLoggerToTrace(String loggerName)
	{
		setLoggerToLevel(loggerName, Level.TRACE);
	}

	/**
	 * Set a particular logger (by its class) to <code>DEBUG</code> level.
	 *
	 * @param loggerClass
	 *            <code>Class</code> whose logger is to be affected.
	 */
	public static void setLoggerToDebug(Class<?> loggerClass)
	{
		setLoggerToLevel(loggerClass.getName(), Level.DEBUG);
	}

	/**
	 * Set a particular logger (by its class) to <code>DEBUG</code> level.
	 *
	 * @param loggerName
	 *            Fully-qualified name of the class whose logger is to be
	 *            affected.
	 */
	public static void setLoggerToDebug(String loggerName)
	{
		setLoggerToLevel(loggerName, Level.DEBUG);
	}

	/**
	 * Set a particular logger (by its class) to <code>INFO</code> level.
	 *
	 * @param loggerClass
	 *            <code>Class</code> whose logger is to be affected.
	 */
	public static void setLoggerToInfo(Class<?> loggerClass)
	{
		setLoggerToLevel(loggerClass.getName(), Level.INFO);
	}

	/**
	 * Set a particular logger (by its class) to <code>INFO</code> level.
	 *
	 * @param loggerName
	 *            Fully-qualified name of the class whose logger is to be
	 *            affected.
	 */
	public static void setLoggerToInfo(String loggerName)
	{
		setLoggerToLevel(loggerName, Level.INFO);
	}

	/**
	 * Set a particular logger (by its class) to <code>WARN</code> level.
	 *
	 * @param loggerClass
	 *            <code>Class</code> whose logger is to be affected.
	 */
	public static void setLoggerToWarn(Class<?> loggerClass)
	{
		setLoggerToLevel(loggerClass.getName(), Level.WARN);
	}

	/**
	 * Set a particular logger (by its class) to <code>WARN</code> level.
	 *
	 * @param loggerName
	 *            Fully-qualified name of the class whose logger is to be
	 *            affected.
	 */
	public static void setLoggerToWarn(String loggerName)
	{
		setLoggerToLevel(loggerName, Level.WARN);
	}

	/**
	 * Set a particular logger (by its class) to <code>ERROR</code> level.
	 *
	 * @param loggerClass
	 *            <code>Class</code> whose logger is to be affected.
	 */
	public static void setLoggerToError(Class<?> loggerClass)
	{
		setLoggerToLevel(loggerClass.getName(), Level.ERROR);
	}

	/**
	 * Set a particular logger (by its class) to <code>ERROR</code> level.
	 *
	 * @param loggerName
	 *            Fully-qualified name of the class whose logger is to be
	 *            affected.
	 */
	public static void setLoggerToError(String loggerName)
	{
		setLoggerToLevel(loggerName, Level.ERROR);
	}

	/**
	 * Set a particular logger (by its class) to <code>FATAL</code> level.
	 *
	 * @param loggerClass
	 *            <code>Class</code> whose logger is to be affected.
	 */
	public static void setLoggerToFatal(Class<?> loggerClass)
	{
		setLoggerToLevel(loggerClass.getName(), Level.FATAL);
	}

	/**
	 * Set a particular logger (by its class) to <code>FATAL</code> level.
	 *
	 * @param loggerName
	 *            Fully-qualified name of the class whose logger is to be
	 *            affected.
	 */
	public static void setLoggerToFatal(String loggerName)
	{
		setLoggerToLevel(loggerName, Level.FATAL);
	}

	/**
	 * Turn a particular logger off.
	 *
	 * @param loggerClass
	 *            <code>Class</code> whose logger is to be affected.
	 */
	public static void setLoggerToOff(Class<?> loggerClass)
	{
		setLoggerToLevel(loggerClass.getName(), Level.OFF);
	}

	/**
	 * Turn a particular logger off.
	 *
	 * @param loggerName
	 *            Fully-qualified name of the class whose logger is to be
	 *            affected.
	 */
	public static void setLoggerToOff(String loggerName)
	{
		setLoggerToLevel(loggerName, Level.OFF);
	}

	/**
	 * Set a particular logger (by its class) to a specified level.
	 *
	 * @param loggerClass
	 *            <code>Class</code> whose logger is to be affected.
	 * @param level
	 *            <code>Level</code> to which to set the logger.
	 */
	public static void setLoggerToLevel(Class<?> loggerClass, Level level)
	{
		setLoggerToLevel(loggerClass.getName(), level);
	}

	/**
	 * Set a particular logger (by its class) to a specified level.
	 *
	 * @param loggerName
	 *            Fully-qualified name of the class whose logger is to be
	 *            affected.
	 * @param level
	 *            <code>Level</code> to which to set the logger.
	 */
	public static void setLoggerToLevel(String loggerName, Level level)
	{
		Logger logger = Logger.getLogger(loggerName);

		if (!originalLevels.containsKey(loggerName))
		{
			originalLevels.put(loggerName, logger.getLevel());
		}

		logger.setLevel(level);
	}

	/**
	 * Reset all loggers to the levels they had prior to any call to any of the
	 * <code>setLogger*</code> methods.
	 */
	public static void resetLoggerLevels()
	{
		for (String loggerName : originalLevels.keySet())
		{
			Level level = originalLevels.get(loggerName);
			Logger.getLogger(loggerName).setLevel(level);
		}

		originalLevels.clear();
	}

	/**
	 * Set a series of loggers to specified levels. The input is an array of
	 * strings. These are interpreted in pairs, with the first of each pair
	 * indicating the level, and the second of each pair the name of the logger.
	 * Thus:
	 *
	 * <pre>
	 * private static final String[] LEVELS =
	 * {
	 *     "warn", "net.sourceforge.wsup.core.SomeClass",
	 *     "error","some.other.class.name"
	 * };
	 * . . .
	 * setLoggerLevels(LEVELS);
	 * </pre>
	 *
	 * would be equivalent to the calls
	 *
	 * <pre>
	 * setLoggerToWarn(&quot;net.sourceforge.wsup.core.SomeClass&quot;);
	 * setLoggerToError(&quot;some.other.class.name&quot;);
	 * </pre>
	 *
	 * @param loggersAndLevels
	 *            Array of <code>String</code>s specifying logger names and
	 *            levels.
	 */
	public static void setLoggerLevels(String[] loggersAndLevels)
	{
		for (int i = 0; i < loggersAndLevels.length - 1; i += 2)
		{
			Level level = LEVELS.get(loggersAndLevels[i]);
			Assert.isNotNull(level);
			String logger = loggersAndLevels[i + 1];

			setLoggerToLevel(logger, level);
		}
	}

	private static final Map<String, Level> LEVELS = new HashMap<String, Level>();
	static
	{
		LEVELS.put("trace", Level.TRACE);
		LEVELS.put("debug", Level.DEBUG);
		LEVELS.put("info", Level.INFO);
		LEVELS.put("warn", Level.WARN);
		LEVELS.put("error", Level.ERROR);
		LEVELS.put("fatal", Level.FATAL);
		LEVELS.put("off", Level.OFF);

		LEVELS.put("TRACE", Level.TRACE);
		LEVELS.put("DEBUG", Level.DEBUG);
		LEVELS.put("INFO", Level.INFO);
		LEVELS.put("WARN", Level.WARN);
		LEVELS.put("ERROR", Level.ERROR);
		LEVELS.put("FATAL", Level.FATAL);
		LEVELS.put("OFF", Level.OFF);
	}

	/* package */static void coverage()
	{
		new Log4JHelper();
	}

	/**
	 * Constructor. We do not actually expect instances of this class to ever
	 * get built, but having a protected constructor allows classes to be
	 * derived from this in order to add functionality.
	 */
	protected Log4JHelper()
	{
	}
}
