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
import net.sourceforge.wsup.hibernate.DatabaseContext;
import net.sourceforge.wsup.hibernate.TestDatabase;
import net.sourceforge.wsup.hibernate.TestDatabaseManager;

import org.hibernate.HibernateException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.impl.MockLogger;

/**
 * Unit test for the ContextCommandRunner class
 * 
 * @author Kevin Hunter
 *
 */
public class ContextCommandRunnerImplTest
{
	private TestDatabase database;
	
	public ContextCommandRunnerImplTest()
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
	public void testSimpleCase()
	{
		MockMonitoringCommand cmd = new MockMonitoringCommand();
		DatabaseContext databaseContext = database.createDatabaseContext();
		
		ContextCommandRunnerImpl testObject = new ContextCommandRunnerImpl(5);
		
		testObject.execute(databaseContext, cmd);
		
		assertEquals(1, cmd.wasPreExecuted());
		assertEquals(1, cmd.wasPreTransactioned());
		assertEquals(1, cmd.wasExecuted());
		assertEquals(1, cmd.wasPostTransactioned());
		assertEquals(1, cmd.wasPostExecuted());
		assertTrue(cmd.wasTransactionSuccessful());
		assertTrue(cmd.wasExecuteSuccessful());
		
		assertTrue(databaseContext.isSessionOpen());
		assertFalse(databaseContext.isInTransaction());
		databaseContext.closeSession();
	}
	
	@Test
	public void testRetrySucceeds()
	{
		MockThrowingCommand cmd = new MockThrowingCommand(new LockAcquisitionException("boom", null));
		DatabaseContext databaseContext = database.createDatabaseContext();
		
		ContextCommandRunnerImpl testObject = new ContextCommandRunnerImpl(5);
		
		testObject.execute(databaseContext, cmd);
		
		assertEquals(1, cmd.wasPreExecuted());
		assertEquals(2, cmd.wasPreTransactioned());
		assertEquals(2, cmd.getExecuteCount());
		assertEquals(2, cmd.wasPostTransactioned());
		assertEquals(1, cmd.wasPostExecuted());
		assertTrue(cmd.wasTransactionSuccessful());
		assertTrue(cmd.wasExecuteSuccessful());
		
		assertTrue(databaseContext.isSessionOpen());
		assertFalse(databaseContext.isInTransaction());
		databaseContext.closeSession();
	}
	
	@Test
	public void testNonRetriableException()
	{
		MockThrowingCommand cmd = new MockThrowingCommand(new RuntimeException("Boom"));
		DatabaseContext databaseContext = database.createDatabaseContext();
		
		ContextCommandRunnerImpl testObject = new ContextCommandRunnerImpl(5);
		
		try
		{
			testObject.execute(databaseContext, cmd);
			fail("didn't throw");
		}
		catch(RuntimeException e)
		{
			assertEquals("Boom", e.getMessage());	// was our exception rethrown
		}
		
		assertEquals(1, cmd.wasPreExecuted());
		assertEquals(1, cmd.wasPreTransactioned());
		assertEquals(1, cmd.getExecuteCount());
		assertEquals(1, cmd.wasPostTransactioned());
		assertEquals(1, cmd.wasPostExecuted());
		assertFalse(cmd.wasTransactionSuccessful());
		assertFalse(cmd.wasExecuteSuccessful());
		
		assertFalse(databaseContext.isSessionOpen());
	}
	
	@Test
	public void testRetriesExceeded()
	{
		HibernateException[] exceptions =
		{
				new JDBCConnectionException("1", null),
				new JDBCConnectionException("2", null),
				new JDBCConnectionException("3", null),
				new JDBCConnectionException("4", null),
				new JDBCConnectionException("5", null),
				new JDBCConnectionException("6", null),
		};
		
		MockThrowingCommand cmd = new MockThrowingCommand(exceptions);
		DatabaseContext databaseContext = database.createDatabaseContext();
		
		ContextCommandRunnerImpl testObject = new ContextCommandRunnerImpl(3);
		
		try
		{
			testObject.execute(databaseContext, cmd);
			fail("didn't throw");
		}
		catch(RuntimeException e)
		{
			assertEquals("3", e.getMessage());	// was our exception rethrown
		}
		
		assertEquals(1, cmd.wasPreExecuted());
		assertEquals(3, cmd.wasPreTransactioned());
		assertEquals(3, cmd.getExecuteCount());
		assertEquals(3, cmd.wasPostTransactioned());
		assertEquals(1, cmd.wasPostExecuted());
		assertFalse(cmd.wasTransactionSuccessful());
		assertFalse(cmd.wasExecuteSuccessful());
		
		assertFalse(databaseContext.isSessionOpen());
	}
	
	@Test
	public void testRetrySucceedsWithCommandRetryable()
	{
		MockRetryableCommand cmd = new MockRetryableCommand(RuntimeException.class, new RuntimeException("boom"));
		DatabaseContext databaseContext = database.createDatabaseContext();
		
		ContextCommandRunnerImpl testObject = new ContextCommandRunnerImpl(5);
		
		testObject.execute(databaseContext, cmd);
		
		assertEquals(1, cmd.wasPreExecuted());
		assertEquals(2, cmd.wasPreTransactioned());
		assertEquals(2, cmd.getExecuteCount());
		assertEquals(2, cmd.wasPostTransactioned());
		assertEquals(1, cmd.wasPostExecuted());
		assertTrue(cmd.wasTransactionSuccessful());
		assertTrue(cmd.wasExecuteSuccessful());
		
		assertTrue(databaseContext.isSessionOpen());
		assertFalse(databaseContext.isInTransaction());
		databaseContext.closeSession();
	}
	
	@Test
	public void testRetryFailesWithCommandRetryable()
	{
		MockRetryableCommand cmd = new MockRetryableCommand(String.class, new RuntimeException("boom"));
		DatabaseContext databaseContext = database.createDatabaseContext();
		
		ContextCommandRunnerImpl testObject = new ContextCommandRunnerImpl(3);
		
		try
		{
			testObject.execute(databaseContext, cmd);
			fail("didn't throw");
		}
		catch(RuntimeException e)
		{
			assertEquals("boom", e.getMessage());	// was our exception rethrown
		}
		
		assertEquals(1, cmd.wasPreExecuted());
		assertEquals(1, cmd.wasPreTransactioned());
		assertEquals(1, cmd.getExecuteCount());
		assertEquals(1, cmd.wasPostTransactioned());
		assertEquals(1, cmd.wasPostExecuted());
		assertFalse(cmd.wasTransactionSuccessful());
		assertFalse(cmd.wasExecuteSuccessful());
		
		assertFalse(databaseContext.isSessionOpen());
	}
}
