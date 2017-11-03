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
 * {@link Notifier} object that simply passes on any notifications
 * to multiple other <code>Notifier</code>s.  Implements a "fan-out"
 * to multiple notification destinations.
 * <p>
 * This multiplexer returns "success" from <code>notify</code>
 * if any the chained <code>Notifier</code>s succeed.  See
 * {@link MultiplexAllNotifier} for an alternate implementation that
 * succeeds only if all of the chained <code>Notifier</code>s succeed.
 * </p>
 * 
 * @author Kevin Hunter
 * @see MultiplexAllNotifier
 *
 */
public class MultiplexAnyNotifier implements Notifier
{
    private final Notifier[] notifiers;
    
    public MultiplexAnyNotifier(Notifier... notifiers)
    {
        this.notifiers = notifiers;
    }
    
    @Override
    public boolean notify(Object sender,
                          Object code,
                          String subject,
                          String message,
                          Throwable throwable)
    {
        boolean success = false;
        
        for (Notifier notifier : notifiers)
        {
            if (notifier.notify(sender, code, subject, message, throwable))
            {
                success = true;
            }
        }
        
        return success;
    }

}

