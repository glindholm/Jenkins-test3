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

import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sourceforge.wsup.graphics.ARGB8Palette;

import org.junit.BeforeClass;
import org.junit.Test;

public class PNG8DrawableTest extends BaseForBitmapTests
{
    private static File testDirectory = new File("target/test/PNG8Drawable");

    public PNG8DrawableTest()
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

    @Test
    public void testBasicRectangles() throws IOException
    {
        PNG8Drawable objUnderTest = new PNG8Drawable(15, 9, palette, 300);

        writeRectangles(objUnderTest);

        File outputFile = new File(testDirectory, "testBasicRectangles.png");
        FileOutputStream stream = new FileOutputStream(outputFile);
        objUnderTest.export(stream);
        stream.close();
        objUnderTest.dispose();

        BufferedImage image = ImageIO.read(outputFile);

        verifyRectangle(image, 0, 0, 4, 4, 0x00000000);
        verifyRectangle(image, 4, 0, 4, 4, 0x00FF0000);
        verifyRectangle(image, 8, 0, 4, 4, 0x0000FF00);
        verifyRectangle(image, 12, 0, 3, 4, 0x000000FF);
        verifyRectangle(image, 0, 4, 4, 4, 0x00FFFF00);
        verifyRectangle(image, 4, 4, 4, 4, 0x00FF00FF);
        verifyRectangle(image, 8, 4, 4, 4, 0x0000FFFF);
        verifyRectangle(image, 12, 4, 3, 4, 0x00FFFFFF);
    }
    
    private byte[] generateBytes(PNG8Drawable objUnderTest) throws Exception
    {
        for (int i = 0; i < 256; i++)
        {
            for (int j = 0; j < 256; j++)
            {
                objUnderTest.setColor((i + j) % 256);
                objUnderTest.fillRect(i, j, 1, 1);
            }
        }
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        objUnderTest.export(stream);
        
        return stream.toByteArray();
    }

    @Test
    public void testCanHandlePixelDataLargerThanChunkSize() throws Exception
    {
        ARGB8Palette fullPalette = new ARGB8Palette(256);
        for (int i = 0; i < 256; i++)
        {
            fullPalette.setEntry(i, i, i, i);
        }

        PNG8Drawable objUnderTest = new PNG8Drawable(256, 256, fullPalette, 300);
        byte[] original = generateBytes(objUnderTest);
        
        /*
         * Now repeat with one that has a smaller chunk buffer
         */
        objUnderTest = new PNG8Drawable(256, 256, fullPalette, 300, new PNGChunkHandler(1024));
        byte[] updated = generateBytes(objUnderTest);
        
        assertTrue(updated.length > original.length);   // uses more chunks
    }
}
