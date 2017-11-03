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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;
import org.apache.struts2.dispatcher.ServletRedirectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

/**
 * An Interceptor to preserve an actions ValidationAware messages across a
 * redirect result.
 *
 * It makes the assumption that you always want to preserve messages across a
 * redirect and restore them to the next action if they exist.
 *
 * The way this works is it looks at the result type after a action has executed
 * and if the result was a redirect (ServletRedirectResult) or a redirectAction
 * (ServletActionRedirectResult) and there were any errors, messages, or
 * fieldErrors they are stored in the session. Before the next action executes
 * it will check if there are any messages stored in the session and add them to
 * the next action.
 *
 */
public class RedirectMessageInterceptor extends MethodFilterInterceptor
{
    private static final long   serialVersionUID    = -1847557437429753540L;

    private static final Logger logger              = LoggerFactory
                                                        .getLogger(RedirectMessageInterceptor.class);

    public static final String  FIELD_ERRORS_KEY    = "RedirectMessageInterceptor_FieldErrors";
    public static final String  ACTION_ERRORS_KEY   = "RedirectMessageInterceptor_ActionErrors";
    public static final String  ACTION_MESSAGES_KEY = "RedirectMessageInterceptor_ActionMessages";

    public RedirectMessageInterceptor()
    {
    }

    public String doIntercept(ActionInvocation invocation) throws Exception
    {
        Object action = invocation.getAction();
        if (action instanceof ValidationAware)
        {
            before(invocation, (ValidationAware) action);
        }

        String result = invocation.invoke();

        if (action instanceof ValidationAware)
        {
            after(invocation, (ValidationAware) action);
        }
        return result;
    }

    /**
     * Retrieve the errors and messages from the session and add them to the
     * action.
     */
    protected void before(ActionInvocation invocation, ValidationAware validationAware)
        throws Exception
    {
        Map<String, ?> session = invocation.getInvocationContext().getSession();

        @SuppressWarnings({ "unchecked", "rawtypes" })
        Collection<String> actionErrors = (Collection) session.remove(ACTION_ERRORS_KEY);
        if (actionErrors != null && actionErrors.size() > 0)
        {
            for (String error : actionErrors)
            {
                validationAware.addActionError(error);
            }
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        Collection<String> actionMessages = (Collection) session.remove(ACTION_MESSAGES_KEY);
        if (actionMessages != null && actionMessages.size() > 0)
        {
            for (String message : actionMessages)
            {
                validationAware.addActionMessage(message);
            }
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        Map<String, List<String>> fieldErrors = (Map) session.remove(FIELD_ERRORS_KEY);
        if (fieldErrors != null && fieldErrors.size() > 0)
        {
            for (Map.Entry<String, List<String>> fieldError : fieldErrors.entrySet())
            {
                for (String message : fieldError.getValue())
                {
                    validationAware.addFieldError(fieldError.getKey(), message);
                }
            }
        }
    }

    /**
     * If the result is a redirect then store error and messages in the session.
     */
    protected void after(ActionInvocation invocation, ValidationAware validationAware)
        throws Exception
    {
        Result result = invocation.getResult();

        if (result != null
            && (result instanceof ServletRedirectResult || result instanceof ServletActionRedirectResult))
        {
            /*
             * Stage the messages into a local map before storing into the session
             * to ease error handling.
             */
            Map<String, Object> messages = new HashMap<String, Object>();

            Collection<String> actionErrors = validationAware.getActionErrors();
            if (actionErrors != null && actionErrors.size() > 0)
            {
                messages.put(ACTION_ERRORS_KEY, actionErrors);
            }

            Collection<String> actionMessages = validationAware.getActionMessages();
            if (actionMessages != null && actionMessages.size() > 0)
            {
                messages.put(ACTION_MESSAGES_KEY, actionMessages);
            }

            Map<String, List<String>> fieldErrors = validationAware.getFieldErrors();
            if (fieldErrors != null && fieldErrors.size() > 0)
            {
                messages.put(FIELD_ERRORS_KEY, fieldErrors);
            }

            if (messages.size() > 0)
            {
                try
                {
                    /*
                     * session is a Struts2 wrapper around the session object that implements
                     * a map interface.
                     */
                    Map<String, Object> session = invocation.getInvocationContext().getSession();
                    session.putAll(messages);
                }
                catch (Exception e)
                {
                    /*
                     * session.put() can throw an IllegalStateException if the session
                     * does not exist and the response has already been committed.
                     * 
                     * If this happens just log all the exception and include all the messages.
                     */
                    HttpServletRequest request = (HttpServletRequest) invocation
                        .getInvocationContext().get(StrutsStatics.HTTP_REQUEST);

                    String requestData = "(unavailble)";
                    if (request != null)
                    {
                        requestData = String
                            .format("%s %s%s",
                                    request.getMethod(),
                                    request.getRequestURL(),
                                    request.getQueryString() == null ? "" : "?"
                                                                            + request
                                                                                .getQueryString());
                    }

                    logger.warn(String.format("Storing messages=[%s] Request=[%s]",
                                              messages.toString(),
                                              requestData),
                                e);
                }
            }
        }
    }

}
