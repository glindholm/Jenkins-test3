/*
 *  Copyright (c) 2011 Kevin Hunter
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

package net.sourceforge.wsup.core;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * This class provides a <code>Locale</code>-aware <code>Comparator</code>
 * for <code>String</code>s.  This class is <code>null</code>-safe, in that
 * either or both strings passed for comparison may be <code>null</code>.
 * The class considers a <code>null</code> string as equal to another
 * <code>null</code> string, and less than any non-<code>null</code>
 * string.
 * 
 * @author Kevin Hunter
 *
 */
public class LocaleStringComparator implements Comparator<String>
{
    private Locale      locale;
    private Collator    collator;
    private boolean     caseSensitive;
    
    /**
     * Produces an object that will use the current default <code>Locale</code>
     * and which performs a case-insensitive comparison.
     */
    public LocaleStringComparator()
    {
        this(null, false);
    }
    
    /**
     * Produces an object that will use the current default <code>Locale</code>.
     * @param caseSensitive Determines whether the comparison will be case-sensitive
     * or case-insensitive.
     */
    public LocaleStringComparator(boolean caseSensitive)
    {
        this(null, caseSensitive);
    }
    
    /**
     * Produces an object that will use the specified <code>Locale</code>
     * and which performs a case-insensitive comparison.
     * @param locale    <code>Locale</code> for comparisons.
     */
    public LocaleStringComparator(Locale locale)
    {
        this(locale, false);
    }

    /**
     * Produces an object that will use the specified <code>Locale</code>
     * @param locale   <code>Locale</code> for comparisons.
     * @param caseSensitive Determines whether the comparison will be case-sensitive
     * or case-insensitive.
     */
    public LocaleStringComparator(Locale locale, boolean caseSensitive)
    {
        if (locale == null)
        {
            locale = Locale.getDefault();
        }
        
        this.locale = locale;
        this.collator = Collator.getInstance(locale);
        this.caseSensitive = caseSensitive;
    }

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(String string1, String string2)
    {
        /*
         * Identity is, of course, equal.  This also catches the case
         * where both strings are null.
         */
        if (string1 == string2)
        {
            return 0;
        }
        
        /*
         * null is less than any other string.
         */
        if (string1 == null)
        {
            return -1;
        }
        
        if (string2 == null)
        {
            return 1;
        }
        
        /*
         * If we get here, we know both are non-null and are
         * different instances, so proceed with "normal" comparison.
         */
        if (!caseSensitive)
        {
            string1 = string1.toUpperCase(locale);
            string2 = string2.toUpperCase(locale);
        }
        
        return collator.compare(string1, string2);
    }
}
