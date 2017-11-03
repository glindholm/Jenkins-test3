/*
 * Copyright 2010 Kevin Hunter
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

package net.sourceforge.wsup.hibernate4.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.wsup.hibernate4.utils.EscapedLikeExpression;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.impl.MockLogger;

/**
 * Unit test for BaseDAO class.
 * 
 * @author Kevin Hunter
 */
public class BaseDAOTest
{
    private DatabaseContextImpl databaseContext;

    private DataAccessContext   daoContext;

    private TestBeanDAO         testBeanDAO;

    private TestDateBeanDAO     testDateBeanDAO;

    public BaseDAOTest()
    {
    }

    /*
     * Make sure the database is open, with tables created but empty, before
     * each test.
     */
    @Before
    public void setup() throws Exception
    {
        MockLogger.get("org.hibernate").setDebug();
        MockLogger.get("com.mchange.v2.c3p0").setWarn();
        MockLogger.get("com.mchange.v2.log").setWarn();

        testBeanDAO = new TestBeanDAO();
        testDateBeanDAO = new TestDateBeanDAO();
        TestDatabase database = TestDatabaseManager.openTestDatabaseIfRequired();
        database.initializeTables();
        databaseContext = (DatabaseContextImpl) database.createDatabaseContext();
        daoContext = new DataAccessContextImpl(databaseContext);
    }

    @After
    public void cleanup() throws Exception
    {
        databaseContext.destroySession();

        MockLogger.clear();
    }

    /*
     * Force a new session so that we are pulling directly from the database,
     * not from the session cache.
     */
    private void newSession()
    {
        databaseContext.destroySession();
        databaseContext.beginSession(false);
    }

    private void beginTransaction()
    {
        databaseContext.beginTransaction();
    }

    private void commit()
    {
        databaseContext.commitTransaction();
    }

    /*
     * Utility method to create a new TestBean and insert it
     */
    private TestBean createTestBean(String content)
    {
        beginTransaction();
        TestBean testBean = new TestBean(content);
        testBean = testBeanDAO.insert(daoContext, testBean);
        commit();
        return testBean;
    }

    /*
     * Utility method to create a new TestDateBean and insert it
     */
    private TestDateBean createTestDateBean(String content)
    {
        beginTransaction();
        TestDateBean testDateBean = new TestDateBean(content, "other");
        testDateBean = testDateBeanDAO.insert(daoContext, testDateBean);
        commit();
        return testDateBean;
    }

    /*
     * Utility method to create a new TestDateBean and insert it
     */
    private TestDateBean createTestDateBean(String content, String other)
    {
        beginTransaction();
        TestDateBean testDateBean = new TestDateBean(content, other);
        testDateBean = testDateBeanDAO.insert(daoContext, testDateBean);
        commit();
        return testDateBean;
    }

    /*
     * Basic info from the classes
     */
    @Test
    public void testClassAndTableName()
    {
        assertEquals(TestBean.class, testBeanDAO.getPersistentClass());
        assertEquals(TestBean.class.getName(), testBeanDAO.getPersistentClassName());
        assertEquals("testTable", testBeanDAO.getTableName(daoContext));

        assertEquals(TestDateBean.class, testDateBeanDAO.getPersistentClass());
        assertEquals(TestDateBean.class.getName(), testDateBeanDAO.getPersistentClassName());
        assertEquals("dateTable", testDateBeanDAO.getTableName(daoContext));
    }

    /*
     * Insert objects
     */
    @Test
    public void testInserts()
    {
        beginTransaction();

        assertEquals(0, testBeanDAO.getCount(daoContext));
        assertEquals(0, testDateBeanDAO.getCount(daoContext));

        TestBean testBean = new TestBean("one");
        TestDateBean testDateBean = new TestDateBean("two", "two");

        testBean = testBeanDAO.insert(daoContext, testBean);
        testDateBean = testDateBeanDAO.insert(daoContext, testDateBean);

        commit();

        assertNotNull(testBean.getId());
        assertNotNull(testDateBean.getId());
        assertNotNull(testDateBean.getCreated());
        assertNotNull(testDateBean.getUpdated());
        assertEquals(testDateBean.getCreated(), testDateBean.getUpdated());

        beginTransaction();

        assertEquals(1, testBeanDAO.getCount(daoContext));
        assertEquals(1, testDateBeanDAO.getCount(daoContext));

        commit();

        beginTransaction();

        List<TestBean> testBeans = testBeanDAO.listAll(daoContext, null);
        List<TestDateBean> testDateBeans = testDateBeanDAO.listAll(daoContext, null);

        assertEquals(1, testBeans.size());
        assertEquals(1, testDateBeans.size());

        assertEquals("one", testBeans.get(0).getContents());
        assertEquals("two", testDateBeans.get(0).getContents());

        commit();
    }

    /*
     * Update objects
     */
    @Test
    public void testUpdates() throws Exception
    {
        // HSQLDB actually only supports READ_UNCOMMITTED, so it logs when we
        // try to get with a lock (but still works)
        MockLogger.get("org.hibernate.dialect.HSQLDialect").setError();

        beginTransaction();

        assertEquals(0, testBeanDAO.getCount(daoContext));
        assertEquals(0, testDateBeanDAO.getCount(daoContext));

        TestBean testBean = new TestBean("one");
        TestDateBean testDateBean = new TestDateBean("two", "two");

        testBean = testBeanDAO.insert(daoContext, testBean);
        testDateBean = testDateBeanDAO.insert(daoContext, testDateBean);

        Date created = testDateBean.getCreated();

        commit();

        Thread.sleep(1000L);

        beginTransaction();

        testBean = testBeanDAO.getByIdLock(daoContext, testBean.getId());
        testDateBean = testDateBeanDAO.getByIdLock(daoContext, testDateBean.getId());

        testBean.setContents("two");
        testDateBean.setContents("one");

        testBean = testBeanDAO.update(daoContext, testBean);
        testDateBean = testDateBeanDAO.update(daoContext, testDateBean);

        commit();

        assertEquals(created, testDateBean.getCreated());
        assertTrue(testDateBean.getUpdated().after(created));

        beginTransaction();

        List<TestBean> testBeans = testBeanDAO.listAll(daoContext, null);
        List<TestDateBean> testDateBeans = testDateBeanDAO.listAll(daoContext, null);

        assertEquals(1, testBeans.size());
        assertEquals(1, testDateBeans.size());

        assertEquals("two", testBeans.get(0).getContents());
        assertEquals("one", testDateBeans.get(0).getContents());

        commit();
    }

    /*
     * findById locates a bean, and returns null if no such id exists
     */
    @Test
    public void testGetById()
    {
        Long testBeanId = createTestBean("a").getId();

        newSession();

        beginTransaction();

        TestBean testBean = testBeanDAO.getById(daoContext, testBeanId);

        TestBean noSuchBean = testBeanDAO.getById(daoContext, new Long(-1));

        commit();

        assertEquals("a", testBean.getContents());
        assertNull(noSuchBean);
    }

    /*
     * Delete deletes only the specified bean
     */
    @Test
    public void testDelete()
    {
        TestDateBean bean1 = createTestDateBean("1");
        TestDateBean bean2 = createTestDateBean("2");

        beginTransaction();

        assertEquals(2, testDateBeanDAO.getCount(daoContext));

        commit();

        beginTransaction();

        testDateBeanDAO.delete(daoContext, bean1);

        commit();

        beginTransaction();

        assertEquals(1, testDateBeanDAO.getCount(daoContext));
        List<TestDateBean> beans = testDateBeanDAO.listAll(daoContext, null);
        assertEquals(1, beans.size());
        assertEquals("2", beans.get(0).getContents());
        assertEquals(bean2.getId(), beans.get(0).getId());

        commit();
    }

    /*
     * Delete deletes only the specified bean
     */
    @Test
    public void testDeleteById()
    {
        // put two in the database
        TestDateBean bean1 = createTestDateBean("1");
        TestDateBean bean2 = createTestDateBean("2");

        beginTransaction();

        assertEquals(2, testDateBeanDAO.getCount(daoContext));

        commit();

        // delete the first one by ID
        beginTransaction();

        assertTrue(testDateBeanDAO.deleteById(daoContext, bean1.getId()));

        commit();

        // second one is still there
        beginTransaction();

        assertEquals(1, testDateBeanDAO.getCount(daoContext));
        List<TestDateBean> beans = testDateBeanDAO.listAll(daoContext, null);
        assertEquals(1, beans.size());
        assertEquals("2", beans.get(0).getContents());
        assertEquals(bean2.getId(), beans.get(0).getId());

        commit();

        // repeat delete by ID - returns false because there isn't one
        // with that iD
        beginTransaction();

        assertFalse(testDateBeanDAO.deleteById(daoContext, bean1.getId()));

        commit();

        // other data untouched
        beginTransaction();

        assertEquals(1, testDateBeanDAO.getCount(daoContext));
        beans = testDateBeanDAO.listAll(daoContext, null);
        assertEquals(1, beans.size());
        assertEquals("2", beans.get(0).getContents());
        assertEquals(bean2.getId(), beans.get(0).getId());

        commit();
    }

    /*
     * Clear table deletes everything
     */
    @Test
    public void testClearTable()
    {
        createTestBean("1");
        createTestBean("2");

        beginTransaction();

        assertEquals(2, testBeanDAO.getCount(daoContext));

        commit();

        beginTransaction();

        testBeanDAO.clearTable(daoContext);

        commit();

        beginTransaction();

        assertEquals(0, testBeanDAO.getCount(daoContext));
        assertEquals(0, testBeanDAO.listAll(daoContext, null).size());

        commit();
    }

    /*
     * Delete all deletes everything
     */
    @Test
    public void testDeleteAll()
    {
        createTestBean("1");
        createTestBean("2");

        beginTransaction();

        assertEquals(2, testBeanDAO.getCount(daoContext));

        commit();

        beginTransaction();

        testBeanDAO.deleteAll(daoContext);

        commit();

        beginTransaction();

        assertEquals(0, testBeanDAO.getCount(daoContext));
        assertEquals(0, testBeanDAO.listAll(daoContext, null).size());

        commit();
    }

    @Test
    public void testListAll()
    {
        Long id1 = createTestBean("a").getId();
        Long id2 = createTestBean("b").getId();
        Long id3 = createTestBean("c").getId();

        beginTransaction();

        List<TestBean> list;

        list = testBeanDAO.listAll(daoContext, null);
        assertEquals(3, list.size());

        list = testBeanDAO.listAllDistinct(daoContext, null);
        assertEquals(3, list.size());

        list = testBeanDAO.listAll(daoContext, Order.asc("contents"));
        assertEquals(id1, list.get(0).getId());
        assertEquals(id2, list.get(1).getId());
        assertEquals(id3, list.get(2).getId());

        list = testBeanDAO.listAll(daoContext, Order.desc("contents"));
        assertEquals(id3, list.get(0).getId());
        assertEquals(id2, list.get(1).getId());
        assertEquals(id1, list.get(2).getId());

        list = testBeanDAO.listAllDistinct(daoContext, Order.asc("contents"));
        assertEquals(id1, list.get(0).getId());
        assertEquals(id2, list.get(1).getId());
        assertEquals(id3, list.get(2).getId());

        list = testBeanDAO.listAllDistinct(daoContext, Order.desc("contents"));
        assertEquals(id3, list.get(0).getId());
        assertEquals(id2, list.get(1).getId());
        assertEquals(id1, list.get(2).getId());
    }

    /*
     * Test variants on getByCriteria
     */
    @Test
    public void testGetByCriteria()
    {
        Long id1 = createTestBean("a").getId();
        Long id2 = createTestBean("b").getId();
        createTestBean("c").getId();

        beginTransaction();

        TestBean result = testBeanDAO.getByCriteria(daoContext,
                                                    null,
                                                    Restrictions.eq("contents", "a"));

        assertEquals(id1, result.getId());

        result = testBeanDAO.getByCriteria(daoContext,
                                           "Look for b",
                                           Restrictions.like("contents", "%b%"));

        assertEquals(id2, result.getId());

        commit();
    }

    /*
     * Use (indirectly via code in TestBeanDAO) the uniqueResult(Query) method
     */
    @Test
    public void testQueryUniqueResult()
    {
        createTestBean("a");
        Long id = createTestBean("b").getId();
        createTestBean("c");

        beginTransaction();
        TestBean bean = testBeanDAO.getViaContents(daoContext, "b");
        assertEquals(id, bean.getId());
        assertNull(testBeanDAO.getViaContents(daoContext, "foo"));

        commit();
    }

    /*
     * Use (indirectly via code in TestBeanDAO) the list(Query) method
     */
    @Test
    public void testQueryList()
    {
        createTestBean("a1");
        createTestBean("a2");
        createTestBean("b");

        beginTransaction();
        List<TestBean> list = testBeanDAO.getViaLike(daoContext, "a%");
        assertEquals(2, list.size());

        list = testBeanDAO.getViaLike(daoContext, "foo%");
        assertEquals(0, list.size());

        commit();
    }

    @Test
    public void testListByCriteria()
    {
        TestDateBean aa = createTestDateBean("a", "a");
        TestDateBean ab = createTestDateBean("a", "b");
        TestDateBean ac = createTestDateBean("a", "c");
        TestDateBean ba = createTestDateBean("b", "a");
        TestDateBean bb = createTestDateBean("b", "b");
        TestDateBean bc = createTestDateBean("b", "c");

        beginTransaction();

        assertEquals(6, testDateBeanDAO.getCount(daoContext));

        List<TestDateBean> list;

        list = testDateBeanDAO.listByCriteria(daoContext,
                                              null,
                                              null,
                                              Restrictions.eq("contents", "a"));
        assertEquals(3, list.size());

        list = testDateBeanDAO.listByCriteria(daoContext,
                                              "a/a",
                                              null,
                                              Restrictions.eq("contents", "a"),
                                              Restrictions.eq("other", "a"));
        assertEquals(1, list.size());
        assertEquals(aa.getId(), list.get(0).getId());

        list = testDateBeanDAO.listByCriteria(daoContext,
                                              null,
                                              Order.asc("other"),
                                              Restrictions.eq("contents", "a"));
        assertEquals(3, list.size());
        assertEquals(aa.getId(), list.get(0).getId());
        assertEquals(ab.getId(), list.get(1).getId());
        assertEquals(ac.getId(), list.get(2).getId());

        list = testDateBeanDAO.listByCriteria(daoContext,
                                              null,
                                              Order.desc("other"),
                                              Restrictions.eq("contents", "b"));
        assertEquals(3, list.size());
        assertEquals(bc.getId(), list.get(0).getId());
        assertEquals(bb.getId(), list.get(1).getId());
        assertEquals(ba.getId(), list.get(2).getId());

        list = testDateBeanDAO.listDistinctByCriteria(daoContext,
                                                      null,
                                                      null,
                                                      Restrictions.eq("contents", "a"));
        assertEquals(3, list.size());

        list = testDateBeanDAO.listDistinctByCriteria(daoContext,
                                                      "a/a",
                                                      null,
                                                      Restrictions.eq("contents", "a"),
                                                      Restrictions.eq("other", "a"));
        assertEquals(1, list.size());
        assertEquals(aa.getId(), list.get(0).getId());

        list = testDateBeanDAO.listDistinctByCriteria(daoContext,
                                                      null,
                                                      Order.asc("other"),
                                                      Restrictions.eq("contents", "a"));
        assertEquals(3, list.size());
        assertEquals(aa.getId(), list.get(0).getId());
        assertEquals(ab.getId(), list.get(1).getId());
        assertEquals(ac.getId(), list.get(2).getId());

        list = testDateBeanDAO.listDistinctByCriteria(daoContext,
                                                      null,
                                                      Order.desc("other"),
                                                      Restrictions.eq("contents", "b"));
        assertEquals(3, list.size());
        assertEquals(bc.getId(), list.get(0).getId());
        assertEquals(bb.getId(), list.get(1).getId());
        assertEquals(ba.getId(), list.get(2).getId());

        commit();
    }

    @Test
    public void testNullSafeContents()
    {
        createTestBean(null);
        createTestBean("a");
        createTestBean("a");
        createTestBean("b");
        createTestBean("b");
        createTestBean("b");

        beginTransaction();
        List<TestBean> list = testBeanDAO.listByNullSafeContents(daoContext, "a");
        assertEquals(2, list.size());

        list = testBeanDAO.listByNullSafeContents(daoContext, null);
        assertEquals(1, list.size());

        list = testBeanDAO.listByNullSafeContents(daoContext, "b");
        assertEquals(3, list.size());

        commit();
    }

    @Test
    public void testListIds()
    {
        createTestBean("a1");
        createTestBean("a2");
        createTestBean("b");

        beginTransaction();
        List<Long> ids = testBeanDAO.listIdsAll(daoContext);
        assertEquals(3, ids.size());

        commit();
    }

    @Test
    public void testEscapedLkeExpression()
    {
        List<TestBean> beans = new ArrayList<TestBean>();
        beans.add(createTestBean("Abe made %50 profit"));
        beans.add(createTestBean("Bob had %55 gain"));
        beans.add(createTestBean("Carl swam %40 better"));
        beans.add(createTestBean("Doug drove 50 precent faster"));

        beginTransaction();

        List<TestBean> results = testBeanDAO.listDistinctByCriteria(daoContext, "comment", Order
            .asc("contents"), new EscapedLikeExpression("contents", "%5"));

        assertEquals(2, results.size());
        assertEquals(beans.get(0).getContents(), results.get(0).getContents());
        assertEquals(beans.get(1).getContents(), results.get(1).getContents());

        results = testBeanDAO.listDistinctByCriteria(daoContext,
                                                     "comment",
                                                     Order.asc("contents"),
                                                     new EscapedLikeExpression("contents", "50"));

        assertEquals(2, results.size());
        assertEquals(beans.get(0).getContents(), results.get(0).getContents());
        assertEquals(beans.get(3).getContents(), results.get(1).getContents());

        commit();
    }

}
