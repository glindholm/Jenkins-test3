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

import net.jcip.annotations.Immutable;

/**
 * This is a utility class for presenting options for a "select" list
 * in Struts.  The action class would normally do the following:
 * 1) Provide a property for a String value associated with
 * the select tag. (getSelectValueProperty and setSelectValueProperty in
 * the example below.
 * 2) Provide a method that returns a list of these objects as the
 * choices for the select list. (methodReturningListOfSelectListEntrys in
 * the example below)
 * 3) Implement a tag as follows:
 * <s:select name="selectValueProperty"
 * 			value="selectValueProperty"
 *			list="methodReturningListOfSelectListEntrys"
 *			listKey="key"		<-- identifies to use the "key" property of SelectListEntry
 *									as the identifier for each value
 *			listValue="text"	<-- identifies to use the "text" property of SelectListEntry
 *									as the user text for each value
 *			headerKey="(none)"	<-- key value for optional initial "please select one" item
 *									in list.
 *			headerValue="%{getText('initialItemInIlist')}"	<-- text of optional initial
 *									"please select one" item in list
 *	/>
 *
 * The list implements the <code>Comparable</code> interface to allow a
 * list of SelectListEntry items to be sorted according to their text value.
 *
 */
@Immutable
public class SelectOption implements Comparable<SelectOption>
{
    public SelectOption(String key, String text)
    {
        this.key = key;
        this.text = text;
    }

    private final String key;
    private final String text;

    public String getKey()
    {
        return key;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public int compareTo(SelectOption other)
    {
        return text.compareToIgnoreCase(other.text);
    }

    @Override
    public String toString()
    {
        return String.format("(%s, %s)", key, text);
    }

}
