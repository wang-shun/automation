package com.gome.test.mq.Constant;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by zhangjiadi on 15/12/22.
 */
public class Constant {

    /**
     * gtp-domain properties file
     */
    public static final String APP_PROP_FILE = "/application.properties";
    public static final String MQ_USETYPE = "Mq_UseType";



    public static void main(String[] args) {
//        Map<Integer,Integer> taskIdStatusMap = new HashMap<Integer, Integer>();
//        taskIdStatusMap.put(100,30);
//        taskIdStatusMap.put(101,40);
//        taskIdStatusMap.put(102,50);
//        taskIdStatusMap.put(103,60);
//        StringBuilder whenStr = new StringBuilder();
//        StringBuilder inStr = new StringBuilder();
//        for (Map.Entry<Integer, Integer> map : taskIdStatusMap.entrySet()) {
//            whenStr.append(String.format("when %d then %d ", map.getKey(), map.getValue()));
//            inStr.append(String.format("%d,",map.getKey()));
//        }
//        whenStr.setLength(whenStr.length() - 1);
//        inStr.setLength(inStr.length() - 1);
//
//        String sql = String.format(
//                "update taskinfo\n" +
//                        "    set taskinfo.TaskStatus = case taskinfo.TaskID\n" +
//                        "        %s\n" +
//                        "    end\n" +
//                        "where taskinfo.TaskID in (%s)",whenStr.toString(),inStr.toString());
//        System.out.println(sql);

        String emailList = "tech-test-arch@yolo24.com,lizonglin@yolo24.com";
        List<String> mails = Arrays.asList(emailList.split(","));

        System.out.println(mails);
    }
}
