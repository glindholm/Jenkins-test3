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

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PagingTest
{

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public final void testHalfPage()
    {
        Paging p = new Paging(10, 1, 20);

        assertEquals(1, p.getFirstPos());
        assertEquals(10, p.getLastPos());
        assertEquals(1, p.getPage());
        assertEquals(1, p.getLastPage());
        assertEquals(20, p.getPageSize());
        assertEquals(10, p.getTotalCount());
    }

    @Test
    public final void testEmpty()
    {
        Paging p = new Paging(0, 1, 20);

        assertEquals(0, p.getFirstPos());
        assertEquals(0, p.getLastPos());
        assertEquals(1, p.getPage());
        assertEquals(0, p.getLastPage());
        assertEquals(20, p.getPageSize());
        assertEquals(0, p.getTotalCount());
    }

    @Test
    public final void testExactlyFullPage()
    {
        Paging p = new Paging(10, 1, 10);

        assertEquals(1, p.getFirstPos());
        assertEquals(10, p.getLastPos());
        assertEquals(1, p.getPage());
        assertEquals(1, p.getLastPage());
        assertEquals(10, p.getPageSize());
        assertEquals(10, p.getTotalCount());

    }

    @Test
    public final void testExactlyFullSecondPage()
    {
        Paging p = new Paging(20, 2, 10);

        assertEquals(11, p.getFirstPos());
        assertEquals(20, p.getLastPos());
        assertEquals(2, p.getPage());
        assertEquals(2, p.getLastPage());
        assertEquals(10, p.getPageSize());
        assertEquals(20, p.getTotalCount());

    }

    @Test
    public final void testBadPage()
    {
        Paging p = new Paging(10, -1, 20);

        assertEquals(1, p.getFirstPos());
        assertEquals(10, p.getLastPos());
        assertEquals(1, p.getPage());
        assertEquals(1, p.getLastPage());
        assertEquals(20, p.getPageSize());
        assertEquals(10, p.getTotalCount());
    }

    @Test
    public final void testBadPage2()
    {
        Paging p = new Paging(25, 200, 10);

        assertEquals(21, p.getFirstPos());
        assertEquals(25, p.getLastPos());
        assertEquals(3, p.getPage());
        assertEquals(3, p.getLastPage());
        assertEquals(10, p.getPageSize());
        assertEquals(25, p.getTotalCount());
    }

    /**
     * Build a list of <code>num</code> page numbers centered on the current page. <br>
     * if page=10 and lastPage=20 then buildPageList(5) it will return list [8,9,10,11,12].<br>
     * if page=1 and lastPage=20 then buildPageList(5) it will return list [1,2,3,4,5].<br>
     */

    @Test
    public final void testPaging()
    {
        Paging p = new Paging(200, 10, 10);

        assertEquals(20, p.getLastPage());
        assertEquals(91, p.getFirstPos());
        assertEquals(100, p.getLastPos());

        assertEquals(Arrays.asList(new Integer[] { 8, 9, 10, 11, 12 }), p.buildPageList(5));
    }

    @Test
    public final void testPaging2()
    {
        Paging p = new Paging(200, 1, 10);

        assertEquals(20, p.getLastPage());
        assertEquals(1, p.getFirstPos());
        assertEquals(10, p.getLastPos());

        assertEquals(Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 }), p.buildPageList(5));
    }

    @Test
    public final void testPagingEdgeUnderEnd()
    {
        Paging p = new Paging(95, 10, 10);

        assertEquals(10, p.getLastPage());
        assertEquals(91, p.getFirstPos());
        assertEquals(95, p.getLastPos());

        assertEquals(Arrays.asList(new Integer[] { 6, 7, 8, 9, 10 }), p.buildPageList(5));
    }

    @Test
    public final void testCoverage()
    {
        Paging p = new Paging(95, 10, 10);

        assertTrue(p.toString().contains("page=10"));
    }

    @Test
    public final void testBuildPageList()
    {
        assertEquals("[1, 2, 3, 4, 5]", Paging.buildPageList(1, 20, 5).toString());
        assertEquals("[1, 2, 3, 4, 5]", Paging.buildPageList(2, 20, 5).toString());
        assertEquals("[1, 2, 3, 4, 5]", Paging.buildPageList(3, 20, 5).toString());
        assertEquals("[2, 3, 4, 5, 6]", Paging.buildPageList(4, 20, 5).toString());

        assertEquals("[8, 9, 10, 11, 12]", Paging.buildPageList(10, 20, 5).toString());

        assertEquals("[7, 8, 9, 10]", Paging.buildPageList(10, 10, 4).toString());
        assertEquals("[7, 8, 9, 10]", Paging.buildPageList(9, 10, 4).toString());
        assertEquals("[7, 8, 9, 10]", Paging.buildPageList(8, 10, 4).toString());
        assertEquals("[6, 7, 8, 9]", Paging.buildPageList(7, 10, 4).toString());

        assertEquals("[1, 2, 3, 4]", Paging.buildPageList(1, 20, 4).toString());
        assertEquals("[1, 2, 3, 4]", Paging.buildPageList(2, 20, 4).toString());
        assertEquals("[2, 3, 4, 5]", Paging.buildPageList(3, 20, 4).toString());

        assertEquals("[1, 2, 3]", Paging.buildPageList(1, 3, 4).toString());
        assertEquals("[1, 2, 3]", Paging.buildPageList(2, 3, 4).toString());
        assertEquals("[1, 2, 3]", Paging.buildPageList(3, 3, 4).toString());

        try
        {
            Paging.buildPageList(0, 10, 5);
            fail("fail page >= 1");
        }
        catch (RuntimeException e)
        {
        }

        try
        {
            Paging.buildPageList(11, 10, 5);
            fail("fail page <= last");
        }
        catch (RuntimeException e)
        {
        }

        try
        {
            Paging.buildPageList(1, 10, 0);
            fail("fail num > 1");
        }
        catch (RuntimeException e)
        {
        }
    }

}
