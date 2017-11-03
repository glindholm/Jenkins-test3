/*
 *  Copyright (c) 2011 Kevin Hunter
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and 
 *  limitations under the License. 
 */

package net.sourceforge.wsup.core.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.sourceforge.wsup.core.BeanUtils;

/**
 * This class can be used in servlet filters in order to modify an incoming
 * <code>HttpServletRequest</code>. One constructs the instance by "wrapping" the original request,
 * possibly altering the path and the parameters before sending it up the filter chain.
 * <p>
 * Note that use of this class may introduce subtle modifications in the incoming information. For
 * example, if the original request was triggered by the URL
 * 
 * <pre>
 * http://server/path?a=b&c=d
 * </pre>
 * It is possible that this class will report a query string of <code>c=d&a=b</code> instead of
 * <code>a=b&amp;c=d</code>. If a parameter has multiple values, however, the order of the values
 * for that parameter will not be altered - it is only the order of parameters with differing names
 * that may be reported. This should not affect the vast majority of applications.
 * </p>
 * 
 * @author Kevin Hunter
 */
@SuppressWarnings("unchecked")
public class RewrittenHttpServletRequest extends HttpServletRequestWrapper
{
    private final Map<String, String[]> params;
    private final String                requestURI;

    /**
     * Constructor that does not modify the request path. This would be used if all
     * you want to do is to alter the parameters.
     * 
     * @param originalRequest Original <code>HttpServletRequest</code>
     */
    public RewrittenHttpServletRequest(HttpServletRequest originalRequest)
    {
        super(originalRequest);

        params = new TreeMap<String, String[]>(originalRequest.getParameterMap());
        requestURI = originalRequest.getRequestURI();
    }

    /**
     * Constructor that provides a replacement path. You would normally use this if you
     * are rewriting the full body of the URL, as opposed to just parameters. For example,
     * if the URL
     * 
     * <pre>
     * http://server/contextPath/path/to/some/file.html
     * </pre>
     * 
     * came in, and you passed "/anotherFile.html" into the <code>replacementPath</code> parameter,
     * the result would be as if the request had come in to
     * 
     * <pre>
     * http://server/contextPath/anotherFile.html
     * </pre>
     * 
     * Note that the context path to the application is preserved.
     * 
     * @param originalRequest Original <code>HttpServletRequest</code>.
     * @param replacementPath Path to replace the existing path. Must not be <code>null</code> and
     *            must start with a "/" character.
     */
    public RewrittenHttpServletRequest(HttpServletRequest originalRequest, String replacementPath)
    {
        super(originalRequest);

        params = new TreeMap<String, String[]>(originalRequest.getParameterMap());

        StringBuilder buffer = new StringBuilder();
        if (!"/".equals(getContextPath()))
        {
            buffer.append(getContextPath());
        }
        buffer.append(replacementPath);
        this.requestURI = buffer.toString();
    }

    /**
     * Clears all of the URL parameters.
     */
    public void clearParameters()
    {
        getParameterMap().clear();
    }

    /**
     * Removes all values for the specified parameter.
     * 
     * @param name Name of the parameter.
     */
    public void removeParameter(String name)
    {
        getParameterMap().remove(name);
    }

    /**
     * Replace any values for the specified parameter with the specified single value.
     * 
     * @param name Name of the parameter.
     * @param value New value of the parameter. Must not be <code>null</code>, although may be the
     *            empty string.
     */
    public void replaceParameter(String name, String value)
    {
        replaceParameter(name, new String[] { value });
    }

    /**
     * Replace any values for the specified parameter with one or more values.
     * 
     * @param name Name of the parameter.
     * @param values Array containing the new value(s) of the parameters. The array must not be
     *            <code>null</code>, it must be of length >= 1, and none of the values in it may be
     *            <code>null</code> (although they may be the empty string).
     */
    public void replaceParameter(String name, String[] values)
    {
        getParameterMap().put(name, values);
    }

    @Override
    public String getParameter(String name)
    {
        String[] values = getParameterValues(name);
        if (values == null)
        {
            return null;
        }

        if (values.length == 0)
        {
            return null;
        }

        return values[0];
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map getParameterMap()
    {
        return params;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Enumeration getParameterNames()
    {
        return Collections.enumeration(getParameterMap().keySet());
    }

    @Override
    public String[] getParameterValues(String name)
    {
        return (String[]) getParameterMap().get(name);
    }

    @Override
    public String getQueryString()
    {
        return ServletUtils.queryStringFromParameters(params);
    }

    @Override
    public String getRequestURI()
    {
        return requestURI;
    }

    @Override
    public StringBuffer getRequestURL()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getScheme());
        buffer.append("://");
        buffer.append(getServerName());
        if (!isDefaultPortForScheme())
        {
            buffer.append(':');
            buffer.append(Integer.toString(getServerPort()));
        }
        buffer.append(getRequestURI());

        return buffer;
    }

    /**
     * Determines whether or not the port in the request is the default one for the specified scheme
     * (i.e. port 80 for HTTP, port 443 for HTTPS). A port value that is zero or negative will be
     * treated as a default port.
     * 
     * @return <code>true</code> if it is the default port, <code>false</code> if not.
     */
    protected boolean isDefaultPortForScheme()
    {
        int port = getServerPort();
        if (port <= 0)
        {
            return true;
        }

        String scheme = getScheme();
        if (BeanUtils.safeEqualsIgnoreCase(scheme, "https"))
        {
            if (port == 443)
            {
                return true;
            }
        }
        else
        {
            if (port == 80)
            {
                return true;
            }
        }

        return false;
    }
}
