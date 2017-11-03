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

package net.sourceforge.wsup.hibernate4.interceptors;

import net.sourceforge.wsup.hibernate4.database.TestBean;

import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.junit.Test;

public class ReadOnlyInterceptorTest
{
    private ReadOnlyInterceptor interceptor = new ReadOnlyInterceptor();

    public ReadOnlyInterceptorTest()
    {
    }

    private static final String[] propertyNames = { "contents" };

    private static final Type[]   types         = { new StringType() };

    @Test(expected = ReadOnlyException.class)
    public void testSave()
    {
        TestBean entity = new TestBean();
        Long id = new Long(1);
        Object[] state = new Object[propertyNames.length];

        interceptor.onSave(entity, id, state, propertyNames, types);
    }

    @Test(expected = ReadOnlyException.class)
    public void testDelete()
    {
        TestBean entity = new TestBean();
        Long id = new Long(1);
        Object[] state = new Object[propertyNames.length];

        interceptor.onDelete(entity, id, state, propertyNames, types);
    }

    @Test(expected = ReadOnlyException.class)
    public void testUpdate()
    {
        TestBean entity = new TestBean();
        Long id = new Long(1);
        Object[] currentState = new Object[propertyNames.length];
        Object[] previousState = new Object[propertyNames.length];

        interceptor.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }
}
