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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.Cookie;

import net.sourceforge.wsup.core.servlet.HeaderUtils;

import org.junit.Test;

import com.mockrunner.mock.web.MockHttpServletRequest;

public class CookieUtilsTest
{
    public CookieUtilsTest()
    {
    }

    @Test
    public void coverConstruction()
    {
        CookieUtils.codeCoverage();
    }
    
    @Test
    public void testGetWhenCookieArrayIsNull()
    {
        MockHttpServletRequest request = new NullCookieArrayReqest();
        assertNull(CookieUtils.getCookie(request, "name"));
        assertNull(CookieUtils.getCookieValue(request, "name"));
    }

    @Test
    public void testGetWhenCookieArrayIsEmpty()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertNull(CookieUtils.getCookie(request, "name"));
        assertNull(CookieUtils.getCookieValue(request, "name"));
    }

    @Test
    public void testGetWhenCookieNotFound()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addCookie(new Cookie("name2", "value2"));
        assertNull(CookieUtils.getCookie(request, "name"));
        assertNull(CookieUtils.getCookieValue(request, "name"));
    }

    @Test
    public void testGetWhenCookieFound()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addCookie(new Cookie("name", "value"));
        request.addCookie(new Cookie("name2", "value2"));

        Cookie cookie = CookieUtils.getCookie(request, "name");
        assertEquals("name", cookie.getName());
        assertEquals("value", cookie.getValue());
        assertEquals("value", CookieUtils.getCookieValue(request, "name"));
    }

    @Test
    public void testAllNumeric()
    {
        assertTrue(CookieUtils.isAllNumeric("192.168.1.1"));
        assertTrue(CookieUtils.isAllNumeric("123.456.789.012"));
        assertFalse(CookieUtils.isAllNumeric("a11.com"));
    }

    private static final String[] DOMAIN_FOR_COOKIE_NULL_CASES = { null, // no host name
        "localhost", // no dots
        "192.168.1.1", // all numeric
                                                               };

    @Test
    public void testDomainForCookieNullCases()
    {
        for (int i = 0; i < DOMAIN_FOR_COOKIE_NULL_CASES.length; i++)
        {
            String testName = "DOMAIN_FOR_COOKIE_NULL_CASES[" + i + "]";

            MockHttpServletRequest request = new MockHttpServletRequest();
            if (DOMAIN_FOR_COOKIE_NULL_CASES[i] != null)
            {
                request.setHeader(HeaderUtils.HEADER_HOST, DOMAIN_FOR_COOKIE_NULL_CASES[i]);
            }

            assertNull(testName, CookieUtils.getDomainForCookie(request, false));
        }
    }

    /*
     * Each entry is a test case, consisting of three strings -
     * the input host name
     * the result when "topLevel" is false
     * the result when "topLevel" is true
     */
    private static final String[][] DOMAIN_FOR_COOKIE_CASES = {
        { "domain.com", ".domain.com", ".domain.com" },
        { "www.domain.com", ".domain.com", ".domain.com" },
        { "www.nested.domain.com", ".nested.domain.com", ".domain.com" }, };

    @Test
    public void testDomainForCookieCases()
    {
        for (int i = 0; i < DOMAIN_FOR_COOKIE_CASES.length; i++)
        {
            String testName = "DOMAIN_FOR_COOKIE_CASES[" + i + "]";

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setHeader(HeaderUtils.HEADER_HOST, DOMAIN_FOR_COOKIE_CASES[i][0]);
            
            String falseResult = CookieUtils.getDomainForCookie(request, false);
            String trueResult = CookieUtils.getDomainForCookie(request, true);

            assertEquals(testName + "-false", DOMAIN_FOR_COOKIE_CASES[i][1], falseResult);
            assertEquals(testName + "-true", DOMAIN_FOR_COOKIE_CASES[i][2], trueResult);
        }
    }
    
    @Test
    public void testCreateDomainCookieNull()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setHeader(HeaderUtils.HEADER_HOST, "localhost");
        Cookie cookie = CookieUtils.createDomainCookie(request, true, "cookieName", "cookieValue");
        assertEquals("cookieName", cookie.getName());
        assertEquals("cookieValue", cookie.getValue());
        assertNull(cookie.getDomain());
    }
    
    @Test
    public void testCreateDomainCookieDomain()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setHeader(HeaderUtils.HEADER_HOST, "www.some.domain.com");
        Cookie cookie = CookieUtils.createDomainCookie(request, false, "cookieName1", "cookieValue1");
        assertEquals("cookieName1", cookie.getName());
        assertEquals("cookieValue1", cookie.getValue());
        assertEquals(".some.domain.com", cookie.getDomain());
    }
    
    @Test
    public void testCreateDomainCookieTopLevel()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setHeader(HeaderUtils.HEADER_HOST, "www.some.domain.com");
        Cookie cookie = CookieUtils.createDomainCookie(request, true, "cookieName2", "cookieValue2");
        assertEquals("cookieName2", cookie.getName());
        assertEquals("cookieValue2", cookie.getValue());
        assertEquals(".domain.com", cookie.getDomain());
    }

    /**
     * MockHttpServletRequest always returns a non-null array for getCookies(),
     * which is what is supposed to happen. This class returns a null array
     * so we can make sure that won't cause a NullPointerException.
     * 
     * @author Kevin Hunter
     * 
     */
    private static class NullCookieArrayReqest extends MockHttpServletRequest
    {
        public NullCookieArrayReqest()
        {
        }

        public Cookie[] getCookies()
        {
            return null;
        }
    }
}
