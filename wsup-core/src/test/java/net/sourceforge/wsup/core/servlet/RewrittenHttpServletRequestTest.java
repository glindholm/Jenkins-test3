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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import com.mockrunner.mock.web.MockHttpServletRequest;

public class RewrittenHttpServletRequestTest
{
    public RewrittenHttpServletRequestTest()
    {
    }

    private MockHttpServletRequest buildRequest(String urlString, String contextPath)
        throws Exception
    {
        URL url = new URL(urlString);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme(url.getProtocol());
        request.setServerName(url.getHost());
        request.setServerPort(url.getPort());
        if (contextPath == null)
        {
            request.setContextPath("/");
        }
        else
        {
            request.setContextPath(contextPath);
        }
        request.setRequestURI(url.getPath());
        String query = url.getQuery();
        if (query != null && query.length() > 0)
        {
            request.setQueryString(url.getQuery());
            String[] parts = query.split("&");
            for (String part : parts)
            {
                int equals = part.indexOf('=');
                String name = part.substring(0, equals);
                String value = part.substring(equals + 1);
                request.setupAddParameter(name, value);
            }
        }
        return request;
    }
    
    @SuppressWarnings({ "rawtypes" })
    private void assertParamNamesAre(String testName, Enumeration enumerator, String...names)
    {
        Set<String> paramNames = new HashSet<String>();
        for (String name : names)
        {
            paramNames.add(name);
        }
        
        while(enumerator.hasMoreElements())
        {
            String name = (String)enumerator.nextElement();
            assertTrue(testName, paramNames.contains(name));
            paramNames.remove(name);
        }
        
        assertEquals(testName, 0, paramNames.size());
    }

    @Test
    public void testInheritsFromParent1() throws Exception
    {
        HttpServletRequest parent = buildRequest("http://www.test.com/servlet/test.jsp?a=b&c=d",
                                                 "/servlet");
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent);
        assertEquals("http", request.getScheme());
        assertEquals("www.test.com", request.getServerName());
        assertEquals("/servlet", request.getContextPath());
        assertEquals("/servlet/test.jsp", request.getRequestURI());
        assertEquals("b", request.getParameter("a"));
        assertEquals("d", request.getParameter("c"));
        assertEquals("a=b&c=d", request.getQueryString());
        assertEquals("http://www.test.com/servlet/test.jsp", request.getRequestURL().toString());
    }

    @Test
    public void testInheritsFromParent2() throws Exception
    {
        HttpServletRequest parent = buildRequest("https://www.test.com/servlet/test.jsp?a=b&c=d",
                                                 null);
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent);
        assertEquals("https", request.getScheme());
        assertEquals("www.test.com", request.getServerName());
        assertEquals("/", request.getContextPath());
        assertEquals("/servlet/test.jsp", request.getRequestURI());
        assertEquals("b", request.getParameter("a"));
        assertEquals("d", request.getParameter("c"));
        assertEquals("a=b&c=d", request.getQueryString());
        assertEquals("https://www.test.com/servlet/test.jsp", request.getRequestURL().toString());
    }

    @Test
    public void testHandlesNonDefaultHttpPort() throws Exception
    {
        HttpServletRequest parent = buildRequest("http://www.test.com:8080/servlet/test.jsp?a=b&c=d",
                                                 null);
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent);
        assertEquals(8080, request.getServerPort());
        assertEquals("http://www.test.com:8080/servlet/test.jsp", request.getRequestURL()
            .toString());
    }

    @Test
    public void testHandlesRedundantDefaultHttpPort() throws Exception
    {
        HttpServletRequest parent = buildRequest("http://www.test.com:80/servlet/test.jsp?a=b&c=d",
                                                 null);
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent);
        assertEquals(80, request.getServerPort());
        assertEquals("http://www.test.com/servlet/test.jsp", request.getRequestURL().toString());
    }

    @Test
    public void testHandlesNonDefaultHttpsPort() throws Exception
    {
        HttpServletRequest parent = buildRequest("https://www.test.com:8443/servlet/test.jsp?a=b&c=d",
                                                 null);
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent);
        assertEquals(8443, request.getServerPort());
        assertEquals("https://www.test.com:8443/servlet/test.jsp", request.getRequestURL()
            .toString());
    }

    @Test
    public void testHandlesRedundantDefaultHttpsPort() throws Exception
    {
        HttpServletRequest parent = buildRequest("https://www.test.com:443/servlet/test.jsp?a=b&c=d",
                                                 null);
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent);
        assertEquals(443, request.getServerPort());
        assertEquals("https://www.test.com/servlet/test.jsp", request.getRequestURL().toString());
    }

    @Test
    public void testChangePathWithContextPath() throws Exception
    {
        HttpServletRequest parent = buildRequest("http://www.test.com/servlet/test.jsp?a=b&c=d",
                                                 "/servlet");
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent,
                                                                              "/newPath.jsp");
        assertEquals("/servlet", request.getContextPath());
        assertEquals("/servlet/newPath.jsp", request.getRequestURI());
        assertEquals("http://www.test.com/servlet/newPath.jsp", request.getRequestURL().toString());
    }

    @Test
    public void testChangePathWithDefaultContextPath() throws Exception
    {
        HttpServletRequest parent = buildRequest("http://www.test.com/servlet/test.jsp?a=b&c=d",
                                                 null);
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent,
                                                                              "/newPath.jsp");
        assertEquals("/", request.getContextPath());
        assertEquals("/newPath.jsp", request.getRequestURI());
        assertEquals("http://www.test.com/newPath.jsp", request.getRequestURL().toString());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testClearParameters() throws Exception
    {
        HttpServletRequest parent = buildRequest("http://www.test.com/servlet/test.jsp?a=b&c=d",
                                                 null);
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent);
        request.clearParameters();
        assertNull(request.getParameter("a"));
        assertNull(request.getParameter("c"));
        assertEquals("", request.getQueryString());
        Enumeration paramNames = request.getParameterNames();
        assertFalse(paramNames.hasMoreElements());
    }

    @Test
    public void testRemoveParameters() throws Exception
    {
        HttpServletRequest parent = buildRequest("http://www.test.com/servlet/test.jsp?a=b&c=d",
                                                 null);
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent);
        request.removeParameter("a");
        assertNull(request.getParameter("a"));
        assertEquals("d", request.getParameter("c"));
        assertEquals("c=d", request.getQueryString());
        assertParamNamesAre("testRemoveParameters", request.getParameterNames(), "c");
    }

    @Test
    public void testReplaceParam() throws Exception
    {
        HttpServletRequest parent = buildRequest("http://www.test.com/servlet/test.jsp?a=b&c=d",
                                                 null);
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent);
        request.replaceParameter("a", "zz");
        assertEquals("zz", request.getParameter("a"));
        assertEquals("d", request.getParameter("c"));
        assertEquals("a=zz&c=d", request.getQueryString());
        assertParamNamesAre("testReplaceParam", request.getParameterNames(), "a", "c");
    }

    @Test
    public void testReplaceParams() throws Exception
    {
        HttpServletRequest parent = buildRequest("http://www.test.com/servlet/test.jsp?a=b&c=d",
                                                 null);
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent);
        request.replaceParameter("a", new String[] { "aa", "zz" });
        String[] values = request.getParameterValues("a");
        assertEquals(2, values.length);
        assertEquals("aa", values[0]);
        assertEquals("zz", values[1]);
        assertEquals("d", request.getParameter("c"));
        assertEquals("a=aa&a=zz&c=d", request.getQueryString());
        assertParamNamesAre("testReplaceParams", request.getParameterNames(), "a", "c");
    }

    @Test
    public void testBadArrayLength() throws Exception
    {
        HttpServletRequest parent = buildRequest("http://www.test.com/servlet/test.jsp?a=b&c=d",
                                                 null);
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent);
        request.replaceParameter("a", new String[0]);
        assertNull(request.getParameter("a"));
        assertEquals("d", request.getParameter("c"));
        assertEquals("c=d", request.getQueryString());
    }

    @Test
    public void testEmptyParameter() throws Exception
    {
        HttpServletRequest parent = buildRequest("http://www.test.com/servlet/test.jsp?a=b&c=d",
                                                 null);
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent);
        request.replaceParameter("a", "");
        assertEquals("", request.getParameter("a"));
        assertEquals("d", request.getParameter("c"));
        assertEquals("a=&c=d", request.getQueryString());
        assertParamNamesAre("testEmptyParameter", request.getParameterNames(), "a", "c");
    }
    
    @Test
    public void testAddParameter() throws Exception
    {
        HttpServletRequest parent = buildRequest("http://www.test.com/servlet/test.jsp?a=b&c=d",
                                                 null);
        RewrittenHttpServletRequest request = new RewrittenHttpServletRequest(parent);
        request.replaceParameter("e", "f");
        assertEquals("b", request.getParameter("a"));
        assertEquals("d", request.getParameter("c"));
        assertEquals("f", request.getParameter("e"));
        assertEquals("a=b&c=d&e=f", request.getQueryString());
        assertParamNamesAre("testEmptyParameter", request.getParameterNames(), "a", "c", "e");
    }
}
