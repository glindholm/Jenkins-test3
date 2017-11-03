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

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

public class ExceptionUtilTest
{
    public ExceptionUtilTest()
    {
    }

    @Test
    public void testCoverage()
    {
        ExceptionUtil.coverage();
    }

    @Test
    public void testPassThrough()
    {
        RuntimeException input = new RuntimeException();
        RuntimeException output = ExceptionUtil.convertToRuntimeException(input);
        assertTrue(input == output);
    }

    @Test
    public void testWrap()
    {
        FileNotFoundException input = new FileNotFoundException();
        assertTrue(ExceptionUtil.convertToRuntimeException(input) instanceof RuntimeException);
    }

    @Test
    public void testFirstCause()
    {
        IllegalArgumentException e1 = new IllegalArgumentException();
        assertEquals(e1, ExceptionUtil.firstCause(e1));

        RuntimeException e2 = new RuntimeException(e1);
        assertEquals(e1, ExceptionUtil.firstCause(e2));

        IllegalStateException e3 = new IllegalStateException(e2);
        assertEquals(e1, ExceptionUtil.firstCause(e3));

    }

    @Test
    public void testFirstCauseLoop()
    {
        IllegalArgumentException e1 = new IllegalArgumentException();
        assertEquals(e1, ExceptionUtil.firstCause(e1));

        RuntimeException e2 = new RuntimeException(e1);
        e1.initCause(e2);

        assertEquals(e1, ExceptionUtil.firstCause(e1));
        assertEquals(e2, ExceptionUtil.firstCause(e2));

    }

}
