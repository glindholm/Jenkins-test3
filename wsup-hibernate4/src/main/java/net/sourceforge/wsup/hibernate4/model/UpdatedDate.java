/*
 * Copyright 2012 Kevin Hunter
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.sourceforge.wsup.hibernate4.model;

import java.util.Date;

import net.sourceforge.wsup.hibernate4.interceptors.DateAwareInterceptor;

/**
 * Interface representing objects that implement an "updated" date property. The
 * {@link DateAwareInterceptor} will recognize this interface, and will insert
 * the <code>Date</code> into the object automatically.
 * 
 * @author Kevin Hunter
 * @see DateAwareInterceptor
 */
public interface UpdatedDate
{
    /**
     * Getter for "updated" date.
     * 
     * @return <code>Date</code> this object was updated
     */
    public Date getUpdated();

    /**
     * Setter for "updated" date.
     * 
     * @param updated
     *            <code>Date</code> this object was updated
     */
    public void setUpdated(Date updated);
}
