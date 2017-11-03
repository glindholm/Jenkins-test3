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

import junit.framework.TestCase;

public class EnsureTest extends TestCase
{

    public EnsureTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public final void testIsTrue()
    {
        Ensure.isTrue("true", true);
        try
        {
            Ensure.isTrue("false", false);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("false", e.getMessage());
        }
    }

    public final void testIsFalse()
    {
        Ensure.isFalse("false", false);
        try
        {
            Ensure.isFalse("true", true);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("true", e.getMessage());
        }
    }

    public final void testIstNotNull()
    {
        Ensure.isNotNull("notNull", "string");
        try
        {
            Ensure.isNotNull("null", null);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("null", e.getMessage());
        }
    }

    public final void testIsNull()
    {
        Ensure.isNull("isNull", null);
        try
        {
            Ensure.isNull("isNull", "string");
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("isNull", e.getMessage());
        }
    }

    public final void testIsNotBlank()
    {
        Ensure.isNotBlank("notBlank", "string");
        try
        {
            Ensure.isNotBlank("null", null);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("null", e.getMessage());
        }

        try
        {
            Ensure.isNotBlank("empty", "");
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("empty", e.getMessage());
        }

        try
        {
            Ensure.isNotBlank("blank", " ");
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("blank", e.getMessage());
        }
    }

    public final void testIsPositive()
    {
        Ensure.isPositive("0", 0);
        Ensure.isPositive("1", 1);
        try
        {
            Ensure.isPositive("-1", -1);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("-1", e.getMessage());
        }
    }

    public final void testIsGreaterThenZero()
    {
        Ensure.isGreaterThenZero("1", 1);
        try
        {
            Ensure.isGreaterThenZero("0", 0);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("0", e.getMessage());
        }
    }

    public final void testIsInRange()
    {
        Ensure.isInRange("range", 1, 1, 10);
        Ensure.isInRange("range", 5, 1, 10);
        Ensure.isInRange("range", 10, 1, 10);

        try
        {
            Ensure.isInRange("range", 0, 1, 10);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("range", e.getMessage());
        }

        try
        {
            Ensure.isInRange("range", 11, 1, 10);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("range", e.getMessage());
        }
    }

    public final void testIsEquals()
    {
        Ensure.isEquals("one=one", "one", "one");
        Ensure.isEquals("1 == 1", new Long(1), new Long(1));
        Ensure.isEquals("null=null", null, null);

        try
        {
            Ensure.isEquals("one != null", "one", null);
            fail();
        }
        catch (IllegalArgumentException e)
        {
        }

        try
        {
            Ensure.isEquals("null != one", null, "one");
            fail();
        }
        catch (IllegalArgumentException e)
        {
        }

        try
        {
            Ensure.isEquals("one != 1", "one", new Long(1));
            fail();
        }
        catch (IllegalArgumentException e)
        {
        }

    }

    public final void testCoverage()
    {
        Ensure.coverage();
    }
}
