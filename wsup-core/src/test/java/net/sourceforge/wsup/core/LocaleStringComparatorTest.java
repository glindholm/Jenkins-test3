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

package net.sourceforge.wsup.core;

import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.Locale;

import org.junit.Test;

public class LocaleStringComparatorTest
{
    public LocaleStringComparatorTest()
    {
    }
    
    @Test
    public void testDefault()
    {
        LocaleStringComparator comparator = new LocaleStringComparator();
        
        assertTrue(comparator.compare("a", "b") < 0);
        assertTrue(comparator.compare("a", "A") == 0);
    }
    
    @Test
    public void testCaseSensitive()
    {
        LocaleStringComparator comparator = new LocaleStringComparator(true);
        
        assertTrue(comparator.compare("a", "b") < 0);
        assertTrue(comparator.compare("a", "A") != 0);
    }
    
    @Test
    public void testCaseInsensitive()
    {
        LocaleStringComparator comparator = new LocaleStringComparator(false);
        
        assertTrue(comparator.compare("a", "b") < 0);
        assertTrue(comparator.compare("a", "A") == 0);
    }
    
    @Test
    public void testLocale()
    {
        LocaleStringComparator comparator = new LocaleStringComparator(Locale.US);
        
        assertTrue(comparator.compare("a", "b") < 0);
        assertTrue(comparator.compare("a", "A") == 0);
        assertTrue(comparator.compare("\u00E9", "f") < 0);  // lower-case e with accent
        assertTrue(comparator.compare("\u00E9", "\u00C9") == 0);   // lower-case e with accent, upper-case e with accent
    }
    
    @Test
    public void testLocaleCaseSensitive()
    {
        LocaleStringComparator comparator = new LocaleStringComparator(Locale.US, true);
        
        assertTrue(comparator.compare("a", "b") < 0);
        assertTrue(comparator.compare("a", "A") != 0);
        assertTrue(comparator.compare("\u00E9", "f") < 0);  // lower-case e with accent
        assertTrue(comparator.compare("\u00E9", "\u00C9") != 0);    // lower-case e with accent, upper-case e with accent
    }
    
    @Test
    public void testLocaleCaseInsensitive()
    {
        LocaleStringComparator comparator = new LocaleStringComparator(Locale.US, false);
        
        assertTrue(comparator.compare("a", "b") < 0);
        assertTrue(comparator.compare("a", "A") == 0);
        assertTrue(comparator.compare("\u00E9", "f") < 0);  // lower-case e with accent
        assertTrue(comparator.compare("\u00E9", "\u00C9") == 0);     // lower-case e with accent, upper-case e with accent
    }
    
    @Test
    public void testNulls()
    {
        LocaleStringComparator comparator = new LocaleStringComparator();
        
        assertTrue(comparator.compare(null, null) == 0);
        assertTrue(comparator.compare(null, "a") < 0);
        assertTrue(comparator.compare("a", null) > 0);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testCoverBridgeMethod()
    {
        LocaleStringComparator comparator = new LocaleStringComparator(Locale.US, false);
        
        // cover the bridge method generated by type erasure
        assertTrue(((Comparator)comparator).compare("a", "b") < 0);
    }
}