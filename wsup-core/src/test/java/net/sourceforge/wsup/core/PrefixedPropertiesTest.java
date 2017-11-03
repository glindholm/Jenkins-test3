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

package net.sourceforge.wsup.core;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

public class PrefixedPropertiesTest
{
	public PrefixedPropertiesTest()
	{
	}
	
	private static final String SIMULATED_FILE1 = "value=0\nprimary.a.value=1\nprimary.b.value=2";
    private static final String SIMULATED_XML1  = 
    	"<?xml version='1.0' encoding='UTF-8'?>\n"
        + "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n"
        + "<properties>\n"
        + "<entry key='value'>0</entry>\n"
        + "<entry key='primary.a.value'>1</entry>\n"
        + "<entry key='primary.b.value'>2</entry>\n"
        + "</properties>";
	

    // Should load the primary.a. properties
	@Test
	public void testSystemA() throws IOException
	{
		MockPrefixedProperties pm;
		
		pm = new MockPrefixedProperties("a.");
		pm.load(createReader(SIMULATED_FILE1));
		assertEquals("1", pm.getProperty("value"));
		
		pm = new MockPrefixedProperties("a.");
		pm.load(createInputStream(SIMULATED_FILE1));
		assertEquals("1", pm.getProperty("value"));
		
		pm = new MockPrefixedProperties("a.");
		pm.loadFromXML(createInputStream(SIMULATED_XML1));
		assertEquals("1", pm.getProperty("value"));
	}
	
	// should load the primary.b. properties
	@Test
	public void testSystemB() throws IOException
	{
		MockPrefixedProperties pm;
		
		pm = new MockPrefixedProperties("b.");
		pm.load(createReader(SIMULATED_FILE1));
		assertEquals("2", pm.getProperty("value"));
		
		pm = new MockPrefixedProperties("b.");
		pm.load(createInputStream(SIMULATED_FILE1));
		assertEquals("2", pm.getProperty("value"));
		
		pm = new MockPrefixedProperties("b.");
		pm.loadFromXML(createInputStream(SIMULATED_XML1));
		assertEquals("2", pm.getProperty("value"));
	}
	
	// should load the unprefixed properties
	@Test
	public void testSystemC() throws IOException
	{
		MockPrefixedProperties pm;
		
		pm = new MockPrefixedProperties("c.");
		pm.load(createReader(SIMULATED_FILE1));
		assertEquals("0", pm.getProperty("value"));
		
		pm = new MockPrefixedProperties("c.");
		pm.load(createInputStream(SIMULATED_FILE1));
		assertEquals("0", pm.getProperty("value"));
		
		pm = new MockPrefixedProperties("c.");
		pm.loadFromXML(createInputStream(SIMULATED_XML1));
		assertEquals("0", pm.getProperty("value"));
	}
	
	private InputStream createInputStream(String simulatedFile)
	{
		return new ByteArrayInputStream(simulatedFile.getBytes());
	}
	
	private Reader createReader(String simulatedFile)
	{
		return new StringReader(simulatedFile);
	}
	
	private static class MockPrefixedProperties extends PrefixedProperties
	{
		private static final long serialVersionUID = 5912561408387073229L;

		private final String secondaryPrefix;
		
		public MockPrefixedProperties(String secondaryPrefix)
		{
			this.secondaryPrefix = secondaryPrefix;
		}

		@Override
		public String getPrimaryPrefix()
		{
			return "primary.";
		}

		@Override
		public String getSecondaryPrefix()
		{
			return secondaryPrefix;
		}
	}
}
