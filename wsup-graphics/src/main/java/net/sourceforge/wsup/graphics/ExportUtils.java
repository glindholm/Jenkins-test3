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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Utility methods used in the process of exporting data.
 * 
 * @author Kevin Hunter
 */
public class ExportUtils
{
    /**
     * Write a 16-bit, Little-Endian value to a stream.
     * @param stream Target <code>OutputStream</code>.
     * @param value Value to be written.
     * @throws IOException
     */
    public static void writeLE16(OutputStream stream, int value) throws IOException
    {
        stream.write(value & 0xFF);
        stream.write((value >> 8) & 0xFF);
    }
    
    /**
     * Write a 32-bit, Little-Endian value to a stream.
     * @param stream Target <code>OutputStream</code>.
     * @param value Value to be written.
     * @throws IOException
     */
    public static void writeLE32(OutputStream stream, int value) throws IOException
    {
        stream.write(value & 0xFF);
        stream.write((value >> 8) & 0xFF);
        stream.write((value >> 16) & 0xFF);
        stream.write((value >> 24) & 0xFF);
    }
    
    /**
     * Write a 16-bit, Big-Endian value to a stream.
     * @param stream Target <code>OutputStream</code>.
     * @param value Value to be written.
     * @throws IOException
     */
    public static void writeBE16(OutputStream stream, int value) throws IOException
    {
        stream.write((value >> 8) & 0xFF);
        stream.write(value & 0xFF);
    }
    
    /**
     * Write a 32-bit, Big-Endian value to a stream.
     * @param stream Target <code>OutputStream</code>.
     * @param value Value to be written.
     * @throws IOException
     */
    public static void writeBE32(OutputStream stream, int value) throws IOException
    {
        stream.write((value >> 24) & 0xFF);
        stream.write((value >> 16) & 0xFF);
        stream.write((value >> 8) & 0xFF);
        stream.write(value & 0xFF);
    }

    /**
     * Write a 16-bit, Little-Endian value into an array.
     * @param array Target <code>byte</code> array.
     * @param pos   Index of the first byte to be written.
     * @param value Value to be written.
     */
    public static void writeLE16(byte[] array, int pos, int value)
    {
        array[pos++] = (byte)(value & 0xFF);
        array[pos++] = (byte)((value >> 8) & 0xFF);
    }
    
    /**
     * Write a 32-bit, Little-Endian value into an array.
     * @param array Target <code>byte</code> array.
     * @param pos   Index of the first byte to be written.
     * @param value Value to be written.
     */
    public static void writeLE32(byte[] array, int pos, int value)
    {
        array[pos++] = (byte)(value & 0xFF);
        array[pos++] = (byte)((value >> 8) & 0xFF);
        array[pos++] = (byte)((value >> 16) & 0xFF);
        array[pos++] = (byte)((value >> 24) & 0xFF);
    }
    
    /**
     * Write a 16-bit, Big-Endian value into an array.
     * @param array Target <code>byte</code> array.
     * @param pos   Index of the first byte to be written.
     * @param value Value to be written.
     */
    public static void writeBE16(byte[] array, int pos, int value)
    {
        array[pos++] = (byte)((value >> 8) & 0xFF);
        array[pos++] = (byte)(value & 0xFF);
    }
    
    /**
     * Write a 32-bit, Big-Endian value into an array.
     * @param array Target <code>byte</code> array.
     * @param pos   Index of the first byte to be written.
     * @param value Value to be written.
     */
    public static void writeBE32(byte[] array, int pos, int value)
    {
        array[pos++] = (byte)((value >> 24) & 0xFF);
        array[pos++] = (byte)((value >> 16) & 0xFF);
        array[pos++] = (byte)((value >> 8) & 0xFF);
        array[pos++] = (byte)(value & 0xFF);
    }
    
    private ExportUtils()
    {
    }
    
    /*package*/ static ExportUtils codeCoverage()
    {
        return new ExportUtils();
    }
}

