/*
 * Copyright (c) 2010 Kevin Hunter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sourceforge.wsup.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utilities for dealing with streams.
 * 
 * @author Kevin Hunter
 * 
 */
public class StreamUtil
{
    /**
     * Close an <code>InputStream</code>, eating any exception that it might throw.
     * 
     * @param stream <code>InputStream</code> to be closed.
     */
    public static void safeCloseStream(InputStream stream)
    {
        try
        {
            if (stream != null)
            {
                stream.close();
            }
        }
        catch (IOException e)
        {
            // ignore
        }
    }
    
    /**
     * Close an <code>OutputStream</code>, eating any exception that it might throw.
     * 
     * @param stream <code>OutputStream</code> to be closed.
     */
    public static void safeCloseStream(OutputStream stream)
    {
        try
        {
            if (stream != null)
            {
                stream.close();
            }
        }
        catch (IOException e)
        {
            // ignore
        }
    }

    /* package */static void coverage()
    {
        new StreamUtil();
    }

    private StreamUtil()
    {
    }
}
