package net.sourceforge.wsup.core.testSupport;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

public class Log4JHelperTest
{
	private static final Logger logger = Logger
			.getLogger(Log4JHelperTest.class);

	public Log4JHelperTest()
	{
	}

	@After
	public void cleanup()
	{
		Log4JHelper.resetLoggerLevels();
		assertEquals(Level.TRACE, logger.getLevel());
	}

	@Test
	public void testTrace()
	{
		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.setLoggerToTrace(Log4JHelperTest.class);
		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.resetLoggerLevels();

		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.setLoggerToTrace("net.sourceforge.wsup.core.testSupport.Log4JHelperTest");
		assertEquals(Level.TRACE, logger.getLevel());
	}

	@Test
	public void testDebug()
	{
		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.setLoggerToDebug(Log4JHelperTest.class);
		assertEquals(Level.DEBUG, logger.getLevel());

		Log4JHelper.resetLoggerLevels();

		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.setLoggerToDebug("net.sourceforge.wsup.core.testSupport.Log4JHelperTest");
		assertEquals(Level.DEBUG, logger.getLevel());
	}

	@Test
	public void testInfo()
	{
		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.setLoggerToInfo(Log4JHelperTest.class);
		assertEquals(Level.INFO, logger.getLevel());

		Log4JHelper.resetLoggerLevels();

		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.setLoggerToInfo("net.sourceforge.wsup.core.testSupport.Log4JHelperTest");
		assertEquals(Level.INFO, logger.getLevel());
	}

	@Test
	public void testWarn()
	{
		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.setLoggerToWarn(Log4JHelperTest.class);
		assertEquals(Level.WARN, logger.getLevel());

		Log4JHelper.resetLoggerLevels();

		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.setLoggerToWarn("net.sourceforge.wsup.core.testSupport.Log4JHelperTest");
		assertEquals(Level.WARN, logger.getLevel());
	}

	@Test
	public void testError()
	{
		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.setLoggerToError(Log4JHelperTest.class);
		assertEquals(Level.ERROR, logger.getLevel());

		Log4JHelper.resetLoggerLevels();

		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.setLoggerToError("net.sourceforge.wsup.core.testSupport.Log4JHelperTest");
		assertEquals(Level.ERROR, logger.getLevel());
	}

	@Test
	public void testFatal()
	{
		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.setLoggerToFatal(Log4JHelperTest.class);
		assertEquals(Level.FATAL, logger.getLevel());

		Log4JHelper.resetLoggerLevels();

		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.setLoggerToFatal("net.sourceforge.wsup.core.testSupport.Log4JHelperTest");
		assertEquals(Level.FATAL, logger.getLevel());
	}

	@Test
	public void testOff()
	{
		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.setLoggerToOff(Log4JHelperTest.class);
		assertEquals(Level.OFF, logger.getLevel());

		Log4JHelper.resetLoggerLevels();

		assertEquals(Level.TRACE, logger.getLevel());

		Log4JHelper.setLoggerToOff("net.sourceforge.wsup.core.testSupport.Log4JHelperTest");
		assertEquals(Level.OFF, logger.getLevel());
	}

	@Test
	public void testLevels()
	{
		Level[] levels = { Level.WARN, Level.INFO, Level.OFF };

		assertEquals(Level.TRACE, logger.getLevel());

		for (Level level : levels)
		{
			Log4JHelper.setLoggerToLevel(Log4JHelperTest.class, level);
			assertEquals(level, logger.getLevel());
		}

		Log4JHelper.resetLoggerLevels();

		assertEquals(Level.TRACE, logger.getLevel());

		for (Level level : levels)
		{
			Log4JHelper.setLoggerToLevel("net.sourceforge.wsup.core.testSupport.Log4JHelperTest", level);
			assertEquals(level, logger.getLevel());
		}
	}

	@Test
	public void testViaString()
	{
		assertEquals(Level.TRACE, logger.getLevel());

		String[] input = { "warn", "net.sourceforge.wsup.core.testSupport.Log4JHelperTest" };

		Log4JHelper.setLoggerLevels(input);

		assertEquals(Level.WARN, logger.getLevel());
	}

    @Test
    public void testEffectiveLevel()
    {
        assertEquals(Level.WARN, Logger.getLogger("com.effective.sub1").getEffectiveLevel());

        Log4JHelper.setLoggerToError("com.effective.sub1.sub2");
        Log4JHelper.setLoggerToFatal("com.effective.sub1.sub2.sub3.sub4");

        assertEquals(Level.ERROR, Logger.getLogger("com.effective.sub1.sub2").getEffectiveLevel());
        assertEquals(Level.ERROR, Logger.getLogger("com.effective.sub1.sub2.sub3").getEffectiveLevel());
        assertEquals(Level.FATAL, Logger.getLogger("com.effective.sub1.sub2.sub3.sub4").getEffectiveLevel());
        assertEquals(Level.FATAL, Logger.getLogger("com.effective.sub1.sub2.sub3.sub4.sub5").getEffectiveLevel());

        Log4JHelper.resetLoggerLevels();
        assertEquals(Level.WARN, Logger.getLogger("com.effective.sub1.sub2").getEffectiveLevel());
        assertEquals(Level.WARN, Logger.getLogger("com.effective.sub1.sub2.sub3").getEffectiveLevel());
        assertEquals(Level.WARN, Logger.getLogger("com.effective.sub1.sub2.sub3.sub4").getEffectiveLevel());
        assertEquals(Level.WARN, Logger.getLogger("com.effective.sub1.sub2.sub3.sub4.sub5").getEffectiveLevel());

        /*
         * Multiple sets
         */
        assertEquals(null, Logger.getLogger("com.effective.sub1.sub2").getLevel());
        assertEquals(Level.WARN, Logger.getLogger("com.effective.sub1.sub2").getEffectiveLevel());

        Log4JHelper.setLoggerToError("com.effective.sub1.sub2");
        assertEquals(Level.ERROR, Logger.getLogger("com.effective.sub1.sub2").getLevel());
        Log4JHelper.setLoggerToFatal("com.effective.sub1.sub2");
        assertEquals(Level.FATAL, Logger.getLogger("com.effective.sub1.sub2").getLevel());

        Log4JHelper.resetLoggerLevels();
        assertEquals(Level.WARN, Logger.getLogger("com.effective.sub1.sub2").getEffectiveLevel());
    }

	@Test
	public void coverage()
	{
		Log4JHelper.coverage();
	}
}
