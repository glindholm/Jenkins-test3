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

package net.sourceforge.wsup.struts2;

import java.util.ArrayList;
import java.util.List;

import net.jcip.annotations.Immutable;
import net.sourceforge.wsup.core.Assert;
import net.sourceforge.wsup.core.Ensure;

@Immutable
/**
 * Paging is an immutable object that encapsulates paging state and provides a method for generating
 * a list of page numbers centered on the current page.
 *
 * @author Greg Lindholm
 *
 */
public class Paging implements PagingInfo
{
    public Paging(final int totalCount, int page, final int pageSize)
    {
        Ensure.isPositive("totalCount", totalCount);
        Ensure.isGreaterThenZero("invalid pageSize", pageSize);

        if (totalCount > 0)
        {
            lastPage = (totalCount + pageSize - 1) / pageSize;

            if (page < 1)
            {
                page = 1;
            }
            if (page > lastPage)
            {
                page = lastPage;
            }

            firstPos = (page - 1) * pageSize + 1;
            if (page < lastPage)
            {
                lastPos = firstPos + pageSize - 1;
            }
            else
            {
                lastPos = totalCount;
            }
        }
        else
        {
            page = 1;
            lastPage = 0;
            firstPos = 0;
            lastPos = 0;
        }

        this.totalCount = totalCount;
        this.page = page;
        this.pageSize = pageSize;
    }

    private final int page;
    private final int pageSize;
    private final int totalCount;
    private final int firstPos;
    private final int lastPos;
    private final int lastPage;

    @Override
    public int getPage()
    {
        return page;
    }

    /**
     * @return the total number of items available
     */
    @Override
    public int getTotalCount()
    {
        return totalCount;
    }

    /**
     * @return the total number of pages available
     */
    @Override
    public int getLastPage()
    {
        return lastPage;
    }

    /**
     * @return the starting position (1 based)
     */
    @Override
    public int getFirstPos()
    {
        return firstPos;
    }

    /**
     * @return the ending position (1 based)
     */
    @Override
    public int getLastPos()
    {
        return lastPos;
    }

    @Override
    public int getPageSize()
    {
        return pageSize;
    }

    /**
     * Build a list of <code>num</code> page numbers centered on the current page. <br>
     * if page=10 and lastPage=20 then buildPageList(5) it will return list [8,9,10,11,12].<br>
     * if page=1 and lastPage=20 thenbuildPageList(5) it will return list [1,2,3,4,5].<br>
     *
     * @param num the maximum number of pages in the list
     * @return a list of page numbers
     */
    @Override
    public List<Integer> buildPageList(final int num)
    {
        return Paging.buildPageList(page, lastPage, num);
    }

    /**
     * Build a list of <code>num</code> page numbers centered on the current
     * page. <br>
     * buildPageList(10,20,5) it will return list [8,9,10,11,12].<br>
     * buildPageList(1,20,5) it will return list [1,2,3,4,5].<br>
     *
     * @param page the current page (1 based)
     * @param lastPage the last page (1 based)
     * @param num the maximum number of pages in the list
     * @return a list of page numbers
     */
    public static List<Integer> buildPageList(final int page, final int lastPage, final int num)
    {
        Ensure.isInRange("page must be in range 1 to last", page, 1, lastPage);
        Ensure.isGreaterThenZero("num must be greater then 1", num);

        List<Integer> pageList = new ArrayList<Integer>();

        // Calc start and end centered on current page
        int startPage = Math.max(1, page - (num - 1) / 2);
        int endPage = Math.min(page + (num) / 2, lastPage);

        // check if we have less then num pages (how much are we under)
        int under = num - (endPage - startPage + 1);
        if (under > 0)
        {
            /*
             * if under we hit an edge;  stretch the non-edge boundary
             */
            if (startPage == 1)
            {
                /*
                 * We are at the begin of the list so stretch the end
                 */
                endPage = Math.min(endPage + under, lastPage);
            }
            else
            {
                /*
                 * We are at the end so stretch the start
                 */
                Assert.equals(endPage, lastPage);
                startPage = Math.max(1, startPage - under);
            }
        }

        // build the page list
        for (int i = startPage; i <= endPage; i++)
        {
            pageList.add(new Integer(i));
        }
        return pageList;
    }

    @Override
    public String toString()
    {
        return String
            .format("Paging [firstPos=%s, lastPage=%s, lastPos=%s, page=%s, pageSize=%s, totalCount=%s]",
                    firstPos,
                    lastPage,
                    lastPos,
                    page,
                    pageSize,
                    totalCount);
    }

}
