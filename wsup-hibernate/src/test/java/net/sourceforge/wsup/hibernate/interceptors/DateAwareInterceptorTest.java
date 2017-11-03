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

package net.sourceforge.wsup.hibernate.interceptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import net.sourceforge.wsup.hibernate.TestBean;
import net.sourceforge.wsup.hibernate.TestDateBean;

import org.hibernate.type.DateType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.junit.Test;

public class DateAwareInterceptorTest
{
	private DateAwareInterceptor interceptor = new DateAwareInterceptor();
	
	public DateAwareInterceptorTest()
	{
	}
	
	private static final int CREATED_INDEX = 0;
	private static final int UPDATED_INDEX = 1;
	private static final String[] propertyNames =
	{
		"created",
		"updated",
		"contents",
		"other"
	};
	
	private static final Type[] types =
	{
		new DateType(),
		new DateType(),
		new StringType(),
		new StringType()
	};
	
	@Test
	public void testCreate()
	{
		TestDateBean bean = new TestDateBean();
		
		Object[] state = new Object[propertyNames.length];
		
		assertTrue(interceptor.onSave(bean, new Long(1), state, propertyNames, types));
		assertNotNull(state[CREATED_INDEX]);
		assertNotNull(state[UPDATED_INDEX]);
	}
	
	@Test
	public void testCreateWithExistingDate()
	{
		TestDateBean bean = new TestDateBean();
		
		Object[] state = new Object[propertyNames.length];
		
		Date then = new Date(4242);
		state[CREATED_INDEX] = then;
		
		assertTrue(interceptor.onSave(bean, new Long(1), state, propertyNames, types));
		assertNotNull(state[CREATED_INDEX]);
		assertNotNull(state[UPDATED_INDEX]);
		
		assertEquals(then, state[CREATED_INDEX]);
	}
	
	@Test
	public void testCreateWithoutInterfaces()
	{
		TestBean bean = new TestBean();
		
		Object[] state = new Object[propertyNames.length];
		
		assertFalse(interceptor.onSave(bean, new Long(1), state, propertyNames, types));
	}
	
	@Test
	public void testUpdate()
	{
		TestDateBean bean = new TestDateBean();
		
		Object[] state = new Object[propertyNames.length];
		
		Date then = new Date(4242);
		state[CREATED_INDEX] = then;
		state[UPDATED_INDEX] = then;
		
		assertTrue(interceptor.onFlushDirty(bean, new Long(1), state, state, propertyNames, types));
		assertNotNull(state[CREATED_INDEX]);
		assertNotNull(state[UPDATED_INDEX]);
		
		assertEquals(then, state[CREATED_INDEX]);
		assertTrue(((Date)state[UPDATED_INDEX]).getTime() >= System.currentTimeMillis() - 1000);
	}
	
	@Test
	public void testUpdateWithoutInterfaces()
	{
		TestBean bean = new TestBean();
		
		Object[] state = new Object[propertyNames.length];
		
		Date then = new Date(4242);
		state[CREATED_INDEX] = then;
		state[UPDATED_INDEX] = then;
		
		assertFalse(interceptor.onFlushDirty(bean, new Long(1), state, state, propertyNames, types));
		assertNotNull(state[CREATED_INDEX]);
		assertNotNull(state[UPDATED_INDEX]);
		
		assertEquals(then, state[CREATED_INDEX]);
		assertEquals(then, state[UPDATED_INDEX]);
	}
	
	@Test
	public void coverMissingProperties()
	{
		TestDateBean bean = new TestDateBean();
		
		Object[] state = new Object[propertyNames.length];
		
		Date then = new Date(4242);
		state[CREATED_INDEX] = then;
		state[UPDATED_INDEX] = then;
		
		String[] badPropertyNames =
		{
			"Xcreated",
			"Xupdated",
			"contents",
			"other"
		};
		
		assertFalse(interceptor.onFlushDirty(bean, new Long(1), state, state, badPropertyNames, types));
	}
}
