/*
 *  Copyright (c) 2012 Kevin Hunter
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

package net.sourceforge.wsup.hibernate4.command;

import org.hibernate.HibernateException;

/**
 * Interface for an object that can run one or more <code>DatabaseCommand</code>s against a
 * database.
 * 
 * @author Kevin Hunter
 * @see DatabaseCommand
 */
public interface DatabaseCommandRunner
{
    /**
     * Execute the sequence of <code>DatabasaeCommand</code>s. The <code>DatabaseCommand</code>s
     * will be executed in the context of a single database transaction. The
     * <code>DatabaseCommandRunner</code> will create its own Hibernate <code>Session</code> in
     * which to execute the <code>DatabaseCommand</code>s, and
     * will close this <code>Session</code> at the end of execution.
     * 
     * @param commands
     *            <code>DatabaseCommand</code>s to be executed.
     * @throws HibernateException If one or more commands fail.
     */
    public void execute(DatabaseCommand... commands) throws HibernateException;
}
