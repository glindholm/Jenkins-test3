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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class inspects each incoming request to which it is mapped
 * and checks to see if it has a character set encoding on it.
 * If it does not, it modifies the request so that it has one set
 * to UTF-8.  This guarantees a consistent interpretation of form
 * information if the browser neglects to specify an encoding.
 * 
 * @author Kevin Hunter
 */

public class UTF8CharsetFilter implements Filter
{
    private static Logger log = LoggerFactory.getLogger(UTF8CharsetFilter.class);
    
    private FilterConfig  filterConfig;

    public UTF8CharsetFilter()
    {
    }

    public void init(FilterConfig filterConfig) throws ServletException
    {
        log.info("Initializing filter: " + filterConfig.getFilterName());
        this.filterConfig = filterConfig;
    }

    public void destroy()
    {
        log.info("Destroying filter: " + filterConfig.getFilterName());
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain next)
        throws IOException, ServletException
    {
        if (StringUtils.trimToNull(request.getCharacterEncoding()) == null)
        {
            request.setCharacterEncoding("UTF-8");
        }

        next.doFilter(request, response);
    }
}
