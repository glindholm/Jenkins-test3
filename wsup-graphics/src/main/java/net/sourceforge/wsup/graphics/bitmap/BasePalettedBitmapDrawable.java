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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;

import net.sourceforge.wsup.core.Assert;
import net.sourceforge.wsup.graphics.PalettedDrawable;

/**
 * This subclass of <code>BaseBitmapDrawable</code> manages drawing for the various bitmap formats
 * that use a palette of up to 256 colors (as opposed to direct color formats).
 * <p>
 * It operates by creating a <code>BufferedImage</code> using an 8-bit <code>IndexColorModel</code>.
 * The color model is actually initialized with fake data - entry N in the model is set to the RGB
 * tuple (N,N,N). This way, this class doesn't have to worry about what kind of palette (RGB, ARGB,
 * CMYK, etc.) is being used. Colors are set by index number, and that index number ends up being
 * written into the bitmap. The actual fiddling with the real palette is handled by the
 * format-specific derived classes, since the palette formats and capabilities are format-dependent.
 * When outputting the actual bitmap data, derived classes simply need to handle the fact that the
 * value in the <code>BufferedImage</code> is the palette index for each pixel.
 * </p>
 * <p>
 * On creation, all the pixels in the image are set to the zero-th entry in the palette.
 * </p>
 * 
 * @author Kevin Hunter
 */
public abstract class BasePalettedBitmapDrawable extends BaseBitmapDrawable implements
    PalettedDrawable
{
    private final int   numPaletteEntries;
    private final int[] pixelBuffer;

    /**
     * Constructor.
     * 
     * @param width Width of the image in pixels (> 0)
     * @param height Height of the image in pixels (> 0)
     * @param numPaletteEntries Number of palette entries (> 0, <= 256)
     * @param resolution Resolution in pixels per inch (>= 0)
     */
    protected BasePalettedBitmapDrawable(int width,
                                         int height,
                                         int numPaletteEntries,
                                         int resolution)
    {
        super(createImage(width, height, numPaletteEntries), resolution);

        this.numPaletteEntries = numPaletteEntries;
        this.pixelBuffer = new int[width];

        setColor(0);
        fillRect(0, 0, width, height);
    }

    private static BufferedImage createImage(int width, int height, int numPaletteEntries)
    {
        Assert.isTrue(width > 0, "Width must be > 0");
        Assert.isTrue(height > 0, "Height must be > 0");
        Assert.isTrue(numPaletteEntries > 0, "numPaletteEntries must be > 0");
        Assert.isTrue(numPaletteEntries <= 256, "numPaletteEntries must be <= 256");

        byte[] entries = new byte[numPaletteEntries];
        for (int i = 0; i < numPaletteEntries; i++)
        {
            entries[i] = (byte) i;
        }

        IndexColorModel model = new IndexColorModel(8, numPaletteEntries, entries, entries, entries);
        return new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, model);
    }

    @Override
    public void setColor(int index)
    {
        Assert.isTrue(index >= 0, "index must be >= 0");
        Assert.isTrue(index < numPaletteEntries, "index must be < numPaletteEntries");

        getGraphics().setColor(new Color(index, index, index));
    }

    @Override
    public int getNumPaletteEntries()
    {
        return numPaletteEntries;
    }

    /**
     * Extracts the sequence of 8-bit values associated with a particular row of
     * data into the provided array
     * 
     * @param row Row to extract
     * @param dest Destination byte array (which must be at least <code>width</code> in length).
     */
    protected void extractRowPixels(int row, byte[] dest)
    {
        int width = getWidth();

        Assert.isTrue(dest.length >= width, 
                      "Destination must be at least width of image in length");

        /*
         * Get a Raster for this row of pixels
         */
        Raster src = getImage().getData(new Rectangle(0, row, width, 1));
        
        /*
         * Extract the pixels into an array of integers
         */
        src.getPixels(0, row, width, 1, pixelBuffer);
        
        /*
         * Convert the pixel array into a byte array by taking the lowest 8 bits
         * of each pixel.
         */
        for (int col = 0; col < width; col++)
        {
            dest[col] = (byte) pixelBuffer[col];
        }
    }
}
