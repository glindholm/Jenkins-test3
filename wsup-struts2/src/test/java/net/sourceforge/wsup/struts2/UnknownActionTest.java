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

package net.sourceforge.wsup.struts2;

import static junit.framework.Assert.*;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.wsup.struts2.test.StrutsTestContext;

import org.apache.struts2.dispatcher.Dispatcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.impl.MockLogger;

import com.google.inject.servlet.GuiceFilter;
import com.mockrunner.mock.web.MockServletContext;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

public class UnknownActionTest
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
    public void testExecute() throws Exception
    {
        MockLogger.get(UnknownAction.class).printOff();
        MockServletContext servletContext = new MockServletContext();
        Dispatcher dispatcher = StrutsTestContext.prepareDispatcher(servletContext, null);

        StrutsTestContext context = new StrutsTestContext(dispatcher, servletContext);

        Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, Object> sessionAttributes = new HashMap<String, Object>();

        ActionProxy proxy = context.createActionProxy("/struts2-testing",
                                                      "unknownxxxx",
                                                      requestParams,
                                                      sessionAttributes);
        context.getMockRequest()
            .setRequestURL("http://example.com/struts2-testing/unknownxxxx.action");

        assertEquals("execute", proxy.getMethod());
        assertTrue(proxy.getAction() instanceof UnknownAction);
        UnknownAction action = (UnknownAction) proxy.getAction();

        MockLogger.get(UnknownAction.class).setError();

        assertEquals(Action.SUCCESS, proxy.execute());

        assertTrue(action.getActionErrors().toString()
            .contains("http://example.com/struts2-testing/unknownxxxx.action"));

        assertTrue(MockLogger.getFilteredLogEntries(UnknownAction.class).get(0).getMessage()
            .contains("http://example.com/struts2-testing/unknownxxxx.action"));

    }

}
