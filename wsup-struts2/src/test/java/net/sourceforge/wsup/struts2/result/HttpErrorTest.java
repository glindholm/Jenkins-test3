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

package net.sourceforge.wsup.struts2.result;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import net.sourceforge.wsup.struts2.test.StrutsTestContext;

import org.apache.struts2.dispatcher.Dispatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.impl.MockLogger;

import com.google.inject.servlet.GuiceFilter;
import com.mockrunner.mock.web.MockServletContext;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

public class HttpErrorTest
{

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
        MockLogger.resetAll();
        MockLogger.get("com.opensymphony.xwork2.ognl.OgnlValueStack").setError();

    }

    @After
    public void tearDown() throws Exception
    {
        new GuiceFilter().destroy();
    }

    @Test
    public void testHttpErrorNoStatus() throws Exception
    {
        MockServletContext servletContext = new MockServletContext();
        Dispatcher dispatcher = StrutsTestContext.prepareDispatcher(servletContext, null);

        StrutsTestContext context = new StrutsTestContext(dispatcher, servletContext);

        Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, Object> sessionAttributes = new HashMap<String, Object>();

        ActionProxy proxy = context.createActionProxy("/struts2-testing",
                                                      "servererror",
                                                      requestParams,
                                                      sessionAttributes);

        Assert.assertEquals("execute", proxy.getMethod());

        Assert.assertEquals(Action.SUCCESS, proxy.execute());

        Assert.assertEquals(true, context.getMockResponse().wasErrorSent());
        Assert.assertEquals(200, context.getMockResponse().getErrorCode());

        Assert.assertEquals("AAA", context.getMockResponse().getHeader("aaa"));
        Assert
            .assertEquals("The Custom A value", context.getMockResponse().getHeader("X-CUSTOM-A"));
        Assert
            .assertEquals("The Custom B value", context.getMockResponse().getHeader("X-CUSTOM-B"));
    }

    @Test
    public void testHttpErrorStatus200() throws Exception
    {
        MockServletContext servletContext = new MockServletContext();
        Dispatcher dispatcher = StrutsTestContext.prepareDispatcher(servletContext, null);

        StrutsTestContext context = new StrutsTestContext(dispatcher, servletContext);

        Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, Object> sessionAttributes = new HashMap<String, Object>();
        requestParams.put("status", "200");
        ActionProxy proxy = context.createActionProxy("/struts2-testing",
                                                      "servererror",
                                                      requestParams,
                                                      sessionAttributes);

        Assert.assertEquals("execute", proxy.getMethod());

        Assert.assertEquals(Action.SUCCESS, proxy.execute());

        Assert.assertEquals(true, context.getMockResponse().wasErrorSent());
        Assert.assertEquals(200, context.getMockResponse().getErrorCode());

        Assert.assertEquals("AAA", context.getMockResponse().getHeader("aaa"));
        Assert
            .assertEquals("The Custom A value", context.getMockResponse().getHeader("X-CUSTOM-A"));
        Assert
            .assertEquals("The Custom B value", context.getMockResponse().getHeader("X-CUSTOM-B"));
    }

    @Test
    public void testHttpErrorError() throws Exception
    {
        MockServletContext servletContext = new MockServletContext();
        Dispatcher dispatcher = StrutsTestContext.prepareDispatcher(servletContext, null);

        StrutsTestContext context = new StrutsTestContext(dispatcher, servletContext);

        Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, Object> sessionAttributes = new HashMap<String, Object>();

        requestParams.put("status", "500");
        requestParams.put("message", "Server is down");

        ActionProxy proxy = context.createActionProxy("/struts2-testing",
                                                      "servererror",
                                                      requestParams,
                                                      sessionAttributes);

        Assert.assertEquals("execute", proxy.getMethod());

        Assert.assertEquals(Action.ERROR, proxy.execute());

        Assert.assertEquals(true, context.getMockResponse().wasErrorSent());
        Assert.assertEquals(500, context.getMockResponse().getErrorCode());
    }

    @Test
    public void testHttpErrorErrorNoMsg() throws Exception
    {
        MockServletContext servletContext = new MockServletContext();
        Dispatcher dispatcher = StrutsTestContext.prepareDispatcher(servletContext, null);

        StrutsTestContext context = new StrutsTestContext(dispatcher, servletContext);

        Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, Object> sessionAttributes = new HashMap<String, Object>();

        requestParams.put("status", "505");

        ActionProxy proxy = context.createActionProxy("/struts2-testing",
                                                      "servererror",
                                                      requestParams,
                                                      sessionAttributes);

        Assert.assertEquals("execute", proxy.getMethod());

        Assert.assertEquals(Action.ERROR, proxy.execute());

        Assert.assertEquals(true, context.getMockResponse().wasErrorSent());
        Assert.assertEquals(505, context.getMockResponse().getErrorCode());
    }

    @Test(expected = Exception.class)
    public void testExceptions() throws Exception
    {
        MockServletContext servletContext = new MockServletContext();
        Dispatcher dispatcher = StrutsTestContext.prepareDispatcher(servletContext, null);

        StrutsTestContext context = new StrutsTestContext(dispatcher, servletContext);

        Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, Object> sessionAttributes = new HashMap<String, Object>();

        requestParams.put("status", "not a number");

        ActionProxy proxy = context.createActionProxy("/struts2-testing",
                                                      "servererror",
                                                      requestParams,
                                                      sessionAttributes);

        Assert.assertEquals("execute", proxy.getMethod());

        proxy.execute();
    }
}
