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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sourceforge.wsup.graphics.ARGB8Palette;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EPSRGB8PalettedDrawableTest
{
    private static File testDirectory = new File("target/test/EPSRGB8PalettedDrawable");
    
    private ARGB8Palette palette;
    
    public EPSRGB8PalettedDrawableTest()
    {
    }
    
    @BeforeClass
    public static void oneTimeSetup()
    {
        if (!testDirectory.exists())
        {
            assertTrue(testDirectory.mkdirs());
        }
    }
    
    @Before
    public void setup()
    {
        palette = new ARGB8Palette(9);
        palette.setEntry(0, 0, 0, 0);
        palette.setEntry(1, 255, 0, 0);
        palette.setEntry(2, 0, 255, 0);
        palette.setEntry(3, 0, 0, 255);
        palette.setEntry(4, 255, 255, 0);
        palette.setEntry(5, 255, 0, 255);
        palette.setEntry(6, 0, 255, 255);
        palette.setEntry(7, 255, 255, 255);
        palette.setEntry(8, 0, 0, 0, 0);   // black, transparent
    }
    
    @Test
    public void testBasicRectangles() throws IOException
    {
        EPSRGB8PalettedDrawable drawable = new EPSRGB8PalettedDrawable(4.0 * 72.0, 2.0 * 72.0, palette);
        
        assertEquals(palette.getNumEntries(), drawable.getNumPaletteEntries());
        
        drawable.setColor(0);
        drawable.fillRect(0, 0, 7200, 7200);
        drawable.setColor(1);
        drawable.fillRect(7200, 0, 7200, 7200);
        drawable.setColor(2);
        drawable.fillRect(14400, 0, 7200, 7200);
        drawable.setColor(3);
        drawable.fillRect(21600, 0, 7200, 7200);
        drawable.setColor(4);
        drawable.fillRect(0, 7200, 7200, 7200);
        drawable.setColor(5);
        drawable.fillRect(7200, 7200, 7200, 7200);
        drawable.setColor(6);
        drawable.fillRect(14400, 7200, 7200, 7200);
        drawable.setColor(7);
        drawable.fillRect(21600, 7200, 7200, 7200);
        drawable.setColor(8);
        drawable.fillRect(0, 0, 28800, 14400);  // shouldn't do anything - transparent
        
        File outputFile = new File(testDirectory, "testBasicRectangles.eps");
        FileOutputStream stream = new FileOutputStream(outputFile);
        drawable.export(stream);
        stream.close();
        drawable.dispose();
    }
    
    @Test
    public void testRoundRect() throws IOException
    {
        EPSRGB8PalettedDrawable drawable = new EPSRGB8PalettedDrawable(4.0 * 72.0, 2.0 * 72.0, palette);
        drawable.setColor(1);
        drawable.fillRoundRect(0, 0, 14400, 14400, 1800);
        drawable.setColor(2);
        drawable.fillRoundRect(14400, 0, 14400, 14400, 900);
        drawable.setColor(7);
        drawable.fillRoundRect(14400 + 450, 0 + 450, 14400 - 900, 14400 - 900, 900);
        
        File outputFile = new File(testDirectory, "testRoundRect.eps");
        FileOutputStream stream = new FileOutputStream(outputFile);
        drawable.export(stream);
        stream.close();
        drawable.dispose();
    }
    
    @Test
    public void testString() throws IOException
    {
        EPSRGB8PalettedDrawable drawable = new EPSRGB8PalettedDrawable(4.0 * 72.0, 2.0 * 72.0, palette);
        
        // done in PS coords
        drawable.createRectPath(0, 72.0, 4.0 * 72.0, 72.0);
        drawable.gsave();
        drawable.setColor(4);
        drawable.fill();
        drawable.grestore();
        drawable.setColor(0);
        drawable.stroke();

        drawable.createRectPath(0, 0, 4.0 * 72.0, 72.0);
        drawable.gsave();
        drawable.setColor(6);
        drawable.fill();
        drawable.grestore();
        drawable.setColor(8);   // transparent - won't stroke
        drawable.stroke();
        
        Font font = new Font("Serif", Font.PLAIN, 3600);
        drawable.setFont(font);
        
        drawable.setColor(0);
        drawable.drawString("Any", 0, 7200);
        
        File outputFile = new File(testDirectory, "testString.eps");
        FileOutputStream stream = new FileOutputStream(outputFile);
        drawable.export(stream);
        stream.close();
        drawable.dispose();
    }
}
