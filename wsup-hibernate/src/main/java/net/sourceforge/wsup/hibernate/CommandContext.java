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

import java.util.List;

/**
 * Interface specifying the object that is passed to each database
 * <code>Command</code> during the <code>PreExecute</code>,
 * <code>PreTransaction</code>, <code>PostTransaction</code> and
 * <code>PostExecute</code> phases.
 * 
 * @author Kevin Hunter
 * @see Command
 * @see PreExecute
 * @see PreTransaction
 * @see PostTransaction
 * @see PostExecute
 * 
 */
public interface CommandContext
{
	/**
	 * Retrieve the current attempt number. This will be zero on the initial
	 * execution attempt on a sequence of <code>Command</code>s, 1 on the first
	 * retry, etc.  Thus, among other things, it will always return 0
	 * during the <code>PreExecute</code> phase.
	 * 
	 * @return Retry count.
	 */
	public int getAttemptNumber();
	
	/**
	 * Retrieve the entire list of <code>Command</code>s that are being
	 * executed together.
	 * @return <code>List</code> of <code>Command</code>s.
	 */
	public List<Command> getCommands();
}
