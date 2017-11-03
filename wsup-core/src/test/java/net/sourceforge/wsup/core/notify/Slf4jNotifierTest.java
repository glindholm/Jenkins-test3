/*
 *  Copyright (c) 2010 Kevin Hunter
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

package net.sourceforge.wsup.core.notify;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.slf4j.impl.MockLogger;

public class Slf4jNotifierTest
{
    public Slf4jNotifierTest()
    {
    }
    
    @After
    public void tearDown()
    {
        MockLogger.resetAll();
    }
    
    @Test
    public void testMessage()
    {
        MockLogger.get(Slf4jNotifierTest.class.toString()).setPrinting(false);
        Slf4jNotifier notifier = new Slf4jNotifier();
        notifier.notify(Slf4jNotifierTest.class, "code", null, "message", null);
        assertEquals(1, MockLogger.getLogEntries().size());
    }
    
    @Test
    public void testException()
    {
        MockLogger.get(Slf4jNotifierTest.class.toString()).setPrinting(false);
        Slf4jNotifier notifier = new Slf4jNotifier();
        notifier.notify(Slf4jNotifierTest.class, "code", null, "message", new NullPointerException());
        assertEquals(1, MockLogger.getLogEntries().size());
    }
}

