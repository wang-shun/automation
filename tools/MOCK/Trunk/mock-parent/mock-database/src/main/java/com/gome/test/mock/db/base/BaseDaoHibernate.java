package com.gome.test.mock.db.base;

import com.gome.test.mock.db.uitls.PageInfo;
import com.gome.test.mock.db.uitls.QueryRule;
import com.gome.test.mock.db.filter.QueryResInterceptor;
import com.gome.test.mock.db.filter.ResEnumInter;
import com.gome.test.mock.db.uitls.BeanUtils;
import com.gome.test.mock.db.uitls.GenericsUtils;
import com.gome.test.mock.db.uitls.QueryRuleUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.internal.CriteriaImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <h3>基础Dao实现</h3>
 * <p> ■ 获取单个实例</p>
 * <p> ■ 获取所有实例</p>
 * <p> ■ 判断实例是否存在</p>
 * <p> ■ 保存单个实例</p>
 * <p> ■ 修改实例</p>
 * <p> ■ 删除实例（按主键删除）</p>
 * <p> ■ 删除实例（按对象删除）</p>
 * <p> ■ 根据queryRule的条件查询实例列表</p>
 * <p> ■ 根据queryRule的条件以及当前页码、每页显示条数实现分页查询实例列表</p>
 * <p> ■ 根据实体属性名、属性值查询单个实例，当查询到多条时抛出非受查异常</p>
 * <p> ■ 根据map里包含的多个属性名、属性值查询单个实例，当查询到多条时抛出非受查异常</p>
 * <p> ■ 根据queryRule查询单个实例，当查询到多条时抛出非受查异常</p>
 *
 * @param <T>  实体类型
 * @param <PK> 主键类型
 * @see Criteria
 * <p/>
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class BaseDaoHibernate<T extends Serializable, PK extends Serializable> extends EntityDaoHibernate implements BaseDao<T, PK> {
    protected Class<T> entityClass;

    public BaseDaoHibernate() {
        this.entityClass = GenericsUtils.getSuperClassGenricType(super.getClass());
    }

    public BaseDaoHibernate(Class<T> persistentClass) {
        this.entityClass = persistentClass;
    }

    @Override
    public T get(PK paramPK) {
        return (T) super.getSession().get(this.entityClass, paramPK);
    }

    @Override
    public List<T> getAll() {

        return this.find(QueryRule.getInstance());
    }

    @Override
    public boolean exists(PK paramPK) {
        Object entity = this.getSession().get(this.entityClass, paramPK);
        return (entity != null);
    }

    @Override
    public void save(T object) {
        this.getSession().saveOrUpdate(object);
    }

    @Override
    public void insert(T object) {
        this.getSession().save(object);
    }

    @Override
    public void update(T object) {
        this.getSession().update(object);
    }

    @Override
    public void deleteByPK(PK paramPK) {
        this.delete(this.get(paramPK));
    }

    @Override
    public void delete(T object) {
        this.getSession().delete(object);
    }

    @Override
    public void merge(T object) {
        this.getSession().merge(object);
    }

    @Override
    public List<T> find(QueryRule queryRule) {
        Criteria criteria = this.getSession().createCriteria(this.entityClass);
        QueryRuleUtils.createCriteriaWithQueryRule(criteria, queryRule);

        List<Order> orders = QueryRuleUtils.getOrderFromQueryRule(queryRule);
        for (Order o : orders) {
            criteria.addOrder(o);
        }
        return criteria.setFirstResult(0).list();
    }

    @Override
    public PageInfo<T> find(QueryRule queryRule, int pageNo, int pageSize) {
        ResEnumInter resEnumInter = QueryResInterceptor.keyLocal.get();
        pageNo = pageNo <= 0 ? 1 : pageNo;
        pageSize = pageSize <= 0 ? 10 : pageSize;

        PageInfo<T> pageInfo = new PageInfo<T>(pageNo, pageSize);
        String listKey = null, countKey = null;
        Object totalInCache = null;
        int totalCount = 0;
        Criteria criteria = this.getSession().createCriteria(this.entityClass);
        QueryRuleUtils.createCriteriaWithQueryRule(criteria, queryRule);
        CriteriaImpl impl = (CriteriaImpl) criteria;

        Projection projection = impl.getProjection();
        List orderEntries;
        try {
            orderEntries = (List) BeanUtils.forceGetProperty(impl, "orderEntries");
            BeanUtils.forceSetProperty(impl, "orderEntries", new ArrayList());
        } catch (Exception e) {
            throw new InternalError(" Runtime Exception impossibility throw ");
        }
        totalCount = Integer.parseInt(criteria.setProjection(Projections.rowCount()).uniqueResult() + "");
        if (totalCount < 1) {
            return pageInfo;
        }
        pageInfo.setTotalCount(totalCount);
        if (pageNo > pageInfo.getTotalPageCount()) {
            pageNo = pageInfo.getTotalPageCount();
            pageInfo.setPageNo(pageNo);
        }
        criteria.setProjection(projection);
        if (projection == null) {
            criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
        }
        try {
            BeanUtils.forceSetProperty(impl, "orderEntries", orderEntries);
        } catch (Exception e) {
            throw new InternalError(" Runtime Exception impossibility throw ");
        }

        List<Order> orders = QueryRuleUtils.getOrderFromQueryRule(queryRule);
        for (Order o : orders) {
            criteria.addOrder(o);
        }

        if (resEnumInter != null) {
            QueryResInterceptor.keyLocal.set(resEnumInter);
        }
        int startIndex = PageInfo.getStartIndex(pageNo, pageSize);
        List<T> data = criteria.setFirstResult(startIndex).setMaxResults(pageSize).list();
        pageInfo.setData(data);
        return pageInfo;
    }

    @Override
    public List<T> findList(QueryRule queryRule, int start, int count) {
        start = start < 0 ? 0 : start;
        count = count < 0 ? 0 : count;
        Criteria criteria = this.getSession().createCriteria(this.entityClass);
        QueryRuleUtils.createCriteriaWithQueryRule(criteria, queryRule);

        List<Order> orders = QueryRuleUtils.getOrderFromQueryRule(queryRule);
        for (Order o : orders) {
            criteria.addOrder(o);
        }
        List<T> result = criteria.setFirstResult(start).setMaxResults(count).list();
        return result;
    }

    @Override
    public T findUnique(String propertyName, Object value) {
        QueryRule queryRule = QueryRule.getInstance();
        queryRule.addEqual(propertyName, value);
        return this.findUnique(queryRule);
    }

    @Override
    public T findUnique(Map<String, Object> properties) {
        QueryRule queryRule = QueryRule.getInstance();
        for (Iterator iterator = properties.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            queryRule.addEqual(key, properties.get(key));
        }
        return this.findUnique(queryRule);
    }

    @Override
    public T findUnique(QueryRule queryRule) {
        List<T> list = this.find(queryRule);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        throw new IllegalStateException("findUnique return " + list.size() + " record(s).");
    }
}