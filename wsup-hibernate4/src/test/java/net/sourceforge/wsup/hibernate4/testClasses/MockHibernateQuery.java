/*
 *  Copyright (c) 2012 Kevin Hunter
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

package net.sourceforge.wsup.hibernate4.testClasses;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;

@SuppressWarnings("rawtypes")
public class MockHibernateQuery implements Query
{
    private String fieldName;
    private String value;

    public MockHibernateQuery()
    {
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public String getQueryString()
    {
        return null;
    }

    @Override
    public Type[] getReturnTypes() throws HibernateException
    {
        return null;
    }

    @Override
    public String[] getReturnAliases() throws HibernateException
    {
        return null;
    }

    @Override
    public String[] getNamedParameters() throws HibernateException
    {
        return null;
    }

    @Override
    public Iterator iterate() throws HibernateException
    {
        return null;
    }

    @Override
    public ScrollableResults scroll() throws HibernateException
    {
        return null;
    }

    @Override
    public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException
    {
        return null;
    }

    @Override
    public List list() throws HibernateException
    {
        return null;
    }

    @Override
    public Object uniqueResult() throws HibernateException
    {
        return this;
    }

    @Override
    public int executeUpdate() throws HibernateException
    {
        return 0;
    }

    @Override
    public Query setMaxResults(int maxResults)
    {
        return this;
    }

    @Override
    public Query setFirstResult(int firstResult)
    {
        return this;
    }

    @Override
    public boolean isReadOnly()
    {
        return false;
    }

    @Override
    public Query setReadOnly(boolean readOnly)
    {
        return this;
    }

    @Override
    public Query setCacheable(boolean cacheable)
    {
        return this;
    }

    @Override
    public Query setCacheRegion(String cacheRegion)
    {
        return this;
    }

    @Override
    public Query setTimeout(int timeout)
    {
        return this;
    }

    @Override
    public Query setFetchSize(int fetchSize)
    {
        return this;
    }

    @Override
    public Query setLockOptions(LockOptions lockOptions)
    {
        return this;
    }

    @Override
    public Query setLockMode(String alias, LockMode lockMode)
    {
        return this;
    }

    @Override
    public Query setComment(String comment)
    {
        return this;
    }

    @Override
    public Query setFlushMode(FlushMode flushMode)
    {
        return this;
    }

    @Override
    public Query setCacheMode(CacheMode cacheMode)
    {
        return this;
    }

    @Override
    public Query setParameter(int position, Object val, Type type)
    {
        return this;
    }

    @Override
    public Query setParameter(String name, Object val, Type type)
    {
        return this;
    }

    @Override
    public Query setParameter(int position, Object val) throws HibernateException
    {
        return this;
    }

    @Override
    public Query setParameter(String name, Object val) throws HibernateException
    {
        return this;
    }

    @Override
    public Query setParameters(Object[] values, Type[] types) throws HibernateException
    {
        return this;
    }

    @Override
    public Query setParameterList(String name, Collection vals, Type type)
        throws HibernateException
    {
        return this;
    }

    @Override
    public Query setParameterList(String name, Collection vals) throws HibernateException
    {
        return this;
    }

    @Override
    public Query setParameterList(String name, Object[] vals, Type type) throws HibernateException
    {
        return this;
    }

    @Override
    public Query setParameterList(String name, Object[] vals) throws HibernateException
    {
        return this;
    }

    @Override
    public Query setProperties(Object bean) throws HibernateException
    {
        return this;
    }

    @Override
    public Query setProperties(Map bean) throws HibernateException
    {
        return this;
    }

    @Override
    public Query setString(int position, String val)
    {
        return this;
    }

    @Override
    public Query setCharacter(int position, char val)
    {
        return this;
    }

    @Override
    public Query setBoolean(int position, boolean val)
    {
        return this;
    }

    @Override
    public Query setByte(int position, byte val)
    {
        return this;
    }

    @Override
    public Query setShort(int position, short val)
    {
        return this;
    }

    @Override
    public Query setInteger(int position, int val)
    {
        return this;
    }

    @Override
    public Query setLong(int position, long val)
    {
        return this;
    }

    @Override
    public Query setFloat(int position, float val)
    {
        return this;
    }

    @Override
    public Query setDouble(int position, double val)
    {
        return this;
    }

    @Override
    public Query setBinary(int position, byte[] val)
    {
        return this;
    }

    @Override
    public Query setText(int position, String val)
    {
        return this;
    }

    @Override
    public Query setSerializable(int position, Serializable val)
    {
        return this;
    }

    @Override
    public Query setLocale(int position, Locale locale)
    {
        return this;
    }

    @Override
    public Query setBigDecimal(int position, BigDecimal number)
    {
        return this;
    }

    @Override
    public Query setBigInteger(int position, BigInteger number)
    {
        return this;
    }

    @Override
    public Query setDate(int position, Date date)
    {
        return this;
    }

    @Override
    public Query setTime(int position, Date date)
    {
        return this;
    }

    @Override
    public Query setTimestamp(int position, Date date)
    {
        return this;
    }

    @Override
    public Query setCalendar(int position, Calendar calendar)
    {
        return this;
    }

    @Override
    public Query setCalendarDate(int position, Calendar calendar)
    {
        return this;
    }

    @Override
    public Query setString(String name, String val)
    {
        fieldName = name;
        value = val;
        return this;
    }

    @Override
    public Query setCharacter(String name, char val)
    {
        return this;
    }

    @Override
    public Query setBoolean(String name, boolean val)
    {
        return this;
    }

    @Override
    public Query setByte(String name, byte val)
    {
        return this;
    }

    @Override
    public Query setShort(String name, short val)
    {
        return this;
    }

    @Override
    public Query setInteger(String name, int val)
    {
        return this;
    }

    @Override
    public Query setLong(String name, long val)
    {
        return this;
    }

    @Override
    public Query setFloat(String name, float val)
    {
        return this;
    }

    @Override
    public Query setDouble(String name, double val)
    {
        return this;
    }

    @Override
    public Query setBinary(String name, byte[] val)
    {
        return this;
    }

    @Override
    public Query setText(String name, String val)
    {
        return this;
    }

    @Override
    public Query setSerializable(String name, Serializable val)
    {
        return this;
    }

    @Override
    public Query setLocale(String name, Locale locale)
    {
        return this;
    }

    @Override
    public Query setBigDecimal(String name, BigDecimal number)
    {
        return this;
    }

    @Override
    public Query setBigInteger(String name, BigInteger number)
    {
        return this;
    }

    @Override
    public Query setDate(String name, Date date)
    {
        return this;
    }

    @Override
    public Query setTime(String name, Date date)
    {
        return this;
    }

    @Override
    public Query setTimestamp(String name, Date date)
    {
        return this;
    }

    @Override
    public Query setCalendar(String name, Calendar calendar)
    {
        return this;
    }

    @Override
    public Query setCalendarDate(String name, Calendar calendar)
    {
        return this;
    }

    @Override
    public Query setEntity(int position, Object val)
    {
        return this;
    }

    @Override
    public Query setEntity(String name, Object val)
    {
        return this;
    }

    @Override
    public Query setResultTransformer(ResultTransformer transformer)
    {
        return this;
    }

}
