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

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * A collection of utility methods to assist in Internationalization (I18N).
 *
 * @author Greg Lindholm
 *
 */
public final class I18NUtils
{
    private static final List<TimeZone> availableTimeZones = Collections.unmodifiableList(buildAvailableTimeZones());

    private static final int     LOCALE_PATTERN_LANG_GROUP    = 1;
    private static final int     LOCALE_PATTERN_COUNTRY_GROUP = 3;
    private static final int     LOCALE_PATTERN_VARIANT_GROUP = 5;
    private static final Pattern localePattern                = Pattern
                                                                  .compile("([a-z][a-z])(_([A-Z][A-Z])(_(.+))?)?");

    /**
     * Build a Locale from <code>id</code>
     *
     * @param id The locale id in "ll" or "ll_CC" or "ll_CC_VV" format.
     * @return the Locale or null if <code>id</code> is not a valid id
     */
    public static Locale buildLocale(String id)
    {
        Matcher matcher = localePattern.matcher(id);

        if (!matcher.matches())
        {
            return null;
        }

        String lang = matcher.group(LOCALE_PATTERN_LANG_GROUP);
        String country = StringUtils.defaultString(matcher.group(LOCALE_PATTERN_COUNTRY_GROUP), "");
        String variant = StringUtils.defaultString(matcher.group(LOCALE_PATTERN_VARIANT_GROUP), "");
        return new Locale(lang, country, variant);
    }

    /**
     * Build a TimeZone from <code>id</code>. <br>
     * This is a wrapper around TimeZone.getTimeZone(id) that detects invalid id's and returns null
     * instead of GMT.
     *
     * @param id the ID for a <code>TimeZone</code>, either an abbreviation
     *            such as "PST", a full name such as "America/Los_Angeles", or a custom
     *            ID such as "GMT-8:00".
     * @return the TimeZone or null if <code>id</code> is not a valid id
     */
    public static TimeZone buildTimeZone(String id)
    {
        TimeZone timezone = TimeZone.getTimeZone(id);
        /*
         * If id is not recognized it will use GMT so test it.
         */
        if ("GMT".equals(timezone.getID()))
        {
            if (!timezone.getID().equalsIgnoreCase(id))
            {
                return null;
            }
        }

        return timezone;
    }

    public static String getCustomTimeZoneId(TimeZone timeZone)
    {
        long offset = timeZone.getRawOffset();

        if (offset == 0)
        {
            return "GMT";
        }

        boolean negative = offset < 0;
        if (negative)
        {
            offset *= -1;
        }
        long hours = offset / DateUtils.MILLIS_PER_HOUR;
        long minutes = (offset - hours * DateUtils.MILLIS_PER_HOUR) / DateUtils.MILLIS_PER_MINUTE;

        if (negative)
        {
            return String.format("GMT-%02d:%02d", hours, minutes);
        }
        else
        {
            return String.format("GMT+%02d:%02d", hours, minutes);
        }
    }

    /**
     * Gets an immutable sorted list of available TimeZones. The list is sorted
     * by raw offset and Id.
     *
     * @return an immutable sorted list of available TimeZones.
     */
    public static List<TimeZone> getAvailableTimeZones()
    {
        return availableTimeZones;
    }

    /**
     * Builds a list of available TimeZones. The list is sorted by raw offset
     * and Id.
     *
     * @return a sorted list of available TimeZones.
     */
    public static List<TimeZone> buildAvailableTimeZones()
    {
        List<TimeZone> timeZones = new ArrayList<TimeZone>();

        for (String timeZoneId : TimeZone.getAvailableIDs())
        {
            timeZones.add(TimeZone.getTimeZone(timeZoneId));
        }
        Collections.sort(timeZones, new TimeZoneComparator());

        return timeZones;
    }

    /**
     * Compares TimeZones by raw offset and ID.
     *
     */
    public static class TimeZoneComparator implements Comparator<TimeZone>
    {
        public int compare(TimeZone o1, TimeZone o2)
        {
            int rc = o1.getRawOffset() - o2.getRawOffset();

            if (rc == 0)
            {
                rc = o1.getID().compareToIgnoreCase(o2.getID());
            }

            return rc;
        }
    }

    /**
     * A convenience method for getting an array of month names for a locale.
     * Note: The returned array is 13 items, January is item [0], December is item [11]. Item [12]
     * (the 13th month) always appears to be empty, this appears to be reserved for other time
     * systems.
     *
     * @param locale
     * @return an array of month names in the given locale.
     */
    public static String[] getMonthNames(Locale locale)
    {
        return DateFormatSymbols.getInstance(locale).getMonths();
    }
    
    private I18NUtils()
    {
    }
    
    /*package*/ static void coverage()
    {
        new I18NUtils();
    }
}
