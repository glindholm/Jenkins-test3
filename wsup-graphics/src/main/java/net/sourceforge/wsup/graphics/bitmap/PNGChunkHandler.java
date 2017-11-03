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

package net.sourceforge.wsup.graphics.bitmap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;

import net.sourceforge.wsup.core.Assert;
import net.sourceforge.wsup.graphics.ARGB8Color;
import net.sourceforge.wsup.graphics.ARGB8Palette;
import net.sourceforge.wsup.graphics.Constants;
import net.sourceforge.wsup.graphics.ExportUtils;

/**
 * This is a utility class used in generation of PNG files. It handles assembling data into a
 * "chunk" for inclusion in the file.
 * 
 * @author Kevin Hunter
 */
public class PNGChunkHandler
{
    /*
     * Number of palette entries.
     */
    private static final int NUM_PALETTE_ENTRIES    = 256;
    /*
     * Maximum size of a chunk
     */
    public static final int  DEFAULT_MAX_CHUNK_SIZE = 16384;
    /*
     * Length of the "type" field that starts a chunk
     */
    private static final int LEN_CHUNK_TYPE         = 4;
    /*
     * 4-byte array containing the current chunk type.
     */
    private byte[]           chunkType;
    /*
     * Maximum size of a chunk
     */
    private final int        maxChunkSize;
    /*
     * Buffer to store chunk data
     */
    private final byte[]     chunkData;
    /*
     * Index of the next available byte in the chunk buffer
     */
    private int              chunkDataLength        = 0;
    /*
     * Object used to calculate chunk CRC's
     */
    private CRC32            crc                    = new CRC32();

    /**
     * Primary constructor for this class.
     * 
     * @param maxChunkSize Maximum number of bytes in a PNG chunk
     */
    public PNGChunkHandler(int maxChunkSize)
    {
        this.maxChunkSize = maxChunkSize;
        this.chunkData = new byte[maxChunkSize];
    }

    /**
     * Convenience constructor for this class. Sets the maximum chunk size to
     * <code>DEFAULT_MAX_CHUNK_SIZE</code>.
     */
    public PNGChunkHandler()
    {
        this(DEFAULT_MAX_CHUNK_SIZE);
    }

    /**
     * Begin a chunk. Each chunk starts with a 4-byte "type" sequence, so that
     * is copied in and the chunk pointer set right after that.
     * 
     * @param chunkType Byte array containing the chunk type information
     */
    public void beginChunk(byte[] chunkType)
    {
        Assert.isNull(this.chunkType, "chunk already started");
        Assert.isNotNull(chunkType, "chunkType must not be null");
        Assert.isTrue(chunkType.length == LEN_CHUNK_TYPE, "Chunk type should be 4 bytes");

        this.chunkType = chunkType;
        chunkDataLength = 0;
    }

    /**
     * Begin a chunk using a ChunkType object to specify the type of the chunk.
     * 
     * @param chunkType <code>ChunkType</code> object.
     */
    public void beginChunk(ChunkType chunkType)
    {
        beginChunk(chunkType.getBytes());
    }

    /**
     * Write data from an array to a chunk.
     * 
     * @param bytes Source array for data to write
     * @param start Index into array of first byte to be written
     * @param length Number of bytes to be written
     */
    public void writeToChunk(byte[] bytes, int start, int length)
    {
        Assert.isNotNull(chunkType, "Chunk has not been started");
        Assert.isTrue(chunkDataLength + length <= maxChunkSize, "Not enough room in chunk");

        System.arraycopy(bytes, start, chunkData, chunkDataLength, length);
        chunkDataLength += length;
    }

    /**
     * Writes an 8-bit data item into the chunk buffer.
     * 
     * @param value Value to be written. Only the 8 least-significant bits are written.
     */
    public void writeByteToChunk(int value)
    {
        Assert.isNotNull(chunkType, "Chunk has not been started");
        Assert.isTrue(chunkDataLength + 1 <= maxChunkSize, "Not enough room in chunk");

        chunkData[chunkDataLength++] = (byte) value;
    }

    /**
     * Writes a 32-bit data item into the chunk buffer (MSB first, per the PNG standard).
     * 
     * @param value Value to be written.
     */
    public void writeIntToChunk(int value)
    {
        Assert.isNotNull(chunkType, "Chunk has not been started");
        Assert.isTrue(chunkDataLength + 4 <= maxChunkSize, "Not enough room in chunk");

        ExportUtils.writeBE32(chunkData, chunkDataLength, value);

        chunkDataLength += 4;
    }

    /**
     * Completes and flushes a chunk. This takes care of the CRC generation, and inserting the
     * length and the chunk data into the output stream.
     * A Chunk consists of:
     * <ul>
     * <li>4-byte length</li>
     * <li>4-byte chunk type</li>
     * <li>Chunk data</li>
     * <li>4-byte CRC</li>
     * </ul>
     * 
     * @param stream <code>OutputStream</code> to which to send data.
     * @throws IOException If the stream throws an IOException during the write operation.
     */
    public void endChunk(OutputStream stream) throws IOException
    {
        Assert.isNotNull(chunkType, "Chunk has not been started");

        /*
         * Calculate the CRC
         */
        crc.reset();
        crc.update(chunkType, 0, chunkType.length);
        if (chunkDataLength > 0)
        {
            crc.update(chunkData, 0, chunkDataLength);
        }

        ExportUtils.writeBE32(stream, chunkDataLength);
        stream.write(chunkType);
        if (chunkDataLength > 0)
        {
            stream.write(chunkData, 0, chunkDataLength);
        }
        ExportUtils.writeBE32(stream, (int) crc.getValue());

        chunkType = null;
        chunkDataLength = 0;
    }

    /**
     * Returns the amount of data space remaining in the current chunk.
     * 
     * @return Number of additional bytes that will fit in this chunk.
     */
    public int getChunkSpaceRemaining()
    {
        return maxChunkSize - chunkDataLength;
    }

    /*
     * "Magic number" that begins all PNG files.
     */
    private static final byte[] PNG_MAGIC_NUMBER = {
        (byte) 0x89,
        0x50,
        0x4E,
        0x47,
        0x0D,
        0x0A,
        0x1A,
        0x0A                                    };

    /**
     * Each PNG file begins with a "magic number" byte sequence - this routine
     * writes it.
     * 
     * @param stream <code>OutputStream</code> to which to write.
     */
    public void writePngMagicNumber(OutputStream stream) throws IOException
    {
        stream.write(PNG_MAGIC_NUMBER);
    }

    /**
     * Writes out the IHDR chunk that begins all PNG files.
     * 
     * @param stream <code>OutputStream</code> to which to write
     * @param bitDepth Pixel depth (i.e. bits per pixel)
     * @param colorType
     * @param compression
     * @param filter
     * @param interlace
     * @throws IOException
     */
    public void writeIHDRChunk(OutputStream stream,
                               int width,
                               int height,
                               int bitDepth,
                               int colorType,
                               int compression,
                               int filter,
                               int interlace) throws IOException
    {
        beginChunk(IHDR);
        writeIntToChunk(width);
        writeIntToChunk(height);
        writeByteToChunk(bitDepth);
        writeByteToChunk(colorType);
        writeByteToChunk(compression);
        writeByteToChunk(filter);
        writeByteToChunk(interlace);
        endChunk(stream);
    }

    /**
     * Writes out the PHYS chunk, which provides the resolution of the image
     * 
     * @param stream
     * @throws IOException
     */
    public void writePHYSChunk(OutputStream stream, int resolution) throws IOException
    {
        int pelsPerMeter = (resolution * Constants.DPI_TO_DPM_NUMERATOR + Constants.DPI_TO_DPM_DENOMINATOR / 2)
                           / Constants.DPI_TO_DPM_DENOMINATOR;

        beginChunk(PHYS);

        writeIntToChunk(pelsPerMeter); // x axis
        writeIntToChunk(pelsPerMeter); // y axis
        writeByteToChunk(1); // 1 = units of meters

        endChunk(stream);
    }

    /**
     * Writes out the IEND chunk that terminates the file
     * 
     * @param stream <code>OutputStream</code> to which to write
     */
    public void writeIENDChunk(OutputStream stream) throws IOException
    {
        beginChunk(IEND);
        endChunk(stream);
    }

    /**
     * Writes out an IDAT chunk containing pixel data.
     * 
     * @param stream <code>OutputStream</code> to which to write
     * @param src Array containing source data
     * @param start Index of first byte to write
     * @param length Number of bytes to write
     * @throws IOException
     */
    public void writeIDATChunk(OutputStream stream, byte[] src, int start, int length)
        throws IOException
    {
        beginChunk(IDAT);
        writeToChunk(src, start, length);
        endChunk(stream);
    }

    public void writePLTEChunk(OutputStream stream, ARGB8Palette palette) throws IOException
    {
        beginChunk(PNGChunkHandler.PLTE);

        int numEntries = palette.getNumEntries();
        if (numEntries > NUM_PALETTE_ENTRIES)
        {
            numEntries = NUM_PALETTE_ENTRIES;
        }

        for (int i = 0; i < numEntries; i++)
        {
            ARGB8Color entry = palette.getEntry(i);
            writeByteToChunk(entry.getRed());
            writeByteToChunk(entry.getGreen());
            writeByteToChunk(entry.getBlue());
        }

        for (int i = numEntries; i < NUM_PALETTE_ENTRIES; i++)
        {
            writeByteToChunk(ARGB8Color.MIN_VALUE);
            writeByteToChunk(ARGB8Color.MIN_VALUE);
            writeByteToChunk(ARGB8Color.MIN_VALUE);
        }

        endChunk(stream);
    }

    public void writeTRNSChunk(OutputStream stream, ARGB8Palette palette) throws IOException
    {
        beginChunk(PNGChunkHandler.TRNS);

        int numEntries = palette.getNumEntries();
        if (numEntries > NUM_PALETTE_ENTRIES)
        {
            numEntries = NUM_PALETTE_ENTRIES;
        }

        for (int i = 0; i < numEntries; i++)
        {
            writeByteToChunk(palette.getEntry(i).getAlpha());
        }

        for (int i = numEntries; i < NUM_PALETTE_ENTRIES; i++)
        {
            writeByteToChunk(ARGB8Color.MAX_VALUE);
        }

        endChunk(stream);
    }

    /**
     * Convenience class for definining PNG chunk types.
     * 
     * @author Kevin Hunter
     */
    public static class ChunkType
    {
        private final byte[] bytes;

        /**
         * Constructor.
         * 
         * @param bytes 4-byte array containing the chunk type information.
         */
        public ChunkType(byte[] bytes)
        {
            this.bytes = bytes;
            Assert.isTrue(bytes.length == LEN_CHUNK_TYPE, "Chunk type must be 4 bytes");

        }

        /**
         * Constructor.
         * 
         * @param chunkType 4-character string containing the chunk type. This will be converted
         *            to a byte array using <code>getBytes</code>.
         */
        public ChunkType(String chunkType)
        {
            this(chunkType.getBytes());
        }

        /**
         * Get the 4-byte array containing the type information.
         * 
         * @return 4-byte array.
         */
        public byte[] getBytes()
        {
            return bytes;
        }
    }

    /**
     * Type for IHDR chunk which begins all PNG files, and contains the basic format information.
     */
    public static ChunkType IHDR = new ChunkType("IHDR");
    /**
     * Type for PHYS chunk which provides the file resolution.
     */
    public static ChunkType PHYS = new ChunkType("pHYs");
    /**
     * Type for IDAT chunk which carries pixel data.
     */
    public static ChunkType IDAT = new ChunkType("IDAT");
    /**
     * Type for IEND chunk which ends all PNG files.
     */
    public static ChunkType IEND = new ChunkType("IEND");
    /**
     * Type for PLTE chunk which carries RGB palette information.
     */
    public static ChunkType PLTE = new ChunkType("PLTE");
    /**
     * Type for tRNS chunk which carries palette transparency information.
     */
    public static ChunkType TRNS = new ChunkType("tRNS");
}
