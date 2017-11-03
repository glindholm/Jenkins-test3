/*
 *  Copyright (c) 2010 Greg Lindholm
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

import static org.junit.Assert.*;

import java.net.URLEncoder;

import org.junit.Test;

public class EncodeUtilsTest
{
    public EncodeUtilsTest()
    {
    }

    @Test
    public void testUrlEncode()
    {
        assertEquals("%3CCut+%26+Dry%3E", EncodeUtils.urlEncode("<Cut & Dry>"));
    }

    @Test
    public void testUnicodeEscapeChar()
    {
        assertEquals("\\u000a", EncodeUtils.unicodeEscape('\n'));
        assertEquals("\\u0020", EncodeUtils.unicodeEscape(' '));
        assertEquals("\\u007e", EncodeUtils.unicodeEscape('~'));
        assertEquals("\\u0100", EncodeUtils.unicodeEscape('\u0100'));
        assertEquals("\\u0fff", EncodeUtils.unicodeEscape('\u0FFF'));
        assertEquals("\\u1000", EncodeUtils.unicodeEscape('\u1000'));
        assertEquals("\\uffff", EncodeUtils.unicodeEscape('\uFFFF'));
    }

    @Test
    public void testUnicodeEscapeString()
    {
        assertNull(EncodeUtils.unicodeEscape(null, false));
        assertEquals("\\u000a ABC\\u007f", EncodeUtils.unicodeEscape("\n ABC\u007F", false));
        assertEquals("\\u000a\\u0020ABC\\u007f", EncodeUtils.unicodeEscape("\n ABC\u007F", true));

        String unaltered = "abc";
        String output = EncodeUtils.unicodeEscape(unaltered, false);
        assertTrue(output == unaltered);
    }

    @Test
    public void testToHex()
    {
        assertNull(EncodeUtils.toHex(null));
        assertEquals("", EncodeUtils.toHex(new byte[0]));
        byte[] input = { 0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF };
        assertEquals("1234567890ABCDEF", EncodeUtils.toHex(input));
    }

    private void assertArraySame(String test, byte[] expected, byte[] actual)
    {
        assertEquals(test, expected.length, actual.length);
        for (int i = 0; i < expected.length; i++)
        {
            assertEquals(test + "[" + i + "]", expected[i], actual[i]);
        }
    }

    @Test
    public void testFromHexStringSuccess()
    {
        assertNull(EncodeUtils.fromHex(null));
        assertArraySame("zero length", new byte[0], EncodeUtils.fromHex(""));
        byte[] output = {
            0x12,
            0x34,
            0x56,
            0x78,
            (byte) 0x90,
            (byte) 0xAB,
            (byte) 0xCD,
            (byte) 0xEF };
        assertArraySame("upper case", output, EncodeUtils.fromHex("1234567890ABCDEF"));
        assertArraySame("lower case", output, EncodeUtils.fromHex("1234567890abcdef"));
    }

    @Test
    public void testFromHexStringFailure()
    {
        assertNull(EncodeUtils.fromHex("123")); // odd length string
        assertNull(EncodeUtils.fromHex("Z1")); // first char of pair bad
        assertNull(EncodeUtils.fromHex("1Z")); // second char of pair bad
    }

    @Test
    public void testFromHexCharReturnsCorrectValues()
    {
        String lower = "0123456789abcdef";
        for (int i = 0; i < lower.length(); i++)
        {
            assertEquals("lower[" + i + "]", i, EncodeUtils.fromHex(lower.charAt(i)));
        }
        String upper = "0123456789ABCDEF";
        for (int i = 0; i < upper.length(); i++)
        {
            assertEquals("upper[" + i + "]", i, EncodeUtils.fromHex(upper.charAt(i)));
        }
    }

    @Test
    public void testFromHexCharRejectsAllNonHexChars()
    {
        String hexChars = "0123456789abcdefABCDEF";

        for (int i = 0; i <= 0xFFFF; i++)
        {
            char c = (char) i;
            if (hexChars.indexOf(c) < 0)
            {
                assertTrue(EncodeUtils.fromHex(c) < 0);
            }
        }
    }

    @Test
    public void testPercentEscape() throws Exception
    {
        assertEquals("", EncodeUtils.percentEscape(""));
        assertEquals(" a-zA-Z/&~@#$%^\\", EncodeUtils.percentEscape(" a-zA-Z/&~@#$%^\\"));
        assertEquals(" %0D %0A ", EncodeUtils.percentEscape(" \r \n "));

        {
            String testCyrlic = "\u0441\u0430\u0445\u0430\u043B\u0438\u043D-\u0442\u0443\u0440\u0438\u0437\u043C.\u0440\u0444";

            // URLEncoder gives the same results since there are no special URL chars in test string
            assertEquals(URLEncoder.encode(testCyrlic, "UTF-8"),
                         EncodeUtils.percentEscape(testCyrlic));
            assertEquals("%D1%81%D0%B0%D1%85%D0%B0%D0%BB%D0%B8%D0%BD-%D1%82%D1%83%D1%80%D0%B8%D0%B7%D0%BC.%D1%80%D1%84",
                         EncodeUtils.percentEscape(testCyrlic));
        }

        // 4 byte unicode
        assertEquals("%F0%90%90%80",
                     EncodeUtils.percentEscape(new String(Character.toChars(0x10400))));

    }

    @Test
    public void testCoverage()
    {
        EncodeUtils.coverage();
    }

}
