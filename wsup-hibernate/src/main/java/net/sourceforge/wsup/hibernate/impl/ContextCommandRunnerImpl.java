/*
 * Copyright 2010 Kevin Hunter
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.sourceforge.wsup.hibernate.impl;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.wsup.hibernate.Command;
import net.sourceforge.wsup.hibernate.ContextCommandRunner;
import net.sourceforge.wsup.hibernate.DatabaseContext;
import net.sourceforge.wsup.hibernate.RetryableExceptions;

import org.hibernate.HibernateException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;

/**
 * Implementation of the <code>ContextCommandRunner</code> interface that
 * handles retries automatically.
 * 
 * @author Kevin Hunter
 * @see ContextCommandRunner
 * 
 */
public class ContextCommandRunnerImpl implements ContextCommandRunner
{
	private final int maxAttempts;

	/**
	 * Constructor.
	 * 
	 * @param maxAttempts
	 *            Maximum number of times the runner will attempt to execute the
	 *            commands before giving up.
	 */
	public ContextCommandRunnerImpl(int maxAttempts)
	{
		this.maxAttempts = maxAttempts;
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.ContextCommandRunner#execute(net.sourceforge.wsup.hibernate.DatabaseContext,
	 *      net.sourceforge.wsup.hibernate.Command[])
	 */
	@Override
	public void execute(DatabaseContext databaseContext, Command... commands)
			throws HibernateException
	{
		CommandRunnerContext commandContext = new CommandRunnerContext(commands);

		commandContext.handlePreExecute();

		boolean success = false;

		for (int attempt = 0; attempt < maxAttempts; attempt++)
		{
			try
			{
				commandContext.setAttemptNumber(attempt);

				databaseContext.beginSession(attempt > 0);

				commandContext.handlePreTransaction();

				databaseContext.beginTransaction();

				commandContext.setDataAccessContext(databaseContext
						.createDataAccessContext());

				commandContext.handleExecute();

				databaseContext.commitTransaction();

				commandContext.handlePostTransaction(true);

				success = true;

				break;
			}
			catch (RuntimeException e)
			{
				databaseContext.destroySession();

				if (!shouldRetry(e, attempt, commands))
				{
					commandContext.handlePostTransaction(false);
					commandContext.handlePostExecute(false);
					throw e;
				}

				commandContext.handlePostTransaction(false);
			}
		}

		commandContext.handlePostExecute(success);
	}

	/**
	 * Overrideable method that determines whether or not to retry following an
	 * exception. The default implementation retries if the maximum number of
	 * attempts has not been reached and the exception is retryable.
	 * <p>
	 * Whether or not an exception is retryable is determined two ways - this
	 * class's {@link #isRetryable(RuntimeException)} method is checked, plus
	 * each individual command that implements {@link RetryableExceptions} is
	 * checked.
	 * </p>
	 * 
	 * @param e
	 *            The exception that occurred.
	 * @param attempt
	 *            The current attempt number
	 * @return <code>true</code> if execution should be retried,
	 *         <code>false</code> if not.
	 * @see #isRetryable(RuntimeException)
	 */
	protected boolean shouldRetry(RuntimeException e, int attempt,
			Command[] commands)
	{
		if (attempt >= maxAttempts - 1)
		{
			return false;
		}

		if (isRetryable(e))
		{
			return true;
		}

		for (Command command : commands)
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
	 * Overrideable method that determines whether a particular exception
	 * represents a situation that can be retried, regardless of the nature of
	 * the <code>Command</code>s in question.
	 * <p>
	 * The default implementation retries on:
	 * </p>
	 * <ul>
	 * <li><code>LockAcquisitionException</code>s (typically indicating that
	 * there was a deadlock or timeout), and</li>
	 * <li>
	 * <code>JDBCConnectionException</code>s (typically indicating that there
	 * has been an issue talking to the database).</li>
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

	private static final Set<Class<?>> RETRYABLE = new HashSet<Class<?>>();
	static
	{
		RETRYABLE.add(LockAcquisitionException.class);
		RETRYABLE.add(JDBCConnectionException.class);
	}
}
