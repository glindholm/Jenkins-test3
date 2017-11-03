/*
 *  Copyright (c) 2013 Kevin Hunter
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

import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;

import net.sourceforge.wsup.graphics.ARGB8Palette;

import org.junit.Before;

public class BaseForBitmapTests
{
    protected ARGB8Palette palette;
    
    public BaseForBitmapTests()
    {
    }
    
    @Before
    public void setup()
    {
        palette = new ARGB8Palette(8);
        palette.setEntry(0, 0, 0, 0);
        palette.setEntry(1, 255, 0, 0);
        palette.setEntry(2, 0, 255, 0);
        palette.setEntry(3, 0, 0, 255);
        palette.setEntry(4, 255, 255, 0);
        palette.setEntry(5, 255, 0, 255);
        palette.setEntry(6, 0, 255, 255);
        palette.setEntry(7, 255, 255, 255);
    }

    public static void verifyRectangle(BufferedImage image, int x, int y, int width, int height, int correct)
    {
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                int rgb = image.getRGB(x + i, y + j) & 0xFFFFFF;
                if (rgb != correct)
                {
                    fail("(" + (x+i) + "," + (y+j) + ") " + Integer.toString(rgb,16) + " instead of " + Integer.toString(correct,16));
                }
            }
        }
    }
    
    public void writeRectangles(BasePalettedBitmapDrawable objUnderTest)
    {
        objUnderTest.setColor(0);
        objUnderTest.fillRect(0, 0, 4, 4);
        objUnderTest.setColor(1);
        objUnderTest.fillRect(4, 0, 4, 4);
        objUnderTest.setColor(2);
        objUnderTest.fillRect(8, 0, 4, 4);
        objUnderTest.setColor(3);
        objUnderTest.fillRect(12, 0, 3, 4);
        objUnderTest.setColor(4);
        objUnderTest.fillRect(0, 4, 4, 4);
        objUnderTest.setColor(5);
        objUnderTest.fillRect(4, 4, 4, 4);
        objUnderTest.setColor(6);
        objUnderTest.fillRect(8, 4, 4, 4);
        objUnderTest.setColor(7);
        objUnderTest.fillRect(12, 4, 3, 4);
     }
}

