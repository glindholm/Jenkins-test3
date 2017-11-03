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

import java.util.concurrent.atomic.AtomicReference;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * Compute basic statistics (low, high, and mean) on a series of values.<br>
 * <br>
 * When {@link #increment(double)} is called to add a value the mean is computed using this
 * algorithm:
 * <ol>
 * <li>initial <code>mean</code> = the first value</li>
 * <li>For each additional value<br>
 * <code>mean = mean + (new value - mean) / (number of observations)</code></li>
 * </ol>
 * This algorithm for calculating mean was borrowed from
 * <a href=
 * "http://commons.apache.org/math/apidocs/org/apache/commons/math3/stat/descriptive/moment/Mean.html"
 * >org.apache.commons.math3.stat.descriptive.moment.Mean</a>. <br>
 * <br>
 * If no values have been added with {@link #increment(double)} then count, mean, low, and high will
 * all have values of 0. <br>
 * <br>
 * The individual observation values are not kept, only the cumulative statistics of seeing the
 * value are maintained. As a result these objects are fixed-size and do not grow as observations
 * are added.<br>
 * <br>
 * This class is thread safe and uses a non-blocking (non-locking) algorithm based on
 * <code>AtomicReference</code> to update the current state.<br>
 * <br>
 * Requires Java 6 java.util.concurrent package.
 *
 */
@ThreadSafe
public class BasicStatistic
{
    /**
     * Values holds the {@link BasicStatistic} state values (<code>count, mean, low, high</code>).<br>
     * <br>
     * All of the fields are immutable and are exposed as <code>public final</code> and via bean
     * getters for convenience.
     */
    @Immutable
    public static class Values
    {
        /** the number of observations */
        public final long   count;
        /** the mean value of the observations (or 0 if <code>count</code> is 0) */
        public final double mean;
        /** the lowest value of the observations (or 0 if <code>count</code> is 0) */
        public final double low;
        /** the highest value of the observations (or 0 if <code>count</code> is 0) */
        public final double high;

        public Values(long count, double mean, double low, double high)
        {
            this.count = count;
            this.mean = mean;
            this.low = low;
            this.high = high;
        }

        /**
         * @return the number of observations
         */
        public long getCount()
        {
            return count;
        }

        /**
         * @return the mean value of the observations (or 0 if <code>count</code> is 0)
         */
        public double getMean()
        {
            return mean;
        }

        /**
         * @return the lowest value of the observations (or 0 if <code>count</code> is 0)
         */
        public double getLow()
        {
            return low;
        }

        /**
         * @return the highest value of the observations (or 0 if <code>count</code> is 0)
         */
        public double getHigh()
        {
            return high;
        }

    }

    public BasicStatistic()
    {
        values = new AtomicReference<BasicStatistic.Values>(new Values(0, 0, 0, 0));
    }

    private AtomicReference<Values> values;

    /**
     * Update the internal state to include a new observation of <code>value</code>.
     *
     * @param value the new value
     */
    public void increment(double value)
    {
        while (true)
        {
            Values old = values.get();

            long count = old.count + 1;
            double mean = old.mean + ((value - old.mean) / count);
            double low = old.low;
            double high = old.high;

            if (old.count == 0)
            {
                low = value;
                high = value;
            }
            else if (value < low)
            {
                low = value;
            }
            else if (value > high)
            {
                high = value;
            }

            /*
             * Atomically update the internal state with the new values.
             * If the update fails then loop and try again.
             */
            if (values.compareAndSet(old, new Values(count, mean, low, high)))
            {
                return;
            }
        }
    }

    /**
     * @return the number of values added by {@link #increment(double)}
     */
    public long getCount()
    {
        return values.get().count;
    }

    /**
     * @return the lowest value of the observations (or 0 if count is 0)
     */
    public double getLow()
    {
        return values.get().low;
    }

    /**
     * @return the highest value of the observations (or 0 if count is 0)
     */
    public double getHigh()
    {
        return values.get().high;
    }

    /**
     * @return the mean value of the observations (or 0 if count is 0)
     */
    public double getMean()
    {
        return values.get().mean;
    }

    /**
     * @return the current state (count, low, high, mean)
     */
    public Values getValues()
    {
        return values.get();
    }
}
