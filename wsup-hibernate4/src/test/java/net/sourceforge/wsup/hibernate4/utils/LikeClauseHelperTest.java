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

package net.sourceforge.wsup.hibernate4.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.sourceforge.wsup.hibernate4.testClasses.MockHibernateQuery;

import org.junit.Test;

public class LikeClauseHelperTest
{
    public LikeClauseHelperTest()
    {
    }

    @Test
    public void coverConstructor()
    {
        LikeClauseHelper.coverConstructor();
    }

    @Test
    public void testEscapeLikeValue()
    {
        assertEquals("Abc", LikeClauseHelper.escapeLikeValue("Abc", '!'));
        assertEquals("!%Abc!%", LikeClauseHelper.escapeLikeValue("%Abc%", '!'));
        assertEquals("![!%!_!!", LikeClauseHelper.escapeLikeValue("[%_!", '!'));
        assertEquals("!!!!", LikeClauseHelper.escapeLikeValue("!!", '!'));
        assertEquals(null, LikeClauseHelper.escapeLikeValue(null, '!'));

        try
        {
            LikeClauseHelper.escapeLikeValue("abc", '%');
            fail();
        }
        catch (IllegalArgumentException success)
        {
        }
    }

    @Test
    public void testBuildEscapedLikeClause()
    {
        assertEquals(" lower(fieldName) like :fieldName escape '!' ",
                     LikeClauseHelper.buildEscapedLikeClause("fieldName"));
    }

    @Test
    public void setEscapedLikeStringAny_works()
    {
        MockHibernateQuery query = new MockHibernateQuery();

        LikeClauseHelper.setEscapedLikeStringAny(query, "field", "value");

        assertEquals("field", query.getFieldName());
        assertEquals("%value%", query.getValue());
    }

    @Test
    public void setEscapedLikeStringStart_works()
    {
        MockHibernateQuery query = new MockHibernateQuery();

        LikeClauseHelper.setEscapedLikeStringStart(query, "field", "value");

        assertEquals("field", query.getFieldName());
        assertEquals("value%", query.getValue());
    }
}
