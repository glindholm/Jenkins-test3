/*
 * Copyright (c) 2010 Greg Lindholm
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

package net.sourceforge.wsup.core;

import org.apache.commons.lang.StringUtils;

/**
 * Ensure is like to Assert except it throws a RuntimeException (IllegalArgumentException) when the condition is not met. Use it
 * to perform validation of user data in setters and validation routines.
 *
 */
public final class Ensure
{

    /**
     * Ensures <code>value</code> is <code>true</code>.
     *
     * @param msg The Exception message.
     * @param value The value to test.
     * @throws IllegalArgumentException if <code>value</code> is not <code>true</code>.
     */
    public static void isTrue(String msg, boolean value)
    {
        if (!value)
        {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Ensures <code>value</code> is <code>false</code>.
     *
     * @param msg The Exception message.
     * @param value The value to test.
     * @throws IllegalArgumentException if <code>value</code> is not <code>false</code>.
     */
    public static void isFalse(String msg, boolean value)
    {
        if (value)
        {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Ensures obj1 and obj2 are equal.<br>
     * Specifically obj1 and obj2 are considered equal if:<br>
     * <ul>
     * <li> they are the same object (obj1==obj2) or </li>
     * <li> they are both null</li>
     * <li> obj1.equals(obj2) is true</li>
     * </ul>
     * @param msg The Exception message.
     * @param obj1 object 1.
     * @param obj2 object 2.
     * @throws IllegalArgumentException if obj1 and obj2 are not equal.
     */
    public static void isEquals(String msg, Object obj1, Object obj2)
    {
        if (obj1 != obj2)
        {
            if (obj1 == null || obj2 == null || !obj1.equals(obj2))
            {
                throw new IllegalArgumentException(msg);
            }
        }
    }

    /**
     * Ensures <code>obj</code> is not blank (empty, whitespace, or <code>null</code>).
     *
     * @param msg The Exception message.
     * @param obj The <code>String</code> to test.
     * @throws IllegalArgumentException if <code>obj</code> is blank.
     */
    public static void isNotBlank(String msg, String obj)
    {
        if (StringUtils.isBlank(obj))
        {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Ensures <code>num</code> is positive (greater then or equal to zero).
     *
     * @param msg The Exception message.
     * @param num The number to test.
     * @throws IllegalArgumentException if <code>num</code> is not positive.
     */
    public static void isPositive(String msg, long num)
    {
        if (num < 0)
        {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Ensures <code>num</code> is greater then zero.
     *
     * @param msg The Exception message.
     * @param num The number to test.
     * @throws IllegalArgumentException if <code>num</code> is not greater then zero.
     */
    public static void isGreaterThenZero(String msg, long num)
    {
        if (num <= 0)
        {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Ensures <code>num</code> is in the range of <code>lowValue</code> to <code>highValue</code>.
     * Specifically <code>num</code> must be greater then or equal to <code>lowValue</code> and
     * less then or equal to <code>highValue</code>.
     *
     * @param msg The Exception message.
     * @param num The number to test.
     * @param lowValue The low end of the range
     * @param highValue The high end of the range.
     * @throws IllegalArgumentException if <code>num</code> is not in the range.
     */
    public static void isInRange(String msg,
                                 long num,
                                 long lowValue,
                                 long highValue)
    {
        if (num < lowValue || num > highValue)
        {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Ensures <code>obj</code> is not <code>null</code>.
     *
     * @param msg The Exception message.
     * @param obj The object to test.
     * @throws IllegalArgumentException if <code>obj</code> is <code>null</code>.
     */
    public static void isNotNull(String msg, Object obj)
    {
        if (obj == null)
        {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Ensures <code>obj</code> is <code>null</code>.
     *
     * @param msg The Exception message.
     * @param obj The object to test.
     * @throws IllegalArgumentException if <code>obj</code> is not <code>null</code>.
     */
    public static void isNull(String msg, Object obj)
    {
        if (obj != null)
        {
            throw new IllegalArgumentException(msg);
        }
    }

    private Ensure()
    {
    }

    static void coverage()
    {
        new Ensure();
    }

}
