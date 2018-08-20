package com.gome.test.mock.dao;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.gome.test.mock.dao.base.BaseDao;
import com.gome.test.mock.model.bean.RequestResponseLog;

/**
 * Created by Administrator on 2015/10/14.
 */
@Repository
@Transactional
public class RequestResponseLogDao extends BaseDao<RequestResponseLog> {

    public void saveRequestResponseLog(RequestResponseLog reqResLog) {
        this.save(reqResLog);
    }

    public List queryAllListBySearch() {

        String sql = "select log.ID ,log.SESSION_ID,api.API_NAME,log.CLIENT_ADDRESS,log.REQUEST_TIME,log.RESPONSE_TIME,log.REQUEST_DATA,log.RESPONSE_DATA,log.sequency ,api.ENABLE from  \n" + " m_request_response_log as log left join " + " m_api as api on api.ID=log.API_ID order by log.REQUEST_TIME,api.ENABLE";
        List result = this.sqlQuery(sql);
        return result == null ? new ArrayList() : result;
    }

    public Object[] getLogDetailById(int logId) {
        String sql = String.format("select log.ID ,log.SESSION_ID,log.API_ID,log.CLIENT_ADDRESS,log.REQUEST_TIME,log.RESPONSE_TIME,log.REQUEST_DATA,log.RESPONSE_DATA,log.sequency " + " from m_request_response_log as log " + " where log.ID= %s ", logId);
        List result = this.sqlQuery(sql);

        if (!result.isEmpty()) {
            return (Object[]) result.get(0);
        } else {
            return null;
        }
    }
}
