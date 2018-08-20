package com.gome.test.mock.db.uitls;

import org.hibernate.criterion.Criterion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <h3>查询规则对象</h3>
 * <p> ■ 添加升序</p>
 * <p> ■ 添加降序</p>
 * <p> ■ 添加NULL条件</p>
 * <p> ■ 添加NOT NULL条件</p>
 * <p> ■ 添加空条件</p>
 * <p> ■ 添加非空条件</p>
 * <p> ■ 添加like条件</p>
 * <p> ■ 添加equal条件</p>
 * <p> ■ 添加between条件</p>
 * <p> ■ 添加Or条件</p>
 * <p> ■ 添加In条件（List或Object[]）</p>
 * <p> ■ 添加不等条件</p>
 * <p> ■ 添加大于条件</p>
 * <p> ■ 添加大于或等于条件</p>
 * <p> ■ 添加小于条件</p>
 * <p> ■ 添加小于或等于条件</p>
 * <p> ■ 添加sql条件</p>
 * 
 * @see Criterion
 */
public final class QueryRule implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int LIKE = 1;
    public static final int IN = 2;
    public static final int BETWEEN = 3;
    public static final int EQ = 4;
    public static final int NOTEQ = 5;
    public static final int GT = 6;
    public static final int GE = 7;
    public static final int LT = 8;
    public static final int LE = 9;
    public static final int SQL = 10;
    public static final int ISNULL = 11;
    public static final int ISNOTNULL = 12;
    public static final int ISEMPTY = 13;
    public static final int ISNOTEMPTY = 14;
    public static final int OR = 15;
    public static final int MAX_RESULTS = 101;
    public static final int FIRST_RESULTS = 102;
    public static final int ASC_ORDER = 103;
    public static final int DESC_ORDER = 104;

    private final List<Rule> ruleList = new ArrayList<Rule>();
    private final List<QueryRule> queryRuleList = new ArrayList<QueryRule>();
    private String propertyName;

    private QueryRule() {}

    private QueryRule(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * <b>获取查询规则对象</b>
     * 
     * @return QueryRule
     */
    public static QueryRule getInstance() {
        return new QueryRule();
    }

    /**
     * <b>添加升序</b>
     * 
     * @param propertyName 属性名
     * 
     * @return QueryRule
     */
    public QueryRule addAscOrder(String propertyName) {
        this.ruleList.add(new Rule(ASC_ORDER, propertyName));
        return this;
    }

    /**
     * <b>添加降序</b>
     * 
     * @param propertyName
     * 
     * @return QueryRule
     */
    public QueryRule addDescOrder(String propertyName) {
        this.ruleList.add(new Rule(DESC_ORDER, propertyName));
        return this;
    }

    /**
     * <b>添加NULL条件</b>
     * 
     * @param propertyName
     * 
     * @return QueryRule
     */
    public QueryRule addIsNull(String propertyName) {
        this.ruleList.add(new Rule(ISNULL, propertyName));
        return this;
    }

    /**
     * <b>添加NOT NULL条件</b>
     * 
     * @param propertyName
     * 
     * @return QueryRule
     */
    public QueryRule addIsNotNull(String propertyName) {
        this.ruleList.add(new Rule(ISNOTNULL, propertyName));
        return this;
    }

    /**
     * <b>添加空条件</b>
     * 
     * @param propertyName 属性名
     * 
     * @return QueryRule
     */
    public QueryRule addIsEmpty(String propertyName) {
        this.ruleList.add(new Rule(ISEMPTY, propertyName));
        return this;
    }

    /**
     * <b>添加非空条件</b>
     * 
     * @param propertyName 属性名
     * 
     * @return QueryRule
     */
    public QueryRule addIsNotEmpty(String propertyName) {
        this.ruleList.add(new Rule(ISNOTEMPTY, propertyName));
        return this;
    }

    /**
     * <b>添加like条件</b>
     * 
     * @param propertyName 属性名
     * @param value 属性值
     * 
     * @return QueryRule
     */
    public QueryRule addLike(String propertyName, Object value) {
        this.ruleList.add(new Rule(LIKE, propertyName, new Object[] { value }));
        return this;
    }

    /**
     * <b>添加equal条件</b>
     * 
     * @param propertyName 属性名
     * @param value 属性值
     * 
     * @return QueryRule
     */
    public QueryRule addEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(EQ, propertyName, new Object[] { value }));
        return this;
    }

    /**
     * <b>添加between条件</b>
     * <p>values长度必须为2，分别存放取值区间的上限和下限值</p>
     * 
     * @param propertyName 属性名
     * @param values 属性值
     * 
     * @return QueryRule
     */
    public QueryRule addBetween(String propertyName, Object[] values) {
        this.ruleList.add(new Rule(BETWEEN, propertyName, values));
        return this;
    }

    /**
     * <b>添加Or条件</b>
     * <p>例如：queryRule.addOr(Restrictions.in("TStatus", new String[] {"1","2"}), Restrictions.and(Restrictions.eq("TStatus","3"), Restrictions.le("TRunTime", trunTime)));</p>
     * 
     * @param criterion1 条件1
     * @param criterion2 条件2
     * 
     * @return QueryRule
     */
    public QueryRule addOr(Criterion criterion1, Criterion criterion2) {
        this.ruleList.add(new Rule(OR, "", new Object[] { criterion1, criterion2 }));
        return this;
    }

    /**
     * <b>添加In条件</b>
     * <p>in里面包含的值存放在List里</p>
     * 
     * @param propertyName
     * @param values
     * 
     * @return QueryRule
     */
    public QueryRule addIn(String propertyName, Collection<? extends Object> values) {
        if (values == null || values.size() == 0) {
            this.ruleList.add(new Rule(IN, propertyName, new Object[] {}));
        } else {
            this.ruleList.add(new Rule(IN, propertyName, new Object[] { values }));
        }

        return this;
    }

    /**
     * <b>添加In条件</b>
     * <p>in里面包含的值存放在Object[]里</p>
     * 
     * @param propertyName 属性名
     * @param values 属性值
     * 
     * @return QueryRule
     */
    public QueryRule addIn(String propertyName, Object[] values) {
        if (values == null || values.length == 0) {
            values = new Object[] {};
        }

        this.ruleList.add(new Rule(IN, propertyName, values));
        return this;
    }

    /**
     * <b>添加不等条件</b>
     * 
     * @param propertyName 属性名
     * @param value 属性值
     * 
     * @return QueryRule
     */
    public QueryRule addNotEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(NOTEQ, propertyName, new Object[] { value }));
        return this;
    }

    /**
     * <b>添加大于条件</b>
     * 
     * @param propertyName 属性名
     * @param value 属性值
     * 
     * @return QueryRule
     */
    public QueryRule addGreaterThan(String propertyName, Object value) {
        this.ruleList.add(new Rule(GT, propertyName, new Object[] { value }));
        return this;
    }

    /**
     * <b>添加大于或等于条件</b>
     * 
     * @param propertyName 属性名
     * @param value 属性值
     * 
     * @return QueryRule
     */
    public QueryRule addGreaterEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(GE, propertyName, new Object[] { value }));
        return this;
    }

    /**
     * <b>添加小于条件</b>
     * 
     * @param propertyName 属性名
     * @param value 属性值
     * 
     * @return QueryRule
     */
    public QueryRule addLessThan(String propertyName, Object value) {
        this.ruleList.add(new Rule(LT, propertyName, new Object[] { value }));
        return this;
    }

    /**
     * <b>添加小于或等于条件</b>
     * 
     * @param propertyName 属性名
     * @param value 属性值
     * 
     * @return QueryRule
     */
    public QueryRule addLessEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(LE, propertyName, new Object[] { value }));
        return this;
    }

    /**
     * <b>添加sql条件</b>
     * 
     * @param sql sql语句
     * 
     * @return QueryRule
     */
    public QueryRule addSql(String sql) {
        this.ruleList.add(new Rule(SQL, sql));
        return this;
    }

    /**
     * <b>添加外连接条件</b>
     * 
     * @param propertyName 属性名
     * 
     * @return QueryRule
     */
    public QueryRule addSubQueryRule(String propertyName) {
        QueryRule queryRule = new QueryRule(propertyName);
        this.queryRuleList.add(queryRule);
        return queryRule;
    }

    /**
     * <b>获取Rule列表</b>
     * 
     * @return List<Rule>
     */
    public List<Rule> getRuleList() {
        return this.ruleList;
    }

    /**
     * <b>获取QueryRule列表</b>
     * 
     * @return List<QueryRule>
     */
    public List<QueryRule> getQueryRuleList() {
        return this.queryRuleList;
    }

    /**
     * 获取属性名
     * 
     * @return String
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * 规则对象
     * 
     * @author liangzh
     *
     * 2013年8月29日 下午4:07:25
     *
     */
    public class Rule implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int type;
        private final String propertyName;
        private Object[] values;

        public Rule(int paramInt, String paramString) {
            this.propertyName = paramString;
            this.type = paramInt;
        }

        public Rule(int paramInt, String paramString, Object[] paramArrayOfObject) {
            this.propertyName = paramString;
            this.values = paramArrayOfObject;
            this.type = paramInt;
        }

        public Object[] getValues() {
            return this.values;
        }

        public int getType() {
            return this.type;
        }

        public String getPropertyName() {
            return this.propertyName;
        }
    }
}