/*
 *  Copyright 2010 Kevin
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

/**
 * Interface specifying an object that is capable of running a series of
 * commands.
 * 
 * @author Kevin Hunter
 * 
 */
public interface ContextCommandRunner
{
	/**
	 * Execute the sequence of <code>Command</code>s using the provided
	 * <code>DatabaseContext</code>. The <code>Command</code>s will be executed
	 * in the context of a single database transaction. The
	 * <code>ContextCommandRunner</code> will begin a <code>Session</code> in
	 * the <code>DatabaseContext</code> if necessary. At the end of successful
	 * execution, this <code>Session</code> will be left open so that it can be
	 * used for subsequent transactions, lazy loading, etc.
	 * <p>
	 * If the database transaction is successful, execution will proceed
	 * normally. If a <code>HibernateException</code> is thrown, this indicates
	 * that the transaction failed and was rolled back and, as a side effect,
	 * the <code>Session</code> in the <code>DatabaseContext</code> closed.
	 * </p>
	 * <p>
	 * <code>ContextCommandRunner</code> implementations may or may not
	 * implement their own retry logic. Because retries may require that the
	 * underlying Hibernate <code>Session</code> be closed and a new one
	 * created, if the object implements retries, the underlying
	 * <code>Session</code> object may change during the execution of this
	 * method. Applications attempting to use multiple <code>execute</code>
	 * operations on the same <code>DatabaseContext</code> object need to be
	 * aware of this.
	 * </p>
	 * 
	 * @param context
	 *            <code>DatabaseContext</code> object on which to execute the
	 *            <code>Command</code>s.
	 * @param commands
	 *            <code>Command</code>s to be executed.
	 * @throws HibernateException
	 */
	public void execute(DatabaseContext context, Command... commands)
			throws HibernateException;
}
