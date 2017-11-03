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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import net.sourceforge.wsup.graphics.ARGB8Color;
import net.sourceforge.wsup.graphics.ARGB8Palette;
import net.sourceforge.wsup.graphics.ExportUtils;

/**
 * This class implements a Drawable that produces a 256-color (8-bit-per-pixel)
 * Windows BMP file format.  This is a paletted format.  Although it uses the
 * <code>ARGB8</code> color system, the <code>alpha</code> components of the
 * individual palette entries are ignored, since this format does not support
 * transparency.
 * 
 * @author Kevin Hunter
 */
public class BMP8Drawable extends BasePalettedBitmapDrawable
{
    /*
     * Number of palette entries in the output file (regardless of the number
     * in the input palette).
     */
    private static final int NUM_PALETTE_ENTRIES = 256;
    
    private final ARGB8Palette palette;

    /**
     * Constructor.
     * 
     * @param width Width of the image in pixels (> 0)
     * @param height Height of the image in pixels (> 0)
     * @param palette Instance of ARGB8Palette that will form the color palette.
     *            Note that the Windows BMP format does not support transparency, so the
     *            "A" compoenent is ignored.  If the palette has fewer than 256 entries,
     *            the remaining entries will be filled with black.
     * @param resolution Resolution in pixels per inch (> 0)
     */
    public BMP8Drawable(int width, int height, ARGB8Palette palette, int resolution)
    {
        super(width, height, palette.getNumEntries(), resolution);

        this.palette = palette;
    }

    @Override
    public void export(OutputStream stream) throws IOException
    {
        BufferedImage image = getImage();
        int width = image.getWidth();
        int height = image.getHeight();
        int extraBytes = (4 - (width % 4)) % 4;
        int paddedWidth = width + extraBytes;
        int pixelOffset = 14 + 40 + 256 * 4;
        int imageSize = height * paddedWidth;
        int fileSize = pixelOffset + imageSize;
        int resolutionPPM = (getResolutionDPI() * 3937 + 50) / 100;
        int numPaletteEntries = getNumPaletteEntries();
        
        /*
         * Bitmap File Header (14 bytes)
         */

        stream.write(0x42);
        stream.write(0x4D);
        ExportUtils.writeLE32(stream, fileSize);
        ExportUtils.writeLE16(stream, 0);
        ExportUtils.writeLE16(stream, 0);
        ExportUtils.writeLE32(stream, pixelOffset);

        /*
         * Bitmap Info Header (40 bytes)
         */

        ExportUtils.writeLE32(stream, 40); // sizeof(BITMAPINFOHEADER)
        ExportUtils.writeLE32(stream, width);
        ExportUtils.writeLE32(stream, height);
        ExportUtils.writeLE16(stream, 1); // numPlanes
        ExportUtils.writeLE16(stream, 8); // bits per pixel
        ExportUtils.writeLE32(stream, 0); // compression method (0=none)
        ExportUtils.writeLE32(stream, imageSize);
        ExportUtils.writeLE32(stream, resolutionPPM); // horizontal resolution
        ExportUtils.writeLE32(stream, resolutionPPM); // vertical resolution
        ExportUtils.writeLE32(stream, 256); // number of palette entries
        ExportUtils.writeLE32(stream, numPaletteEntries); // "important" palette entries

        /*
         * RGBQuads - order is B, G, R, (reserved==0)
         */
        for (int i = 0; i < numPaletteEntries; i++)
        {
            ARGB8Color paletteEntry = palette.getEntry(i);
            stream.write(paletteEntry.getBlue());
            stream.write(paletteEntry.getGreen());
            stream.write(paletteEntry.getRed());
            stream.write(0);
        }

        /*
         * Extra entries, if the provided palette is < 256, set to all black
         */
        for (int i = numPaletteEntries; i < NUM_PALETTE_ENTRIES; i++)
        {
            ExportUtils.writeLE32(stream, 0);
        }

        byte[] pixelBuffer = new byte[width + extraBytes];
        Arrays.fill(pixelBuffer, (byte)0);

        /*
         * Rows are written bottom up.  Note that the pixelBuffer contains the
         * required extra bytes (if any), which are initialized to zero above, and won't
         * be overwritten by extractRowPixels, thus providing the correct number of
         * extra zeros at the end of each row, per the spec.
         */
        for (int row = height - 1; row >= 0; row--)
        {
            extractRowPixels(row, pixelBuffer);
            stream.write(pixelBuffer);
        }
    }
}
