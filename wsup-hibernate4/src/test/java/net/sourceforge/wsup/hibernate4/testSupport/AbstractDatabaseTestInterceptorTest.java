/*
 *  Copyright (c) 2012 Kevin Hunter
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

package net.sourceforge.wsup.hibernate4.testSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import net.sourceforge.wsup.hibernate4.database.BaseDatabase;
import net.sourceforge.wsup.hibernate4.database.TestDatabase;
import net.sourceforge.wsup.hibernate4.database.TestDatabaseManager;
import net.sourceforge.wsup.hibernate4.testSupport.AbstractDatabaseTestInterceptor;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.impl.MockLogger;

public class AbstractDatabaseTestInterceptorTest
{
    public AbstractDatabaseTestInterceptorTest()
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
    public void coverInterceptor() throws Exception
    {
        TestDatabase database = TestDatabaseManager.getSingleton();
        if (database != null)
        {
            if (database.isOpen())
            {
                database.close();
            }
        }
        else
        {
            TestDatabaseManager.bootTestDatabaseIfRequired();
        }

        database = TestDatabaseManager.getSingleton();
        assertNull(database.getDatabasetestInterceptor());
        TestInterceptor interceptor = new TestInterceptor();
        database.setDatabaseTestInterceptor(interceptor);
        assertNotNull(database.getDatabasetestInterceptor());

        assertEquals(0, interceptor.getOpenCalled());
        assertEquals(0, interceptor.getCreateSessionCalled());
        assertEquals(0, interceptor.getCloseSessionCalled());
        assertEquals(0, interceptor.getCloseCalled());

        database.open();
        assertEquals(1, interceptor.getOpenCalled());

        Session session = database.createHibernateSession(false);
        assertEquals(1, interceptor.getCreateSessionCalled());

        database.closeHibernateSession(session);
        assertEquals(1, interceptor.getCloseSessionCalled());

        database.close();

        assertEquals(1, interceptor.getOpenCalled());
        assertEquals(1, interceptor.getCreateSessionCalled());
        assertEquals(1, interceptor.getCloseSessionCalled());
        assertEquals(1, interceptor.getCloseCalled());
    }

    private static class TestInterceptor extends AbstractDatabaseTestInterceptor
    {
        private int openCalled          = 0;
        private int closeCalled         = 0;
        private int createSessionCalled = 0;
        private int closeSessionCalled  = 0;

        public TestInterceptor()
        {
        }

        @Override
        public void interceptCloseDatabase(BaseDatabase database) throws HibernateException
        {
            closeCalled++;
            super.interceptCloseDatabase(database);
        }

        @Override
        public void interceptOpenDatabase(BaseDatabase database) throws HibernateException
        {
            openCalled++;
            super.interceptOpenDatabase(database);
        }

        @Override
        public Session interceptCloseSession(Session session) throws HibernateException
        {
            closeSessionCalled++;
            return super.interceptCloseSession(session);
        }

        @Override
        public Session interceptCreateSession(Session session) throws HibernateException
        {
            createSessionCalled++;
            return super.interceptCreateSession(session);
        }

        public int getOpenCalled()
        {
            return openCalled;
        }

        public int getCloseCalled()
        {
            return closeCalled;
        }

        public int getCreateSessionCalled()
        {
            return createSessionCalled;
        }

        public int getCloseSessionCalled()
        {
            return closeSessionCalled;
        }
    }

}
