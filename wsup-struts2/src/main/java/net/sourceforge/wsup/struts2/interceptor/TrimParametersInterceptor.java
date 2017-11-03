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
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class TrimParametersInterceptor implements Interceptor
{
    private static final long serialVersionUID = 6387549824746207667L;

    public TrimParametersInterceptor()
    {
        super();
    }

    private Set<String> excludeParmsSet = Collections.emptySet();

    @Override
    public String intercept(ActionInvocation invocation) throws Exception
    {
        Map<String, Object> parameters = invocation.getInvocationContext().getParameters();
        for (Entry<String, Object> entry : parameters.entrySet())
        {
            if (!excludeParmsSet.contains(entry.getKey().toLowerCase()))
            {
                if (entry.getValue() instanceof String[])
                {
                    String[] values = (String[]) entry.getValue();
                    for (int i = 0; i < values.length; i++)
                    {
                        values[i] = StringUtils.trimToEmpty(values[i]);
                    }
                }
            }
        }

        return invocation.invoke();
    }

    @Override
    public void destroy()
    {
    }

    @Override
    public void init()
    {
    }

    /**
     * @param excludeParams a comma separated list of parameter names (case-insensitive) that are to
     *            be excluded from trimming.
     */
    public void setExcludeParams(String excludeParams)
    {
        excludeParmsSet = new HashSet<String>(Arrays.asList(StringUtils.split(excludeParams
            .toLowerCase(), ',')));
    }

    /**
     * Exposed for testing
     *
     * @return the set of lowercase exclude paramaters.
     */
    Set<String> getExcludeParmsSet()
    {
        return excludeParmsSet;
    }

}
