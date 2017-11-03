/*
 *  Copyright (c) 2010 Greg Lindholm
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mockrunner.mock.web.MockHttpServletRequest;

public class ServletUtilsTest
{

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testBuildSyntheticRequestUrl()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setRequestURL("http://example.com/test");
        request.setMethod("GET");
        assertEquals("http://example.com/test", ServletUtils.buildSyntheticRequestUrl(request));

        request = new MockHttpServletRequest();
        request.setRequestURL("http://example.com/test");
        request.setMethod("GET");
        request.setQueryString("One=1&Two=2");
        assertEquals("http://example.com/test?One=1&Two=2", ServletUtils
            .buildSyntheticRequestUrl(request));

        request = new MockHttpServletRequest();
        request.setRequestURL("http://example.com/test");
        request.setMethod("POST");
        assertEquals("http://example.com/test", ServletUtils.buildSyntheticRequestUrl(request));

        request = new MockHttpServletRequest();
        request.setRequestURL("http://example.com/test");
        request.setMethod("POST");
        request.setupAddParameter("One", "1");
        request.setupAddParameter("Two", "<Cut & Dry>");

        // KDH can't be sure in what order the parameters will come out
        String url = ServletUtils.buildSyntheticRequestUrl(request);
        assertTrue(url, "http://example.com/test?Two=%3CCut+%26+Dry%3E&One=1".equals(url)
                        || "http://example.com/test?One=1&Two=%3CCut+%26+Dry%3E".equals(url));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testQueryStringFromParameters()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter("One", "1");
        request.setupAddParameter("Two", "<Cut & Dry>");

        //  can't be sure in what order the parameters will come out
        String queryString = ServletUtils.queryStringFromParameters(request);
        assertTrue(queryString, "Two=%3CCut+%26+Dry%3E&One=1".equals(queryString)
                                || "One=1&Two=%3CCut+%26+Dry%3E".equals(queryString));

        queryString = ServletUtils.queryStringFromParameters(request.getParameterMap());
        assertTrue(queryString, "Two=%3CCut+%26+Dry%3E&One=1".equals(queryString)
                                || "One=1&Two=%3CCut+%26+Dry%3E".equals(queryString));
    }
    
    @Test
    public void testFormatDate()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(Calendar.YEAR, 2010);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 9);
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 12);
        calendar.set(Calendar.SECOND, 13);
        
        assertEquals("Sat, 09 Jan 2010 11:12:13 GMT", ServletUtils.formatHttpDate(calendar.getTime()));
    }
    
    @Test
    public void testParseDate()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(Calendar.YEAR, 2010);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 9);
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 12);
        calendar.set(Calendar.SECOND, 13);
        calendar.set(Calendar.MILLISECOND, 0);
        
        Date correctDate = calendar.getTime();
        
        String[] formats =
        {
         "Sat, 09 Jan 2010 11:12:13 GMT",
         "Sat, 09 Jan 2010 11:12:13GMT",
         "Sat, 09 Jan 2010 11:12:13+0000",
         "Sat, 09 Jan 2010 06:12:13-0500",
         "Sat, 09 Jan 2010 11:12:13 +0000",
         "Sat, 09 Jan 2010 06:12:13 -0500",
         "Saturday, 09-Jan-10 11:12:13 GMT",
         "Saturday, 09-Jan-10 11:12:13+0000",
         "Sat Jan  9 11:12:13 2010"
        };
        
        for (int i = 0; i < formats.length; i++)
        {
            String test = "formats["+i+"]";
            Date parsedDate = ServletUtils.parseHttpDate(formats[i]);
            assertNotNull(test, parsedDate);
            assertEquals(test, correctDate, parsedDate);
        }
        
        assertNull(ServletUtils.parseHttpDate(null));
        assertNull(ServletUtils.parseHttpDate("bogus string"));
    }

    @Test
    public void testCoverage()
    {
        ServletUtils.coverage();
    }

}
