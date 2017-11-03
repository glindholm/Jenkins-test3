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

package net.sourceforge.wsup.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MD5Test
{
    public MD5Test()
    {
    }

    private static final byte[] OUTPUT012 = {
        (byte) 0xb9,
        0x5f,
        0x67,
        (byte) 0xf6,
        0x1e,
        (byte) 0xbb,
        0x3,
        0x61,
        (byte) 0x96,
        0x22,
        (byte) 0xd7,
        (byte) 0x98,
        (byte) 0xf4,
        0x5f,
        (byte) 0xc2,
        (byte) 0xd3,                     };

    @Test
    public void testHashByteArray()
    {
        byte[] output = MD5.hash(new byte[] { 0, 1, 2 });
        assertEquals(16, output.length);
        for (int i = 0; i < output.length; i++)
        {
            assertEquals("OUTPUT012[" + i + "]", OUTPUT012[i], output[i]);
        }
    }

    @Test
    public void testHashString()
    {
        assertEquals("ce114e4501d2f4e2dcea3e17b546f339", MD5.hash("This is a test"));
    }
    
    @Test(expected = RuntimeException.class)
    public void coverException()
    {
        MD5.hash((byte[])null);
    }
    
    @Test
    public void coverConstructor()
    {
        MD5.coverConstructor();
    }
}
