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

/**
 * Interface that may be implemented by database <code>Command</code>s to allow
 * fine-grained observation of the <code>Command</code> execution process.
 * 
 * @author Kevin Hunter
 * @see Command
 * 
 */
public interface PostTransaction
{

	/**
	 * Method called just after the termination of the database transaction on
	 * which the <code>Command</code> was executed. If a <code>Command</code>
	 * sequence is retried due to a database exception, this method will be
	 * called at the end of each retry as well. 
	 * 
	 * @param context
	 *            <code>CommandContext</code> object
	 * @param successful
	 *            <code>true</code> if the transaction was successfully
	 *            committed, <code>false</code> if it failed and was rolled back
	 * @see CommandContext
	 */

	public void postTransaction(CommandContext context, boolean successful);
}
