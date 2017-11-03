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

package net.sourceforge.wsup.core.stat;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

public class BasicStatisticSeriesTest
{

    @Test
    public void testQuantizeTime()
    {
        Date now = new Date();
        assertEquals(createQtime(now, 1),
                     BasicStatisticSeries.quantizeTime(now, 1 * DateUtils.MILLIS_PER_MINUTE));
        assertEquals(createQtime(now, 5),
                     BasicStatisticSeries.quantizeTime(now, 5 * DateUtils.MILLIS_PER_MINUTE));
        assertEquals(createQtime(now, 15),
                     BasicStatisticSeries.quantizeTime(now, 15 * DateUtils.MILLIS_PER_MINUTE));
    }

    /*
     * This is an alternate (more expensive?) way of quantizing the time but should achieve the same
     * value.
     */
    private long createQtime(Date now, int period)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(now);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        int minute = calendar.get(Calendar.MINUTE);
        minute -= minute % period;
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime().getTime();
    }

    @Test
    public void testEmpty()
    {
        BasicStatisticSeries.Result[] results;

        // 24 * 5 minute periods
        BasicStatisticSeries series = new BasicStatisticSeries(5, 24);

        /*
         * There is a very small change that the construct time and the
         * getResults() time will be in different periods.
         *
         * So it is possible this could fail.
         */

        // Empty results
        results = series.getResults();
        assertEquals(1, results.length); // This could fail if getResuls() is in a different period then construction
        assertEquals(0, results[0].values.count);
        assertEquals(0, (long) results[0].values.mean);
        assertEquals(0, (long) results[0].values.low);
        assertEquals(0, (long) results[0].values.high);
    }

    @Test
    public void testBasic()
    {
        BasicStatisticSeries.Result[] results;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.US);
        cal.set(2012, 4, 10, 9, 0, 0);
        Date base = cal.getTime();

        // 1 minute period, 5 buckets
        BasicStatisticSeries series = new BasicStatisticSeries(1, 5, base);

        assertEquals(1, series.getPeriod());
        assertEquals(5, series.getLength());

        // Empty results
        results = series._getResults(base);
        assertEquals(1, results.length);
        assertEquals(createQtime(base, 1), results[0].qtime.getTime());
        assertEquals(0, results[0].values.count);
        assertEquals(0, (long) results[0].values.mean);
        assertEquals(0, (long) results[0].values.low);
        assertEquals(0, (long) results[0].values.high);
        assertEquals(0, (long) results[0].countPerSecond);

        // 3 empty buckets
        results = series._getResults(plusMinutes(base, 2));
        assertEquals(3, results.length);
        assertEquals(createQtime(base, 1), results[0].qtime.getTime());
        assertEquals(createQtime(plusMinutes(base, 1), 1), results[1].qtime.getTime());
        assertEquals(createQtime(plusMinutes(base, 2), 1), results[2].qtime.getTime());
        assertEquals(0, results[0].values.count);
        assertEquals(0, results[1].values.count);
        assertEquals(0, results[2].values.count);

        // increment each bucket
        series._increment(base, 10d);
        series._increment(plusMinutes(base, 1), 20d);
        series._increment(plusMinutes(base, 2), 30d);

        results = series._getResults();
        assertEquals(3, results.length);
        assertEquals(createQtime(base, 1), results[0].qtime.getTime());
        assertEquals(createQtime(plusMinutes(base, 1), 1), results[1].qtime.getTime());
        assertEquals(createQtime(plusMinutes(base, 2), 1), results[2].qtime.getTime());
        assertEquals(1, results[0].values.count);
        assertEquals(1, results[1].values.count);
        assertEquals(1, results[2].values.count);
        assertEquals(10, (long) results[0].values.mean);
        assertEquals(20, (long) results[1].values.mean);
        assertEquals(30, (long) results[2].values.mean);

        assertEquals(10,  (long)Math.floor(results[0].getCountPerSecond() * 600d));

        // Scroll off old buckets
        series._increment(plusMinutes(base, 5), 60d);
        results = series._getResults();
        assertEquals(5, results.length);
        assertEquals(createQtime(plusMinutes(base, 1), 1), results[0].qtime.getTime());
        assertEquals(createQtime(plusMinutes(base, 2), 1), results[1].qtime.getTime());
        assertEquals(createQtime(plusMinutes(base, 3), 1), results[2].qtime.getTime());
        assertEquals(createQtime(plusMinutes(base, 4), 1), results[3].qtime.getTime());
        assertEquals(createQtime(plusMinutes(base, 5), 1), results[4].qtime.getTime());
        assertEquals(1, results[0].values.count);
        assertEquals(1, results[1].values.count);
        assertEquals(0, results[2].values.count);
        assertEquals(0, results[3].values.count);
        assertEquals(1, results[4].values.count);
        assertEquals(20, (long) results[0].values.mean);
        assertEquals(30, (long) results[1].values.mean);
        assertEquals(0, (long) results[2].values.mean);
        assertEquals(0, (long) results[3].values.mean);
        assertEquals(60, (long) results[4].values.mean);

    }

    @Test
    public void testOutOfSeries()
    {
        BasicStatisticSeries.Result[] results;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.US);
        cal.set(2012, 4, 10, 9, 0, 0);
        Date base = cal.getTime();

        // 1 minute period, 5 buckets
        BasicStatisticSeries series = new BasicStatisticSeries(1, 5, base);

        // increment before earliest period
        series._increment(plusMinutes(base, -1), 10d);

        // Empty results
        results = series._getResults(base);
        assertEquals(1, results.length);
        assertEquals(createQtime(base, 1), results[0].qtime.getTime());
        assertEquals(0, results[0].values.count);
    }

    @Test
    public void testExceptions()
    {
        try
        {
            new BasicStatisticSeries(0, 5);
            fail();
        }
        catch (IllegalArgumentException success)
        {

        }

        try
        {
            new BasicStatisticSeries(1, 1);
            fail();
        }
        catch (IllegalArgumentException success)
        {

        }

    }

    private Date plusMinutes(Date base, int minutes)
    {
        return new Date(base.getTime() + minutes * DateUtils.MILLIS_PER_MINUTE);
    }
}
