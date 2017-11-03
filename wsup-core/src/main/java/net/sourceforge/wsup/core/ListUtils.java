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

import java.util.ArrayList;
import java.util.List;

/**
 * General list utility class
 * 
 * @author Kevin Hunter
 */
public final class ListUtils
{
    /**
     * Extract a page's worth out of an input <code>List</code>.
     * 
     * @param <T> Type of object contained in the <code>List</code>.
     * @param input Input <code>List</code>
     * @param firstIndex Zero-based index of the first entry to extract
     * @param pageSize Maximum number of entries to extract
     * @return A new <code>List</code> with at most <code>pageSize</code> entries.
     */
    public static <T> List<T> pageFromList(List<T> input, int firstIndex, int pageSize)
    {
        ArrayList<T> output = new ArrayList<T>();

        if (input != null)
        {
            int i = 0;

            for (T obj : input)
            {
                if (i >= firstIndex + pageSize)
                {
                    break;
                }

                if (i >= firstIndex)
                {
                    output.add(obj);
                }

                i++;
            }
        }

        return output;
    }
    
    private ListUtils()
    {
    }
    
    /*package*/ static void coverage()
    {
        new ListUtils();
    }
}
