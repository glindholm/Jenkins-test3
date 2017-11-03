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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for the CommandRunnerContext class.
 * 
 * @author Kevin Hunter
 * 
 */
public class CommandRunnerContextTest
{
	public CommandRunnerContextTest()
	{
	}
	
	@Test
	public void testSetGet()
	{
		CommandRunnerContext testObject = new CommandRunnerContext(new MockSimpleCommand());
		
		assertEquals(1, testObject.getCommands().size());
		
		assertEquals(0, testObject.getAttemptNumber());
		testObject.setAttemptNumber(1);
		assertEquals(1, testObject.getAttemptNumber());
		
		assertNull(testObject.getDataAccessContext());
		DataAccessContextImpl context = new DataAccessContextImpl(null);
		testObject.setDataAccessContext(context);
		assertTrue(context == testObject.getDataAccessContext());
	}
	
	@Test
	public void testExecution()
	{
		MockSimpleCommand cmd1 = new MockSimpleCommand();
		MockMonitoringCommand cmd2 = new MockMonitoringCommand();
		
		CommandRunnerContext testObject = new CommandRunnerContext(cmd1, cmd2);
		
		assertTrue(cmd1 == testObject.getCommands().get(0));
		assertTrue(cmd2 == testObject.getCommands().get(1));
		
		assertEquals(0, cmd1.wasExecuted());
		assertEquals(0, cmd2.wasPreExecuted());
		assertEquals(0, cmd2.wasPreTransactioned());
		assertEquals(0, cmd2.wasExecuted());
		assertEquals(0, cmd2.wasPostTransactioned());
		assertEquals(0, cmd2.wasPostExecuted());
		
		testObject.handlePreExecute();
		
		assertEquals(0, cmd1.wasExecuted());
		assertEquals(1, cmd2.wasPreExecuted());
		assertEquals(0, cmd2.wasPreTransactioned());
		assertEquals(0, cmd2.wasExecuted());
		assertEquals(0, cmd2.wasPostTransactioned());
		assertEquals(0, cmd2.wasPostExecuted());
		
		testObject.handlePreTransaction();
		
		assertEquals(0, cmd1.wasExecuted());
		assertEquals(1, cmd2.wasPreExecuted());
		assertEquals(1, cmd2.wasPreTransactioned());
		assertEquals(0, cmd2.wasExecuted());
		assertEquals(0, cmd2.wasPostTransactioned());
		assertEquals(0, cmd2.wasPostExecuted());
		
		testObject.handleExecute();
		
		assertEquals(1, cmd1.wasExecuted());
		assertEquals(1, cmd2.wasPreExecuted());
		assertEquals(1, cmd2.wasPreTransactioned());
		assertEquals(1, cmd2.wasExecuted());
		assertEquals(0, cmd2.wasPostTransactioned());
		assertEquals(0, cmd2.wasPostExecuted());
		
		testObject.handlePostTransaction(true);
		
		assertEquals(1, cmd1.wasExecuted());
		assertEquals(1, cmd2.wasPreExecuted());
		assertEquals(1, cmd2.wasPreTransactioned());
		assertEquals(1, cmd2.wasExecuted());
		assertEquals(1, cmd2.wasPostTransactioned());
		assertEquals(0, cmd2.wasPostExecuted());
		assertTrue(cmd2.wasTransactionSuccessful());
		
		testObject.handlePostExecute(true);
		
		assertEquals(1, cmd1.wasExecuted());
		assertEquals(1, cmd2.wasPreExecuted());
		assertEquals(1, cmd2.wasPreTransactioned());
		assertEquals(1, cmd2.wasExecuted());
		assertEquals(1, cmd2.wasPostTransactioned());
		assertEquals(1, cmd2.wasPostExecuted());
		assertTrue(cmd2.wasExecuteSuccessful());
	}
}
