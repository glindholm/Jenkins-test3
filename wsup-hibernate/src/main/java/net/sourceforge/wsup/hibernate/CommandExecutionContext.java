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

/**
 * Interface specifying the object that is passed to <code>Command</code>s
 * during the <code>execute</code> phase.
 * 
 * @author Kevin Hunter
 * @see Command#execute(CommandExecutionContext)
 */
public interface CommandExecutionContext extends CommandContext
{
	/**
	 * Retrieve a <code>DataAccessContext</code> suitable to be passed to DAO's.
	 * 
	 * @return <code>DataAccessContext</code> object
	 */
	public DataAccessContext getDataAccessContext();

}
