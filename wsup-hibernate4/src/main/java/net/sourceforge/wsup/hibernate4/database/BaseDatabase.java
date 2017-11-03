/*
 * Copyright 2012 Kevin Hunter
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sourceforge.wsup.hibernate4.database;

import net.sourceforge.wsup.core.Assert;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class serves as a base for Hibernate databases. It provides thread-safe
 * methods for opening and closing the database and creating, returning and
 * counting Hibernate <code>Session</code>s.
 * <p>
 * The primary thing that derived classes need to do is to provide configuration information and
 * possibly modify the <code>SessionRegistryBuilder</code> to install any services that are to be
 * used.
 * </p>
 * <p>
 * Most of the methods on this class are threadsafe - they are marked. Those which are are
 * synchronized on the <code>BaseDatabase</code> object.
 * </p>
 * 
 * @author Kevin Hunter
 * 
 */
public abstract class BaseDatabase implements DatabaseContextProvider
{
    private static final Logger              log = LoggerFactory.getLogger(BaseDatabase.class);

    /*
     * Hibernate SessionFactory. Accesses to this variable must be thread-safe,
     * and synchronized on "this".
     */
    private volatile SessionFactory          sessionFactory;

    /*
     * Count of number of outstanding Hibernate Sessions. Accesses to this
     * variable must be thread-safe, and synchronized on "this".
     */
    private volatile int                     outstandingSessions;

    /*
     * Hibernate Configuration. Accesses to this variable must be thread-safe,
     * and synchronized on "this".
     */
    private volatile Configuration           configuration;

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
     *            <code>DatabaseTestInterceptor</code> instance or <code>null</code>.
     */
    public synchronized void setDatabaseTestInterceptor(DatabaseTestInterceptor databaseTestInterceptor)
    {
        this.databaseTestInterceptor = databaseTestInterceptor;
    }

    /**
     * Get the current database test interceptor.
     * 
     * @return <code>DatabaseTestInterceptor</code> instance or <code>null</code>.
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
        if (isOpen())
        {
            log.info("Opening database when already open");
            return;
        }

        if (databaseTestInterceptor != null)
        {
            databaseTestInterceptor.interceptOpenDatabase(this);
        }

        configuration = createConfiguration();
        Interceptor sessionInterceptor = getHibernateSessionInterceptor();
        if (sessionInterceptor != null)
        {
            configuration.setInterceptor(sessionInterceptor);
        }
        ServiceRegistry serviceRegistry = createServiceRegistry(configuration);
        outstandingSessions = 0;
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    /**
     * "Close" the database. Once the database is closed, no further <code>Session</code>s can be
     * obtained from it.
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
        if (!isOpen())
        {
            log.info("Closing database when already closed");
            return;
        }

        if (databaseTestInterceptor != null)
        {
            databaseTestInterceptor.interceptCloseDatabase(this);
        }

        if (outstandingSessions != 0)
        {
            log.warn("Closing database with {} outstanding sessions", outstandingSessions);
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
     * Do not alter the <code>Configuration</code> object while the database is open.
     * </p>
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
     * Create and return a Hibernate <code>Session</code> object. <code>Session</code>s created
     * through this call should be closed by
     * passing them to {@link #closeHibernateSession(Session)} so that the count
     * of outstanding <code>Session</code>s is kept correct.
     * <p>
     * Note that, despite not being synchronized itself, this method is thread-safe due to its
     * underlying implementation.
     * </p>
     * 
     * @param verify
     *            If <code>true</code>, the code uses logic designed to ensure
     *            that the <code>Session</code> returned is actually live. If <code>false</code>,
     *            the code simply asks the <code>SessionManager</code> for a new
     *            <code>Session</code>,
     *            which can result
     * @return Hibernate <code>Session</code> object
     * @throws HibernateException
     * @see #closeHibernateSession(Session)
     */

    public Session createHibernateSession(boolean verify) throws HibernateException
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
     * Close a <code>Session</code> previously retrieved from <code>createHibernateSession</code>.
     * Closing a session this way keeps the
     * count of outstanding <code>Session</code>s correct.
     * <p>
     * This method is thread-safe.
     * </p>
     * 
     * @param session
     *            Hibernate <code>Session</code> object previously retrieved
     *            from <code>createHibernateSession</code>.
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
     * Derived classes may override this method to provide the base
     * implementation with a <code>Configuration</code> object. This method is
     * called during the <code>open</code> processing. The default
     * implementation:
     * <ol>
     * <li>
     * Creates a <code>Configuration</code> object</li>
     * <li>calls <code>configure</code> on it</li>
     * <li>Calls <code>getHibernateSessionInterceptor</code> and, if that returns an
     * <code>Interceptor</code>, sets that <code>Interceptor</code> into the
     * <code>Configuration</code>.
     * </ol>
     * <p>
     * Thus, for example, a derived class could call this method, then alter the properties on the
     * <code>Configuration</code> and then return that object. Or it could totally replace this
     * implementation and return its own configuration.
     * <p>
     * This method is called from <code>open</code>, which is synchronized. Thus, overrides of this
     * method are not required to implement their own thread safety.
     * </p>
     * 
     * @return Hibernate <code>Configuration</code> object
     * @throws HibernateException
     */
    protected Configuration createConfiguration() throws HibernateException
    {
        Configuration configuration = new Configuration();
        configuration.configure();
        return configuration;
    }

    /**
     * Derived classes may override this function to alter the way the <code>ServiceRegistry</code>
     * is built. The default implementation:
     * <ol>
     * <li>Constructs a <code>ServiceRegistryBuilder</code></li>
     * <li>Applies the properties from the <code>Configuration</code></li>
     * <li>Calls <code>configureServiceRegistryBuilder</code> to provide the derived class an
     * opportunity to do additional configuration on the <code>ServiceRegistryBuilder</code></li>
     * <li>Uses the <code>ServiceRegistryBuilder</code> to build a <code>ServiceRegistry</code>.</li>
     * </ol>
     * <p>
     * The resulting <code>ServiceRegistry</code> will be used to build the
     * <code>SessionFactory</code>.
     * </p>
     * 
     * @param configuration <code>Configuration</code> returned from
     *            <code>createConfiguration</code>.
     * @return Instance of <code>ServiceRegistry</code> that will be used to
     *         create the <code>SessionFactory</code>.
     * @throws HibernateException
     */
    protected ServiceRegistry createServiceRegistry(Configuration configuration)
        throws HibernateException
    {
        ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilder();
        serviceRegistryBuilder.applySettings(configuration.getProperties());
        configureServiceRegistryBuilder(serviceRegistryBuilder);
        return serviceRegistryBuilder.buildServiceRegistry();
    }

    /**
     * Derived classes may override this method in order to do configuration on
     * the <code>ServiceRegistryBuilder</code> before it is used to build the
     * <code>ServiceRegistry</code> (and thence the <code>SessionFactory</code> ).
     * <p>
     * The default implementation does nothing.
     * </p>
     * 
     * @param serviceRegistryBuilder <code>ServiceRegistryBuilder</code> to be
     *            configured.
     * @see #createServiceRegistry(Configuration)
     */
    protected void configureServiceRegistryBuilder(ServiceRegistryBuilder serviceRegistryBuilder)
    {
    }

    /**
     * Create a new <code>DatabaseContext</code> associated with this database.
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
     * returned string is passed to <code>createSQLQuery</code>, not <code>createQuery</code>, and
     * thus should be SQL, not HQL. A typical
     * implementation will return something like
     * 
     * <pre>
	 * select 1 from SomeTable
	 * </pre>
     * <p>
     * This method is called indirectly from createHibernateSession, which is synchronized. Thus,
     * overrides of this method do not need to implement their own thread safety.
     * </p>
     * <p>
     * The default implementation returns <code>null</code>. If <code>null</code> is returned,
     * <code>getVerifiedSession</code> will simply return a raw session without verification.
     * </p>
     * 
     * @return HQL query string
     */
    protected String getVerificationSQL()
    {
        return null;
    }

    /**
     * Create a new Hibernate <code>Session</code> from the internal <code>SessionFactory</code>,
     * and increment the corresponding count of
     * outstanding sessions.
     * <p>
     * This method is only <code>protected</code> (as opposed to <code>private</code>) in order to
     * allow unit testing of the <code>getVerifiedSession</code> retry logic in mock
     * implementations. It should not be overridden by normal implementations.
     * </p>
     * 
     * @return Hibernate <code>Session</code>
     * @throws HibernateException
     */
    protected synchronized Session getRawSession() throws HibernateException
    {
        Assert.isTrue(sessionFactory != null, "createHibernateSession on database that isn't open");

        Session session = sessionFactory.openSession();

        outstandingSessions++;

        return session;
    }

    /**
     * Derived classes may override this method in order to provide an <code>Interceptor</code> that
     * will be installed into the <code>Configuration</code> as the database is opened,
     * and thus will result in the <code>Interceptor</code> being installed into
     * all <code>Session</code>s that are created. The default implementation
     * returns <code>null</code>.
     * <p>
     * This method is called from <code>open</code>, which is indirectly synchronized. Thus,
     * overrides of this method are not required to implement their own thread safety.
     * </p>
     * 
     * @return Hibernate <code>Interceptor</code> object.
     * @see #createConfiguration()
     */
    protected Interceptor getHibernateSessionInterceptor()
    {
        return null;
    }

    /**
     * Return a verified session, retrying as required. The returned <code>Session</code> will have
     * successfully executed the SQL command
     * provided by <code>getVerificationSQL</code>.
     * <p>
     * This method is called indirectly from createHibernateSession, which is synchronized. Thus,
     * overrides of this method do not need to implement their own thread safety.
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
        String verificationSQL = getVerificationSQL();
        if (verificationSQL == null)
        {
            return getRawSession();
        }

        long tryUntil = System.currentTimeMillis() + getSessionVerificationTimeout();

        Session session = null;

        for (;;)
        {
            try
            {
                session = getRawSession();

                session.createSQLQuery(verificationSQL).list();

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
