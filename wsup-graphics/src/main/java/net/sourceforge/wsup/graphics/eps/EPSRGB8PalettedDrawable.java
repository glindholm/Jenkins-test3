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

package net.sourceforge.wsup.graphics.eps;

import net.sourceforge.wsup.graphics.ARGB8Palette;
import net.sourceforge.wsup.graphics.PalettedDrawable;

/**
 * This class provides a version of <code>EPSRGB8Drawable that supports a palette.
 * Although PostScript isn't natively a paletted file format, this class simulates
 * the palette behavior, turning "indexed" color sets into the appropriate raw values.
 * 
 * @author Kevin Hunter
 */
public class EPSRGB8PalettedDrawable extends EPSRGB8Drawable implements PalettedDrawable
{
    private final ARGB8Palette palette;

    /**
     * Constructor.
     * @param widthPts Width of the image, in points.
     * @param heightPts Height of the image, in points.
     * @param palette Palette to use for setting colors.
     */
    public EPSRGB8PalettedDrawable(double widthPts, double heightPts, ARGB8Palette palette)
    {
        super(widthPts, heightPts);

        this.palette = palette;
    }

    @Override
    public int getNumPaletteEntries()
    {
        return palette.getNumEntries();
    }

    @Override
    public void setColor(int index)
    {
        setColor(palette.getEntry(index));
    }
}
