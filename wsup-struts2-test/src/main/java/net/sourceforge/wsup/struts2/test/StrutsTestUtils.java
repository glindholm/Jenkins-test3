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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

/**
 * Utility methods used to help test Struts <code>Action</code>s.
 * 
 * @author Kevin Hunter
 * 
 */
public class StrutsTestUtils
{
    /**
     * Utility method to prepare a <code>Dispatcher</code> that has either a
     * <code>ServletContext</code> or initialization parameters.
     * <p>
     * Creating a <code>Dispatcher</code> is a moderately expensive operation, so the
     * <code>Dispatcher</code> should be reused where possible.
     * </p>
     * 
     * @param servletContext <code>ServletContext</code> object. May be <code>null</code>.
     * @param params Initialization parameters for the context. May be <code>null</code>.
     * @return <code>Dispatcher</code> instance.
     */
    public static Dispatcher prepareDispatcher(ServletContext servletContext,
                                               Map<String, String> params)
    {
        if (params == null)
        {
            params = new HashMap<String, String>();
        }
        Dispatcher dispatcher = new Dispatcher(servletContext, params);
        dispatcher.init();
        Dispatcher.setInstance(dispatcher);
        return dispatcher;
    }

    /**
     * Utility method that creates an <code>ActionProxy</code> for a particular
     * Struts action.
     * 
     * @param dispatcher <code>Dispatcher</code> instance
     * @param namespace Namespace for the action. Can be <code>null</code>.
     * @param actionName Name of the action.
     * @param request <code>HttpServletRequest</code> containing request information
     * @param response <code>HttpServletReponse</code> object into which the response will be put
     * @param servletContext <code>ServletContext</code> for the request
     * @return <code>ActionProxy</code> instance through which the action can be run
     * @throws Exception
     */
    public static ActionProxy createActionProxy(Dispatcher dispatcher,
                                                String namespace,
                                                String actionName,
                                                HttpServletRequest request,
                                                HttpServletResponse response,
                                                ServletContext servletContext) throws Exception
    {
        Container container = dispatcher.getContainer();
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        ActionContext.setContext(new ActionContext(stack.getContext()));

        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ServletActionContext.setServletContext(servletContext);

        ActionMapping mapping = null;
        return dispatcher.getContainer().getInstance(ActionProxyFactory.class)
            .createActionProxy(namespace,
                               actionName,
                               (String) null,
                               dispatcher.createContextMap(request,
                                                           response,
                                                           mapping),
                               true, // execute result
                               false); // cleanupContext
    }

    /**
     * Setup a mock request with reasonable defaults for things that Struts or an action may
     * commonly attempt to extract. The <code>namespace</code> and <code>actionName</code> are used
     * to synthesize a URI and the <code>requestParams</code> are used to synthesize the
     * QueryString.
     * 
     * @param mockRequest
     * @param namespace
     * @param actionName
     * @param requestParams
     */
    public static void prepareMockRequestDefaults(MockHttpServletRequest mockRequest,
                                                  String namespace,
                                                  String actionName,
                                                  String[] requestParams)
    {
        /*
         * Setup reasonable default values for common request attributes
         */
        mockRequest.setContextPath("");
        mockRequest.setPathInfo("");

        // synthesize the requestUri
        String requestUri = synthesizeRequestUri(namespace, actionName);
        mockRequest.setRequestURI(requestUri);
        mockRequest.setRequestURL("http://localhost" + requestUri);
        mockRequest.setQueryString(synthesizeQueryString(requestParams));

        mockRequest.setRemoteHost("127.0.0.1");
        mockRequest.setServerName("localhost");
        mockRequest.setServerPort(80);

    }

    /**
     * Synthesize a requestUri from the <code>namespace</code> and <code>actionName</code>. The
     * result will take the form <code>/namespace/actionName.action</code>.
     * 
     * @param namespace Namespace string.  May be <code>null</code>.
     * @param actionName    Action name string.  May not be <code>null</code>.
     * @return the synthesized requestUri
     */
    public static String synthesizeRequestUri(String namespace, String actionName)
    {
        StringBuilder requestUri = new StringBuilder();

        if (namespace != null)
        {
            if (!namespace.startsWith("/"))
            {
                requestUri.append('/');
            }
            requestUri.append(namespace);
        }

        if (requestUri.length() == 0 || requestUri.charAt(requestUri.length() - 1) != '/')
        {
            requestUri.append('/');
        }

        requestUri.append(actionName).append(".action");

        return requestUri.toString();
    }

    /**
     * Synthesize a queryString from the <code>requestParams</code>
     * 
     * @param requestParams Request parameters
     * @return the synthesized queryString
     */
    public static String synthesizeQueryString(String[] requestParams)
    {
        return synthesizeQueryString(requestParams, "UTF-8");
    }

    /**
     * Synthesize a queryString from the <code>requestParams</code>
     * 
     * @param requestParams Request parameters
     * @param valueEncoding Encoding method for values
     * @return the synthesized queryString
     */
    public static String synthesizeQueryString(String[] requestParams, String valueEncoding)
    {
        StringBuilder queryString = new StringBuilder();

        if (requestParams != null)
        {
            for (int i = 0; i < requestParams.length; i += 2)
            {
                if (i > 0)
                {
                    queryString.append('&');
                }

                queryString.append(requestParams[i]);
                queryString.append('=');
                try
                {
                    queryString.append(URLEncoder.encode(requestParams[i + 1], valueEncoding));
                }
                catch (UnsupportedEncodingException e)
                {
                    throw new AssertionError(e.getMessage());
                }
            }
        }

        return queryString.toString();
    }

    /**
     * Convert a <code>Map</code> of string pairs to an array of <code>String</code>s
     * in key,value order.
     * 
     * @param input Input <code>Map</code>.
     * @return <code>null</code> if the input is <code>null</code>, otherwise
     *         an array of <code>String</code>s in key,value order.
     */
    public static String[] mapToStringArray(Map<String, String> input)
    {
        if (input == null)
        {
            return null;
        }

        String[] result = new String[input.size() * 2];

        int i = 0;
        for (Map.Entry<String, String> item : input.entrySet())
        {
            result[i++] = item.getKey();
            result[i++] = item.getValue();
        }

        return result;
    }

    /* package */static void coverage()
    {
        new StrutsTestUtils();
    }

    private StrutsTestUtils()
    {
    }
}
