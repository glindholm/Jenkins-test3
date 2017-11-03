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

import java.util.List;

/**
 * Supports paging of results.
 *
 */
public interface PagingInfo
{
    /**
     * @return the current page number (1 based)
     */
    int getPage();

    /**
     * @return the maximum number of items per page
     */
    int getPageSize();

    /**
     * @return the number of the last page (1 based)
     */
    int getLastPage();

    /**
     * @return the total number of items available
     */
    int getTotalCount();

    /**
     * @return the position (1 based) of the first item on this page
     */
    int getFirstPos();

    /**
     * @return the position (1 based) of the last item on this page
     */
    int getLastPos();

    /**
     * Build a list of <code>num</code> page numbers centered on the current page. <br>
     * if page=10 and lastPage=20 then buildPageList(5) it will return list [8,9,10,11,12].<br>
     * if page=1 and lastPage=20 then buildPageList(5) it will return list [1,2,3,4,5].<br>
     *
     * @param num the maximum number of pages in the list
     * @return a list of page numbers
     */
    public List<Integer> buildPageList(final int num);
}
