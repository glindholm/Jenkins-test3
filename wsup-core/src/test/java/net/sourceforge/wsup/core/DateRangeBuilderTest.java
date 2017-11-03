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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class DateRangeBuilderTest
{

    private static final TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
    private static final Locale   locale   = Locale.US;

    @Test
    public final void testBuildHours()
    {
        Calendar base = Calendar.getInstance(timeZone, locale);
        base.set(2008, 8, 11, 10, 6, 31);
        base.set(Calendar.MILLISECOND, 345);

        DateRange dr = DateRangeBuilder.build("h24", base.getTime(), timeZone, locale);
        Calendar expect = Calendar.getInstance(timeZone, locale);
        expect.set(2008, 8, 10, 10, 0, 0);
        expect.set(Calendar.MILLISECOND, 0);

        assertEquals(expect.getTime(), dr.getStart());
        assertEquals(base.getTime(), dr.getEnd());
    }

    @Test
    public final void testBuildDays()
    {
        Calendar base = Calendar.getInstance(timeZone, locale);
        base.set(2008, 8, 11, 10, 6, 31);
        base.set(Calendar.MILLISECOND, 345);

        DateRange dr = DateRangeBuilder.build("d14", base.getTime(), timeZone, locale);
        Calendar expect = Calendar.getInstance(timeZone, locale);
        expect.set(2008, 7, 28, 0, 0, 0);
        expect.set(Calendar.MILLISECOND, 0);

        assertEquals(expect.getTime(), dr.getStart());
        assertEquals(base.getTime(), dr.getEnd());
    }

    @Test
    public final void testBuildMonths()
    {
        Calendar base = Calendar.getInstance(timeZone, locale);
        base.set(2008, 8, 11, 10, 6, 31);
        base.set(Calendar.MILLISECOND, 345);

        DateRange dr = DateRangeBuilder.build("m0", base.getTime(), timeZone, locale);
        Calendar expect = Calendar.getInstance(timeZone, locale);
        expect.set(2008, 8, 1, 0, 0, 0);
        expect.set(Calendar.MILLISECOND, 0);

        assertEquals(expect.getTime(), dr.getStart());
        assertEquals(base.getTime(), dr.getEnd());
    }

    @Test
    public final void testBuildYears()
    {
        Calendar base = Calendar.getInstance(timeZone, locale);
        base.set(2008, 8, 11, 10, 6, 31);
        base.set(Calendar.MILLISECOND, 345);

        DateRange dr = DateRangeBuilder.build("y1", base.getTime(), timeZone, locale);
        Calendar expect = Calendar.getInstance(timeZone, locale);
        expect.set(2007, 0, 1, 0, 0, 0);
        expect.set(Calendar.MILLISECOND, 0);

        assertEquals(expect.getTime(), dr.getStart());
        assertEquals(base.getTime(), dr.getEnd());
    }

    @Test
    public final void testBuildPastHour()
    {
        Calendar base = Calendar.getInstance(timeZone, locale);
        base.set(2008, 8, 11, 10, 6, 31);
        base.set(Calendar.MILLISECOND, 345);

        DateRange dr = DateRangeBuilder.build("xh1", base.getTime(), timeZone, locale);
        Calendar expect = Calendar.getInstance(timeZone, locale);
        expect.set(2008, 8, 11, 9, 0, 0);
        expect.set(Calendar.MILLISECOND, 0);

        assertEquals(expect.getTime(), dr.getStart());

        expect.set(2008, 8, 11, 9, 59, 59);
        expect.set(Calendar.MILLISECOND, 999);
        assertEquals(expect.getTime(), dr.getEnd());
    }

    @Test
    public final void testBuildPastDay()
    {
        Calendar base = Calendar.getInstance(timeZone, locale);
        base.set(2008, 8, 11, 10, 6, 31);
        base.set(Calendar.MILLISECOND, 345);

        DateRange dr = DateRangeBuilder.build("xd2", base.getTime(), timeZone, locale);
        Calendar expect = Calendar.getInstance(timeZone, locale);
        expect.set(2008, 8, 9, 0, 0, 0);
        expect.set(Calendar.MILLISECOND, 0);

        assertEquals(expect.getTime(), dr.getStart());

        expect.set(2008, 8, 9, 23, 59, 59);
        expect.set(Calendar.MILLISECOND, 999);
        assertEquals(expect.getTime(), dr.getEnd());
    }

    @Test
    public final void testBuildPastMonth()
    {
        Calendar base = Calendar.getInstance(timeZone, locale);
        base.set(2008, 8, 11, 10, 6, 31);
        base.set(Calendar.MILLISECOND, 345);

        DateRange dr = DateRangeBuilder.build("xm0", base.getTime(), timeZone, locale);
        Calendar expect = Calendar.getInstance(timeZone, locale);
        expect.set(2008, 8, 1, 0, 0, 0);
        expect.set(Calendar.MILLISECOND, 0);

        assertEquals(expect.getTime(), dr.getStart());

        expect.set(2008, 8, 30, 23, 59, 59);
        expect.set(Calendar.MILLISECOND, 999);
        assertEquals(expect.getTime(), dr.getEnd());
    }

    @Test
    public final void testBuildPastYear()
    {
        Calendar base = Calendar.getInstance(timeZone, locale);
        base.set(2008, 8, 11, 10, 6, 31);
        base.set(Calendar.MILLISECOND, 345);

        DateRange dr = DateRangeBuilder.build("xy1", base.getTime(), timeZone, locale);
        Calendar expect = Calendar.getInstance(timeZone, locale);
        expect.set(2007, 0, 1, 0, 0, 0);
        expect.set(Calendar.MILLISECOND, 0);

        assertEquals(expect.getTime(), dr.getStart());

        expect.set(2007, 11, 31, 23, 59, 59);
        expect.set(Calendar.MILLISECOND, 999);
        assertEquals(expect.getTime(), dr.getEnd());
    }

    @Test
    public final void testInvalidDateCode()
    {
        try
        {
            DateRangeBuilder.build("1", new Date(), timeZone, locale);
            fail();
        }
        catch (IllegalArgumentException e)
        {
        }
        try
        {
            DateRangeBuilder.build("Z", new Date(), timeZone, locale);
            fail();
        }
        catch (IllegalArgumentException e)
        {
        }
        try
        {
            DateRangeBuilder.build("x0", new Date(), timeZone, locale);
            fail();
        }
        catch (IllegalArgumentException e)
        {
        }

    }

    @Test
    public final void testIsDateCodeValid()
    {
        assertEquals(true, DateRangeBuilder.isDateCodeValid("h12"));
        assertEquals(true, DateRangeBuilder.isDateCodeValid("xd1"));
        assertEquals(false, DateRangeBuilder.isDateCodeValid("xxx"));
    }

    @Test
    public final void testCoverage()
    {
        DateRangeBuilder.coverage();
    }
}
