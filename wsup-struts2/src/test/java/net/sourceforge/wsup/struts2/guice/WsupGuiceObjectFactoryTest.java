/*
 * Copyright (c) 2010 Greg Lindholm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sourceforge.wsup.struts2.guice;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Module;

public class WsupGuiceObjectFactoryTest
{

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testSetModuleGood() throws Exception
    {
        WsupGuiceObjectFactory factory = new WsupGuiceObjectFactory();
        factory.setModule("net.sourceforge.wsup.struts2.guice.WsupGuiceObjectFactoryTest$TestModule");
        assertNotNull(factory.getClassInstance("net.sourceforge.wsup.struts2.guice.WsupGuiceObjectFactoryTest$TestClass"));
    }
    
    @Test(expected = RuntimeException.class)
    public void testSetModuleBad() throws Exception
    {
        WsupGuiceObjectFactory factory = new WsupGuiceObjectFactory();
        factory.setModule("net.sourceforge.wsup.struts2.guice.WsupGuiceObjectFactoryTest$TestClass");
    }
    
    public static class TestModule implements Module
    {
        public TestModule()
        {
        }
        
        @Override
        public void configure(Binder binder)
        {
        }
    }
    
    public static class TestClass
    {
        public TestClass()
        {
        }
    }
}
