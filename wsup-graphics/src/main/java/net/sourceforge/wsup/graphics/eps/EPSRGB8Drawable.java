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

import net.sourceforge.wsup.core.Assert;
import net.sourceforge.wsup.graphics.ARGB8Color;
import net.sourceforge.wsup.graphics.ARGB8Colored;

/**
 * This is a variant on <code>EPSDrawable</code> that implements the <code>ARGB8Colored</code>
 * interface, meaning that one can set colors using 0-255 values in addition to the "native" 0.0-1.0
 * values.
 * <p>
 * This version also accepts <code>alpha</code> values. It does this by manipulating the
 * <code>transparent</code> flag on <code>EPSDrawable</code>, will deem a color as transparent if
 * <code>alpha</code> has any value < 128.
 * </p>
 * 
 * @author Kevin Hunter
 * 
 */
public class EPSRGB8Drawable extends EPSDrawable implements ARGB8Colored
{
    public static final int OPAQUE_THRESHOLD = 128;

    /**
     * Constructor. This always builds an RGB EPS.
     * 
     * @param widthPts Width of the image, in points.
     * @param heightPts Height of the image, in points.
     */
    public EPSRGB8Drawable(double widthPts, double heightPts)
    {
        super(widthPts, heightPts, false);
    }

    @Override
    public void setColor(ARGB8Color color)
    {
        setColor(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
    }

    @Override
    public void setColor(int alpha, int red, int green, int blue)
    {
        setrgbolor(scaleColorComponent(red), scaleColorComponent(green), scaleColorComponent(blue));

        if (alpha < OPAQUE_THRESHOLD)
        {
            setTransparent(true);
        }
        else
        {
            setTransparent(false);
        }
    }

    @Override
    public void setColor(int red, int green, int blue)
    {
        setColor(ARGB8Color.MAX_VALUE, red, green, blue);
    }

    /*
     * Convert from the 0-255 space to the 0.0-1.0 space.
     */
    private double scaleColorComponent(int value)
    {
        Assert.isTrue(value >= ARGB8Color.MIN_VALUE, "Palette value must be >= MIN");
        Assert.isTrue(value <= ARGB8Color.MAX_VALUE, "Palette value must be <= MAX");

        return (double) value / (double) ARGB8Color.MAX_VALUE;
    }
}
