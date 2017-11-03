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

import net.sourceforge.wsup.hibernate4.database.DataAccessContext;

/**
 * Basic interface for commands that will be executed against the database.
 * 
 * @author Kevin Hunter
 * 
 */
public interface DatabaseCommand
{
    /**
     * Execute the command.
     * 
     * @param commandContext <code>DatabaseCommandContext</code> object providing the command with
     *            context as to what other commands are running, retry information, etc.
     * @param dataAccessContext <code>DataAccessContext</code> providing access to the database
     *            session/transaction.
     */
    public void execute(DatabaseCommandContext commandContext, DataAccessContext dataAccessContext);

    /**
     * Result of the command. Typically one of the constants in <code>DatabaseCommandResult</code>
     * or an app-specific extension.
     * 
     * @return Result code.
     * @see DatabaseCommandResult
     */
    public int getResult();
}
