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
import net.sourceforge.wsup.core.notify.ThrottlingNotifier.NotificationKey;

import org.junit.Test;

public class ThrottlingNotifierTest
{
    public ThrottlingNotifierTest()
    {
    }
    
    @Test
    public void testPassIfSenderDifferent()
    {
        MockNotifier chain = new MockNotifier();
        ThrottlingNotifier notifier = new ThrottlingNotifier(chain);
        
        assertTrue(notifier.notify("a", "code", null, "message", null));
        assertTrue(notifier.notify("b", "code", null, "message", null));
        
        assertEquals(2, chain.getNotifications().size());
    }
    
    @Test
    public void testPassIfCodeDifferent()
    {
        MockNotifier chain = new MockNotifier();
        ThrottlingNotifier notifier = new ThrottlingNotifier(chain);
        
        assertTrue(notifier.notify("a", "code1", null, "message", null));
        assertTrue(notifier.notify("a", "code2", null, "message", null));
        
        assertEquals(2, chain.getNotifications().size());
    }
    
    @Test
    public void testEatIfSame()
    {
        MockNotifier chain = new MockNotifier();
        ThrottlingNotifier notifier = new ThrottlingNotifier(chain);
        
        assertTrue(notifier.notify("a", "code1", null, "message", null));
        assertTrue(notifier.notify("a", "code1", null, "message", null));
        
        assertEquals(1, chain.getNotifications().size());
    }
    
    @Test
    public void testPassIfDelayLongEnough() throws Exception
    {
        MockNotifier chain = new MockNotifier();
        ThrottlingNotifier notifier = new ThrottlingNotifier(chain, 1);
        
        assertTrue(notifier.notify("a", "code1", null, "message", null));
        Thread.sleep(500L);
        assertTrue(notifier.notify("a", "code1", null, "message", null));
        
        assertEquals(2, chain.getNotifications().size());
    }
    
    @Test
    public void testReturnsChainedResult() throws Exception
    {
        ThrottlingNotifier notifier = new ThrottlingNotifier(new FailingNotifier());
        assertFalse(notifier.notify("a", "code1", null, "message", null));
    }
    
    @Test
    public void testNotificationKeyEquals()
    {
        NotificationKey key11 = new NotificationKey("one", "one");
        NotificationKey key12 = new NotificationKey("one", "two");
        NotificationKey key21 = new NotificationKey("two", "one");
        NotificationKey key22 = new NotificationKey("two", "two");
        
        assertTrue(key11.equals(key11));
        assertFalse(key11.equals("key11"));
        assertFalse(key11.equals(key12));
        assertFalse(key11.equals(key21));
        assertFalse(key11.equals(key22));
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
