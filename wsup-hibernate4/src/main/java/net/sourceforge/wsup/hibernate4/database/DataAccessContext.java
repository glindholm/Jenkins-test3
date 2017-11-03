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
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session.LockRequest;
import org.hibernate.TransientObjectException;
import org.hibernate.UnknownProfileException;
import org.hibernate.jdbc.Work;
import org.hibernate.stat.SessionStatistics;

/**
 * This interface provides the query-related methods of a Hibernate <code>Session</code> to a
 * <code>BaseDAO</code>-derived object.
 * 
 * @author Kevin Hunter
 * 
 */
public interface DataAccessContext
{
    /**
     * Force this session to flush. Must be called at the end of a unit of work,
     * before committing the transaction and closing the session.
     * <p/>
     * <i>Flushing</i> is the process of synchronizing the underlying persistent store with
     * persistable state held in memory.
     * 
     * @throws HibernateException
     *             Indicates problems flushing the session or talking to the
     *             database.
     */
    public void flush() throws HibernateException;

    /**
     * Set the flush mode for this session.
     * <p/>
     * The flush mode determines the points at which the session is flushed. <i>Flushing</i> is the
     * process of synchronizing the underlying persistent store with persistable state held in
     * memory.
     * <p/>
     * For a logically "read only" session, it is reasonable to set the session's flush mode to
     * {@link FlushMode#MANUAL} at the start of the session (in order to achieve some extra
     * performance).
     * 
     * @param flushMode
     *            the new flush mode
     */
    public void setFlushMode(FlushMode flushMode);

    /**
     * Get the current flush mode for this session.
     * 
     * @return The flush mode
     */
    public FlushMode getFlushMode();

    /**
     * Set the cache mode.
     * <p/>
     * Cache mode determines the manner in which this session can interact with the second level
     * cache.
     * 
     * @param cacheMode
     *            The new cache mode.
     */
    public void setCacheMode(CacheMode cacheMode);

    /**
     * Get the current cache mode.
     * 
     * @return The current cache mode.
     */
    public CacheMode getCacheMode();

    /**
     * Cancel the execution of the current query.
     * <p/>
     * This is the sole method on session which may be safely called from another thread.
     * 
     * @throws HibernateException
     *             There was a problem canceling the query
     */
    public void cancelQuery() throws HibernateException;

    /**
     * Does this session contain any changes which must be synchronized with the
     * database? In other words, would any DML operations be executed if we
     * flushed this session?
     * 
     * @return True if the session contains pending changes; false otherwise.
     * @throws HibernateException
     *             could not perform dirtying checking
     */
    public boolean isDirty() throws HibernateException;

    /**
     * Will entities and proxies that are loaded into this session be made
     * read-only by default?
     * 
     * To determine the read-only/modifiable setting for a particular entity or
     * proxy:
     * 
     * @return true, loaded entities/proxies will be made read-only by default;
     *         false, loaded entities/proxies will be made modifiable by
     *         default.
     */
    public boolean isDefaultReadOnly();

    /**
     * Change the default for entities and proxies loaded into this session from
     * modifiable to read-only mode, or from modifiable to read-only mode.
     * 
     * Read-only entities are not dirty-checked and snapshots of persistent
     * state are not maintained. Read-only entities can be modified, but changes
     * are not persisted.
     * 
     * When a proxy is initialized, the loaded entity will have the same
     * read-only/modifiable setting as the uninitialized proxy has, regardless
     * of the session's current setting.
     * 
     * Will not change the read-only/modifiable setting for a particular entity or
     * proxy that is already in this session. To do that, you must use the <code>Session</code>
     * <code>setReadOnly</code> method.
     * 
     * The <code>Query</code> <code>setReadOnly</code> method can be used to override this session's
     * read-only/modifiable setting for entities
     * and proxies loaded by a <code>Query</code>.
     * 
     * @param readOnly
     *            true, the default for loaded entities/proxies is read-only;
     *            false, the default for loaded entities/proxies is modifiable
     */
    public void setDefaultReadOnly(boolean readOnly);

    /**
     * Return the identifier value of the given entity as associated with this
     * session. An exception is thrown if the given entity instance is transient
     * or detached in relation to this session.
     * 
     * @param object
     *            a persistent instance
     * @return the identifier
     * @throws TransientObjectException
     *             if the instance is transient or associated with a different
     *             session
     */
    public Serializable getIdentifier(Object object) throws HibernateException;

    /**
     * Check if this instance is associated with this <tt>Session</tt>.
     * 
     * @param object
     *            an instance of a persistent class
     * @return true if the given instance is associated with this <tt>Session</tt>
     */
    public boolean contains(Object object);

    /**
     * Remove this instance from the session cache. Changes to the instance will
     * not be synchronized with the database. This operation cascades to
     * associated instances if the association is mapped with <tt>cascade="evict"</tt>.
     * 
     * @param object
     *            a persistent instance
     * @throws HibernateException
     */
    public void evict(Object object) throws HibernateException;

    /**
     * Return the persistent instance of the given entity class with the given
     * identifier, obtaining the specified lock mode, assuming the instance
     * exists.
     * 
     * @param theClass
     *            a persistent class
     * @param id
     *            a valid identifier of an existing persistent instance of the
     *            class
     * @param lockOptions
     *            contains the lock level
     * @return the persistent instance or proxy
     * @throws HibernateException
     */
    public Object load(Class<?> theClass, Serializable id, LockOptions lockOptions)
        throws HibernateException;

    /**
     * Return the persistent instance of the given entity class with the given
     * identifier, obtaining the specified lock mode, assuming the instance
     * exists.
     * 
     * @param entityName
     *            a persistent class
     * @param id
     *            a valid identifier of an existing persistent instance of the
     *            class
     * @param lockOptions
     *            contains the lock level
     * @return the persistent instance or proxy
     * @throws HibernateException
     */
    public Object load(String entityName, Serializable id, LockOptions lockOptions)
        throws HibernateException;

    /**
     * Return the persistent instance of the given entity class with the given
     * identifier, assuming that the instance exists. This method might return a
     * proxied instance that is initialized on-demand, when a non-identifier
     * method is accessed. <br>
     * <br>
     * You should not use this method to determine if an instance exists (use <tt>get()</tt>
     * instead). Use this only to retrieve an instance that you
     * assume exists, where non-existence would be an actual error.
     * 
     * @param theClass
     *            a persistent class
     * @param id
     *            a valid identifier of an existing persistent instance of the
     *            class
     * @return the persistent instance or proxy
     * @throws HibernateException
     */
    public Object load(Class<?> theClass, Serializable id) throws HibernateException;

    /**
     * Return the persistent instance of the given entity class with the given
     * identifier, assuming that the instance exists. This method might return a
     * proxied instance that is initialized on-demand, when a non-identifier
     * method is accessed. <br>
     * <br>
     * You should not use this method to determine if an instance exists (use <tt>get()</tt>
     * instead). Use this only to retrieve an instance that you
     * assume exists, where non-existence would be an actual error.
     * 
     * @param entityName
     *            a persistent class
     * @param id
     *            a valid identifier of an existing persistent instance of the
     *            class
     * @return the persistent instance or proxy
     * @throws HibernateException
     */
    public Object load(String entityName, Serializable id) throws HibernateException;

    /**
     * Read the persistent state associated with the given identifier into the
     * given transient instance.
     * 
     * @param object
     *            an "empty" instance of the persistent class
     * @param id
     *            a valid identifier of an existing persistent instance of the
     *            class
     * @throws HibernateException
     */
    public void load(Object object, Serializable id) throws HibernateException;

    /**
     * Persist the state of the given detached instance, reusing the current
     * identifier value. This operation cascades to associated instances if the
     * association is mapped with <tt>cascade="replicate"</tt>.
     * 
     * @param object
     *            a detached instance of a persistent class
     */
    public void replicate(Object object, ReplicationMode replicationMode) throws HibernateException;

    /**
     * Persist the state of the given detached instance, reusing the current
     * identifier value. This operation cascades to associated instances if the
     * association is mapped with <tt>cascade="replicate"</tt>.
     * 
     * @param object
     *            a detached instance of a persistent class
     */
    public void replicate(String entityName, Object object, ReplicationMode replicationMode)
        throws HibernateException;

    /**
     * Persist the given transient instance, first assigning a generated
     * identifier. (Or using the current value of the identifier property if the <tt>assigned</tt>
     * generator is used.) This operation cascades to
     * associated instances if the association is mapped with <tt>cascade="save-update"</tt>.
     * 
     * @param object
     *            a transient instance of a persistent class
     * @return the generated identifier
     * @throws HibernateException
     */
    public Serializable save(Object object) throws HibernateException;

    /**
     * Persist the given transient instance, first assigning a generated
     * identifier. (Or using the current value of the identifier property if the <tt>assigned</tt>
     * generator is used.) This operation cascades to
     * associated instances if the association is mapped with <tt>cascade="save-update"</tt>.
     * 
     * @param object
     *            a transient instance of a persistent class
     * @return the generated identifier
     * @throws HibernateException
     */
    public Serializable save(String entityName, Object object) throws HibernateException;

    /**
     * Either {@link #save(Object)} or {@link #update(Object)} the given
     * instance, depending upon resolution of the unsaved-value checks (see the
     * manual for discussion of unsaved-value checking).
     * <p/>
     * This operation cascades to associated instances if the association is mapped with
     * <tt>cascade="save-update"</tt>.
     * 
     * @param object
     *            a transient or detached instance containing new or updated
     *            state
     * @throws HibernateException
     */
    public void saveOrUpdate(Object object) throws HibernateException;

    /**
     * Either {@link #save(String, Object)} or {@link #update(String, Object)} the given instance,
     * depending upon resolution of the unsaved-value checks
     * (see the manual for discussion of unsaved-value checking).
     * <p/>
     * This operation cascades to associated instances if the association is mapped with
     * <tt>cascade="save-update"</tt>.
     * 
     * @param object
     *            a transient or detached instance containing new or updated
     *            state
     * @throws HibernateException
     */
    public void saveOrUpdate(String entityName, Object object) throws HibernateException;

    /**
     * Update the persistent instance with the identifier of the given detached
     * instance. If there is a persistent instance with the same identifier, an
     * exception is thrown. This operation cascades to associated instances if
     * the association is mapped with <tt>cascade="save-update"</tt>.
     * 
     * @param object
     *            a detached instance containing updated state
     * @throws HibernateException
     */
    public void update(Object object) throws HibernateException;

    /**
     * Update the persistent instance with the identifier of the given detached
     * instance. If there is a persistent instance with the same identifier, an
     * exception is thrown. This operation cascades to associated instances if
     * the association is mapped with <tt>cascade="save-update"</tt>.
     * 
     * @param object
     *            a detached instance containing updated state
     * @throws HibernateException
     */
    public void update(String entityName, Object object) throws HibernateException;

    /**
     * Copy the state of the given object onto the persistent object with the
     * same identifier. If there is no persistent instance currently associated
     * with the session, it will be loaded. Return the persistent instance. If
     * the given instance is unsaved, save a copy of and return it as a newly
     * persistent instance. The given instance does not become associated with
     * the session. This operation cascades to associated instances if the
     * association is mapped with <tt>cascade="merge"</tt>.<br>
     * <br>
     * The semantics of this method are defined by JSR-220.
     * 
     * @param object
     *            a detached instance with state to be copied
     * @return an updated persistent instance
     */
    public Object merge(Object object) throws HibernateException;

    /**
     * Copy the state of the given object onto the persistent object with the
     * same identifier. If there is no persistent instance currently associated
     * with the session, it will be loaded. Return the persistent instance. If
     * the given instance is unsaved, save a copy of and return it as a newly
     * persistent instance. The given instance does not become associated with
     * the session. This operation cascades to associated instances if the
     * association is mapped with <tt>cascade="merge"</tt>.<br>
     * <br>
     * The semantics of this method are defined by JSR-220.
     * 
     * @param object
     *            a detached instance with state to be copied
     * @return an updated persistent instance
     */
    public Object merge(String entityName, Object object) throws HibernateException;

    /**
     * Make a transient instance persistent. This operation cascades to
     * associated instances if the association is mapped with <tt>cascade="persist"</tt>.<br>
     * <br>
     * The semantics of this method are defined by JSR-220.
     * 
     * @param object
     *            a transient instance to be made persistent
     */
    public void persist(Object object) throws HibernateException;

    /**
     * Make a transient instance persistent. This operation cascades to
     * associated instances if the association is mapped with <tt>cascade="persist"</tt>.<br>
     * <br>
     * The semantics of this method are defined by JSR-220.
     * 
     * @param object
     *            a transient instance to be made persistent
     */
    public void persist(String entityName, Object object) throws HibernateException;

    /**
     * Remove a persistent instance from the datastore. The argument may be an
     * instance associated with the receiving <tt>Session</tt> or a transient
     * instance with an identifier associated with existing persistent state.
     * This operation cascades to associated instances if the association is
     * mapped with <tt>cascade="delete"</tt>.
     * 
     * @param object
     *            the instance to be removed
     * @throws HibernateException
     */
    public void delete(Object object) throws HibernateException;

    /**
     * Remove a persistent instance from the datastore. The <b>object</b>
     * argument may be an instance associated with the receiving <tt>Session</tt> or a transient
     * instance with an identifier associated
     * with existing persistent state. This operation cascades to associated
     * instances if the association is mapped with <tt>cascade="delete"</tt>.
     * 
     * @param entityName
     *            The entity name for the instance to be removed.
     * @param object
     *            the instance to be removed
     * @throws HibernateException
     */
    public void delete(String entityName, Object object) throws HibernateException;

    /**
     * Build a LockRequest that specifies the LockMode, pessimistic lock timeout
     * and lock scope. timeout and scope is ignored for optimistic locking.
     * After building the LockRequest, call LockRequest.lock to perform the
     * requested locking.
     * 
     * Use: session.buildLockRequest().
     * setLockMode(LockMode.PESSIMISTIC_WRITE).setTimeOut(1000 *
     * 60).lock(entity);
     * 
     * @param lockOptions
     *            contains the lock level
     * @return a lockRequest that can be used to lock the passed object.
     * @throws HibernateException
     */
    public LockRequest buildLockRequest(LockOptions lockOptions);

    /**
     * Re-read the state of the given instance from the underlying database. It
     * is inadvisable to use this to implement long-running sessions that span
     * many business tasks. This method is, however, useful in certain special
     * circumstances. For example
     * <ul>
     * <li>where a database trigger alters the object state upon insert or update
     * <li>after executing direct SQL (eg. a mass update) in the same session
     * <li>after inserting a <tt>Blob</tt> or <tt>Clob</tt>
     * </ul>
     * 
     * @param object
     *            a persistent or detached instance
     * @throws HibernateException
     */
    public void refresh(Object object) throws HibernateException;

    /**
     * Re-read the state of the given instance from the underlying database,
     * with the given <tt>LockMode</tt>. It is inadvisable to use this to
     * implement long-running sessions that span many business tasks. This
     * method is, however, useful in certain special circumstances.
     * 
     * @param object
     *            a persistent or detached instance
     * @param lockOptions
     *            contains the lock mode to use
     * @throws HibernateException
     */
    public void refresh(Object object, LockOptions lockOptions) throws HibernateException;

    /**
     * Determine the current lock mode of the given object.
     * 
     * @param object
     *            a persistent instance
     * @return the current lock mode
     * @throws HibernateException
     */
    public LockMode getCurrentLockMode(Object object) throws HibernateException;

    /**
     * Create a new <tt>Criteria</tt> instance, for the given entity class, or a
     * superclass of an entity class.
     * 
     * @param persistentClass
     *            a class, which is persistent, or has persistent subclasses
     * @return Criteria
     */
    public Criteria createCriteria(Class<?> persistentClass);

    /**
     * Create a new <tt>Criteria</tt> instance, for the given entity class, or a
     * superclass of an entity class, with the given alias.
     * 
     * @param persistentClass
     *            a class, which is persistent, or has persistent subclasses
     * @return Criteria
     */
    public Criteria createCriteria(Class<?> persistentClass, String alias);

    /**
     * Create a new <tt>Criteria</tt> instance, for the given entity name.
     * 
     * @param entityName
     * @return Criteria
     */
    public Criteria createCriteria(String entityName);

    /**
     * Create a new <tt>Criteria</tt> instance, for the given entity name, with
     * the given alias.
     * 
     * @param entityName
     * @return Criteria
     */
    public Criteria createCriteria(String entityName, String alias);

    /**
     * Create a new instance of <tt>Query</tt> for the given HQL query string.
     * 
     * @param queryString
     *            a HQL query
     * @return Query
     * @throws HibernateException
     */
    public Query createQuery(String queryString) throws HibernateException;

    /**
     * Create a new instance of <tt>SQLQuery</tt> for the given SQL query
     * string.
     * 
     * @param queryString
     *            a SQL query
     * @return SQLQuery
     * @throws HibernateException
     */
    public SQLQuery createSQLQuery(String queryString) throws HibernateException;

    /**
     * Create a new instance of <tt>Query</tt> for the given collection and
     * filter string.
     * 
     * @param collection
     *            a persistent collection
     * @param queryString
     *            a Hibernate query
     * @return Query
     * @throws HibernateException
     */
    public Query createFilter(Object collection, String queryString) throws HibernateException;

    /**
     * Obtain an instance of <tt>Query</tt> for a named query string defined in
     * the mapping file.
     * 
     * @param queryName
     *            the name of a query defined externally
     * @return Query
     * @throws HibernateException
     */
    public Query getNamedQuery(String queryName) throws HibernateException;

    /**
     * Return the persistent instance of the given entity class with the given
     * identifier, or null if there is no such persistent instance. (If the
     * instance is already associated with the session, return that instance.
     * This method never returns an uninitialized instance.)
     * 
     * @param clazz
     *            a persistent class
     * @param id
     *            an identifier
     * @return a persistent instance or null
     * @throws HibernateException
     */
    public Object get(Class<?> clazz, Serializable id) throws HibernateException;

    /**
     * Return the persistent instance of the given entity class with the given
     * identifier, or null if there is no such persistent instance. (If the
     * instance is already associated with the session, return that instance.
     * This method never returns an uninitialized instance.) Obtain the
     * specified lock mode if the instance exists.
     * 
     * @param clazz
     *            a persistent class
     * @param id
     *            an identifier
     * @param lockOptions
     *            the lock mode
     * @return a persistent instance or null
     * @throws HibernateException
     */
    public Object get(Class<?> clazz, Serializable id, LockOptions lockOptions)
        throws HibernateException;

    /**
     * Return the persistent instance of the given named entity with the given
     * identifier, or null if there is no such persistent instance. (If the
     * instance is already associated with the session, return that instance.
     * This method never returns an uninitialized instance.)
     * 
     * @param entityName
     *            the entity name
     * @param id
     *            an identifier
     * @return a persistent instance or null
     * @throws HibernateException
     */
    public Object get(String entityName, Serializable id) throws HibernateException;

    /**
     * Return the persistent instance of the given entity class with the given
     * identifier, or null if there is no such persistent instance. (If the
     * instance is already associated with the session, return that instance.
     * This method never returns an uninitialized instance.) Obtain the
     * specified lock mode if the instance exists.
     * 
     * @param entityName
     *            the entity name
     * @param id
     *            an identifier
     * @param lockOptions
     *            contains the lock mode
     * @return a persistent instance or null
     * @throws HibernateException
     */
    public Object get(String entityName, Serializable id, LockOptions lockOptions)
        throws HibernateException;

    /**
     * Return the entity name for a persistent entity
     * 
     * @param object
     *            a persistent entity
     * @return the entity name
     * @throws HibernateException
     */
    public String getEntityName(Object object) throws HibernateException;

    /**
     * Enable the named filter for this current session.
     * 
     * @param filterName
     *            The name of the filter to be enabled.
     * @return The Filter instance representing the enabled filter.
     */
    public Filter enableFilter(String filterName);

    /**
     * Retrieve a currently enabled filter by name.
     * 
     * @param filterName
     *            The name of the filter to be retrieved.
     * @return The Filter instance representing the enabled filter.
     */
    public Filter getEnabledFilter(String filterName);

    /**
     * Disable the named filter for the current session.
     * 
     * @param filterName
     *            The name of the filter to be disabled.
     */
    public void disableFilter(String filterName);

    /**
     * Get the statistics for this session.
     */
    public SessionStatistics getStatistics();

    /**
     * Is the specified entity or proxy read-only?
     * 
     * To get the default read-only/modifiable setting used for entities and
     * proxies that are loaded into the session:
     * 
     * @param entityOrProxy
     *            an entity or HibernateProxy
     * @return <code>true</code> if the entity or proxy is read-only; <code>false</code> if the
     *         entity or
     *         proxy is modifiable.
     */
    public boolean isReadOnly(Object entityOrProxy);

    /**
     * Set an unmodified persistent object to read-only mode, or a read-only
     * object to modifiable mode. In read-only mode, no snapshot is maintained,
     * the instance is never dirty checked, and changes are not persisted.
     * 
     * If the entity or proxy already has the specified read-only/modifiable
     * setting, then this method does nothing.
     * 
     * To set the default read-only/modifiable setting used for entities and
     * proxies that are loaded into the session:
     * 
     * @see org.hibernate.Session#setDefaultReadOnly(boolean)
     * 
     *      To override this session's read-only/modifiable setting for entities
     *      and proxies loaded by a Query:
     * @see org.hibernate.Query#setReadOnly(boolean)
     * 
     * @param entityOrProxy
     *            , an entity or HibernateProxy
     * @param readOnly
     *            , if true, the entity or proxy is made read-only; if false,
     *            the entity or proxy is made modifiable.
     */
    public void setReadOnly(Object entityOrProxy, boolean readOnly);

    /**
     * Controller for allowing users to perform JDBC related work using the
     * Connection managed by this Session.
     * 
     * @param work
     *            The work to be performed.
     * @throws HibernateException
     *             Generally indicates wrapped {@link java.sql.SQLException}
     */
    public void doWork(Work work) throws HibernateException;

    /**
     * Is a particular fetch profile enabled on this session?
     * 
     * @param name
     *            The name of the profile to be checked.
     * @return True if fetch profile is enabled; false if not.
     * @throws UnknownProfileException
     *             Indicates that the given name does not match any known
     *             profile names
     */
    public boolean isFetchProfileEnabled(String name) throws UnknownProfileException;

    /**
     * Enable a particular fetch profile on this session. No-op if requested
     * profile is already enabled.
     * 
     * @param name
     *            The name of the fetch profile to be enabled.
     * @throws UnknownProfileException
     *             Indicates that the given name does not match any known
     *             profile names
     */
    public void enableFetchProfile(String name) throws UnknownProfileException;

    /**
     * Disable a particular fetch profile on this session. No-op if requested
     * profile is already disabled.
     * 
     * @param name
     *            The name of the fetch profile to be disabled.
     * @throws UnknownProfileException
     *             Indicates that the given name does not match any known
     *             profile names
     */
    public void disableFetchProfile(String name) throws UnknownProfileException;

    /**
     * Return the name of the database table associated with a particular
     * persistent class.
     * 
     * @param persistentClass
     *            Class to be looked up.
     * @return String containing the table name.
     * @throws HibernateException
     */
    public String getTableNameForClass(Class<?> persistentClass) throws HibernateException;

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
