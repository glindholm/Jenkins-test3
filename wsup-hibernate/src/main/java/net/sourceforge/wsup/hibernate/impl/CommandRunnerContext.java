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

package net.sourceforge.wsup.hibernate.impl;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.wsup.hibernate.Command;
import net.sourceforge.wsup.hibernate.CommandContext;
import net.sourceforge.wsup.hibernate.CommandExecutionContext;
import net.sourceforge.wsup.hibernate.DataAccessContext;
import net.sourceforge.wsup.hibernate.PostExecute;
import net.sourceforge.wsup.hibernate.PostTransaction;
import net.sourceforge.wsup.hibernate.PreExecute;
import net.sourceforge.wsup.hibernate.PreTransaction;

/**
 * Implementation of the <code>CommandContext</code> and
 * <code>CommandExecutionContext</code> interfaces for use by the
 * ContextCommandRunnerImpl class.
 * 
 * @author Kevin Hunter
 * @see CommandContext
 * @see CommandExecutionContext
 * @see ContextCommandRunnerImpl
 * 
 */
public class CommandRunnerContext implements CommandContext,
		CommandExecutionContext
{
	private DataAccessContext dataAccessContext;

	private int attemptNumber;

	private final List<Command> commands;

	/**
	 * Constructor.
	 * 
	 * @param commands
	 *            <code>Command</code> instances to be run.
	 */
	public CommandRunnerContext(Command... commands)
	{
		this.commands = new ArrayList<Command>();
		for (Command command : commands)
		{
			this.commands.add(command);
		}
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.CommandExecutionContext#getDataAccessContext()
	 */
	@Override
	public DataAccessContext getDataAccessContext()
	{
		return dataAccessContext;
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.CommandContext#getAttemptNumber()
	 */
	@Override
	public int getAttemptNumber()
	{
		return attemptNumber;
	}

	/**
	 * @see net.sourceforge.wsup.hibernate.CommandContext#getCommands()
	 */
	@Override
	public List<Command> getCommands()
	{
		return commands;
	}

	/**
	 * Set the <code>DataAccessContext</code>.
	 * 
	 * @param dataAccessContext
	 *            <code>DataAccessContext</code>.
	 * @see #getDataAccessContext()
	 */
	public void setDataAccessContext(DataAccessContext dataAccessContext)
	{
		this.dataAccessContext = dataAccessContext;
	}

	/**
	 * Set the retry count.
	 * 
	 * @param retryCount
	 *            return count
	 * @see #getAttemptNumber()
	 */
	public void setAttemptNumber(int retryCount)
	{
		this.attemptNumber = retryCount;
	}

	/**
	 * Perform the <code>PreExecute</code> phase on any <code>Command</code>s
	 * that support it.
	 * 
	 * @see PreExecute
	 */
	public void handlePreExecute()
	{
		for (Command command : commands)
		{
			if (command instanceof PreExecute)
			{
				((PreExecute) command).preExecute(this);
			}
		}
	}

	/**
	 * Perform the <code>PreTransaction</code> phase on any <code>Command</code>
	 * s that support it.
	 * 
	 * @see PreTransaction
	 */
	public void handlePreTransaction()
	{
		for (Command command : commands)
		{
			if (command instanceof PreTransaction)
			{
				((PreTransaction) command).preTransaction(this);
			}
		}
	}

	/**
	 * Execute each of the commands.
	 * 
	 * @see Command
	 */
	public void handleExecute()
	{
		for (Command command : commands)
		{
			command.execute(this);
		}
	}

	/**
	 * Perform the <code>PostTransaction</code> phase on any
	 * <code>Command</code>s that support it.
	 * 
	 * @param successful
	 *            whether or not the transaction was successful
	 * @see PostTransaction
	 */
	public void handlePostTransaction(boolean successful)
	{
		for (Command command : commands)
		{
			if (command instanceof PostTransaction)
			{
				((PostTransaction) command).postTransaction(this, successful);
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
	public void handlePostExecute(boolean successful)
	{
		for (Command command : commands)
		{
			if (command instanceof PostExecute)
			{
				((PostExecute) command).postExecute(this, successful);
			}
		}
	}
}
