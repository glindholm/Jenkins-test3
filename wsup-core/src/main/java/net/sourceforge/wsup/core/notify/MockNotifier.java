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

import java.util.ArrayList;
import java.util.List;

/**
 * "Mock" {@link Notifier}.  This implementation saves all its
 * notifications to an internal list.  It is primarily intended
 * for use during unit testing.
 * 
 * @author Kevin Hunter
 *
 */
public class MockNotifier implements Notifier
{
    private List<Notification> notifications = new ArrayList<Notification>();
    
    /**
     * Constructor.
     */
    public MockNotifier()
    {
    }

    /**
     * @see Notifier#notify(java.lang.Object, java.lang.Object, java.lang.String, java.lang.String, java.lang.Throwable)
     */
    @Override
    public boolean notify(Object sender,
                          Object code,
                          String subject,
                          String message,
                          Throwable throwable)
    {
        notifications.add(new Notification(sender, code, subject, message, throwable));
        
        return true;
    }
    
    /**
     * Get the list of notifications that have been saved.
     * @return <code>List</code> of <code>Notification</code>s.
     */
    public List<Notification> getNotifications()
    {
        return notifications;
    }

    /**
     * Utility class that is used to save the results of each call
     * to {@link MockNotifier#notify(Object, Object, String, String, Throwable)}.
     * 
     * @author Kevin Hunter
     *
     */
    public static class Notification
    {
        private Object sender;
        private Object code;
        private String subject;
        private String message;
        private Throwable throwable;
        
        public Notification(Object sender,
                            Object code,
                            String subject,
                            String message,
                            Throwable throwable)
        {
            this.sender = sender;
            this.code = code;
            this.subject = subject;
            this.message = message;
            this.throwable = throwable;
        }

        public Object getSender()
        {
            return sender;
        }

        public Object getCode()
        {
            return code;
        }

        public String getSubject()
        {
            return subject;
        }

        public String getMessage()
        {
            return message;
        }

        public Throwable getThrowable()
        {
            return throwable;
        }
    }
}
