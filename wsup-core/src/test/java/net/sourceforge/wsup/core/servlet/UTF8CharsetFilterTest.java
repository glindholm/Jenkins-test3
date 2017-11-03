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

package net.sourceforge.wsup.core.servlet;

import static org.junit.Assert.assertEquals;
import net.sourceforge.wsup.core.testSupport.Log4JHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mockrunner.mock.web.MockFilterChain;
import com.mockrunner.mock.web.MockFilterConfig;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;

public class UTF8CharsetFilterTest
{
    private UTF8CharsetFilter   filter;
    
    public UTF8CharsetFilterTest()
    {
    }
    
    @Before
    public void setup() throws Exception
    {
        Log4JHelper.setLoggerToError(UTF8CharsetFilterTest.class);
        
        MockFilterConfig filterConfig = new MockFilterConfig();
        filterConfig.setFilterName("UTF8CharsetFilter");

        filter = new UTF8CharsetFilter();
        filter.init(filterConfig);
    }
    
    @After
    public void tearDown()
    {
        filter.destroy();
        Log4JHelper.resetLoggerLevels();
    }
    
    @Test
    public void testSetsEncodingIfNotPresent() throws Exception
    {
        MockFilterChain next = new MockFilterChain();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        filter.doFilter(request, response, next);
        
        assertEquals("UTF-8", request.getCharacterEncoding());
    }
    
    @Test
    public void testLeavesExistingEncodingAlone() throws Exception
    {
        MockFilterChain next = new MockFilterChain();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCharacterEncoding("US-ASCII");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        filter.doFilter(request, response, next);
        
        assertEquals("US-ASCII", request.getCharacterEncoding());
    }
}

