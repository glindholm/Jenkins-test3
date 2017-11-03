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

import java.util.ArrayList;
import java.util.List;

/**
 * An object with this interface is passed to a <code>DatabaseCommand</code> when it is executing to
 * give it context on the entire operation.
 * 
 * @author Kevin Hunter
 * @see DatabaseCommand
 */
public class DatabaseCommandContext
{
    private final List<DatabaseCommand> commands;
    private int                         attemptNumber;

    public DatabaseCommandContext(DatabaseCommand... commands)
    {
        this.commands = new ArrayList<DatabaseCommand>();
        for (DatabaseCommand command : commands)
        {
            this.commands.add(command);
        }
    }

    public void setAttemptNumber(int attemptNumber)
    {
        this.attemptNumber = attemptNumber;
    }

    /**
     * Retrieve the current attempt number. This will be zero on the initial
     * execution attempt on a sequence of <code>DatabaseCommand</code>s, 1 on the first
     * retry, etc. Thus, among other things, it will always return 0
     * during the <code>PreExecute</code> phase.
     * 
     * @return Retry count.
     */
    public int getAttemptNumber()
    {
        return attemptNumber;
    }

    /**
     * Retrieve the entire list of <code>DatabaseCommand</code>s that are being
     * executed together.
     * 
     * @return <code>List</code> of <code>DatabaseCommand</code>s.
     */
    public List<DatabaseCommand> getCommands()
    {
        return commands;
    }
}
