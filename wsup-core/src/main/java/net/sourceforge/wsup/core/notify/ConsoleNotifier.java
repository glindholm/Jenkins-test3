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

import java.io.PrintStream;

/**
 * {@link Notifier} that writes its output to the console.
 * Generally intended for use as a "Notifier of last resort."
 * 
 * @author Kevin Hunter
 * 
 */
public class ConsoleNotifier implements Notifier
{
    /**
     * Constructor.
     */
    public ConsoleNotifier()
    {
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
        PrintStream console = getConsole();

        if (message != null)
        {
            console.println(message);
        }

        if (throwable != null)
        {
            throwable.printStackTrace(console);
        }

        return true;
    }

    /**
     * Overloadrideable function for obtaining a <code>PrintStream</code> console object.
     * 
     * @return <code>PrintStream</code> for console.
     */
    protected PrintStream getConsole()
    {
        return System.err;
    }
}
