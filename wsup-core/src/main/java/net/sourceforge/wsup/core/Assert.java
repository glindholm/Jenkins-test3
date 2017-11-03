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

/**
 * Utility class for in-code assertions.  Basically similar to using the built-in
 * Java <code>assert</code> mechanism, except that these can't be disabled by
 * a JVM argument.
 * 
 * @author Kevin Hunter
 *
 */
public class Assert
{
	public static void isNotNull(Object obj, String message)
	{
		if (obj == null)
		{
			throw new AssertionError(message);
		}
	}
	
	public static void isNotNull(Object obj)
	{
		isNotNull(obj, "isNotNull constraint violated");
	}
	
	public static void isNull(Object obj, String message)
	{
		if (obj != null)
		{
			throw new AssertionError(message);
		}
	}
	
	public static void isNull(Object obj)
	{
		isNull(obj, "isNull constraint violated");
	}
	
	public static void isTrue(boolean condition, String message)
	{
		if (!condition)
		{
			throw new AssertionError(message);
		}
	}
	
	public static void isTrue(boolean condition)
	{
		isTrue(condition, "isTrue constraint violated");
	}
	
	public static void isFalse(boolean condition, String message)
	{
		if (condition)
		{
			throw new AssertionError(message);
		}
	}
	
	public static void isFalse(boolean condition)
	{
		isFalse(condition, "isFalse constraint violated");
	}
	
	public static void equals(int expected, int actual, String message)
	{
		if (expected != actual)
		{
			throw new AssertionError(message);
		}
	}
	
	public static void equals(int expected, int actual)
	{
		equals(expected, actual, "equals constraint violated");
	}
	
	/*package*/static void coverage()
	{
		new Assert();
	}
	
	private Assert()
	{
	}
}
