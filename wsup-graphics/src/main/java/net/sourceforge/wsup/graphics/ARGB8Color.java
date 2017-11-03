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

import net.sourceforge.wsup.core.Assert;

/**
 * This class implements a color with four eight-bit components - alpha,
 * red, green and blue. Instances of this class are immutable.
 * 
 * @author Kevin Hunter
 */
public final class ARGB8Color
{
    /**
     * Minimum legal value for one of the separations.
     * For <code>alpha</code>, this represents a fully-transparent
     * value.
     */
    public static final int MIN_VALUE = 0;

    /**
     * Maximum legal value for one of the separations.
     * For <code>alpha</code>, this represents a fully-opaque
     * value.
     */
    public static final int MAX_VALUE = 255;

    private final int       red;
    private final int       green;
    private final int       blue;
    private final int       alpha;

    /**
     * Constructs a fully opaque, black entry.
     */
    public ARGB8Color()
    {
        this(MAX_VALUE, MIN_VALUE, MIN_VALUE, MIN_VALUE);
    }

    /**
     * Constructs a fully opaque entry with the specified separation values.
     * 
     * @param red Red value (0-255)
     * @param green Green value (0-255)
     * @param blue Blue value (0-255)
     */
    public ARGB8Color(int red, int green, int blue)
    {
        this(MAX_VALUE, red, green, blue);
    }

    /**
     * Constructs an entry with the specified separation values and alpha.
     * 
     * @param alpha Alpha value (0-255)
     * @param red Red value (0-255)
     * @param green Green value (0-255)
     * @param blue Blue value (0-255)
     */
    public ARGB8Color(int alpha, int red, int green, int blue)
    {
        Assert.isTrue(red >= MIN_VALUE && red <= MAX_VALUE);
        Assert.isTrue(green >= MIN_VALUE && green <= MAX_VALUE);
        Assert.isTrue(blue >= MIN_VALUE && blue <= MAX_VALUE);
        Assert.isTrue(alpha >= MIN_VALUE && alpha <= MAX_VALUE);

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     * Get the red separation.
     * 
     * @return Red value (0-255)
     */
    public int getRed()
    {
        return red;
    }

    /**
     * Get the green separation.
     * 
     * @return Green value (0-255)
     */
    public int getGreen()
    {
        return green;
    }

    /**
     * Get the blue separation.
     * 
     * @return Blue value (0-255)
     */
    public int getBlue()
    {
        return blue;
    }

    /**
     * Get the alpha value.
     * 
     * @return Alpha value (0-255). Zero indicates fully transparent, 255 indicates fully opaque.
     */
    public int getAlpha()
    {
        return alpha;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof ARGB8Color))
        {
            return false;
        }

        ARGB8Color other = (ARGB8Color) obj;
        if (this.red != other.red)
        {
            return false;
        }

        if (this.green != other.green)
        {
            return false;
        }

        if (this.blue != other.blue)
        {
            return false;
        }

        if (this.alpha != other.alpha)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return red ^ green ^ blue ^ alpha;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("ARGB8Color:[");
        builder.append(Integer.toString(alpha));
        builder.append(',');
        builder.append(Integer.toString(red));
        builder.append(',');
        builder.append(Integer.toString(green));
        builder.append(',');
        builder.append(Integer.toString(blue));
        builder.append(']');

        return builder.toString();
    }
}
