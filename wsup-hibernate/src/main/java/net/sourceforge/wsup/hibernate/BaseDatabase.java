/*
 *  Copyright 2010 Kevin
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

package net.sourceforge.wsup.hibernate;

import net.sourceforge.wsup.core.Assert;
import net.sourceforge.wsup.hibernate.impl.DatabaseContextImpl;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class serves as a base for Hibernate databases. It provides thread-safe
 * methods for opening and closing the database and creating, returning and
 * counting Hibernate <code>Session</code>s. The primary thing that a user needs
 * to do is to provide an implementation for the abstract method
 * {@link #createConfiguration()}.
 * <p>
 * Most of the methods on this class are threadsafe - they are marked. Those
 * which are are synchronized on the <code>BaseDatabase</code> object.
 * </p>
 * 
 * @author Kevin Hunter
 * 
 */
public abstract class BaseDatabase implements DatabaseContextProvider
{
	private static final Logger log = LoggerFactory
			.getLogger(BaseDatabase.class);

	/*
	 * Hibernate SessionFactory. Accesses to this variable must be thread-safe,
	 * and synchronized on "this".
	 */
	private volatile SessionFactory sessionFactory;

	/*
	 * Count of number of outstanding Hibernate Sessions. Accesses to this
	 * variable must be thread-safe, and synchronized on "this".
	 */
	private volatile int outstandingSessions;

	/*
	 * Hibernate Configuration. Accesses to this variable must be thread-safe,
	 * and synchronized on "this".
	 */
	private volatile Configuration configuration;

	/*
	 * Interceptor that can be used to help test database implementations.
	 * Accesses to this variable must be thread-safe, and synchronized on
	 * "this".
	 */
	private volatile DatabaseTestInterceptor databaseTestInterceptor;

	/**
	 * Base constructor for derived classes
	 */
	protected BaseDatabase()
	{
	}

	/**
	 * Set a database test interceptor.
	 * 
	 * @param databaseTestInterceptor
	 *            <code>DatabaseTestInterceptor</code> instance or
	 *            <code>null</code>.
	 */
	public synchronized void setDatabaseTestInterceptor(
			DatabaseTestInterceptor databaseTestInterceptor)
	{
		this.databaseTestInterceptor = databaseTestInterceptor;
	}

	/**
	 * Get the current database test interceptor.
	 * 
	 * @return <code>DatabaseTestInterceptor</code> instance or
	 *         <code>null</code>.
	 */
	public synchronized DatabaseTestInterceptor getDatabasetestInterceptor()
	{
		return databaseTestInterceptor;
	}

	/**
	 * "Open" the database, making it possible to create <code>Session</code>s.
	 * Opening a database that is already open is benign, although an
	 * informational log message will be made.
	 * <p>
	 * This method is thread-safe.
	 * </p>
	 * 
	 * @throws HibernateException
	 * @see #close()
	 * @see #isOpen()
	 * 
	 */
	public synchronized void open() throws HibernateException
	{
		if (databaseTestInterceptor != null)
		{
			databaseTestInterceptor.interceptOpenDatabase(this);
		}

		if (isOpen())
		{
			log.info("Opening database when already open");
			return;
		}

		configuration = createConfiguration();
		outstandingSessions = 0;
		sessionFactory = configuration.buildSessionFactory();
	}

	/**
	 * "Close" the database. Once the database is closed, no further
	 * <code>Session</code>s can be obtained from it.
	 * <p>
	 * This method is thread-safe.
	 * </p>
	 * 
	 * @throws HibernateException
	 * @see #open()
	 * @see #isOpen()
	 */
	public synchronized void close() throws HibernateException
	{
		if (databaseTestInterceptor != null)
		{
			databaseTestInterceptor.interceptCloseDatabase(this);
		}

		if (!isOpen())
		{
			log.info("Closing database when already closed");
			return;
		}

		if (outstandingSessions != 0)
		{
			log.warn("Closing database with {} outstanding sessions",
					outstandingSessions);
		}

		try
		{
			sessionFactory.close();
		}
		finally
		{
			sessionFactory = null;
			configuration = null;
		}
	}

	/**
	 * Tests whether the database is currently open.
	 * <p>
	 * This method is thread-safe.
	 * </p>
	 * 
	 * @return <code>true</code> if it is open, <code>false</code> otherwise.
	 * @see #open()
	 * @see #close()
	 */
	public synchronized boolean isOpen()
	{
		return sessionFactory != null;
	}

	/**
	 * Return a copy of the Hibernate <code>Configuration</code> object that was
	 * used to bootstrap the <code>SessionFactory</code>. This is primarily
	 * useful for obtaining metainformation about the database, such as the
	 * table name associated with a particular persistent object. The returned
	 * value will be <code>null</code> if the database is not open.
	 * <p>
	 * This method is thread-safe.
	 * </p>
	 * 
	 * @return Hibernate <code>Configuration</code> object
	 */
	public synchronized Configuration getConfiguration()
	{
		return configuration;
	}

	/**
	 * Create and return a Hibernate <code>Session</code> object.
	 * <code>Session</code>s created through this call should be closed by
	 * passing them to {@link #closeHibernateSession(Session)} so that the count
	 * of outstanding <code>Session</code>s is kept correct.
	 * <p>
	 * Note that, despite not being synchronized itself, this method is
	 * thread-safe due to its underlying implementation.
	 * </p>
	 * 
	 * @param verify
	 *            If <code>true</code>, the code uses logic designed to ensure
	 *            that the <code>Session</code> returned is actually live. If
	 *            <code>false</code>, the code simply asks the
	 *            <code>SessionManager</code> for a new <code>Session</code>,
	 *            which can result
	 * @return Hibernate <code>Session</code> object
	 * @throws HibernateException
	 * @see #closeHibernateSession(Session)
	 */

	public Session createHibernateSession(boolean verify)
			throws HibernateException
	{
		Session session;

		if (verify)
		{
			session = getVerifiedSession();
		}
		else
		{
			session = getRawSession();
		}

		if (databaseTestInterceptor != null)
		{
			session = databaseTestInterceptor.interceptCreateSession(session);
		}

		return session;
	}

	/**
	 * Close a <code>Session</code> previously retrieved from
	 * <code>createHibernateSession</code>. Closing a session this way keeps the
	 * count of outstanding <code>Session</code>s correct.
	 * <p>
	 * This method is thread-safe.
	 * </p>
	 * 
	 * @param session
	 *            Hibernate <code>Session</code> object
	 * @see #createHibernateSession(boolean)
	 */
	public synchronized void closeHibernateSession(Session session)
	{
		if (databaseTestInterceptor != null)
		{
			session = databaseTestInterceptor.interceptCloseSession(session);
		}

		outstandingSessions--;
		try
		{
			session.close();
		}
		catch (Exception e)
		{
			log.warn("Exception while closing session", e);
		}

		Assert.isTrue(outstandingSessions >= 0, "session count corrupted");
	}

	/**
	 * Return the number of outstanding sessions.
	 * <p>
	 * This method is thread-safe.
	 * </p>
	 * 
	 * @return Outstanding number of Hibernate <code>Session</code>s.
	 */
	public synchronized int getOutstandingSessionCount()
	{
		return outstandingSessions;
	}

	/**
	 * Derived classes may override this method in order to provide an
	 * <code>Interceptor</code> that will be used when creating
	 * <code>Session</code>s. The default implementation returns
	 * <code>null</code>.
	 * <p>
	 * This method is called from <code>createHibernateSession</code>, which is
	 * synchronized. Thus, overrides of this method are not required to
	 * implement their own thread safety.
	 * </p>
	 * 
	 * @return Hibernate <code>Interceptor</code> object.
	 * @see #createHibernateSession(boolean)
	 */
	protected Interceptor getHibernateSessionInterceptor()
	{
		return null;
	}

	/**
	 * Derived classes must override this method to provide the base
	 * implementation with a <code>Configuration</code> object. This method is
	 * called during the <code>open</code> processing.
	 * <p>
	 * This method is called from <code>open</code>, which is synchronized.
	 * Thus, overrides of this method are not required to implement their own
	 * thread safety.
	 * </p>
	 * 
	 * @return Hibernate <code>Configuration</code> object
	 * @throws HibernateException
	 */
	protected abstract Configuration createConfiguration()
			throws HibernateException;

	/**
	 * Create a new <code>DatabaseContext</code> associated with this
	 * <p>
	 * This method is thread-safe.
	 * </p>
	 * 
	 * @return <code>DatabaseContext</code> object
	 */
	@Override
	public DatabaseContext createDatabaseContext()
	{
		return new DatabaseContextImpl(this);
	}

	/**
	 * Overrideable method that returns the minimum amount of time during which
	 * <code>createVerifiedSession</could> should repeat trying to get a session.  
	 * The default implementation returns 30000, meaning 30 seconds.
	 * <p>
	 * This method is called indirectly from createHibernateSession, which
	 * is synchronized.  Thus, overrides of this method do not need to implement
	 * their own thread safety.
	 * </p>
	 * 
	 * @return Timeout period (in milliseconds).
	 */
	protected long getSessionVerificationTimeout()
	{
		return 30L * 1000L;
	}

	/**
	 * Overrideable method that returns the text of an SQL query that is to be
	 * used to test a session during <code>createVerifiedSession</code>. The
	 * returned string is passed to <code>createSQLQuery</code>, not
	 * <code>createQuery</code>, and thus should be SQL, not HQL. A typical
	 * implementation will return something like
	 * 
	 * <pre>
	 * select 1 from SomeTable
	 * </pre>
	 * <p>
	 * This method is called indirectly from createHibernateSession, which is
	 * synchronized. Thus, overrides of this method do not need to implement
	 * their own thread safety.
	 * </p>
	 * 
	 * @return HQL query string
	 */
	protected abstract String getVerificationSQL();

	/**
	 * Create a new Hibernate <code>Session</code> from the internal
	 * <code>SessionFactory</code>, and increment the corresponding count of
	 * outstanding sessions.
	 * <p>
	 * This method is only <code>protected</code> (as opposed to
	 * <code>private</code>) in order to allow unit testing of the
	 * <code>getVerifiedSession</code> retry logic in mock implementations. It
	 * should not be overridden by normal implementations.
	 * </p>
	 * 
	 * @return Hibernate <code>Session</code>
	 * @throws HibernateException
	 */
	protected synchronized Session getRawSession() throws HibernateException
	{
		Assert.isTrue(sessionFactory != null,
				"createHibernateSession on database that isn't open");

		Session session = sessionFactory
				.openSession(getHibernateSessionInterceptor());

		outstandingSessions++;

		return session;
	}

	/**
	 * Return a verified session, retrying as required. The returned
	 * <code>Session</code> will have successfully executed the SQL command
	 * provided by <code>getVerificationSQL</code>.
	 * <p>
	 * This method is called indirectly from createHibernateSession, which is
	 * synchronized. Thus, overrides of this method do not need to implement
	 * their own thread safety.
	 * </p>
	 * 
	 * @return <code>Session</code> object
	 * @throws HibernateException
	 *             If a verified connection can't be obtained before the
	 *             verification timeout.
	 * @see #getVerificationSQL()
	 * @see #getSessionVerificationTimeout()
	 */
	protected Session getVerifiedSession() throws HibernateException
	{
		long tryUntil = System.currentTimeMillis()
				+ getSessionVerificationTimeout();

		Session session = null;

		for (;;)
		{
			try
			{
				session = getRawSession();

				session.createSQLQuery(getVerificationSQL()).list();

				break;
			}
			catch (HibernateException e)
			{
				if (session != null)
				{
					closeHibernateSession(session);
				}

				if (System.currentTimeMillis() > tryUntil)
				{
					throw e;
				}
			}
		}

		return session;
	}
}
