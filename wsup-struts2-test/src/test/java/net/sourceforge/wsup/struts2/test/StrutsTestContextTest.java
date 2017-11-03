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

package net.sourceforge.wsup.struts2.test;

import static junit.framework.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.dispatcher.Dispatcher;
import org.junit.Test;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockServletContext;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

public class StrutsTestContextTest
{

    @Test
    public void testStrutsTestContext() throws Exception
    {

        MockServletContext servletContext = new MockServletContext();
        Dispatcher dispatcher = StrutsTestContext.prepareDispatcher(servletContext, null);

        StrutsTestContext context = new StrutsTestContext(dispatcher, servletContext);

        Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, Object> sessionAttributes = new HashMap<String, Object>();
        requestParams.put("name", "Test Name");

        ActionProxy proxy = context.createActionProxy("/struts2-test-testing",
                                                      "test1",
                                                      requestParams,
                                                      sessionAttributes);

        MockHttpServletRequest request = context.getMockRequest();

        assertEquals("/struts2-test-testing/test1.action", request.getRequestURI());
        assertEquals("http://localhost/struts2-test-testing/test1.action", request.getRequestURL()
            .toString());
        assertEquals("name=Test+Name", request.getQueryString());

        assertEquals("execute", proxy.getMethod());

        Test1Action action = (Test1Action) proxy.getAction();

        assertEquals(Action.SUCCESS, proxy.execute());

        assertEquals("Test Name", action.getName());
    }
}
