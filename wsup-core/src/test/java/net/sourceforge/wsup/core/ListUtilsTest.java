/*
 *  Copyright (c) 2011 Kevin Hunter
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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ListUtilsTest
{
	public ListUtilsTest()
	{
	}
	
	@Test
	public void testPaging()
	{
		ArrayList<Integer> input = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++)
		{
			input.add(new Integer(i));
		}
		
		List<Integer> output = ListUtils.pageFromList(input, 2, 3);
		assertEquals(3, output.size());
		assertEquals(new Integer(2), output.get(0));
		assertEquals(new Integer(3), output.get(1));
		assertEquals(new Integer(4), output.get(2));
		
		output = ListUtils.pageFromList(input, 20, 5);
		assertEquals(0, output.size());
		
		output = ListUtils.pageFromList(null, 20, 5);
		assertEquals(0, output.size());
	}
	
	@Test
	public void coverage()
	{
	    ListUtils.coverage();
	}
}

