/*
 * Copyright (c) 2011 Kevin Hunter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sourceforge.wsup.struts2;

import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.Locale;

import org.junit.Test;

public class SelectOptionComparatorTest
{
    public SelectOptionComparatorTest()
    {
    }
    
    @Test
    public void testDefault()
    {
        SelectOptionComparator comparator = new SelectOptionComparator();
        
        assertTrue(comparator.compare(new SelectOption("key", "a"), new SelectOption("key", "b")) < 0);
        assertTrue(comparator.compare(new SelectOption("key", "a"), new SelectOption("key", "A")) == 0);
    }
    
    @Test
    public void testCaseSensitive()
    {
        SelectOptionComparator comparator = new SelectOptionComparator(true);
        
        assertTrue(comparator.compare(new SelectOption("key", "a"), new SelectOption("key", "b")) < 0);
        assertTrue(comparator.compare(new SelectOption("key", "a"), new SelectOption("key", "A")) != 0);
    }
    
    @Test
    public void testCaseInsensitive()
    {
        SelectOptionComparator comparator = new SelectOptionComparator(false);
        
        assertTrue(comparator.compare(new SelectOption("key", "a"), new SelectOption("key", "b")) < 0);
        assertTrue(comparator.compare(new SelectOption("key", "a"), new SelectOption("key", "A")) == 0);
    }
    
    @Test
    public void testLocale()
    {
        SelectOptionComparator comparator = new SelectOptionComparator(Locale.US);
        
        assertTrue(comparator.compare(new SelectOption("key", "a"), new SelectOption("key", "b")) < 0);
        assertTrue(comparator.compare(new SelectOption("key", "a"), new SelectOption("key", "A")) == 0);
        assertTrue(comparator.compare(new SelectOption("key", "\u00E9"), new SelectOption("key", "f")) < 0);
        assertTrue(comparator.compare(new SelectOption("key", "\u00E9"), new SelectOption("key", "\u00C9")) == 0); 
    }
    
    @Test
    public void testLocaleCaseSensitive()
    {
        SelectOptionComparator comparator = new SelectOptionComparator(Locale.US, true);
        
        assertTrue(comparator.compare(new SelectOption("key", "a"), new SelectOption("key", "b")) < 0);
        assertTrue(comparator.compare(new SelectOption("key", "a"), new SelectOption("key", "A")) != 0);
        assertTrue(comparator.compare(new SelectOption("key", "\u00E9"), new SelectOption("key", "f")) < 0);
        assertTrue(comparator.compare(new SelectOption("key", "\u00E9"), new SelectOption("key", "\u00C9")) != 0); 
    }
    
    @Test
    public void testLocaleCaseInsensitive()
    {
        SelectOptionComparator comparator = new SelectOptionComparator(Locale.US, false);
        
        assertTrue(comparator.compare(new SelectOption("key", "a"), new SelectOption("key", "b")) < 0);
        assertTrue(comparator.compare(new SelectOption("key", "a"), new SelectOption("key", "A")) == 0);
        assertTrue(comparator.compare(new SelectOption("key", "\u00E9"), new SelectOption("key", "f")) < 0);
        assertTrue(comparator.compare(new SelectOption("key", "\u00E9"), new SelectOption("key", "\u00C9")) == 0); 
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testBridgeMethod()
    {
        SelectOptionComparator comparator = new SelectOptionComparator();
        
        Object a = new SelectOption("key", "a");
        Object b = new SelectOption("key", "b");
        
        // Cover the hidden bridge method generated as part of type erasure
        assertTrue(((Comparator)comparator).compare(a, b) < 0);
    }
}

