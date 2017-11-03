/*
 * Copyright (c) 2011 Kevin Hunter
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

import java.util.Comparator;
import java.util.Locale;

import net.sourceforge.wsup.core.LocaleStringComparator;

/**
 * This class provides a <code>Locale</code>-sensitive <code>Comparator</code> for
 * the <code>SelectOption</code> class.
 * 
 * @author Kevin Hunter
 *
 */
public class SelectOptionComparator implements Comparator<SelectOption>
{
    private Comparator<String> comparator;
    
    /**
     * Produces an object that will use the current default <code>Locale</code>
     * and which performs a case-insensitive comparison.
     */
    public SelectOptionComparator()
    {
        comparator = new LocaleStringComparator();
    }
    
    /**
     * Produces an object that will use the current default <code>Locale</code>.
     * @param caseSensitive Determines whether the comparison will be case-sensitive
     * or case-insensitive.
     */
    public SelectOptionComparator(boolean caseSensitive)
    {
        comparator = new LocaleStringComparator(caseSensitive);
    }
    
    /**
     * Produces an object that will use the specified <code>Locale</code>
     * and which performs a case-insensitive comparison.
     * @param locale    <code>Locale</code> for comparisons.
     */
    public SelectOptionComparator(Locale locale)
    {
        comparator = new LocaleStringComparator(locale);
    }

    /**
     * Produces an object that will use the specified <code>Locale</code>
     * @param locale   <code>Locale</code> for comparisons.
     * @param caseSensitive Determines whether the comparison will be case-sensitive
     * or case-insensitive.
     */
    public SelectOptionComparator(Locale locale, boolean caseSensitive)
    {
        comparator = new LocaleStringComparator(locale, caseSensitive);
    }

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(SelectOption o1, SelectOption o2)
    {
        String text1 = o1.getText();
        String text2 = o2.getText();
        
        return comparator.compare(text1, text2);
    }
}
