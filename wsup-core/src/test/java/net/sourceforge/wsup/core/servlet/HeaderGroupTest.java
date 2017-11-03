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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;

/**
 * Unit test for HeaderGroup
 * 
 * @author Kevin Hunter
 * 
 */
public class HeaderGroupTest
{
	public HeaderGroupTest()
	{
	}
	
	@Test
	public void testBasic()
	{
		HeaderGroup obj = new HeaderGroup();
		
		assertEquals(0, obj.getHeaderCount());
		assertEquals(0, obj.getHeaders().size());
		
		obj.addHeaderValue("name", "value");
		
		assertEquals(1, obj.getHeaderCount());
		assertEquals(1, obj.getHeaders().size());
		assertEquals("name", obj.getHeader(0).getName());
		assertEquals(1, obj.getHeader(0).getValueCount());
		assertEquals(1, obj.getHeader(0).getValues().size());
		assertEquals("value", obj.getHeader(0).getValue(0));
		assertEquals("value", obj.getHeader(0).getValues().get(0));
		obj.verifyConsistency();
		
		obj.addHeaderValue("name", "value2");
		
		assertEquals(1, obj.getHeaderCount());
		assertEquals(1, obj.getHeaders().size());
		assertEquals("name", obj.getHeader(0).getName());
		assertEquals(2, obj.getHeader(0).getValueCount());
		assertEquals(2, obj.getHeader(0).getValues().size());
		assertEquals("value", obj.getHeader(0).getValue(0));
		assertEquals("value2", obj.getHeader(0).getValue(1));
		assertEquals("value", obj.getHeader(0).getValues().get(0));
		assertEquals("value2", obj.getHeader(0).getValues().get(1));
		obj.verifyConsistency();
		
		obj.addHeaderValue("Name", "value3");
		
		assertEquals(2, obj.getHeaderCount());
		assertEquals(2, obj.getHeaders().size());
		assertEquals("name", obj.getHeader(0).getName());
		assertEquals("Name", obj.getHeader(1).getName());
		assertEquals(2, obj.getHeader(0).getValueCount());
		assertEquals(2, obj.getHeader(0).getValues().size());
		assertEquals("value", obj.getHeader(0).getValue(0));
		assertEquals("value2", obj.getHeader(0).getValue(1));
		assertEquals("value", obj.getHeader(0).getValues().get(0));
		assertEquals("value2", obj.getHeader(0).getValues().get(1));
		assertEquals(1, obj.getHeader(1).getValueCount());
		assertEquals(1, obj.getHeader(1).getValues().size());
		assertEquals("value3", obj.getHeader(1).getValue(0));
		assertEquals("value3", obj.getHeader(1).getValues().get(0));
		obj.verifyConsistency();
	}
	
	@Test
	public void testNormalizing()
	{
		HeaderGroup obj = new HeaderGroup();
		
		assertEquals(0, obj.getHeaderCount());
		assertEquals(0, obj.getHeaders().size());
		
		obj.addHeaderValue("name", true, "value1");
		obj.addHeaderValue("NAME", true, "value2");
		
		assertEquals(1, obj.getHeaderCount());
		assertEquals("Name", obj.getHeaderName(0));
		assertEquals(2, obj.getValueCount(0));
		assertEquals("value1", obj.getValue(0, 0));
		assertEquals("value2", obj.getValue(0, 1));
		obj.verifyConsistency();
	}
	
	@Test
	public void testNonNormalizing()
	{
		HeaderGroup obj = new HeaderGroup();
		
		assertEquals(0, obj.getHeaderCount());
		assertEquals(0, obj.getHeaders().size());
		
		obj.addHeaderValue("name", false, "value1");
		obj.addHeaderValue("NAME", false, "value2");
		
		assertEquals(2, obj.getHeaderCount());
		assertEquals("name", obj.getHeaderName(0));
		assertEquals("NAME", obj.getHeaderName(1));
		assertEquals(1, obj.getValueCount(0));
		assertEquals(1, obj.getValueCount(1));
		assertEquals("value1", obj.getValue(0, 0));
		assertEquals("value2", obj.getValue(1, 0));
		obj.verifyConsistency();
	}
	
	@Test
	public void testFromString()
	{
		String input = "header1: Value1 \nHeader1 : value2\n\n";
		
		HeaderGroup obj1 = new HeaderGroup(input, false);
		
		assertEquals(2, obj1.getHeaderCount());
		assertEquals("header1", obj1.getHeaderName(0));
		assertEquals("Header1", obj1.getHeaderName(1));
		assertEquals(1, obj1.getValueCount(0));
		assertEquals(1, obj1.getValueCount(1));
		assertEquals("Value1", obj1.getValue(0, 0));
		assertEquals("value2", obj1.getValue(1, 0));
		obj1.verifyConsistency();
		
		obj1.load(input, true);
		
		assertEquals(1, obj1.getHeaderCount());
		assertEquals("Header1", obj1.getHeaderName(0));
		assertEquals(2, obj1.getValueCount(0));
		assertEquals("Value1", obj1.getValue(0, 0));
		assertEquals("value2", obj1.getValue(0, 1));
		obj1.verifyConsistency();
		
		HeaderGroup obj2 = new HeaderGroup(input, true);
		
		assertEquals(1, obj2.getHeaderCount());
		assertEquals("Header1", obj2.getHeaderName(0));
		assertEquals(2, obj2.getValueCount(0));
		assertEquals("Value1", obj2.getValue(0, 0));
		assertEquals("value2", obj2.getValue(0, 1));
		obj2.verifyConsistency();
		
		obj2.load(input, false);
		
		assertEquals(2, obj2.getHeaderCount());
		assertEquals("header1", obj2.getHeaderName(0));
		assertEquals("Header1", obj2.getHeaderName(1));
		assertEquals(1, obj2.getValueCount(0));
		assertEquals(1, obj2.getValueCount(1));
		assertEquals("Value1", obj2.getValue(0, 0));
		assertEquals("value2", obj2.getValue(1, 0));
		obj2.verifyConsistency();
	}
	
	@Test
	public void testBadInputStrings()
	{
		// empty lines and headers without values
		String input = "\n\nNoColon\n\n";
		
		HeaderGroup obj = new HeaderGroup(input, false);
		assertEquals(0, obj.getHeaderCount());
		obj.verifyConsistency();
	}
	
	@Test
	public void testFromRequest()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		request.addHeader("header1", "Value1");
		
		HeaderGroup obj1 = new HeaderGroup(request, false);
		
		assertEquals(1, obj1.getHeaderCount());
		assertEquals("header1", obj1.getHeaderName(0));
		assertEquals(1, obj1.getValueCount(0));
		assertEquals("Value1", obj1.getValue(0, 0));
		obj1.verifyConsistency();
		
		obj1.load(request, true);
		
		assertEquals(1, obj1.getHeaderCount());
		assertEquals("Header1", obj1.getHeaderName(0));
		assertEquals(1, obj1.getValueCount(0));
		assertEquals("Value1", obj1.getValue(0, 0));
		obj1.verifyConsistency();
		
		HeaderGroup obj2 = new HeaderGroup(request, true);
		
		assertEquals(1, obj2.getHeaderCount());
		assertEquals("Header1", obj2.getHeaderName(0));
		assertEquals(1, obj2.getValueCount(0));
		assertEquals("Value1", obj2.getValue(0, 0));
		obj2.verifyConsistency();
		
		obj2.load(request, false);
		
		assertEquals(1, obj2.getHeaderCount());
		assertEquals("header1", obj2.getHeaderName(0));
		assertEquals(1, obj2.getValueCount(0));
		assertEquals("Value1", obj2.getValue(0, 0));
		obj2.verifyConsistency();
	}
	
	@Test
	public void testToString()
	{
		HeaderGroup obj = new HeaderGroup();
		
        obj.addHeaderValue("Header3", "Value3");
		obj.addHeaderValue("Header2", "Value2");
        obj.addHeaderValue("Header1", "Value1b");
        obj.addHeaderValue("Header1", "Value1a");
		
        assertEquals("Header3: Value3\nHeader2: Value2\nHeader1: Value1b\nHeader1: Value1a", obj.toString());
        assertEquals("Header1: Value1a\nHeader1: Value1b\nHeader2: Value2\nHeader3: Value3", obj.toNormalizedString());
        obj.verifyConsistency();
	}
	
	@Test
	public void testRemoveHeaders()
	{
		String input = "Keep-Alive: true\nHost: foo.com\nCookie: theCookie";
		
		HeaderGroup obj = new HeaderGroup(input, true);
		
        assertNotNull(obj.getHeader("Keep-Alive"));
        
		obj.removeHeaders(HeaderUtils.PER_CONNECTION_HEADER_SET);
		obj.removeHeaders(HeaderUtils.SESSION_HEADER_SET);
		
		assertEquals(1, obj.getHeaderCount());
		assertEquals("Host", obj.getHeaderName(0));
		assertNull(obj.getHeader("Keep-Alive"));
		obj.verifyConsistency();
	}
    
    @Test
    public void testNormalizeHeader1()
    {
        HeaderGroup.Header header = new HeaderGroup.Header(" ");
        header.addValue(" ");
        header.normalize();
        
        assertNull(header.getName());
        assertEquals(0, header.getValueCount());
    }
    
    @Test
    public void testNormalizeHeader2()
    {
        HeaderGroup.Header header = new HeaderGroup.Header(" header ");
        header.addValue(" value 2 ");
        header.addValue(" value 1 ");
        header.normalize();
        
        assertEquals("header", header.getName());
        assertEquals(2, header.getValueCount());
        assertEquals("value 1", header.getValue(0));
        assertEquals("value 2", header.getValue(1));
    }
    
    @Test
    public void testNormalizeHeader3()
    {
        HeaderGroup.Header header = new HeaderGroup.Header(" header ");
        header.addValue(" value 2 ");
        header.addValue(" value 2 ");
        header.addValue(" value 1 ");
        header.addValue(" value 1 ");
        header.addValue("   ");
        assertEquals(5, header.getValueCount());
        header.normalize();
        
        assertEquals("header", header.getName());
        assertEquals(2, header.getValueCount());
        assertEquals("value 1", header.getValue(0));
        assertEquals("value 2", header.getValue(1));
    }
    
    @Test
    public void testNormalizeHeaderGroup1()
    {
        HeaderGroup group = new HeaderGroup();
        
        group.addHeaderValue(" ", " ");
        group.addHeaderValue(" ", "null name");
        group.addHeaderValue("name3", " ");
        group.addHeaderValue(" name2 ", " value2 ");
        group.addHeaderValue(" name1 ", " value1b ");
        group.addHeaderValue(" name1 ", " value1a ");
        group.addHeaderValue("user-agent", "value0");
        
        group.normalize();
        
        assertEquals(3, group.getHeaderCount());
        assertEquals("User-Agent", group.getHeaderName(0));
        assertEquals("Name1", group.getHeaderName(1));
        assertEquals("Name2", group.getHeaderName(2));
        
        assertEquals(2, group.getValueCount(1));
        assertEquals("value1a", group.getValue(1, 0));
        assertEquals("value1b", group.getValue(1, 1));
        assertEquals(1, group.getValueCount(2));
        assertEquals("value2", group.getValue(2, 0));
        
        group.verifyConsistency();
    }
    
    @Test
    public void testNormalizeGroup2()
    {
        HeaderGroup group = new HeaderGroup();
        
        group.addHeaderValue(" ", " ");
        
        group.normalize();
        
        assertEquals(0, group.getHeaderCount());
    }
    
    @Test
    public void testNormalizeGroup3()
    {
        HeaderGroup group = new HeaderGroup();
        
        group.addHeaderValue("headeR", "value2");
        group.addHeaderValue("header", "value1");
        group.addHeaderValue("Header", "value1");
        
        group.normalize();
        
        assertEquals(1, group.getHeaderCount());
        HeaderGroup.Header header = group.getHeader(0);
        assertEquals("Header", header.getName());
        assertEquals(2, header.getValueCount());
        assertEquals("value1", header.getValue(0));
        assertEquals("value2", header.getValue(1));
    }
    
    @Test
    public void testToNormalizedString1()
    {
        HeaderGroup group = new HeaderGroup();
        
        group.addHeaderValue(" ", " ");
        group.addHeaderValue(" ", "null name");
        group.addHeaderValue("name3", " ");
        group.addHeaderValue(" name2 ", " value2 ");
        group.addHeaderValue(" name1 ", " value1b ");
        group.addHeaderValue(" name1 ", " value1b ");
        group.addHeaderValue(" name1 ", " value1a ");
        group.addHeaderValue("user-agent", "value0");
        
        String preResult = "user-agent: value0\nname1: value1a\nname1: value1b\nname1: value1b\nname2: value2";
        
        assertEquals(preResult, group.toNormalizedString());

        group.normalize();
        String postResult = "User-Agent: value0\nName1: value1a\nName1: value1b\nName2: value2";
        assertEquals(postResult, group.toString());
    }
    
    @Test
    public void testListReturnedIsACopy()
    {
        HeaderGroup group = new HeaderGroup();
        group.addHeaderValue("name1", "value1");
        group.addHeaderValue("name2", "value2");
        
        group.getHeaders().remove(0);
        
        assertEquals(2, group.getHeaderCount());
        assertEquals(2, group.getHeaders().size());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testOutput()
    {
        HeaderGroup group = new HeaderGroup();
        group.addHeaderValue("name1", "value1a");
        group.addHeaderValue("name1", "value1b");
        group.addHeaderValue("name2", "value2");
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        group.outputTo(response);
        
        List<String> one = response.getHeaderList("name1");
        assertEquals(2, one.size());
        assertEquals("value1a", one.get(0));
        assertEquals("value1b", one.get(1));
        
        List<String> two = response.getHeaderList("name2");
        assertEquals(1, two.size());
        assertEquals("value2", two.get(0));
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testListImmutable()
    {
        HeaderGroup.Header header = new HeaderGroup.Header("Header");
        header.addValue("Value");
        List<String> list = header.getValues();
        list.clear();
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testSetImmutable()
    {
        HeaderGroup.Header header = new HeaderGroup.Header("Header");
        header.addValue("Value");
        Set<String> set = header.getUniqueValues();
        set.clear();
    }
    
    @Test
    public void testContains()
    {
        HeaderGroup.Header header = new HeaderGroup.Header("Header");
        assertFalse(header.containsValue("Value"));
        header.addValue("Value");
        assertTrue(header.containsValue("Value"));
    }
    
    @Test
    public void testUniqueValues()
    {
        HeaderGroup.Header header = new HeaderGroup.Header("Header");
        assertEquals(0, header.getValueCount());
        assertEquals(0, header.getUniqueValueCount());
        header.addValue("Value");
        assertEquals(1, header.getValueCount());
        assertEquals(1, header.getUniqueValueCount());
        header.addValue("Value");
        assertEquals(2, header.getValueCount());
        assertEquals(1, header.getUniqueValueCount());
    }
}
