/*
 *  Copyright 2012 Greg Lindholm
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

package net.sourceforge.wsup.hibernate4.utils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;

public class LikeClauseHelper
{
    public static final String LIKE_WIDECARDS = "[%_";

    /**
     * Escape a LIKE clause value using the supplied escape character. If the supplied
     * <code>value</code> contains any SQL wildcard characters ('%','_','[') they will be escaped by
     * prefixing
     * each wildcard with the <code>escape</code> character. Additionally any escape characters in
     * the value will be doubled up.
     * 
     * <pre>
     * LikeClauseHelper.escapeLikeValue("%Abc%",'!')     = "!%Abc!%"
     * LikeClauseHelper.escapeLikeValue("Max %50",'!')   = "Max !%50"
     * LikeClauseHelper.escapeLikeValue("[%_!",'!')      = "![!%!_!!"
     * LikeClauseHelper.escapeLikeValue("!!",'!')        = "!!!!"
     * LikeClauseHelper.escapeLikeValue("Abc",'!')       = "Abc"
     * LikeClauseHelper.escapeLikeValue(null,'!')        = null
     * </pre>
     * 
     * @param value the value to escape (null safe)
     * @param escape the escape character, usually '!'. (Wildcard characters ('%','_','[') are
     *            invalid)
     * @return the escaped value
     * @throws IllegalArgumentException if the escape character is LIKE clause wildcard character.
     */
    public static String escapeLikeValue(String value, char escape)
    {
        if (LIKE_WIDECARDS.indexOf(escape) != -1)
        {
            throw new IllegalArgumentException("Invalid escape [" + escape + "]");
        }

        value = StringUtils.replace(value, "" + escape, "" + escape + escape);

        for (int i = 0; i < LIKE_WIDECARDS.length(); i++)
        {
            String wildcard = LIKE_WIDECARDS.substring(i, i + 1);
            value = StringUtils.replace(value, wildcard, escape + wildcard);
        }

        return value;
    }

    /**
     * Build an HQL case-insensitive escaped LIKE clause. The escaped LIKE clause is used to search
     * for a partial match in a field using a user supplied value string. The character '!' is used
     * as the escape character for escaping LIKE wildcard characters.
     * Use {@link #setEscapedLikeStringAny(Query, String, String)} or
     * {@link #setEscapedLikeStringStart(Query, String, String)} to set the LIKE clause value on the
     * Query.
     * 
     * @param fieldName the name of the field.
     * @return the HQL case-insensitive escaped LIKE clause.
     */
    public static String buildEscapedLikeClause(String fieldName)
    {
        return StringUtils.replace(" lower(?) like :? escape '!' ", "?", fieldName);
    }

    /**
     * Set the value for an HQL case-insensitive escaped LIKE clause that matches ANY portion of the
     * field. The clause should be build by {@link LikeClauseHelper#buildEscapedLikeClause(String)}.
     * The character '!' is used as the escape character for escaping LIKE wildcard characters.
     * 
     * @param query
     * @param fieldName the name of the field.
     * @param value the un-escaped LIKE clause value
     */
    public static void setEscapedLikeStringAny(Query query, String fieldName, String value)
    {
        query.setString(fieldName, "%"
                                   + LikeClauseHelper.escapeLikeValue(value, '!').toLowerCase()
                                   + "%");
    }

    /**
     * Set the value for an HQL case-insensitive escaped LIKE clause that matches the START of the
     * field. The clause should be build by {@link LikeClauseHelper#buildEscapedLikeClause(String)}.
     * The character '!' is used as the escape character for escaping LIKE wildcard characters.
     * 
     * @param query
     * @param fieldName the name of the field.
     * @param value the un-escaped LIKE clause value
     */
    public static void setEscapedLikeStringStart(Query query, String fieldName, String value)
    {
        query
            .setString(fieldName, LikeClauseHelper.escapeLikeValue(value, '!').toLowerCase() + "%");
    }

    private LikeClauseHelper()
    {
    }

    /* package */static void coverConstructor()
    {
        new LikeClauseHelper();
    }
}
