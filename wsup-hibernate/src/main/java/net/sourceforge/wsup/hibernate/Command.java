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
 * Interface associated with database commands.
 * 
 * @author Kevin Hunter
 * 
 */
public interface Command
{
	/**
	 * Execute the operation associated with the command. Note that this method
	 * could be called more than once if the command has to be retried. If
	 * finer-grained control is necessary, the <code>Command</code> object can
	 * implement {@link PreExecute}, {@link PreTransaction},
	 * {@link PostTransaction} or {@link PostExecute}.
	 * 
	 * @param context
	 *            <code>CommandExecutionContext</code> object
	 * @see CommandContext
	 */
	public void execute(CommandExecutionContext context);
}
