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
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class EPSDrawableTest
{
    public EPSDrawableTest()
    {
    }
    
    @Test
    public void testSize()
    {
        EPSDrawable drawable = new EPSDrawable(144.0, 72.0, false);
        assertEquals(2.0 * EPSDrawable.POINTS_PER_INCH, drawable.getWidthPts(), 0.0001);
        assertEquals(1.0 * EPSDrawable.POINTS_PER_INCH, drawable.getHeightPts(), 0.0001);
        assertEquals(2 * EPSDrawable.ARTIFICIAL_DPI, drawable.getWidth());
        assertEquals(1 * EPSDrawable.ARTIFICIAL_DPI, drawable.getHeight());
        assertEquals(EPSDrawable.ARTIFICIAL_DPI, drawable.getResolutionDPI());
    }
    
    private void compare(String test, String[] expected, String[] actual)
    {
        for (int i = 0; i < expected.length; i++)
        {
            if (i >= actual.length)
            {
                fail(test + " Expected EOF at line " + (i+1));
            }
            
            assertEquals(test + " line " + (i+1), expected[i], actual[i]);
        }
        
        if (actual.length > expected.length)
        {
            fail(test + " Got " + (actual.length-expected.length) + " extra lines");
        }
    }
    
    private String[] getOutput(EPSDrawable drawable) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        drawable.export(stream);
        String result = new String(stream.toByteArray());
        return result.split("\n");
    }
    
    private static final String[] MIN_RGB =
    {
        "%!PS-Adobe-3.0 EPSF-3.0",
        "%%BoundingBox: 0 0 145 73",
        "%%DocumentData: Clean7Bit",
        "%%EndComments",
        "gsave",
        "grestore",
        "showpage",
        "%%EOF",
    };
    
    @Test
    public void testMinimumRgb() throws Exception
    {
        EPSDrawable drawable = new EPSDrawable(144.5, 72.5, false);
        String[] result = getOutput(drawable);
        compare("testMinimumRgb", MIN_RGB, result);
        drawable.dispose();
    }
    
    private static final String[] MIN_CMYK =
    {
        "%!PS-Adobe-3.0 EPSF-3.0",
        "%%BoundingBox: 0 0 144 72",
        "%%Extensions: CMYK",
        "%%DocumentProcessColors: Cyan Magenta Yellow Black",
        "%%DocumentData: Clean7Bit",
        "%%EndComments",
        "gsave",
        "grestore",
        "showpage",
        "%%EOF",
    };
    
    @Test
    public void testMinimumCmyk() throws Exception
    {
        EPSDrawable drawable = new EPSDrawable(144.0, 72.0, true);
        String[] result = getOutput(drawable);
        compare("testMinimumCmyk", MIN_CMYK, result);
        drawable.dispose();
    }
    
    private static final String[] RGB_WITH_TITLE_AND_CREATOR =
    {
        "%!PS-Adobe-3.0 EPSF-3.0",
        "%%Creator: The Creator",
        "%%Title: The Title",
        "%%BoundingBox: 0 0 145 73",
        "%%DocumentData: Clean7Bit",
        "%%EndComments",
        "gsave",
        "grestore",
        "showpage",
        "%%EOF",
    };
    
    @Test
    public void testRgbWithTitleAndCreator() throws Exception
    {
        EPSDrawable drawable = new EPSDrawable(144.1, 72.1, false);
        drawable.setTitle("The Title");
        drawable.setCreator("The Creator");
        String[] result = getOutput(drawable);
        compare("testRgbWithTitleAndCreator", RGB_WITH_TITLE_AND_CREATOR, result);
        drawable.dispose();
    }
}

