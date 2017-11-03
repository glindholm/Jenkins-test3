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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;
import net.sourceforge.wsup.core.stat.BasicStatistic.Values;

import org.apache.commons.lang.time.DateUtils;

/**
 * A time series of basic statistics (count, mean, low, high) via {@link BasicStatistic}.<br>
 * <br>
 * Each statistic has a fixed period in minutes and the series has a fixed length. The period and
 * length are established when the series is created.<br>
 * <br>
 * To add an observation value call {@link BasicStatisticSeries#increment(double)} and
 * the statistic for the current period will be
 * incremented to reflect the addition of the value. The period statistic that is incremented is
 * determined based on the time increment() is called.<br>
 * A sorted series of {@link BasicStatistic}'s are kept, one for each period in the series. As time
 * passes and periods change new period statistics are added to the series and old period statistics
 * are removed to ensure the series does not grow longer then the designated length.<br>
 * <br>
 * The individual observation values are not keep, only the cumulative statistic of seeing that
 * value at that time is maintained.
 * The memory used by a series is constrained by the length of the series as each period statistic
 * is essentially a fixed size.<br>
 * <br>
 * Call {@link BasicStatisticSeries#getResults()} to retrieve the results.<br>
 * <br>
 *
 * This class is thread safe and non-blocking.<br>
 * <br>
 * Requires Java 6 java.util.concurrent package.
 */
@ThreadSafe
public class BasicStatisticSeries
{
    /**
     * @param period The period of each statistic in minutes (minimum value is 1)
     * @param length The length of the series in <code>period</code> units (minimum value is 2)
     */
    public BasicStatisticSeries(final int period, final int length)
    {
        this(period, length, new Date());
    }

    // Exposed for Testing
    BasicStatisticSeries(final int period, final int length, final Date now)
    {
        if (period < 1)
        {
            throw new IllegalArgumentException("period must be greater then 0");
        }
        if (length < 2)
        {
            throw new IllegalArgumentException("length must be greater then 1");
        }

        this.period = period;
        this.periodMills = period * DateUtils.MILLIS_PER_MINUTE;
        this.length = length;

        activeNode = createNode(quantizeTime(now));
        series.put(Long.valueOf(activeNode.qtime), activeNode);
    }

    private final int                                period;
    /** The period of each statistic in milliseconds */
    private final long                               periodMills;
    /** The length of the series in period units */
    private final int                                length;

    /**
     * Series is an sorted map with the key being the quantized time for the period.
     */
    private final ConcurrentNavigableMap<Long, Node> series = new ConcurrentSkipListMap<Long, Node>();

    /** a reference to the most recent node in the series */
    private volatile Node                            activeNode;

    /**
     * @return the period of each statistic in minutes
     */
    public int getPeriod()
    {
        return period;
    }

    /**
     * @return the maximum length of the series (as set by the constructor)
     */
    public int getLength()
    {
        return length;
    }

    /**
     * Increment the statistic with a new observation value. This will find the
     * {@link BasicStatistic} for the current period and call
     * {@link BasicStatistic#increment(double)} with <code>value</code>.
     *
     * @param value the new value
     */
    public void increment(double value)
    {
        _increment(new Date(), value);
    }

    // Exposed for testing
    void _increment(Date now, double value)
    {
        BasicStatistic stat = getStatisticForTime(quantizeTime(now));
        if (stat != null)
        {
            stat.increment(value);
        }
    }

    /**
     * Gets the array of {@link Result}'s. <br>
     * The array will contain at most {@link #getLength()} elements.
     * The elements will be in time ascending order with the last element being for the current
     * period.
     *
     * @return the results.
     */
    public Result[] getResults()
    {
        return _getResults(new Date());
    }

    // Exposed for testing
    Result[] _getResults(Date now)
    {
        /*
         * Ensure the series contains the statistic for the current (now) period.
         * If there has not been recent calls to increment() then the series may have old data and
         * may be missing the current period.
         */
        getStatisticForTime(quantizeTime(now));
        return _getResults();
    }

    // Exposed for testing
    Result[] _getResults()
    {

        /*
         * Copy the series into a List.
         * The concurrent nature of series ensures that the series iterator will return a consistent
         * snapshot of the series.
         */
        List<Node> list = new ArrayList<Node>(series.values());

        /*
         * if list is too long remove oldest elements
         */
        while (list.size() > length)
        {
            list.remove(0);
        }

        /*
         * Convert Nodes into Results
         */
        Result[] results = new Result[list.size()];
        int i = 0;
        for (Node node : list)
        {
            results[i++] = new Result(new Date(node.qtime), node.stat.getValues(), period);
        }

        return results;
    }

    /**
     * Gets the statistic for the supplied quantized time.<br>
     * This will add new statistic and remove old statistic from the series if needed.
     *
     * @param qtime the quantized time.
     * @return the statistic for the supplied quantized time (or null if qtime is too old and no
     *         longer in the series)
     */
    private BasicStatistic getStatisticForTime(final long qtime)
    {
        while (true)
        {
            /*
             * In most cases the active node is the one we want.
             * Get a final reference to the current active node that we can use for this iteration
             * of the loop.
             * Note: activeNode is volatile and may change at any time.
             */
            final Node currentNode = activeNode;
            if (currentNode.qtime == qtime)
            {
                return currentNode.stat;
            }

            /*
             * If this thread was delayed then the active node will be for a
             * later time period and will need to get the correct node from
             * the series.
             */
            if (currentNode.qtime > qtime)
            {
                /*
                 * Get the correct node from the series
                 */
                Node seriesNode = series.get(Long.valueOf(qtime));
                if (seriesNode != null)
                {
                    return seriesNode.stat;
                }
                else
                {
                    /*
                     * The stat was not in the series, it must have fell off the end
                     */
                    return null;
                }
            }

            /*
             * The current node is old.
             *
             * Create the node for the next period and add it to the series.
             * If successful replace the active node with the next node.
             */
            final Node nextNode = createNode(nextPeriodQtime(currentNode.qtime));
            if (series.putIfAbsent(Long.valueOf(nextNode.qtime), nextNode) == null)
            {
                /*
                 * Only the thread that successfully updates the series gets to replace the active
                 * node with the next node.
                 */
                activeNode = nextNode;

                /*
                 * We've added a node to the series.
                 * The series may now contain length+1 entries.
                 * Remove oldest nodes from series if needed
                 */
                final Long oldestPeriodTime = oldestPeriodQtime(nextNode.qtime);
                while (true)
                {
                    final Entry<Long, Node> entry = series.firstEntry();
                    if (entry.getValue().qtime < oldestPeriodTime)
                    {
                        series.remove(entry.getKey());
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }
    }

    private Node createNode(long qtime)
    {
        return new Node(qtime, new BasicStatistic());
    }

    private long nextPeriodQtime(long qtime)
    {
        return qtime + periodMills;
    }

    /**
     * Calculate the qtime of oldest period in the series based on the current period's
     * <code>qtime</code> and the <code>length</code> of the series.
     *
     * @param qtime the current period's qtime
     * @return the qtime of the oldest period in the series
     */
    private long oldestPeriodQtime(long qtime)
    {
        return qtime - ((length - 1) * periodMills);
    }

    private long quantizeTime(Date now)
    {
        return quantizeTime(now, periodMills);
    }

    /**
     * @param now the date to quantize
     * @param periodMills the period in minutes
     * @return the quantized time
     */
    protected static long quantizeTime(Date now, long periodMills)
    {
        long time = now.getTime();
        return time - (time % periodMills);
    }

    @Immutable
    private static class Node
    {
        Node(long qtime, BasicStatistic stat)
        {
            this.qtime = qtime;
            this.stat = stat;
        }
        final long           qtime;
        final BasicStatistic stat;
    }

    /**
     * Result holds the {@link BasicStatistic.Values} plus the quantized time of the period.<br>
     * <br>
     * All of the fields are immutable and are exposed as <code>public final</code> and via bean
     * getters for convenience.
     */
    @Immutable
    public static class Result
    {
        public Result(final Date qtime, final BasicStatistic.Values values, long periodMinutes)
        {
            this.qtime = qtime;
            this.values = values;
            this.countPerSecond = (double) values.count / (double) (periodMinutes * 60);
        }

        /** the quantized date for the period of this statistic */
        public final Date                  qtime;

        /** the values for the period of <code>qtime</code> */
        public final BasicStatistic.Values values;

        /** the count per second */
        public final double                countPerSecond;

        /**
         * @return the quantized date for the period of this statistic
         */
        public Date getQtime()
        {
            return qtime;
        }

        /**
         * @return the values for the period of <code>qtime</code>
         */
        public BasicStatistic.Values getValues()
        {
            return values;
        }

        /**
         * Get the count per second. This value is calculated by dividing the
         * {@link Values#getCount()} by the period in seconds.<br>
         * <br>
         * Note: if the period is not finished then this value will be inaccurate.
         *
         * @return the count per second.
         */
        public double getCountPerSecond()
        {
            return countPerSecond;
        }

    }
}
