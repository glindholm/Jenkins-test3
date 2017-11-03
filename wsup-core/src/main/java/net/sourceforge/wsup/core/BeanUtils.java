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

import java.util.Date;

/**
 * Various utilities for dealing with beans.
 *
 * @author Kevin Hunter
 *
 */
public class BeanUtils
{
    /**
     * Safely compares two possibly-null <code>Object</code> references for
     * equality.
     *
     * @param one
     *            First <code>Object</code>
     * @param two
     *            Second <code>Object</code>
     * @return <code>true</code> if both are <code>null</code>, the two both
     *         refer to the same <code>Object</code> or if the two <code>Object</code>s are
     *         <code>equal</code>. <code>false</code> if one is <code>null</code> but the other is
     *         not, or if <code>equals</code> returns <code>false</code>.
     */
    public static boolean safeEquals(Object one, Object two)
    {
        /*
         * Are one and two either both null or same object?
         */
        if (one == two)
        {
            return true;
        }

        /*
         * Both can't be null, so if either is null, it's not
         * the same as the other.
         */
        if (one == null)
        {
            return false;
        }

        if (two == null)
        {
            return false;
        }

        /*
         * Both are non-null, so fall back to equals()
         */
        return one.equals(two);
    }

    /**
     * Safely compares two possibly-null <code>String</code> references for
     * equality in a case-insensitive manner.
     *
     * @param one
     *            First <code>String</code>
     * @param two
     *            Second <code>String</code>
     * @return <code>true</code> if both are <code>null</code>, the two both
     *         refer to the same <code>String</code> or if the two <code>String</code>s are
     *         <code>equalsIgnoreCase</code>. <code>false</code> if one is <code>null</code> but the
     *         other is not, or if <code>equalsIgnoreCase</code> returns <code>false</code>.
     */
    public static boolean safeEqualsIgnoreCase(String one, String two)
    {
        /*
         * Are one and two either both null or same object?
         */
        if (one == two)
        {
            return true;
        }

        /*
         * Both can't be null, so if either is null, it's not
         * the same as the other.
         */
        if (one == null)
        {
            return false;
        }

        if (two == null)
        {
            return false;
        }

        /*
         * Both are non-null, so fall back to equalsIgnoreCase()
         */
        return one.equalsIgnoreCase(two);
    }

    /**
     * Safely returns an <code>Object</code>s <code>hashCode</code>, returning 0
     * if the <code>Object</code> reference is <code>null</code>.
     *
     * @param obj
     *            <code>Object</code>
     * @return <code>Object.hashCode</code>, or 0 if the object reference is <code>null</code>.
     */
    public static int safeHashCode(Object obj)
    {
        if (obj == null)
        {
            return 0;
        }

        return obj.hashCode();
    }

    /**
     * Null safe trim and truncate. Trim to null string <code>s</code> and if the result
     * is longer then <code>maxSize</code> then truncate it and trim again. (Truncating
     * could leave a dangling space.)
     *
     * @param s the string to trim and truncate
     * @param maxSize the maximum size of the returned string
     * @return the trimmed and truncated string or <code>null</code> if string <code>s</code> was
     *         empty or
     *         blank.
     */
    public static String trimAndTruncate(String s, int maxSize)
    {
        if (s == null)
        {
            return null;
        }

        String trimmed = s.trim();

        if (trimmed.length() == 0)
        {
            return null;
        }

        if (trimmed.length() > maxSize)
        {
            return trimmed.substring(0, maxSize).trim();
        }
        else
        {
            return trimmed;
        }
    }

    /**
     * Returns a <code>null</code> safe copy of <code>date</code>.
     *
     * @param date the Date to copy (may be <code>null</code>)
     * @return a copy of <code>date</code> or <code>null</code> if <code>date</code> is
     *         <code>null</code>.
     */
    public static Date safeCopyDate(Date date)
    {
        if (date == null)
        {
            return null;
        }
        else
        {
            return new Date(date.getTime());
        }
    }

    /* package */static void coverage()
    {
        new BeanUtils();
    }

    private BeanUtils()
    {
    }
}
