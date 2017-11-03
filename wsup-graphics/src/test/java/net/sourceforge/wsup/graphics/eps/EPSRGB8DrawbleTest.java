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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EPSRGB8DrawbleTest
{
    public EPSRGB8DrawbleTest()
    {
    }
    
    @Test
    public void testSetColor()
    {
        TestClass test = new TestClass(72,72);
        
        test.setColor(0, 255, 255, 255);
        assertTrue(test.getTransparent());
        assertEquals(1.0, test.getR(), 0.0001);
        assertEquals(1.0, test.getG(), 0.0001);
        assertEquals(1.0, test.getB(), 0.0001);
        
        test.setColor(0, 0, 0);
        assertFalse(test.getTransparent());
        assertEquals(0.0, test.getR(), 0.0001);
        assertEquals(0.0, test.getG(), 0.0001);
        assertEquals(0.0, test.getB(), 0.0001);
        
        test.setColor(255, 255, 255);
        assertFalse(test.getTransparent());
        assertEquals(1.0, test.getR(), 0.0001);
        assertEquals(1.0, test.getG(), 0.0001);
        assertEquals(1.0, test.getB(), 0.0001);
    }
    
    @Test
    public void testBadSetsAssert()
    {
        TestClass test = new TestClass(72,72);
        boolean failed;
        try
        {
            failed = true;
            test.setColor(-1, 0, 0);
        }
        catch(AssertionError e)
        {
            failed = false;
        }
        assertFalse(failed);
        try
        {
            failed = true;
            test.setColor(0, -1, 0);
        }
        catch(AssertionError e)
        {
            failed = false;
        }
        assertFalse(failed);
        try
        {
            failed = true;
            test.setColor(0, 0, -1);
        }
        catch(AssertionError e)
        {
            failed = false;
        }
        assertFalse(failed);
        try
        {
            failed = true;
            test.setColor(256, 0, 0);
        }
        catch(AssertionError e)
        {
            failed = false;
        }
        assertFalse(failed);
        try
        {
            failed = true;
            test.setColor(0, 256, 0);
        }
        catch(AssertionError e)
        {
            failed = false;
        }
        assertFalse(failed);
        try
        {
            failed = true;
            test.setColor(0, 0, 256);
        }
        catch(AssertionError e)
        {
            failed = false;
        }
        assertFalse(failed);
    }
    
    private static class TestClass extends EPSRGB8Drawable
    {
        private double r;
        private double g;
        private double b;
        
        public TestClass(double widthPts, double heightPts)
        {
            super(widthPts, heightPts);
        }

        @Override
        public void setrgbolor(double r, double g, double b)
        {
            this.r = r;
            this.g = g;
            this.b = b;
            super.setrgbolor(r, g, b);
        }

        public double getR()
        {
            return r;
        }

        public double getG()
        {
            return g;
        }

        public double getB()
        {
            return b;
        }
    }
}

