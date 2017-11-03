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

/**
 * Utilities for dealing with exceptions.
 *
 * @author Kevin Hunter
 * @author Greg Lindholm
 *
 */
public class ExceptionUtil
{
    /**
     * Convert the specified exception to a RuntimeException (assuming that it
     * is not one already).
     *
     * @param e <code>Exception</code> to be converted.
     * @return The original exception (if it was a <code>RuntimeException</code>,
     *         or the original exception wrapped in a <code>RuntimeException</code>.
     */
    public static RuntimeException convertToRuntimeException(Exception e)
    {
        if (e instanceof RuntimeException)
        {
            return (RuntimeException) e;
        }

        return new RuntimeException(e);
    }

    /**
     * Follow the <code>getCause()</code> chain to find and return the first cause on the chain.<br>
     * <br>
     * This deals with self referencing loops on the chain. If the cause chain depth exceeds 100
     * then it assumes there is a loop and just returns the original value.
     *
     * @param original the exception that was caught.
     * @return the first exception on the cause chain.
     */
    public static Throwable firstCause(Throwable original)
    {
        final int maxDepth = 100;
        Throwable current = original;

        for (int depth = 0; depth < maxDepth; depth++)
        {
            Throwable next = current.getCause();
            if (next == null)
            {
                return current;
            }
            if (next == current)
            {
                return current;
            }
            current = next;
        }

        /*
         * Maximum chain depth has been exceeded so return the original
         */
        return original;
    }

    /* package */static void coverage()
    {
        new ExceptionUtil();
    }

    private ExceptionUtil()
    {
    }
}
