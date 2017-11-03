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

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;

import net.sourceforge.wsup.core.Assert;
import net.sourceforge.wsup.hibernate.BaseDatabase;
import net.sourceforge.wsup.hibernate.DataAccessContext;
import net.sourceforge.wsup.hibernate.DatabaseContext;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides an implementation of {@link DatabaseContext} that works
 * with {@link BaseDatabase}.
 * 
 * @author Kevin Hunter
 * 
 */
public class DatabaseContextImpl implements DatabaseContext
{
	private static final Logger log = LoggerFactory
			.getLogger(DatabaseContextImpl.class);

	private final BaseDatabase database;

	private Session session;

	private Transaction transaction;

	/**
	 * Constructor.
	 * 
	 * @param database
	 *            <code>BaseDatabase</code> instance with which this is
	 *            associated.
	 */
	public DatabaseContextImpl(BaseDatabase database)
	{
		this.database = database;
	}

	/**
	 * @see DatabaseContext#beginSession(boolean)
	 */
	@Override
	public void beginSession(boolean preverify) throws HibernateException
	{
		if (session == null)
		{
			session = database.createHibernateSession(preverify);
		}
	}

	/**
	 * @see DatabaseContext#beginTransaction()
	 */
	@Override
	public void beginTransaction() throws HibernateException
	{
		if (transaction == null)
		{
			beginSession(false);

			transaction = session.beginTransaction();
		}
	}

	/**
	 * @see DatabaseContext#closeSession()
	 */
	@Override
	public void closeSession() throws HibernateException
	{
		Assert.isNull(transaction);

		doCloseSession();
	}

	/**
	 * @see DatabaseContext#commitTransaction()
	 */
	@Override
	public void commitTransaction() throws HibernateException
	{
		Assert.isNotNull(transaction);

		try
		{
			transaction.commit();
		}
		catch (HibernateException e)
		{
			doCloseSession();
			throw e;
		}
		finally
		{
			transaction = null;
		}
	}

	/**
	 * @see DatabaseContext#rollbackTransaction()
	 */
	@Override
	public void rollbackTransaction() throws HibernateException
	{
		Assert.isNotNull(transaction);

		try
		{
			transaction.rollback();
		}
		finally
		{
			transaction = null;
			doCloseSession();
		}
	}

	/**
	 * @see DatabaseContext#destroySession()
	 */
	@Override
	public void destroySession()
	{
		if (transaction != null)
		{
			try
			{
				transaction.rollback();
			}
			catch (HibernateException e)
			{
				log.info("Exception eaten during destroySession/rollback", e);
			}

			transaction = null;
		}

		try
		{
			doCloseSession();
		}
		catch (HibernateException e)
		{
			log.info("Exception eaten during destroySession/close", e);
		}
	}

	/**
	 * @see DatabaseContext#isInTransaction()
	 */
	@Override
	public boolean isInTransaction()
	{
		return transaction != null;
	}

	/**
	 * @see DatabaseContext#isSessionOpen()
	 */
	@Override
	public boolean isSessionOpen()
	{
		return session != null;
	}

	/**
	 * @see DatabaseContext#createDataAccessContext()
	 */
	@Override
	public DataAccessContext createDataAccessContext()
			throws HibernateException
	{
		beginSession(false);

		return new DataAccessContextImpl(this);
	}

	/**
	 * @see DatabaseContext#getDatabase()
	 */
	@Override
	public BaseDatabase getDatabase()
	{
		return database;
	}

	/**
	 * @see DatabaseContext#createBlob(byte[])
	 */
	@Override
	public Blob createBlob(byte[] bytes)
	{
		beginSession(false);
		return Hibernate.createBlob(bytes, session);
	}

	/**
	 * @see DatabaseContext#createBlob(java.io.InputStream, long)
	 */
	@Override
	public Blob createBlob(InputStream stream, long length)
	{
		beginSession(false);
		return Hibernate.createBlob(stream, length, session);
	}

	/**
	 * @see DatabaseContext#createClob(java.lang.String)
	 */
	@Override
	public Clob createClob(String string)
	{
		beginSession(false);
		return Hibernate.createClob(string, session);
	}

	/**
	 * @see DatabaseContext#createClob(java.io.Reader, long)
	 */
	@Override
	public Clob createClob(Reader reader, long length)
	{
		beginSession(false);
		return Hibernate.createClob(reader, length, session);
	}

	/**
	 * Get the underlying Hibernate <code>Session</code> object.
	 * 
	 * @return <code>Session</code> object if a session is open,
	 *         <code>null</code> otherwise.
	 */
	public Session getSession()
	{
		return session;
	}

	/**
	 * Get the underlying Hibernate <code>Session</code> object, ensuring that a
	 * session has actually been started. If necessary, calls
	 * <code>beginSession(false)</code>;
	 * 
	 * @return <code>Session</code> object. Will not be <code>null</code>.
	 */
	public Session getSafeSession()
	{
		beginSession(false);
		return session;
	}

	/**
	 * Get the underlying Hibernate <code>Transaction</code> object.
	 * 
	 * @return <code>Transaction</code> object if a transaction is open,
	 *         <code>null</code> otherwise.
	 */
	public Transaction getTransaction()
	{
		return transaction;
	}

	/**
	 * Do the actual work of closing a session, if required, ensuring that the
	 * session object is cleaned up even if an exception is thrown.
	 */
	private void doCloseSession()
	{
		if (session != null)
		{
			try
			{
				database.closeHibernateSession(session);
			}
			finally
			{
				session = null;
			}
		}
	}
}
