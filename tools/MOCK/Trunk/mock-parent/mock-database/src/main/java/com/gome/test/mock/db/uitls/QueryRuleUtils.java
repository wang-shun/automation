package com.gome.test.mock.db.uitls;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <h3>查询规则工具类</h3>
 * @see org.hibernate.Criteria
 */
public class QueryRuleUtils {

    /**
     * 处理查询条件
     * 
     * @param criteria
     * @param queryRule
     */
    public static void createCriteriaWithQueryRule(Criteria criteria, QueryRule queryRule) {
        for (QueryRule.Rule rule : queryRule.getRuleList()) {
            switch (rule.getType()) {
                case QueryRule.BETWEEN:
                    processBetween(criteria, rule);
                    break;
                case QueryRule.EQ:
                    processEqual(criteria, rule);
                    break;
                case QueryRule.LIKE:
                    processLike(criteria, rule);
                    break;
                case QueryRule.NOTEQ:
                    processNotEqual(criteria, rule);
                    break;
                case QueryRule.GT:
                    processGreaterThen(criteria, rule);
                    break;
                case QueryRule.GE:
                    processGreaterEqual(criteria, rule);
                    break;
                case QueryRule.LT:
                    processLessThen(criteria, rule);
                    break;
                case QueryRule.LE:
                    processLessEqual(criteria, rule);
                    break;
                case QueryRule.SQL:
                    processSQL(criteria, rule);
                    break;
                case QueryRule.IN:
                    processIN(criteria, rule);
                    break;
                case QueryRule.ISNULL:
                    processIsNull(criteria, rule);
                    break;
                case QueryRule.ISNOTNULL:
                    processIsNotNull(criteria, rule);
                    break;
                case QueryRule.ISEMPTY:
                    processIsEmpty(criteria, rule);
                    break;
                case QueryRule.ISNOTEMPTY:
                    processIsNotEmpty(criteria, rule);
                    break;
                case QueryRule.OR:
                    processOr(criteria, rule);
                    break;
                case QueryRule.MAX_RESULTS:
                    break;
                case QueryRule.FIRST_RESULTS:
                    break;
                case QueryRule.ASC_ORDER:
                    break;
                case QueryRule.DESC_ORDER:
                    break;
                default:
                    throw new IllegalArgumentException("type " + rule.getType() + " not supported.");
            }
        }
        for (QueryRule subQueryRule : queryRule.getQueryRuleList()) {
            Criteria subCriteria = criteria.createCriteria(subQueryRule.getPropertyName());
            createCriteriaWithQueryRule(subCriteria, subQueryRule);
        }
    }

    /**
     * 处理排序条件
     * 
     * @param queryRule
     * @return List<Order>
     */
    public static List<Order> getOrderFromQueryRule(QueryRule queryRule) {
        List<Order> orders = new ArrayList<Order>();
        for (QueryRule.Rule rule : queryRule.getRuleList()) {
            switch (rule.getType()) {
                case QueryRule.ASC_ORDER:
                    if (StringUtils.isNotEmpty(rule.getPropertyName())) {
                        orders.add(Order.asc(rule.getPropertyName()));
                    }
                    break;
                case QueryRule.DESC_ORDER:
                    if (StringUtils.isNotEmpty(rule.getPropertyName())) {
                        orders.add(Order.desc(rule.getPropertyName()));
                    }
            }
        }
        return orders;
    }

    private static void processLike(Criteria criteria, QueryRule.Rule rule) {
        Object obj = rule.getValues()[0];

        if (obj != null) {
            String value = obj.toString();
            if (StringUtils.isNotEmpty(value)) {
                value = value.replace('*', '%');

                obj = value;
            }
        }
        criteria.add(Restrictions.like(rule.getPropertyName(), obj));
    }

    private static void processBetween(Criteria criteria, QueryRule.Rule rule) {
        criteria.add(Restrictions.between(rule.getPropertyName(), rule.getValues()[0], rule.getValues()[1]));
    }

    private static void processEqual(Criteria criteria, QueryRule.Rule rule) {
        criteria.add(Restrictions.eq(rule.getPropertyName(), rule.getValues()[0]));
    }

    private static void processNotEqual(Criteria criteria, QueryRule.Rule rule) {
        criteria.add(Restrictions.ne(rule.getPropertyName(), rule.getValues()[0]));
    }

    private static void processGreaterThen(Criteria criteria, QueryRule.Rule rule) {
        criteria.add(Restrictions.gt(rule.getPropertyName(), rule.getValues()[0]));
    }

    private static void processGreaterEqual(Criteria criteria, QueryRule.Rule rule) {
        criteria.add(Restrictions.ge(rule.getPropertyName(), rule.getValues()[0]));
    }

    private static void processLessThen(Criteria criteria, QueryRule.Rule rule) {
        criteria.add(Restrictions.lt(rule.getPropertyName(), rule.getValues()[0]));
    }

    private static void processLessEqual(Criteria criteria, QueryRule.Rule rule) {
        criteria.add(Restrictions.le(rule.getPropertyName(), rule.getValues()[0]));
    }

    private static void processSQL(Criteria criteria, QueryRule.Rule rule) {
        criteria.add(Restrictions.sqlRestriction(rule.getPropertyName()));
    }

    private static void processIsNull(Criteria criteria, QueryRule.Rule rule) {
        criteria.add(Restrictions.isNull(rule.getPropertyName()));
    }

    private static void processIsNotNull(Criteria criteria, QueryRule.Rule rule) {
        criteria.add(Restrictions.isNotNull(rule.getPropertyName()));
    }

    private static void processIsNotEmpty(Criteria criteria, QueryRule.Rule rule) {
        criteria.add(Restrictions.isNotEmpty(rule.getPropertyName()));
    }

    private static void processIsEmpty(Criteria criteria, QueryRule.Rule rule) {
        criteria.add(Restrictions.isEmpty(rule.getPropertyName()));
    }

    @SuppressWarnings("rawtypes")
    private static void processIN(Criteria criteria, QueryRule.Rule rule) {
        if ((rule.getValues().length == 1) && (rule.getValues()[0] != null) && (rule.getValues()[0] instanceof Collection)) {
            Collection coll = (Collection) rule.getValues()[0];
            if ((coll != null) && (!(coll.isEmpty()))) {
                criteria.add(Restrictions.in(rule.getPropertyName(), coll));
            }
        } else {
            criteria.add(Restrictions.in(rule.getPropertyName(), rule.getValues()));
        }
    }

    private static void processOr(Criteria criteria, QueryRule.Rule rule) {
        Object[] cts = rule.getValues();
        criteria.add(Restrictions.or((Criterion) cts[0], (Criterion) cts[1]));
    }
}