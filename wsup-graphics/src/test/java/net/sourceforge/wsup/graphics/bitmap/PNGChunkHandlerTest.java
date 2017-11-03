/*
 *  Copyright (c) 2013 Kevin Hunter
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

package net.sourceforge.wsup.graphics.bitmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;

import net.sourceforge.wsup.graphics.ARGB8Palette;

import org.junit.Before;
import org.junit.Test;

public class PNGChunkHandlerTest
{
    private PNGChunkHandler       objUnderTest;
    private ByteArrayOutputStream stream;

    public PNGChunkHandlerTest()
    {
    }

    @Before
    public void setup()
    {
        objUnderTest = new PNGChunkHandler();
        stream = new ByteArrayOutputStream();
    }

    private int extract4(byte[] buffer, int index)
    {
        int result = buffer[index++] & 0xFF;
        result <<= 8;
        result |= (buffer[index++] & 0xFF);
        result <<= 8;
        result |= (buffer[index++] & 0xFF);
        result <<= 8;
        result |= (buffer[index++] & 0xFF);

        return result;
    }

    @Test
    public void testHeader() throws Exception
    {
        objUnderTest.writePngMagicNumber(stream);
        byte[] result = stream.toByteArray();

        assertEquals(8, result.length);
        assertEquals(0x89, result[0] & 0xFF);
        assertEquals(0x50, result[1] & 0xFF);
        assertEquals(0x4E, result[2] & 0xFF);
        assertEquals(0x47, result[3] & 0xFF);
        assertEquals(0x0D, result[4] & 0xFF);
        assertEquals(0x0A, result[5] & 0xFF);
        assertEquals(0x1A, result[6] & 0xFF);
        assertEquals(0x0A, result[7] & 0xFF);
    }

    @Test
    public void testEndChunkIncludingCRC() throws Exception
    {
        objUnderTest.writeIENDChunk(stream);
        byte[] result = stream.toByteArray();

        assertEquals(12, result.length);
        assertEquals(0, result[0] & 0xFF);
        assertEquals(0, result[1] & 0xFF);
        assertEquals(0, result[2] & 0xFF);
        assertEquals(0, result[3] & 0xFF);
        assertEquals('I', result[4] & 0xFF);
        assertEquals('E', result[5] & 0xFF);
        assertEquals('N', result[6] & 0xFF);
        assertEquals('D', result[7] & 0xFF);
        assertEquals(0xAE, result[8] & 0xFF);
        assertEquals(0x42, result[9] & 0xFF);
        assertEquals(0x60, result[10] & 0xFF);
        assertEquals(0x82, result[11] & 0xFF);
    }

    @Test
    public void testHeaderChunk() throws Exception
    {
        objUnderTest.writeIHDRChunk(stream, 1, 2, 3, 4, 5, 6, 7);
        byte[] result = stream.toByteArray();

        assertEquals(25, result.length);

        assertEquals(13, extract4(result, 0)); // length
        assertEquals('I', result[4] & 0xFF);
        assertEquals('H', result[5] & 0xFF);
        assertEquals('D', result[6] & 0xFF);
        assertEquals('R', result[7] & 0xFF);

        assertEquals(1, extract4(result, 8)); // width
        assertEquals(2, extract4(result, 12)); // height
        assertEquals(3, result[16]);
        assertEquals(4, result[17]);
        assertEquals(5, result[18]);
        assertEquals(6, result[19]);
        assertEquals(7, result[20]);
    }

    @Test
    public void testPhysChunk() throws Exception
    {
        int pelsPerInch = 300;
        int pelsPerMeter = (int) Math.round(pelsPerInch * 39.37);

        objUnderTest.writePHYSChunk(stream, pelsPerInch);
        byte[] result = stream.toByteArray();

        assertEquals(21, result.length);

        assertEquals(9, extract4(result, 0)); // length
        assertEquals('p', result[4] & 0xFF);
        assertEquals('H', result[5] & 0xFF);
        assertEquals('Y', result[6] & 0xFF);
        assertEquals('s', result[7] & 0xFF);

        assertEquals(pelsPerMeter, extract4(result, 8)); // x axis
        assertEquals(pelsPerMeter, extract4(result, 12)); // y axis
        assertEquals(1, result[16]); // units
    }

    @Test
    public void testDataChunk() throws Exception
    {
        byte[] data = { 1, 2, 3, 4, 5, 6, 7, 8 };

        objUnderTest.writeIDATChunk(stream, data, 1, 6);
        byte[] result = stream.toByteArray();

        assertEquals(18, result.length);

        assertEquals(6, extract4(result, 0)); // length
        assertEquals('I', result[4] & 0xFF);
        assertEquals('D', result[5] & 0xFF);
        assertEquals('A', result[6] & 0xFF);
        assertEquals('T', result[7] & 0xFF);

        assertEquals(2, result[8]);
        assertEquals(3, result[9]);
        assertEquals(4, result[10]);
        assertEquals(5, result[11]);
        assertEquals(6, result[12]);
        assertEquals(7, result[13]);
    }

    @Test
    public void testPaletteChunkWritesRGBEntries() throws Exception
    {
        ARGB8Palette palette = new ARGB8Palette(16);
        for (int i = 0; i < 16; i++)
        {
            palette.setEntry(i, i, i + 1, i + 2);
        }

        objUnderTest.writePLTEChunk(stream, palette);
        byte[] result = stream.toByteArray();

        assertEquals(12 + 256 * 3, result.length);

        assertEquals(256 * 3, extract4(result, 0)); // length
        assertEquals('P', result[4] & 0xFF);
        assertEquals('L', result[5] & 0xFF);
        assertEquals('T', result[6] & 0xFF);
        assertEquals('E', result[7] & 0xFF);

        for (int i = 0; i < 16; i++)
        {
            int offset = 8 + i * 3;

            assertEquals(i, result[offset]);
            assertEquals(i + 1, result[offset + 1]);
            assertEquals(i + 2, result[offset + 2]);
        }

        for (int i = 16; i < 256; i++)
        {
            int offset = 8 + i * 3;

            assertEquals(0, result[offset]);
            assertEquals(0, result[offset + 1]);
            assertEquals(0, result[offset + 2]);
        }
    }

    @Test
    public void testPaletteChunkCutsOffLongPalettes() throws Exception
    {
        ARGB8Palette palette = new ARGB8Palette(512);

        objUnderTest.writePLTEChunk(stream, palette);
        byte[] result = stream.toByteArray();

        assertEquals(12 + 256 * 3, result.length);
    }

    @Test
    public void testTransparentChunkWritesRGBEntries() throws Exception
    {
        ARGB8Palette palette = new ARGB8Palette(16);
        for (int i = 0; i < 16; i++)
        {
            palette.setEntry(i, i, 0, 0, 0);
        }

        objUnderTest.writeTRNSChunk(stream, palette);
        byte[] result = stream.toByteArray();

        assertEquals(12 + 256, result.length);

        assertEquals(256, extract4(result, 0)); // length
        assertEquals('t', result[4] & 0xFF);
        assertEquals('R', result[5] & 0xFF);
        assertEquals('N', result[6] & 0xFF);
        assertEquals('S', result[7] & 0xFF);

        for (int i = 0; i < 16; i++)
        {
            int offset = 8 + i;

            assertEquals("i="+i, i, result[offset]);
        }

        for (int i = 16; i < 256; i++)
        {
            int offset = 8 + i;

            assertEquals(255, result[offset] & 0xFF);
        }
    }

    @Test
    public void testTransparentChunkCutsOffLongPalettes() throws Exception
    {
        ARGB8Palette palette = new ARGB8Palette(512);

        objUnderTest.writeTRNSChunk(stream, palette);
        byte[] result = stream.toByteArray();

        assertEquals(12 + 256, result.length);
    }
    
    @Test
    public void assertsIfBeginChunkCalledWithWrongLength()
    {
        boolean asserted = false;
        
        try
        {
            objUnderTest.beginChunk(new byte[1]);
        }
        catch(AssertionError e)
        {
            asserted = true;
        }
        
        assertTrue(asserted);
    }
    
    @Test
    public void nestedChunkTypeClassAssertsIfBeginChunkCalledWithWrongLength()
    {
        boolean asserted = false;
        
        try
        {
            new PNGChunkHandler.ChunkType(new byte[1]);
        }
        catch(AssertionError e)
        {
            asserted = true;
        }
        
        assertTrue(asserted);
    }
    
    @Test
    public void assertsIfWriteOffEndOfChunk1()
    {
        objUnderTest = new PNGChunkHandler(1);  // less than 16 bytes
        objUnderTest.beginChunk(PNGChunkHandler.IDAT);
        
        boolean asserted = false;
        
        try
        {
            objUnderTest.writeToChunk(new byte[16], 0, 16);
        }
        catch(AssertionError e)
        {
            asserted = true;
        }
        
        assertTrue(asserted);
    }
    
    @Test
    public void assertsIfWriteOffEndOfChunk2()
    {
        objUnderTest = new PNGChunkHandler(1);  // one byte capacity
        objUnderTest.beginChunk(PNGChunkHandler.IDAT);
        objUnderTest.writeByteToChunk(0);
        
        boolean asserted = false;
        
        try
        {
            objUnderTest.writeByteToChunk(0);   // second byte
        }
        catch(AssertionError e)
        {
            asserted = true;
        }
        
        assertTrue(asserted);
    }
    
    @Test
    public void assertsIfWriteOffEndOfChunk3()
    {
        objUnderTest = new PNGChunkHandler(1);  // one byte capacity
        objUnderTest.beginChunk(PNGChunkHandler.IDAT);
        
        boolean asserted = false;
        
        try
        {
            objUnderTest.writeIntToChunk(0);    // four bytes
        }
        catch(AssertionError e)
        {
            asserted = true;
        }
        
        assertTrue(asserted);
    }
}
