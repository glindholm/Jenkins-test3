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

package net.sourceforge.wsup.hibernate;

/**
 * Manages a singleton <code>TestDatabase</code> for unit test purposes.
 * 
 * @author Kevin Hunter
 *
 */
public class TestDatabaseManager
{
	private static TestDatabase	singleton;
	
	public synchronized static TestDatabase getSingleton()
	{
		return singleton;
	}
	
	public synchronized static TestDatabase bootTestDatabaseIfRequired() throws Exception
	{
		if (singleton == null)
		{
			bootNewTestDatabase();
		}
		
		return singleton;
	}
	
	public synchronized static TestDatabase openTestDatabaseIfRequired() throws Exception
	{
		if (singleton == null)
		{
			bootNewTestDatabase();
		}
		
		if (!singleton.isOpen())
		{
			singleton.open();
		}
		
		return singleton;
	}
	
	public synchronized static TestDatabase bootNewTestDatabase() throws Exception
	{
		return bootTestDatabase(new TestDatabase());
	}
	
	public synchronized static TestDatabase bootTestDatabase(TestDatabase database) throws Exception
	{
		shutdownTestDatabase();
		
		singleton = database;
		
		singleton.initializeTables();
		
		return singleton;
	}
	
	public synchronized static void shutdownTestDatabase() throws Exception
	{
		if (singleton != null)
		{
			if (singleton.isOpen())
			{
				singleton.close();
			}
			
			singleton.shutdown();
			singleton = null;
		}
	}
}
