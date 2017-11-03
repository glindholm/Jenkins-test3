/*
 *  Copyright (c) 2010 Kevin Hunter
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

public class BeanUtilsTest
{
    public BeanUtilsTest()
    {
    }

    @Test
    public void coverage()
    {
        BeanUtils.coverage();
    }

    @Test
    public void testHashCode()
    {
        assertEquals(0, BeanUtils.safeHashCode(null));
        assertEquals("abc".hashCode(), BeanUtils.safeHashCode("abc"));
    }

    @Test
    public void testEquals()
    {
        assertTrue(BeanUtils.safeEquals(null, null));
        assertFalse(BeanUtils.safeEquals("abc", null));
        assertFalse(BeanUtils.safeEquals(null, "abc"));
        assertTrue(BeanUtils.safeEquals("abc", new String("abc")));
    }

    @Test
    public void testEqualsIgnoreCase()
    {
        assertTrue(BeanUtils.safeEqualsIgnoreCase(null, null));
        assertFalse(BeanUtils.safeEqualsIgnoreCase("abc", null));
        assertFalse(BeanUtils.safeEqualsIgnoreCase(null, "abc"));
        assertTrue(BeanUtils.safeEqualsIgnoreCase("abc", new String("abc")));
        assertTrue(BeanUtils.safeEqualsIgnoreCase("ABC", new String("abc")));
        assertTrue(BeanUtils.safeEqualsIgnoreCase("abc", new String("ABC")));
    }

    @Test
    public void testTrimAndTruncate()
    {
        assertEquals(null, BeanUtils.trimAndTruncate(null, 4));
        assertEquals(null, BeanUtils.trimAndTruncate("", 4));
        assertEquals(null, BeanUtils.trimAndTruncate(" ", 4));
        assertEquals(null, BeanUtils.trimAndTruncate("       ", 4));

        assertEquals("A", BeanUtils.trimAndTruncate("A", 4));
        assertEquals("A", BeanUtils.trimAndTruncate("A ", 4));
        assertEquals("A", BeanUtils.trimAndTruncate(" A ", 4));
        assertEquals("A", BeanUtils.trimAndTruncate(" A         b", 4));

        assertEquals("Abcd", BeanUtils.trimAndTruncate("Abcd", 4));
        assertEquals("Abcd", BeanUtils.trimAndTruncate(" Abcd ", 4));
        assertEquals("Abcd", BeanUtils.trimAndTruncate(" Abcde ", 4));
        assertEquals("A  b", BeanUtils.trimAndTruncate("A  b d e", 4));

    }

    @Test
    public void testSafeCopyDate()
    {
        assertEquals(null, BeanUtils.safeCopyDate(null));
        assertEquals(1000L, BeanUtils.safeCopyDate(new Date(1000L)).getTime());
    }
}
