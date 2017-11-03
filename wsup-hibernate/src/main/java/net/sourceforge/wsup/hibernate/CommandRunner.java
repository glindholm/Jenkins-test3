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
 * <code>Command</code>s.
 * 
 * @author Kevin Hunter
 * 
 */
public interface CommandRunner
{
	/**
	 * Execute the sequence of <code>Command</code>s. The <code>Command</code>s
	 * will be executed in the context of a single database transaction. The
	 * <code>CommandRunner</code> will create its own Hibernate
	 * <code>Session</code> in which to execute the <code>Command</code>s, and
	 * will close this <code>Session</code> at the end of execution.
	 * <p>
	 * If the database transaction is successful, execution will proceed
	 * normally. If a <code>HibernateException</code> is thrown, this indicates
	 * that the transaction failed and was rolled back.
	 * <code>CommandRunner</code> implementations may or may not implement their
	 * own retry logic.
	 * </p>
	 * 
	 * @param commands
	 *            <code>Command</code>s to be executed.
	 * @throws HibernateException
	 */
	public void execute(Command... commands) throws HibernateException;
}
