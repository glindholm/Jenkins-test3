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

package net.sourceforge.wsup.struts2;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import net.jcip.annotations.NotThreadSafe;
import net.sourceforge.wsup.core.servlet.ServletUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * ActionBase
 *
 */
@NotThreadSafe
public class ActionBase extends ActionSupport implements SessionAware, ServletRequestAware
{
    private static final long   serialVersionUID    = -1052785427118676550L;
    private static final Logger logger              = LoggerFactory.getLogger(ActionBase.class);

    public static final String  RESULT_NOT_FOUND    = "notFound";
    public static final String  RESULT_FAILED       = "failed";

    public static final String  ERROR_STRING_LENGTH = "StringLengthField.error.length";
    public static final String  ERROR_STRING_MAXLEN = "StringLengthField.error.maxLength";
    public static final String  ERROR_STRING_MINLEN = "StringLengthField.error.minLength";

    public ActionBase()
    {
        super();
        now = new Date();
    }

    // Dependencies
    private Map<String, Object>    sessionMap;
    private HttpServletRequest     httpServletRequest;

    // State
    private Date                   now;
    private List<Exception>        exceptions;

    private transient DateFormat   dateFormat;
    private transient DateFormat   dateTimeFormat;
    private transient NumberFormat integerFormat;

    /**
     * Gets the ActionContext. <br>
     * Can be overridden for testing.
     *
     * @return the ThreadLocal ActionContext
     */
    protected ActionContext getActionContext()
    {
        return ActionContext.getContext();
    }

    /**
     * Gets the action name. This is just the bare name without ".action" extension.
     *
     * This is equivalent to "#context['struts.actionMapping'].name" from in a JSP.
     *
     * @return the action name
     */
    public String getActionName()
    {
        return getActionContext().getName();
    }

    /**
     * Gets the standard Struts Locale.<br>
     * Override to provide a user specific Locale
     *
     * @return the User's Locale
     */
    public Locale getUserLocale()
    {
        return getActionContext().getLocale();
    }

    /**
     * Gets the current Date. This is the timestamp when the action was created.
     * Actions are very short lived so this date can be used throughout whenever
     * a timestamp is needed.
     *
     * @return the current Date
     */
    public Date getNow()
    {
        return now;
    }

    /**
     * Set now to a different value for testing.
     *
     * @param now
     */
    protected void setNow(Date now)
    {
        this.now = now;
    }

    /**
     * Gets the default TimeZone.<br>
     * Override to provide a user specific TimeZone.
     *
     * @return the User's TimeZone
     */
    public TimeZone getUserTimeZone()
    {
        return TimeZone.getDefault();
    }

    /**
     * @return the localized (short) name of the user's TimeZone
     */
    public String getUserTimeZoneName()
    {
        return getUserTimeZone().getDisplayName(getUserTimeZone().inDaylightTime(getNow()),
                                                TimeZone.SHORT,
                                                getUserLocale());
    }

    /**
     * @return the localized name of the user's TimeZone
     */
    public String getUserTimeZoneNameLong()
    {
        return getUserTimeZone().getDisplayName(getUserTimeZone().inDaylightTime(getNow()),
                                                TimeZone.LONG,
                                                getUserLocale());
    }

    /**
     * Gets the style to use for formatting dates with {@link #formatDate(Date)}.<br>
     * Defaults to {@link DateFormat#MEDIUM}.<br>
     * Override to customize.
     *
     * @return the DateFormat style
     */
    protected int getDateFormatDateStyle()
    {
        return DateFormat.MEDIUM;
    }

    /**
     * Gets a DateFormat object used by {@link #formatDate(Date)} suitable for formatting a Date
     * (without time) based on the user's
     * Locale and TimeZone.
     *
     * @return a DateFormat
     * @see ActionBase#getUserLocale()
     * @see ActionBase#getUserTimeZone()
     * @see ActionBase#getDateFormatDateStyle()
     */
    protected DateFormat getDateFormat()
    {
        if (dateFormat == null)
        {
            dateFormat = DateFormat.getDateInstance(getDateFormatDateStyle(), getUserLocale());
            dateFormat.setTimeZone(getUserTimeZone());
        }

        return dateFormat;
    }

    /**
     * Format a date (without time) using the user's Locale and TimeZone.
     *
     * @param date the date to format
     * @return the formatted date
     */
    public String formatDate(Date date)
    {
        return getDateFormat().format(date);
    }

    /**
     * Gets the style to use for formatting dates with {@link #formatDateTime(Date)}.<br>
     * Defaults to {@link DateFormat#SHORT}.<br>
     * Override to customize.
     *
     * @return the DateFormat style
     */
    protected int getDateTimeFormatDateStyle()
    {
        return DateFormat.SHORT;
    }

    /**
     * Gets the style to use for formatting time with {@link #formatDateTime(Date)}.<br>
     * Defaults to {@link DateFormat#MEDIUM}.<br>
     * Override to customize.
     *
     * @return the DateFormat style
     */
    protected int getDateTimeFormatTimeStyle()
    {
        return DateFormat.MEDIUM;
    }

    /**
     * Gets a DateFormat object user by {@link #formatDateTime(Date)} suitable for formatting a Date
     * and Time based on the user's Locale
     * and TimeZone.
     *
     * @return a DateFormat
     * @see ActionBase#getDateTimeFormatDateStyle()
     * @see ActionBase#getDateTimeFormatTimeStyle()
     * @see ActionBase#getUserLocale()
     * @see ActionBase#getUserTimeZone()
     */
    protected DateFormat getDateTimeFormat()
    {
        if (dateTimeFormat == null)
        {
            dateTimeFormat = DateFormat.getDateTimeInstance(getDateTimeFormatDateStyle(),
                                                            getDateTimeFormatTimeStyle(),
                                                            getUserLocale());
            dateTimeFormat.setTimeZone(getUserTimeZone());
        }
        return dateTimeFormat;
    }

    /**
     * Format a date and time using the user's Locale and TimeZone.
     *
     * @param timestamp the date and time to format
     * @return the formatted date and time
     */
    public String formatDateTime(Date timestamp)
    {
        return getDateTimeFormat().format(timestamp);
    }

    /**
     * Gets a NumberFormat object for use by {@link ActionBase#formatInteger(long)} suitable for
     * formatting an integer in the user's locale.
     *
     * @return a NumberFormat
     */
    protected NumberFormat getIntegerFormat()
    {
        if (integerFormat == null)
        {
            integerFormat = NumberFormat.getIntegerInstance(getUserLocale());
        }
        return integerFormat;
    }

    /**
     * Format a number based on the user's Locale
     *
     * @param num the number to format
     * @return the formatted number
     */
    public String formatInteger(long num)
    {
        return getIntegerFormat().format(num);
    }

    /*
     * Methods dealing with sessionMap
     */

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setSession(Map sessionMap)
    {
        this.sessionMap = sessionMap;
    }

    protected Map<String, Object> getSessionMap()
    {
        return sessionMap;
    }

    public Object getSessionObject(String key)
    {
        return sessionMap.get(key);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getSessionObject(String key, Class<T> clazz)
    {
        Object object = getSessionObject(key);
        if (object == null)
        {
            return null;
        }
        
        if (clazz.isAssignableFrom(object.getClass())) 
        {
            return (T)object;
        }
        
        return null;
    }
    
    public void setSessionObject(String key, Object value)
    {
        sessionMap.put(key, value);
    }
    
    public void removeSessionObject(String key)
    {
        sessionMap.remove(key);
    }

    protected void invalidateSession()
    {
        if (sessionMap instanceof org.apache.struts2.dispatcher.SessionMap<?, ?>)
        {
            try
            {
                ((org.apache.struts2.dispatcher.SessionMap<?, ?>) sessionMap).invalidate();
            }
            catch (Exception e)
            {
                // never mind
                logger.error("Session has not been invalidated!", e);
            }
        }
        else
        {
            logger.error("Session has not been invalidated! Unexpected type {}", sessionMap
                .getClass().getName());
        }
    }

    /*
     * Methods dealing with HttpServletRequest
     */

    @Override
    public void setServletRequest(HttpServletRequest httpServletRequest)
    {
        this.httpServletRequest = httpServletRequest;
    }

    protected HttpServletRequest getHttpServletRequest()
    {
        return httpServletRequest;
    }

    public String getContextPath()
    {
        return httpServletRequest.getContextPath();
    }

    /*
     * Methods dealing with internal exceptions list
     */

    public void addException(Exception exception)
    {
        getInternalExceptions().add(exception);
    }

    public List<Exception> getExceptions()
    {
        return Collections.unmodifiableList(getInternalExceptions());
    }

    private List<Exception> getInternalExceptions()
    {
        if (exceptions == null)
        {
            exceptions = new ArrayList<Exception>();
        }
        return exceptions;
    }

    /**
     * Called this from your Exception handling jsp page in order to grab the exception. <br>
     * This version will add the exception to the internal exception list then calls
     * {@link #notifyAdmin(Throwable)}.<br>
     * <br>
     * In struts.xml setup exception mapping:
     *
     * <pre>
     *     [global-exception-mappings]
     *         [exception-mapping exception="java.lang.Exception" result="exception" /]
     *     [/global-exception-mappings]
     *     [global-results]
     *         [result name="exception"]/struts/Exception.jsp[/result]
     *     [/global-results]
     * </pre>
     *
     * In your "exception" jsp file call this method:
     *
     * <pre>
     *     &lt;s:if test=&quot;exceptionCallback(exception)&quot;&gt;&lt;/s:if&gt;
     * </pre>
     *
     * @param e the exception
     * @return false
     */
    public boolean exceptionCallback(Exception e)
    {
        addException(e);
        notifyAdmin(e);
        return false;
    }

    /**
     * A stub method for notifying the admin of an exception.<br>
     * This base version simply logs the exception as an error.<br>
     * Override this method to implement a real notifier.
     *
     * @param e the exception
     */
    public void notifyAdmin(Throwable e)
    {
        logger.error(e.getMessage(), e);
    }

    /**
     * Build a list of messages based on the state of the action. This is used in preparing an error
     * report or notifying the admin.
     *
     * @return a list of messages based on the state of the action
     */
    protected List<String> actionStateMessages()
    {
        List<String> messages = new ArrayList<String>();
        messages.add(String.format("HttpRequest:%s", ServletUtils
            .buildSyntheticRequestUrl(getHttpServletRequest())));
        messages.add(String.format("ActionName:%s", getActionName()));
        messages.add(String.format("ActionClass:%s", getClass().getName()));

        messages.addAll(getActionErrors());
        messages.addAll(getActionMessages());

        for (Map.Entry<String, List<String>> entry : getFieldErrors().entrySet())
        {
            for (String msg : entry.getValue())
            {
                messages.add(String.format("Field Error: [%s] [%s]", entry.getKey(), msg));
            }
        }

        return messages;
    }

    /*
     * Utility UI methods
     */

    /**
     * Abbreviate a string using ellipses (...).<br>
     * If text is longer then len it is truncated to len characters then the
     * last 3 characters are replaced with ellipses (...).
     *
     * @param text the string to abbreviate
     * @param len the maximum length of the returned string (must be greater
     *            then 3)
     * @return the string abbreviated with ellipses to a maximum length of <code>len</code>
     */
    public String abbreviate(String text, int len)
    {
        return StringUtils.abbreviate(text, len);
    }

    /**
     * Build a list of <code>num</code> page numbers centered on the current
     * page. <br>
     * buildPageList(10,20,5) it will return list [8,9,10,11,12].<br>
     * buildPageList(1,20,5) it will return list [1,2,3,4,5].<br>
     *
     * @param page the current page (1 based)
     * @param lastPage the last page (1 based)
     * @param num the maximum number of pages in the list
     * @return a list of page numbers
     */
    public List<Integer> buildPageList(final int page, final int lastPage, final int num)
    {
        return Paging.buildPageList(page, lastPage, num);
    }

}
