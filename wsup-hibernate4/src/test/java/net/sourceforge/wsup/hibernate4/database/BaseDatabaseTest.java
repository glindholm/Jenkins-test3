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

package net.sourceforge.wsup.hibernate4.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.impl.MockLogger;

/**
 * Unit test for BaseDatabase class.
 * 
 * @author Kevin Hunter
 * 
 */
public class BaseDatabaseTest
{
    public BaseDatabaseTest()
    {
    }

    @Before
    public void setup()
    {
    }

    @After
    public void cleanup()
    {
        BaseDatabase database = TestDatabaseManager.getSingleton();
        if (database != null)
        {
            database.setDatabaseTestInterceptor(null); // remove any left-over interceptor
        }

        MockLogger.clear();
    }

    @Test
    public void openAndCloseWorkAsExpected() throws Exception
    {
        BaseDatabase database = TestDatabaseManager.bootNewTestDatabase();
        assertFalse(database.isOpen());
        assertNull(database.getConfiguration());

        database.open();
        assertTrue(database.isOpen());
        assertNotNull(database.getConfiguration());

        database.close();
        assertFalse(database.isOpen());
        assertNull(database.getConfiguration());
    }

    @Test
    public void canGetAndReturnSession() throws Exception
    {
        BaseDatabase database = TestDatabaseManager.openTestDatabaseIfRequired();

        assertTrue(database.isOpen());
        assertEquals(0, database.getOutstandingSessionCount());

        Session session = database.createHibernateSession(false);
        assertTrue(session.isOpen());

        assertEquals(1, database.getOutstandingSessionCount());

        database.closeHibernateSession(session);
        assertFalse(session.isOpen());

        assertEquals(0, database.getOutstandingSessionCount());
    }

    @Test
    public void doubleOpenIsBenign() throws Exception
    {
        MockLogger.get(BaseDatabase.class).setWarn();

        BaseDatabase database = TestDatabaseManager.bootNewTestDatabase();

        assertFalse(database.isOpen());
        database.open();
        assertTrue(database.isOpen());
        database.open();
        assertTrue(database.isOpen());
    }

    @Test
    public void doubleCloseIsBenign() throws Exception
    {
        MockLogger.get(BaseDatabase.class).setWarn();

        BaseDatabase database = TestDatabaseManager.bootNewTestDatabase();

        assertFalse(database.isOpen());
        database.open();
        assertTrue(database.isOpen());
        database.close();
        assertFalse(database.isOpen());
        database.close();
    }

    @Test
    public void canCloseWithSessionPending() throws Exception
    {
        BaseDatabase database = TestDatabaseManager.openTestDatabaseIfRequired();

        MockLogger.get(BaseDatabase.class).setError();

        Session session = database.createHibernateSession(false);
        assertTrue(session.isOpen());
        assertEquals(1, database.getOutstandingSessionCount());

        database.close();
        try
        {
            session.close();
        }
        catch (HibernateException e)
        {
            // Will probably throw because it no longer has a service registry
            // That's OK - we're doing this just to make sure things 
            // are cleaned up.
        }
    }

    @Test
    public void coverExceptionDuringClose() throws Exception
    {
        TestDatabaseManager.shutdownTestDatabase();
        TestDatabase database = new TestDatabase();
        try
        {
            MockLogger.get(BaseDatabase.class).setOff();
            /*
             * Will cause a NullPointerException during close (which will be
             * caught) and then will assert on the session count.
             */

            database.closeHibernateSession(null);
            fail("Didn't assert");
        }
        catch (AssertionError e)
        {
            /*
             * Catch, rather than using "expected = " mechanism, to make sure
             * that it is this operation that asserts.
             */
        }
    }

    @Test
    public void getSessionWhileClosedShouldAssert() throws Exception
    {
        TestDatabaseManager.shutdownTestDatabase();
        TestDatabase database = new TestDatabase();
        assertFalse(database.isOpen());
        try
        {
            /*
             * Will assert because the database isn't open.
             */

            database.createHibernateSession(false);
            fail("Didn't assert");
        }
        catch (AssertionError e)
        {
            /*
             * Catch, rather than using "expected = " mechanism, to make sure
             * that it is this operation that asserts.
             */
        }
    }

    @Test
    public void canCreateDatabaseContext() throws Exception
    {
        BaseDatabase database = TestDatabaseManager.openTestDatabaseIfRequired();
        assertNotNull(database.createDatabaseContext());
    }

    /*
     * Get a verified session under normal circumstances (will work internally
     * without any retries required)
     */
    @Test
    public void canGetVerifiedSessionFirstTry() throws Exception
    {
        BaseDatabase database = TestDatabaseManager.openTestDatabaseIfRequired();
        Session session = database.createHibernateSession(true);
        assertTrue(session.isOpen());
        database.closeHibernateSession(session);
    }

    /*
     * Force a situation in which the first attempt at a verified connection
     * will fail, thus requiring a retry
     */
    @Test
    public void canGetVerifiedSessionWhenFirstAttemptFails() throws Exception
    {
        MockLogger.get("org.hibernate.util.JDBCExceptionReporter").setOff();

        TestDatabase database = TestDatabaseManager.openTestDatabaseIfRequired();

        /*
         * Make multiple simultaneous connections so that we have several JDBC
         * connections in the connection pool.
         */
        Session[] sessions = new Session[5];
        for (int i = 0; i < sessions.length; i++)
        {
            sessions[i] = database.createHibernateSession(false);
            assertTrue(sessions[i].isOpen());
        }

        for (int i = 0; i < sessions.length; i++)
        {
            database.closeHibernateSession(sessions[i]);
        }

        /*
         * Shut down the HSQLDB database, but keep the TestDatabase instance
         * (and thus the SessionFactory, etc.) around. Simulates the database
         * crashing.
         */
        database.shutdown();

        /*
         * Because we've shut down the database, we lose the table declarations,
         * so we have to regenerate them.
         */

        database.initializeTables();

        /*
         * Now ask for a session. Initial attempt will fail because the first
         * JDBC connection it gets will be dead, but should recover and provide
         * an Session without thrown an exception.
         */

        MockLogger.get("org.hibernate.engine.jdbc.spi.SqlExceptionHelper").setOff();

        Session session = database.createHibernateSession(true);
        assertTrue(session.isOpen());
        database.closeHibernateSession(session);
    }

    /*
     * Create the situation in which we are unable to get a verified session
     * prior to the timeout.
     */
    @Test
    public void throwsIfCantGetVerifiedSessionBeforeTimeout() throws Exception
    {
        TestDatabaseManager.shutdownTestDatabase();
        BarfingTestDatabase database = new BarfingTestDatabase();
        database.open();
        try
        {
            database.createHibernateSession(true);
            fail("Didn't throw");
        }
        catch (HibernateException e)
        {
            assertEquals("Barf", e.getMessage());
        }

        database.shutdown();
    }

    /*
     * Creates a Configuration based on the hibernate.cfg.xml file.
     */
    @Test
    public void baseClassCanReturnConfiguration()
    {
        DefaultConfigurationDatabase database = new DefaultConfigurationDatabase();
        assertNotNull(database.createConfiguration());
    }

    @Test
    public void verifiedSessionWithNullVerificationSqlStillWorks()
    {
        TestDatabaseWithoutVerificationSql database = new TestDatabaseWithoutVerificationSql();
        assertNull(database.getVerificationSQL());

        database.open();
        Session session = database.createHibernateSession(true);
        assertNotNull(session);
        database.closeHibernateSession(session);
        database.close();
    }

    /**
     * Version of TestDatabase that will always throw an exception in
     * getRawSession, and which has a short verification timeout. Used to test
     * that if we go past our timeout without getting a verified session that
     * we'll rethrow the exception we get. (Can't do this with the
     * interceptor stuff because that operates too high up.)
     * 
     * @author Kevin Hunter
     * 
     */
    private static class BarfingTestDatabase extends TestDatabase
    {
        public BarfingTestDatabase()
        {
        }

        @Override
        protected synchronized Session getRawSession() throws HibernateException
        {
            throw new HibernateException("Barf");
        }

        @Override
        protected long getSessionVerificationTimeout()
        {
            return 1000L; // one second
        }
    }

    /**
     * Version of BaseDatabase with createConfiguration made 'public'
     */
    private static class DefaultConfigurationDatabase extends BaseDatabase
    {
        public DefaultConfigurationDatabase()
        {
        }

        @Override
        public Configuration createConfiguration() throws HibernateException
        {
            return super.createConfiguration();
        }

        @Override
        public String getVerificationSQL()
        {
            return super.getVerificationSQL();
        }
    }
}
