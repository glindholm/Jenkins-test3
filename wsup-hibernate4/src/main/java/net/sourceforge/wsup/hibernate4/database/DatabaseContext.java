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

package net.sourceforge.wsup.hibernate4.database;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;

import org.hibernate.HibernateException;

/**
 * This interface abstracts and manages the session and transaction elements of
 * a Hibernate <code>Session</code>.
 * <p>
 * Objects that implement this interface are not required to be thread-safe.
 * </p>
 * 
 * @author Kevin Hunter
 * 
 */
public interface DatabaseContext
{
    /**
     * Begin a new <code>Session</code>. This method does nothing if a <code>Session</code> has
     * already been begun.
     * 
     * @param preverify
     *            If <code>true</code>, the <code>Session</code> is verified as
     *            part of its creation process. This ensures that the underlying
     *            JDBC connection is valid. This has a minor performance impact
     *            in the normal case (i.e. when the database is up and running
     *            properly), but can be useful following an exception that
     *            suggests that the database connection was lost. If <code>false</code>, the
     *            <code>Session</code> is created but
     *            not validated before being returned. This will typically be
     *            fine, but can potentially result in a <code>Session</code> being returned with a
     *            dead JDBC connection if connections time
     *            out, the database failed over, etc.
     * @throws HibernateException
     *             if a new session cannot be obtained from the database.
     */
    public void beginSession(boolean preverify) throws HibernateException;

    /**
     * Begin a new database transaction. This method does nothing if a
     * transaction is already underway. If called before <code>beginSession</code>, this method will
     * automatically begin a new
     * session as if <code>beginSession(false)</code> were called.
     * 
     * @throws HibernateException
     */
    public void beginTransaction() throws HibernateException;

    /**
     * Commit an outstanding transaction. If the commit is successful, the
     * method will return normally, and the current session will be left open.
     * If the commit fails, it will throw an exception, and the current session
     * will be closed.
     * <p>
     * This method will assert if there is not a transaction outstanding.
     * </p>
     * 
     * @throws HibernateException
     */
    public void commitTransaction() throws HibernateException;

    /**
     * Roll back the outstanding transaction. This will also have the side
     * effect of closing the current session, since the validity of the current
     * session cannot be guaranteed under these circumstances.
     * <p>
     * This method will assert if there is not a transaction outstanding. For a "safe" means of
     * rolling back a possibly-outstanding transaction and closing a session, use
     * {@link #destroySession()}.
     * </p>
     * 
     * @throws HibernateException
     */
    public void rollbackTransaction() throws HibernateException;

    /**
     * Close the current session. This method is benign if there is no session
     * currently open, but will assert if a session is open and there is an
     * outstanding transaction.
     * <p>
     * For a "safe" means of rolling back a possibly-outstanding transaction and closing a session,
     * use {@link #destroySession()}.
     * </p>
     * 
     * @throws HibernateException
     */
    public void closeSession() throws HibernateException;

    /**
     * Rollback any outstanding transaction and close the current session.
     * <p>
     * Unlike {@link #rollbackTransaction()}, this method is benign if there is no outstanding
     * transaction. It is also benign if there is no outstanding session.
     * </p>
     * <p>
     * This method will "eat" any <code>HibernateException</code> that is thrown by the transaction
     * as it is rolled back, or by the session as it is closed. It is therefore a safe way of
     * abandoning any database activity that might have been outstanding. (The exceptions will be
     * logged at the "info" level.)
     * </p>
     */
    public void destroySession();

    /**
     * Is there currently a session open?
     * 
     * @return <code>true</code> if so, <code>false</code> if not.
     * @see #beginSession(boolean)
     * @see #closeSession()
     * @see #destroySession()
     */
    public boolean isSessionOpen();

    /**
     * Is there currently a transaction open?
     * 
     * @return <code>true</code> if so, <code>false</code> if not.
     * @see #beginTransaction()
     * @see #commitTransaction()
     * @see #rollbackTransaction()
     */
    public boolean isInTransaction();

    /**
     * Create a <code>DataAccessContext</code> object associated with the
     * current session. If called before <code>beginSession</code>, this method
     * will automatically begin a new session as if <code>beginSession(false)</code> were called.
     * 
     * @return <code>DataAccessContext</code> object
     * @see #beginSession(boolean)
     */
    public DataAccessContext createDataAccessContext() throws HibernateException;

    /**
     * Get the database object with which this object is associated.
     * 
     * @return <code>BaseDatabase</code> object.
     * @see BaseDatabase
     */
    public BaseDatabase getDatabase();

    /**
     * Create a <code>Blob</code> associated with the current session. If called
     * before <code>beginSession</code>, this method will automatically begin a
     * new session as if <code>beginSession(false)</code> were called.
     * 
     * @param bytes Contents of the <code>Blob</code>
     * @return <code>Blob</code> object.
     */
    public Blob createBlob(byte[] bytes);

    /**
     * Create a <code>Blob</code> associated with the current session. If called
     * before <code>beginSession</code>, this method will automatically begin a
     * new session as if <code>beginSession(false)</code> were called.
     * 
     * @param stream <code>InputStream</code> containing the data.
     * @param length Number of bytes in the stream
     * @return <code>Blob</code> object.
     */
    public Blob createBlob(InputStream stream, long length);

    /**
     * Create a <code>Clob</code> associated with the current session. If called
     * before <code>beginSession</code>, this method will automatically begin a
     * new session as if <code>beginSession(false)</code> were called.
     * 
     * @param string Contents of the <code>Clob</code>
     * @return <code>Clob</code> object.
     */
    public Clob createClob(String string);

    /**
     * Create a <code>Clob</code> associated with the current session. If called
     * before <code>beginSession</code>, this method will automatically begin a
     * new session as if <code>beginSession(false)</code> were called.
     * 
     * @param reader <code>Reader</code> containing the data.
     * @param length Number of characters in the input
     * @return <code>Clob</code> object.
     */
    public Clob createClob(Reader reader, long length);
}
