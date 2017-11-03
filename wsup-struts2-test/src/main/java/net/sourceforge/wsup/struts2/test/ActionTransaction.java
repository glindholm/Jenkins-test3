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

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

/**
 * This class encapsulates a particular test case for a Struts
 * action. It groups together the <code>ActionProxy</code> that can be used to execute the
 * <code>Action</code>, along
 * with the corresponding <code>HttpServletRequest</code>, <code>MockHttpServletResponse</code> and
 * <code>MockHttpSession</code> objects associated with
 * the execution of that request.
 * <p>
 * Objects of this class are not normally constructed manually - one normally obtains them from the
 * <code>ActionTransactionFactory</code> class.
 * </p>
 * 
 * @author Kevin Hunter
 * 
 * @param <ACTIONCLASS> Class of the actual underlying <code>Action</code>.
 */
public class ActionTransaction<ACTIONCLASS extends Action>
{
    private final ActionProxy             actionProxy;
    private final MockHttpServletRequest  request;
    private final MockHttpServletResponse response;
    private final MockHttpSession         session;

    /**
     * Constructor.
     * 
     * @param actionProxy <code>ActionProxy</code>
     * @param request <code>MockHttpServletRequest</code>
     * @param response <code>MockHttpServletResponse</code>
     * @param session <code>MockHttpSession</code>
     */
    public ActionTransaction(ActionProxy actionProxy,
                             MockHttpServletRequest request,
                             MockHttpServletResponse response,
                             MockHttpSession session)
    {
        this.actionProxy = actionProxy;
        this.request = request;
        this.response = response;
        this.session = session;
    }

    /**
     * Execute the underlying <code>Action</code> via the <code>ActionProxy</code>.
     * 
     * @return The result code <code>String</code> returned from the <code>Action</code>.
     * @throws Exception
     */
    public String execute() throws Exception
    {
        return actionProxy.execute();
    }

    /**
     * Get the <code>ActionProxy</code> object.
     * 
     * @return <code>ActionProxy</code> object
     */
    public ActionProxy getActionProxy()
    {
        return actionProxy;
    }

    /**
     * Get the <code>Action</code> instance that underlies the <code>ActionProxy</code>.
     * 
     * @return <code>Action</code> instance
     */
    @SuppressWarnings("unchecked")
    public ACTIONCLASS getAction()
    {
        return (ACTIONCLASS) actionProxy.getAction();
    }

    /**
     * Get the <code>MockHttpServletRequest</code> request that is
     * bound to the <code>ActionProxy</code>. May be used to manipulate
     * certain aspects of the contents of the request prior to <code>execute</code>-ing the
     * <code>ActionProxy</code>.
     * 
     * @return <code>MockHttpServletRequest</code> object
     */
    public MockHttpServletRequest getRequest()
    {
        return request;
    }

    /**
     * Get the <code>MockHttpSession</code> request that is
     * bound to the <code>ActionProxy</code>. May be used to manipulate
     * the contents of the session prior to <code>execute</code>-ing
     * the <code>ActionProxy</code>, and to inspecting its contents
     * thereafter.
     * 
     * @return <code>MockHttpSession</code> object
     */
    public MockHttpSession getSession()
    {
        return session;
    }

    /**
     * Get the <code>MockHttpServletResponse</code> response that is
     * bound to the <code>ActionProxy</code>. May be used to inspect
     * the output of the <code>Action</code> (e.g. check result codes,
     * redirect locations, etc.)
     * 
     * @return <code>MockHttpServletResponse</code> object
     */
    public MockHttpServletResponse getResponse()
    {
        return response;
    }
}
