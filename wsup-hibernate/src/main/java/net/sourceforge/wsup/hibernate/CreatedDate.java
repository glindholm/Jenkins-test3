/*
 *  Copyright 2010 Kevin Hunter
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

package net.sourceforge.wsup.hibernate;

import java.util.Date;

import net.sourceforge.wsup.hibernate.interceptors.DateAwareInterceptor;

/**
 * Interface representing objects that implement a "created" date property. The
 * {@link DateAwareInterceptor} will recognize this interface, and will
 * insert the <code>Date</code> into the object automatically.
 * 
 * @author Kevin Hunter
 * @see DateAwareInterceptor
 * 
 */
public interface CreatedDate
{
	/**
	 * Getter for "created" date.
	 * 
	 * @return <code>Date</code> this object was created
	 */
	public Date getCreated();

	/**
	 * Setter for "created" date.
	 * 
	 * @param created
	 *            <code>Date</code> this object was created
	 */
	public void setCreated(Date created);
}
