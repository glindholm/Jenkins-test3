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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MultiplexAnyNotifierTest
{
    public MultiplexAnyNotifierTest()
    {
    }
    
    @Test
    public void testSuccess()
    {
        MockNotifier one = new MockNotifier();
        MockNotifier two = new MockNotifier();
        
        MultiplexAnyNotifier notifier = new MultiplexAnyNotifier(one, two);
        
        assertTrue(notifier.notify(this, this, null, "message", null));
        
        assertEquals(1, one.getNotifications().size());
        assertEquals(1, two.getNotifications().size());
    }
    
    @Test
    public void testFail1()
    {
        MockNotifier one = new MockNotifier();
        MockNotifier two = new FailingNotifier();
        
        MultiplexAnyNotifier notifier = new MultiplexAnyNotifier(one, two);
        
        assertTrue(notifier.notify(this, this, null, "message", null));
        
        assertEquals(1, one.getNotifications().size());
        assertEquals(1, two.getNotifications().size());
    }
    
    @Test
    public void testFail2()
    {
        MockNotifier one = new FailingNotifier();
        MockNotifier two = new MockNotifier();
        
        MultiplexAnyNotifier notifier = new MultiplexAnyNotifier(one, two);
        
        assertTrue(notifier.notify(this, this, null, "message", null));
        
        assertEquals(1, one.getNotifications().size());
        assertEquals(1, two.getNotifications().size());
    }
    
    @Test
    public void testFailBoth()
    {
        MockNotifier one = new FailingNotifier();
        MockNotifier two = new FailingNotifier();
        
        MultiplexAnyNotifier notifier = new MultiplexAnyNotifier(one, two);
        
        assertFalse(notifier.notify(this, this, null, "message", null));
        
        assertEquals(1, one.getNotifications().size());
        assertEquals(1, two.getNotifications().size());
    }

    public static class FailingNotifier extends MockNotifier
    {
        public FailingNotifier()
        {
        }

        @Override
        public boolean notify(Object sender,
                              Object code,
                              String subject,
                              String message,
                              Throwable throwable)
        {
            super.notify(sender, code, subject, message, throwable);

            return false;
        }
    }
}
