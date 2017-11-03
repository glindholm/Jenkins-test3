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

package net.sourceforge.wsup.core;

import org.junit.Test;

public class AssertTest
{
	public AssertTest()
	{
	}
	
	@Test
	public void testSuccess()
	{
		Assert.isNull(null);
		Assert.isNotNull(this);
		Assert.isTrue(true);
		Assert.isFalse(false);
		Assert.equals(0, 0);
	}
	
	@Test(expected = AssertionError.class)
	public void testFailedAssertNull()
	{
		Assert.isNull(this);
	}
	
	@Test(expected = AssertionError.class)
	public void testFailedAssertNotNull()
	{
		Assert.isNotNull(null);
	}
	
	@Test(expected = AssertionError.class)
	public void testFailedAssertTrue()
	{
		Assert.isTrue(false);
	}
	
	@Test(expected = AssertionError.class)
	public void testFailedAssertFalse()
	{
		Assert.isFalse(true);
	}
	
	@Test(expected = AssertionError.class)
	public void testFailedAssertEquals()
	{
		Assert.equals(0, 1);
	}
	
	@Test
	public void coverage()
	{
		Assert.coverage();
	}
}
