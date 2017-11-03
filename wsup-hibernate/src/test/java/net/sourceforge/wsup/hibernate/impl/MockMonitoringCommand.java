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

import net.sourceforge.wsup.hibernate.CommandContext;
import net.sourceforge.wsup.hibernate.PostExecute;
import net.sourceforge.wsup.hibernate.PostTransaction;
import net.sourceforge.wsup.hibernate.PreExecute;
import net.sourceforge.wsup.hibernate.PreTransaction;

public class MockMonitoringCommand extends MockSimpleCommand implements PreExecute,
		PostExecute, PreTransaction, PostTransaction
{
	private int preExecuted = 0;

	private int preTransactioned = 0;

	private int postTransactioned = 0;

	private int postExecuted = 0;

	private boolean transactionSuccess;
	
	private boolean executeSuccess;

	public MockMonitoringCommand()
	{
	}

	@Override
	public void preExecute(CommandContext context)
	{
		preExecuted++;
	}

	@Override
	public void postExecute(CommandContext context, boolean successful)
	{
		executeSuccess = successful;
		postExecuted++;
	}

	@Override
	public void preTransaction(CommandContext context)
	{
		preTransactioned++;
	}

	@Override
	public void postTransaction(CommandContext context, boolean successful)
	{
		transactionSuccess = successful;
		postTransactioned++;
	}

	public int wasPreExecuted()
	{
		return preExecuted;
	}

	public int wasPreTransactioned()
	{
		return preTransactioned;
	}

	public int wasPostTransactioned()
	{
		return postTransactioned;
	}

	public int wasPostExecuted()
	{
		return postExecuted;
	}

	public boolean wasTransactionSuccessful()
	{
		return transactionSuccess;
	}

	public boolean wasExecuteSuccessful()
	{
		return executeSuccess;
	}
}
