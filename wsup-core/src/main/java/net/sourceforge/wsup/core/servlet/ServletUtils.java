/*
 *  Copyright (c) 2010 Greg Lindholm
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

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.wsup.core.EncodeUtils;

import org.apache.commons.lang.StringUtils;

public final class ServletUtils
{
    public static final String METHOD_GET    = "GET";
    public static final String METHOD_POST   = "POST";
    public static final String METHOD_HEAD   = "HEAD";
    public static final String METHOD_PUT    = "PUT";
    public static final String METHOD_DELETE = "DELETE";

    /**
     * Reconstruct a the full URL with parameters from <code>request</code>.
     * If HTTP method is GET then the queryString is used directly,
     * otherwise a synthetic queryString is assembled from the parameters.
     * 
     * @param request the request
     * @return the full URL with parameters
     */
    public static String buildSyntheticRequestUrl(HttpServletRequest request)
    {
        StringBuilder url = new StringBuilder(request.getRequestURL());

        String queryString;
        if (METHOD_GET.equalsIgnoreCase(request.getMethod()))
        {
            queryString = request.getQueryString();
        }
        else
        {
            queryString = queryStringFromParameters(request);
        }

        if (!StringUtils.isBlank(queryString))
        {
            url.append('?').append(queryString);
        }

        return url.toString();
    }

    /**
     * Build a queryString from the parameters. This can be used to synthesize a queryString from a
     * POST request.
     * 
     * @param request
     * @return the queryString (or "" empty string if no parameters);
     */
    public static String queryStringFromParameters(HttpServletRequest request)
    {
        StringBuilder queryString = new StringBuilder();
        boolean first = true;
        Enumeration<?> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String name = (String) enumeration.nextElement();

            for (String value : request.getParameterValues(name))
            {
                if (!first)
                {
                    queryString.append('&');
                }
                else
                {
                    first = false;
                }

                queryString.append(name).append('=').append(EncodeUtils.urlEncode(value));
            }
        }

        return queryString.toString();
    }

    /**
     * Build a queryString from the parameters. This can be used to synthesize a queryString from a
     * POST request.
     * 
     * @param params the parameters map
     * @return the queryString (or "" empty string if no parameters);
     */
    public static String queryStringFromParameters(Map<String, String[]> params)
    {
        StringBuilder queryString = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String[]> entry : params.entrySet())
        {
            String name = entry.getKey();
            String[] values = entry.getValue();
            for (String value : values)
            {
                if (!first)
                {
                    queryString.append('&');
                }
                else
                {
                    first = false;
                }

                queryString.append(name).append('=').append(EncodeUtils.urlEncode(value));
            }
        }

        return queryString.toString();
    }

    /**
     * This string produces the HTTP date format from RFC822, updated by RFC 1123.
     * This is the format you are supposed to use when generating dates.
     */
    public static final String   RFC822_DATE_OUTPUT_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

    /**
     * Format a date into the "official" HTTP date format.
     * @param date <code>Date</code> to be formatted.
     * @return String in RFC822 format.
     */
    public static String formatHttpDate(Date date)
    {
        SimpleDateFormat format = new SimpleDateFormat(RFC822_DATE_OUTPUT_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(date);
    }
    
    /**
     * These are possible date formats that could be encountered when parsing dates.
     */
    public static final String[] INPUT_FORMATS             = { 
        /*
         * RFC 822, updated by RFC 1123
         */
        "EEE, dd MMM yyyy HH:mm:ss Z",
        /*
         * RFC 822, updated by RFC 1123, without space before the time zone
         */
        "EEE, dd MMM yyyy HH:mm:ssZ",
        /*
         * RFC 850, obsoleted by RFC 1036
         */
        "EEEE, dd-MMM-yy HH:mm:ss Z",
        /*
         * RFC 850, obsoleted by RFC 1036, without space before the time zone
         */
        "EEEE, dd-MMM-yy HH:mm:ssZ",
        /*
         *  // ANSI C's asctime() format
         */
        "EEE MMM d HH:mm:ss yyyy"
                                                           };
    
    /**
     * Parse an HTTP date string
     * @param dateString Input date string.
     * @return <code>Date</code> if the string can be parsed, otherwise <code>null</code>.
     */
    public static Date parseHttpDate(String dateString)
    {
        if (dateString == null)
        {
            return null;
        }
        
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        
        for (int i = 0; i < INPUT_FORMATS.length; i++)
        {
            SimpleDateFormat format = new SimpleDateFormat(INPUT_FORMATS[i]);
            format.setTimeZone(gmt);
            try
            {
                return format.parse(dateString);
            }
            catch (ParseException e)
            {
                // try the next pattern
            }
        }
        
        return null;
    }

    private ServletUtils()
    {
    }

    /*
     * Exposed for Unit Test coverage
     */
    static void coverage()
    {
        new ServletUtils();
    }
}
