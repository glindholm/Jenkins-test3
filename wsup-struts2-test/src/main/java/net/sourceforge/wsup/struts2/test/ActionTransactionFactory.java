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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;

import org.apache.struts2.dispatcher.Dispatcher;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

/**
 * This class serves as a factory through which * instances of <code>ActionTransaction</code> can be
 * created for testing Struts <code>Action</code> classes. This class provides an alternate
 * mechanism
 * to <code>StrutsTestContext</code>.
 * <p>
 * The normal sequence of operations is as follows:
 * </p>
 * <ol>
 * <li>The JUnit test class contains an instance of this class. There may be one per test class, or
 * the instance could be static and shared across multiple test classes, depending on the
 * initialization needs.</li>
 * <li>The factory object is initialized with an appropriate <code>ServletContext</code> (or
 * <code>null</code>) and parameters.</li>
 * <li>Each individual unit test for an <code>Action</code> calls <code>createTransaction</code> to
 * create an <code>ActionTransaction</code> object.</li>
 * <li>If necessary, the unit test manipulates the <code>ActionTransaction</code> object's
 * <code>request</code> and <code>session</code> members. (Typically, this isn't necessary, since
 * these objects can be set up by parameters to <code>createTransaction</code>.)</li>
 * <li>
 * The <code>ActionTransaction</code>'s <code>execute</code> member is called, and the resulting
 * <code>String</code> checked for accuracy.</li>
 * <li>The unit test accesses the <code>Action</code> to check any of its properties used by the
 * rendering process.</li>
 * <li>The unit test accesses, if necessary, the <code>ActionTransaction</code>'s
 * <code>session</code> and <code>response</code> objects to check that the appropriate information
 * has been left behind/output.</li>
 * </ol>
 * 
 * @author Kevin Hunter
 * 
 */
public class ActionTransactionFactory
{
    private Dispatcher     dispatcher;
    private ServletContext servletContext;

    /**
     * Constructor.
     */
    public ActionTransactionFactory()
    {
    }

    /**
     * Initialize the factory instance. This builds the underlying
     * Struts <code>Dispatcher</code> object, which is a moderately
     * expensive operation.
     * 
     * @param context <code>ServletContext</code> or <code>MockServletContext</code> instance
     *            providing the contextual information for Struts.
     *            May be <code>null</code> if no context support is required.
     * @param params Initialization parameters, if any. May be <code>null</code>.
     */
    public void initialize(ServletContext context, Map<String, String> params)
    {
        servletContext = context;
        if (params == null)
        {
            params = new HashMap<String, String>();
        }
        dispatcher = new Dispatcher(servletContext, params);
        dispatcher.init();
        Dispatcher.setInstance(dispatcher);
    }

    /**
     * Indicates if the instance has been initialized.
     * 
     * @return <code>true</code> if <code>initialize</code> has been called.
     * @see #initialize(ServletContext, Map)
     */
    public boolean isInitialized()
    {
        return dispatcher != null;
    }

    /**
     * Create an individual <code>ActionTransaction</code>
     * 
     * @param <T> <code>Class</code> type of the underlying <code>Action</code>.
     * @param type <code>Class</code> type of the underlying <code>Action</code>.
     * @param namespace Namespace for the <code>Action</code>. May be <code>null</code>.
     * @param actionName String specifying the <code>Action</code> name.
     * @param requestParams Array of <code>String</code>s specifying the request parameters.
     *            <code>String</code>s are interpreted as (name,value) pairs. May be
     *            <code>null</code>.
     * @param headers Array of <code>String</code>s specifying the request headers.
     *            <code>String</code>s are interpreted as (name,value) pairs. May be
     *            <code>null</code>.
     * @param sessionAttributes <code>Map</code> of session objects. May be <code>null</code>.
     * @return <code>ActionTransaction</code> instance.
     * @throws Exception
     * @see ActionTransaction
     * @see #createTransaction(Class, String, String, Map, Map, Map)
     */
    public <T extends Action> ActionTransaction<T> createTransaction(Class<T> type,
                                                                     String namespace,
                                                                     String actionName,
                                                                     String[] requestParams,
                                                                     String[] headers,
                                                                     Map<String, Object> sessionAttributes)
        throws Exception
    {
        return createTransaction(type, namespace, actionName, requestParams, null, headers, sessionAttributes);
    }

    /**
     * Create an individual <code>ActionTransaction</code>
     * 
     * @param <T> <code>Class</code> type of the underlying <code>Action</code>.
     * @param type <code>Class</code> type of the underlying <code>Action</code>.
     * @param namespace Namespace for the <code>Action</code>. May be <code>null</code>.
     * @param actionName String specifying the <code>Action</code> name.
     * @param requestParams Array of <code>String</code>s specifying the request parameters.
     *            <code>String</code>s are interpreted as (name,value) pairs. May be
     *            <code>null</code>.
     * @param cookies Array of <code>Cookie</code>s to be added to the request.  May
     *            be <code>null</code>.
     * @param headers Array of <code>String</code>s specifying the request headers.
     *            <code>String</code>s are interpreted as (name,value) pairs. May be
     *            <code>null</code>.
     * @param sessionAttributes <code>Map</code> of session objects. May be <code>null</code>.
     * @return <code>ActionTransaction</code> instance.
     * @throws Exception
     * @see ActionTransaction
     * @see #createTransaction(Class, String, String, Map, Map, Map)
     */
    public <T extends Action> ActionTransaction<T> createTransaction(Class<T> type,
                                                                     String namespace,
                                                                     String actionName,
                                                                     String[] requestParams,
                                                                     Cookie[] cookies,
                                                                     String[] headers,
                                                                     Map<String, Object> sessionAttributes)
        throws Exception
    {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        if (requestParams != null)
        {
            int length = requestParams.length;
            for (int i = 0; i < length; i += 2)
            {
                mockRequest.setupAddParameter(requestParams[i], requestParams[i + 1]);
            }
        }
        
        if (cookies != null)
        {
            for (Cookie cookie : cookies)
            {
                mockRequest.addCookie(cookie);
            }
        }

        if (headers != null)
        {
            int length = headers.length;
            for (int i = 0; i < length; i += 2)
            {
                mockRequest.addHeader(headers[i], headers[i + 1]);
            }
        }

        if (sessionAttributes != null)
        {
            /*
             * Force the session object to appear to have been pre-created.
             * (Without this call, MockHttpRequest will "hide" the session
             * object - simply setting a HttpSession object into MockHttpSession
             * does not make that session actually "appear.")
             * 
             * (We have to do this outside the loop, since otherwise there
             * is no way to have a "created but empty" session. See the
             * discussion on sessions in the class Javadoc.
             */
            mockRequest.getSession();

            for (Map.Entry<String, ?> attribute : sessionAttributes.entrySet())
            {
                mockSession.setAttribute(attribute.getKey(), attribute.getValue());
            }
        }

        /*
         * Setup reasonable default values for common request attributes
         */
        StrutsTestUtils.prepareMockRequestDefaults(mockRequest,
                                                   namespace,
                                                   actionName,
                                                   requestParams);

        ActionProxy proxy = StrutsTestUtils.createActionProxy(dispatcher,
                                                              namespace,
                                                              actionName,
                                                              mockRequest,
                                                              mockResponse,
                                                              servletContext);

        return new ActionTransaction<T>(proxy, mockRequest, mockResponse, mockSession);
    }

    /**
     * Create an individual <code>ActionTransaction</code>. This method behaves
     * identically to {@link #createTransaction(Class, String, String, String[], String[], Map)}
     * except that it uses <code>Map</code>s to specify the <code>requestParams</code> and
     * <code>headers</code>. The string array method is slightly more flexible,
     * as it permits multiple parameters or headers of the same name.
     * 
     * @param <T> <code>Class</code> type of the underlying <code>Action</code>.
     * @param type <code>Class</code> type of the underlying <code>Action</code>.
     * @param namespace Namespace for the <code>Action</code>. May be <code>null</code>.
     * @param actionName <code>String</code> specifying the <code>Action</code> name.
     * @param requestParams <code>Map</code> specifying the request parameters. May be <code>null</code>.
     * @param headers <code>Map</code> specifying the request headers. May be <code>null</code>.
     * @param sessionAttributes <code>Map</code> of session objects. May be <code>null</code>.
     * @return <code>ActionTransaction</code> instance.
     * @throws Exception
     * @see ActionTransaction
     * @see #createTransaction(Class, String, String, String[], String[], Map)
     */
    public <T extends Action> ActionTransaction<T> createTransaction(Class<T> type,
                                                                     String namespace,
                                                                     String actionName,
                                                                     Map<String, String> requestParams,
                                                                     Map<String, String> headers,
                                                                     Map<String, Object> sessionAttributes)
        throws Exception
    {
        String[] paramsArray = StrutsTestUtils.mapToStringArray(requestParams);
        String[] headerArray = StrutsTestUtils.mapToStringArray(headers);

        return createTransaction(type,
                                 namespace,
                                 actionName,
                                 paramsArray,
                                 headerArray,
                                 sessionAttributes);
    }
}
