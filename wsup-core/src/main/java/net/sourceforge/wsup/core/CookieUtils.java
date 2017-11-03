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

package net.sourceforge.wsup.core;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.wsup.core.servlet.HeaderUtils;

/**
 * Utilities for dealing with <code>Cookie</code>s in requests and responses.
 * 
 * @author Kevin Hunter
 */

public class CookieUtils
{
    /**
     * Retrieve a <code>Cookie</code> from a request.
     * 
     * @param request <code>HttpServletRequest</code> to look in.
     * @param cookieName Name of the cookie as a <code>String</code>. The name is case-sensitive.
     * @return <code>Cookie</code> instance if one is defined for that name, otherwise
     *         <code>null</code>.
     */
    public static Cookie getCookie(HttpServletRequest request, String cookieName)
    {
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
        {
            for (Cookie cookie : cookies)
            {
                if (BeanUtils.safeEquals(cookie.getName(), cookieName))
                {
                    return cookie;
                }
            }
        }

        return null;
    }

    /**
     * Retrieve the value portion of a <code>Cookie</code> from a request.
     * 
     * @param request <code>HttpServletRequest</code> to look in.
     * @param cookieName Name of the cookie as a <code>String</code>. The name is case-sensitive.
     * @return Value of the cookie as a <code>String</code> if a cookie is defined for that name,
     *         otherwise <code>null</code>.
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName)
    {
        Cookie cookie = getCookie(request, cookieName);
        if (cookie == null)
        {
            return null;
        }

        return cookie.getValue();
    }

    /**
     * Indicates whether a host name is all numeric (i.e. is an IP address).
     * 
     * @param host Host name to test. Must be non-<code>null</code>.
     * @return <code>true</code> if the host name contains only digits and the '.' character,
     *         <code>false</code> if it contains any other characters.
     */
    public static boolean isAllNumeric(String host)
    {
        int length = host.length();
        for (int i = 0; i < length; i++)
        {
            char c = host.charAt(i);
            switch (c)
            {
            case '.':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                break;
            default:
                return false;
            }
        }

        return true;
    }

    /**
     * This routine will extract the domain name portion of the request host name in a format
     * that can be used in setting a domain-wide cookie. Returns <code>null</code> if the
     * host name is <code>null</code>, is numeric-only, or doesn't contain at least one '.'
     * character (e.g. "localhost").
     * 
     * @param request <code>HttpServletRequest</code> from which to obtain the host name.
     * @param topLevel If <code>true</code>, the top-level domain will be returned. If
     *            <code>false</code>,
     *            only the host name will be stripped off. Thus, for example, a host name of
     *            <code>www.some.domain.com</code> would result in <code>.some.domain.com</code> if
     *            this value is <code>false</code> and <code>.domain.com</code> if <code>true</code>.
     * @return <code>String</code> containing the domain name string. This will start with a "."
     *         character.
     */
    public static String getDomainForCookie(HttpServletRequest request, boolean topLevel)
    {
        String host = request.getHeader(HeaderUtils.HEADER_HOST);
        return getDomainForCookie(host, topLevel);
    }

    /**
     * This routine will extract the domain name portion of a host name in a format
     * that can be used in setting a domain-wide cookie. Returns <code>null</code> if the
     * host name is <code>null</code>, is numeric-only, or doesn't contain at least one '.'
     * character (e.g. "localhost").
     * 
     * @param host Host name.
     * @param topLevel If <code>true</code>, the top-level domain will be returned. If
     *            <code>false</code>,
     *            only the host name will be stripped off. Thus, for example, a host name of
     *            <code>www.some.domain.com</code> would result in <code>.some.domain.com</code> if
     *            this value is <code>false</code> and <code>.domain.com</code> if <code>true</code>.
     * @return <code>String</code> containing the domain name string. This will start with a "."
     *         character.
     */
    public static String getDomainForCookie(String host, boolean topLevel)
    {
        if (host == null)
        {
            return null;
        }

        if (isAllNumeric(host))
        {
            return null;
        }

        int dotIndex = host.indexOf('.');
        if (dotIndex < 0)
        {
            return null;
        }

        String[] parts = host.split("\\.");

        StringBuilder result = new StringBuilder();

        // normally, skip over the host part.
        int i = 1;

        if (parts.length > 2)
        {
            if (topLevel)
            {
                // include only the last two components
                i = parts.length - 2;
            }
        }
        else
        {
            // if the host name is just "domain.com", include the whole thing.
            i = 0;
        }

        for (; i < parts.length; i++)
        {
            result.append('.');
            result.append(parts[i]);
        }

        return result.toString();
    }

    /**
     * Creates a domain-level cookie with the specified name and value for the
     * domain in an <code>HttpServletRequest</code>.
     * 
     * @param request <code>HttpServletRequest</code> from which to obtain the
     *            host name.
     * @param topLevel If <code>true</code>, attach the cookie to the top-level domain instead of
     *            the host's domain.
     * @param name Name for the cookie
     * @param value Value for the cookie.
     * @return <code>Cookie</code> instance.
     */
    public static Cookie createDomainCookie(HttpServletRequest request,
                                            boolean topLevel,
                                            String name,
                                            String value)
    {
        Cookie cookie = new Cookie(name, value);
        String domainName = getDomainForCookie(request, topLevel);
        if (domainName != null)
        {
            cookie.setDomain(domainName);
        }
        return cookie;
    }
    
    private CookieUtils()
    {
    }
    
    /*package*/ static void codeCoverage()
    {
        new CookieUtils();
    }
}
