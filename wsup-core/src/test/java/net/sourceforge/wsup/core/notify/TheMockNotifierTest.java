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
import static org.junit.Assert.assertTrue;

import net.sourceforge.wsup.core.notify.MockNotifier.Notification;

import org.junit.Test;

public class TheMockNotifierTest
{
    public TheMockNotifierTest()
    {
    }
    
    @Test
    public void testMockNotifier()
    {
        MockNotifier notifier = new MockNotifier();
        
        assertEquals(0, notifier.getNotifications().size());
        
        assertTrue(notifier.notify(this, "code", "subject", "message", new NullPointerException()));

        assertEquals(1, notifier.getNotifications().size());
        
        Notification n = notifier.getNotifications().get(0);
        
        assertTrue(this == n.getSender());
        assertEquals("code", n.getCode());
        assertEquals("subject", n.getSubject());
        assertEquals("message", n.getMessage());
        assertTrue(n.getThrowable() instanceof NullPointerException);
    }
}

