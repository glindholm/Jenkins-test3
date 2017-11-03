/*
 *  Copyright 2010 Kevin Hunter
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

package net.sourceforge.wsup.hibernate4.database;

import java.util.List;

import org.hibernate.Query;

/**
 * Data Access Object corresponding to {@link TestBean}
 * 
 * @author Kevin Hunter
 */
public class TestBeanDAO extends BaseDAO<TestBean, Long>
{
    public TestBeanDAO()
    {
        super(TestBean.class);
    }

    public List<TestBean> getViaLike(DataAccessContext context, String like)
    {
        Query query = context
            .createQuery("select t from TestBean t where t.contents like :contents");
        query.setParameter("contents", like);

        return list(query);
    }

    public TestBean getViaContents(DataAccessContext context, String contents)
    {
        Query query = context.createQuery("select t from TestBean t where t.contents = :contents");
        query.setParameter("contents", contents);

        return uniqueResult(query);
    }

    public List<Long> listIdsAll(DataAccessContext context)
    {
        Query query = context.createQuery("select t.id from TestBean t");

        return listIds(query);
    }

    public List<TestBean> listByNullSafeContents(DataAccessContext context, String contents)
    {
        return listDistinctByCriteria(context,
                                      ".listByNullSafeContents()",
                                      null,
                                      nullSafeEquals("contents", contents));
    }
}
