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

package net.sourceforge.wsup.hibernate.interceptors;

import java.io.Serializable;

import net.jcip.annotations.ThreadSafe;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;

/**
 * A Hibernate <code>Interceptor</code> that enforce READ-ONLY database mode
 * by throwing a {@link ReadOnlyException} if any attempt is made to change the
 * database.
 * <p>
 * This interceptor is thread-safe, so a single instance of it can be
 * used across multiple <code>Session</code>s.
 * </p>
 * 
 */
@ThreadSafe
public class ReadOnlyInterceptor extends EmptyInterceptor implements Interceptor
{
    private static final long serialVersionUID = -1359145715466348135L;

    /**
     * @see org.hibernate.EmptyInterceptor#onDelete(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
     */
    @Override
    public void onDelete(Object entity,
                         Serializable id,
                         Object[] state,
                         String[] propertyNames,
                         Type[] types)
    {
        throw new ReadOnlyException("Attempt to Delete " + entity);
    }

    /**
     * @see org.hibernate.EmptyInterceptor#onFlushDirty(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
     */
    @Override
    public boolean onFlushDirty(Object entity,
                                Serializable id,
                                Object[] currentState,
                                Object[] previousState,
                                String[] propertyNames,
                                Type[] types)
    {
        throw new ReadOnlyException("Attempt to Update " + entity);
    }

    /**
     * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
     */
    @Override
    public boolean onSave(Object entity,
                          Serializable id,
                          Object[] state,
                          String[] propertyNames,
                          Type[] types)
    {
        throw new ReadOnlyException("Attempt to Save " + entity);
    }
}
