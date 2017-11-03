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

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

public class PropertiesUtilsTest
{

    @Test
    public void testGetIntegerProperty()
    {
        Properties props = new Properties();

        assertEquals(Integer.valueOf(1),
                     PropertiesUtils.getIntegerProperty(props, "key", Integer.valueOf(1)));

        props.put("key", " ");
        assertEquals(Integer.valueOf(1),
                     PropertiesUtils.getIntegerProperty(props, "key", Integer.valueOf(1)));

        props.put("key", "20");
        assertEquals(Integer.valueOf(20),
                     PropertiesUtils.getIntegerProperty(props, "key", Integer.valueOf(1)));

        props.put("key", " 20 ");
        assertEquals(Integer.valueOf(20),
                     PropertiesUtils.getIntegerProperty(props, "key", Integer.valueOf(1)));

        try
        {
            props.put("key", "x");
            PropertiesUtils.getIntegerProperty(props, "key", Integer.valueOf(1));
            fail();
        }
        catch (InvalidPropertyException success)
        {
            // success
        }

        try
        {
            props.put("key", "20x");
            PropertiesUtils.getIntegerProperty(props, "key", Integer.valueOf(1));
            fail();
        }
        catch (InvalidPropertyException success)
        {
            // success
        }

        try
        {
            props.put("key", "20   x");
            PropertiesUtils.getIntegerProperty(props, "key", Integer.valueOf(1));
            fail();
        }
        catch (InvalidPropertyException success)
        {
            // success
        }

    }

    @Test
    public void testGetIntProperty()
    {
        Properties props = new Properties();

        assertEquals(1, PropertiesUtils.getIntProperty(props, "key", 1));

        props.put("key", " ");
        assertEquals(1, PropertiesUtils.getIntProperty(props, "key", 1));

        props.put("key", "20");
        assertEquals(20, PropertiesUtils.getIntProperty(props, "key", 1));

        props.put("key", " 20 ");
        assertEquals(20, PropertiesUtils.getIntProperty(props, "key", 1));

        try
        {
            props.put("key", "x");
            PropertiesUtils.getIntProperty(props, "key", 1);
            fail();
        }
        catch (InvalidPropertyException success)
        {
            // success
        }

    }

    @Test
    public void testGetLongProperty()
    {
        Properties props = new Properties();

        assertEquals(Long.valueOf(1),
                     PropertiesUtils.getLongProperty(props, "key", Long.valueOf(1)));

        props.put("key", " ");
        assertEquals(Long.valueOf(1),
                     PropertiesUtils.getLongProperty(props, "key", Long.valueOf(1)));

        props.put("key", "20");
        assertEquals(Long.valueOf(20),
                     PropertiesUtils.getLongProperty(props, "key", Long.valueOf(1)));

        props.put("key", " 20 ");
        assertEquals(Long.valueOf(20),
                     PropertiesUtils.getLongProperty(props, "key", Long.valueOf(1)));

        try
        {
            props.put("key", "x");
            PropertiesUtils.getLongProperty(props, "key", Long.valueOf(1));
            fail();
        }
        catch (InvalidPropertyException success)
        {
            // success
        }

        try
        {
            props.put("key", "20x");
            PropertiesUtils.getLongProperty(props, "key", Long.valueOf(1));
            fail();
        }
        catch (InvalidPropertyException success)
        {
            // success
        }

    }

    @Test
    public void testGetBooleanProperty()
    {
        Properties props = new Properties();

        assertEquals(null, PropertiesUtils.getBooleanProperty(props, "key", null));

        props.put("key", " ");
        assertEquals(null, PropertiesUtils.getBooleanProperty(props, "key", null));

        props.put("key", "true");
        assertEquals(Boolean.TRUE, PropertiesUtils.getBooleanProperty(props, "key", null));

        props.put("key", "FALSE");
        assertEquals(Boolean.FALSE, PropertiesUtils.getBooleanProperty(props, "key", null));

        try
        {
            props.put("key", "x");
            PropertiesUtils.getBooleanProperty(props, "key", null);
            fail();
        }
        catch (InvalidPropertyException success)
        {
            // success
        }

    }

}
