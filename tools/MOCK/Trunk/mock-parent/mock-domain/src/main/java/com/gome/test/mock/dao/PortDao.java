package com.gome.test.mock.dao;

import com.gome.test.mock.dao.base.BaseDao;
import com.gome.test.mock.model.bean.Api;
import com.gome.test.mock.model.bean.Port;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/14.
 */
@Repository
@Transactional
public class PortDao extends BaseDao<Port> {

    public List queryPortListByNumber(int portNumber)
    {
        String sql = String.format( "select ID,ENABLE,PORT_NUMBER from m_port where PORT_NUMBER = %s",portNumber);
        List<Port>   portList = this.sqlQuery(sql);
        return portList == null? new ArrayList<Port>(): portList;
    }
}
