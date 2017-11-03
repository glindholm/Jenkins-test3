/*
 * Copyright (c) 2008 Greg Lindholm
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

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.dispatcher.Dispatcher;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletContext;
import com.opensymphony.xwork2.ActionProxy;

/**
 * This class is designed to do the work of setting up a Struts <code>ActionProxy</code> for unit
 * testing. The core work is done through the static
 * {@link #createActionProxy(Dispatcher, String, String, HttpServletRequest, HttpServletResponse, ServletContext)}
 * method. This is the most flexible of the methods, but also requires the most work, since one
 * must set up request, response and context objects for it.
 * <p>
 * The non-static methods on this class are designed to make setting up <code>ActionProxy</code>s a
 * little easier. The normal flow is as follows:
 * </p>
 * <ol>
 * <li>
 * Create a <code>Dispatcher</code> object using either {@link #prepareDispatcher()} or
 * {@link #prepareDispatcher(ServletContext, Map)}. Creating a <code>Dispatcher</code> is an
 * expensive operation, so this is typically done only once, and the resulting object re-used.</li>
 * <li>
 * Create a <code>StrutsTestContext</code> object, using the <code>Dispatcher</code> and a
 * <code>MockServletContext</code>, if necessary.</li>
 * <li>
 * Create an <code>ActionProxy</code>. As part of this operation, the <code>StrutsTestContext</code>
 * will create mock request, response and session objects.</li>
 * <li>
 * If necessary, manipulate the request and session objects to adjust their contents. If all that is
 * necessary is to have parameters on the request and/or items in the session, the various
 * <code>createActionProxy</code> methods can populate the mock objects. If, however, one needs to
 * do something more sophisticated, the raw objects are available once the <code>ActionProxy</code>
 * has been created.</li>
 * <li>
 * Call <code>execute</code> on the <code>ActionProxy</code> object. This will cause the underlying
 * <code>Action</code> to be executed.</li>
 * <li>
 * As necessary, based on the nature of the test:
 * <ol>
 * <li>Obtain the <code>Action</code> object from the <code>ActionProxy</code> and check the various
 * property to see what is being returned to the view.</li>
 * <li>
 * Check the mock session object to see what has been put into the session.</li>
 * <li>
 * Check the mock response object to see what has been returned there. (e.g. check for redirects,
 * etc.)</li>
 * </ol>
 * </li>
 * </ol>
 * <p>
 * The <code>StrutsTestContext</code> creates new request, response and session objects with each
 * call to one of the <code>createActionProxy</code> methods, so a <code>StrutsTestContext</code>
 * instance can be re-used across tests, if desired. (Session objects are not persisted, however -
 * anything along that line is the responsibility of the caller.) Alternately, since creating a
 * <code>StrutsTestContext</code> is a lightweight operation, a new instance can be created for each
 * test.
 * </p>
 * <p>
 * A special note about sessions. Tests may need to examine the behavior of <code>Action</code>s
 * when (a) there is no existing session in place or (b) when there is an existing session in place.
 * In order to service this need, the <code>createActionProxy</code> methods behave as follows:
 * </p>
 * <ul>
 * <li>
 * If the <code>sessionAttributes</code> parameter to <code>createActionProxy</code> is
 * <code>null</code>, the request will be set up so that it appears to the <code>Action</code> that
 * there is not a pre-existing session. An underlying <code>MockHttpSession</code> is still created,
 * because the unit under test may itself want to create a session and place objects into it,
 * however the <code>MockHttpRequest</code> is set up so that it will appear that no session has
 * been created before the Struts logic is invoked.</li>
 * <li>
 * If the <code>sessionAttributes</code> parameter to <code>createActionProxy</code> is non-
 * <code>null</code>, the <code>MockHttpRequest</code> will be manipulated so that it appears that a
 * session <em>did</em> exist from a prior request, even if there aren't actually any attributes in
 * the <code>sessionAttributes</code> <code>Map</code>.</li>
 * </ul>
 * 
 * @author Greg Lindholm
 * @author Kevin Hunter
 * 
 */
public class StrutsTestContext
{
    /**
     * Construct a <code>StrutsTestContext</code> object.
     * 
     * @param dispatcher Struts <code>Dispatcher</code> object that will handle the
     *            <code>Action</code> mapping.
     * @param servletContext <code>MockServletContext</code> object to act as the context for the
     *            Struts system.
     */
    public StrutsTestContext(Dispatcher dispatcher, MockServletContext servletContext)
    {
        this.dispatcher = dispatcher;
        this.mockServletContext = servletContext;
    }

    /**
     * Construct a <code>StrutsTestContext</code> object with an "empty"
     * servlet context.
     * 
     * @param dispatcher Struts <code>Dispatcher</code> object that will handle the
     *            <code>Action</code> mapping.
     */
    public StrutsTestContext(Dispatcher dispatcher)
    {
        this(dispatcher, new MockServletContext());
    }

    /*
     * Struts Dispatcher object. Provided by the constructor.
     */
    private Dispatcher              dispatcher;
    /*
     * Servlet context for Struts. Provided by the constructor.
     */
    private MockServletContext      mockServletContext;

    /*
     * Request object from which the HTTP request parameters, etc. will be taken. Created with each
     * call to
     * createActionProxy.
     */
    private MockHttpServletRequest  mockRequest;
    /*
     * Response object into which the result will be output. Created with each call to
     * createActionProxy.
     */
    private MockHttpServletResponse mockResponse;
    /*
     * Session object. Created with each call to createActionProxy.
     */
    private MockHttpSession         mockSession;

    /**
     * Utility method to prepare a <code>Dispatcher</code> that doesn't have either a
     * <code>ServletContext</code> or any initialization parameters.
     * <p>
     * Creating a <code>Dispatcher</code> is a moderately expensive operation, so the
     * <code>Dispatcher</code> should be reused where possible.
     * </p>
     * 
     * @return <code>Dispatcher</code> instance.
     */
    public static Dispatcher prepareDispatcher()
    {
        return prepareDispatcher(null, null);
    }

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
        return StrutsTestUtils.prepareDispatcher(servletContext, params);
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
        return StrutsTestUtils.createActionProxy(dispatcher,
                                                 namespace,
                                                 actionName,
                                                 request,
                                                 response,
                                                 servletContext);
    }

    /**
     * Create an ActionProxy using the Dispatcher built into this object.
     * 
     * @param namespace Namespace for the action. Can be null.
     * @param actionName Name of the action.
     * @param requestParams Map of request HTTP parameters
     * @param sessionAttributes Map of HTTP session attributes.
     * @return <code>ActionProxy</code> instance through which the action can be run
     * @throws Exception
     */
    public ActionProxy createActionProxy(String namespace,
                                         String actionName,
                                         Map<String, String> requestParams,
                                         Map<String, Object> sessionAttributes) throws Exception
    {
        return createActionProxy(namespace, actionName, requestParams, null, sessionAttributes);
    }

    /**
     * Create an ActionProxy using the Dispatcher built into this object.
     * 
     * @param namespace Namespace for the action. Can be null.
     * @param actionName Name of the action.
     * @param requestParams Map of request HTTP parameters
     * @param headers Map of request HTTP headers
     * @param sessionAttributes Map of HTTP session attributes
     * @return <code>ActionProxy</code> object through which the action can be run
     * @throws Exception
     */
    public ActionProxy createActionProxy(String namespace,
                                         String actionName,
                                         Map<String, String> requestParams,
                                         Map<String, String> headers,
                                         Map<String, Object> sessionAttributes) throws Exception
    {
        mockRequest = new MockHttpServletRequest();
        mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        mockResponse = new MockHttpServletResponse();

        if (headers != null)
        {
            for (Map.Entry<String, String> param : headers.entrySet())
            {
                mockRequest.setHeader(param.getKey(), param.getValue());
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

        if (requestParams != null)
        {
            for (Map.Entry<String, String> param : requestParams.entrySet())
            {
                mockRequest.setupAddParameter(param.getKey(), param.getValue());
            }

        }

        /*
         * Setup reasonable default values for common request attributes
         */
        StrutsTestUtils.prepareMockRequestDefaults(mockRequest,
                                                   namespace,
                                                   actionName,
                                                   StrutsTestUtils.mapToStringArray(requestParams));

        /*
         * Create the ActionProxy
         */
        return createActionProxy(dispatcher,
                                 namespace,
                                 actionName,
                                 mockRequest,
                                 mockResponse,
                                 mockServletContext);
    }

    /**
     * Get the "wrapped" Struts <code>Dispatcher</code>.
     * 
     * @return Struts <code>Dispatcher</code>
     */
    public Dispatcher getDispatcher()
    {
        return dispatcher;
    }

    /**
     * Get the "wrapped" <code>MockServletContext</code>.
     * 
     * @return <code>MockServletContext</code> object. May be <code>null</code>.
     */
    public MockServletContext getMockServletContext()
    {
        return mockServletContext;
    }

    /**
     * Get the <code>MockHttpServletRequest</code> associated with
     * the most recently-created <code>ActionProxy</code>.
     * 
     * @return <code>MockHttpServletRequest</code> object. Will be <code>null</code> prior to the
     *         first call to <code>createActionProxy</code>.
     */
    public MockHttpServletRequest getMockRequest()
    {
        return mockRequest;
    }

    /**
     * Get the <code>MockHttpServletResponse</code> associated with
     * the most recently-created <code>ActionProxy</code>.
     * 
     * @return <code>MockHttpServletResponse</code> object. Will be <code>null</code> prior to the
     *         first call to <code>createActionProxy</code>.
     */
    public MockHttpServletResponse getMockResponse()
    {
        return mockResponse;
    }

    /**
     * Get the <code>MockHttpSession</code> associated with
     * the most recently-created <code>ActionProxy</code>.
     * 
     * @return <code>MockHttpSession</code> object. Will be <code>null</code> prior to the
     *         first call to <code>createActionProxy</code>.
     */
    public MockHttpSession getMockSession()
    {
        return mockSession;
    }
}
