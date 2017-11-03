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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link Notifier} that simply logs the
 * notification (at <code>error</code> level) via SLF4J.
 * 
 * @author Kevin Hunter
 *
 */
public class Slf4jNotifier implements Notifier
{
    public Slf4jNotifier()
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
        Logger logger = LoggerFactory.getLogger(sender.toString());
        
        if (throwable == null)
        {
            logger.error(message);
        }
        else
        {
            logger.error(message, throwable);
        }
        
        return true;
    }
}
