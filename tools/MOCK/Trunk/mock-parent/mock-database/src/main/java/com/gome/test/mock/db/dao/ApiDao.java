package com.gome.test.mock.db.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.gome.test.mock.cnst.ConstDefine;
import com.gome.test.mock.db.base.BaseDaoHibernate;
import com.gome.test.mock.domain.bean.Api;
import com.gome.test.mock.exception.MyException;

/**
 * Created by chaizhongbao on 2015/9/25.
 */
@Component("apiDao")
public class ApiDao extends BaseDaoHibernate<Api, Integer> {

    @SuppressWarnings("unchecked")
    public List<Api> queryApis() throws MyException {
        String hql = "from Api where enable = " + ConstDefine.INT_YES;
        return (List<Api>) this.findByHql(hql, new Object[] {});
    }

    @SuppressWarnings("unchecked")
    public List<Api> queryApiList(List<Integer> hostIds) throws MyException {
        String hql = "from Api a where a.enable = " + ConstDefine.INT_YES + " and a.hostId in ( ? ) ";
        return (List<Api>) this.findByHql(hql, new Object[] { hostIds });
    }
}
