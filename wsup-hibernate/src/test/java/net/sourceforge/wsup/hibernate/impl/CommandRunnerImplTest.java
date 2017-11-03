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

package net.sourceforge.wsup.hibernate.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sourceforge.wsup.hibernate.TestDatabase;
import net.sourceforge.wsup.hibernate.TestDatabaseManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.impl.MockLogger;

/**
 * Unit test of the CommandRunnerImpl class.
 * 
 * @author Kevin Hunter
 *
 */
public class CommandRunnerImplTest
{
	private TestDatabase database;
	
	public CommandRunnerImplTest()
	{
	}
	
	@Before
	public void setup() throws Exception
	{
    	MockLogger.get("org.hibernate").setWarn();
    	MockLogger.get("com.mchange.v2.c3p0").setWarn();
    	MockLogger.get("com.mchange.v2.log").setWarn();
    	
		database = TestDatabaseManager.openTestDatabaseIfRequired();
	}
	
	@After
	public void cleanup()
	{
		MockLogger.clear();
	}
	
	@Test
	public void testSuccess()
	{
		MockMonitoringCommand cmd = new MockMonitoringCommand();
		
		ContextCommandRunnerImpl runner = new ContextCommandRunnerImpl(5);
		CommandRunnerImpl testObject = new CommandRunnerImpl(database, runner);
		
		testObject.execute(cmd);
		
		assertEquals(1, cmd.wasPreExecuted());
		assertEquals(1, cmd.wasPreTransactioned());
		assertEquals(1, cmd.wasExecuted());
		assertEquals(1, cmd.wasPostTransactioned());
		assertEquals(1, cmd.wasPostExecuted());
		assertTrue(cmd.wasTransactionSuccessful());
		assertTrue(cmd.wasExecuteSuccessful());
		assertEquals(0, database.getOutstandingSessionCount());
	}
	
	@Test
	public void testFailure()
	{
		MockThrowingCommand cmd = new MockThrowingCommand(new RuntimeException("not retryable"));
		
		ContextCommandRunnerImpl runner = new ContextCommandRunnerImpl(5);
		CommandRunnerImpl testObject = new CommandRunnerImpl(database, runner);
		
		try
		{
			testObject.execute(cmd);
			fail("didn't throw");
		}
		catch(RuntimeException e)
		{
		}
		
		assertEquals(1, cmd.wasPreExecuted());
		assertEquals(1, cmd.wasPreTransactioned());
		assertEquals(1, cmd.getExecuteCount());
		assertEquals(1, cmd.wasPostTransactioned());
		assertEquals(1, cmd.wasPostExecuted());
		assertFalse(cmd.wasTransactionSuccessful());
		assertFalse(cmd.wasExecuteSuccessful());
		assertEquals(0, database.getOutstandingSessionCount());
	}
}
