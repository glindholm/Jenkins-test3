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

import net.sourceforge.wsup.core.servlet.ServletUtils;

import org.apache.struts2.interceptor.NoParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Action for catching unrecognized actions. <br>
 *
 * In struts.xml configure the default-action-ref and the Unknown action:
 *
 * <pre>
 * &lt;default-action-ref name=&quot;Unknown&quot; /&gt;
 *
 * &lt;action name=&quot;Unknown&quot; class=&quot;net.sourceforge.wsup.struts2.UnknownAction&quot;&gt;
 *     &lt;result&gt;Unknown.jsp&lt;/result&gt;
 * &lt;/action&gt;
 * </pre>
 *
 * @author Greg Lindholm
 *
 */
public class UnknownAction extends ActionBase implements NoParameters
{
    private static final long   serialVersionUID         = -8530907792689678827L;

    private static final Logger logger                   = LoggerFactory
                                                             .getLogger(UnknownAction.class);

    public static final String  ERROR_UNKNOWN_ACTION     = "common.error.unknown.action";
    public static final String  ERROR_UNKNOWN_ACTION_DEF = "Unknown Action Requested URL=[{0}] Remote IP=[{1}]";

    @Override
    public String execute() throws Exception
    {
        String[] args = {
            ServletUtils.buildSyntheticRequestUrl(getHttpServletRequest()),
            getHttpServletRequest().getRemoteAddr() };

        String message = getText(ERROR_UNKNOWN_ACTION, ERROR_UNKNOWN_ACTION_DEF, args);
        logger.error(message);

        addActionError(message);

        return SUCCESS;
    }

}
