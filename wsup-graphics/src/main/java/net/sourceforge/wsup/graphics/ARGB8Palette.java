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
 * This class implements a fixed-length palette of color entries,
 * each of which is an 8-bit red/green/blue/alpha tuple.
 * 
 * @author Kevin Hunter
 */
public class ARGB8Palette
{
    private ARGB8Color[] entries;

    /**
     * Creates a palette with the specified number of entries.
     * Each of the entries is initially initialized to a full-alpha
     * black color.
     * 
     * @param numEntries Number of entries in the palette.
     */
    public ARGB8Palette(int numEntries)
    {
        this.entries = new ARGB8Color[numEntries];
        for (int i = 0; i < numEntries; i++)
        {
            setEntry(i, ARGB8Color.MIN_VALUE, ARGB8Color.MIN_VALUE, ARGB8Color.MIN_VALUE);
        }
    }

    /**
     * Creates a palette based on the specified array of entries.
     * This constructor does a deep copy on the array of entries.
     * 
     * @param entries Array of <code>Entry</code> objects to make
     *            up the palette.
     */
    public ARGB8Palette(ARGB8Color[] entries)
    {
        Assert.isNotNull(entries, "Array cannot be null");

        this.entries = new ARGB8Color[entries.length];
        for (int i = 0; i < entries.length; i++)
        {
            Assert.isNotNull(entries[i], "Array cannot contain null entries");
            this.entries[i] = entries[i];
        }
    }

    /**
     * Creates a palette that is a copy of the specified palette.
     * 
     * @param other Palette to copy.
     */
    public ARGB8Palette(ARGB8Palette other)
    {
        this(other.entries);
    }

    /**
     * Returns the <code>Entry</code> at the specified index.
     * 
     * @param index Index to retrieve.
     * @return <code>Entry</code> object at that index.
     */
    public ARGB8Color getEntry(int index)
    {
        return entries[index];
    }

    /**
     * Set an entry in the palette to the specified color.
     * 
     * @param index Index in the palette to set.
     * @param color <code>ARGB8Color</code> to which the entry should be set.
     */
    public void setEntry(int index, ARGB8Color color)
    {
        Assert.isNotNull(color, "Entry cannot be null");

        entries[index] = color;
    }

    /**
     * Set an entry in the palette to the specified (opaque) color.
     * 
     * @param index Index in the palette to set.
     * @param red Red separation
     * @param green Green separation
     * @param blue Blue separation
     */
    public void setEntry(int index, int red, int green, int blue)
    {
        entries[index] = new ARGB8Color(red, green, blue);
    }

    /**
     * Set an entry in the palette to the specified (opaque) color.
     * 
     * @param index Index in the palette to set.
     * @param alpha Alpha value (opacity)
     * @param red Red separation
     * @param green Green separation
     * @param blue Blue separation
     */
    public void setEntry(int index, int alpha, int red, int green, int blue)
    {
        entries[index] = new ARGB8Color(alpha, red, green, blue);
    }

    /**
     * Returns the number of entries in the palette.
     * 
     * @return Number of entries in the palette.
     */
    public int getNumEntries()
    {
        return entries.length;
    }
}
