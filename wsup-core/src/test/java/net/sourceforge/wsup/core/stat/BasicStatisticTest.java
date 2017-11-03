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

import java.text.DecimalFormat;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

public class BasicStatisticTest
{

    @Test
    public void testBasic()
    {
        BasicStatistic s = new BasicStatistic();

        assertEquals(0, s.getCount());

        /*
         * Double has exact representations for 0 so we can cast to long
         */
        assertEquals(0, (long) s.getLow());
        assertEquals(0, (long) s.getHigh());
        assertEquals(0, (long) s.getMean());

        s.increment(100d);
        assertEquals(1, s.getCount());
        assertTrue(100d == s.getLow());
        assertTrue(100d == s.getHigh());
        assertTrue(100d == s.getMean());

        s.increment(50d);
        assertEquals(2, s.getCount());
        assertTrue(50d == s.getLow());
        assertTrue(100d == s.getHigh());
        assertTrue(75d == s.getMean());

        s.increment(150d);
        BasicStatistic.Values v = s.getValues();
        assertEquals(3, v.count);
        assertTrue(50d == v.low);
        assertTrue(150d == v.high);
        assertTrue(100d == v.mean);
    }

    @Test
    public void testMultiThread() throws Exception
    {
        final BasicStatistic s = new BasicStatistic();
        final int threads = 10;
        final int increments = 10000;

        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++)
        {
            final double value = i;

            Thread t = new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        startGate.await();

                        for (int cnt = 0; cnt < increments; cnt++)
                        {
                            s.increment(value);
                        }
                        endGate.countDown();
                    }
                    catch (InterruptedException e)
                    {
                        fail();
                    }
                }
            };
            t.start();
        }

        startGate.countDown();
        endGate.await();

        BasicStatistic.Values v = s.getValues();
        assertEquals(threads * increments, v.count);
        assertTrue(0d == v.low);
        assertTrue(threads - 1 == v.high);

        DecimalFormat df = new DecimalFormat("#.##");

        assertEquals(df.format((double) (threads - 1) / 2d), df.format(v.mean));
    }
}
