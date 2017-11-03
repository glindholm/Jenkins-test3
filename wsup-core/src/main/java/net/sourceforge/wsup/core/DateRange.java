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

import java.util.Date;

import net.jcip.annotations.Immutable;

/**
 * Represents and Date range by the start and end dates in the range.
 *
 * @author Greg Lindholm
 *
 */
@Immutable
public final class DateRange
{
    /**
     * Construct a DateRange from two dates. The earlier date will be set as 'start' the later will
     * be set as 'end'.
     *
     * @param date1 First date
     * @param date2 Second date
     */
    public DateRange(Date date1, Date date2)
    {
        if (date1.before(date2))
        {
            this.start = date1.getTime();
            this.end = date2.getTime();

        }
        else
        {
            this.start = date2.getTime();
            this.end = date1.getTime();
        }
    }

    private final long start;
    private final long end;

    /**
     * @return the earlier date in the range
     */
    public Date getStart()
    {
        return new Date(start);
    }

    /**
     * @return the later date in the range
     */
    public Date getEnd()
    {
        return new Date(end);
    }

}
