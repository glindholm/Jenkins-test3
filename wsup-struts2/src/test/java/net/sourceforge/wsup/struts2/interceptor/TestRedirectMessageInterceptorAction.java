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

import com.opensymphony.xwork2.ActionSupport;

public class TestRedirectMessageInterceptorAction extends ActionSupport
{
    private static final long serialVersionUID = 3497169873547118291L;

    public String doBefore()
    {
        addActionError("ActionError1");
        addActionError("ActionError2");
        addActionMessage("ActionMessage1");
        addActionMessage("ActionMessage2");
        addFieldError("field1", "Field1 Error1");
        addFieldError("field1", "Field1 Error2");
        addFieldError("field2", "Field2 Error1");
        addFieldError("field2", "Field2 Error2");
        return SUCCESS;
    }

    public String doAfter()
    {
        return SUCCESS;
    }

}
