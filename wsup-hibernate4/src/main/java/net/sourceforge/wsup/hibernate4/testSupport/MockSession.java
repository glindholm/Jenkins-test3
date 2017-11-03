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

import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.LobHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.SharedSessionBuilder;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.hibernate.Transaction;
import org.hibernate.TypeHelper;
import org.hibernate.UnknownProfileException;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.hibernate.stat.SessionStatistics;

/**
 * This is a test class that can be used to "wrap" a real Hibernate <code>Session</code> so that
 * exceptions can be thrown when required for
 * testing. Among other things, if asked for a transaction, it will wrap that
 * transaction in a <code>MockTransaction</code>.
 * 
 * @author Kevin Hunter
 * @see MockingDatabaseInterceptor
 * @see MockTransaction
 * 
 */
public class MockSession implements Session
{
    private static final long                serialVersionUID = -634207486403343032L;

    private final MockingDatabaseInterceptor interceptor;
    private final Session                    realSession;
    private Map<Transaction, Transaction>    transactions     = new HashMap<Transaction, Transaction>();

    public MockSession(MockingDatabaseInterceptor interceptor, Session realSession)
    {
        this.interceptor = interceptor;
        this.realSession = realSession;
    }

    public Session getRealSession()
    {
        return realSession;
    }

    @Override
    public Transaction beginTransaction() throws HibernateException
    {
        if (interceptor.getBeginTransactionException() != null)
        {
            throw interceptor.getBeginTransactionException();
        }

        Transaction realTransaction = realSession.beginTransaction();
        Transaction mockTransaction = interceptor.wrapTransaction(realTransaction);
        transactions.put(realTransaction, mockTransaction);

        return mockTransaction;
    }

    @Override
    public Connection close() throws HibernateException
    {
        if (interceptor.getCloseSessionException() != null)
        {
            throw interceptor.getCloseSessionException();
        }

        return realSession.close();
    }

    @Override
    public Transaction getTransaction()
    {
        Transaction realTransaction = realSession.getTransaction();
        if (realTransaction == null)
        {
            return null;
        }

        return transactions.get(realTransaction);
    }

    @Override
    public LockRequest buildLockRequest(LockOptions lockOptions)
    {
        return realSession.buildLockRequest(lockOptions);
    }

    @Override
    public void cancelQuery() throws HibernateException
    {
        realSession.cancelQuery();
    }

    @Override
    public void clear()
    {
        realSession.clear();
    }

    @Override
    public boolean contains(Object object)
    {
        return realSession.contains(object);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Criteria createCriteria(Class persistentClass)
    {
        return realSession.createCriteria(persistentClass);
    }

    @Override
    public Criteria createCriteria(String entityName)
    {
        return realSession.createCriteria(entityName);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Criteria createCriteria(Class persistentClass, String alias)
    {
        return realSession.createCriteria(persistentClass, alias);
    }

    @Override
    public Criteria createCriteria(String entityName, String alias)
    {
        return realSession.createCriteria(entityName, alias);
    }

    @Override
    public Query createFilter(Object collection, String queryString) throws HibernateException
    {
        return realSession.createFilter(collection, queryString);
    }

    @Override
    public Query createQuery(String queryString) throws HibernateException
    {
        return realSession.createQuery(queryString);
    }

    @Override
    public SQLQuery createSQLQuery(String queryString) throws HibernateException
    {
        return realSession.createSQLQuery(queryString);
    }

    @Override
    public void delete(Object object) throws HibernateException
    {
        realSession.delete(object);
    }

    @Override
    public void delete(String entityName, Object object) throws HibernateException
    {
        realSession.delete(entityName, object);
    }

    @Override
    public void disableFetchProfile(String name) throws UnknownProfileException
    {
        realSession.disableFetchProfile(name);
    }

    @Override
    public void disableFilter(String filterName)
    {
        realSession.disableFilter(filterName);
    }

    @Override
    public Connection disconnect() throws HibernateException
    {
        return realSession.disconnect();
    }

    @Override
    public void doWork(Work work) throws HibernateException
    {
        realSession.doWork(work);
    }

    @Override
    public void enableFetchProfile(String name) throws UnknownProfileException
    {
        realSession.enableFetchProfile(name);

    }

    @Override
    public Filter enableFilter(String filterName)
    {
        return realSession.enableFilter(filterName);
    }

    @Override
    public void evict(Object object) throws HibernateException
    {
        realSession.evict(object);
    }

    @Override
    public void flush() throws HibernateException
    {
        realSession.flush();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object get(Class clazz, Serializable id) throws HibernateException
    {
        return realSession.get(clazz, id);
    }

    @Override
    public Object get(String entityName, Serializable id) throws HibernateException
    {
        return realSession.get(entityName, id);
    }

    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Override
    public Object get(Class clazz, Serializable id, LockMode lockMode) throws HibernateException
    {
        return realSession.get(clazz, id, lockMode);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object get(Class clazz, Serializable id, LockOptions lockOptions)
        throws HibernateException
    {
        return realSession.get(clazz, id, lockOptions);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object get(String entityName, Serializable id, LockMode lockMode)
        throws HibernateException
    {
        return realSession.get(entityName, id, lockMode);
    }

    @Override
    public Object get(String entityName, Serializable id, LockOptions lockOptions)
        throws HibernateException
    {
        return realSession.get(entityName, id, lockOptions);
    }

    @Override
    public CacheMode getCacheMode()
    {
        return realSession.getCacheMode();
    }

    @Override
    public LockMode getCurrentLockMode(Object object) throws HibernateException
    {
        return realSession.getCurrentLockMode(object);
    }

    @Override
    public Filter getEnabledFilter(String filterName)
    {
        return realSession.getEnabledFilter(filterName);
    }

    @Override
    public String getEntityName(Object object) throws HibernateException
    {
        return realSession.getEntityName(object);
    }

    @Override
    public FlushMode getFlushMode()
    {
        return realSession.getFlushMode();
    }

    @Override
    public Serializable getIdentifier(Object object) throws HibernateException
    {
        return realSession.getIdentifier(object);
    }

    @Override
    public Query getNamedQuery(String queryName) throws HibernateException
    {
        return realSession.getNamedQuery(queryName);
    }

    @Override
    public SessionFactory getSessionFactory()
    {
        return realSession.getSessionFactory();
    }

    @Override
    public SessionStatistics getStatistics()
    {
        return realSession.getStatistics();
    }

    @Override
    public boolean isConnected()
    {
        return realSession.isConnected();
    }

    @Override
    public boolean isDefaultReadOnly()
    {
        return realSession.isDefaultReadOnly();
    }

    @Override
    public boolean isDirty() throws HibernateException
    {
        return realSession.isDirty();
    }

    @Override
    public boolean isFetchProfileEnabled(String name) throws UnknownProfileException
    {
        return realSession.isFetchProfileEnabled(name);
    }

    @Override
    public boolean isOpen()
    {
        return realSession.isOpen();
    }

    @Override
    public boolean isReadOnly(Object entityOrProxy)
    {
        return realSession.isReadOnly(entityOrProxy);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object load(Class theClass, Serializable id) throws HibernateException
    {
        return realSession.load(theClass, id);
    }

    @Override
    public Object load(String entityName, Serializable id) throws HibernateException
    {
        return realSession.load(entityName, id);
    }

    @Override
    public void load(Object object, Serializable id) throws HibernateException
    {
        realSession.load(object, id);
    }

    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Override
    public Object load(Class theClass, Serializable id, LockMode lockMode)
        throws HibernateException
    {
        return realSession.load(theClass, id, lockMode);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object load(Class theClass, Serializable id, LockOptions lockOptions)
        throws HibernateException
    {
        return realSession.load(theClass, id, lockOptions);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object load(String entityName, Serializable id, LockMode lockMode)
        throws HibernateException
    {
        return realSession.load(entityName, id, lockMode);
    }

    @Override
    public Object load(String entityName, Serializable id, LockOptions lockOptions)
        throws HibernateException
    {
        return realSession.load(entityName, id, lockOptions);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void lock(Object object, LockMode lockMode) throws HibernateException
    {
        realSession.lock(object, lockMode);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void lock(String entityName, Object object, LockMode lockMode) throws HibernateException
    {
        realSession.lock(entityName, object, lockMode);
    }

    @Override
    public Object merge(Object object) throws HibernateException
    {
        return realSession.merge(object);
    }

    @Override
    public Object merge(String entityName, Object object) throws HibernateException
    {
        return realSession.merge(entityName, object);
    }

    @Override
    public void persist(Object object) throws HibernateException
    {
        realSession.persist(object);
    }

    @Override
    public void persist(String entityName, Object object) throws HibernateException
    {
        realSession.persist(entityName, object);

    }

    @Override
    public void reconnect(Connection connection) throws HibernateException
    {
        realSession.reconnect(connection);

    }

    @Override
    public void refresh(Object object) throws HibernateException
    {
        realSession.refresh(object);

    }

    @SuppressWarnings("deprecation")
    @Override
    public void refresh(Object object, LockMode lockMode) throws HibernateException
    {
        realSession.refresh(object, lockMode);

    }

    @Override
    public void refresh(Object object, LockOptions lockOptions) throws HibernateException
    {
        realSession.refresh(object, lockOptions);

    }

    @Override
    public void replicate(Object object, ReplicationMode replicationMode) throws HibernateException
    {
        realSession.replicate(object, replicationMode);

    }

    @Override
    public void replicate(String entityName, Object object, ReplicationMode replicationMode)
        throws HibernateException
    {
        realSession.replicate(entityName, object, replicationMode);

    }

    @Override
    public Serializable save(Object object) throws HibernateException
    {
        return realSession.save(object);
    }

    @Override
    public Serializable save(String entityName, Object object) throws HibernateException
    {
        return realSession.save(entityName, object);
    }

    @Override
    public void saveOrUpdate(Object object) throws HibernateException
    {
        realSession.saveOrUpdate(object);

    }

    @Override
    public void saveOrUpdate(String entityName, Object object) throws HibernateException
    {
        realSession.saveOrUpdate(entityName, object);

    }

    @Override
    public void setCacheMode(CacheMode cacheMode)
    {
        realSession.setCacheMode(cacheMode);

    }

    @Override
    public void setDefaultReadOnly(boolean readOnly)
    {
        realSession.setDefaultReadOnly(readOnly);

    }

    @Override
    public void setFlushMode(FlushMode flushMode)
    {
        realSession.setFlushMode(flushMode);

    }

    @Override
    public void setReadOnly(Object entityOrProxy, boolean readOnly)
    {
        realSession.setReadOnly(entityOrProxy, readOnly);

    }

    @Override
    public void update(Object object) throws HibernateException
    {
        realSession.update(object);

    }

    @Override
    public void update(String entityName, Object object) throws HibernateException
    {
        realSession.update(entityName, object);

    }

    @Override
    public String getTenantIdentifier()
    {
        return realSession.getTenantIdentifier();
    }

    @Override
    public IdentifierLoadAccess byId(String id)
    {
        return realSession.byId(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public IdentifierLoadAccess byId(Class clazz)
    {
        return realSession.byId(clazz);
    }

    @Override
    public NaturalIdLoadAccess byNaturalId(String id)
    {
        return realSession.byNaturalId(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public NaturalIdLoadAccess byNaturalId(Class clazz)
    {
        return realSession.byNaturalId(clazz);
    }

    @Override
    public SimpleNaturalIdLoadAccess bySimpleNaturalId(String id)
    {
        return realSession.bySimpleNaturalId(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public SimpleNaturalIdLoadAccess bySimpleNaturalId(Class clazz)
    {
        return realSession.bySimpleNaturalId(clazz);
    }

    @Override
    public <T> T doReturningWork(ReturningWork<T> work) throws HibernateException
    {
        return realSession.doReturningWork(work);
    }

    @Override
    public LobHelper getLobHelper()
    {
        return realSession.getLobHelper();
    }

    @Override
    public TypeHelper getTypeHelper()
    {
        return realSession.getTypeHelper();
    }

    @Override
    public void refresh(String arg0, Object arg1)
    {
    }

    @Override
    public void refresh(String arg0, Object arg1, LockOptions arg2)
    {
    }

    @Override
    public SharedSessionBuilder sessionWithOptions()
    {
        return realSession.sessionWithOptions();
    }

}
