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

package net.sourceforge.wsup.hibernate4.interceptors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.jcip.annotations.ThreadSafe;

import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

/**
 * Hibernate only supports a single <code>Interceptor</code> on each <code>Session</code>. This
 * interceptor implements a "fan-out" to
 * a list of interceptors, thus essentially allowing multiple interceptors
 * to be installed into a Hibernate <code>Session</code>.
 * <p>
 * This interceptor is thread-safe, so a single instance of it can be used across multiple
 * <code>Session</code>s, providing that all of the interceptors that it holds are also thread-safe.
 * </p>
 */
@ThreadSafe
public class MultiplexInterceptor implements Interceptor
{
    private final Interceptor[] interceptors;

    /**
     * Constructs a <code>MultiplexInterceptor</code> that will
     * call each of the provided interceptors.
     * 
     * @param interceptors
     */
    public MultiplexInterceptor(Interceptor... interceptors)
    {
        super();
        this.interceptors = interceptors;
    }

    /**
     * @see org.hibernate.Interceptor#afterTransactionBegin(org.hibernate.Transaction)
     */
    @Override
    public void afterTransactionBegin(Transaction tx)
    {
        for (Interceptor interceptor : interceptors)
        {
            interceptor.afterTransactionBegin(tx);
        }
    }

    /**
     * @see org.hibernate.Interceptor#afterTransactionCompletion(org.hibernate.Transaction)
     */
    @Override
    public void afterTransactionCompletion(Transaction tx)
    {
        for (Interceptor interceptor : interceptors)
        {
            interceptor.afterTransactionCompletion(tx);
        }
    }

    /**
     * @see org.hibernate.Interceptor#beforeTransactionCompletion(org.hibernate.Transaction)
     */
    @Override
    public void beforeTransactionCompletion(Transaction tx)
    {
        for (Interceptor interceptor : interceptors)
        {
            interceptor.beforeTransactionCompletion(tx);
        }
    }

    /**
     * @see org.hibernate.Interceptor#findDirty(java.lang.Object, java.io.Serializable,
     *      java.lang.Object[], java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
     */
    @Override
    public int[] findDirty(Object entity,
                           Serializable id,
                           Object[] currentState,
                           Object[] previousState,
                           String[] propertyNames,
                           Type[] types)
    {

        for (Interceptor interceptor : interceptors)
        {
            int[] result = interceptor.findDirty(entity,
                                                 id,
                                                 currentState,
                                                 previousState,
                                                 propertyNames,
                                                 types);
            // return the first non-null result
            if (result != null)
            {
                return result;
            }
        }

        return null;
    }

    /**
     * @see org.hibernate.Interceptor#getEntity(java.lang.String, java.io.Serializable)
     */
    @Override
    public Object getEntity(String entityName, Serializable id) throws CallbackException
    {
        for (Interceptor interceptor : interceptors)
        {
            Object result = interceptor.getEntity(entityName, id);

            // return the first non-null result
            if (result != null)
            {
                return result;
            }
        }

        return null;
    }

    /**
     * @see org.hibernate.Interceptor#getEntityName(java.lang.Object)
     */
    @Override
    public String getEntityName(Object object) throws CallbackException
    {
        for (Interceptor interceptor : interceptors)
        {
            String result = interceptor.getEntityName(object);

            // return the first non-null result
            if (result != null)
            {
                return result;
            }
        }

        return null;
    }

    /**
     * @see org.hibernate.Interceptor#instantiate(java.lang.String, org.hibernate.EntityMode,
     *      java.io.Serializable)
     */
    @Override
    public Object instantiate(String entityName, EntityMode entityMode, Serializable id)
        throws CallbackException
    {
        for (Interceptor interceptor : interceptors)
        {
            Object result = interceptor.instantiate(entityName, entityMode, id);

            // return the first non-null result
            if (result != null)
            {
                return result;
            }
        }

        return null;
    }

    /**
     * @see org.hibernate.Interceptor#isTransient(java.lang.Object)
     */
    @Override
    public Boolean isTransient(Object entity)
    {
        for (Interceptor interceptor : interceptors)
        {
            Boolean result = interceptor.isTransient(entity);

            // return the first non-null result
            if (result != null)
            {
                return result;
            }
        }

        return null;
    }

    /**
     * @see org.hibernate.Interceptor#onCollectionRecreate(java.lang.Object, java.io.Serializable)
     */
    @Override
    public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException
    {
        for (Interceptor interceptor : interceptors)
        {
            interceptor.onCollectionRecreate(collection, key);
        }
    }

    /**
     * @see org.hibernate.Interceptor#onCollectionRemove(java.lang.Object, java.io.Serializable)
     */
    @Override
    public void onCollectionRemove(Object collection, Serializable key) throws CallbackException
    {
        for (Interceptor interceptor : interceptors)
        {
            interceptor.onCollectionRemove(collection, key);
        }
    }

    /**
     * @see org.hibernate.Interceptor#onCollectionUpdate(java.lang.Object, java.io.Serializable)
     */
    @Override
    public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException
    {
        for (Interceptor interceptor : interceptors)
        {
            interceptor.onCollectionUpdate(collection, key);
        }
    }

    /**
     * @see org.hibernate.Interceptor#onDelete(java.lang.Object, java.io.Serializable,
     *      java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
     */
    @Override
    public void onDelete(Object entity,
                         Serializable id,
                         Object[] state,
                         String[] propertyNames,
                         Type[] types) throws CallbackException
    {
        for (Interceptor interceptor : interceptors)
        {
            interceptor.onDelete(entity, id, state, propertyNames, types);
        }
    }

    /**
     * @see org.hibernate.Interceptor#onFlushDirty(java.lang.Object, java.io.Serializable,
     *      java.lang.Object[], java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
     */
    @Override
    public boolean onFlushDirty(Object entity,
                                Serializable id,
                                Object[] currentState,
                                Object[] previousState,
                                String[] propertyNames,
                                Type[] types) throws CallbackException
    {
        boolean result = false;
        for (Interceptor interceptor : interceptors)
        {
            // return true if any of the interceptors modified the state
            result |= interceptor.onFlushDirty(entity,
                                               id,
                                               currentState,
                                               previousState,
                                               propertyNames,
                                               types);
        }

        return result;
    }

    /**
     * @see org.hibernate.Interceptor#onLoad(java.lang.Object, java.io.Serializable,
     *      java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
     */
    @Override
    public boolean onLoad(Object entity,
                          Serializable id,
                          Object[] state,
                          String[] propertyNames,
                          Type[] types) throws CallbackException
    {
        boolean result = false;
        for (Interceptor interceptor : interceptors)
        {
            // return true if any of the interceptors modified the state
            result |= interceptor.onLoad(entity, id, state, propertyNames, types);
        }

        return result;
    }

    /**
     * @see org.hibernate.Interceptor#onPrepareStatement(java.lang.String)
     */
    @Override
    public String onPrepareStatement(String sql)
    {
        String result = sql;
        for (Interceptor interceptor : interceptors)
        {
            // give each interceptor a chance to modify the sql
            result = interceptor.onPrepareStatement(result);
        }

        return result;
    }

    /**
     * @see org.hibernate.Interceptor#onSave(java.lang.Object, java.io.Serializable,
     *      java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
     */
    @Override
    public boolean onSave(Object entity,
                          Serializable id,
                          Object[] state,
                          String[] propertyNames,
                          Type[] types) throws CallbackException
    {
        boolean result = false;
        for (Interceptor interceptor : interceptors)
        {
            // return true if any of the interceptors modified the state
            result |= interceptor.onSave(entity, id, state, propertyNames, types);
        }

        return result;
    }

    /**
     * @see org.hibernate.Interceptor#postFlush(java.util.Iterator)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void postFlush(Iterator entities) throws CallbackException
    {
        List<Object> entityList = createList(entities);
        for (Interceptor interceptor : interceptors)
        {
            interceptor.postFlush(entityList.iterator());
        }
    }

    /**
     * @see org.hibernate.Interceptor#preFlush(java.util.Iterator)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void preFlush(Iterator entities) throws CallbackException
    {
        List<Object> entityList = createList(entities);
        for (Interceptor interceptor : interceptors)
        {
            interceptor.preFlush(entityList.iterator());
        }
    }

    /**
     * Creates and returns a new <code>List</code> containing all the elements
     * returned from the <code>Iterator</code>.
     * 
     * @param iterator The iterator.
     * @return A <code>List</code> of the iterator's elements.
     */
    private List<Object> createList(Iterator<?> iterator)
    {
        List<Object> list = new ArrayList<Object>();
        while (iterator.hasNext())
        {
            list.add(iterator.next());
        }

        return list;
    }
}
