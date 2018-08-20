package com.gome.test.mock.db.base;

import com.gome.test.mock.db.uitls.PageInfo;
import com.gome.test.mock.db.filter.QueryResInterceptor;
import com.gome.test.mock.db.filter.ResEnumInter;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h3>复杂Dao操作</h3>
 * <p> ■ 刷新缓存</p>
 * <p> ■ 清除指定的缓存对象</p>
 * <p> ■ 清除缓存</p>
 * <p> ■ 通过hql查询实体列表</p>
 * <p> ■ 通过hql查询前top条记录</p>
 * <p> ■ 通过hql分页查询</p>
 * <p> ■ 执行hql操作</p>
 * <p> ■ 查询总条数</p>
 * <p> ■ 转换为分页对象</p>
 * <p> ■ 获取sequence</p>
 * <p> ■ 通过sql查询</p>
 * <p> ■ 通过sql分页查询</p>
 * <p/>

 */
@Component
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EntityDaoHibernate {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    /////如果要切换不同的数据源，请把下面的三行放到子类里面。将 @Qualifier("sessionFactory")这个数据修改即可
    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;
    private Transaction transaction = null;

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    /**
     * <b>获取session对象</b>
     *
     * @return Session
     */
    public Session getSession() {
        Session session = null;
        try {
            session = this.getSessionFactory().getCurrentSession();
        } catch (HibernateException ex) {
                this.logger.debug("EntityDaoHibernate无法从Spring环境中获得Session，请确保该调用是否通过了hibernateFilter】" + ex.getMessage());
        }

        if (session == null || !session.isOpen()) {
            SessionHolder sessionHolder = MySynchronizationManager.getSessionHolder();
            if (sessionHolder != null) {
                return sessionHolder.getSession();
            } else {
                session = this.getSessionFactory().openSession();
                sessionHolder = new SessionHolder(session);
                MySynchronizationManager.setSessionHolder(sessionHolder);
            }
        }
        return session;
    }

    /**
     * <b>刷新缓存</b>
     */
    public void flush() {
        this.getSession().flush();
    }

    public void beginTransaction() {
        try {
            if (this.transaction == null || !this.transaction.isActive()) {
                this.transaction = this.getSession().beginTransaction();
            }
        } catch (Exception ex) {
            this.logger.error("commit error", ex);
        }
    }

    public void commit() {
        try {
            if (this.transaction != null && this.transaction.isActive()) {
                this.transaction.commit();
            } else if (this.getSession().getTransaction() != null && this.getSession().getTransaction().isActive()) {
                this.getSession().getTransaction().commit();
            }
        } catch (Exception ex) {
            this.logger.error("commit error", ex);
        }
    }

    /**
     * <b>清除指定的缓存对象</b>
     *
     * @param obj
     */
    public void evict(Object obj) {
        this.getSession().evict(obj);
    }

    /**
     * <b>清除缓存</b>
     */
    public void clear() {
        this.getSession().clear();
    }

    /**
     * <b>通过hql查询实体列表</b>
     *
     * @param hql    hql语句
     * @param values hql对应参数值
     * @return List<?>
     */
    public List<?> findByHql(String hql, Object[] values) {
        Assert.hasText(hql);
        String newHql = this.getNewHql(hql, values);
        Query query = this.getSession().createQuery(newHql);
        return this.setParameters(query, values).list();
    }

    /**
     * <b>hql查询记录列表</b>
     * <p>根据hql查询前top条记录</p>
     *
     * @param hql    hql语句
     * @param start  开始位置
     * @param count  查询记录条数
     * @param values hql对应参数值
     * @return List<?>
     */

    public List<?> findListByHql(String hql, int start, int count, Object[] values) {
        Assert.hasText(hql);
        String newHql = this.getNewHql(hql, values);
        Query query = this.getSession().createQuery(newHql);
        this.setParameters(query, values).setFirstResult(start).setMaxResults(count);
        List<?> result = query.list();
        return result;
    }

    /**
     * <b>hql分页查询</b>
     *
     * @param hql       hql语句
     * @param pageNo    当前页码
     * @param pageSize  每页显示条数
     * @param values    hql对应参数值
     * @param countHqls 查询总条数sql
     * @return PageInfo<T>
     */
    public <T> PageInfo<T> findByHql(String hql, int pageNo, int pageSize, Object[] values, String... countHqls) {

        String countSql;
        String newHql = this.getNewHql(hql, values);
        if (countHqls == null || countHqls.length == 0) {
            countSql = (new StringBuffer(newHql.length() + 20).append(" select count(*) ").append(this.removeSelect(this.removeOrders(newHql)))).toString();
        } else {
            countSql = this.getNewHql(countHqls[0], values);
        }
        return this.findByHql(hql, pageNo, pageSize, values, countSql, values);
    }

    public <T> PageInfo<T> findByHql(String hql, int pageNo, int pageSize, Object[] values) {
        return this.findByHql(hql, pageNo, pageSize, values, null, values);
    }

    /**
     * <b>hql分页查询</b>
     *
     * @param hql         hql语句
     * @param pageNo      当前页码
     * @param pageSize    每页显示条数
     * @param values      hql对应参数值
     * @param countHql    查询总条数sql
     * @param countParams 查询总条数参数
     * @return PageInfo<T>
     */
    public <T> PageInfo<T> findByHql(String hql, int pageNo, int pageSize, Object[] values, String countHql, Object[] countParams) {
        ResEnumInter resEnumInter = QueryResInterceptor.keyLocal.get();
        Assert.hasText(hql);
        pageNo = pageNo <= 0 ? 1 : pageNo;
        pageSize = pageSize <= 0 ? 10 : pageSize;

        PageInfo<T> pageInfo = new PageInfo<T>(pageNo, pageSize);
        String newHql = this.getNewHql(hql, values);
        int totalCount = 0;

        //查询总条数
        Query countQuery = this.getSession().createQuery(this.getNewHql(countHql, countParams));

        List<?> countlist = this.setParameters(countQuery, countParams).list();
        if (countlist != null && countlist.size() > 0) {
            totalCount = Integer.parseInt(countlist.get(0) + "");
        }
        if (totalCount < 1) {
            return pageInfo;
        }
        pageInfo.setTotalCount(totalCount);
        if (pageNo > pageInfo.getTotalPageCount()) {
            pageNo = pageInfo.getTotalPageCount();
            pageInfo.setPageNo(pageNo);
        }

        //查询数据
        if (resEnumInter != null) {
            QueryResInterceptor.keyLocal.set(resEnumInter);
        }
        Query dataQuery = this.getSession().createQuery(newHql);
        List<T> data = this.setParameters(dataQuery, values).setFirstResult(PageInfo.getStartIndex(pageNo, pageSize)).setMaxResults(pageSize).list();
        pageInfo.setData(data);
        if (totalCount > 0) {
            pageInfo.setTotalCount(totalCount);
        }
        return pageInfo;
    }

    /**
     * <b>查询总条数</b>
     *
     * @param hql
     * @param values
     * @return
     */
    public long getCount(String hql, Object[] values) {
        Assert.hasText(hql);
        String newHql = this.getNewHql(hql, values);

        Query countQquery = this.getSession().createQuery(newHql);
        List<Long> countlist = this.setParameters(countQquery, values).list();

        long totalCount = 0;
        if (countlist != null && countlist.size() > 0) {
            totalCount = countlist.get(0).longValue();
        }

        return totalCount;
    }

    /**
     * <b>执行hql操作</b>
     *
     * @param hql    hql语句
     * @param values hql对应参数值
     */
    public int executeHql(String hql, Object[] values) {
        Assert.hasText(hql);
        String newHql = this.getNewHql(hql, values);
        Query dataQuery = this.getSession().createQuery(newHql);
        return this.setParameters(dataQuery, values).executeUpdate();
    }

    /**
     * <b>转换为分页对象</b>
     *
     * @param objList  对象列表
     * @param pageNo   当前页码
     * @param pageSize 每页显示条数
     * @return PageInfo<T>
     */
    public <T> PageInfo<T> pagination(List<T> objList, int pageNo, int pageSize) {
        try {
            pageNo = pageNo <= 0 ? 1 : pageNo;
            pageSize = pageSize <= 0 ? 10 : pageSize;

            List<T> objectArray = new ArrayList<T>(0);
            int startIndex = (pageNo - 1) * pageSize;
            int endIndex = pageNo * pageSize;
            for (int i = startIndex; i < endIndex; ++i) {
                objectArray.add(objList.get(i));
            }
            return new PageInfo<T>(pageNo, pageSize, objList.size(), objectArray);
        } finally {

        }
    }

    /**
     * <b>获取sequence</b>
     *
     * @param sequenceName 序列名字
     * @return sequence的下一个值
     */
    public Long getSequence(String sequenceName) {
        SQLQuery sqlQuery = this.getSession().createSQLQuery("select " + sequenceName + ".nextval from systables where tabid=1");
        List<?> list = sqlQuery.list();
        return Long.valueOf("" + list.get(0));
    }

    /**
     * <b>按sql查询</b>
     *
     * @param sql    sql语句
     * @param values sql对应参数值
     * @return List<?>
     */
    public List<?> findBySql(String sql, Object[] values) {
        Assert.hasText(sql);
        String newSql = this.getNewHql(sql, values);
        SQLQuery query = this.getSession().createSQLQuery(newSql);
        return this.setParameters(query, values).list();
    }

    /**
     * <b>sql分页查询</b>
     *
     * @param sql       sql语句
     * @param pageNo    当前页码
     * @param pageSize  每页显示条数
     * @param values    sql对应参数值
     * @param countSqls 查询总条数sql
     * @return PageInfo<T>
     */
    public <T> PageInfo<T> findBySql(String sql, int pageNo, int pageSize, Object[] values, String... countSqls) {
        return this.findBySql(sql, pageNo, pageSize, values, null, countSqls);
    }

    /**
     *
     *
     * @param sql，
     * @param pageNo
     * @param pageSize
     * @param values
     * @param claszzMap
     * @param countSqls
     * @return
     */
    public <T> PageInfo<T> findBySql(String sql, int pageNo, int pageSize, Object[] values, Map<String, Class> claszzMap, String... countSqls) {
        ResEnumInter resEnumInter = QueryResInterceptor.keyLocal.get();
        Assert.hasText(sql);
        pageNo = pageNo <= 0 ? 1 : pageNo;
        pageSize = pageSize <= 0 ? 10 : pageSize;

        PageInfo<T> pageInfo = new PageInfo<T>(pageNo, pageSize);
        String newSql = this.getNewHql(sql, values);

        int totalCount = 0;

        //查询总条数
        Query countQquery = null;
        if (countSqls == null || countSqls.length == 0) {
            StringBuffer countQueryString = new StringBuffer(newSql.length() + 20).append(" select count(*) ").append(this.removeSelect(this.removeOrders(newSql)));
            countQquery = this.getSession().createSQLQuery(countQueryString.toString());
        } else {
            countQquery = this.getSession().createSQLQuery(this.getNewHql(countSqls[0], values));
        }

        List<?> countlist = this.setParameters(countQquery, values).list();

        if (countlist != null && countlist.size() > 0) {
            totalCount = Integer.parseInt(countlist.get(0) + "");
        }
        if (totalCount < 1) {
            return pageInfo;
        }

        pageInfo.setTotalCount(totalCount);
        if (pageNo > pageInfo.getTotalPageCount()) {
            pageNo = pageInfo.getTotalPageCount();
            pageInfo.setPageNo(pageNo);
        }

        //查询数据
        if (resEnumInter != null) {
            QueryResInterceptor.keyLocal.set(resEnumInter);
        }
        SQLQuery dataQuery = this.getSession().createSQLQuery(newSql);
        if (claszzMap != null && !claszzMap.isEmpty()) {
            for (String alias : claszzMap.keySet()) {
                dataQuery.addEntity(alias, claszzMap.get(alias));
            }
        }

        List<T> data = this.setParameters(dataQuery, values).setFirstResult(PageInfo.getStartIndex(pageNo, pageSize)).setMaxResults(pageSize).list();
        pageInfo.setData(data);
        if (totalCount > 0 ) {
            pageInfo.setTotalCount(totalCount);
        }
        return pageInfo;
    }

    /**
     * <b>执行Sql操作</b>
     *
     * @param sql    sql语句
     * @param values hql对应参数值
     */
    public int executeSql(String sql, Object[] values) {
        Assert.hasText(sql);
        String newSql = this.getNewHql(sql, values);
        Query dataQuery = this.getSession().createSQLQuery(newSql);
        return this.setParameters(dataQuery, values).executeUpdate();
    }

    /**
     * 执行原生sql
     *
     * @param baseWork work子类
     */
    public void doWork(BaseWork baseWork) {
        this.getSession().doWork(baseWork);
    }

    private String removeSelect(String hql) {
        Assert.hasText(hql);
        int beginPos = hql.toLowerCase(Locale.US).indexOf("from");
        Assert.isTrue(beginPos != -1, " hql : " + hql + " must has a keyword 'from'");
        return hql.substring(beginPos);
    }

    private String removeOrders(String hql) {
        Assert.hasText(hql);
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", 2);
        Matcher m = p.matcher(hql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private String getNewHql(String hql, Object[] values) {
        String newHql = hql;
        int pos = 0;
        if (values != null) {
            for (int i = 0; i < values.length; ++i) {
                pos = newHql.indexOf(63, pos);
                if (pos == -1) {
                    break;
                }
                if (pos > -1) {
                    newHql = newHql.substring(0, pos) + ":queryParam" + i + newHql.substring(pos + 1);
                }
                pos += 1;
            }
        }
        return newHql;
    }

    private Query setParameters(Query query, Object[] values) {
        if (values != null) {
            for (int i = 0; i < values.length; ++i) {
                if (values[i] instanceof Collection) {
                    query.setParameterList("queryParam" + i, (Collection<?>) values[i]);
                } else {
                    query.setParameter("queryParam" + i, values[i]);
                }
            }
        }
        return query;
    }
}