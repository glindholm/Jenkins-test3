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

import net.sourceforge.wsup.hibernate.BaseDatabase;
import net.sourceforge.wsup.hibernate.Command;
import net.sourceforge.wsup.hibernate.CommandRunner;
import net.sourceforge.wsup.hibernate.ContextCommandRunner;
import net.sourceforge.wsup.hibernate.DatabaseContext;

import org.hibernate.HibernateException;

public class CommandRunnerImpl implements CommandRunner
{
	private final BaseDatabase database;

	private final ContextCommandRunner runner;

	public CommandRunnerImpl(BaseDatabase database, ContextCommandRunner runner)
	{
		this.database = database;
		this.runner = runner;
	}

	@Override
	public void execute(Command... commands) throws HibernateException
	{
		DatabaseContext databaseContext = database.createDatabaseContext();

		try
		{
		    runner.execute(databaseContext, commands);
		}
		finally
		{
		    databaseContext.closeSession();
		}
	}
}
