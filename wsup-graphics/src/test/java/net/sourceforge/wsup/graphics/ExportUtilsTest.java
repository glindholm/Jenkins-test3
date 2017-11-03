/*
 *  Copyright (c) 2011 Kevin Hunter
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

package net.sourceforge.wsup.graphics;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class ExportUtilsTest
{
    public ExportUtilsTest()
    {
    }
    
    @Test
    public void codeCoverage()
    {
        ExportUtils.codeCoverage();
    }
    
    @Test
    public void testStreamOutput() throws IOException
    {
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        ExportUtils.writeLE16(dest, 0x0100);
        ExportUtils.writeLE32(dest, 0x05040302);
        ExportUtils.writeBE16(dest, 0x0607);
        ExportUtils.writeBE32(dest, 0x08090A0B);
        
        byte[] result = dest.toByteArray();
        
        for (int i = 0; i < 12; i++)
        {
            assertEquals("pos " + i, (byte)i, result[i]);
        }
    }
    
    @Test
    public void testArrayOutput()
    {
        byte[] dest = new byte[12];
        
        ExportUtils.writeLE16(dest, 0, 0x0100);
        ExportUtils.writeLE32(dest, 2, 0x05040302);
        ExportUtils.writeBE16(dest, 6, 0x0607);
        ExportUtils.writeBE32(dest, 8, 0x08090A0B);
        
        for (int i = 0; i < 12; i++)
        {
            assertEquals("pos " + i, (byte)i, dest[i]);
        }
    }
}

