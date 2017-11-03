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

/**
 * Interface for classes that will notify someone of something
 * (usually an error condition) having happened.
 * 
 * @author Kevin Hunter
 * 
 */
public interface Notifier
{
    /**
     * Perform a notification.
     * 
     * @param sender <code>Object</code> that identifies the sender
     * @param code <code>Object</code> that identifies the particular condition within the sender's
     *            domain
     * @param subject Subject of the notification
     * @param message Body of the notification
     * @param throwable Related <code>Throwable</code>.
     * @return <code>true</code> if the notification was successful, <code>false</code> if the
     *         <code>Notifier</code> was unable to perform the notification.
     */
    public boolean notify(Object sender,
                          Object code,
                          String subject,
                          String message,
                          Throwable throwable);
}
