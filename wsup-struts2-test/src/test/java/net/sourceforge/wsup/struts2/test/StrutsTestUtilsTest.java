/*
 * Copyright (c) 2010 Kevin Hunter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sourceforge.wsup.struts2.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;

import org.junit.Test;

import com.mockrunner.mock.web.MockServletContext;

public class StrutsTestUtilsTest
{
    public StrutsTestUtilsTest()
    {
    }

    @Test
    public void coverDispatcher1()
    {
        MockServletContext servletContext = new MockServletContext();
        assertNotNull(StrutsTestUtils.prepareDispatcher(servletContext, null));
    }

    @Test
    public void coverDispatcher2()
    {
        MockServletContext servletContext = new MockServletContext();
        HashMap<String, String> params = new HashMap<String, String>();
        assertNotNull(StrutsTestUtils.prepareDispatcher(servletContext, params));
    }

    @Test
    public void testMapToStringArray()
    {
        assertNull(StrutsTestUtils.mapToStringArray(null));

        String[] one = StrutsTestUtils.mapToStringArray(new HashMap<String, String>());
        assertNotNull(one);
        assertEquals(0, one.length);

        HashMap<String, String> input = new HashMap<String, String>();
        input.put("key1", "value1");
        input.put("key2", "value2");

        String[] two = StrutsTestUtils.mapToStringArray(input);
        assertEquals(4, two.length);

        if (two[0].equals("key1"))
        {
            assertEquals("value1", two[1]);
            assertEquals("key2", two[2]);
            assertEquals("value2", two[3]);
        }
        else
        {
            assertEquals("key2", two[0]);
            assertEquals("value2", two[1]);
            assertEquals("key1", two[2]);
            assertEquals("value1", two[3]);
        }
    }

    @Test
    public void testQueryString1()
    {
        String[] params = { "key1", "value1", "key2", "value 2" };

        String result = StrutsTestUtils.synthesizeQueryString(params);

        assertEquals("key1=value1&key2=value+2", result);
    }

    @Test(expected = AssertionError.class)
    public void testQueryString2()
    {
        String[] params = { "key1", "value1", "key2", "value 2" };

        StrutsTestUtils.synthesizeQueryString(params, "NO-SUCH-CHAR-SET");
    }

    private static final String[][] URI_CASES = 
    {
        // namespace, actionName, expected result
        { null, "Action", "/Action.action" },
        { "", "Action", "/Action.action" },
        { "/", "Action", "/Action.action" },
        { "namespace", "Action", "/namespace/Action.action" },
        { "/namespace", "Action", "/namespace/Action.action" },
        { "/namespace/", "Action", "/namespace/Action.action" }, 
    };

    @Test
    public void testRequestUri()
    {
        for (int i = 0; i < URI_CASES.length; i++)
        {
            String test = "URI_CASES[" + i + "]";
            String namespace = URI_CASES[i][0];
            String actionName = URI_CASES[i][1];
            String expected = URI_CASES[i][2];

            assertEquals(test, expected, StrutsTestUtils
                .synthesizeRequestUri(namespace, actionName));
        }
    }

    @Test
    public void completeCoverage()
    {
        StrutsTestUtils.coverage();
    }
}
