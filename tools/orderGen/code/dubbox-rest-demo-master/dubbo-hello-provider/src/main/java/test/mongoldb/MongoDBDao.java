package test.mongoldb;

/**
 db.createCollection("ReportOwner”)
 db.ReportOwner.createIndex( { date:1,owner: 1,taskType:1 } )

 db.createCollection("ReportGroup”)
 db.ReportGroup.createIndex( { date:1,businessGroupId: 1,taskType:1 } )

 db.createCollection("TaskReport")
 db.TaskReport.createIndex( {taskId: 1})
 db.TaskReport.createIndex( {date:1 ,taskType:1} )

 db.createCollection("CaseRunTime")
 db.CaseRunTime.createIndex( { caseName:1 } )
 */


import com.gome.test.gtp.dao.AppConfiguration;
import com.gome.test.gtp.model.*;
import com.gome.test.gtp.utils.Constant;
import com.gome.test.utils.Logger;
import com.gome.test.utils.StringUtils;
import com.mongodb.*;
import com.mongodb.util.JSON;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.testng.annotations.Test;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.*;


@Component
public class MongoDBDao {

    @Autowired
    private AppConfiguration env;


    private MongoTemplate mongoTemplate;
    private static final String DEFAULT_ADDRESS = "10.144.32.129";
    private static final int DEFAULT_PORT = 27017;
    private String JMTREPORT_NAME = "";
    private String LOADREPORT_NAME = ""; 
    private String JMTAGGREPORT_NAME = "";
    private String JOBREPORT_NAME = "";

    @PostConstruct
    private synchronized void init() {
        if (mongoTemplate == null) {
            String address = env.getProperty("mongodb.address") == null ? DEFAULT_ADDRESS : env.getProperty("mongodb.address");
            int port = env.getProperty("mongodb.port") == null ? DEFAULT_PORT : Integer.valueOf(env.getProperty("mongodb.port"));
            JMTREPORT_NAME = env.getProperty("jmt.report.environment").equals("test") ? Constant.TESTJMT_REPORT : Constant.JMT_REPORT;
            LOADREPORT_NAME = "TaskLoadReport";
            JMTAGGREPORT_NAME = "JMTAGGReport";
            JOBREPORT_NAME = "JobReport";
            init(address, port);
        }
    }

    public void init(String address, int port) {
        if (mongoTemplate == null) {
            try {
                mongoTemplate = new MongoTemplate(
                        new SimpleMongoDbFactory(new MongoClient(address, port), Constant.MONGODB_NAME), null);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    public WriteResult insert(String json, String collectionName) {
        return mongoTemplate.getCollection(collectionName).insert((DBObject) JSON.parse(json));
    }
    public WriteResult insert(JSON json , String collectionName){
        return mongoTemplate.getCollection(collectionName).insert((DBObject) json);
    }

    public WriteResult insertList(List<? extends Object> objectList) {
        WriteResult wr = null;
        if (objectList == null || objectList.size() == 0) {
            return wr;
        }
        List<DBObject> list = objectToDBObjectList(objectList);
        if (list != null && list.size() > 0) {
            wr = mongoTemplate.getCollection(objectList.get(0).getClass().getSimpleName()).insert(list);
        }
        return wr;
    }

    public WriteResult insert(Object object) {
        WriteResult wr = null;
        if (object == null) {
            return wr;
        }

        DBObject obj = objectToDBObject(object);
        if (obj != null)
            wr = mongoTemplate.getCollection(object.getClass().getSimpleName()).insert(obj);

        return wr;
    }

    public WriteResult delete(String collectionName, DBObject query) {
        WriteResult wr = null;
        if (query == null || collectionName == null || collectionName.isEmpty()) {
            return wr;
        }
        return mongoTemplate.getCollection(collectionName).remove(query);
    }

    public BasicDBList executeCommand(String command) {
        Logger.info(command);


        return (BasicDBList) mongoTemplate.executeCommand(command).get("result");
    }

    private static DBObject getDbObject(Field[] fields, Object object) throws IllegalAccessException {
        DBObject dbObject = new BasicDBObject();
        for (Field field : fields) {
            field.setAccessible(true);
            dbObject.put(field.getName(), field.get(object));
        }
        return dbObject;
    }

    private String convertArrayToString(Object[] groups, String contract) {
        if (groups == null || groups.length == 0)
            return "";

        StringBuffer strB = new StringBuffer();
        for (Object i : groups) {
            if (strB.length() > 0)
                strB.append(contract);

            if (i instanceof Integer)
                strB.append(i);
            else
                strB.append(String.format("\"%s\"", i));
        }
        return strB.toString();
    }

    /**
     * 适用于按照Group和Owner查询报表
     *
     * @param minDate         最小日期 格式yyyyMMdd
     * @param maxDate         最大日期 格式yyyyMMdd
     * @param taskType        任务类型
     * @param collectionName  mongodb 集合名称
     * @param groupField      groupby的列
     * @param groupFieldValue groupby查询限制
     * @return <groupid,<date:<caseNum/passNum,numValue>>>
     */
    public HashMap<Object, HashMap<Integer, HashMap<String, Integer>>> getReportResult(
            int minDate, int maxDate, Integer[] taskType, String collectionName, String groupField, Object[] groupFieldValue) {
        String command = String.format("{aggregate:\"%s\",pipeline:[\n" +
                        "  　{\n" +
                        "    $match: {\n" +
                        "      date: {\n" +
                        "        $gte: %d,\n" +
                        "        $lte: %d\n" +
                        "      },\n" +
                        "      %s: {$in: [%s]},%s:{$in:[%s]}\n" +
                        "      }\n" +
                        "  },\n" +
                        "  {\n" +
                        "    $group: {\n" +
                        "      _id: {\n" +
                        "        date: \"$date\",\n" +
                        "        %s: \"$%s\"\n" +
                        "      },\n" +
                        "      caseNum: {\n" +
                        "        $sum: \"$caseNum\"\n" +
                        "      },\n" +
                        "      passNum: {\n" +
                        "        $sum: \"$passNum\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }\n" +
                        "]}", collectionName, minDate, maxDate, groupField, convertArrayToString(groupFieldValue, ","),
                Constant.TASK_TYPE, convertArrayToString(taskType, ","), groupField, groupField
        );

        BasicDBList commandResult = this.executeCommand(command);
        HashMap<Object, HashMap<Integer, HashMap<String, Integer>>> result = new HashMap<Object, HashMap<Integer, HashMap<String, Integer>>>();

        for (Object obj : commandResult) {
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;

            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            Object groupOrOwner = id.get(groupField);
            if (groupOrOwner instanceof Double)
                groupOrOwner = ((Double) groupOrOwner).intValue();

            Integer date = Integer.valueOf(id.get(Constant.DATE).toString());
            if (result.containsKey(groupOrOwner) == false) {
                HashMap<Integer, HashMap<String, Integer>> groupMap = new HashMap<Integer, HashMap<String, Integer>>();
                groupMap.put(date, getDateMap(kv));
                result.put(groupOrOwner, groupMap);
            } else {
                result.get(groupOrOwner).put(date, getDateMap(kv));
            }
        }

        System.out.println(result);
        return result;
    }

    public HashMap<Object, HashMap<Integer, HashMap<String, Integer>>> getReportResult(
            int minDate, int maxDate, Integer[] group, String[] owner, Integer[] taskType, String collectionName, String groupField) {
//        String command = String.format("{\n" +
//                        "    \"aggregate\":\"%s\",\n" +
//                        "    \"pipeline\":[\n" +
//                        "        {\n" +
//                        "            $match:{\n" +
//                        "                date:{\n" +
//                        "                    $gte:%d,$lte:%d\n" +
//                        "                    },\n" +
//                        "                groupId:{\n" +
//                        "                    $in:[%s]\n" +
//                        "                    },\n" +
//                        "                owner:{\n" +
//                        "                    $in:[%s]\n" +
//                        "                    },\n" +
//                        "                taskType:{\n" +
//                        "                    $in:[%s]\n" +
//                        "                    }\n" +
//                        "                }\n" +
//                        "        },\n" +
//                        "        {\n" +
//                        "            $group:{\n" +
//                        "                _id:{\n" +
//                        "                    date:\"$date\"\n" +
//                        "                    %s:\"$%s\"\n" +
//                        "                    },\n" +
//                        "                caseNum:{\n" +
//                        "                    $sum:\"$caseNum\"\n" +
//                        "                    },\n" +
//                        "                passNum:{\n" +
//                        "                    $sum:\"$passNum\"\n" +
//                        "                    }\n" +
//                        "                }\n" +
//                        "        }\n" +
//                        "    ]\n" +
//                        "}", collectionName, minDate, maxDate, convertArrayToString(group, ","), convertArrayToString(owner, ","), convertArrayToString(taskType, ","),groupField,groupField);
        String command = String.format("{\n" +
                "    \"aggregate\": \"%s\",\n" +
                "    \"pipeline\": [\n" +
                "        {\n" +
                "            \"$match\": {\n" +
                "                \"date\": {\n" +
                "                    \"$gte\": %d,\n" +
                "                    \"$lte\": %d\n" +
                "                },\n" +
                "                \"groupId\": {\n" +
                "                    \"$in\": [%s]\n" +
                "                },\n" +
                "                \"owner\": {\n" +
                "                    \"$in\": [%s]\n" +
                "                },\n" +
                "                \"taskType\": {\n" +
                "                    \"$in\": [%s]\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"$group\": {\n" +
                "                \"_id\": {\n" +
                "                    \"date\": \"$date\",\n" +
                "                    \"%s\": \"$%s\"\n" +
                "                },\n" +
                "                \"caseNum\": {\n" +
                "                    \"$sum\": \"$caseNum\"\n" +
                "                },\n" +
                "                \"passNum\": {\n" +
                "                    \"$sum\": \"$passNum\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}", collectionName, minDate, maxDate, convertArrayToString(group, ","), convertArrayToString(owner, ","), convertArrayToString(taskType, ","), groupField, groupField);

        BasicDBList commandResult = this.executeCommand(command);
        HashMap<Object, HashMap<Integer, HashMap<String, Integer>>> result = new HashMap<Object, HashMap<Integer, HashMap<String, Integer>>>();

        for (Object obj : commandResult) {
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;

            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            Object groupOrOwner = id.get(groupField);
            if (groupOrOwner instanceof Double)
                groupOrOwner = ((Double) groupOrOwner).intValue();

            Integer date = Integer.valueOf(id.get(Constant.DATE).toString());
            if (result.containsKey(groupOrOwner) == false) {
                HashMap<Integer, HashMap<String, Integer>> groupMap = new HashMap<Integer, HashMap<String, Integer>>();
                groupMap.put(date, getDateMap(kv));
                result.put(groupOrOwner, groupMap);
            } else {
                result.get(groupOrOwner).put(date, getDateMap(kv));
            }
        }

        System.out.println(result);
        return result;
    }

    /**
     * 将指定时间和tasktype的数据 从taskreport汇总并更新至  ReportOwner
     *
     * @param minDate
     * @param maxDate
     * @param taskType
     */
    public void updateReportOwner(int minDate, int maxDate, int taskType) {
        BasicDBList list = getTaskReportGroupedByOwner(minDate, maxDate, taskType);

        if (list != null && list.isEmpty() == false) {
            WriteResult result = deleteReport(Constant.REPORT_OWNER, minDate, maxDate, taskType);

            if (result.getLastError().containsKey("ok") && result.getLastError().get("ok").toString().equals("1")) {
                this.insertList(convertToReportOwner(list));
            }
        }
    }


    /**
     * 将ReportOwner汇总至ReportGroup
     *
     * @param minDate
     * @param maxDate
     * @param taskType
     * @param taskIdAndGroupId taskid与Groupid 的mapping
     */
    public void updateReportGroup(int minDate, int maxDate, int taskType, Map<Integer, Integer> taskIdAndGroupId) {
        BasicDBList list = getTaskReportGroupedByGroup(minDate, maxDate, taskType);

        if (list != null && list.isEmpty() == false) {
            WriteResult result = deleteReport(Constant.REPORT_GROUP, minDate, maxDate, taskType);

            if (result.getLastError().containsKey("ok") && result.getLastError().get("ok").toString().equals("1")) {
                this.insertList(convertToReportGroup(list, taskIdAndGroupId));
            }
        }
    }

    /**
     * @param commandResult    示例 { "_id" : { "date" : 20150430,"taskType" : 1,"taskId" : 5140},"caseNum" : 34,"passNum" : 30}
     * @param taskIdAndGroupId key:taskid value:groupid
     * @return
     */
    private List<ReportGroup> convertToReportGroup(BasicDBList commandResult, Map<Integer, Integer> taskIdAndGroupId) {
        //多个TaskId 如果Groupid相同 需要汇总到一行记录中
        HashMap<String, ReportGroup> groupIdAndReportGroup = new HashMap<String, ReportGroup>();

        for (Object obj : commandResult) {
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;

            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            int taskId = Integer.valueOf(id.get(Constant.TASK_ID).toString());
            int businessGroupId = -1;
            if (taskIdAndGroupId.containsKey(taskId)) {
                businessGroupId = taskIdAndGroupId.get(taskId);
            } else {
                Logger.error(String.format("taskId : %d 找不到group defalut:Arch-Test(7)", taskId));
                businessGroupId = 7;
            }

            String key = String.format("%s_%s_%s", id.get(Constant.DATE), businessGroupId, id.get(Constant.TASK_TYPE));

            if (groupIdAndReportGroup.containsKey(key)) {
                ReportGroup reportGroup = groupIdAndReportGroup.get(key);
                reportGroup.setCaseNum(reportGroup.getCaseNum() + Integer.valueOf(kv.get(Constant.CASE_NUM).toString()));
                reportGroup.setPassNum(reportGroup.getPassNum() + Integer.valueOf(kv.get(Constant.PASS_NUM).toString()));
            } else {
                ReportGroup reportGroup = new ReportGroup();
                reportGroup.setDate(Integer.valueOf(id.get(Constant.DATE).toString()));
                reportGroup.setTaskType(Integer.valueOf(id.get(Constant.TASK_TYPE).toString()));
                reportGroup.setCaseNum(Integer.valueOf(kv.get(Constant.CASE_NUM).toString()));
                reportGroup.setPassNum(Integer.valueOf(kv.get(Constant.PASS_NUM).toString()));
                reportGroup.setBusinessGroupId(businessGroupId);

                groupIdAndReportGroup.put(key, reportGroup);
            }
        }

        return new ArrayList<ReportGroup>(groupIdAndReportGroup.values());
    }


    @Test
    public void aa() {
        getTaskReportGroupedByGroup(10, 20, 1);
    }

    /**
     * @param minDate
     * @param maxDate
     * @param taskType
     * @return 示例 { "_id" : { "date" : 20150430,"taskType" : 1,"taskId" : 5140},"caseNum" : 34,"passNum" : 30}
     */
    private BasicDBList getTaskReportGroupedByGroup(int minDate, int maxDate, int taskType) {
        String command = String.format("{\n" +
                "  aggregate: \"%s\",\n" +
                "  pipeline: [\n" +
                "    {\n" +
                "      \"$match\": {\n" +
                "        \"$and\": [\n" +
                "          {\n" +
                "            \"date\": {\n" +
                "              \"$gte\": %d,\n" +
                "              \"$lte\": %d\n" +
                "            }\n" +
                "          }%s\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"$group\": {\n" +
                "        \"_id\": {\n" +
                "          \"date\": \"$date\",\n" +
                "          \"taskType\": \"$taskType\",\n" +
                "          \"taskId\": \"$taskId\"\n" +
                "        },\n" +
                "        \"caseNum\": {\n" +
                "          \"$sum\": \"$totalCases\"\n" +
                "        },\n" +
                "        \"passNum\": {\n" +
                "          \"$sum\": \"$pass\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}", Constant.TASK_REPORT, minDate, maxDate, taskType > 0 ? String.format(",{taskType:%d}", taskType) : "");

        return executeCommand(command);
    }


    private BasicDBList getTaskReport(int minDate, int maxDate, int taskType) {
        String command = String.format("{aggregate:\"%s\",pipeline:[\n" +
                "  {\n" +
                "    \"$match\": {\n" +
                "      \"$and\": [\n" +
                "        {\n" +
                "          \"date\": {\n" +
                "            \"$gte\": %d,\n" +
                "            \"$lt\": %d\n" +
                "          }\n" +
                "        }%s\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"$unwind\": \"$details\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"$project\": {\n" +
                "      \"_id\": 0,\n" +
                "      \"date\": 1,\n" +
                "      \"taskType\": 1,\n" +
                "      \"taskId\": 1,\n" +
                "      \"testResult\": \"$details.testResult\",\n" +
                "      \"owner\": \"$details.owner\" , \n" +
                "      \"testCaseName\":\"$details.testCaseName\" " +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"$group\": {\n" +
                "      \"_id\": {\n" +
                "        \"date\": \"$date\",\n" +
                "        \"taskType\": \"$taskType\",\n" +
                "        \"taskId\":\"$taskId\" , \n " +
                "        \"owner\": \"$owner\",\n" +
                "        \"testCaseName\":\"$testCaseName\" , \n" +
                "        \"testResult\": \"$testResult\" " +
                "      },\n" +
                "      \"caseNum\": {\n" +
                "        \"$sum\": 1\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "]}", Constant.TASK_REPORT, minDate, maxDate, taskType > 0 ? String.format(",{taskType:%d}", taskType) : "");

        return executeCommand(command);


    }

    private WriteResult deleteReport(String collectionName, int minDate, int maxDate, int taskType) {
        List<BasicDBObject> array = new ArrayList<BasicDBObject>();
        appendDateFilter(array, minDate, maxDate);

        BasicDBObject query = new BasicDBObject();
        query.put(Constant.DATE, new BasicDBObject("$lte", maxDate).append("$gte", minDate));

        if (taskType > 0)
            query.put(Constant.TASK_TYPE, taskType);

        Logger.info(String.format("db.%s.remove(%s)", collectionName, query.toString()));

        return mongoTemplate.getCollection(collectionName).remove(query);
    }

    /**
     * BasicDBList 转化为ReportOwner
     *
     * @param commandResult 结构如 { "_id" : { "date" : 20150504, "taskType" : 1, "owner" : "Onnwe5", "testResult" : "pass" }, "caseNum" : 121 }
     * @return
     */
    private List<ReportOwner> convertToReportOwner(BasicDBList commandResult) {
        //Key:date_tasktype_owner
        HashMap<String, ReportOwner> reportOwners = new HashMap<String, ReportOwner>();

        for (Object obj : commandResult) {
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;

            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            int date = Integer.valueOf(id.get(Constant.DATE).toString());
            int taskType = Integer.valueOf(id.get(Constant.TASK_TYPE).toString());
            String owner = id.get(Constant.OWNER).toString();
            String testResult = id.get(Constant.TEST_RESULT).toString();
            int caseNum = Integer.valueOf((kv.get(Constant.CASE_NUM)).toString());
            String key = String.format("%s_%s_%s", date, taskType, owner);

            if (reportOwners.containsKey(key)) {//继续追加fail的数量并set pass的数量
                ReportOwner reportOwner = reportOwners.get(key);

                if (testResult.equals(Constant.PASS))
                    reportOwner.setPassNum(caseNum);

                reportOwner.setCaseNum(reportOwner.getCaseNum() + caseNum);
            } else {
                ReportOwner reportOwner = new ReportOwner();
                reportOwner.setOwner(owner);
                reportOwner.setDate(date);
                reportOwner.setTaskType(taskType);

                reportOwner.setCaseNum(caseNum);

                if (testResult.equals(Constant.PASS))
                    reportOwner.setPassNum(caseNum);

                reportOwners.put(key, reportOwner);
            }
        }

        return new ArrayList<ReportOwner>(reportOwners.values());
    }

    @Test
    public void aab() {
        getTaskReportGroupedByOwner(10, 20, 1);
    }

    /**
     * 从TaskReport按照Owner聚合
     *
     * @param minDate
     * @param maxDate
     * @param taskType
     * @return 结构如 { "_id" : { "date" : 20150504, "taskType" : 1, "owner" : "Onnwe5", "testResult" : "pass" }, "caseNum" : 121 }
     */
    private BasicDBList getTaskReportGroupedByOwner(int minDate, int maxDate, int taskType) {
        String command = String.format("{aggregate:\"%s\",pipeline:[\n" +
                "  {\n" +
                "    \"$match\": {\n" +
                "      \"$and\": [\n" +
                "        {\n" +
                "          \"date\": {\n" +
                "            \"$gte\": %d,\n" +
                "            \"$lte\": %d\n" +
                "          }\n" +
                "        }%s\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"$unwind\": \"$details\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"$project\": {\n" +
                "      \"_id\": 0,\n" +
                "      \"date\": 1,\n" +
                "      \"taskType\": 1,\n" +
                "      \"testResult\": \"$details.testResult\",\n" +
                "      \"owner\": \"$details.owner\"\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"$group\": {\n" +
                "      \"_id\": {\n" +
                "        \"date\": \"$date\",\n" +
                "        \"taskType\": \"$taskType\",\n" +
                "        \"owner\": \"$owner\",\n" +
                "        \"testResult\": \"$testResult\"\n" +
                "      },\n" +
                "      \"caseNum\": {\n" +
                "        \"$sum\": 1\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "]}", Constant.TASK_REPORT, minDate, maxDate, taskType > 0 ? String.format(",{taskType:%d}", taskType) : "");

        return executeCommand(command);
    }

    private static HashMap<String, Integer> getDateMap(HashMap<String, Object> kv) {
        HashMap<String, Integer> dateMap = new HashMap<String, Integer>();
        dateMap.put(Constant.CASE_NUM, Integer.valueOf(kv.get(Constant.CASE_NUM).toString()));
        dateMap.put(Constant.PASS_NUM, Integer.valueOf(kv.get(Constant.PASS_NUM).toString()));
        return dateMap;
    }


    public static <T> DBObject objectToDBObject(T object) {
        if (object == null)
            return null;

        Class objectClass = object.getClass();
        Field[] fields = objectClass.getDeclaredFields();

        try {
            return getDbObject(fields, object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<DBObject> objectToDBObjectList(List<? extends Object> objectList) {
        List<DBObject> dbObjectList = new ArrayList<DBObject>();

        if (objectList != null && objectList.size() > 0) {
            Class objectClass = objectList.get(0).getClass();
            Field[] fields = objectClass.getDeclaredFields();
            for (Object object : objectList) {
                try {
                    dbObjectList.add(getDbObject(fields, object));
                } catch (Exception e) {
                    continue;
                }
            }
        }

        return dbObjectList;
    }


    private static void appendDateFilter(List<BasicDBObject> array, int minDate, int maxDate) {
        array.add(new BasicDBObject(Constant.DATE, new BasicDBObject("$lte", maxDate).append("$gte", minDate)));
    }

    private static void appendInFilter(List<BasicDBObject> array, int[] fieldValue, String field) {
        if (fieldValue != null && fieldValue.length > 0) {
            BasicDBList groupCondition = new BasicDBList();
            for (Integer g : fieldValue)
                groupCondition.add(g);

            array.add(new BasicDBObject(field, new BasicDBObject("$in", groupCondition)));
        }
    }

    private static void appendEqualsFilter(List<BasicDBObject> array, String propName, int value) {
        array.add(new BasicDBObject(propName, value));
    }

    /**
     * 构造group
     *
     * @param groupByField group by 的字段 即_id后面的字段
     * @param fieldAndFunc 除_id之外的部分
     * @return
     */
    private static DBObject generateGroupField(String[] groupByField, HashMap<String, String> fieldAndFunc) {
        if (groupByField != null && groupByField.length > 0) {
            DBObject groupFields = generateGroupField(groupByField);

            for (Map.Entry<String, String> kv : fieldAndFunc.entrySet()) {
                groupFields.put(removePrefix(kv.getKey(), "$"), new BasicDBObject(appendPrefix(kv.getValue(), "$"), appendPrefix(kv.getKey(), "$")));
            }
            return new BasicDBObject("$group", groupFields);
        } else {
            return null;
        }
    }

    private static DBObject generateGroupField(String[] groupByField) {
        if (groupByField != null && groupByField.length > 0) {
            DBObject fields = new BasicDBObject();

            for (String f : groupByField) {
                if (f != null && f.isEmpty() == false)
                    fields.put(removePrefix(f, "$"), appendPrefix(f, "$"));
            }
            DBObject groupFields = new BasicDBObject("_id", fields);

            return groupFields;
        } else {
            return null;
        }
    }

    private static String removePrefix(String value, String prefix) {
        if (value.startsWith(prefix) == false)
            return value;
        else
            return value.substring(prefix.length());
    }

    private static String appendPrefix(String value, String prefix) {
        if (value.equals("1"))
            return value;

        if (value.startsWith(prefix))
            return value;
        else
            return String.format("%s%s", prefix, value);
    }

    public <T> List<T> getAll(Class<T> entityClass) {
        return mongoTemplate.findAll(entityClass, entityClass.getSimpleName());
    }

    public <T> List<T> getByCondition(Class<T> entityClass, Query query) {
        return mongoTemplate.find(query, entityClass, entityClass.getSimpleName());
    }

    public <T> T getOneByCondition(Class<T> entityClass, Query query) {
        return mongoTemplate.findOne(query, entityClass, entityClass.getSimpleName());
    }

    public List getDistinct(String collectionName, String key) {
        return mongoTemplate.getCollection(collectionName).distinct(key);
    }

    public List getJMTSceneNameList() {

        String command = String.format("\n" +
                "{aggregate: \"%s\",pipeline: [{\"$match\": { isEnable: true }},{\"$group\": {\"_id\":  \"$sceneName\"}}]}", JMTREPORT_NAME);

        return getListByAggregateCommand(command);
    }

    public HashMap<String, List<String>> getJMTSceneNameMap(String template, Boolean isEnable) {
        HashMap<String, List<String>> resultList = new HashMap<String, List<String>>();

        if (!StringUtils.isEmpty(template))
            template = String.format(",template:\"%s\"", template);

        String command = String.format("{aggregate: \"%s\",pipeline:  [{\"$match\": {  isEnable: %s %s}},{\"$group\": {\"_id\":{ \"sceneName\":\"$sceneName\",\"taskid\":\"$taskid\"}}}]}",
                JMTREPORT_NAME, isEnable, template
        );

        BasicDBList commandResult = executeCommand(command);

        for (Object obj : commandResult) {
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);

            String key = id.getString("taskid");
            if (!resultList.containsKey(key))
                resultList.put(key, new ArrayList<String>());
            resultList.get(key).add(id.getString("sceneName"));

        }

        return resultList;
    }


    public List getJMTSmallSceneNameList(String sceneName, Boolean isEnable) {
        String command = String.format("\n" +
                "{aggregate: \"%s\",pipeline: [{\"$match\": { \"$and\":[{isEnable: %s ,sceneName:\"%s\"}]}},{\"$group\": {\"_id\":  \"$smallSceneName\" }},{ \"$sort\": { \"smallSceneName\": 1}}]}", JMTREPORT_NAME, isEnable, sceneName);

        return getListByAggregateCommand(command);
    }

    public List getJMTLabelNameList(String sceneName, Boolean isEnable) {
        String command = String.format("{aggregate: \"%s\",pipeline: [{\"$match\": { \"$and\": [{isEnable: %s,sceneName: \"%s\"} ] }},{\"$group\": {\"_id\":  \"$labelName\" }},{ $sort: { labelName: 1}} ]}", JMTREPORT_NAME, isEnable, sceneName);

        return getListByAggregateCommand(command);
    }

    //根据command查询，将结果存入到List中
    private List getListByAggregateCommand(String command) {
        BasicDBList commandResult = executeCommand(command);
        List resultList = new ArrayList<Object>();
        for (Object obj : commandResult) {
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            resultList.add(kv.get(Constant._ID));
        }
        Collections.sort(resultList);
        return resultList;
    }

    public List getJMTenvironmentList(String sceneName, Boolean isEnable) {
        sceneName = StringUtils.isEmpty(sceneName) ? "" : String.format(" , sceneName: \"%s\"", sceneName);
        String command = String.format("{aggregate: \"%s\",pipeline: [{\"$match\": { \"$and\": [{isEnable: %s %s} ] }},{\"$group\": {\"_id\":  \"$environment\" }} ]}", JMTREPORT_NAME, isEnable, sceneName);
        return getListByAggregateCommand(command);
    }

    /**
     * get report info from mongo by taskId
     * @param taskId
     * @return
     */
    public List<JobReport> getJobList(String taskId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("taskId").is(taskId));
        List<JobReport> list = mongoTemplate.find(query , JobReport.class , JOBREPORT_NAME);
        return  list;
    }

    //verify split job task if total case is pass then return true.
    public boolean getJobPassTest(String taskId) {
        boolean isPass = false;
        Query query = new Query();
        query.addCriteria(Criteria.where("taskId").is(taskId));
        List<JobReport> list = mongoTemplate.find(query , JobReport.class , JOBREPORT_NAME);
        if(list!=null && list.size()>0){
            JobReport report = list.get(0);
            if(report.getTotalCases() >0 && (report.getTotalCases() == report.getPass())){
                isPass = true;
            }
        }
        return  isPass;
    }

    /**
     * search all split test case , when one of them fail test then fail all .
     * @param queueList
     * @return
     */
    public boolean getAllIsPassTest(List<ResponseQueue> queueList){
       boolean isAllPass = false;
        if(queueList != null && queueList.size()>0){
            for(ResponseQueue queue : queueList){
                isAllPass = getJobPassTest(queue.getJobName());
                if(!isAllPass){
                    //if one of split app fail then break.
                    break;
                }
            }
        }
        return isAllPass;
    }
    public List<JMTReport> getJMTLabelNameList(String resultVersion, String resultEndVerison, String environment, String sceneName, String smallSceneName, String template, boolean isEnable) {

        smallSceneName = StringUtils.isEmpty(smallSceneName) ? "" : String.format(",smallSceneName:\"%s\"", smallSceneName);
        template = StringUtils.isEmpty(template) ? "" : String.format(",template:\"%s\"", template);

        String command = String.format("{aggregate: \"%s\",pipeline:  [{\"$match\": { \"$and\": [{ resultVersion:{$gte:\"%s\", $lte:\"%s\"},isEnable:%s,environment:\"%s\",sceneName: \"%s\"  %s %s } ] }},{\"$group\": {\"_id\": \"$labelName\"}}]}",
                JMTREPORT_NAME, resultVersion, resultEndVerison, isEnable, environment, sceneName, smallSceneName, template
        );


        return getListByAggregateCommand(command);
    }


    public String getEndVersion(String sceneName, String smallSceneName, String labelName, String environment, Boolean isEnable) {
        smallSceneName = smallSceneName == "" ? "" : String.format(",smallSceneName:\"%s\"", smallSceneName);
        labelName = labelName == "" ? "" : String.format(",labelName:\"%s\"", labelName);
        environment = environment == "" ? "" : String.format(",environment:\"%s\"", environment);
        String command = String.format("{aggregate: \"%s\",pipeline: [{\"$match\": { \"$and\": [{isEnable: %s,sceneName: \"%s\" %s %s %s} ] }},{\"$group\": {\"_id\":  \"$resultVersion\" }}]}",
                JMTREPORT_NAME, isEnable, sceneName, smallSceneName, labelName, environment);

        BasicDBList commandResult = executeCommand(command);
        String result = "";
        for (Object obj : commandResult) {
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            if (result == "")
                result = kv.get(Constant._ID).toString();
            else if (result != "") {
                Integer dbValue = Integer.valueOf(kv.get(Constant._ID).toString());
                result = (Integer.valueOf(result) > dbValue ? result : dbValue.toString());
            }
        }
        return result;
    }

    public LinkedHashMap<String, List<String>> getHistoryJMTReport(String sceneName, String smallSceneName, String labelName, String resultVersion, String environment, Boolean isEnable) {
        LinkedHashMap<String, List<String>> result = new LinkedHashMap<String, List<String>>();

        String command = String.format("{aggregate: \"%s\",pipeline:  [{\"$match\": { \"$and\": [{ resultVersion:{ $lte:\"%s\"},environment:\"%s\",UnLoad:false,isEnable: %s,sceneName: \"%s\" ,labelName:\"%s\",smallSceneName:\"%s\" } ] }},{\"$group\": {\"_id\": { \"avg\":\"$avg\" ,\"tps\":\"$tps\",\"resultVersion\":\"$resultVersion\"}}},{ $sort: { resultVersion:1 } }]}",
                JMTREPORT_NAME, resultVersion, environment, isEnable, sceneName, labelName, smallSceneName);
        BasicDBList commandResult = executeCommand(command);

        for (Object obj : commandResult) {
            JMTReport report = new JMTReport();
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            String SmallSceneName = id.get(report.getResultVersion_name()).toString();
            if (!result.containsKey(SmallSceneName)) {
                List<String> list = new ArrayList<String>();
                list.add(id.get(report.getAvg_name()).toString());
                list.add(id.get(report.getTps_name()).toString());
                result.put(SmallSceneName, list);
            }
        }
        return result;
    }


    public LinkedHashMap<String, List<String>> getlinejmtreport(String resultVersion, String environment, String sceneName, String labelName, Boolean isEnable) {
        LinkedHashMap<String, List<String>> result = new LinkedHashMap<String, List<String>>();

        String command = String.format("{aggregate: \"%s\",pipeline:  [{\"$match\": { \"$and\": [{ UnLoad:false, resultVersion:\"%s\",environment:\"%s\",isEnable: %s,sceneName: \"%s\" ,labelName:\"%s\" } ] }},{\"$group\": {\"_id\": { \"avg\":\"$avg\" ,\"tps\":\"$tps\",\"smallSceneName\":\"$smallSceneName\"}}},{ $sort: { smallSceneName:1 } }]}",
                JMTREPORT_NAME, resultVersion, environment, isEnable, sceneName, labelName);
        BasicDBList commandResult = executeCommand(command);

        for (Object obj : commandResult) {
            JMTReport report = new JMTReport();
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            String SmallSceneName = id.get(report.getSmallSceneName_name()).toString();
            if (!result.containsKey(SmallSceneName)) {
                List<String> list = new ArrayList<String>();
                list.add(id.get(report.getAvg_name()).toString());
                list.add(id.get(report.getTps_name()).toString());
                result.put(SmallSceneName, list);
            }
        }
        return result;
    }

    public List<JMTReport> getGrpThreads(String sceneName, String environment, String resultVersion, String timeVersion, String template, Boolean isEnable) {
        String timeVersion_w = StringUtils.isEmpty(timeVersion) ? "" : String.format(",\"timeVersion\":\"%s\"", timeVersion);
        String command = String.format("{aggregate: \"%s\",pipeline:  [{\"$match\": { \"$and\": [{ UnLoad:false, resultVersion:\"%s\",environment:\"%s\",isEnable: %s,sceneName: \"%s\" ,template:\"%s\" %s} ] }},{\"$group\": {\"_id\": { \"smallSceneName\":\"$smallSceneName\",\"labelName\":\"$labelName\",\"grpThreads\":\"$grpThreads\",\"timeStamp\":\"$timeStamp\"}}},{ $sort: { sceneName: -1,smallSceneName:-1 } }]}",
                JMTREPORT_NAME, resultVersion, environment, isEnable, sceneName, template, timeVersion_w
        );

        List<JMTReport> resultList = new ArrayList<JMTReport>();
        BasicDBList commandResult = executeCommand(command);

        for (Object obj : commandResult) {
            JMTReport report = new JMTReport();
            report.setEnvironment(environment);
            report.setSceneName(sceneName);
            timeVersion = StringUtils.isEmpty(timeVersion) ? "" : String.format("_%s", timeVersion);
            report.setResultVersion(resultVersion + timeVersion + timeVersion);
            report.setTemplate(template);
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            report.setSmallSceneName(id.get(report.getSmallSceneName_name()).toString());
            report.setLabelName(id.get(report.getLabelName_name()).toString());
            report.setGrpThreads(Long.valueOf(id.get(report.getGrpThreads_name()).toString()));
            report.setTimeStamp(Long.valueOf(id.get(report.getTimeStamp_name()).toString()));

            resultList.add(report);
        }

        return resultList;
    }

    public List<JMTReport> getLabelReport(String taskid, String resultVersion, String resultEndVerison, String environment, String sceneName, String smallSceneName, String labelName, String template, boolean isEnable) {
        String version = "";
        taskid = StringUtils.isEmpty(taskid) ? "" : String.format(",taskid: %s", taskid);
        sceneName = StringUtils.isEmpty(sceneName) ? "" : String.format(",sceneName: \"%s\"", sceneName);
        labelName = StringUtils.isEmpty(labelName) ? "" : String.format(",labelName:\"%s\"", labelName);
        smallSceneName = StringUtils.isEmpty(smallSceneName) ? "" : String.format(",smallSceneName:\"%s\"", smallSceneName);
        environment = StringUtils.isEmpty(environment) ? "" : String.format(",environment:\"%s\"", environment);
        template = StringUtils.isEmpty(template) ? "" : String.format(",template:\"%s\"", template);
        if (!StringUtils.isEmpty(resultVersion) && !StringUtils.isEmpty(resultEndVerison)) {
            version = String.format(" , resultVersion:{$gte:\"%s\", $lt:\"%s\"}", resultVersion, resultEndVerison);
        } else if (!StringUtils.isEmpty(resultVersion)) {
            version = String.format(", resultVersion:{$gte:\"%s\"}", resultVersion);
        } else if (!StringUtils.isEmpty(resultEndVerison)) {
            version = String.format(", resultVersion:{$lte:\"%s\"}", resultEndVerison);

        }


        String command = String.format("{aggregate: \"%s\",pipeline:  [{\"$match\": { \"$and\": [{ UnLoad:false, isEnable: %s %s %s %s %s %s %s %s } ] }},{\"$group\": {\"_id\": { \"sceneName\":\"$sceneName\" ,\"resultVersion\":\"$resultVersion\",\"environment\":\"$environment\",\"template\":\"$template\",\"timeVersion\":\"$timeVersion\"}}},{ $sort: { sceneName: -1,resultVersion:-1 ,timeVersion: 1 } }]}",
                JMTREPORT_NAME, isEnable, version, environment, sceneName, smallSceneName, labelName, template, taskid
        );

        List<JMTReport> resultList = new ArrayList<JMTReport>();
        BasicDBList commandResult = executeCommand(command);

        for (Object obj : commandResult) {
            JMTReport report = new JMTReport();

            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            report.setEnvironment(id.get(report.getEnvironment_name()).toString());
            report.setSceneName(id.get(report.getSceneName_name()).toString());
            report.setResultVersion(id.get(report.getResultVersion_name()).toString());
            report.setTemplate(id.get(report.getTemplate_name()).toString());
//            report.setSumElapsed(id.get(report.getSumElapsed_name())==null?0:Double.valueOf(id.get(report.getSumElapsed_name()).toString()) );

            String timeVersion = id.get("timeVersion") == null ? "" : "_" + id.get("timeVersion").toString();
            report.setResultVersion(report.getResultVersion() + (StringUtils.isEmpty(timeVersion) ? "" : timeVersion));

            resultList.add(report);

        }


        return resultList;
    }

    public List<TaskLoadReport> getLoadReportList(int taskId, int taskListId) {


        String command = String.format("{aggregate: \"%s\",pipeline:  [{\"$match\": { taskId:%s } },{\"$group\": {\"_id\": { \"createTime\":\"$createTime\",\"taskListId\":\"$taskListId\" ,\"duration\":\"$duration\",\"endTime\":\"$endTime\",\"fail\":\"$fail\",\"startTime\":\"$startTime\",\"pass\":\"$pass\",\"total\":\"$total\",\"isTest\":\"$isTest\",\"_id\":\"$_id\"}}},{ $sort: { createTime: -1 } }]}",
                LOADREPORT_NAME, taskId
        );
        List<TaskLoadReport> resultList = new ArrayList<TaskLoadReport>();

        BasicDBList commandResult = executeCommand(command);
        for (Object obj : commandResult) {
            TaskLoadReport report = new TaskLoadReport();

            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            report.setTotal(id.getInt("total"));
            report.setFail(id.getInt("fail"));
            report.setPass(id.getInt("pass"));
            report.setDuration(id.getLong("duration"));
            report.setEndTime(id.get("endTime") == null ? null : id.getLong("endTime"));
            report.setStartTime(id.get("startTime") == null ? null : id.getLong("startTime"));
            report.setTaskId(taskId);
            report.setCreateTime(id.getLong("createTime"));
            report.set_id(id.getString("_id"));
            report.setIsTest(id.getBoolean("isTest", false));
            report.setTaskListId(id.get("taskListId") == null ? 0 : id.getInt("taskListId"));

            resultList.add(report);

        }
        return resultList;
    }

    public List<TaskLoadReport> getLoadReportList(int taskId) throws Exception {
        String command = String.format("{aggregate: \"%s\",pipeline:  [{\"$match\": { taskId:%s } },{\"$group\": {\"_id\": { \"createTime\":\"$createTime\",\"taskListId\":\"$taskListId\" ,\"duration\":\"$duration\",\"endTime\":\"$endTime\",\"fail\":\"$fail\",\"startTime\":\"$startTime\",\"pass\":\"$pass\",\"total\":\"$total\",\"isTest\":\"$isTest\",\"_id\":\"$_id\"}}},{ $sort: { createTime: -1 } }]}",
                LOADREPORT_NAME, taskId
        );
        List<TaskLoadReport> resultList = new ArrayList<TaskLoadReport>();

        BasicDBList commandResult = executeCommand(command);
        for (Object obj : commandResult) {
            TaskLoadReport report = new TaskLoadReport();

            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            report.setTotal(id.getInt("total"));
            report.setFail(id.getInt("fail"));
            report.setPass(id.getInt("pass"));
            report.setDuration(id.getLong("duration"));
            report.setEndTime(id.get("endTime") == null ? null : id.getLong("endTime"));
            report.setStartTime(id.get("startTime") == null ? null : id.getLong("startTime"));
            report.setTaskId(taskId);
            report.setCreateTime(id.getLong("createTime"));
            report.set_id(id.getString("_id"));
            report.setIsTest(id.getBoolean("isTest", false));
            report.setTaskListId(id.get("taskListId") == null ? 0 : id.getInt("taskListId"));

            resultList.add(report);

        }
        return resultList;
    }

    public int getJmtCount(String version, String timeVersion, String environment, String sceneName, String smallSceneName, String template, Boolean isEnable) {
        timeVersion = StringUtils.isEmpty(timeVersion) ? "" : String.format(",timeVersion:\"%s\"", timeVersion);
        String command = String.format(
                "{aggregate: \"%s\"," +
                        "pipeline:  [" +
                        "{\"$match\": { \"$and\": [{ environment:\"%s\",template:\"%s\",resultVersion:\"%s\",smallSceneName:\"%s\",isEnable: %s,sceneName: \"%s\" %s } ] }}," +
                        "{\"$group\": {\"_id\": { \"sceneName\":\"$sceneName\" ," +
                        "\"createTime\":\"$createTime\",\"taskid\":\"$taskid\"}}}," +
                        "{ $sort: { sceneName: -1} }]}",
                JMTREPORT_NAME, environment, template, version, smallSceneName, isEnable, sceneName, timeVersion
        );

        BasicDBList commandResult = executeCommand(command);

        return commandResult.size();

    }

    public List getJmtHistroy(String taskId,String resultVersion, String resultEndVerison, String timeVersion, String environment, String sceneName, String smallSceneName, String template, Boolean isEnable) {
        smallSceneName = StringUtils.isEmpty(smallSceneName) ? "" : String.format(",smallSceneName:\"%s\"", smallSceneName);
        timeVersion = StringUtils.isEmpty(timeVersion) ? "" : String.format(",timeVersion:\"%s\"", timeVersion);
        template = String.format(",template:\"%s\"", template.trim());
        taskId =  StringUtils.isEmpty(taskId) ? "" : String.format(",taskId:\"%s\"", taskId);
        sceneName = StringUtils.isEmpty(sceneName)?"": String.format(",sceneName:\"%s\"",sceneName);
        String version = "";

        if (!StringUtils.isEmpty(resultVersion) && !StringUtils.isEmpty(resultEndVerison)) {
            version = String.format(" , resultVersion:{$gte:\"%s\", $lt:\"%s\"}", resultVersion, resultEndVerison);
        } else if (!StringUtils.isEmpty(resultVersion)) {
            version = String.format(", resultVersion:{$gte:\"%s\"}", resultVersion);
        } else if (!StringUtils.isEmpty(resultEndVerison)) {
            version = String.format(", resultVersion:{$lt:\"%s\"}", resultEndVerison);

        }


        String command = String.format(
                "{aggregate: \"%s\"," +
                        "pipeline:  [" +
                        "{\"$match\": { \"$and\": [{ environment:\"%s\" %s ,isEnable: %s  %s  %s %s %s %s } ] }}," +
                        "{\"$group\": {\"_id\": { \"sceneName\":\"$sceneName\" , \"smallSceneName\":\"$smallSceneName\" , \"resultVersion\":\"$resultVersion\" ," +
                        " \"template\":\"$template\" , " + " \"timeVersion\":\"$timeVersion\" ," +
                        "\"createTime\":\"$createTime\",\"taskid\":\"$taskid\"}}}," +
                        "{ $sort: { sceneName: -1,smallSceneName:-1 } }]}",
                JMTREPORT_NAME, environment, version, isEnable, sceneName, smallSceneName, template, timeVersion,taskId
        );

        BasicDBList commandResult = executeCommand(command);
        HashMap<String, String[]> hashMap = new HashMap<String, String[]>();
        List result = new ArrayList();
        for (Object obj : commandResult) {
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);

            String createTime = id.getString("createTime");
            String dbtaskId = id.getString("taskid");
            sceneName = id.getString("sceneName");
            smallSceneName = id.getString("smallSceneName");
            resultVersion = id.getString("resultVersion");
            template = id.getString("template");
            timeVersion = id.get("timeVersion") == null ? "" : id.get("timeVersion").toString();
            resultVersion = StringUtils.isEmpty(timeVersion) ? resultVersion : resultVersion + "_" + timeVersion;

            String[] jmtData = {sceneName, smallSceneName, resultVersion, environment, template, createTime, dbtaskId};
            if (!hashMap.containsKey(createTime))
                result.add(jmtData);


        }
        return result;
    }


    public List<JMTReport> getJMTReportList(String resultVersion, String resultEndVerison, String timeVersion, String environment, String sceneName, String smallSceneName, String labelName, String template, String grpThreads, boolean isEnable) {
        timeVersion = StringUtils.isEmpty(timeVersion) ? "" : String.format(",timeVersion:\"%s\"", timeVersion);
        labelName = StringUtils.isEmpty(labelName) ? "" : String.format(",labelName:\"%s\"", labelName);
        smallSceneName = StringUtils.isEmpty(smallSceneName) ? "" : String.format(",smallSceneName:\"%s\"", smallSceneName);
        template = String.format(",template:\"%s\"", template.trim());
        grpThreads = StringUtils.isEmpty(grpThreads) ? "" : String.format(",grpThreads:\"%s\"", grpThreads);
        String version = "";

        if (!StringUtils.isEmpty(resultVersion) && !StringUtils.isEmpty(resultEndVerison)) {
            version = String.format(" , resultVersion:{$gte:\"%s\", $lt:\"%s\"}", resultVersion, resultEndVerison);
        } else if (!StringUtils.isEmpty(resultVersion)) {
            version = String.format(", resultVersion:{$gte:\"%s\"}", resultVersion);
        } else if (!StringUtils.isEmpty(resultEndVerison)) {
            version = String.format(", resultVersion:{$lt:\"%s\"}", resultEndVerison);

        }


        String command = String.format("{aggregate: \"%s\",pipeline:  [{\"$match\": { \"$and\": [{ environment:\"%s\" %s ,UnLoad:false,isEnable: %s,sceneName: \"%s\" %s %s %s %s %s } ] }},{\"$group\": {\"_id\": { \"sceneName\":\"$sceneName\" ,\"resultVersion\":\"$resultVersion\",\"smallSceneName\":\"$smallSceneName\",\"labelName\":\"$labelName\",\"avg\":\"$avg\",\"minElapsed\":\"$minElapsed\",\"elapsed50\":\"$elapsed50\",\"elapsed90\":\"$elapsed90\",\"elapsed95\":\"$elapsed95\",\"elapsed99\":\"$elapsed99\",\"fail\":\"$fail\",\"tps\":\"$tps\",\"maxElapsed\":\"$maxElapsed\",\"template\":\"$template\",\"grpThreads\":\"$grpThreads\",\"errorCount\":\"$errorCount\",\"timeStamp\":\"$timeStamp\",\"elapsedCount\":\"$elapsedCount\",\"sumElapsed\":\"$sumElapsed\",\"createTime\":\"$createTime\",\"timeVersion\":\"$timeVersion\"}}},{ $sort: { sceneName: -1,smallSceneName:-1 } }]}",
                JMTREPORT_NAME, environment, version, isEnable, sceneName, smallSceneName, labelName, template, grpThreads, timeVersion
        );

        List<JMTReport> resultList = new ArrayList<JMTReport>();

        BasicDBList commandResult = executeCommand(command);

        for (Object obj : commandResult) {
            JMTReport report = new JMTReport();

            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            report.setEnvironment(environment);
            report.setSceneName(id.get(report.getSceneName_name()).toString());
            timeVersion = id.get("timeVersion") == null ? "" : id.get("timeVersion").toString();
            String v = id.get(report.getResultVersion_name()).toString();
            v = StringUtils.isEmpty(timeVersion) ? v : v + "_" + timeVersion;
            report.setResultVersion(v);
            report.setSmallSceneName(id.get(report.getSmallSceneName_name()).toString());
            report.setAvg(Double.valueOf(id.get(report.getAvg_name()).toString()));
            report.setElapsed50(Double.valueOf(id.get(report.getElapsed50_name()).toString()));
            report.setElapsed90(Double.valueOf(id.get(report.getElapsed90_name()).toString()));
            report.setElapsed95(Double.valueOf(id.get(report.getElapsed95_name()).toString()));
            report.setElapsed99(Double.valueOf(id.get(report.getElapsed99_name()).toString()));
            report.setLabelName(id.get(report.getLabelName_name()).toString());
            report.setMaxElapsed(Long.valueOf(id.get(report.getMaxElapsed_name()).toString()));
            report.setMinElapsed(Long.valueOf(id.get(report.getMinElapsed_name()).toString()));
            report.setFail(Double.valueOf(id.get(report.getFail_name()).toString()));
            report.setTps(Double.valueOf(id.get(report.getTps_name()).toString()));
            report.setTemplate(id.get(report.getTemplate_name()).toString());
            report.setGrpThreads(Long.valueOf(id.get(report.getGrpThreads_name()).toString()));
            report.setErrorCount(Integer.valueOf(id.get(report.getErrorCount_name()).toString()));
            report.setTimeStamp(getLongValue(id.get(report.getTimeStamp_name()).toString()));
            report.setElapsedCount(Integer.valueOf(id.get(report.getElapsedCount_name()).toString()));

            report.setSumElapsed(getLongValue(id.get(report.getSumElapsed_name()).toString()));
            report.setCreateTime(id.getString("createTime"));

            resultList.add(report);

        }

        Collections.sort(resultList);
        return resultList;
    }

    private Long getLongValue(String value)
    {
        int index= value.indexOf(".");
        if(index>0)
            value= value.substring(0,index);

        return Long.valueOf(value);
    }


    public void update(Query query, Update update) {

        this.mongoTemplate.updateMulti(query, update, JMTREPORT_NAME);

    }

    public void updateAGGReport(Query query, Update update) {

        this.mongoTemplate.updateMulti(query, update, JMTAGGREPORT_NAME);

    }


    public void updateAGReport(int minDate, int maxDate, int taskType, Map<Integer, Integer> taskIdAndGroupId) {

        BasicDBList list = getTaskReport(minDate, maxDate, taskType);

        if (list != null && list.isEmpty() == false) {
            //删除匹配的数据
            WriteResult resultIsOK = deleteAGReport(minDate, maxDate, taskType);
            //删除成功后合并数据并插入数据
            if (resultIsOK.getLastError().containsKey("ok") && resultIsOK.getLastError().get("ok").toString().equals("1")) {
                List<AGReport> result = convertToAGReport(list, taskIdAndGroupId);
                this.insertList(result);
            }
        }
    }


    public List<String> getCaseOwnerByGroupId(int groupId) {
        List<String> ownerList = new ArrayList<String>();
        String groupMatch = String.valueOf(groupId);
        if (groupId == -1) {
            groupMatch = "{$gte:-1}";
        }
        String command = String.format("{aggregate: \"%s\", pipeline:[{\"$match\":{\"groupId\":%s}},{\"$group\":{\"_id\":{\"owner\":\"$owner\"}}}]}", Constant.AG_REPORT, groupMatch);
        BasicDBList commandResult = executeCommand(command);
        for (Object obj : commandResult) {
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            ownerList.add(id.getString("owner"));
        }

        return ownerList;
    }

    //找到相同date taskid owner caseName 通过的case
    private boolean getCaseisPass(BasicDBList commandResult, String date, int taskId, String owner, String caseName) {
        Boolean result = null;
        for (Object obj : commandResult) {
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject dBid = (BasicDBObject) kv.get(Constant._ID);
            Boolean isPass = dBid.getString("testResult").trim().toLowerCase().equals("pass");
            if (!isPass)//如果是失败的，直接跳出进入下一个循环
                continue;

            int dbtaskId = Integer.valueOf(dBid.get(Constant.TASK_ID).toString());
            String dbdate = dBid.get(Constant.DATE).toString();
            String dbowner = dBid.get("owner").toString();
            String dbcaseName = dBid.getString("testCaseName");


            if (isPass && dbdate.equals(date) && dbtaskId == taskId && dbowner.equals(owner) && dbcaseName.equals(caseName))
                return true;
            else
                result = false;
        }

        return result;
    }


    private List<AGReport> convertToAGReport(BasicDBList commandResult, Map<Integer, Integer> taskIdAndGroupId) {
        //根据 date groupid taskid owner 合并
        List<AGReport> result = mergeAGReportByCase(commandResult, taskIdAndGroupId);
        //根据 date groupid owner 合并
        return mergeAGReportByTaskId(result);
    }


    private void checkIsOK(BasicDBList commandResult, Map<Integer, Integer> taskIdAndGroupId, String owner) {
        HashMap<String, Boolean> caseNameList = new HashMap<String, Boolean>();
        HashMap<String, AGReport> groupAGReport = new HashMap<String, AGReport>();

        int isPass2 = 0;
        int s = 0;
        int e = 0;
        int t = 0;

        //遍历数据
        for (Object obj : commandResult) {
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;

            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            int taskId = Integer.valueOf(id.get(Constant.TASK_ID).toString());
            int businessGroupId = -1;
            if (taskIdAndGroupId.containsKey(taskId)) {
                businessGroupId = taskIdAndGroupId.get(taskId);
            } else {
                Logger.info(String.format("taskId : %d 找不到group defalut:Arch-Test(7)", taskId));
                businessGroupId = 7;
            }

            String dbdate = id.get(Constant.DATE).toString();
            String dbowner = id.get("owner").toString();
            if (!dbowner.equals(owner))
                continue;

            String dbcaseName = id.getString("testCaseName");
            int dbtaskId = id.getInt("taskId");
            Boolean isPass = id.getString("testResult").trim().toLowerCase().equals("pass");


            if (caseNameList.containsKey(dbtaskId + dbcaseName)) {
                if (isPass && !caseNameList.get(dbtaskId + dbcaseName))
                    isPass2++;
            } else {
                caseNameList.put(dbtaskId + dbcaseName, isPass);
                if (isPass)
                    s++;
                else
                    e++;
                t++;
            }
        }

        Logger.info("pass的数量" + s);
        Logger.info("error的数量" + e);
        Logger.info("error后pass的数量" + isPass2);
        Logger.info("全部的数量" + t);

    }

    private List<AGReport> mergeAGReportByCase(BasicDBList commandResult, Map<Integer, Integer> taskIdAndGroupId) {
        //多个TaskId 如果date groupid taskid owner相同 需要汇总到一行记录中
        HashMap<String, AGReport> groupAGReport = new HashMap<String, AGReport>();
        //将caseName和结果储存在该变量中（根据 taskId+date+caseName+owner 分组）
        HashMap<String, Boolean> caseNameList = new HashMap<String, Boolean>();
        //遍历数据
        for (Object obj : commandResult) {
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;

            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            int taskId = Integer.valueOf(id.get(Constant.TASK_ID).toString());
            int businessGroupId = -1;
            if (taskIdAndGroupId.containsKey(taskId)) {
                businessGroupId = taskIdAndGroupId.get(taskId);
            } else {
                Logger.error(String.format("taskId : %d 找不到group defalut:Arch-Test(7)", taskId));
                businessGroupId = 7;
            }

            String dbdate = id.get(Constant.DATE).toString();
            String dbowner = id.get("owner").toString();
            String dbcaseName = id.getString("testCaseName");
            int dbtaskId = id.getInt("taskId");
            Boolean isPass = id.getString("testResult").trim().toLowerCase().equals("pass");
            // date_groupid_taskid_owner
            String key = String.format("%s_%s_%s_%s",
                    dbdate, businessGroupId, dbtaskId, dbowner);
            //caseNameLis 根据 taskId+caseName+owner 分组
            String caseNamekey = String.format("%s_%s_%s_%s", dbtaskId, dbdate, dbcaseName, dbowner);

            //如果有数据需要合并
            if (groupAGReport.containsKey(key)) {
                AGReport reportGroup = groupAGReport.get(key);

                //如果存在过caseName 并且 上一次是失败的，查找相同条件的成功的数据
                if (caseNameList.containsKey(caseNamekey) && !caseNameList.get(caseNamekey)) {
                    //如果有成功pass的数据
                    if (getCaseisPass(commandResult, dbdate, dbtaskId, dbowner, dbcaseName)) {
                        reportGroup.setPassNum(reportGroup.getPassNum() + 1);
                        //将保存的失败的casename 去除
                        if (reportGroup.getFailCase().trim().indexOf("," + dbtaskId + "_" + dbcaseName) > 0)
                            reportGroup.setFailCase(reportGroup.getFailCase().trim().replace("," + dbtaskId + "_" + dbcaseName, ""));
                        else
                            reportGroup.setFailCase(reportGroup.getFailCase().trim().replace(dbtaskId + "_" + dbcaseName, ""));

                        caseNameList.remove(caseNamekey);
                        caseNameList.put(caseNamekey, true);

                    }
                    //否则没有运行过的
                } else if (!caseNameList.containsKey(caseNamekey)) {
                    reportGroup.setCaseNum(reportGroup.getCaseNum() + 1);
                    caseNameList.put(caseNamekey, isPass);
                    if (isPass)
                        reportGroup.setPassNum(reportGroup.getPassNum() + 1);
                    else {
                        if (StringUtils.isEmpty(reportGroup.getFailCase()))
                            reportGroup.setFailCase(dbtaskId + "_" + dbcaseName);
                        else
                            reportGroup.setFailCase(reportGroup.getFailCase() + "," + dbtaskId + "_" + dbcaseName);
                    }
                }

            } else { // 第一个数据 没有数据需要合并
                AGReport reportGroup = new AGReport();
                reportGroup.setDate(Long.valueOf(id.get(Constant.DATE).toString()));
                reportGroup.setTaskType(Integer.valueOf(id.get(Constant.TASK_TYPE).toString()));
                reportGroup.setCaseNum(1);
                reportGroup.setGroupId(businessGroupId);
                reportGroup.setOwner(id.getString("owner"));
                if (isPass) {
                    reportGroup.setPassNum(1);
                    reportGroup.setFailCase("");
                } else {
                    reportGroup.setPassNum(0);
                    reportGroup.setFailCase(id.getString("taskId") + "_" + id.getString("testCaseName"));
                }

                caseNameList.put(caseNamekey, isPass);

                groupAGReport.put(key, reportGroup);
            }
        }


        //根据 date groupid taskid owner 合并
        List<AGReport> result = new ArrayList<AGReport>(groupAGReport.values());
        return result;
    }

    private List<AGReport> mergeAGReportByTaskId(List<AGReport> agReports) {
        //多个TaskId 如果date groupid owner相同 需要汇总到一行记录中
        HashMap<String, AGReport> groupAGReport = new HashMap<String, AGReport>();
        for (AGReport report : agReports) {
            String key = String.format("%s_%s_%s", report.getDate(), report.getGroupId(), report.getOwner());
            if (groupAGReport.containsKey(key)) {
                AGReport re = groupAGReport.get(key);
                if (!StringUtils.isEmpty(report.getFailCase())) {
                    report.setFailCase((StringUtils.isEmpty(re.getFailCase()) ? "" : ",") + report.getFailCase());

                    re.setFailCase(re.getFailCase() + report.getFailCase());
                }
                re.setCaseNum(re.getCaseNum() + report.getCaseNum());
                re.setPassNum(re.getPassNum() + report.getPassNum());

            } else
                groupAGReport.put(key, report);
        }
        return new ArrayList<AGReport>(groupAGReport.values());
    }


    private WriteResult deleteAGReport(int minDate, int maxDate, int taskType) {

        BasicDBObject query = new BasicDBObject();
        query.put(Constant.DATE, new BasicDBObject("$lt", maxDate).append("$gte", minDate));

        if (taskType > 0)
            query.put(Constant.TASK_TYPE, taskType);

        Logger.info(String.format("db.%s.remove(%s)", Constant.AG_REPORT, query.toString()));

        return mongoTemplate.getCollection(Constant.AG_REPORT).remove(query);
    }


    public TreeMap<String, List<String>> getResultVersion(String sceneName, String smallSceneName, String labelName, String environment, String resultdate, Boolean isEnable) {
        resultdate = StringUtils.isEmpty(resultdate) ? "" : String.format(",resultVersion:\"%s\"", resultdate);
        smallSceneName = smallSceneName == "" ? "" : String.format(",smallSceneName:\"%s\"", smallSceneName);

        labelName = labelName == "" ? "" : String.format(",labelName:\"%s\"", labelName);

        environment = environment == "" ? "" : String.format(",environment:\"%s\"", environment);

        String command = String.format("{aggregate: \"%s\",pipeline: [{\"$match\": { \"$and\": [{isEnable: %s,sceneName: \"%s\" %s %s %s %s }]}},{\"$group\": {\"_id\": { resultVersion: \"$resultVersion\", timeVersion:\"$timeVersion\"  } }}]}",
                JMTREPORT_NAME, isEnable, sceneName, smallSceneName, labelName, environment, resultdate);

        BasicDBList commandResult = executeCommand(command);

        TreeMap<String, List<String>> result = new TreeMap<String, List<String>>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });


        for (Object obj : commandResult) {
            HashMap<String, BasicDBObject> kv = (HashMap<String, BasicDBObject>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            String key = id.get("resultVersion").toString();
            String value = id.get("timeVersion") == null ? "NONE" : id.get("timeVersion").toString();

            if (!result.containsKey(key))
                result.put(key, new ArrayList<String>());

            result.get(key).add(value);
        }

        for (String key : result.keySet()) {
            Collections.sort(result.get(key));
        }


        return result;
    }


    public List<String> getJMTTaskIdList(String sceneName, String template, Boolean isEnable) {
        List<String> resultList = new ArrayList<String>();

        if (!StringUtils.isEmpty(template))
            template = String.format(",template:\"%s\"", template);

        if (!StringUtils.isEmpty(sceneName))
            sceneName = String.format(",sceneName:\"%s\"", sceneName);

        String command = String.format("{aggregate: \"%s\",pipeline:  [{\"$match\": {  isEnable: %s %s %s }},{\"$group\": {\"_id\":{ \"taskid\":\"$taskid\"}}}]}",
                JMTREPORT_NAME, isEnable, template, sceneName
        );

        BasicDBList commandResult = executeCommand(command);

        for (Object obj : commandResult) {
            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);

            String key = id.getString("taskid");
            if (!StringUtils.isEmpty(key) && !resultList.contains(key))
                resultList.add(key);

        }

        return resultList;
    }

    public List<JMTReport> getJMTReportByResultVersion(String resultVersion) {
        List<JMTReport> resultList = new ArrayList<JMTReport>();

        resultVersion = StringUtils.isEmpty(resultVersion) ? "" : String.format(", resultVersion:{$gt:\"%s\"}", resultVersion);

        String command = String.format("{aggregate: \"%s\",pipeline:  [{\"$match\": { \"$and\": [{  isEnable: true , UnLoad: false %s } ] }},{\"$group\": {\"_id\": { \"resultVersion\":\"$resultVersion\",\"timeVersion\":\"$timeVersion\",\"environment\":\"$environment\",\"sceneName\":\"$sceneName\",\"smallSceneName\":\"$smallSceneName\"},\"minversion\" : {$min: \"$resultVersion\" }}},{ $sort: { minversion:1  }}, { $limit: 1 }]}",
                JMTREPORT_NAME, resultVersion
        );

        BasicDBList commandResult = executeCommand(command);

        resultVersion = "";

        for (Object obj : commandResult) {
            JMTReport report = new JMTReport();

            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);

            resultVersion = id.get("resultVersion").toString();

        }


        if (!StringUtils.isEmpty(resultVersion)) {
            command = String.format("{aggregate: \"%s\",pipeline:  [{\"$match\": { \"$and\": [{  isEnable: true , UnLoad: false , resultVersion : \"" + resultVersion + "\"} ] }},{\"$group\": {\"_id\": {  \"sceneName\":\"$sceneName\" ,\"environment\":\"$environment\",\"resultVersion\":\"$resultVersion\",\"smallSceneName\":\"$smallSceneName\",\"labelName\":\"$labelName\",\"avg\":\"$avg\",\"minElapsed\":\"$minElapsed\",\"elapsed50\":\"$elapsed50\",\"elapsed90\":\"$elapsed90\",\"elapsed95\":\"$elapsed95\",\"elapsed99\":\"$elapsed99\",\"fail\":\"$fail\",\"tps\":\"$tps\",\"maxElapsed\":\"$maxElapsed\",\"template\":\"$template\",\"grpThreads\":\"$grpThreads\",\"errorCount\":\"$errorCount\",\"timeStamp\":\"$timeStamp\",\"elapsedCount\":\"$elapsedCount\",\"sumElapsed\":\"$sumElapsed\",\"createTime\":\"$createTime\",\"timeVersion\":\"$timeVersion\",\"tasklistid\":\"$tasklistid\",\"taskid\":\"$taskid\" }}},{ $sort: { sceneName: -1,resultVersion:-1 ,timeVersion: 1 } }]}",
                    JMTREPORT_NAME
            );


             commandResult = executeCommand(command);

            for (Object obj : commandResult) {
                JMTReport report = new JMTReport();

                HashMap<String, Object> kv = (HashMap<String, Object>) obj;
                BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
                report.setEnvironment(id.getString("environment"));
                report.setSceneName(id.get(report.getSceneName_name()).toString());
                String timeVersion = id.get("timeVersion") == null ? "" : id.get("timeVersion").toString();
                String v = id.get(report.getResultVersion_name()).toString();
                v = StringUtils.isEmpty(timeVersion) ? v : v + "_" + timeVersion;
                report.setResultVersion(v);
                report.setSmallSceneName(id.get(report.getSmallSceneName_name()).toString());
                report.setAvg(Double.valueOf(id.get(report.getAvg_name()).toString()));
                report.setElapsed50(Double.valueOf(id.get(report.getElapsed50_name()).toString()));
                report.setElapsed90(Double.valueOf(id.get(report.getElapsed90_name()).toString()));
                report.setElapsed95(Double.valueOf(id.get(report.getElapsed95_name()).toString()));
                report.setElapsed99(Double.valueOf(id.get(report.getElapsed99_name()).toString()));
                report.setLabelName(id.get(report.getLabelName_name()).toString());
                report.setMaxElapsed(Long.valueOf(id.get(report.getMaxElapsed_name()).toString()));
                report.setMinElapsed(Long.valueOf(id.get(report.getMinElapsed_name()).toString()));
                report.setFail(Double.valueOf(id.get(report.getFail_name()).toString()));
                report.setTps(Double.valueOf(id.get(report.getTps_name()).toString()));
                report.setTemplate(id.get(report.getTemplate_name()).toString());
                report.setGrpThreads(Long.valueOf(id.get(report.getGrpThreads_name()).toString()));
                report.setErrorCount(Integer.valueOf(id.get(report.getErrorCount_name()).toString()));
                report.setTimeStamp(Long.valueOf(id.get(report.getTimeStamp_name()).toString()));
                report.setElapsedCount(Integer.valueOf(id.get(report.getElapsedCount_name()).toString()));
                String sumElapsed=id.get(report.getSumElapsed_name()).toString();
                sumElapsed = sumElapsed.indexOf(".")>0 ? sumElapsed.substring(0,sumElapsed.indexOf(".")): sumElapsed ;
                report.setSumElapsed(Long.valueOf(sumElapsed));
                report.setTaskID(Integer.valueOf(id.get("taskid") == null ? "0" : id.get("taskid").toString()));
                report.setTaskListID(Integer.valueOf(id.get("tasklistid")==null?"0":id.get("tasklistid").toString()));
                report.setCreateTime(id.getString("createTime"));
//                if(report.getResultVersion().equals("20160307")&& report.getSceneName().equals("IsTransIsEnable"))
                    resultList.add(report);

            }

            Collections.sort(resultList);
        }

        return resultList;

    }

    public List<JMTReport> getLabelReportByAGGReport(String timearg, String taskid, String resultVersion, String resultEndVersion, String environment, String sceneName, String smallSceneName, String labelName, String template, Boolean isEnable) {

        String version = "";
        timearg = StringUtils.isEmpty(timearg) ? "" : String.format(",avg : { $gte : %s }", timearg);
        taskid = StringUtils.isEmpty(taskid) ? "" : String.format(",taskid: %s", taskid);
        sceneName = StringUtils.isEmpty(sceneName) ? "" : String.format(",sceneName: \"%s\"", sceneName);
        labelName = StringUtils.isEmpty(labelName) ? "" : String.format(",labelName:\"%s\"", labelName);
        smallSceneName = StringUtils.isEmpty(smallSceneName) ? "" : String.format(",smallSceneName:\"%s\"", smallSceneName);
        environment = StringUtils.isEmpty(environment) ? "" : String.format(",environment:\"%s\"", environment);
        template = StringUtils.isEmpty(template) ? "" : String.format(",template:\"%s\"", template);
        if (!StringUtils.isEmpty(resultVersion) && !StringUtils.isEmpty(resultEndVersion)) {
            version = String.format(" , resultVersion:{$gte:\"%s\", $lt:\"%s\"}", resultVersion, resultEndVersion);
        } else if (!StringUtils.isEmpty(resultVersion)) {
            version = String.format(", resultVersion:{$gte:\"%s\"}", resultVersion);
        } else if (!StringUtils.isEmpty(resultEndVersion)) {
            version = String.format(", resultVersion:{$lte:\"%s\"}", resultEndVersion);

        }


        String command = String.format("{aggregate: \"%s\",pipeline:  [{\"$match\": { \"$and\": [{  isEnable: %s %s %s %s %s %s %s %s %s} ] }},{\"$group\": {\"_id\": { \"sceneName\":\"$sceneName\" ,\"resultVersion\":\"$resultVersion\",\"environment\":\"$environment\",\"template\":\"$template\",\"timeVersion\":\"$timeVersion\",\"grpThreads\":\"$grpThreads\"}}},{ $sort: { resultVersion:-1 ,timeVersion: -1 ,sceneName: -1 } }]}",
                JMTAGGREPORT_NAME, isEnable, version, environment, sceneName, smallSceneName, labelName, template, taskid, timearg
        );

        List<JMTReport> resultList = new ArrayList<JMTReport>();
        BasicDBList commandResult = executeCommand(command);

        for (Object obj : commandResult) {
            JMTReport report = new JMTReport();

            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            report.setEnvironment(id.get(report.getEnvironment_name()).toString());
            report.setSceneName(id.get(report.getSceneName_name()).toString());
            report.setResultVersion(id.get(report.getResultVersion_name()).toString());
            report.setTemplate(id.get(report.getTemplate_name()).toString());
            report.setGrpThreads(getLongValue(id.get(report.getGrpThreads_name()).toString()));
            String timeVersion = id.get("timeVersion") == null || StringUtils.isEmpty(id.getString("timeVersion")) ? "" : "_" + id.get("timeVersion").toString();
            report.setResultVersion(report.getResultVersion() + (StringUtils.isEmpty(timeVersion) ? "" : timeVersion));

            resultList.add(report);

        }


        return resultList;

    }


    public List<JmtReportData> getJMTAGGReportList(String resultVersion, String resultEndVerison, String timeVersion, String environment, String sceneName, String smallSceneName, String labelName, String template, String grpThreads, String timeavg, boolean isEnable) {
        timeavg = StringUtils.isEmpty(timeavg) ? "" : String.format(", avg:{ $gte: %s }", timeavg);
        timeVersion = StringUtils.isEmpty(timeVersion) ? "" : String.format(",timeVersion:\"%s\"", timeVersion);
        labelName = StringUtils.isEmpty(labelName) ? "" : String.format(",labelName:\"%s\"", labelName);
        smallSceneName = StringUtils.isEmpty(smallSceneName) ? "" : String.format(",smallSceneName:\"%s\"", smallSceneName);
        template = String.format(",template:\"%s\"", template.trim());
        grpThreads = StringUtils.isEmpty(grpThreads) ? "" : String.format(",grpThreads:\"%s\"", grpThreads);
        String version = "";

        if (!StringUtils.isEmpty(resultVersion) && !StringUtils.isEmpty(resultEndVerison)) {
            version = String.format(" , resultVersion:{$gte:\"%s\", $lt:\"%s\"}", resultVersion, resultEndVerison);
        } else if (!StringUtils.isEmpty(resultVersion)) {
            version = String.format(", resultVersion:{$gte:\"%s\"}", resultVersion);
        } else if (!StringUtils.isEmpty(resultEndVerison)) {
            version = String.format(", resultVersion:{$lt:\"%s\"}", resultEndVerison);

        }


        String command = String.format("{aggregate: \"%s\",pipeline:  [{\"$match\": { \"$and\": [{ environment:\"%s\" %s ,isEnable: %s,sceneName: \"%s\" %s %s %s %s %s %s } ] }},{\"$group\": {\"_id\": { \"sceneName\":\"$sceneName\" ,\"timeStamp\":\"$timeStamp\",\"resultVersion\":\"$resultVersion\",\"smallSceneName\":\"$smallSceneName\",\"labelName\":\"$labelName\",\"avg\":\"$avg\",\"minElapsed\":\"$minElapsed\",\"elapsed50\":\"$elapsed50\",\"elapsed90\":\"$elapsed90\",\"elapsed95\":\"$elapsed95\",\"elapsed99\":\"$elapsed99\",\"fail\":\"$fail\",\"tps\":\"$tps\",\"maxElapsed\":\"$maxElapsed\",\"template\":\"$template\",\"grpThreads\":\"$grpThreads\",\"errorCount\":\"$errorCount\",\"elapsedCount\":\"$elapsedCount\",\"timeVersion\":\"$timeVersion\",\"begin\":\"$begin\",\"end\":\"$end\"}}},{ $sort: { resultVersion:-1,timeVersion:-1, sceneName: -1,smallSceneName:-1 } }]}",
                JMTAGGREPORT_NAME, environment, version, isEnable, sceneName, smallSceneName, labelName, template, grpThreads, timeVersion, timeavg
        );

        List<JmtReportData> resultList = new ArrayList<JmtReportData>();

        BasicDBList commandResult = executeCommand(command);

        for (Object obj : commandResult) {
            JmtReportData report = new JmtReportData();

            HashMap<String, Object> kv = (HashMap<String, Object>) obj;
            BasicDBObject id = (BasicDBObject) kv.get(Constant._ID);
            report.setEnvironment(environment);
            report.setSceneName(id.get(report.getSceneName_name()).toString());
            timeVersion = id.get("timeVersion") == null ? "" : id.get("timeVersion").toString();
            String v = id.get(report.getResultVersion_name()).toString();
            v = StringUtils.isEmpty(timeVersion) ? v : v + "_" + timeVersion;
            report.setResultVersion(v);
            report.setBegin(id.get("begin").toString());
            report.setEnd(id.get("end").toString());
            report.setSmallSceneName(id.get(report.getSmallSceneName_name()).toString());
            report.setAvg(Double.valueOf(id.get(report.getAvg_name()).toString()));
            report.setElapsed50(Double.valueOf(id.get(report.getElapsed50_name()).toString()));
            report.setElapsed90(Double.valueOf(id.get(report.getElapsed90_name()).toString()));
            report.setElapsed95(Double.valueOf(id.get(report.getElapsed95_name()).toString()));
            report.setElapsed99(Double.valueOf(id.get(report.getElapsed99_name()).toString()));
            report.setLabelName(id.get(report.getLabelName_name()).toString());
            report.setMaxElapsed(Double.valueOf(id.get(report.getMaxElapsed_name()).toString()));
            report.setMinElapsed(Double.valueOf(id.get(report.getMinElapsed_name()).toString()));
            report.setFail(Double.valueOf(id.get(report.getFail_name()).toString()));
            report.setTps(Double.valueOf(id.get(report.getTps_name()).toString()));
            report.setTemplate(id.get(report.getTemplate_name()).toString());
            report.setGrpThreads(Long.valueOf(id.get(report.getGrpThreads_name()).toString()));
            report.setErrorCount(Integer.valueOf(id.get(report.getErrorCount_name()).toString()));
            report.setElapsedCount(Integer.valueOf(id.get(report.getElapsedCount_name()).toString()));
            report.setTimeStamp(id.getLong(report.getTimeStamp_name()));
            resultList.add(report);

        }

        Collections.sort(resultList);
        return resultList;
    }


}
//单个查询结果插入collection
//db.ReportGroup.insert( db.ReportGroup.findOne({"businessGroupId":1},{"_id":0}))

/*所有查询结果插入collection
var lst = db.ReportGroup.find({"businessGroupId":1},{"_id":0});while(lst.hasNext()) db.ReportGroup.insert(lst.next());
db.ReportGroup.find({ date: { $gte: %s, $lte: %s } })
db.ReportGroup.find({ date: { $gte: 20150402, $lte: 20150402 } })

-----将details中的每个children做sum 数据才正确
----构造查询结果
db.TaskReport.aggregate([
            {
            $match: {
                date: 20150504,
                taskType : 1
                }
         },
           {$unwind:"$details"},
            {$project: {_id:1,date:1,taskType:1,testResult: "$details.testResult",owner:"$details.owner"}}
           ])


-------check:
    db.TaskReport.find({date:20150504,taskType:1,"details.owner":"作者1"}).count()
    db.TaskReport.findOne({date:20150504,taskType:1,"details.owner":"GomeTest_1427110732170_Owner"},{"details.testResult":1,"details.owner":1})

---------------------------------------ReportGroup---------------------------------------------


  // 首先利$match筛选出where条件
        BasicDBObject[] array = {
                new BasicDBObject("resultVersion", new BasicDBObject("$gte",begindate)),
                new BasicDBObject("resultVersion", new BasicDBObject("$lt",enddate)),
                new BasicDBObject("isEnable",isEnable),
                new BasicDBObject("sceneName",sceneName),
                new BasicDBObject("smallSceneName",smallSceneName)
               // ,labelName!=""?new BasicDBObject("smallSceneName",smallSceneName):new BasicDBObject("1","1")
        };


        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", array);
        DBObject match = new BasicDBObject("$match", cond);

// 利用$project拼装group需要的数据，包含optCode列、processTime列
        DBObject fields = new BasicDBObject("resultVersion", 1); // 接口
        fields.put("isEnable", 1);// 耗时
        fields.put("sceneName", 1);// 渠道
        fields.put("smallSceneName", 1);// 平台

        DBObject project = new BasicDBObject("$project", fields);

// 利用$group进行分组
        DBObject _group = new BasicDBObject("resultVersion", "$resultVersion");
        _group.put("isEnable", "$isEnable");
        _group.put("sceneName", "$sceneName");
        _group.put("smallSceneName", "$smallSceneName");

        DBObject groupFields = new BasicDBObject("_id", _group);
//总数

        DBObject group = new BasicDBObject("$group", groupFields);
        AggregationOutput output = mongoTemplate.getCollection(JMTREPORT_NAME).aggregate(match, project, group);
         //mongoTemplate.

        Query query = new Query();
        query.addCriteria(Criteria.where("resultVersion").gte(begindate));
        query.addCriteria(Criteria.where("resultVersion").lte(enddate));
        query.addCriteria(Criteria.where("isEnable").is(isEnable));
        query.addCriteria(Criteria.where("sceneName").is(sceneName));
        query.addCriteria(Criteria.where("smallSceneName").is(smallSceneName));

        mongoTemplate.group(Criteria.where("resultVersion").gte(begindate), "collectionName", new GroupBy("GroupField"), JMTReport.class);

        BasicDBList list= (BasicDBList) mongoTemplate.getCollection(JMTREPORT_NAME).aggregate(match,project,group).getCommandResult().get("result");
        // return mongoDBDao.getJMTReportList(begindate,enddate,sceneName,smallSceneName,labelName);




        Query query = new Query();
        query.with(new Sort(Sort.Direction.ASC, "labelName"));
        query.addCriteria(Criteria.where("isEnable").is(true));
        query.addCriteria(Criteria.where("sceneName").is(sceneName));
        mongoTemplate.find(query,JMTReport.class,JMTREPORT_NAME);

       // mongoTemplate.group(Criteria.where("resultVersion").gte(begindate), "collectionName", new GroupBy("GroupField"), JMTReport.class);


        // 首先利$match筛选出where条件
        BasicDBObject[] array = {

                new BasicDBObject("isEnable",isEnable),
                new BasicDBObject("sceneName",sceneName),
        };


        BasicDBObject cond = new BasicDBObject();
        cond.put("$and", array);
        DBObject match = new BasicDBObject("$match", cond);

// 利用$project拼装group需要的数据，包含optCode列、processTime列
        DBObject fields = new BasicDBObject("smallSceneName", 1); // 接口


        DBObject project = new BasicDBObject("$project", fields);

        DBObject Sortfields = new BasicDBObject("labelName", 1); // 接口


        DBObject Sortproject = new BasicDBObject("$sort", Sortfields);


// 利用$group进行分组
        DBObject _group = new BasicDBObject("resultVersion", "$resultVersion");
        _group.put("isEnable", "$isEnable");
        _group.put("sceneName", "$sceneName");
        _group.put("smallSceneName", "$smallSceneName");

        DBObject groupFields = new BasicDBObject("_id", _group);
//总数

        DBObject group = new BasicDBObject("$group", groupFields);
        mongoTemplate.getCollection(JMTREPORT_NAME).find(match).sort(Sortproject);//.aggregate(match, project, group);
        //mongoTemplate.



*/
