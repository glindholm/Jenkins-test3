/*
 *  Copyright (c) 2012 Greg Lindholm
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

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class PropertiesUtils
{

    /**
     * Gets the property value for <code>key</code> as an <code>Integer</code>. If the property
     * value is missing or blank the <code>defaultValue</code> is returned.
     *
     * @param props the Properties
     * @param key the key
     * @param defaultValue the default value
     * @return the property value or <code>defaultValue</code> if missing or blank
     * @throws InvalidPropertyException if the value does not convert to an Integer
     */
    public static Integer getIntegerProperty(Properties props, String key, Integer defaultValue)
    {
        String value = props.getProperty(key);
        if (StringUtils.isBlank(value))
        {
            return defaultValue;
        }

        try
        {
            return Integer.valueOf(value.trim());
        }
        catch (NumberFormatException e)
        {
            throw new InvalidPropertyException(String.format("Property [%s] has invalid value [%s]. Not an Integer.",
                                                             key,
                                                             value));
        }
    }

    /**
     * Gets the property value for <code>key</code> as an <code>int</code>. If the property value is
     * missing or blank the <code>defaultValue</code> is returned.
     *
     * @param props the Properties
     * @param key the key
     * @param defaultValue the default value
     * @return the property value or <code>defaultValue</code> if missing or blank
     * @throws InvalidPropertyException if the value does not convert to an int
     */
    public static int getIntProperty(Properties props, String key, int defaultValue)
    {
        String value = props.getProperty(key);
        if (StringUtils.isBlank(value))
        {
            return defaultValue;
        }

        try
        {
            return Integer.valueOf(value.trim());
        }
        catch (NumberFormatException e)
        {
            throw new InvalidPropertyException(String.format("Property [%s] has invalid value [%s]. Not an int.",
                                                             key,
                                                             value));
        }
    }

    /**
     * Gets the property value for <code>key</code> as a <code>Long</code>. If the property
     * value is missing or blank the <code>defaultValue</code> is returned.
     *
     * @param props the Properties
     * @param key the key
     * @param defaultValue the default value
     * @return the property value or <code>defaultValue</code> if missing or blank
     * @throws InvalidPropertyException if the value does not convert to a Long
     */
    public static Long getLongProperty(Properties props, String key, Long defaultValue)
    {
        String value = props.getProperty(key);
        if (StringUtils.isBlank(value))
        {
            return defaultValue;
        }

        try
        {
            return Long.valueOf(value.trim());
        }
        catch (NumberFormatException e)
        {
            throw new InvalidPropertyException(String.format("Property [%s] has invalid value [%s]. Not a Long.",
                                                             key,
                                                             value));
        }
    }

    /**
     * Gets the property value for <code>key</code> as an <code>Boolean</code>. If the property
     * value is missing or blank the <code>defaultValue</code> is returned. Valid values are case
     * insensitive <code>"true"</code> or <code>"false"</code>.
     *
     * @param props the Properties
     * @param key the key
     * @param defaultValue the default value
     * @return the property value or <code>defaultValue</code> if missing or blank
     * @throws InvalidPropertyException if the value does not convert to a Boolean
     */
    public static Boolean getBooleanProperty(Properties props, String key, Boolean defaultValue)
    {
        String value = props.getProperty(key);
        if (StringUtils.isBlank(value))
        {
            return defaultValue;
        }

        value = value.trim();

        if (value.equalsIgnoreCase("true"))
        {
            return Boolean.TRUE;
        }
        else if (value.equalsIgnoreCase("false"))
        {
            return Boolean.FALSE;
        }
        else
        {
            throw new InvalidPropertyException(String.format("Property [%s] has invalid value [%s]. Not a Boolean.",
                                                             key,
                                                             value));
        }
    }
}
