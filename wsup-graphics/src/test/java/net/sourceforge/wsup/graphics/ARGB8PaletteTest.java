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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ARGB8PaletteTest
{
    public ARGB8PaletteTest()
    {
    }
    
    @Test
    public void testConstructor1()
    {
        ARGB8Palette palette = new ARGB8Palette(4);
        assertEquals(4, palette.getNumEntries());
        
        for (int i = 0; i < 4; i++)
        {
            assertEquals(ARGB8Color.MIN_VALUE, palette.getEntry(i).getRed());
            assertEquals(ARGB8Color.MIN_VALUE, palette.getEntry(i).getGreen());
            assertEquals(ARGB8Color.MIN_VALUE, palette.getEntry(i).getBlue());
            assertEquals(ARGB8Color.MAX_VALUE, palette.getEntry(i).getAlpha());
        }
    }
    
    @Test
    public void testConstructor2()
    {
        ARGB8Color[] entries = new ARGB8Color[4];
        
        entries[0] = new ARGB8Color(128, 0, 0);
        entries[1] = new ARGB8Color(0, 128, 0);
        entries[2] = new ARGB8Color(0, 0, 128);
        entries[3] = new ARGB8Color(128, 0, 0, 0);
        
        ARGB8Palette palette = new ARGB8Palette(entries);
        assertEquals(4, palette.getNumEntries());
        
        // values were copied
        assertEquals(128, entries[0].getRed());
        assertEquals(128, entries[1].getGreen());
        assertEquals(128, entries[2].getBlue());
        assertEquals(128, entries[3].getAlpha());
    }
    
    @Test
    public void testConstructor3()
    {
        ARGB8Palette src = new ARGB8Palette(4);
        for (int i = 0; i < 4; i++)
        {
            src.setEntry(i, i, 0, 0);
        }
        
        ARGB8Palette palette = new ARGB8Palette(src);
        
        for (int i = 0; i < 4; i++)
        {
            assertTrue(palette.getEntry(i).equals(src.getEntry(i)));
        }
    }
    
    @Test
    public void testSet()
    {
        ARGB8Palette src = new ARGB8Palette(4);
        for (int i = 0; i < 4; i++)
        {
            src.setEntry(i, new ARGB8Color(i, 0, 0));
        }
        
        for (int i = 0; i < 4; i++)
        {
            assertEquals(i, src.getEntry(i).getRed());
        }

    }
    
    @Test
    public void testSetNullAsserts()
    {
        boolean passed;
        ARGB8Palette src = new ARGB8Palette(4);
        
        try
        {
            src.setEntry(0, null);
            passed = false;
        }
        catch(AssertionError e)
        {
            passed = true;
        }
        
        assertTrue(passed);
    }
}

