/*
 * Copyright 2012 Kevin Hunter
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.sourceforge.wsup.hibernate4.interceptors;

import java.io.Serializable;
import java.util.Date;

import net.jcip.annotations.ThreadSafe;
import net.sourceforge.wsup.hibernate4.model.CreatedDate;
import net.sourceforge.wsup.hibernate4.model.UpdatedDate;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

/**
 * This is a Hibernate <code>Interceptor</code> that will automatically set the <code>created</code>
 * and <code>updated</code> properties on entities as they
 * are persisted or updated. The entities in question simply need to implement
 * the {@link CreatedDate} and/or {@link UpdatedDate} interfaces.
 * <p>
 * The <code>updated</code> date will be set unconditionally whenever an object is created or a
 * dirty object is flushed. The <code>created</code> date will be set when an object is created if
 * and only if it does not already have a value.
 * </p>
 * <p>
 * This interceptor is thread-safe, so a single instance of it can be used across multiple
 * <code>Session</code>s.
 * </p>
 * 
 * @author Kevin Hunter
 * 
 */
@ThreadSafe
public class DateAwareInterceptor extends EmptyInterceptor
{
    private static final long serialVersionUID = 8880860040080446369L;

    /**
     * @see org.hibernate.EmptyInterceptor#onFlushDirty(java.lang.Object, java.io.Serializable,
     *      java.lang.Object[], java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
     */
    @Override
    public boolean onFlushDirty(Object entity,
                                Serializable id,
                                Object[] currentState,
                                Object[] previousState,
                                String[] propertyNames,
                                Type[] types)
    {
        if (entity instanceof UpdatedDate)
        {
            return setDate("updated", new Date(), currentState, propertyNames, true);
        }

        return false;
    }

    /**
     * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object, java.io.Serializable,
     *      java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
     */
    @Override
    public boolean onSave(Object entity,
                          Serializable id,
                          Object[] state,
                          String[] propertyNames,
                          Type[] types)
    {
        boolean objectChanged = false;
        Date now = new Date();

        if (entity instanceof UpdatedDate)
        {
            objectChanged |= setDate("updated", now, state, propertyNames, true);
        }

        if (entity instanceof CreatedDate)
        {
            objectChanged |= setDate("created", now, state, propertyNames, false);
        }

        return objectChanged;
    }

    private boolean setDate(String datePropertyName,
                            Date date,
                            Object[] currentState,
                            String[] propertyNames,
                            boolean unconditional)
    {
        for (int i = 0; i < propertyNames.length; i++)
        {
            if (propertyNames[i].equals(datePropertyName))
            {
                if (unconditional)
                {
                    currentState[i] = date;
                    return true;
                }
                if (currentState[i] == null)
                {
                    currentState[i] = date;
                    return true;
                }
                return false;
            }
        }

        return false;
    }
}
