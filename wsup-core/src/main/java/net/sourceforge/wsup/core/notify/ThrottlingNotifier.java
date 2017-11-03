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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.wsup.core.BeanUtils;

/**
 * This is an implementation of {@link Notifier} that is intended
 * to help prevent "error flooding." Each call to
 * {@link #notify(Object, Object, String, String, Throwable)} is
 * checked, and if a previous call with the same sender and code
 * has occurred within a configurable period of time, the notification
 * is "eaten." Otherwise, it is passed through to a "chained"
 * notifier.
 * 
 * @author Kevin Hunter
 * 
 */
public class ThrottlingNotifier implements Notifier
{
    /**
     * Default period of time (60 seconds) for which a duplicate
     * notification will be suppressed.
     */
    public static final long           DEFAULT_HOLDOFF_PERIOD = 60000L;

    private final Notifier             chainedNotifier;
    private final long                 holdoffPeriod;
    private Map<NotificationKey, Date> lastSent               = new HashMap<NotificationKey, Date>();

    /**
     * Constructor.
     * 
     * @param chainedNotifier <code>Notifier</code> to which unsuppressed
     *            notifications should be passed.
     */
    public ThrottlingNotifier(Notifier chainedNotifier)
    {
        this(chainedNotifier, DEFAULT_HOLDOFF_PERIOD);
    }

    /**
     * Constructor.
     * 
     * @param chainedNotifier <code>Notifier</code> to which unsuppressed
     *            notifications should be passed.
     * @param holdoffPeriod Period of time (expressed in milliseconds)
     *            for which duplicate notifications should be suppressed.
     */
    public ThrottlingNotifier(Notifier chainedNotifier, long holdoffPeriod)
    {
        this.chainedNotifier = chainedNotifier;
        this.holdoffPeriod = holdoffPeriod;
    }

    /**
     * @see Notifier#notify(java.lang.Object, java.lang.Object, java.lang.String, java.lang.String,
     *      java.lang.Throwable)
     */
    @Override
    public boolean notify(Object sender,
                          Object code,
                          String subject,
                          String message,
                          Throwable throwable)
    {
        synchronized (this)
        {
            Date now = new Date();
            NotificationKey key = new NotificationKey(sender, code);

            Date lastSentDate = lastSent.get(key);
            if (lastSentDate != null)
            {
                if (now.getTime() <= lastSentDate.getTime() + holdoffPeriod)
                {
                    return true;
                }
            }

            lastSent.put(key, now);
        }

        return chainedNotifier.notify(sender, code, subject, message, throwable);
    }

    /**
     * Class used as the key into a map storing the last occurrence of
     * each particular type of notification.
     * 
     * @author Kevin Hunter
     */
    public static class NotificationKey
    {
        private Object sender;
        private Object code;

        /**
         * Constructor.
         * 
         * @param sender <code>sender</code> object from <code>notify</code> call
         * @param code <code>code</code> object from <code>notify</code> call
         */
        public NotificationKey(Object sender, Object code)
        {
            this.sender = sender;
            this.code = code;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            
            if (!(obj instanceof NotificationKey))
            {
                return false;
            }

            NotificationKey other = (NotificationKey) obj;
            if (!BeanUtils.safeEquals(sender, other.sender))
            {
                return false;
            }

            if (!BeanUtils.safeEquals(code, other.code))
            {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode()
        {
            return BeanUtils.safeHashCode(sender) ^ BeanUtils.safeHashCode(code);
        }
    }
}
