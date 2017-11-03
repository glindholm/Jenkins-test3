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

package net.sourceforge.wsup.hibernate4.testClasses;

import static org.junit.Assert.assertEquals;
import net.sourceforge.wsup.hibernate4.command.DatabaseCommandContext;
import net.sourceforge.wsup.hibernate4.database.DataAccessContext;

public class MockThrowingCommand extends MockMonitoringCommand
{
    private RuntimeException[] exceptions;
    private int                executeCount = 0;

    public MockThrowingCommand(RuntimeException[] exceptions)
    {
        this.exceptions = exceptions;
    }

    @Override
    public void execute(DatabaseCommandContext commandContext, DataAccessContext dataAccessContext)
    {
        // ensure that the attempt number is being set properly
        assertEquals(executeCount, commandContext.getAttemptNumber());

        if (executeCount < exceptions.length)
        {
            executeCount++;
            throw exceptions[executeCount - 1];
        }

        executeCount++;
    }

    @Override
    public int wasExecuted()
    {
        return executeCount;
    }
}
