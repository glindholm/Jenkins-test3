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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A collection of utility methods for building {@link DateRange}'s based on a dateCode.
 *
 * @author Greg Lindholm
 *
 */
public final class DateRangeBuilder
{
    private static final String  DATE_CODE_RE    = "(x?[hdmy])([0-9]+)";
    private static final Pattern dateCodePattern = Pattern.compile(DATE_CODE_RE);

    /**
     * Check if a dateCode is valid. <br>
     * The dateCode must match the RE pattern "x?[hdmy][0-9]+". <br>
     * h=hours, d=days, m=months, y=years
     *
     *
     * <br>
     * A leading 'x' means 'past'. <br>
     * "xd1" means yesterday (past day -1). <br>
     * "xm2" means 2 months ago (past month -2). <br>
     * "xy0" means this year (past year -0).
     *
     * @param dateCode the date code
     * @return <code>true</code> if the <code>dateCode</code> is valid
     */
    public static boolean isDateCodeValid(CharSequence dateCode)
    {
        return dateCodePattern.matcher(dateCode).matches();
    }

    /**
     * Builds a DateRange based on dateCode.
     *
     * <pre>
     * h12 = previous 12 hours (full hours from minute 0)
     * d2 = previous 2 days (full days from hour 0)
     * m0 = current month (from day 1)
     * m2 = previous 2 full months (month-2, from day 1)
     * y1 = previous full year (year-1, from month 0, day 1)
     *
     * xd1 = past day -1 or yesterday (day - 1, time 0:00:00 to 23:59:59)
     * xd2 = past day -2 or day before yesterday (day - 2, time 0:00:00 to 23:59:59)
     * xm1 = past month -1 (last whole month) (month - 1, day 0 - last)
     * </pre>
     *
     * @param dateCode the date code
     * @param now the 'now' time
     * @param timeZone time zone
     * @param locale locale
     * @return a DateRange based on dateCode.
     */
    public static DateRange build(CharSequence dateCode, Date now, TimeZone timeZone, Locale locale)
    {
        Matcher matcher = dateCodePattern.matcher(dateCode);

        if (!matcher.matches())
        {
            throw new IllegalArgumentException(String.format("Invalid dateCode [%s]", dateCode));
        }

        String dateCodeUnit = matcher.group(1);
        int value = Integer.parseInt(matcher.group(2));

        Calendar start = Calendar.getInstance(timeZone, locale);
        start.setTime(now);
        Calendar end = Calendar.getInstance(timeZone, locale);
        end.setTime(now);

        if (dateCodeUnit.length() == 1)
        {
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);

            switch (dateCodeUnit.charAt(0))
            {
            case 'h': // Full Hours ago
                start.add(Calendar.HOUR, -value);
                break;

            case 'd': // Full Days ago
                start.add(Calendar.DAY_OF_MONTH, -value);

                start.set(Calendar.HOUR_OF_DAY, 0);
                break;

            case 'm': // Full Months ago
                start.add(Calendar.MONTH, -value);

                start.set(Calendar.HOUR_OF_DAY, 0);
                start.set(Calendar.DAY_OF_MONTH, 1);
                break;

            case 'y': // Full Years ago
                start.add(Calendar.YEAR, -value);

                start.set(Calendar.HOUR_OF_DAY, 0);
                start.set(Calendar.DAY_OF_MONTH, 1);
                start.set(Calendar.MONTH, 0);
                break;
            }
        }
        else
        {
            Assert.equals('x', dateCodeUnit.charAt(0));

            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);

            end.set(Calendar.MINUTE, 59);
            end.set(Calendar.SECOND, 59);
            end.set(Calendar.MILLISECOND, 999);

            switch (dateCodeUnit.charAt(1))
            {
            case 'h': // -x Hour
                start.add(Calendar.HOUR, -value);

                end.add(Calendar.HOUR, -value);
                break;

            case 'd': // -x Day
                start.add(Calendar.DAY_OF_MONTH, -value);

                start.set(Calendar.HOUR_OF_DAY, 0);

                end.add(Calendar.DAY_OF_MONTH, -value);

                end.set(Calendar.HOUR_OF_DAY, 23);
                break;

            case 'm': // -x Month
                start.add(Calendar.MONTH, -value);

                start.set(Calendar.HOUR_OF_DAY, 0);
                start.set(Calendar.DAY_OF_MONTH, 1);

                end.add(Calendar.MONTH, -value);

                end.set(Calendar.HOUR_OF_DAY, 23);
                end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;

            case 'y': // -x Year
                start.add(Calendar.YEAR, -value);

                start.set(Calendar.HOUR_OF_DAY, 0);
                start.set(Calendar.DAY_OF_MONTH, 1);
                start.set(Calendar.MONTH, 0);

                end.add(Calendar.YEAR, -value);

                end.set(Calendar.HOUR_OF_DAY, 23);
                end.set(Calendar.MONTH, 11);
                end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            }

        }

        return new DateRange(start.getTime(), end.getTime());

    }

    private DateRangeBuilder()
    {
    }

    static void coverage()
    {
        new DateRangeBuilder();
    }
}
