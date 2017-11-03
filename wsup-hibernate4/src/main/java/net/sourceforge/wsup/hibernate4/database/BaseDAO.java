/*
 * Copyright 2012 Kevin Hunter
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.sourceforge.wsup.hibernate4.database;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * This is a base class for DAOs (Data Access Objects) for Hibernate-based
 * persistence. This class implements (in a parameterized way) most of the basic
 * operations necessary for manipulating persistent entities.
 * 
 * Thus, if you have a class named Foo, which has an ID type of Long, you would
 * typically create a FooDAO as:
 * 
 * <pre>
 * public class FooDAO extends BaseDAO<Foo, Long>
 * {
 * 		public FooDAO()
 * 		{
 * 			super(Foo.class);
 * 		}
 *
 * 	... additional methods if required ...
 * }
 * </pre>
 * 
 * @author Kevin Hunter
 * 
 * @param <PERSISTENTCLASS>
 *            The type of the persistent entity.
 * @param <IDTYPE>
 *            The type of ID that the persistent entity has.
 */
public abstract class BaseDAO<PERSISTENTCLASS, IDTYPE extends Serializable>
{
    private Class<PERSISTENTCLASS> persistantClass;

    /**
     * Constructor.
     * 
     * @param persistantClass
     *            <code>Class</code> object of the persistent class.
     */
    protected BaseDAO(Class<PERSISTENTCLASS> persistantClass)
    {
        this.persistantClass = persistantClass;
    }

    /**
     * Return the <code>Class</code> of the persistent class.
     * 
     * @return <code>Class</code> object associated with the persistent class.
     */
    public Class<PERSISTENTCLASS> getPersistentClass()
    {
        return persistantClass;
    }

    /**
     * Return the fully qualified name of the class of the persistent class.
     * 
     * @return Fully qualified name of the class of the persistent class.
     */
    public String getPersistentClassName()
    {
        return persistantClass.getName();
    }

    /**
     * Return the name of the table containing the persistent entities.
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @return Name of the table containing the persistent entities
     */
    public String getTableName(DataAccessContext daoContext)
    {
        return daoContext.getTableNameForClass(getPersistentClass());
    }

    /**
     * Build up a <code>Criteria</code> object using the specified <code>Criterion</code>s.
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @param criterions
     *            Zero or more <code>Criterion</code> objects describing the
     *            objects to be selected. If no <code>Criterion</code>s
     *            are specified, this is implicitly a "select all".
     * @return <code>Criteria</code> object that can be used to query the
     *         database.
     */
    public Criteria buildCriteria(DataAccessContext daoContext, Criterion... criterions)
    {
        Criteria criteria = daoContext.createCriteria(getPersistentClass());

        if (criterions != null)
        {
            for (Criterion c : criterions)
            {
                criteria.add(c);
            }
        }

        return criteria;
    }

    /**
     * Load the particular instance identified by the specified ID.
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @param id
     *            ID to be found.
     * @return Instance identified by the ID, or <code>null</code> if not
     *         present.
     */
    @SuppressWarnings("unchecked")
    public PERSISTENTCLASS getById(DataAccessContext daoContext, IDTYPE id)
    {
        return (PERSISTENTCLASS) daoContext.get(getPersistentClass(), id);
    }

    /**
     * Load the particular instance identified by the specified ID, obtaining a
     * pessimistic lock on it while doing so.
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @param id
     *            ID to be found.
     * @return Instance identified by the ID, or <code>null</code> if not
     *         present.
     */
    @SuppressWarnings("unchecked")
    public PERSISTENTCLASS getByIdLock(DataAccessContext daoContext, IDTYPE id)
    {
        return (PERSISTENTCLASS) daoContext.get(getPersistentClass(), id, LockOptions.UPGRADE);
    }

    /**
     * Return the entity in the table that meets the specified criteria.
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @param comment
     *            Comment to be added to the query (for debugging purposes)
     * @param criterions
     *            One or more <code>Criterion</code> objects describing the
     *            objects to be selected.
     * @return Object that uniquely meets the criteria, or <code>null</code> if
     *         none do.
     */
    public PERSISTENTCLASS getByCriteria(DataAccessContext daoContext,
                                         String comment,
                                         Criterion... criterions)
    {
        Criteria criteria = buildCriteria(daoContext, criterions);
        if (comment != null)
        {
            criteria.setComment(comment);
        }

        return uniqueResult(criteria);
    }

    /**
     * Insert a new entity into the database. 
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @param entity
     *            Entity to be saved
     * @return Copy of the entity after being saved.
     */
    public PERSISTENTCLASS insert(DataAccessContext daoContext, PERSISTENTCLASS entity)
    {
        daoContext.save(entity);
        return entity;
    }

    /**
     * Update an existing entity in the database. This method uses the Hibernate
     * "saveOrUpdate", so it can also be used to insert a new entity. 
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @param entity
     *            Entity to be updated
     * @return Copy of the entity after being updated.
     */
    public PERSISTENTCLASS update(DataAccessContext daoContext, PERSISTENTCLASS entity)
    {
        daoContext.saveOrUpdate(entity);
        return entity;
    }

    /**
     * Delete a persistent entity from the database.
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @param entity
     *            Entity to be deleted
     */
    public void delete(DataAccessContext daoContext, PERSISTENTCLASS entity)
    {
        daoContext.delete(entity);
        daoContext.flush();
    }

    /**
     * Delete an entity from the database given only its ID (as opposed to
     * directly deleting a persistent instance as in {@link #delete(DataAccessContext, Object)}).
     * <p>
     * Things of which to be aware:
     * </p>
     * <ol>
     * <li>
     * <p>
     * This method uses an HQL "bulk delete":
     * </p>
     * 
     * <pre>
	 * delete from table where id = :id
	 * </pre>
     * 
     * <p>
     * This is efficient, since it does not require the object to have been fetched from the
     * database in order to delete it. However, this method will fail to operate properly if the
     * object in question has an &x40;ElementCollection, since those do not cascade properly under
     * bulk deletes, or if it is not set up to cascade deletes to any other objects that may have
     * foreign keys to it.
     * </p>
     * </li>
     * <li>
     * This method assumes that the unique ID for the object is, in fact, called <code>id</code>.</li>
     * </ol>
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @param id
     *            ID for the item to be deleted
     * @return <code>true</code> if an object was actually deleted, <code>false</code> if there was
     *         no matching object in the
     *         database.
     * @see #delete(DataAccessContext, Object)
     */
    public boolean deleteById(DataAccessContext daoContext, IDTYPE id)
    {
        String hql = "delete from " + getPersistentClassName() + " obj where obj.id = :id";
        Query query = daoContext.createQuery(hql);
        query.setComment(getClass().getSimpleName() + ".deleteById");
        query.setParameter("id", id);
        return query.executeUpdate() != 0;
    }

    /**
     * Perform a "fast clear" on the table containing the entities via an HQL
     * "bulk delete."
     * <p>
     * While efficient, this method doesn't cascade, so if there are other objects in the database
     * that maintain a foreign key relationship into this table, this operation will fail with a
     * constraint violation exception unless those objects are deleted first. Thus, in this case,
     * there are two options:
     * </p>
     * <ul>
     * <li>Use <code>deleteAll</code> instead of this method</li>
     * <li>Override this method in a derived DAO and clear the dependent tables before delegating
     * upward to this method.</li>
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @see #deleteAll(DataAccessContext)
     */
    public void clearTable(DataAccessContext daoContext)
    {
        String hql = "delete from " + getPersistentClassName();
        Query query = daoContext.createQuery(hql);
        query.setComment(getClass().getSimpleName() + ".clearTable()");
        query.executeUpdate();
        daoContext.flush();
    }

    /**
     * Perform an entity-by-entity delete on the entities in the database. This
     * delete will cascade, if the table is set up that way. Performing this
     * operation requires selecting all the entities, however, which can be slow
     * and memory-intensive for large tables. When possible, it is faster to use
     * {@link BaseDAO#clearTable(DataAccessContext)} and make sure that
     * dependent objects are deleted first.
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @see #clearTable(DataAccessContext)
     */
    public void deleteAll(DataAccessContext daoContext)
    {
        for (PERSISTENTCLASS entity : listAll(daoContext, null))
        {
            daoContext.delete(entity);
        }
        daoContext.flush();
    }

    /**
     * Count the number of entities in the database.
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @return Number of entities in the table.
     */
    public long getCount(DataAccessContext daoContext)
    {
        daoContext.flush();
        String queryString = "select count(*) from " + getPersistentClassName();
        Query query = daoContext.createQuery(queryString);
        query.setComment(getClass().getSimpleName() + ".getCount()");
        Long result = (Long) query.uniqueResult();
        return result.longValue();
    }

    /**
     * Return a <code>List</code> of all the entities in the table.
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @param order
     *            <code>Order</code> in which the data should be returned.
     *            (Optional - may be <code>null</code>.)
     * @return <code>List</code> of all the entities. The returned <code>List</code> will not be
     *         <code>null</code>, but may be
     *         empty.
     */
    public List<PERSISTENTCLASS> listAll(DataAccessContext daoContext, Order order)
    {
        Criteria criteria = buildCriteria(daoContext).setComment(".listAll()");
        if (order != null)
        {
            criteria.addOrder(order);
        }
        return list(criteria);
    }

    /**
     * Return a <code>List</code> of all the entities in the table. Similar to
     * {@link #listAll(DataAccessContext, Order)}, except that it handles issues
     * with duplicate root elements due to embedded collections.
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @param order
     *            <code>Order</code> in which the data should be returned.
     *            (Optional - may be <code>null</code>.)
     * @return <code>List</code> of all the entities. The returned <code>List</code> will not be
     *         <code>null</code>, but may be
     *         empty.
     */
    public List<PERSISTENTCLASS> listAllDistinct(DataAccessContext daoContext, Order order)
    {
        Criteria criteria = buildCriteria(daoContext).setComment(".listAll()");
        if (order != null)
        {
            criteria.addOrder(order);
        }
        return listDistinct(criteria);
    }

    /**
     * Return a <code>List</code> of entities in the table that meet the
     * specified criteria.
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @param comment
     *            Comment to be added to the query for debugging purposes.
     *            (Optional - may be <code>null</code>).
     * @param order
     *            <code>Order</code> object indicating order in which to return
     *            objects. (Optional - may be <code>null</code>).
     * @param criterions
     *            One or more <code>Criterion</code> objects describing the
     *            objects to be selected.
     * @return <code>List</code> of all the entities. The returned <code>List</code> will not be
     *         <code>null</code>, but may be
     *         empty.
     */
    public List<PERSISTENTCLASS> listByCriteria(DataAccessContext daoContext,
                                                String comment,
                                                Order order,
                                                Criterion... criterions)
    {
        Criteria criteria = buildCriteria(daoContext, criterions);

        if (comment != null)
        {
            criteria.setComment(comment);
        }

        if (order != null)
        {
            criteria.addOrder(order);
        }

        return list(criteria);
    }

    /**
     * Return a <code>List</code> of entities in the table that meet the
     * specified criteria.Similar to
     * {@link #listByCriteria(DataAccessContext, String, Order, Criterion...)},
     * except that it handles issues
     * with duplicate root elements due to embedded collections.
     * 
     * @param daoContext
     *            {@link DataAccessContext} object for the database.
     * @param comment
     *            Comment to be added to the query for debugging purposes.
     *            (Optional - may be <code>null</code>).
     * @param order
     *            <code>Order</code> object indicating order in which to return
     *            objects. (Optional - may be <code>null</code>).
     * @param criterions
     *            One or more <code>Criterion</code> objects describing the
     *            objects to be selected.
     * @return <code>List</code> of all the entities. The returned <code>List</code> will not be
     *         <code>null</code>, but may be
     *         empty.
     */
    public List<PERSISTENTCLASS> listDistinctByCriteria(DataAccessContext daoContext,
                                                        String comment,
                                                        Order order,
                                                        Criterion... criterions)
    {
        Criteria criteria = buildCriteria(daoContext, criterions);

        if (comment != null)
        {
            criteria.setComment(comment);
        }

        if (order != null)
        {
            criteria.addOrder(order);
        }

        return listDistinct(criteria);
    }

    /**
     * Utility function that applies the <code>DISTINCT_ROOT_ENTITY</code>
     * <code>ResultTransformer</code>. This is useful if you have an entity with
     * one or more <code>&#x40;ElementCollection</code>s, since some of the
     * normal Hibernate retrieval processes will generate duplicate root
     * entities in this case.
     * 
     * @param criteria
     *            Input <code>Criteria</code>
     * @return The input <code>Criteria</code> with the transformer added
     */
    public static Criteria setDistinctRoot(Criteria criteria)
    {
        return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Return the <code>List</code> of objects that satisfy the specified <code>Criteria</code>.
     * (Utility method to handle type safety).
     * 
     * @param criteria
     *            <code>Criteria</code> object specifying the selection
     *            criteria.
     * @return <code>List</code> of persistent objects. May be empty.
     */
    @SuppressWarnings("unchecked")
    protected List<PERSISTENTCLASS> list(Criteria criteria)
    {
        return criteria.list();
    }

    /**
     * Return the <code>List</code> of objects that satisfy the specified <code>Criteria</code>.
     * (Utility method to handle type safety). This
     * method does two things: </p>
     * <ol>
     * <li>It runs the <code>Criteria</code> through the {@link #setDistinctRoot(Criteria)} utility
     * method to help handle issues associated with multiple results being returned when the class
     * in question has one or more <code>&#x40;EmbeddedCollection</code>s.</li>
     * <li>It casts the returned object to the appropriate <code>PERSISTENTCLASS</code>, thus
     * eliminating the need to do the cast in the derived class.</li>
     * </ol>
     * 
     * @param criteria
     *            <code>Criteria</code> object specifying the selection
     *            criteria.
     * @return <code>List</code> of persistent objects. May be empty.
     */
    protected List<PERSISTENTCLASS> listDistinct(Criteria criteria)
    {
        return list(setDistinctRoot(criteria));
    }

    /**
     * Return the list of objects that satisfy the specified <code>Query</code>.
     * (Utility method to handle type safety).
     * 
     * @param query
     *            <code>Query</code> object specifying retrieval.
     * @return <code>List</code> of persistent objects. May be empty.
     */
    @SuppressWarnings("unchecked")
    protected List<PERSISTENTCLASS> list(Query query)
    {
        return query.list();
    }

    /**
     * Return the list of object ids that satisfy the specified <code>Query</code>. (Utility method
     * to handle type safety).
     * 
     * @param query <code>Query</code> object specifying retrieval of <code>IDTYPE</code>.
     * @return <code>List</code> of persistent object ids. May be empty.
     */
    @SuppressWarnings("unchecked")
    protected List<IDTYPE> listIds(Query query)
    {
        return query.list();
    }

    /**
     * Utility method to return the unique entity that satisfies the specified <code>Criteria</code>
     * specification.
     * <p>
     * This method does two things:
     * </p>
     * <ol>
     * <li>It runs the <code>Criteria</code> through the {@link #setDistinctRoot(Criteria)} utility
     * method to help handle issues associated with multiple results being returned when the class
     * in question has one or more <code>&#x40;EmbeddedCollection</code>s.</li>
     * <li>It casts the returned object to the appropriate <code>PERSISTENTCLASS</code>, thus
     * eliminating the need to do the cast in the derived class.</li>
     * </ol>
     * 
     * @param criteria
     *            <code>Criteria</code> object specifying the selection
     *            criteria.
     * @return Persistent object, or <code>null</code> if no object satisfies
     *         the result.
     */
    @SuppressWarnings("unchecked")
    protected PERSISTENTCLASS uniqueResult(Criteria criteria)
    {
        return (PERSISTENTCLASS) setDistinctRoot(criteria).uniqueResult();
    }

    /**
     * Return the unique object that satisfies the specified <code>Query</code>.
     * (Utility method to handle type safety).
     * 
     * @param query
     *            <code>Query</code> object specifying retrieval.
     * @return Persistent object, or <code>null</code> if no object satisfies
     *         the result.
     */
    @SuppressWarnings("unchecked")
    protected PERSISTENTCLASS uniqueResult(Query query)
    {
        return (PERSISTENTCLASS) query.uniqueResult();
    }

    /**
     * Create a NULL safe Equals Criterion. <br>
     * This will create either an "IS NULL" or an "EQ" Criterion based on <code>value</code>. If
     * <code>value</code> is <code>null</code> then <code>Restrictions.isNull(propertyName)</code>
     * will be returned,
     * otherwise <code>Restrictions.eq(propertyName, value)</code> will be.
     * 
     * @param propertyName The property name for this restriction.
     * @param value The value to apply to the restriction (can be null)
     * @return a NULL safe Equals Criterion.
     */
    protected Criterion nullSafeEquals(String propertyName, Object value)
    {
        if (value == null)
        {
            return Restrictions.isNull(propertyName);
        }
        else
        {
            return Restrictions.eq(propertyName, value);
        }
    }

}
