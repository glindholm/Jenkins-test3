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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ARGB8ColorTest
{
    public ARGB8ColorTest()
    {
    }

    @Test
    public void testConstructor1()
    {
        ARGB8Color color = new ARGB8Color();
        assertEquals(255, color.getAlpha());
        assertEquals(0, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(0, color.getBlue());
        assertEquals("ARGB8Color:[255,0,0,0]", color.toString());
    }

    @Test
    public void testConstructor2()
    {
        ARGB8Color color = new ARGB8Color(1, 2, 3);
        assertEquals(255, color.getAlpha());
        assertEquals(1, color.getRed());
        assertEquals(2, color.getGreen());
        assertEquals(3, color.getBlue());
        assertEquals("ARGB8Color:[255,1,2,3]", color.toString());
    }

    @Test
    public void testConstructor3()
    {
        ARGB8Color color = new ARGB8Color(1, 2, 3, 4);
        assertEquals(1, color.getAlpha());
        assertEquals(2, color.getRed());
        assertEquals(3, color.getGreen());
        assertEquals(4, color.getBlue());
        assertEquals("ARGB8Color:[1,2,3,4]", color.toString());
    }

    private static final int[][] RANGE_ASSERTIONS = { 
        { -1, 0, 0, 0 }, // alpha too small
        { 256, 0, 0, 0 }, // alpha too big
        { 0, -1, 0, 0 }, // red too small
        { 0, 256, 0, 0 }, // red too big
        { 0, 0, -1, 0 }, // green too small
        { 0, 0, 256, 0 }, // green too big
        { 0, 0, 0, -1 }, // blue too small
        { 0, 0, 0, 256 }, // blue too big
                                                  };

    @Test
    public void testRangeAssertions()
    {
        for (int i = 0; i < RANGE_ASSERTIONS.length; i++)
        {
            boolean passed = false;
            try
            {
                new ARGB8Color(RANGE_ASSERTIONS[i][0],
                               RANGE_ASSERTIONS[i][1],
                               RANGE_ASSERTIONS[i][2],
                               RANGE_ASSERTIONS[i][3]);
            }
            catch (AssertionError e)
            {
                passed = true;
            }

            assertTrue("RANGE_ASSERTIONS[" + i + "]", passed);
        }
    }
    
    @Test
    public void testEquals()
    {
        ARGB8Color src = new ARGB8Color(0, 0, 0, 0);
        
        assertFalse(src.equals("Other Object Type"));
        
        assertTrue(src.equals(new ARGB8Color(0, 0, 0, 0)));
        assertEquals(src.hashCode(), new ARGB8Color(0, 0, 0, 0).hashCode());
        
        for (int i = 1; i <= 255; i++)
        {
            assertFalse(src.equals(new ARGB8Color(i, 0, 0, 0)));
            assertFalse(src.equals(new ARGB8Color(0, i, 0, 0)));
            assertFalse(src.equals(new ARGB8Color(0, 0, i, 0)));
            assertFalse(src.equals(new ARGB8Color(0, 0, 0, i)));
        }
    }
}
