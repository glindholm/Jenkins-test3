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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockServletContext;

public class ActionTransactionFactoryTest
{
    private static ActionTransactionFactory factory;

    public ActionTransactionFactoryTest()
    {
    }

    @BeforeClass
    public static void oneTimeSetup()
    {
        factory = new ActionTransactionFactory();
        MockServletContext context = new MockServletContext();
        assertFalse(factory.isInitialized());
        factory.initialize(context, null);
        assertTrue(factory.isInitialized());
    }
    
    @Test
    public void coverInitWithParams()
    {
        MockServletContext context = new MockServletContext();
        new ActionTransactionFactory().initialize(context, new HashMap<String,String>());
    }

    @Test
    public void testParamsViaArray() throws Exception
    {
        String[] params = { "name", "theName", "desc", "theDesc" };

        ActionTransaction<Test1Action> transaction = factory
            .createTransaction(Test1Action.class,
                               "/struts2-test-testing",
                               "test1",
                               params,
                               null,
                               null);

        assertEquals("success", transaction.getActionProxy().execute());

        Test1Action action = transaction.getAction();

        assertEquals("theName", action.getName());
        assertEquals("theDesc", action.getDesc());

        /*
         * we didn't create a session by passing in session params,
         * and the action isn't session aware, so no session should
         * have been created
         */

        assertNull(transaction.getRequest().getSession(false));
        assertEquals(200, transaction.getResponse().getStatusCode());
    }

    @Test
    public void testParamsViaMap() throws Exception
    {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("name", "theName");
        params.put("desc", "theDesc");

        ActionTransaction<Test1Action> transaction = factory
            .createTransaction(Test1Action.class,
                               "/struts2-test-testing",
                               "test1",
                               params,
                               null,
                               null);

        assertEquals("success", transaction.getActionProxy().execute());

        Test1Action action = transaction.getAction();

        assertEquals("theName", action.getName());
        assertEquals("theDesc", action.getDesc());

        /*
         * we didn't create a session by passing in session params,
         * and the action isn't session aware, so no session should
         * have been created
         */

        assertNull(transaction.getRequest().getSession(false));
        assertEquals(200, transaction.getResponse().getStatusCode());
    }

    @Test
    public void testSessionOutput() throws Exception
    {
        String[] params = { "name", "theKey", "value", "theValue" };

        ActionTransaction<Test2Action> transaction = factory
            .createTransaction(Test2Action.class,
                               "/struts2-test-testing",
                               "test2",
                               params,
                               null,
                               null);

        assertEquals("session", transaction.execute());

        /*
         * We created this request without a session.
         * Because we put stuff in the session in the Action, there
         * should be a session now.
         */

        assertNotNull(transaction.getRequest().getSession(false));
        HttpSession session = transaction.getSession();
        assertEquals("theValue", session.getAttribute("theKey"));
    }

    @Test
    public void testSessionCreatedInSetup() throws Exception
    {
        String[] params = { "name", "theKey", "value", "theValue" };

        Map<String, Object> sessionAttributes = new HashMap<String, Object>();
        sessionAttributes.put("foo", "bar");

        ActionTransaction<Test3Action> transaction = factory
            .createTransaction(Test3Action.class,
                               "/struts2-test-testing",
                               "test3",
                               params,
                               null,
                               sessionAttributes);

        assertEquals("success", transaction.execute());

        /*
         * We created this request without a session.
         * Because we put stuff in the session in the Action, there
         * should be a session now.
         */

        assertNotNull(transaction.getRequest().getSession(false));
        HttpSession session = transaction.getSession();
        assertEquals("theValue", session.getAttribute("theKey"));
        assertEquals("bar", session.getAttribute("foo"));
    }

    @Test
    public void testHeaders() throws Exception
    {
        String[] headers = { "header-name", "header-value" };

        ActionTransaction<Test4Action> transaction = factory
            .createTransaction(Test4Action.class,
                               "/struts2-test-testing",
                               "test4",
                               null,
                               headers,
                               null);

        assertEquals("success", transaction.execute());

        assertNotNull(transaction.getRequest().getSession(false));
        HttpSession session = transaction.getSession();
        assertEquals("header-value", session.getAttribute("name"));
    }

    @Test
    public void testCookies() throws Exception
    {
        Cookie[] cookies = { new Cookie("name1", "value1") };

        ActionTransaction<Test4Action> transaction = factory
            .createTransaction(Test4Action.class,
                               "/struts2-test-testing",
                               "test4",
                               null,
                               cookies,
                               null,
                               null);

        MockHttpServletRequest request = transaction.getRequest();
        Cookie[] cookieList = request.getCookies();
        assertEquals("name1", cookieList[0].getName());
        assertEquals("value1", cookieList[0].getValue());
    }
}
