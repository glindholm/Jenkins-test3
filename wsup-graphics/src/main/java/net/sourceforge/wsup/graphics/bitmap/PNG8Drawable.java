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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import net.sourceforge.wsup.graphics.ARGB8Palette;

/**
 * This class implements a Drawable that produces a 256-color (8-bit-per-pixel)
 * Portable Network Graphics (PNG) file.
 * 
 * @author Kevin Hunter
 */

public class PNG8Drawable extends BasePalettedBitmapDrawable
{
    /*
     * Palette for the image.
     */
    private final ARGB8Palette    palette;
    /*
     * Object to handle PNG chunks
     */
    private final PNGChunkHandler chunkHandler;

    /**
     * Constructor.
     * 
     * @param width Width of the image in pixels (> 0)
     * @param height Height of the image in pixels (> 0)
     * @param palette Instance of ARGB8Palette that will form the color palette.
     * @param resolution Resolution in pixels per inch (>= 0)
     */
    public PNG8Drawable(int width, int height, ARGB8Palette palette, int resolution)
    {
        this(width, height, palette, resolution, new PNGChunkHandler());
    }

    /**
     * Constructor.
     * 
     * @param width Width of the image in pixels (> 0)
     * @param height Height of the image in pixels (> 0)
     * @param palette Instance of ARGB8Palette that will form the color palette.
     * @param resolution Resolution in pixels per inch (>= 0)
     */
    public PNG8Drawable(int width,
                        int height,
                        ARGB8Palette palette,
                        int resolution,
                        PNGChunkHandler chunkHandler)
    {
        super(width, height, palette.getNumEntries(), resolution);

        this.palette = palette;
        this.chunkHandler = chunkHandler;
    }

    @Override
    public void export(OutputStream stream) throws IOException
    {
        chunkHandler.writePngMagicNumber(stream);
        chunkHandler.writeIHDRChunk(stream, getWidth(), getHeight(), 8, 3, 0, 0, 0);
        if (getResolutionDPI() > 0)
        {
            chunkHandler.writePHYSChunk(stream, getResolutionDPI());
        }
        chunkHandler.writePLTEChunk(stream, palette);
        chunkHandler.writeTRNSChunk(stream, palette);
        writePixels(stream);
        chunkHandler.writeIENDChunk(stream);
    }

    /*
     * Writes out the body of the graphics file. This produces one or more
     * IDAT chunks. The data is compressed using the "Deflate" compression.
     */
    protected void writePixels(OutputStream stream) throws IOException
    {
        int height = getHeight();
        int width = getWidth();

        byte[] pixelBuffer = new byte[width];

        Deflater deflater = new Deflater();
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        DeflaterOutputStream deflateStream = new DeflaterOutputStream(byteBuffer, deflater);

        for (int row = 0; row < height; row++)
        {
            extractRowPixels(row, pixelBuffer);

            deflateStream.write(0); // filter
            deflateStream.write(pixelBuffer);
        }

        deflateStream.close();
        byte[] compressedBytes = byteBuffer.toByteArray();

        /*
         * compressedBytes now contains the image data. Now we have to write it out as a series of
         * one or more IDAT chunks.
         */
        
        int pos = 0;
        int bytesRemaining = compressedBytes.length;
        while (bytesRemaining > 0)
        {
            int bytesInThisChunk = bytesRemaining;
            int chunkSpaceAvailable = chunkHandler.getChunkSpaceRemaining();
            if (bytesInThisChunk > chunkSpaceAvailable)
            {
                bytesInThisChunk = chunkSpaceAvailable;
            }

            chunkHandler.writeIDATChunk(stream, compressedBytes, pos, bytesInThisChunk);

            pos += bytesInThisChunk;
            bytesRemaining -= bytesInThisChunk;
        }
    }
}
