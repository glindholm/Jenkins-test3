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

package net.sourceforge.wsup.struts2.test;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Action for testing ActionTransactions.  This puts the name/value
 * pair into the session.
 * 
 * @author Kevin Hunter
 *
 */
public class Test4Action extends ActionSupport implements ServletRequestAware
{
    private static final long serialVersionUID = -2866700423350670399L;

    private HttpServletRequest request;
    
    public Test4Action()
    {
    }
    

    @Override
    public void setServletRequest(HttpServletRequest request)
    {
        this.request = request;        
    }
   
    public String execute()
    {
        String value = request.getHeader("header-name");
        
        request.getSession(true).setAttribute("name", value);
        
        return "success";
    }

}
