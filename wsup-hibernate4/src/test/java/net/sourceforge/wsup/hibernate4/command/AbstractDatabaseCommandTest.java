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
import net.sourceforge.wsup.hibernate4.database.TestDatabase;
import net.sourceforge.wsup.hibernate4.database.TestDatabaseManager;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.impl.MockLogger;

public class AbstractDatabaseCommandTest
{
    private TestDatabase database;

    public AbstractDatabaseCommandTest()
    {
    }

    @Before
    public void setup() throws Exception
    {
        MockLogger.get("org.hibernate").setDebug();
        MockLogger.get("com.mchange.v2.c3p0").setWarn();
        MockLogger.get("com.mchange.v2.log").setWarn();

        database = TestDatabaseManager.openTestDatabaseIfRequired();
    }

    @Test(expected = IllegalStateException.class)
    public void failureToSetResultThrows()
    {
        TestCommand command = new TestCommand(false);
        DatabaseCommandRunnerImpl runner = new DatabaseCommandRunnerImpl(database, 1);
        runner.execute(command);
    }

    @Test
    public void settingResultDoesntThrow()
    {
        TestCommand command = new TestCommand(true);
        DatabaseCommandRunnerImpl runner = new DatabaseCommandRunnerImpl(database, 1);
        runner.execute(command);
    }

    private static class TestCommand extends AbstractDatabaseCommand
    {
        private boolean set;

        public TestCommand(boolean set)
        {
            this.set = set;
        }

        @Override
        public void execute(DatabaseCommandContext commandContext,
                            DataAccessContext dataAccessContext)
        {
            if (set)
            {
                setResult(DatabaseCommandResult.OK);
            }
        }
    }
}
