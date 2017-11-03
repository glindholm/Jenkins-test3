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

import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * This interface specifies an object that can be inserted into the
 * <code>BaseDatabase</code> so that <code>Session</code>s and
 * <code>Transaction</code>s can be intercepted to have additional behavior. For
 * example, this is a way to add "wrappers" to the real <code>Session</code>
 * and/or <code>Transaction</code> objects so that they will throw exceptions
 * when you want.
 * 
 * @author Kevin Hunter
 * 
 */
public interface DatabaseTestInterceptor
{
	/**
	 * This method is called as the first step in opening a database.
	 * @throws HibernateException
	 */
	public void interceptOpenDatabase(BaseDatabase database) throws HibernateException;
	
	/**
	 * This method is called as the first step in closing a database.
	 * @param database
	 * @throws HibernateException
	 */
	public void interceptCloseDatabase(BaseDatabase database) throws HibernateException;
	
	/**
	 * This method is called after a <code>Session</code> has been created in
	 * <code>BaseDatabase</code>, but before it is returned. The return value
	 * from this method will be the <code>Session</code> actually returned, so
	 * it can be used to wrap or replace the "real" one.
	 * 
	 * @param session
	 *            <code>Session</code> actually returned by the underlying
	 *            <code>SessionFactory</code>.
	 * @return <code>Session</code> that will be returned from
	 *         {@link BaseDatabase#createHibernateSession(boolean)}.
	 * @throws HibernateException
	 */
	public Session interceptCreateSession(Session session) throws HibernateException;

	/**
	 * This method is called as the first step in
	 * {@link BaseDatabase#closeHibernateSession(Session)}. The return value
	 * from this method is the <code>Session</code> that will actually be
	 * closed, so it can be used to "unwrap" the "real" <code>Session</code> if
	 * required.
	 * 
	 * @param session
	 *            <code>Session</code> previously obtained from
	 *            {@link BaseDatabase#createHibernateSession(boolean)} (and
	 *            possibly previously wrapped by this interceptor).
	 * @return <code>Session</code> that will actually be "closed."
	 * @throws HibernateException
	 */
	public Session interceptCloseSession(Session session) throws HibernateException;
}
