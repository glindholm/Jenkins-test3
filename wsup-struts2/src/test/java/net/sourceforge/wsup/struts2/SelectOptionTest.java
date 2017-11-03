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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class SelectOptionTest
{

    @Test
    public void testSelectOption()
    {
        SelectOption s1 = new SelectOption("Key", "Text");

        assertEquals("Key", s1.getKey());
        assertEquals("Text", s1.getText());

        // coverage
        s1.toString();
    }

    @Test
    public void testCompareTo()
    {
        List<SelectOption> list = new ArrayList<SelectOption>();

        list.add(new SelectOption("1", "AAA"));
        list.add(new SelectOption("2", "aaa"));
        list.add(new SelectOption("3", "BBB"));
        list.add(new SelectOption("4", "bbb"));

        Collections.sort(list);

        assertEquals("aaa", list.get(0).getText().toLowerCase());
        assertEquals("aaa", list.get(1).getText().toLowerCase());
        assertEquals("bbb", list.get(2).getText().toLowerCase());
        assertEquals("bbb", list.get(3).getText().toLowerCase());

    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testCompareToObject()
    {
        Comparable s1 = new SelectOption("Key", "Text");
        assertEquals(0, s1.compareTo(new SelectOption("Key", "Text")));
    }
}
