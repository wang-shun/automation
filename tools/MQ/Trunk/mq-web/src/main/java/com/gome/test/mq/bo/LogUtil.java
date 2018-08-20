package com.gome.test.mq.bo;

import com.gome.test.mq.dao.LogForDBDao;
import com.gome.test.mq.model.MQLogInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zhangjiadi on 15/12/22.
 */
@Component
public class LogUtil {

    public static Log mqMessageLoger;
    public static Log mqErrorLoger;

    static {
         mqMessageLoger =LogFactory.getLog("mqmessage");
        mqErrorLoger =LogFactory.getLog("mqerror");
    }

    public static void main(String args[]) throws Exception {


//        Log logger2 = LogFactory.getLog("mqmessage");

        mqMessageLoger.info("file info ++++");
        mqMessageLoger.error("file err ++++");

        return;

    }

    @Autowired
    LogForDBDao logDao;

    public List<MQLogInfo> getLogListByTemplate(String template)
    {
        return logDao.getLogListByTemplate(template);

    }

    public MQLogInfo getLogById(int id)
    {
        return logDao.getLogById(id);
    }


    public List<MQLogInfo> getAllLogList()
    {
        return  logDao.getAll();
    }


    public List<MQLogInfo> getLogList(String begin,String end, String template,String host,String qmgName,String channel,String port)
    {
        return  logDao.getLogListByTemplate(begin,end,template,host,qmgName,channel,port);
    }


}
