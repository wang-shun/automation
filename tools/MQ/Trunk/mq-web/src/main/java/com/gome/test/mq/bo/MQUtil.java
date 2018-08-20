package com.gome.test.mq.bo;


import com.gome.test.mq.dao.LogForDBDao;
import com.gome.test.mq.model.MQ;
import com.gome.test.mq.model.MQLogInfo;
import com.gome.test.mq.model.Result;
import com.gome.test.utils.JsonUtils;
import com.ibm.mq.*;
import com.ibm.mq.pcf.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;




/**
 * Created by zhangjiadi on 15/12/17.
 */
@Component
public class MQUtil {

    public MQUtil(){

    }

    public MQ get_mq() {
        return _mq;
    }

    public void set_mq(MQ _mq) {
        this._mq = _mq;
    }

    private MQ _mq;
    private int read = 0 ;//是否读取  1 浏览
    private MQQueueManager mqmenager; // 队列管理器
    private MQQueue mqqueue; // 队列

    public MQQueueManager getMqmenager() {
        return mqmenager;
    }

    public int getOpenOptions(){
        int openOptions;
        if (read == 0) {
            // 浏览 // MQC.MQOO_BROWSE 阅读消息//MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;
            openOptions = MQC.MQOO_BROWSE |MQC.MQOO_FAIL_IF_QUIESCING| MQC.MQOO_OUTPUT | MQC.MQOO_INQUIRE;

        } else {
            // 获取
//            openOptions = MQC.MQOO_INPUT_AS_Q_DEF | MQC.MQOO_OUTPUT | MQC.MQOO_INQUIRE;
            openOptions = MQC.MQOO_FAIL_IF_QUIESCING|MQC.MQOO_INPUT_AS_Q_DEF|MQC.MQOO_OUTPUT|MQC.MQOO_INQUIRE;
        }
        return  openOptions;
    }
    public void openMqmenager()
    {

        MQEnvironment.hostname =_mq.getHostName();//  队列管理器所在主机的主机名，不区分大小写
        MQEnvironment.channel = _mq.getChannel(); // 客户机连接通道名（一般就是队列管理器下面服务器连接通道的名称）
        MQEnvironment.port = _mq.getPort();
        MQEnvironment.CCSID = _mq.getCCSID();
        MQEnvironment.userID= _mq.getMqUser().get_user();
        MQEnvironment.password=_mq.getMqUser().get_pwd();
        int openOptions =getOpenOptions ();

        try {
            // 用于快速联接/MQC.MQCNO_STANDARD_BINDING
            int options = MQC.MQCNO_STANDARD_BINDING;
            if(mqmenager==null ||! mqmenager.isConnected())
                mqmenager = new MQQueueManager(_mq.getQmgName() , options);

        } catch (MQException e) {
            System.out.println("连接不上队列服务器主机 MQException:" + e.toString());
        }

    }

    public MQQueue getMqqueue() {
        if(mqqueue==null || mqmenager==null || !mqmenager.isConnected())
            openMqmenager();
        return mqqueue;
    }

    public MQUtil(String hostName,  String qmgName, String channel, int CCSID, int port)
    {
        this._mq=new MQ(hostName,qmgName,channel,CCSID,port);

    }

    public MQUtil(MQ mq)
    {
       this._mq=mq;
    }

    public void finalizerCloseConnection() {
        try {
            if(mqqueue !=null)
                mqqueue.close();
            if(mqmenager!=null) {
                if(mqmenager.isConnected())
                    mqmenager.commit();
                mqmenager.disconnect();
            }
        } catch (MQException e) {
            System.out.println("A WebSphere MQ error occurred : Completion code " + e.completionCode + " Reason Code is " + e.reasonCode);
        }
    }
    @Autowired
    public LogForDBDao Dao;

    public static void main(String args[]) throws Exception {

        LogUtil.mqMessageLoger.info("1212121212");
        LogUtil.mqMessageLoger.error("1212121212");

        //        Logger logger1 = Logger.getLogger(LoggerUtil.class);
        Log logger2 = LogFactory.getLog("mqmessage");
//        logger1.info("info ++++++++");
//        logger1.error("error ++++++");


        logger2.info("file info ++++");
        logger2.error("file err ++++");

        return;


    }

    public String[] getQName(int MQIA_Q_TYPE)  {
        //客户端链接
        MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES_CLIENT);
        MQEnvironment.CCSID = this._mq.getCCSID();
        PCFMessageAgent agent = null;
        PCFMessage request;
        PCFMessage[] responses;
        String[] names = null;
        try {
            agent = new PCFMessageAgent(this._mq.getHostName(), this._mq.getPort(), this._mq.getChannel());
            request = new com.ibm.mq.pcf.PCFMessage(CMQCFC.MQCMD_INQUIRE_Q_NAMES);
            request.addParameter(CMQC.MQCA_Q_NAME, "*");
            request.addParameter(CMQC.MQIA_Q_TYPE, MQIA_Q_TYPE);//
            responses = agent.send(request);
            names = (String[]) responses[0].getParameterValue(CMQCFC.MQCACF_Q_NAMES);
        } catch (PCFException ex){
            LogUtil.mqErrorLoger.error(ex.getMessage());
        } catch (MQException ex) {
            LogUtil.mqErrorLoger.error(ex.getMessage());
        } catch (Exception ex) {
            LogUtil.mqErrorLoger.error(ex.getMessage());
        }

        finally {
            try {
                if (agent != null)
                    agent.disconnect();
            } catch (Exception ex) {
                LogUtil.mqErrorLoger.error(ex.getMessage());
                names = new String[0];
            }
        }

        return names;
    }



    public List getAllmessageList(String q_Name) {


        List messagelist = new ArrayList();
        try {
             /*关闭了就重新打开*/
            if(mqmenager==null || !mqmenager.isConnected()){
                openMqmenager();
                int openOptions = getOpenOptions ();
                mqqueue = mqmenager.accessQueue(q_Name, openOptions, null, null, null); // 通过队列名打开队列以便进行操作
            }
            int count = this.getMqqueue().getCurrentDepth();
            for (int i = count; i > 0; i--) {

                messagelist.add(this.receiveData(i==count));
            }
        }catch(Exception ex) {
            System.out.println(ex.toString());

        }finally {
            finalizerCloseConnection();
        }

        return messagelist;
    }

    public Result sendMessage(MQLogInfo mqLogInfo) {
        Result result=new Result(true,null);
        String q_Name=mqLogInfo.getMq_QName();
        String message=mqLogInfo.getMq_message();

        try {
             /*关闭了就重新打开*/
            if(mqmenager==null || !mqmenager.isConnected()){
                openMqmenager();
                int openOptions =MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;// 写入包含本地和远程
                mqqueue = mqmenager.accessQueue(q_Name, openOptions, null, null, null); // 通过队列名打开队列以便进行操作
            }
            MQMessage mqmessage = new MQMessage();
            mqmessage.characterSet = _mq.getCCSID();
            mqmessage.writeString(message);
            mqmessage.putApplicationName="MQ Test";
            mqmessage.userId=_mq.getMqUser().get_user();
            mqmessage.format = "MQSTR";
            mqmessage.encoding=546;


            /** 用MQPutMessageOptions放置，传递信息 */
            MQPutMessageOptions pmo = new MQPutMessageOptions();

            // 可以设置选项字段的值，指令队列管理器为消息生成新的消息ID 并将其设为MQMD 的MsgId 字段
            // pmo.options = pmo.options + MQC.MQPMO_NEW_MSG_ID;
            mqqueue.put(mqmessage, pmo);

            result.setIsError(false);
            //插入数据库
            saveDB(mqLogInfo);

        } catch (MQException ex) {
            System.out.println(ex.toString());
            result.setMessage(ex.getMessage());
        } catch (IOException ie) {
            System.out.println(ie.toString());
            result.setMessage(ie.getMessage());
        }finally {
            finalizerCloseConnection();
        }
        return result;
    }

    @Autowired
    private LogForDBDao dao;


    public void saveDB(MQLogInfo mqLogInfo)
    {
        String json=null;

        try{
            dao.save(mqLogInfo);

            json=JsonUtils.toJson(mqLogInfo);
            LogUtil.mqMessageLoger.info("插入数据 数据：" +json );
        }catch (Exception e)
        {
            if(json==null) {
                StringBuilder stringBuilder =new StringBuilder("数据转换JSON失败！MQLogInfo") ;

                stringBuilder.append(String.format("host:%s,",mqLogInfo.getMq_host()));
                stringBuilder.append(String.format("mq_port:%s,",mqLogInfo.getMq_port()));
                stringBuilder.append(String.format("mq_channel:%s,",mqLogInfo.getMq_channel()));
                stringBuilder.append(String.format("mq_jndiName:%s,",mqLogInfo.getMq_jndiName()));
                stringBuilder.append(String.format("mq_UserName:%s,",mqLogInfo.getMq_UserName()));
                stringBuilder.append(String.format("user_IP:%s,",mqLogInfo.getUser_IP()));
                stringBuilder.append(String.format("mq_useType:%s,",mqLogInfo.getMq_useType()));
                stringBuilder.append(String.format("createTime:%s,",mqLogInfo.getCreateTime()));
                stringBuilder.append(String.format("mq_QName:%s,",mqLogInfo.getMq_QName()));
                json=stringBuilder.toString();

            }
            LogUtil.mqMessageLoger.error("插入数据库失败，原因："+e.getMessage()+" 数据："+ json);
        }
    }

    public Result useMessage(MQLogInfo mqLogInfo)
    {
        Result result=new Result(true,null);
        String q_Name=mqLogInfo.getMq_QName();

        try {
             /*关闭了就重新打开*/
            if(mqmenager==null || !mqmenager.isConnected()){
                openMqmenager();
//                int openOptions =MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;// 写入包含本地和远程
                int openOptions = MQC.MQOO_INPUT_AS_Q_DEF|MQC.MQOO_OUTPUT|MQC.MQOO_INQUIRE;
                mqqueue = mqmenager.accessQueue(q_Name, openOptions, null, null, null); // 通过队列名打开队列以便进行操作
            }

            MQGetMessageOptions gmo = new MQGetMessageOptions();
            gmo.options = gmo.options + MQC.MQGMO_SYNCPOINT;//Get messages under sync point control（在同步点控制下获取消息）
            gmo.options = gmo.options + MQC.MQGMO_FAIL_IF_QUIESCING;// Fail if Qeue Manager Quiescing（如果队列管理器停顿则失败）
            gmo.waitInterval = 1000 ;

            //队列深度
            int count=mqqueue.getCurrentDepth();

            if(count >0)
            {
                MQMessage msg = new MQMessage();// 要读的队列的消息
                mqqueue.get(msg, gmo);
                System.out.println("消息的ID为："+msg.messageId);
                System.out.println("消息的大小为："+msg.getDataLength());
                String value=msg.readStringOfByteLength(msg.getDataLength());
                result.setMessage(value);
                mqLogInfo.setMq_message(value);
                System.out.println("消息的内容：\n"+value);
                System.out.println("---------------------------");
                result.setIsError(false);
            }

            //插入数据库
            saveDB(mqLogInfo);


        } catch (MQException ex) {
            System.out.println(ex.toString());
            result.setMessage(ex.getMessage());
        } catch (IOException ie) {
            System.out.println(ie.toString());
            result.setMessage(ie.getMessage());
        }finally {
            finalizerCloseConnection();
        }
        return result;
    }

    public  String format(String str)  {
        try {
            SAXReader reader = new SAXReader();
            // System.out.println(reader);
            // 注释：创建一个串的字符输入流
            StringReader in = new StringReader(str);
            Document doc = reader.read(in);
            // System.out.println(doc.getRootElement());
            // 注释：创建输出格式
            OutputFormat formater = OutputFormat.createPrettyPrint();
            //formater=OutputFormat.createCompactFormat();
            // 注释：设置xml的输出编码
            formater.setEncoding("utf-8");
            // 注释：创建输出(目标)
            StringWriter out = new StringWriter();
            // 注释：创建输出流
            XMLWriter writer = new XMLWriter(out, formater);
            // 注释：输出格式化的串到目标中，执行后。格式化后的串保存在out中。
            writer.write(doc);

            writer.close();
            System.out.println(out.toString());
            // 注释：返回我们格式化后的结果
            return out.toString();
        }catch (Exception ex)
        {

        }
        return null;
    }

    private String[] receiveData(Boolean first){
        String[] msgText = new String[4];
        try {
            MQMessage mqmessage = first? getFirstMQMessage(): getMQMessage();
            byte[] iii = new byte[mqmessage.getMessageLength()];
            mqmessage.readFully(iii); // 取出队列的消息
            String mess=null;// format(new String(iii));
            msgText[0] = mess==null? new String(iii) : mess;
            msgText[1] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(mqmessage.putDateTime.getTime());
            msgText[2] = ((MQMessage) mqmessage).userId ;
            msgText[3] = new String(mqmessage.messageId.toString());
        } catch (java.io.IOException ie) {
            System.out.println(ie.toString());
        }

        if (msgText == null || "".equals(msgText)) {
            return null;
        } else {
            return msgText;
        }
    }

    private String receiveMessage() {
        String msgText = null;
        try {
            MQMessage mqmessage = getMQMessage();
            byte[] iii = new byte[mqmessage.getMessageLength()];
            mqmessage.readFully(iii); // 取出队列的消息
            msgText= new String(iii);
        } catch (java.io.IOException ie) {
            System.out.println(ie.toString());
        }

        if (msgText == null || "".equals(msgText)) {
            return null;
        } else {
            return msgText;
        }
    }

    private MQMessage getFirstMQMessage(){
        MQMessage retrievedMessage = new MQMessage();
        try {
            retrievedMessage.characterSet = _mq.getCCSID();
            retrievedMessage.format="MQSTR";
            retrievedMessage.encoding=546;
            MQGetMessageOptions gmo = new MQGetMessageOptions();
            gmo.options=MQC.MQGMO_BROWSE_FIRST;
            // gmo.options=MQC.MQGMO_CONVERT;
//            gmo.options = MQC.MQGMO_BROWSE_NEXT;
            mqqueue.get(retrievedMessage, gmo);
        } catch (MQException ex) {
            System.out.println(ex.toString());
        }

        if (retrievedMessage == null || "".equals(retrievedMessage)) {
            return null;
        } else {
            return retrievedMessage;
        }
    }

    private MQMessage getMQMessage() {
        MQMessage retrievedMessage = new MQMessage();
        try {
            retrievedMessage.characterSet = _mq.getCCSID();
            MQGetMessageOptions gmo = new MQGetMessageOptions();
//            gmo.options=MQC.MQGMO_BROWSE_FIRST;
            // gmo.options=MQC.MQGMO_CONVERT;
            gmo.options = MQC.MQGMO_BROWSE_NEXT;
            mqqueue.get(retrievedMessage, gmo);
        } catch (MQException ex) {
            System.out.println(ex.toString());
        }

        if (retrievedMessage == null || "".equals(retrievedMessage)) {
            return null;
        } else {
            return retrievedMessage;
        }
    }




}
