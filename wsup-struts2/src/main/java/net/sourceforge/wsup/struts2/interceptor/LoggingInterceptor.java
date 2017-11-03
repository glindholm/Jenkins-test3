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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * LoggingInterceptor is a "better" logger that logs every action invocation with the action and
 * method name and all parameters and the result. <br>
 * Configure this Interceptor in your struts.xml file:
 *
 * <pre>
 *   [interceptors]
 *     [interceptor name="log"
 *       class="net.sourceforge.wsup.struts2.interceptor.LoggingInterceptor"]
 *       [param name="secureParams"]password,passwd[/param]
 *   [/interceptors]
 * </pre>
 *
 * The param "secureParams" is comma separated list of (case-insensitive) parameter names whose
 * values are
 * to be masked ("****") in the log text. Used to mask passwords.
 */
public class LoggingInterceptor implements Interceptor
{
    private static final long   serialVersionUID = -3311913188235755268L;
    private static final Logger logger           = LoggerFactory
                                                     .getLogger(LoggingInterceptor.class);

    public LoggingInterceptor()
    {
    }

    private Set<String> secureParamsSet = Collections.emptySet();

    public void destroy()
    {
    }

    public void init()
    {
    }

    public String intercept(ActionInvocation invocation) throws Exception
    {
        if (logger.isInfoEnabled())
        {
            StringBuilder buf = new StringBuilder("Start action ");
            addNamespaceAction(buf, invocation);

            buf.append(' ');
            buf.append(invocation.getProxy().getAction().getClass().getName());
            if (invocation.getProxy().getMethod() != null)
            {
                buf.append('.').append(invocation.getProxy().getMethod()).append("()");
            }
            else
            {
                buf.append(".execute()");
            }

            Map<String, Object> params = invocation.getInvocationContext().getParameters();
            if (params.size() != 0)
            {
                buf.append(" [");
                for (Iterator<Entry<String, Object>> iterator = params.entrySet().iterator(); iterator
                    .hasNext();)
                {
                    Entry<String, Object> entry = iterator.next();
                    buf.append(entry.getKey()).append('=');
                    // Value is an array
                    Object[] values = (Object[]) entry.getValue();
                    for (int i = 0; i < values.length; i++)
                    {
                        if (i > 0)
                        {
                            buf.append(',');
                        }

                        if (secureParamsSet.contains(entry.getKey().toString().toLowerCase()))
                        {
                            if (!StringUtils.isBlank(values[i].toString()))
                            {
                                buf.append("****");
                            }
                        }
                        else
                        {
                            buf.append(values[i].toString());
                        }
                    }
                    if (iterator.hasNext())
                    {
                        buf.append(',');
                    }
                }
                buf.append(']');
            }

            logger.info(buf.toString());
        }

        String result;

        try
        {
            result = invocation.invoke();
        }
        catch (Exception e)
        {
            logger.info("Action threw exception", e);
            throw e;
        }

        if (logger.isInfoEnabled())
        {
            StringBuilder buf = new StringBuilder("Finish action ");
            addNamespaceAction(buf, invocation);

            buf.append(" result=").append(result);

            Result resultObj = invocation.getResult();
            buf.append(" type=").append(resultObj != null ? resultObj.getClass().getSimpleName()
                                                         : "null");

            logger.info(buf.toString());
        }

        return result;
    }

    private void addNamespaceAction(StringBuilder buf, ActionInvocation invocation)
    {
        String namespace = invocation.getProxy().getNamespace();

        if (namespace != null)
        {
            if (namespace.trim().length() > 0)
            {
                buf.append(namespace);

                if (!"/".equals(namespace))
                {
                    buf.append('/');
                }
            }
        }

        buf.append(invocation.getProxy().getActionName());
    }

    /**
     * @param secureParams a comma separated list of parameter names (case-insensitive)whose values
     *            is to be masked ("****") in the logs. This is used to prevent passwords from being
     *            written to the log.
     */
    public void setSecureParams(String secureParams)
    {
        secureParamsSet = new HashSet<String>(Arrays.asList(StringUtils.split(secureParams
            .toLowerCase(), ',')));
    }

    /**
     * Expose for testing. Parameter names will be converted to lowercase
     */
    Set<String> getSecureParamsSet()
    {
        return secureParamsSet;
    }

}
