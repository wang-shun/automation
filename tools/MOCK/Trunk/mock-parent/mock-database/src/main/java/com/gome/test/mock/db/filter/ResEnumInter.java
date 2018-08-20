package com.gome.test.mock.db.filter;

/**
 * <h3>Hibernate拦截器枚举接口</h3>
 * @author chaizhongbao
 */
public interface ResEnumInter {

    /**
     * <b>获取原表名</b>
     * 
     * @return 原表名
     */
    public String getKey();

    /**
     * <b>获取新表名</b>
     * 
     * @return 新表名
     */
    public String getValue();
}
