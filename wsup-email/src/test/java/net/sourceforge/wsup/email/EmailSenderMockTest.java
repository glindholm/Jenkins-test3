/*
 *  Copyright (c) 2011 Kevin Hunter
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

package net.sourceforge.wsup.email;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.mail.EmailException;
import org.junit.Test;

public class EmailSenderMockTest
{
    public EmailSenderMockTest()
    {
    }
    
    @Test
    public void testSent() throws Exception
    {
        MockEmailSender sender = new MockEmailSender();
        assertFalse(sender.getSent());
        sender.send();
        assertTrue(sender.getSent());
    }
    
    @Test(expected=EmailException.class)
    public void testException() throws Exception
    {
        MockEmailSender sender = new MockEmailSender();
        sender.setEmailException(new EmailException());
        sender.send();
    }
}

