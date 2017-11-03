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

package net.sourceforge.wsup.email;

import org.apache.commons.mail.EmailException;

/**
 * Implementation of the {@link EmailSender} interface designed for
 * test purposes. It records all the data, and whether or not
 * the email was "sent," but doesn't actually do anything.  It is
 * also capable of throwing an exception during the send process.
 */
public class MockEmailSender extends EmailSenderBase implements EmailSender
{
    private EmailException exception;
    private boolean        sent;

    /**
     * Constructor.
     */
    public MockEmailSender()
    {
    }

    /**
     * Set an exception that should be thrown when {@link #send()} is called.
     * 
     * @param exception Instance of <code>EmailException</code>.
     */
    public void setEmailException(EmailException exception)
    {
        this.exception = exception;
    }

    /**
     * Indicate whether or not {@link #send()} was successfully called.
     * 
     * @return <code>true</code> if <code>send</code> was called and did
     *         not throw an exception.
     */
    public boolean getSent()
    {
        return sent;
    }

    @Override
    public void send() throws EmailException
    {
        if (exception != null)
        {
            throw exception;
        }

        sent = true;
    }
}
