package com.gome.test.mock.db.filter;

import org.hibernate.EmptyInterceptor;

/**
 * <h3>Hibernate拦截器</h3>
 *
 * @author chaizhongbao
 *
 * @see com.gome.test.mock.db.filter.ResEnumInter
 *
 */
public class QueryResInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 2332131231231L;

    public static ThreadLocal<ResEnumInter> keyLocal = new ThreadLocal<ResEnumInter>();

    public QueryResInterceptor() {}

    @Override
    public String onPrepareStatement(String sql) {

        ResEnumInter resEnumInter = keyLocal.get();
        if (resEnumInter != null) {
            sql = sql.toUpperCase().replaceAll(resEnumInter.getKey().toUpperCase(), resEnumInter.getValue().toUpperCase());
            keyLocal.set(null);
        }
        return sql;
    }

    /**
     * 设置值
     * 
     * @param resEnumInter
     */
    public static void set(ResEnumInter resEnumInter) {
        keyLocal.set(null);
        keyLocal.set(resEnumInter);
    }
}
