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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

public class StreamUtilTest
{
	public StreamUtilTest()
	{
	}
	
	@Test
	public void testCoverage()
	{
		StreamUtil.coverage();
	}
    
    @Test
    public void testInputNull()
    {
        StreamUtil.safeCloseStream((InputStream)null);
    }
    
    @Test
    public void testInputNormal()
    {
        StreamUtil.safeCloseStream(new ByteArrayInputStream(new byte[] { 0, 1, 2}));
    }
    
    @Test
    public void testInputSwallowException()
    {
        StreamUtil.safeCloseStream(new InputCloseThrower());
    }
    
    @Test
    public void testOutputNull()
    {
        StreamUtil.safeCloseStream((OutputStream)null);
    }
    
    @Test
    public void testOutputNormal()
    {
        StreamUtil.safeCloseStream(new ByteArrayOutputStream());
    }
    
    @Test
    public void testOutputSwallowException()
    {
        StreamUtil.safeCloseStream(new OutputCloseThrower());
    }
    
    private static class InputCloseThrower extends InputStream
    {
        public InputCloseThrower()
        {
        }

        @Override
        public void close() throws IOException
        {
            throw new IOException("boom");
        }

        @Override
        public int read() throws IOException
        {
            return -1;
        }
    }
    
    private static class OutputCloseThrower extends OutputStream
    {
        public OutputCloseThrower()
        {
        }

        @Override
        public void close() throws IOException
        {
            throw new IOException("boom");
        }

        @Override
        public void write(int b) throws IOException
        {
        }
    }
}
