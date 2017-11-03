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

import java.lang.reflect.Constructor;

import net.sourceforge.wsup.hibernate4.database.BaseDatabase;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * This class implements a <code>DatabaseTestInterceptor</code> that wraps <code>Session</code>s and
 * <code>Transaction</code>s so that exceptions can be
 * thrown as desired, or other behavior introduced.
 * 
 * @author Kevin Hunter
 * 
 */
public class MockingDatabaseInterceptor extends AbstractDatabaseTestInterceptor
{
    private HibernateException               openDatabaseException;

    private HibernateException               closeDatabaseException;

    private HibernateException               createSessionException;

    private HibernateException               closeSessionException;

    private HibernateException               beginTransactionException;

    private HibernateException               commitTransactionException;

    private HibernateException               rollbackTransactionException;

    private Class<? extends MockSession>     sessionWrapperClass     = MockSession.class;

    private Class<? extends MockTransaction> transactionWrapperClass = MockTransaction.class;

    /**
     * Constructor
     */
    public MockingDatabaseInterceptor()
    {
    }

    /**
     * @see AbstractDatabaseTestInterceptor#interceptOpenDatabase(BaseDatabase)
     */
    @Override
    public void interceptOpenDatabase(BaseDatabase database) throws HibernateException
    {
        if (getOpenDatabaseException() != null)
        {
            throw getOpenDatabaseException();
        }
    }

    /**
     * @see AbstractDatabaseTestInterceptor#interceptCloseDatabase(BaseDatabase)
     */
    @Override
    public void interceptCloseDatabase(BaseDatabase database) throws HibernateException
    {
        if (getCloseDatabaseException() != null)
        {
            throw getCloseDatabaseException();
        }
    }

    /**
     * @see AbstractDatabaseTestInterceptor#interceptCreateSession(org.hibernate.Session)
     */
    @Override
    public Session interceptCreateSession(Session session) throws HibernateException
    {
        if (getCreateSessionException() != null)
        {
            throw getCreateSessionException();
        }

        return wrapSession(session);
    }

    /**
     * @see AbstractDatabaseTestInterceptor#interceptCloseSession(org.hibernate.Session)
     */
    @Override
    public Session interceptCloseSession(Session session) throws HibernateException
    {
        if (getCloseSessionException() != null)
        {
            throw getCloseSessionException();
        }

        return ((MockSession) session).getRealSession();
    }

    /**
     * Get the exception, if any, that will be thrown when a session is created.
     * 
     * @return <code>HibernateException</code> instance, or <code>null</code>.
     */
    public HibernateException getCreateSessionException()
    {
        return createSessionException;
    }

    /**
     * Set the exception, if any, that will be thrown when a session is opened.
     * 
     * @param beginSessionException
     *            <code>HibernateException</code> instance, or <code>null</code> to not throw
     *            anything.
     */
    public void setCreateSessionException(HibernateException beginSessionException)
    {
        this.createSessionException = beginSessionException;
    }

    /**
     * Get the exception, if any, that will be thrown when a session is closed.
     * 
     * @return <code>HibernateException</code> instance, or <code>null</code>.
     */
    public HibernateException getCloseSessionException()
    {
        return closeSessionException;
    }

    /**
     * Set the exception, if any, that will be thrown when a session is closed.
     * 
     * @param closeSessionException
     *            <code>HibernateException</code> instance, or <code>null</code> to not throw
     *            anything.
     */
    public void setCloseSessionException(HibernateException closeSessionException)
    {
        this.closeSessionException = closeSessionException;
    }

    /**
     * Get the exception, if any, that will be thrown when a transaction is
     * begun.
     * 
     * @return <code>HibernateException</code> instance, or <code>null</code>.
     */
    public HibernateException getBeginTransactionException()
    {
        return beginTransactionException;
    }

    /**
     * Set the exception, if any, that will be thrown when a transaction is
     * begun.
     * 
     * @param beginTransactionException
     *            <code>HibernateException</code> instance, or <code>null</code> to not throw
     *            anything.
     */
    public void setBeginTransactionException(HibernateException beginTransactionException)
    {
        this.beginTransactionException = beginTransactionException;
    }

    /**
     * Get the exception, if any, that will be thrown when a transaction is
     * committed.
     * 
     * @return <code>HibernateException</code> instance, or <code>null</code>.
     */
    public HibernateException getCommitTransactionException()
    {
        return commitTransactionException;
    }

    /**
     * Set the exception, if any, that will be thrown when a transaction is
     * committed.
     * 
     * @param commitTransactionException
     *            <code>HibernateException</code> instance, or <code>null</code> to not throw
     *            anything.
     */
    public void setCommitTransactionException(HibernateException commitTransactionException)
    {
        this.commitTransactionException = commitTransactionException;
    }

    /**
     * Get the exception, if any, that will be thrown when a transaction is
     * rolled back.
     * 
     * @return <code>HibernateException</code> instance, or <code>null</code>.
     */
    public HibernateException getRollbackTransactionException()
    {
        return rollbackTransactionException;
    }

    /**
     * Set the exception, if any, that will be thrown when a transaction is
     * rolled back.
     * 
     * @param rollbackTransactionException
     *            <code>HibernateException</code> instance, or <code>null</code> to not throw
     *            anything.
     */
    public void setRollbackTransactionException(HibernateException rollbackTransactionException)
    {
        this.rollbackTransactionException = rollbackTransactionException;
    }

    /**
     * Get the exception, if any, that will be thrown when the database is
     * opened.
     * 
     * @return <code>HibernateException</code> instance, or <code>null</code>.
     */
    public HibernateException getOpenDatabaseException()
    {
        return openDatabaseException;
    }

    /**
     * Set the exception, if any, that will be thrown when the database is
     * opened.
     * 
     * @param openDatabaseException
     *            <code>HibernateException</code> instance, or <code>null</code> to not throw
     *            anything.
     */
    public void setOpenDatabaseException(HibernateException openDatabaseException)
    {
        this.openDatabaseException = openDatabaseException;
    }

    /**
     * Get the exception, if any, that will be thrown when the database is
     * closed.
     * 
     * @return <code>HibernateException</code> instance, or <code>null</code>.
     */
    public HibernateException getCloseDatabaseException()
    {
        return closeDatabaseException;
    }

    /**
     * Set the exception, if any, that will be thrown when the database is
     * closed.
     * 
     * @param closeDatabaseException
     *            <code>HibernateException</code> instance, or <code>null</code> to not throw
     *            anything.
     */
    public void setCloseDatabaseException(HibernateException closeDatabaseException)
    {
        this.closeDatabaseException = closeDatabaseException;
    }

    /**
     * Get the <code>Class</code> that will be used to wrap sessions.
     * 
     * @return <code>MockSession</code> <code>Class</code>, or <code>null</code> if sessions will
     *         not be wrapped.
     * @see #wrapSession(Session)
     */
    public Class<? extends MockSession> getSessionWrapperClass()
    {
        return sessionWrapperClass;
    }

    /**
     * Set the <code>Class</code> that will be used to wrap sessions.
     * Defaults to <code>MockSession</code>.
     * 
     * @param sessionWrapperClass
     *            <code>MockSession</code> <code>Class</code>, or <code>null</code> if sessions will
     *            not be wrapped.
     * @see #wrapSession(Session)
     * @see MockSession
     */
    public void setSessionWrapperClass(Class<? extends MockSession> sessionWrapperClass)
    {
        this.sessionWrapperClass = sessionWrapperClass;
    }

    /**
     * Get the <code>Class</code> that will be used to wrap transactions.
     * 
     * @return <code>MockTransaction</code> <code>Class</code>, or <code>null</code> if transactions
     *         will not be wrapped.
     * @see #wrapTransaction(Transaction)
     */
    public Class<? extends MockTransaction> getTransactionWrapperClass()
    {
        return transactionWrapperClass;
    }

    /**
     * Set the <code>Class</code> that will be used to wrap transactions.
     * Defaults to <code>MockTransaction</code>.
     * 
     * @param transactionWrapperClass
     *            <code>MockTransaction</code> <code>Class</code>, or <code>null</code> if
     *            transactions will not be wrapped.
     * @see #wrapTransaction(Transaction)
     * @see MockTransaction
     */
    public void setTransactionWrapperClass(Class<? extends MockTransaction> transactionWrapperClass)
    {
        this.transactionWrapperClass = transactionWrapperClass;
    }

    /**
     * Wrap a <code>Session</code> in a <code>MockSession</code> class.
     * 
     * @param realSession
     *            <code>Session</code> to be wrapped.
     * @return Original session (if sessions not being wrapped) or <code>MockSession</code> wrapping
     *         that session.
     * @see #setSessionWrapperClass(Class)
     */
    public Session wrapSession(Session realSession)
    {
        if (sessionWrapperClass == null)
        {
            return realSession;
        }

        try
        {
            Constructor<? extends MockSession> constructor = sessionWrapperClass
                .getConstructor(MockingDatabaseInterceptor.class, Session.class);
            return constructor.newInstance(this, realSession);
        }
        catch (Exception e)
        {
            throw new RuntimeException("wrapSession failed", e);
        }
    }

    /**
     * Wrap a <code>Transaction</code> in a <code>MockTransaction</code> class.
     * 
     * @param realTransaction
     *            <code>Transaction</code> to be wrapped.
     * @return Original transaction (if transactions not being wrapped) or
     *         <code>MockTransaction</code> wrapping that transaction.
     * @see #setTransactionWrapperClass(Class)
     */
    public Transaction wrapTransaction(Transaction realTransaction)
    {
        if (transactionWrapperClass == null)
        {
            return realTransaction;
        }

        try
        {
            Constructor<? extends MockTransaction> constructor = transactionWrapperClass
                .getConstructor(MockingDatabaseInterceptor.class, Transaction.class);
            return constructor.newInstance(this, realTransaction);
        }
        catch (Exception e)
        {
            throw new RuntimeException("wrapTransaction failed", e);
        }
    }
}
