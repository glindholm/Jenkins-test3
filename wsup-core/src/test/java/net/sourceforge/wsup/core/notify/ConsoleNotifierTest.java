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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class ConsoleNotifierTest
{
    public ConsoleNotifierTest()
    {
    }
    
    @Test
    public void testMessage()
    {
        TestConsoleNotifier notifier = new TestConsoleNotifier();
        
        assertTrue(notifier.notify(this, "test", "subject", "message", null));
        assertEquals("message" + System.getProperty("line.separator"), notifier.getContents());
    }
    
    @Test
    public void testException()
    {
        TestConsoleNotifier notifier = new TestConsoleNotifier();
        
        try
        {
            throw new NullPointerException("foo");
        }
        catch(Throwable t)
        {
            assertTrue(notifier.notify(this, "test", "subject", null, t));
        }
        
        String results = notifier.getContents();
        String[] lines = results.split("\n");
        assertTrue(lines.length > 1);
        assertTrue(results.indexOf("NullPointerException") > 0);
    }
    
    private static class TestConsoleNotifier extends ConsoleNotifier
    {
        private ByteArrayOutputStream sw;
        private PrintStream ps;
        
        public TestConsoleNotifier()
        {
        }

        @Override
        protected PrintStream getConsole()
        {
            assertTrue(System.err == super.getConsole());
            
            if (ps == null)
            {
                sw = new ByteArrayOutputStream();
                ps = new PrintStream(sw);
            }
            
            return ps;
        }
        
        public String getContents()
        {
            ps.flush();
            return new String(sw.toByteArray());
        }
    }
}
