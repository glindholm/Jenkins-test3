/*
 *  Copyright 2012 Kevin Hunter
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

import javax.transaction.Synchronization;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.engine.transaction.spi.LocalStatus;

/**
 * This is a test class that can be used to "wrap" a real Hibernate <code>Transaction</code> so that
 * exceptions can be thrown when required for
 * testing.
 * 
 * @author Kevin Hunter
 * @see MockSession
 * 
 */
public class MockTransaction implements Transaction
{
    private final Transaction                realTransaction;

    private final MockingDatabaseInterceptor interceptor;

    public MockTransaction(MockingDatabaseInterceptor interceptor, Transaction realTransaction)
    {
        this.interceptor = interceptor;
        this.realTransaction = realTransaction;
    }

    public Transaction getRealTransaction()
    {
        return realTransaction;
    }

    @Override
    public void commit() throws HibernateException
    {
        if (interceptor.getCommitTransactionException() != null)
        {
            throw interceptor.getCommitTransactionException();
        }

        realTransaction.commit();
    }

    @Override
    public void rollback() throws HibernateException
    {
        if (interceptor.getRollbackTransactionException() != null)
        {
            throw interceptor.getRollbackTransactionException();
        }

        realTransaction.rollback();
    }

    @Override
    public void begin() throws HibernateException
    {
        realTransaction.begin();
    }

    @Override
    public boolean isActive() throws HibernateException
    {
        return realTransaction.isActive();
    }

    @Override
    public void registerSynchronization(Synchronization synchronization) throws HibernateException
    {
        realTransaction.registerSynchronization(synchronization);
    }

    @Override
    public void setTimeout(int seconds)
    {
        realTransaction.setTimeout(seconds);
    }

    @Override
    public boolean wasCommitted() throws HibernateException
    {
        return realTransaction.wasCommitted();
    }

    @Override
    public boolean wasRolledBack() throws HibernateException
    {
        return realTransaction.wasRolledBack();
    }

    @Override
    public LocalStatus getLocalStatus()
    {
        return realTransaction.getLocalStatus();
    }

    @Override
    public int getTimeout()
    {
        return realTransaction.getTimeout();
    }

    @Override
    public boolean isInitiator()
    {
        return realTransaction.isInitiator();
    }

    @Override
    public boolean isParticipating()
    {
        return realTransaction.isParticipating();
    }
}
