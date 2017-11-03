/*
 * Copyright (c) 2011 Kevin Hunter
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.mockrunner.mock.web.MockServletContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * This class is intended to serve as a base class for JUnit 4 test classes
 * that are testing Struts <code>Action</code> classes. It holds a static
 * instance of <code>ActionTransactionFactory</code> that is used to create
 * <code>ActionTransaction</code>s for the individual tests. (The use of
 * a static instance means that Struts will be initialized only once per
 * overall test run.)
 * <p>
 * The typical pattern is one of the following:
 * </p>
 * <p>
 * <b>Pattern 1:</b> An individual test case calls one of the <code>execute</code>
 * methods, which will create the <code>ActionTransaction</code>, execute the
 * Struts action and return the result returned by the Action.
 * </p>
 * <p>
 * <b>Pattern 2:</b> An individual test case calls one of the
 * <code>createTransaction</code> methods.  It then manipulates the target
 * class in some manner before calling <code>execute</code> on the 
 * transaction.
 * </p>
 * <p>
 * In either case, the target Action class is available after the transaction
 * is complete in order to allow its outputs to be examined.
 * </p>
 * 
 * @author Kevin Hunter
 * 
 * @param <ACTIONCLASS> Class that is being tested. Note that this class
 *            must extend <code>ActionSupport</code> - implementing <code>Action</code> isn't
 *            sufficient.
 * @see ActionTransactionFactory
 * @see ActionTransaction
 */
public class StrutsTestBase<ACTIONCLASS extends ActionSupport>
{
    private static ActionTransactionFactory factory;

    private Class<ACTIONCLASS>              type;

    private ActionTransaction<ACTIONCLASS>  transaction;

    /**
     * Constructor.
     * 
     * @param type Class being tested.
     */
    protected StrutsTestBase(Class<ACTIONCLASS> type)
    {
        this.type = type;
    }

    public static ActionTransactionFactory getFactory()
    {
        if (factory == null)
        {
            factory = new ActionTransactionFactory();
            MockServletContext servletContext = new MockServletContext();
            factory.initialize(servletContext, null);
        }

        return factory;
    }

    protected ActionTransaction<ACTIONCLASS> createTransaction(String actionName) throws Exception
    {
        transaction = getFactory().createTransaction(type,
                                                     "/",
                                                     actionName,
                                                     null,
                                                     null,
                                                     (String[]) null,
                                                     null);

        return transaction;
    }

    protected ActionTransaction<ACTIONCLASS> createTransaction(String actionName,
                                                               String[] requestParams)
        throws Exception
    {
        transaction = getFactory().createTransaction(type,
                                                     "/",
                                                     actionName,
                                                     requestParams,
                                                     null,
                                                     (String[]) null,
                                                     null);

        return transaction;
    }

    protected ActionTransaction<ACTIONCLASS> createTransaction(String namespace,
                                                               String actionName,
                                                               String[] requestParams,
                                                               Cookie[] cookies,
                                                               String[] headers,
                                                               Map<String, Object> sessionAttributes)
        throws Exception
    {
        transaction = getFactory().createTransaction(type,
                                                     namespace,
                                                     actionName,
                                                     requestParams,
                                                     cookies,
                                                     headers,
                                                     sessionAttributes);

        return transaction;
    }

    protected String execute(String actionName, String[] requestParams) throws Exception
    {
        transaction = createTransaction(actionName, requestParams);
        return transaction.execute();
    }

    protected String execute(String actionName) throws Exception
    {
        transaction = createTransaction(actionName);
        return transaction.execute();
    }

    protected String execute(String namespace,
                             String actionName,
                             String[] requestParams,
                             Cookie[] cookies,
                             String[] headers,
                             Map<String, Object> sessionAttributes) throws Exception
    {
        transaction = createTransaction(namespace,
                                        actionName,
                                        requestParams,
                                        cookies,
                                        headers,
                                        sessionAttributes);
        return transaction.execute();
    }

    protected ACTIONCLASS getAction()
    {
        return transaction.getAction();
    }

    protected ActionTransaction<ACTIONCLASS> getTransaction()
    {
        return transaction;
    }

    protected void assertFieldContainsError(String test, String field, String errKey)
    {
        ActionSupport action = transaction.getAction();
        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertNotNull(test, fieldErrors);
        List<String> errors = fieldErrors.get(field);
        assertNotNull(test, errors);
        for (String error : errors)
        {
            if (error != null && error.equals(action.getText(errKey)))
            {
                return;
            }
        }

        fail(test + ": Field " + field + " does not contain " + errKey);
    }
}
