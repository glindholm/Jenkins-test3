/*
 *  Copyright (c) 2010 Greg Lindholm
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

package org.slf4j.impl;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.MockLogger.Level;
import org.slf4j.impl.MockLogger.LogEntry;
import org.slf4j.impl.MockLogger.MockLoggerContext;
import org.slf4j.impl.MockLogger.MockLoggerFactory;

public class TheMockLoggerTest
{

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
        MockLogger.resetAll();
    }

    @Before
    public void setUp() throws Exception
    {
        MockLogger.resetAll();
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testResetAll()
    {
        //reset
        MockLogger.resetAll();
        // check reset state
        assertEquals(0, MockLogger.getLogEntries().size());
        assertEquals(MockLogger.DEFAULT_ROOT_LEVEL, MockLogger.getRootLevel());

        // do stuff
        MockLogger.setRootLevel(Level.TRACE);
        MockLogger.get("testReset").printOff().warn("warn1");

        // check stuff was done
        assertEquals(1, MockLogger.getLogEntries().size());
        assertEquals(Level.TRACE, MockLogger.getRootLevel());

        // reset
        MockLogger.resetAll();
        // check reset state
        assertEquals(0, MockLogger.getLogEntries().size());
        assertEquals(MockLogger.DEFAULT_ROOT_LEVEL, MockLogger.getRootLevel());

    }

    @Test
    public void testConfigure()
    {
        assertEquals(0, MockLogger.getLogEntries().size());

        MockLogger.setRootLevel(Level.OFF);
        MockLogger.get("testConfigure1").setError();
        MockLogger.get("testConfigure2").setError();
        MockLogger.get("testConfigure2").setError();
        MockLogger.get("testConfigure4");

        MockLoggerContext context = MockLogger.copyContext();
        assertEquals(Level.OFF, context.getRootLevel());

        MockLogger.resetAll();
        assertEquals(0, MockLogger.getLogEntries().size());
        assertEquals(Level.WARN, MockLogger.getRootLevel());

        MockLogger.configure(context);
        assertEquals(0, MockLogger.getLogEntries().size());
        assertEquals(Level.OFF, context.getRootLevel());

    }

    @Test
    public void testGetRootLevel()
    {
        MockLogger.setRootLevel(Level.ERROR);
        assertEquals(Level.ERROR, MockLogger.getRootLevel());
        MockLogger.setRootLevel(Level.WARN);
        assertEquals(Level.WARN, MockLogger.getRootLevel());
    }

    @Test
    public void testSetLevelClassOfQLevel()
    {
        MockLogger logger = MockLogger.get(TheMockLoggerTest.class).setError();

        assertEquals(Level.ERROR, logger.getLevel());
        assertEquals(TheMockLoggerTest.class.getName(), logger.getName());
    }

    @Test
    public void testGetLogEntries()
    {
        assertEquals(0, MockLogger.getLogEntries().size());

        MockLogger.get("test1").printOff().warn("warn1-1");
        MockLogger.get("test1").printOff().warn("warn1-2");
        MockLogger.get("test2").printOff().warn("warn2-1");
        MockLogger.get(this.getClass()).printOff().warn("warn3");

        assertEquals(4, MockLogger.getLogEntries().size());
        assertEquals(2, MockLogger.getFilteredLogEntries("test1").size());
        assertEquals(1, MockLogger.getFilteredLogEntries("test2").size());
        assertEquals(1, MockLogger.getFilteredLogEntries(this.getClass()).size());

        MockLogger.clear();
        assertEquals(0, MockLogger.getLogEntries().size());
    }

    @Test
    public void testCopyMockLogger()
    {
        MockLogger logger = MockLogger.get("testMockLoggerMockLogger");
        logger.setError();

        assertEquals("testMockLoggerMockLogger", logger.getName());

        MockLogger logger2 = MockLogger.copyContext().getMockLogger("testMockLoggerMockLogger");

        assertTrue(logger != logger2);
        assertEquals(logger.getName(), logger2.getName());
        assertEquals(logger.getLevel(), logger2.getLevel());

    }

    @Test
    public void testSetLevelLevel()
    {
        MockLogger logger = MockLogger.get("testSetLevelLevel");

        logger.setDebug();
        assertEquals(Level.DEBUG, logger.getLevel());

        logger.setOff();
        assertEquals(Level.OFF, logger.getLevel());
    }

    @Test
    public void testIsTraceEnabled()
    {
        MockLogger logger = (MockLogger) MockLoggerFactory.get().getLogger("testIsTraceEnabled");

        logger.setWarn();
        assertFalse(logger.isTraceEnabled());

        logger.setTrace();
        assertTrue(logger.isTraceEnabled());
    }

    @Test
    public void testTrace()
    {
        MockLogger.setRootLevel(Level.TRACE);

        MockLogger.get("testTrace").printOff();

        MockLogger.get("testTrace").trace("msg");
        assertEquals("LogEntry [level=TRACE, message=msg, name=testTrace, throwable=null]",
                     MockLogger.getLogEntries().remove(0).toString());

        MockLogger.get("testTrace").trace("arg={}", "arg");
        assertEquals("LogEntry [level=TRACE, message=arg=arg, name=testTrace, throwable=null]",
                     MockLogger.getLogEntries().remove(0).toString());

        MockLogger.get("testTrace").trace("arg1={} arg2={}", "arg1", "arg2");
        assertEquals("LogEntry [level=TRACE, message=arg1=arg1 arg2=arg2, name=testTrace, throwable=null]",
                     MockLogger.getLogEntries().remove(0).toString());

        MockLogger.assertEmpty();

        MockLogger.get("testTrace").setDebug();
        MockLogger.get("testTrace").trace("msg");
        MockLogger.get("testTrace").trace("arg={}", "arg");
        MockLogger.get("testTrace").trace("arg1={} arg2={}", "arg1", "arg2");
        MockLogger.assertEmpty();
    }

    @Test
    public void testIsDebugEnabled()
    {
        MockLogger logger = (MockLogger) MockLoggerFactory.get().getLogger("testIsDebugEnabled()");

        logger.setWarn();
        assertFalse(logger.isDebugEnabled());

        logger.setTrace();
        assertTrue(logger.isDebugEnabled());

    }

    @Test
    public void testDebug()
    {
        MockLogger.setRootLevel(Level.DEBUG);

        MockLogger.get("testDebug").printOff().debug("msg");
        assertEquals("LogEntry [level=DEBUG, message=msg, name=testDebug, throwable=null]",
                     MockLogger.getLogEntries().remove(0).toString());

        MockLogger.get("testDebug").printOff().debug("arg={}", "arg");
        assertEquals("LogEntry [level=DEBUG, message=arg=arg, name=testDebug, throwable=null]",
                     MockLogger.getLogEntries().remove(0).toString());


        MockLogger.assertEmpty();

        MockLogger.get("testDebug").setInfo();
        MockLogger.get("testDebug").trace("msg");
        MockLogger.get("testDebug").trace("arg={}", "arg");
        MockLogger.get("testDebug").trace("arg1={} arg2={}", "arg1", "arg2");
        MockLogger.assertEmpty();

    }

    @Test
    public void testIsInfoEnabled()
    {
        MockLogger logger = (MockLogger) MockLoggerFactory.get().getLogger("testIsInfoEnabled");

        logger.setWarn();
        assertFalse(logger.isInfoEnabled());

        logger.setTrace();
        assertTrue(logger.isInfoEnabled());

    }

    @Test
    public void testInfo()
    {
        MockLogger.setRootLevel(Level.INFO);

        MockLogger.get("testInfo").setInfo().printOff();

        MockLogger.get("testInfo").info("msg");
        assertEquals("LogEntry [level=INFO, message=msg, name=testInfo, throwable=null]",
                     MockLogger.getLogEntries().remove(0).toString());

        MockLogger.get("testInfo").info("arg={}", "arg");
        assertEquals("LogEntry [level=INFO, message=arg=arg, name=testInfo, throwable=null]",
                     MockLogger.getLogEntries().remove(0).toString());


        MockLogger.assertEmpty();

        MockLogger.get("testInfo").setWarn();
        MockLogger.get("testInfo").trace("msg");
        MockLogger.get("testInfo").trace("arg={}", "arg");
        MockLogger.get("testInfo").trace("arg1={} arg2={}", "arg1", "arg2");
        MockLogger.assertEmpty();

    }

    @Test
    public void testIsWarnEnabled()
    {
        MockLogger logger = (MockLogger) MockLoggerFactory.get().getLogger("testIsWarnEnabled");

        logger.setError();
        assertFalse(logger.isWarnEnabled());

        logger.setWarn();
        assertTrue(logger.isWarnEnabled());

    }

    @Test
    public void testWarn()
    {
        MockLogger.setRootLevel(Level.WARN);

        MockLogger.get("testWarn").printOff();

        MockLogger.get("testWarn").warn("msg");
        assertEquals("LogEntry [level=WARN, message=msg, name=testWarn, throwable=null]",
                     MockLogger.getLogEntries().remove(0).toString());

        MockLogger.get("testWarn").warn("arg={}", "arg");
        assertEquals("LogEntry [level=WARN, message=arg=arg, name=testWarn, throwable=null]",
                     MockLogger.getLogEntries().remove(0).toString());

        MockLogger.get("testWarn").warn("msg", new RuntimeException("RTE"));
        assertEquals("LogEntry [level=WARN, message=msg, name=testWarn, throwable=java.lang.RuntimeException: RTE]",
                     MockLogger.getLogEntries().remove(0).toString());

    }

    @Test
    public void testIsErrorEnabled()
    {
        MockLogger logger = (MockLogger) MockLoggerFactory.get().getLogger("testIsErrorEnabled");

        logger.setOff();
        assertFalse(logger.isErrorEnabled());

        logger.setWarn();
        assertTrue(logger.isErrorEnabled());

    }

    @Test
    public void testError()
    {
        MockLogger.setRootLevel(Level.ERROR);

        MockLogger.get("testError").printOff();

        MockLogger.get("testError").error("msg");
        assertEquals("LogEntry [level=ERROR, message=msg, name=testError, throwable=null]",
                     MockLogger.getLogEntries().remove(0).toString());

        MockLogger.get("testError").error("arg={}", "arg");
        assertEquals("LogEntry [level=ERROR, message=arg=arg, name=testError, throwable=null]",
                     MockLogger.getLogEntries().remove(0).toString());

    }

    @Test
    public void testSetAll()
    {
        MockLogger.get("testSetAll").printOff();
        assertEquals(Level.WARN, MockLogger.get("testSetAll").getLevel());
        assertEquals(false, MockLogger.get("testSetAll").isPrinting());

        MockLogger.setAll(Level.ERROR, true);
        assertEquals(Level.ERROR, MockLogger.get("testSetAll").getLevel());
        assertEquals(true, MockLogger.get("testSetAll").isPrinting());

        MockLogger.setAllOff();
        assertEquals(Level.OFF, MockLogger.get("testSetAll").getLevel());
        assertEquals(false, MockLogger.get("testSetAll").isPrinting());
    }

    @Test
    public void testPrinting()
    {
        assertTrue(MockLogger.isDefaultPrinting());

        MockLogger.setDefaultPrinting(false);
        assertFalse(MockLogger.isDefaultPrinting());

        MockLogger.setDefaultPrinting(true);
        assertTrue(MockLogger.isDefaultPrinting());

        MockLogger.defaultPrintingOff();
        assertFalse(MockLogger.isDefaultPrinting());

        assertEquals(false, MockLogger.get("testPrinting").isPrinting());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(byteArrayOutputStream);
        MockLogger.setPrintStream(ps);

        MockLogger.get("testPrinting").setPrinting(false).error("msg1");

        MockLogger.get("testPrinting").setPrinting(true).error("msg2");

        assertEquals(2, MockLogger.getLogEntries().size());

        // the "trim" is because Mac & Windows use different EOL characters - trim removes them
        assertEquals("MockLogger ERROR testPrinting msg2", byteArrayOutputStream.toString().trim());

        byteArrayOutputStream.reset();
        MockLogger.get("testPrinting").setPrinting(true).error("msg2", new RuntimeException("RTE"));

        // print a stack trace
        assertTrue(byteArrayOutputStream.toString(), byteArrayOutputStream.toString().contains(this
            .getClass().getName()));
    }

    @Test
    public void testLast()
    {
        MockLogger.get("testLast").printOff();

        MockLogger.get("testLast").error("msg1");
        assertEquals("LogEntry [level=ERROR, message=msg1, name=testLast, throwable=null]",
                     MockLogger.last().toString());

        MockLogger.get("testLast").error("msg2");
        assertEquals("LogEntry [level=ERROR, message=msg2, name=testLast, throwable=null]",
                     MockLogger.last().toString());

        MockLogger.clear();

        try
        {
            MockLogger.last();
            fail();
        }
        catch (AssertionError e)
        {
            // success
        }
    }

    @Test
    public void testAssertEmpty()
    {
        MockLogger.get("testAssertEmpty").printOff();

        MockLogger.assertEmpty();

        MockLogger.get("testAssertEmpty").error("msg1");

        try
        {
            MockLogger.assertEmpty();
            fail();
        }
        catch (AssertionError e)
        {
            // success
        }
    }

    @Test
    public void testLogEntry()
    {
        MockLogger.get("testLogEntry").printOff().error("msg1", new RuntimeException("RTE"));

        LogEntry last = MockLogger.last();

        assertEquals("testLogEntry", last.getName());
        assertEquals(Level.ERROR, last.getLevel());
        assertEquals("msg1", last.getMessage());
        assertNotNull(last.getThrowable());
    }

    @Test
    public void testLoggerFactory()
    {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        assertTrue(logger instanceof MockLogger);

        assertEquals(MockLoggerFactory.class.getName(), StaticLoggerBinder.getSingleton()
            .getLoggerFactoryClassStr());

    }
}
