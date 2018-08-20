package com.gome.test.mock.db.uitls;

import org.hibernate.Query;

/**
 * ====================================================================
 * <h3>HQL 查询辅助工具</h3>
 * <p>构造HQL查询语句 ,可以通过设置selectCourse和fromCourse语句自由组合HQL</p>
 * <p>支持参数按名称压入</p>
 *
 * [主体类由公司原来的系统公共类com.EduStar.Dao.Hibernate.SQL.QueryHelper继承和设计]
 * -------------------------------------------------------
 * 注意： hql 必须设置一个主键 query.addOrder("id ")，否则分页会乱！
 *
 * 基类:HqlHelperBase 查询条件记忆器
 * 依赖:QueryParam 查询参数记忆器
 *注意，因此存在多个辅助类，因此2015-03-10日由刘点兵合并到这里，其他三个QueryHelper废弃了
 */

public class QueryHelper {

    /** UPDATE 子句. */
    public String updateClause = "";
    /** SELECT 子句. */
    public String selectClause = "";

    /** FROM 子句. */
    public String fromClause = "";

    /** FROM 子句 for count. */
    public String fromClauseForCount = "";

    /** WHERE 子句. */
    public String whereClause = "";

    /** ORDER BY 子句. */
    public String orderClause = "";

    /** GROUP BY 子句. */
    public String groupbyClause = "";

    /** HAVING 子句 */
    public String havingClause = "";

    /** SQL的 command语句,如 set identity off*/
    public String command = "";

    public boolean isHQL = true;

    /**
     * 添加一个 whereClause 的与(and)条件.
     * @param condition
     */
    public QueryHelper addAndWhere(String condition) {
        if (this.whereClause.length() == 0) {
            this.whereClause = " WHERE (" + condition + ")";
        } else {
            this.whereClause += " AND (" + condition + ")";
        }
        return this;
    }

    /**
     *  添加一个 whereClause 的与(or)条件.
     * @param condition
     * @return
     */
    public QueryHelper addOrWhere(String condition) {
        if (this.whereClause.length() == 0) {
            this.whereClause = " WHERE (" + condition + ")";
        } else {
            this.whereClause += " OR (" + condition + ")";
        }
        return this;
    }

    /**
     * 添加一个selectClause的因子
     * @param selectCause
     * @return
     */
    public QueryHelper addSelect(String selectCause) {

        if (this.selectClause.length() == 0) {

            this.selectClause = " SELECT " + selectCause + "";

        } else {
            this.selectClause += " , " + selectCause + "";
        }
        return this;
    }

    /**
     *
     * @param fromCause
     * @return
     */
    public QueryHelper addFrom(String fromCause) {

        if (this.fromClause.length() == 0) {

            this.fromClause = " FROM " + fromCause + "";

        } else {
            this.fromClause += " , " + fromCause + "";
        }
        return this;
    }

    /**
     * 添加一个 orderClause 的排序方式.
     * @param order - 排序的子条件，如 'id ASC'.
     */
    public QueryHelper addOrder(String order) {
        if (this.orderClause.length() == 0) {
            this.orderClause = " ORDER BY " + order;
        } else {
            this.orderClause += ", " + order;
        }
        return this;
    }

    /**
     * 返回记录总数的查询
     * 查询语句为 "SELECT COUNT(*) " + fromClause + whereClause.
     * @return
     */
    public String getTotalCountSQL() {

        String _from = this.fromClause + " " + this.whereClause;
        String _select = "SELECT COUNT(*) ";

        return _select + _from;
    }
    public String getTotalCountHQL(){
        return  this.getTotalCountSQL();
    }

    /**
     * 返回 INSERT UPDATE 操作的HQL
     * @return
     */
    public String getUpdateSQL() {

        return this.updateClause + " " + this.fromClause + " " + this.whereClause + " ";

    }

    /**
     * 返回 INSERT UPDATE 操作的HQL
     * @return
     */
    public String getUpdateHQL() {

        return this.getUpdateSQL();

    }

    /**
     * 返回HQL查询语句为 selectClause + fromClause + whereClause + orderClause.
     * @return
     */
    public String getQuerySQL() {
        return this.selectClause + " " + this.fromClause + " " + this.whereClause + " " + this.orderClause + " " + this.groupbyClause + " " + this.havingClause;

    }


    /**
     * 返回HQL查询语句为 selectClause + fromClause + whereClause + orderClause.
     * @return
     */
    public String getQueryHQL() {
        return this.getQuerySQL();

    }


    public static final int safeGetIntResult(Object v) {
        if (v == null) {
            return 0;
        }
        if (v instanceof Integer) {
            return (Integer) v;
        }
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        return 0;
    }

    public static final long safeGetLongResult(Object v) {
        if (v == null) {
            return 0;
        }
        if (v instanceof Integer) {
            return (Long) v;
        }
        if (v instanceof Long) {
            return ((Long) v).longValue();
        }
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        return 0;
    }

    /**
     * 清除查询的属性，以便重新赋属性值
     */
    public QueryHelper clear() {
        this.selectClause = "";
        this.fromClause = "";
        this.groupbyClause = "";
        this.orderClause = "";
        this.havingClause = "";
        this.whereClause = "";
        return this;
    }


    //baseQuery method

    /** 根据记忆中的查询条件，初始化 query */
    public void initQuery(Query query) {
//        for (int i = 0; i < query_param.size(); ++i)
//            query_param.get(i).setParam(query);
    }
}
