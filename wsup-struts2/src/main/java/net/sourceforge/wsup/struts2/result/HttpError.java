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

package net.sourceforge.wsup.struts2.result;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsStatics;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;

/**
 *
 * A custom Result type for setting HTTP error status and headers. The values can be optionally
 * evaluating against the ValueStack.
 *
 * <p/>
 * <b>This result type takes the following parameters:</b>
 *
 * <ul>
 *
 * <li><b>status</b> - the http servlet response status code that should be set on a response.</li>
 *
 * <li><b>errorMessage</b> - the text error message to be set on response with the status.</li>
 *
 * <li><b>parse</b> - true by default. If set to false, the headers param will not be parsed for
 * Ognl expressions.</li>
 *
 * <li><b>headers</b> - header values.</li>
 * </ul>
 *
 *
 * <b>Example:</b>
 *
 * <pre>
 * [result-types]
 *   [result-type name="httperror" class="net.sourceforge.wsup.struts2.result.HttpError"/]
 * [/result-types>]
 *
 *
 * [result name="error" type="httperror"]
 *   [param name="status"]${httpStatus}[/param]
 *   [param name="errorMessage"]${message}[/param]
 *   [param name="headers.a"]a custom header value[/param]
 *   [param name="headers.b"]another custom header value[/param]
 *   [param name="headers['x-wap-profile']"]Header with '-' in the name[/param]
 * [/result]
 *
 * [result name="proxyRequired" type="httperror"]
 *   [param name="status"]305[/param]
 *   [param name="errorMessage"]this action must be accessed through a prozy[/param]
 * [/result]
 * </pre>
 *
 */

public class HttpError implements Result
{
    private static final long   serialVersionUID = -6303452031011966369L;

    /** The default parameter */
    public static final String  DEFAULT_PARAM    = "status";

    private boolean             parse            = true;
    private Map<String, String> headers          = new HashMap<String, String>();
    private String              status;
    private String              errorMessage;

    public HttpError()
    {
        super();
    }

    /**
     * Returns a Map of all HTTP headers.
     *
     * @return a Map of all HTTP headers.
     */
    public Map<String, String> getHeaders()
    {
        return headers;
    }

    /**
     * Sets whether or not the HTTP header values should be evaluated against
     * the ValueStack (by default they are).
     *
     * @param parse <tt>true</tt> if HTTP header values should be evaluated
     *            agains the ValueStack, <tt>false</tt> otherwise.
     */
    public void setParse(boolean parse)
    {
        this.parse = parse;
    }

    /**
     * Sets the http servlet response status code that should be set on a
     * response.
     *
     * @param status the Http status code
     * @see javax.servlet.http.HttpServletResponse#setStatus(int)
     */
    public void setStatus(String status)
    {
        this.status = status;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    /**
     * Sets the optional HTTP response status code and also re-sets HTTP headers
     * after they've been optionally evaluated against the ValueStack.
     *
     * @param invocation an encapsulation of the action execution state.
     * @throws Exception if an error occurs when re-setting the headers.
     */
    @Override
    public void execute(ActionInvocation invocation) throws Exception
    {
        // ValueStack stack = ActionContext.getContext().getValueStack();
        ValueStack stack = invocation.getStack();

        // HttpServletResponse response = ServletActionContext.getResponse();
        // HttpServletResponse response = (HttpServletResponse) invocation.getInvocationContext().get(StrutsStatics.HTTP_RESPONSE);
        HttpServletResponse response = (HttpServletResponse) stack
            .findValue(StrutsStatics.HTTP_RESPONSE);

        int finalStatus = Integer.parseInt(parse ? TextParseUtil.translateVariables(status, stack)
                                                : status);

        if (StringUtils.isNotBlank(errorMessage))
        {
            String finalMessage = parse ? TextParseUtil.translateVariables(errorMessage, stack)
                                       : errorMessage;
            response.sendError(finalStatus, finalMessage);
        }
        else
        {
            response.sendError(finalStatus);
        }

        for (Entry<String, String> header : headers.entrySet())
        {
            String value = header.getValue();
            String finalValue = parse ? TextParseUtil.translateVariables(value, stack) : value;
            response.addHeader(header.getKey(), finalValue);
        }
    }
}
