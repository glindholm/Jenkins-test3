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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import net.sourceforge.wsup.struts2.test.StrutsTestContext;

import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.impl.MockLogger;
import org.slf4j.impl.MockLogger.LogEntry;

import com.google.inject.servlet.GuiceFilter;
import com.mockrunner.mock.web.MockServletContext;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

public class LoggingInterceptorTest
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
        MockLogger.resetAll();
    }

    @After
    public void tearDown() throws Exception
    {
        new GuiceFilter().destroy();
    }

    @Test
    public void testSetSecureParams()
    {
        LoggingInterceptor interceptor = new LoggingInterceptor();

        interceptor.setSecureParams("Password,Passwd");

        Assert.assertTrue(interceptor.getSecureParamsSet().contains("password"));
        Assert.assertTrue(interceptor.getSecureParamsSet().contains("passwd"));
        Assert.assertEquals(2, interceptor.getSecureParamsSet().size());
    }

    @Test
    public void testIntercept() throws Exception
    {
        MockLogger.get(LoggingInterceptor.class).setInfo().printOff();

        MockServletContext servletContext = new MockServletContext();
        Dispatcher dispatcher = StrutsTestContext.prepareDispatcher(servletContext, null);

        StrutsTestContext context = new StrutsTestContext(dispatcher, servletContext);

        Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, Object> sessionAttributes = new HashMap<String, Object>();

        requestParams.put("name", "Test Name");
        requestParams.put("password", "MySecretPassword");

        ActionProxy proxy = context.createActionProxy("/struts2-testing",
                                                      "testLogging",
                                                      requestParams,
                                                      sessionAttributes);

        Assert.assertEquals("execute", proxy.getMethod());

        TestLoggingInterceptorAction action = (TestLoggingInterceptorAction) proxy.getAction();

        Assert.assertEquals(Action.SUCCESS, proxy.execute());

        Assert.assertEquals("Test Name", action.getName());

        List<LogEntry> logEntries = MockLogger.getFilteredLogEntries(LoggingInterceptor.class
            .getName());

        Assert.assertEquals(2, logEntries.size());
        Assert.assertTrue(logEntries.get(0).getMessage(), logEntries.get(0).getMessage()
            .contains("/struts2-testing/testLogging"));
        Assert.assertTrue(logEntries.get(0).getMessage(), logEntries.get(0).getMessage()
            .contains(TestLoggingInterceptorAction.class.getName() + ".execute()"));
        Assert.assertTrue(logEntries.get(0).getMessage(), logEntries.get(0).getMessage()
            .contains("name=Test Name"));
        Assert.assertTrue(logEntries.get(0).getMessage(), logEntries.get(0).getMessage()
            .contains("password=****"));

        Assert.assertTrue(logEntries.get(1).getMessage(), logEntries.get(1).getMessage()
            .contains("/struts2-testing/testLogging"));
        Assert.assertTrue(logEntries.get(1).getMessage(), logEntries.get(1).getMessage()
            .contains("result=success"));
        Assert.assertTrue(logEntries.get(1).getMessage(), logEntries.get(1).getMessage()
            .contains("type=" + ServletDispatcherResult.class.getSimpleName()));

    }

    @Test
    public void testCoverage()
    {
        LoggingInterceptor interceptor = new LoggingInterceptor();
        interceptor.init();
        interceptor.destroy();
    }

}
