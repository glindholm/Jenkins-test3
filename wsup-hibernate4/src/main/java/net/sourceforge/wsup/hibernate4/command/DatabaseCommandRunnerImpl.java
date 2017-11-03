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

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.wsup.hibernate4.database.BaseDatabase;
import net.sourceforge.wsup.hibernate4.database.DataAccessContext;
import net.sourceforge.wsup.hibernate4.database.DatabaseContext;

import org.hibernate.HibernateException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;

public class DatabaseCommandRunnerImpl implements DatabaseCommandRunner
{
    private final BaseDatabase database;
    private final int          maxAttempts;

    public DatabaseCommandRunnerImpl(BaseDatabase database, int maxAttempts)
    {
        this.database = database;
        this.maxAttempts = maxAttempts;
    }

    @Override
    public void execute(DatabaseCommand... commands) throws HibernateException
    {
        DatabaseContext databaseContext = database.createDatabaseContext();

        try
        {
            execute(databaseContext, commands);
        }
        finally
        {
            databaseContext.closeSession();
        }
    }

    public void execute(DatabaseContext databaseContext, DatabaseCommand... commands)
        throws HibernateException
    {
        DatabaseCommandContext commandContext = new DatabaseCommandContext(commands);

        handlePreExecute(commandContext);

        boolean success = false;

        for (int attempt = 0; attempt < maxAttempts; attempt++)
        {
            try
            {
                commandContext.setAttemptNumber(attempt);

                databaseContext.beginSession(attempt > 0);

                handlePreTransaction(commandContext);

                databaseContext.beginTransaction();

                handleExecute(commandContext, databaseContext.createDataAccessContext());

                databaseContext.commitTransaction();

                handlePostTransaction(commandContext, true);

                success = true;

                break;
            }
            catch (RuntimeException e)
            {
                databaseContext.destroySession();

                if (!shouldRetry(e, attempt, commands))
                {
                    handlePostTransaction(commandContext, false);
                    handlePostExecute(commandContext, false);
                    throw e;
                }

                handlePostTransaction(commandContext, false);
            }
        }

        handlePostExecute(commandContext, success);
    }

    /**
     * Overrideable method that determines whether or not to retry following an
     * exception. The default implementation retries if the maximum number of
     * attempts has not been reached and the exception is retryable.
     * <p>
     * Whether or not an exception is retryable is determined two ways - this class's
     * {@link #isRetryable(RuntimeException)} method is checked, plus each individual command that
     * implements {@link RetryableExceptions} is checked.
     * </p>
     * 
     * @param e
     *            The exception that occurred.
     * @param attempt
     *            The current attempt number
     * @return <code>true</code> if execution should be retried, <code>false</code> if not.
     * @see #isRetryable(RuntimeException)
     */
    protected boolean shouldRetry(RuntimeException e, int attempt, DatabaseCommand[] commands)
    {
        if (attempt >= maxAttempts - 1)
        {
            return false;
        }

        if (isRetryable(e))
        {
            return true;
        }

        for (DatabaseCommand command : commands)
        {
            if (command instanceof RetryableExceptions)
            {
                if (((RetryableExceptions) command).isRetryable(e))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Perform the <code>PreExecute</code> phase on any <code>Command</code>s
     * that support it.
     * 
     * @see PreExecute
     */
    public void handlePreExecute(DatabaseCommandContext commandContext)
    {
        for (DatabaseCommand command : commandContext.getCommands())
        {
            if (command instanceof PreExecute)
            {
                ((PreExecute) command).preExecute(commandContext);
            }
        }
    }

    /**
     * Perform the <code>PreTransaction</code> phase on any <code>Command</code> s that support it.
     * 
     * @see PreTransaction
     */
    public void handlePreTransaction(DatabaseCommandContext commandContext)
    {
        for (DatabaseCommand command : commandContext.getCommands())
        {
            if (command instanceof PreTransaction)
            {
                ((PreTransaction) command).preTransaction(commandContext);
            }
        }
    }

    /**
     * Execute each of the commands.
     */
    public void handleExecute(DatabaseCommandContext commandContext,
                              DataAccessContext dataAccessContext)
    {
        for (DatabaseCommand command : commandContext.getCommands())
        {
            command.execute(commandContext, dataAccessContext);
        }
    }

    /**
     * Perform the <code>PostTransaction</code> phase on any <code>Command</code>s that support it.
     * 
     * @param successful
     *            whether or not the transaction was successful
     * @see PostTransaction
     */
    public void handlePostTransaction(DatabaseCommandContext commandContext, boolean successful)
    {
        for (DatabaseCommand command : commandContext.getCommands())
        {
            if (command instanceof PostTransaction)
            {
                ((PostTransaction) command).postTransaction(commandContext, successful);
            }
        }
    }

    /**
     * Perform the <code>PostExecute</code> phase on any <code>Command</code>s
     * that support it.
     * 
     * @param successful
     *            whether or not the execution was successful
     * @see PostExecute
     */
    public void handlePostExecute(DatabaseCommandContext commandContext, boolean successful)
    {
        for (DatabaseCommand command : commandContext.getCommands())
        {
            if (command instanceof PostExecute)
            {
                ((PostExecute) command).postExecute(commandContext, successful);
            }
        }
    }

    /**
     * Overrideable method that determines whether a particular exception
     * represents a situation that can be retried, regardless of the nature of
     * the <code>Command</code>s in question.
     * <p>
     * The default implementation retries on:
     * </p>
     * <ul>
     * <li><code>LockAcquisitionException</code>s (typically indicating that there was a deadlock or
     * timeout), and</li>
     * <li>
     * <code>JDBCConnectionException</code>s (typically indicating that there has been an issue
     * talking to the database).</li>
     * </ul>
     * 
     * @param e
     *            The exception that occurred.
     * @return <code>true</code> if execution should be retried on this type of
     *         exception, <code>false</code> if not.
     */
    protected boolean isRetryable(RuntimeException e)
    {
        return RETRYABLE.contains(e.getClass());
    }

    /*
     * Exceptions that we will automatically retry on.
     */
    private static final Set<Class<?>> RETRYABLE = new HashSet<Class<?>>();
    static
    {
        RETRYABLE.add(LockAcquisitionException.class);
        RETRYABLE.add(JDBCConnectionException.class);
    }
}
