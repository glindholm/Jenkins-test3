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

package net.sourceforge.wsup.hibernate.testSupport;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import net.sourceforge.wsup.hibernate.BaseDatabase;
import net.sourceforge.wsup.hibernate.DatabaseTestInterceptor;

/**
 * This is a convenience base class for classes that want to implement
 * <code>DatabaseTestInterceptor</code>. It provides "no-op" methods for all the
 * methods in <code>DatabaseTestInterceptor</code>.
 * 
 * @author Kevin Hunter
 * @see DatabaseTestInterceptor
 */
public class AbstractDatabaseTestInterceptor implements DatabaseTestInterceptor
{
	/**
	 * Constructor.
	 */
	public AbstractDatabaseTestInterceptor()
	{
	}

	@Override
	public void interceptCloseDatabase(BaseDatabase database)
			throws HibernateException
	{
	}

	@Override
	public void interceptOpenDatabase(BaseDatabase database)
			throws HibernateException
	{
	}

	@Override
	public Session interceptCloseSession(Session session)
			throws HibernateException
	{
		return session;
	}

	@Override
	public Session interceptCreateSession(Session session)
			throws HibernateException
	{
		return session;
	}
}
