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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;

import net.sourceforge.wsup.hibernate.BaseDatabase;
import net.sourceforge.wsup.hibernate.TestDatabase;
import net.sourceforge.wsup.hibernate.TestDatabaseManager;
import net.sourceforge.wsup.hibernate.testSupport.MockingDatabaseInterceptor;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.impl.MockLogger;

/**
 * Unit test for the DatabaseContextImpl class.
 * 
 * @author Kevin Hunter
 * 
 */
public class DatabaseContextImplTest
{
	private TestDatabase database;

	private DatabaseContextImpl context;

	public DatabaseContextImplTest()
	{
	}

	@Before
	public void setup() throws Exception
	{
    	MockLogger.get("org.hibernate").setWarn();
    	MockLogger.get("com.mchange.v2.c3p0").setWarn();
    	MockLogger.get("com.mchange.v2.log").setWarn();

		database = TestDatabaseManager.openTestDatabaseIfRequired();
		context = new DatabaseContextImpl(database);
	}

	@After
	public void cleanup()
	{
		// clean up any interceptor
		database.setDatabaseTestInterceptor(null);

		// just in case
		context.destroySession();

		// ensure that no matter what happens we clean up sessions
		assertEquals(0, database.getOutstandingSessionCount());

		MockLogger.clear();
	}

	@Test
	public void testStartingConfiguration()
	{
		assertFalse(context.isSessionOpen());
		assertFalse(context.isInTransaction());

		assertNull(context.getSession());
		assertNull(context.getTransaction());
		assertNotNull(context.getDatabase());
	}

	/*
	 * Open and close a session
	 */
	@Test
	public void testSession()
	{
		context.beginSession(false);
		assertTrue(context.isSessionOpen());
		Session session = context.getSession();
		assertNotNull(session);

		// second open has no effect - same session is there
		context.beginSession(true);
		assertTrue(context.isSessionOpen());
		assertTrue(session == context.getSession());

		context.closeSession();
		assertFalse(context.isSessionOpen());
		assertNull(context.getSession());

		// second close is benign
		context.closeSession();
		assertFalse(context.isSessionOpen());
		assertNull(context.getSession());
	}

	/*
	 * Basic transaction manipulation
	 */
	@Test
	public void testTransaction()
	{
		context.beginSession(false);
		assertNull(context.getTransaction());

		context.beginTransaction();
		Transaction transaction = context.getTransaction();

		// second begin does nothing
		context.beginTransaction();
		assertTrue(transaction == context.getTransaction());

		// commit eliminates internal object
		context.commitTransaction();
		assertNull(context.getTransaction());
		assertFalse(transaction.isActive());
	}

	/*
	 * Beginning a transaction will automatically open a session
	 */
	@Test
	public void testBeginTransactionOpensSession()
	{
		assertFalse(context.isSessionOpen());
		assertFalse(context.isInTransaction());

		context.beginTransaction();

		assertTrue(context.isSessionOpen());
		assertTrue(context.isInTransaction());
	}

	/*
	 * Commit will close the transaction, but not the session
	 */
	@Test
	public void testCommit()
	{
		assertFalse(context.isSessionOpen());
		assertFalse(context.isInTransaction());

		context.beginSession(false);

		assertTrue(context.isSessionOpen());
		assertFalse(context.isInTransaction());

		context.beginTransaction();

		assertTrue(context.isSessionOpen());
		assertTrue(context.isInTransaction());

		context.commitTransaction();

		assertTrue(context.isSessionOpen());
		assertFalse(context.isInTransaction());
	}

	/*
	 * Rollback will close the transaction and not the session
	 */
	@Test
	public void testRollback()
	{
		assertFalse(context.isSessionOpen());
		assertFalse(context.isInTransaction());

		context.beginSession(false);

		assertTrue(context.isSessionOpen());
		assertFalse(context.isInTransaction());

		context.beginTransaction();

		assertTrue(context.isSessionOpen());
		assertTrue(context.isInTransaction());

		context.rollbackTransaction();

		assertFalse(context.isSessionOpen());
		assertFalse(context.isInTransaction());
	}

	@Test
	public void testCloseSessionAssertsIfTransactionOpen()
	{
		context.beginSession(false);
		context.beginTransaction();
		try
		{
			context.closeSession();
			fail("Didn't assert");
		}
		catch (AssertionError e)
		{
		}
	}

	@Test
	public void testCommitAssertsIfTransactionNotOpen()
	{
		context.beginSession(false);
		try
		{
			context.commitTransaction();
			fail("Didn't assert");
		}
		catch (AssertionError e)
		{
		}
	}

	@Test
	public void testRollbackAssertsIfTransactionNotOpen()
	{
		context.beginSession(false);
		try
		{
			context.rollbackTransaction();
			fail("Didn't assert");
		}
		catch (AssertionError e)
		{
		}
	}

	@Test
	public void testDestroyIsBenign()
	{
		// no session open
		context.destroySession();
		assertFalse(context.isSessionOpen());
		assertFalse(context.isInTransaction());

		// session, but no transaction
		context.beginSession(false);
		context.destroySession();
		assertFalse(context.isSessionOpen());
		assertFalse(context.isInTransaction());

		// session and transaction
		context.beginSession(false);
		context.beginTransaction();
		context.destroySession();
		assertFalse(context.isSessionOpen());
		assertFalse(context.isInTransaction());
	}

	@Test
	public void testDAOContextStartsSession()
	{
		assertFalse(context.isSessionOpen());
		DataAccessContextImpl daoContext = (DataAccessContextImpl) context
				.createDataAccessContext();
		assertTrue(context.isSessionOpen());
		assertEquals(context.getSession(), daoContext.getSession());
		assertEquals(context.getDatabase(), daoContext.getDatabase());
	}

	@Test
	public void testCommitExceptionClosesSession()
	{
		MockingDatabaseInterceptor interceptor = new MockingDatabaseInterceptor();
		interceptor.setCommitTransactionException(new HibernateException(
				"Commit"));
		database.setDatabaseTestInterceptor(interceptor);

		context.beginSession(false);
		context.beginTransaction();

		try
		{
			context.commitTransaction();
			fail("Didn't throw");
		}
		catch (HibernateException e)
		{
			assertEquals("Commit", e.getMessage());
		}

		assertFalse(context.isInTransaction());
		assertFalse(context.isSessionOpen());
	}

	@Test
	public void testDestroyEatsExceptions()
	{
		// suppress messages about exceptions being eaten
		MockLogger.get(DatabaseContextImpl.class).setWarn();
		// and about exceptions during close
		MockLogger.get(BaseDatabase.class).setError();

		MockingDatabaseInterceptor interceptor = new MockingDatabaseInterceptor();
		interceptor.setRollbackTransactionException(new HibernateException(
				"Rollback"));
		interceptor.setCloseSessionException(new HibernateException("Close"));
		database.setDatabaseTestInterceptor(interceptor);

		context.beginSession(false);
		assertTrue(context.isSessionOpen());
		Session session = ((DatabaseContextImpl) context).getSession();

		context.beginTransaction();
		assertTrue(context.isInTransaction());

		context.destroySession();

		assertFalse(context.isInTransaction());
		assertFalse(context.isSessionOpen());

		// clean up session count behind us
		database.setDatabaseTestInterceptor(null);
		database.closeHibernateSession(session);
	}

	@Test
	public void testBlobs() throws Exception
	{
		Blob blob1 = context.createBlob(new byte[]
		{
				1, 2, 3
		});
		assertEquals(3L, blob1.length());

		ByteArrayInputStream stream = new ByteArrayInputStream(new byte[]
		{
				4, 5, 6, 7
		});
		Blob blob2 = context.createBlob(stream, 4);
		assertEquals(4L, blob2.length());
	}
	
	@Test
	public void testClobs() throws Exception
	{
		Clob clob1 = context.createClob("abc");
		assertEquals(3, clob1.length());
		
		StringReader reader = new StringReader("defg");
		
		Clob clob2 = context.createClob(reader, 4);
		assertEquals(4, clob2.length());
	}
}
