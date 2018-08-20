package com.gome.test.mq.dao;

import com.gome.test.mq.dao.base.BaseDao;
import com.gome.test.mq.model.MQLogInfo;
import com.gome.test.utils.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhangjiadi on 15/12/22.
 */
@Repository
public class LogForDBDao extends BaseDao<MQLogInfo> {

    public List<MQLogInfo> getLogListByTemplate(String template) {
        String sql = String.format("select * from logformock_mqinfo where Template = '%s' order by CreateTime desc ", template);
        return sqlQuery(sql, MQLogInfo.class);
    }

    public MQLogInfo getLogById(int id) {
        String sql = String.format("select * from logformock_mqinfo where Id = %d", id);
        List logList = sqlQuery(sql, MQLogInfo.class);
        if (logList.size() == 0 || logList.isEmpty()) {
            return null;
        } else {
            return (MQLogInfo) logList.get(0);
        }
    }

    public List<MQLogInfo> getLogListByTemplate(String begin, String end, String template,String host,String qmgName,String channel,String port) {
        StringBuffer sqlBuffer = new StringBuffer();

        sqlBuffer.append(String.format("select * from logformock_mqinfo where Mq_Host='%s' and Mq_JNDIName='%s' and Mq_Channel='%s' and Mq_Port='%s' ",host,qmgName,channel,port));

        if(!StringUtils.isEmpty(begin))
        {
            sqlBuffer.append(String.format(" and CreateTime >= '%s' ", begin));
        }

        if(!StringUtils.isEmpty(end))
        {
            sqlBuffer.append(String.format(" and CreateTime < '%s' ", end));
        }


        if (!StringUtils.isEmpty(template)) {
            sqlBuffer.append(String.format(" and Template ='%s' ", template));
        }

        sqlBuffer.append(" order by CreateTime desc  ");


        String sql = sqlBuffer.toString();
        return sqlQuery(sql, MQLogInfo.class);
    }
}
