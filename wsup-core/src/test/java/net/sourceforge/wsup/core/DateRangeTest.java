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

import java.util.Date;

import org.junit.Test;

public class DateRangeTest
{

    @Test
    public void testBasic()
    {
        assertEquals(new Date(1000), new DateRange(new Date(1000), new Date(1001)).getStart());
        assertEquals(new Date(1000), new DateRange(new Date(1000), new Date(999)).getEnd());
        assertEquals(new Date(1000), new DateRange(new Date(1000), new Date(1000)).getEnd());
    }
}