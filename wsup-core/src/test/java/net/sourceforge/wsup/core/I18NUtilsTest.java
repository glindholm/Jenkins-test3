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

import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class I18NUtilsTest
{

    @Test
    public void testBuildLocale()
    {
        assertEquals(Locale.ENGLISH, I18NUtils.buildLocale("en"));
        assertEquals(Locale.US, I18NUtils.buildLocale("en_US"));
        assertEquals(new Locale("ja", "JP", "JP"), I18NUtils.buildLocale("ja_JP_JP"));

        assertEquals(null, I18NUtils.buildLocale(""));
        assertEquals(null, I18NUtils.buildLocale("123"));
    }

    @Test
    public void testBuildTimezone()
    {
        assertEquals(TimeZone.getTimeZone("America/New_York"), I18NUtils
            .buildTimeZone("America/New_York"));
        assertEquals(TimeZone.getTimeZone("GMT"), I18NUtils.buildTimeZone("GMT"));

        assertEquals(null, I18NUtils.buildTimeZone(""));
        assertEquals(null, I18NUtils.buildTimeZone("123"));
    }

    @Test
    public void testGetCustomTimeZoneId()
    {
        assertEquals("GMT", I18NUtils.getCustomTimeZoneId(TimeZone.getTimeZone("GMT")));
        assertEquals("GMT-13:30", I18NUtils.getCustomTimeZoneId(TimeZone.getTimeZone("GMT-13:30")));
        assertEquals("GMT-05:00", I18NUtils.getCustomTimeZoneId(TimeZone
            .getTimeZone("America/New_York")));
        assertEquals("GMT+08:00", I18NUtils.getCustomTimeZoneId(TimeZone
            .getTimeZone("Asia/Hong_Kong")));
        assertEquals("GMT-08:00", I18NUtils.getCustomTimeZoneId(TimeZone.getTimeZone("US/Pacific")));
        assertEquals("GMT", I18NUtils.getCustomTimeZoneId(TimeZone.getTimeZone("Europe/London")));
    }

    @Test
    public void testGetMonthNames()
    {
        assertEquals("[January, February, March, April, May, June, July, August, September, October, November, December, ]",
                     Arrays.toString(I18NUtils.getMonthNames(Locale.US)));
        assertEquals(13, I18NUtils.getMonthNames(Locale.US).length);

        assertEquals("[janvier, f\u00E9vrier, mars, avril, mai, juin, juillet, ao\u00FBt, septembre, octobre, novembre, d\u00E9cembre, ]",
                     Arrays.toString(I18NUtils.getMonthNames(Locale.CANADA_FRENCH)));

        assertEquals("[enero, febrero, marzo, abril, mayo, junio, julio, agosto, septiembre, octubre, noviembre, diciembre, ]",
                     Arrays.toString(I18NUtils.getMonthNames(new Locale("es", "MX"))));

    }

    @Test
    public void testCoverage()
    {
        I18NUtils.getAvailableTimeZones();
        I18NUtils.coverage();
    }
}
