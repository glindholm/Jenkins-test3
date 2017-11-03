/*
 * Copyright (c) 2010 Greg Lindholm
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

package net.sourceforge.wsup.struts2.interceptor;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import net.sourceforge.wsup.struts2.test.StrutsTestContext;
import net.sourceforge.wsup.struts2.test.StrutsTestUtils;

import org.apache.struts2.dispatcher.Dispatcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.impl.MockLogger;
import org.slf4j.impl.MockLogger.LogEntry;

import com.google.inject.servlet.GuiceFilter;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletContext;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

public class RedirectMessageInterceptorTest
{

    private static MockServletContext servletContext;
    private static Dispatcher         dispatcher;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        servletContext = new MockServletContext();
        dispatcher = StrutsTestContext.prepareDispatcher(servletContext, null);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
        MockLogger.resetAll();
    }

    @After
    public void tearDown() throws Exception
    {
        new GuiceFilter().destroy();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRedirectAction() throws Exception
    {
        /*
         * First parts test saving the messages into the session
         */
        StrutsTestContext context = new StrutsTestContext(dispatcher, servletContext);

        Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, Object> sessionAttributes = new HashMap<String, Object>();

        ActionProxy proxy = context.createActionProxy("/struts2-testing",
                                                      "testRedirectActionBefore",
                                                      requestParams,
                                                      sessionAttributes);

        assertEquals("doBefore", proxy.getMethod());
        assertTrue(proxy.getAction() instanceof TestRedirectMessageInterceptorAction);
        assertEquals(Action.SUCCESS, proxy.execute());

        MockHttpSession session = context.getMockSession();

        Collection<String> actionErrors = (Collection<String>) session
            .getAttribute(RedirectMessageInterceptor.ACTION_ERRORS_KEY);

        Collection<String> actionMessages = (Collection<String>) session
            .getAttribute(RedirectMessageInterceptor.ACTION_MESSAGES_KEY);

        Map<String, List<String>> fieldErrors = (Map<String, List<String>>) session
            .getAttribute(RedirectMessageInterceptor.FIELD_ERRORS_KEY);

        assertEquals("[ActionError1, ActionError2]", actionErrors.toString());
        assertEquals("[ActionMessage1, ActionMessage2]", actionMessages.toString());
        assertEquals("[Field1 Error1, Field1 Error2]", fieldErrors.get("field1").toString());
        assertEquals("[Field2 Error1, Field2 Error2]", fieldErrors.get("field2").toString());

        assertEquals(true, context.getMockResponse().wasRedirectSent());
        assertEquals("/struts2-testing/testRedirectActionAfter.action", context.getMockResponse()
            .getHeader("Location"));

        /*
         * Second part test retrieving the messages from the session
         */
        sessionAttributes.clear();
        sessionAttributes.put(RedirectMessageInterceptor.ACTION_ERRORS_KEY, actionErrors);
        sessionAttributes.put(RedirectMessageInterceptor.ACTION_MESSAGES_KEY, actionMessages);
        sessionAttributes.put(RedirectMessageInterceptor.FIELD_ERRORS_KEY, fieldErrors);

        proxy = context.createActionProxy("/struts2-testing",
                                          "testRedirectActionAfter",
                                          requestParams,
                                          sessionAttributes);

        assertEquals("doAfter", proxy.getMethod());
        assertTrue(proxy.getAction() instanceof TestRedirectMessageInterceptorAction);
        TestRedirectMessageInterceptorAction action = (TestRedirectMessageInterceptorAction) proxy
            .getAction();
        assertEquals(Action.INPUT, proxy.execute());

        session = context.getMockSession();
        assertNull(session.getAttribute(RedirectMessageInterceptor.ACTION_ERRORS_KEY));
        assertNull(session.getAttribute(RedirectMessageInterceptor.ACTION_MESSAGES_KEY));
        assertNull(session.getAttribute(RedirectMessageInterceptor.FIELD_ERRORS_KEY));

        assertEquals("[ActionError1, ActionError2]", action.getActionErrors().toString());
        assertEquals("[ActionMessage1, ActionMessage2]", action.getActionMessages().toString());
        assertEquals("[Field1 Error1, Field1 Error2]", action.getFieldErrors().get("field1")
            .toString());
        assertEquals("[Field2 Error1, Field2 Error2]", action.getFieldErrors().get("field2")
            .toString());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRedirect() throws Exception
    {
        /*
         * First parts test saving the messages into the session
         */
        StrutsTestContext context = new StrutsTestContext(dispatcher, servletContext);

        Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, Object> sessionAttributes = new HashMap<String, Object>();

        ActionProxy proxy = context.createActionProxy("/struts2-testing",
                                                      "testRedirectBefore",
                                                      requestParams,
                                                      sessionAttributes);

        assertEquals("doBefore", proxy.getMethod());
        assertTrue(proxy.getAction() instanceof TestRedirectMessageInterceptorAction);
        assertEquals(Action.SUCCESS, proxy.execute());

        MockHttpSession session = context.getMockSession();

        Collection<String> actionErrors = (Collection<String>) session
            .getAttribute(RedirectMessageInterceptor.ACTION_ERRORS_KEY);

        Collection<String> actionMessages = (Collection<String>) session
            .getAttribute(RedirectMessageInterceptor.ACTION_MESSAGES_KEY);

        Map<String, List<String>> fieldErrors = (Map<String, List<String>>) session
            .getAttribute(RedirectMessageInterceptor.FIELD_ERRORS_KEY);

        assertEquals("[ActionError1, ActionError2]", actionErrors.toString());
        assertEquals("[ActionMessage1, ActionMessage2]", actionMessages.toString());
        assertEquals("[Field1 Error1, Field1 Error2]", fieldErrors.get("field1").toString());
        assertEquals("[Field2 Error1, Field2 Error2]", fieldErrors.get("field2").toString());

        assertEquals(true, context.getMockResponse().wasRedirectSent());
        assertEquals("/struts2-testing/testRedirectAfter.action", context.getMockResponse()
            .getHeader("Location"));

        /*
         * Second part test retrieving the messages from the session
         */
        sessionAttributes.clear();
        sessionAttributes.put(RedirectMessageInterceptor.ACTION_ERRORS_KEY, actionErrors);
        sessionAttributes.put(RedirectMessageInterceptor.ACTION_MESSAGES_KEY, actionMessages);
        sessionAttributes.put(RedirectMessageInterceptor.FIELD_ERRORS_KEY, fieldErrors);

        proxy = context.createActionProxy("/struts2-testing",
                                          "testRedirectAfter",
                                          requestParams,
                                          sessionAttributes);

        assertEquals("doAfter", proxy.getMethod());
        assertTrue(proxy.getAction() instanceof TestRedirectMessageInterceptorAction);
        TestRedirectMessageInterceptorAction action = (TestRedirectMessageInterceptorAction) proxy
            .getAction();
        assertEquals(Action.INPUT, proxy.execute());

        session = context.getMockSession();
        assertNull(session.getAttribute(RedirectMessageInterceptor.ACTION_ERRORS_KEY));
        assertNull(session.getAttribute(RedirectMessageInterceptor.ACTION_MESSAGES_KEY));
        assertNull(session.getAttribute(RedirectMessageInterceptor.FIELD_ERRORS_KEY));

        assertEquals("[ActionError1, ActionError2]", action.getActionErrors().toString());
        assertEquals("[ActionMessage1, ActionMessage2]", action.getActionMessages().toString());
        assertEquals("[Field1 Error1, Field1 Error2]", action.getFieldErrors().get("field1")
            .toString());
        assertEquals("[Field2 Error1, Field2 Error2]", action.getFieldErrors().get("field2")
            .toString());

    }

    @Test
    public void testNoSessionFail() throws Exception
    {
        /*
         * This simulates when there is no session and the response has been committed
         */

        /*
         * Create a proxy with a special session that throws an exception
         */
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession()
        {
            @Override
            public synchronized void setAttribute(String key, Object value)
            {
                throw new IllegalStateException("bad session");
            }

        };
        mockRequest.setSession(mockSession);
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        StrutsTestUtils.prepareMockRequestDefaults(mockRequest,
                                                   "/struts2-testing",
                                                   "testRedirectActionBefore",
                                                   null);
        ActionProxy proxy = StrutsTestContext.createActionProxy(dispatcher,
                                                                "/struts2-testing",
                                                                "testRedirectActionBefore",
                                                                mockRequest,
                                                                mockResponse,
                                                                servletContext);

        assertEquals("doBefore", proxy.getMethod());
        assertTrue(proxy.getAction() instanceof TestRedirectMessageInterceptorAction);

        MockLogger.get(RedirectMessageInterceptor.class).setWarn().printOff();

        assertEquals(Action.SUCCESS, proxy.execute());

        List<LogEntry> logEntries = MockLogger
            .getFilteredLogEntries(RedirectMessageInterceptor.class.getName());

        Assert.assertEquals(1, logEntries.size());
        Assert.assertTrue(logEntries.get(0).getMessage(),
                          logEntries.get(0).getMessage().contains("Storing messages"));
        Assert.assertTrue(logEntries.get(0).getMessage(),
                          logEntries.get(0).getMessage()
                              .contains("/struts2-testing/testRedirectActionBefore"));

        Assert.assertEquals("IllegalStateException", logEntries.get(0).getThrowable().getClass()
            .getSimpleName());
        Assert.assertEquals("bad session", logEntries.get(0).getThrowable().getMessage());

    }

}
