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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sourceforge.wsup.hibernate4.database.TestDatabase;
import net.sourceforge.wsup.hibernate4.database.TestDatabaseManager;
import net.sourceforge.wsup.hibernate4.testClasses.MockMonitoringCommand;
import net.sourceforge.wsup.hibernate4.testClasses.MockSimpleCommand;
import net.sourceforge.wsup.hibernate4.testClasses.MockThrowingCommand;
import net.sourceforge.wsup.hibernate4.testClasses.MockThrowingCommandWithRetryable;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.LockAcquisitionException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.impl.MockLogger;

public class DatabaseCommandRunnerImplTest
{
    private static TestDatabase database;

    public DatabaseCommandRunnerImplTest()
    {
    }

    @BeforeClass
    public static void setup() throws Exception
    {
        MockLogger.get("org.hibernate").setDebug();
        MockLogger.get("com.mchange.v2.c3p0").setWarn();
        MockLogger.get("com.mchange.v2.log").setWarn();

        database = TestDatabaseManager.openTestDatabaseIfRequired();
    }

    private DatabaseCommandRunnerImpl createObject(int retries)
    {
        return new DatabaseCommandRunnerImpl(database, retries);
    }

    @Test
    public void executesCommandsWithoutOptionalInterfaces()
    {
        MockSimpleCommand command = new MockSimpleCommand();
        DatabaseCommandRunnerImpl object = createObject(1);
        object.execute(command);
        assertEquals(1, command.wasExecuted());
    }

    @Test
    public void executesAllOptionalCommandInterfaces()
    {
        MockMonitoringCommand command = new MockMonitoringCommand();
        DatabaseCommandRunnerImpl object = createObject(1);
        object.execute(command);

        assertEquals(1, command.wasPreTransactioned());
        assertEquals(1, command.wasPreExecuted());
        assertEquals(1, command.wasExecuted());
        assertEquals(1, command.wasPostExecuted());
        assertEquals(1, command.wasPostTransactioned());
        assertTrue(command.wasTransactionSuccessful());
        assertTrue(command.wasExecuteSuccessful());
    }

    @Test
    public void willRetryOnGlobalRetryExceptionThrown()
    {
        RuntimeException[] exceptions = { new LockAcquisitionException(null, null) };
        MockThrowingCommand command = new MockThrowingCommand(exceptions);
        DatabaseCommandRunnerImpl object = createObject(2);
        object.execute(command);
        assertEquals(2, command.wasExecuted());
    }

    @Test
    public void willThrowWhenGlobalRetriesExhausted()
    {
        RuntimeException[] exceptions = {
            new LockAcquisitionException(null, null),
            new LockAcquisitionException(null, null),
            new LockAcquisitionException(null, null) };
        MockThrowingCommand command = new MockThrowingCommand(exceptions);
        DatabaseCommandRunnerImpl object = createObject(2);
        try
        {
            object.execute(command);
            fail("Didn't throw");
        }
        catch (LockAcquisitionException e)
        {
        }

        assertEquals(2, command.wasExecuted());
    }

    @Test
    public void willThrowImmediatelyIfNotGlobalRetryableCommand()
    {
        RuntimeException[] exceptions = { new ConstraintViolationException(null, null, null) };
        MockThrowingCommand command = new MockThrowingCommand(exceptions);
        DatabaseCommandRunnerImpl object = createObject(2);
        try
        {
            object.execute(command);
            fail("Didn't throw");
        }
        catch (ConstraintViolationException e)
        {
        }

        assertEquals(1, command.wasExecuted());
    }

    @Test
    public void willRetryOnCommandRetryExceptionThrown()
    {
        RuntimeException[] exceptions = { new ConstraintViolationException(null, null, null) };
        Class<?>[] classes = { ConstraintViolationException.class };

        MockThrowingCommandWithRetryable command = new MockThrowingCommandWithRetryable(exceptions,
                                                                                        classes);
        DatabaseCommandRunnerImpl object = createObject(2);
        object.execute(command);
        assertEquals(2, command.wasExecuted());
    }

    @Test
    public void willThrowWhenCommandRetriesExhausted()
    {
        RuntimeException[] exceptions = {
            new ConstraintViolationException(null, null, null),
            new ConstraintViolationException(null, null, null),
            new ConstraintViolationException(null, null, null) };
        Class<?>[] classes = { ConstraintViolationException.class };
        MockThrowingCommandWithRetryable command = new MockThrowingCommandWithRetryable(exceptions,
                                                                                        classes);
        DatabaseCommandRunnerImpl object = createObject(2);
        try
        {
            object.execute(command);
            fail("Didn't throw");
        }
        catch (ConstraintViolationException e)
        {
        }

        assertEquals(2, command.wasExecuted());
    }

    @Test
    public void willThrowImmediatelyIfNotCommandRetryableCommand()
    {
        RuntimeException[] exceptions = { new ConstraintViolationException(null, null, null) };
        Class<?>[] classes = { LockAcquisitionException.class };
        MockThrowingCommandWithRetryable command = new MockThrowingCommandWithRetryable(exceptions,
                                                                                        classes);
        DatabaseCommandRunnerImpl object = createObject(2);
        try
        {
            object.execute(command);
            fail("Didn't throw");
        }
        catch (ConstraintViolationException e)
        {
        }

        assertEquals(1, command.wasExecuted());
    }
}
