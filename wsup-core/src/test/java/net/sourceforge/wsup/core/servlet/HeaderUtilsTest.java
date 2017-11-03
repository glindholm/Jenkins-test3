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

package net.sourceforge.wsup.core.servlet;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Test;

/**
 * Unit test for the HeaderUtils class.
 * 
 * @author Kevin Hunter
 * 
 */
public class HeaderUtilsTest
{
	public HeaderUtilsTest()
	{
	}
	
	@Test
	public void coverage()
	{
		HeaderUtils.coverage();
	}
	
	@Test
	public void testNormalize()
	{
		String correct = "User-Agent";
		String[] inputs = { "user-agent", "USER-AGENT", "User-Agent" };
		
		for (int i = 0; i < inputs.length; i++)
		{
			assertEquals("inputs["+i+"]", correct, HeaderUtils.normalizeHeader(inputs[i]));
		}
	}
	
	/*
	 * Verify that all the HEADER_ fields in HeaderUtils are in their normalized
	 * form (i.e. normalizing them results in the same value)
	 */
	@Test
	public void testHeadersAllNormalized() throws Exception
	{
		Field[] fields = HeaderUtils.class.getFields();
		for (Field field : fields)
		{
			if (Modifier.isPublic(field.getModifiers()))
			{
				if (field.getName().startsWith("HEADER_"))
				{
					String value = (String)field.get(null);
					
					assertEquals(value, HeaderUtils.normalizeHeader(value));
				}
			}
		}
	}
}
