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

package net.sourceforge.wsup.hibernate.testSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test the MockingDatabaseInterceptorTest.  (Named "DoMockingDatabaseInterceptorTest"
 * because we exclude any classes that start with "Mock" from unit test status.)
 * 
 * @author Kevin Hunter
 *
 */
public class DoMockingDatabaseInterceptorTest
{
	private MockingDatabaseInterceptor testObject;
	
	public DoMockingDatabaseInterceptorTest()
	{
	}
	
	@Before
	public void setup()
	{
		testObject = new MockingDatabaseInterceptor();
	}
	
	@Test
	public void testInitialSetup()
	{
		assertNull(testObject.getBeginTransactionException());
		assertNull(testObject.getCloseDatabaseException());
		assertNull(testObject.getCloseSessionException());
		assertNull(testObject.getCommitTransactionException());
		assertNull(testObject.getCreateSessionException());
		assertNull(testObject.getOpenDatabaseException());
		assertNull(testObject.getRollbackTransactionException());
		assertEquals(MockSession.class, testObject.getSessionWrapperClass());
		assertEquals(MockTransaction.class, testObject.getTransactionWrapperClass());
	}
	
	@Test
	public void testSetGet()
	{
		HibernateException exception = new HibernateException("Test");
		
		testObject.setBeginTransactionException(exception);
		assertTrue(testObject.getBeginTransactionException() == exception);
		assertNull(testObject.getCloseDatabaseException());
		assertNull(testObject.getCloseSessionException());
		assertNull(testObject.getCommitTransactionException());
		assertNull(testObject.getCreateSessionException());
		assertNull(testObject.getOpenDatabaseException());
		assertNull(testObject.getRollbackTransactionException());
		testObject.setBeginTransactionException(null);
		
		
		testObject.setCloseDatabaseException(exception);
		assertNull(testObject.getBeginTransactionException());
		assertTrue(testObject.getCloseDatabaseException() == exception);
		assertNull(testObject.getCloseSessionException());
		assertNull(testObject.getCommitTransactionException());
		assertNull(testObject.getCreateSessionException());
		assertNull(testObject.getOpenDatabaseException());
		assertNull(testObject.getRollbackTransactionException());
		testObject.setCloseDatabaseException(null);
		
		
		testObject.setCloseSessionException(exception);
		assertNull(testObject.getBeginTransactionException());
		assertNull(testObject.getCloseDatabaseException());
		assertTrue(testObject.getCloseSessionException() == exception);
		assertNull(testObject.getCommitTransactionException());
		assertNull(testObject.getCreateSessionException());
		assertNull(testObject.getOpenDatabaseException());
		assertNull(testObject.getRollbackTransactionException());
		testObject.setCloseSessionException(null);
		
		
		testObject.setCommitTransactionException(exception);
		assertNull(testObject.getBeginTransactionException());
		assertNull(testObject.getCloseDatabaseException());
		assertNull(testObject.getCloseSessionException());
		assertTrue(testObject.getCommitTransactionException() == exception);
		assertNull(testObject.getCreateSessionException());
		assertNull(testObject.getOpenDatabaseException());
		assertNull(testObject.getRollbackTransactionException());
		testObject.setCommitTransactionException(null);
		
		
		testObject.setCreateSessionException(exception);
		assertNull(testObject.getBeginTransactionException());
		assertNull(testObject.getCloseDatabaseException());
		assertNull(testObject.getCloseSessionException());
		assertNull(testObject.getCommitTransactionException());
		assertTrue(testObject.getCreateSessionException() == exception);
		assertNull(testObject.getOpenDatabaseException());
		assertNull(testObject.getRollbackTransactionException());
		testObject.setCreateSessionException(null);
		
		
		testObject.setOpenDatabaseException(exception);
		assertNull(testObject.getBeginTransactionException());
		assertNull(testObject.getCloseDatabaseException());
		assertNull(testObject.getCloseSessionException());
		assertNull(testObject.getCommitTransactionException());
		assertNull(testObject.getCreateSessionException());
		assertTrue(testObject.getOpenDatabaseException() == exception);
		assertNull(testObject.getRollbackTransactionException());
		testObject.setOpenDatabaseException(null);
		
		
		testObject.setRollbackTransactionException(exception);
		assertNull(testObject.getBeginTransactionException());
		assertNull(testObject.getCloseDatabaseException());
		assertNull(testObject.getCloseSessionException());
		assertNull(testObject.getCommitTransactionException());
		assertNull(testObject.getCreateSessionException());
		assertNull(testObject.getOpenDatabaseException());
		assertTrue(testObject.getRollbackTransactionException() == exception);
		testObject.setRollbackTransactionException(null);
		
	}
	
	@Test
	public void testOpenClose()
	{
		// doesn't touch database object
		
		testObject.interceptOpenDatabase(null);
		testObject.interceptCloseDatabase(null);
	}
	
	@Test
	public void testOpenException()
	{
		testObject.setOpenDatabaseException(new HibernateException("Open"));
		
		try
		{
			testObject.interceptOpenDatabase(null);
			fail("Didn't throw");
		}
		catch(HibernateException e)
		{
			assertEquals("Open", e.getMessage());
		}
	}
	
	@Test
	public void testCloseException()
	{
		testObject.setCloseDatabaseException(new HibernateException("Close"));
		
		try
		{
			testObject.interceptCloseDatabase(null);
			fail("Didn't throw");
		}
		catch(HibernateException e)
		{
			assertEquals("Close", e.getMessage());
		}
	}
	
	@Test
	public void testWrapUnwrapSession()
	{
		TestMockSession originalSession = new TestMockSession();
		
		Session wrapped = testObject.interceptCreateSession(originalSession);
		
		assertTrue(wrapped instanceof MockSession);
		assertFalse(wrapped == originalSession);
		
		assertTrue(((MockSession)wrapped).getRealSession() == originalSession);
		
		Session unwrapped = testObject.interceptCloseSession(wrapped);
		
		assertTrue(unwrapped == originalSession);
	}
	
	@Test
	public void testDontWrapSession()
	{
		testObject.setSessionWrapperClass(null);
		
		TestMockSession originalSession = new TestMockSession();
		
		Session wrapped = testObject.interceptCreateSession(originalSession);
		
		assertTrue(wrapped == originalSession);
	}
	
	@Test
	public void testWrapTransaction()
	{
		TestMockTransaction originalTransaction = new TestMockTransaction();
		
		Transaction wrapped = testObject.wrapTransaction(originalTransaction);
		
		assertTrue(wrapped instanceof MockTransaction);
		assertFalse(wrapped == originalTransaction);
		
		assertTrue(((MockTransaction)wrapped).getRealTransaction() == originalTransaction);
	}
	
	@Test
	public void testDontWrapTransaction()
	{
		testObject.setTransactionWrapperClass(null);
		TestMockTransaction originalTransaction = new TestMockTransaction();
		
		Transaction wrapped = testObject.wrapTransaction(originalTransaction);
		
		assertTrue(wrapped == originalTransaction);
	}
	
	@Test
	public void testCreateSessionException()
	{
		testObject.setCreateSessionException(new HibernateException("Create"));
		
		try
		{
			testObject.interceptCreateSession(null);
			fail("Didn't throw");
		}
		catch(HibernateException e)
		{
			assertEquals("Create", e.getMessage());
		}
	}
	
	@Test
	public void testCloseSessionException()
	{
		testObject.setCloseSessionException(new HibernateException("Close"));
		
		try
		{
			testObject.interceptCloseSession(null);
			fail("Didn't throw");
		}
		catch(HibernateException e)
		{
			assertEquals("Close", e.getMessage());
		}
	}
	
	@Test(expected = RuntimeException.class)
	public void testBadMockSessionClass()
	{
		testObject.setSessionWrapperClass(BadMockSessionClass.class);
		testObject.wrapSession(null);
	}
	
	@Test(expected = RuntimeException.class)
	public void testBadMockTransactionClass()
	{
		testObject.setTransactionWrapperClass(BadMockTransactionClass.class);
		testObject.wrapTransaction(null);
	}
	
	// convenient class that implements session
	private static class TestMockSession extends MockSession
	{
		private static final long serialVersionUID = 6957553878552424829L;

		public TestMockSession()
		{
			super(null, null);
		}
	}
	
	// convenient class that implements Transaction
	private static class TestMockTransaction extends MockTransaction
	{
		public TestMockTransaction()
		{
			super(null, null);
		}
	}
	
	// class doesn't implement the correct constructor
	private static class BadMockSessionClass extends MockSession
	{
		private static final long serialVersionUID = -9048997691312031358L;

		public BadMockSessionClass()
		{
			super(null, null);
		}
	}
	
	// class doesn't implement the correct constructor
	private static class BadMockTransactionClass extends MockTransaction
	{
		public BadMockTransactionClass()
		{
			super(null, null);
		}
	}
}
