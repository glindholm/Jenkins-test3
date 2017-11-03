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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;

public class BasePalettedBitmapDrawableTest
{
    public BasePalettedBitmapDrawableTest()
    {
    }

    private static final int[][] BAD_CONSTRUCTOR_ASSERTIONS = { { -1, 16, 16, 300 }, // width < 0
        { 0, 16, 16, 300 }, // width == 0
        { 16, -1, 16, 300 }, // height < 0
        { 16, 0, 16, 300 }, // height == 0
        { 16, 16, -1, 300 }, // numPalette < 0
        { 16, 16, 0, 300 }, // numPalette == 0
        { 16, 16, 257, 300 }, // numPalette > 256
        { 16, 16, 256, -1 }, // resolution < 0
                                                            };

    @Test
    public void testBadConstructorAssertions()
    {
        for (int i = 0; i < BAD_CONSTRUCTOR_ASSERTIONS.length; i++)
        {
            boolean asserted = false;
            try
            {
                new TestClass(BAD_CONSTRUCTOR_ASSERTIONS[i][0],
                              BAD_CONSTRUCTOR_ASSERTIONS[i][1],
                              BAD_CONSTRUCTOR_ASSERTIONS[i][2],
                              BAD_CONSTRUCTOR_ASSERTIONS[i][3]);
            }
            catch (AssertionError e)
            {
                asserted = true;
            }
            assertTrue("BAD_CONSTRUCTOR_ASSERTIONS[" + i + "] didn't assert", asserted);
        }
    }

    @Test
    public void assertsIfSetColorNegative()
    {
        TestClass objUnderTest = new TestClass(16, 16, 16, 300);

        boolean asserted = false;

        try
        {
            objUnderTest.setColor(-1);
        }
        catch (AssertionError e)
        {
            asserted = true;
        }

        assertTrue(asserted);
    }

    @Test
    public void assertsIfSetColorLargerThanPalette()
    {
        TestClass objUnderTest = new TestClass(16, 16, 16, 300);

        boolean asserted = false;

        try
        {
            objUnderTest.setColor(128);
        }
        catch (AssertionError e)
        {
            asserted = true;
        }

        assertTrue(asserted);
    }

    @Test
    public void assertsExtractRowPixelsIntoTooShortABuffer()
    {
        TestClass objUnderTest = new TestClass(16, 16, 16, 300);

        boolean asserted = false;

        try
        {
            objUnderTest.extractRowPixels(0, new byte[1]);
        }
        catch (AssertionError e)
        {
            asserted = true;
        }

        assertTrue(asserted);
    }

    private static class TestClass extends BasePalettedBitmapDrawable
    {
        public TestClass(int width, int height, int numPalette, int resolution)
        {
            super(width, height, numPalette, resolution);
        }

        @Override
        public void export(OutputStream stream) throws IOException
        {
        }
    }

}
