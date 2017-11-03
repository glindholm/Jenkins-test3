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

package net.sourceforge.wsup.hibernate.impl;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;

import net.sourceforge.wsup.hibernate.BaseDatabase;
import net.sourceforge.wsup.hibernate.DataAccessContext;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.UnknownProfileException;
import org.hibernate.Session.LockRequest;
import org.hibernate.jdbc.Work;
import org.hibernate.stat.SessionStatistics;

/**
 * Implementation of the <code>DataAccessContext</code> interface that is
 * produced by the <code>DatabaseContextImpl</code> implementation. The
 * effective "lifetime" of this object is bounded by the underlying Hibernate
 * <code>Session</code>.
 * 
 * @author Kevin Hunter
 * @see DataAccessContext
 * @see DatabaseContextImpl#createDataAccessContext()
 */
public class DataAccessContextImpl implements DataAccessContext
{
	private final DatabaseContextImpl databaseContext;

	/**
	 * Constructor.
	 * 
	 * @param databaseContext
	 *            <code>DatabaseContextImpl</code> object identifying the
	 *            database to which this object is connected.
	 * @see DatabaseContextImpl
	 */
	public DataAccessContextImpl(DatabaseContextImpl databaseContext)
	{
		this.databaseContext = databaseContext;
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#buildLockRequest(org.hibernate.LockOptions)
	 */
	@Override
	public LockRequest buildLockRequest(LockOptions lockOptions)
	{
		return databaseContext.getSafeSession().buildLockRequest(lockOptions);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#cancelQuery()
	 */
	@Override
	public void cancelQuery() throws HibernateException
	{
		databaseContext.getSafeSession().cancelQuery();
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object object)
	{
		return databaseContext.getSafeSession().contains(object);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#createCriteria(java.lang.Class)
	 */
	@Override
	public Criteria createCriteria(Class<?> persistentClass)
	{
		return databaseContext.getSafeSession().createCriteria(persistentClass);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#createCriteria(java.lang.String)
	 */
	@Override
	public Criteria createCriteria(String entityName)
	{
		return databaseContext.getSafeSession().createCriteria(entityName);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#createCriteria(java.lang.Class,
	 *      java.lang.String)
	 */
	@Override
	public Criteria createCriteria(Class<?> persistentClass, String alias)
	{
		return databaseContext.getSafeSession().createCriteria(persistentClass,
				alias);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#createCriteria(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Criteria createCriteria(String entityName, String alias)
	{
		return databaseContext.getSafeSession().createCriteria(entityName,
				alias);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#createFilter(java.lang.Object,
	 *      java.lang.String)
	 */
	@Override
	public Query createFilter(Object collection, String queryString)
			throws HibernateException
	{
		return databaseContext.getSafeSession().createFilter(collection,
				queryString);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#createQuery(java.lang.String)
	 */
	@Override
	public Query createQuery(String queryString) throws HibernateException
	{
		return databaseContext.getSafeSession().createQuery(queryString);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#createSQLQuery(java.lang.String)
	 */
	@Override
	public SQLQuery createSQLQuery(String queryString)
			throws HibernateException
	{
		return databaseContext.getSafeSession().createSQLQuery(queryString);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#delete(java.lang.Object)
	 */
	@Override
	public void delete(Object object) throws HibernateException
	{
		databaseContext.getSafeSession().delete(object);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#delete(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void delete(String entityName, Object object)
			throws HibernateException
	{
		databaseContext.getSafeSession().delete(entityName, object);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#disableFetchProfile(java.lang.String)
	 */
	@Override
	public void disableFetchProfile(String name) throws UnknownProfileException
	{
		databaseContext.getSafeSession().disableFetchProfile(name);

	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#disableFilter(java.lang.String)
	 */
	@Override
	public void disableFilter(String filterName)
	{
		databaseContext.getSafeSession().disableFilter(filterName);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#doWork(org.hibernate.jdbc.Work)
	 */
	@Override
	public void doWork(Work work) throws HibernateException
	{
		databaseContext.getSafeSession().doWork(work);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#enableFetchProfile(java.lang.String)
	 */
	@Override
	public void enableFetchProfile(String name) throws UnknownProfileException
	{
		databaseContext.getSafeSession().enableFetchProfile(name);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#enableFilter(java.lang.String)
	 */
	@Override
	public Filter enableFilter(String filterName)
	{
		return databaseContext.getSafeSession().enableFilter(filterName);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#evict(java.lang.Object)
	 */
	@Override
	public void evict(Object object) throws HibernateException
	{
		databaseContext.getSafeSession().evict(object);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#flush()
	 */
	@Override
	public void flush() throws HibernateException
	{
		databaseContext.getSafeSession().flush();
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#get(java.lang.Class,
	 *      java.io.Serializable)
	 */
	@Override
	public Object get(Class<?> clazz, Serializable id)
			throws HibernateException
	{
		return databaseContext.getSafeSession().get(clazz, id);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#get(java.lang.String,
	 *      java.io.Serializable)
	 */
	@Override
	public Object get(String entityName, Serializable id)
	{
		return databaseContext.getSafeSession().get(entityName, id);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#get(java.lang.Class,
	 *      java.io.Serializable, org.hibernate.LockOptions)
	 */
	@Override
	public Object get(Class<?> clazz, Serializable id, LockOptions lockOptions)
			throws HibernateException
	{
		return databaseContext.getSafeSession().get(clazz, id, lockOptions);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#get(java.lang.String,
	 *      java.io.Serializable, org.hibernate.LockOptions)
	 */
	@Override
	public Object get(String entityName, Serializable id,
			LockOptions lockOptions) throws HibernateException
	{
		return databaseContext.getSafeSession()
				.get(entityName, id, lockOptions);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#getCacheMode()
	 */
	@Override
	public CacheMode getCacheMode()
	{
		return databaseContext.getSafeSession().getCacheMode();
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#getCurrentLockMode(java.lang.Object)
	 */
	@Override
	public LockMode getCurrentLockMode(Object object) throws HibernateException
	{
		return databaseContext.getSafeSession().getCurrentLockMode(object);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#getEnabledFilter(java.lang.String)
	 */
	@Override
	public Filter getEnabledFilter(String filterName)
	{
		return databaseContext.getSafeSession().getEnabledFilter(filterName);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#getEntityMode()
	 */
	@Override
	public EntityMode getEntityMode()
	{
		return databaseContext.getSafeSession().getEntityMode();
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#getEntityName(java.lang.Object)
	 */
	@Override
	public String getEntityName(Object object) throws HibernateException
	{
		return databaseContext.getSafeSession().getEntityName(object);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#getFlushMode()
	 */
	@Override
	public FlushMode getFlushMode()
	{
		return databaseContext.getSafeSession().getFlushMode();
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#getIdentifier(java.lang.Object)
	 */
	@Override
	public Serializable getIdentifier(Object object) throws HibernateException
	{
		return databaseContext.getSafeSession().getIdentifier(object);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#getNamedQuery(java.lang.String)
	 */
	@Override
	public Query getNamedQuery(String queryName) throws HibernateException
	{
		return databaseContext.getSafeSession().getNamedQuery(queryName);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#getStatistics()
	 */
	@Override
	public SessionStatistics getStatistics()
	{
		return databaseContext.getSafeSession().getStatistics();
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#isDefaultReadOnly()
	 */
	@Override
	public boolean isDefaultReadOnly()
	{
		return databaseContext.getSafeSession().isDefaultReadOnly();
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#isDirty()
	 */
	@Override
	public boolean isDirty() throws HibernateException
	{
		return databaseContext.getSafeSession().isDirty();
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#isFetchProfileEnabled(java.lang.String)
	 */
	@Override
	public boolean isFetchProfileEnabled(String name)
			throws UnknownProfileException
	{
		return databaseContext.getSafeSession().isFetchProfileEnabled(name);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#isReadOnly(java.lang.Object)
	 */
	@Override
	public boolean isReadOnly(Object entityOrProxy)
	{
		return databaseContext.getSafeSession().isReadOnly(entityOrProxy);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#load(java.lang.Class,
	 *      java.io.Serializable)
	 */
	@Override
	public Object load(Class<?> theClass, Serializable id)
			throws HibernateException
	{
		return databaseContext.getSafeSession().load(theClass, id);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#load(java.lang.String,
	 *      java.io.Serializable)
	 */
	@Override
	public Object load(String entityName, Serializable id)
			throws HibernateException
	{
		return databaseContext.getSafeSession().load(entityName, id);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#load(java.lang.Object,
	 *      java.io.Serializable)
	 */
	@Override
	public void load(Object object, Serializable id) throws HibernateException
	{
		databaseContext.getSafeSession().load(object, id);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#load(java.lang.Class,
	 *      java.io.Serializable, org.hibernate.LockOptions)
	 */
	@Override
	public Object load(Class<?> theClass, Serializable id,
			LockOptions lockOptions) throws HibernateException
	{
		return databaseContext.getSafeSession().load(theClass, id, lockOptions);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#load(java.lang.String,
	 *      java.io.Serializable, org.hibernate.LockOptions)
	 */
	@Override
	public Object load(String entityName, Serializable id,
			LockOptions lockOptions) throws HibernateException
	{
		return databaseContext.getSafeSession().load(entityName, id,
				lockOptions);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#merge(java.lang.Object)
	 */
	@Override
	public Object merge(Object object) throws HibernateException
	{
		return databaseContext.getSafeSession().merge(object);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#merge(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public Object merge(String entityName, Object object)
			throws HibernateException
	{
		return databaseContext.getSafeSession().merge(entityName, object);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#persist(java.lang.Object)
	 */
	@Override
	public void persist(Object object) throws HibernateException
	{
		databaseContext.getSafeSession().persist(object);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#persist(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void persist(String entityName, Object object)
			throws HibernateException
	{
		databaseContext.getSafeSession().persist(entityName, object);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#refresh(java.lang.Object)
	 */
	@Override
	public void refresh(Object object) throws HibernateException
	{
		databaseContext.getSafeSession().refresh(object);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#refresh(java.lang.Object,
	 *      org.hibernate.LockOptions)
	 */
	@Override
	public void refresh(Object object, LockOptions lockOptions)
			throws HibernateException
	{
		databaseContext.getSafeSession().refresh(object, lockOptions);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#replicate(java.lang.Object,
	 *      org.hibernate.ReplicationMode)
	 */
	@Override
	public void replicate(Object object, ReplicationMode replicationMode)
			throws HibernateException
	{
		databaseContext.getSafeSession().replicate(object, replicationMode);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#replicate(java.lang.String,
	 *      java.lang.Object, org.hibernate.ReplicationMode)
	 */
	@Override
	public void replicate(String entityName, Object object,
			ReplicationMode replicationMode) throws HibernateException
	{
		databaseContext.getSafeSession().replicate(entityName, object,
				replicationMode);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#save(java.lang.Object)
	 */
	@Override
	public Serializable save(Object object) throws HibernateException
	{
		return databaseContext.getSafeSession().save(object);
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.DataAccessContext#save(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public Serializable save(String entityName, Object object)
			throws HibernateException
	{
		return databaseContext.getSafeSession().save(entityName, object);
	}

	/**
	 * @see DataAccessContext#saveOrUpdate(java.lang.Object)
	 */
	@Override
	public void saveOrUpdate(Object object) throws HibernateException
	{
		databaseContext.getSafeSession().saveOrUpdate(object);
	}

	/**
	 * @see DataAccessContext#saveOrUpdate(java.lang.String, java.lang.Object)
	 */
	@Override
	public void saveOrUpdate(String entityName, Object object)
			throws HibernateException
	{
		databaseContext.getSafeSession().saveOrUpdate(entityName, object);
	}

	/**
	 * @see DataAccessContext#setCacheMode(org.hibernate.CacheMode)
	 */
	@Override
	public void setCacheMode(CacheMode cacheMode)
	{
		databaseContext.getSafeSession().setCacheMode(cacheMode);
	}

	/**
	 * @see DataAccessContext#setDefaultReadOnly(boolean)
	 */
	@Override
	public void setDefaultReadOnly(boolean readOnly)
	{
		databaseContext.getSafeSession().setDefaultReadOnly(readOnly);
	}

	/**
	 * @see DataAccessContext#setFlushMode(org.hibernate.FlushMode)
	 */
	@Override
	public void setFlushMode(FlushMode flushMode)
	{
		databaseContext.getSafeSession().setFlushMode(flushMode);
	}

	/**
	 * @see DataAccessContext#setReadOnly(java.lang.Object, boolean)
	 */
	@Override
	public void setReadOnly(Object entityOrProxy, boolean readOnly)
	{
		databaseContext.getSafeSession().setReadOnly(entityOrProxy, readOnly);
	}

	/**
	 * @see DataAccessContext#update(java.lang.Object)
	 */
	@Override
	public void update(Object object) throws HibernateException
	{
		databaseContext.getSafeSession().update(object);
	}

	/**
	 * @see DataAccessContext#update(java.lang.String, java.lang.Object)
	 */
	@Override
	public void update(String entityName, Object object)
			throws HibernateException
	{
		databaseContext.getSafeSession().update(entityName, object);
	}

	/**
	 * @see DataAccessContext#getTableNameForClass(java.lang.Class)
	 */
	@Override
	public String getTableNameForClass(Class<?> persistentClass)
			throws HibernateException
	{
		return getDatabase().getConfiguration().getClassMapping(
				persistentClass.getName()).getTable().getName();
	}

	/**
	 * @see DataAccessContext#createBlob(byte[])
	 */
	@Override
	public Blob createBlob(byte[] bytes)
	{
		return databaseContext.createBlob(bytes);
	}

	/**
	 * @see DataAccessContext#createBlob(java.io.InputStream, long)
	 */
	@Override
	public Blob createBlob(InputStream stream, long length)
	{
		return databaseContext.createBlob(stream, length);
	}

	/**
	 * @see DataAccessContext#createClob(java.lang.String)
	 */
	@Override
	public Clob createClob(String string)
	{
		return databaseContext.createClob(string);
	}

	/**
	 * @see DataAccessContext#createClob(java.io.Reader, long)
	 */
	@Override
	public Clob createClob(Reader reader, long length)
	{
		return databaseContext.createClob(reader, length);
	}

	/**
	 * Get the <code>BaseDatabase</code> object with which this object is
	 * associated. At present, this is <u>not</u> part of the
	 * <code>DataAccessContext</code> public interface, but is included here for
	 * use within the class and for testing purposes. (The latter is why it is
	 * <code>public</code>.)
	 * 
	 * @return <code>BaseDatabase</code>
	 */
	public BaseDatabase getDatabase()
	{
		return databaseContext.getDatabase();
	}

	/**
	 * Get the underlying Hibernate <code>Session</code> from the
	 * <code>DatabaseContext</code>. This is deliberately <u>not</u> part of the
	 * <code>DataAccessContext</code> public interface, but is included here for
	 * testing purposes.
	 * 
	 * @return Hibernate <code>Session</code> object
	 */
	public Session getSession()
	{
		return databaseContext.getSession();
	}
}
