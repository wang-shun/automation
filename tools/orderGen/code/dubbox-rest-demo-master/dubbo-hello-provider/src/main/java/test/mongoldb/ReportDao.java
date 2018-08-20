package test.mongoldb;


import com.gome.test.gtp.model.AGReport;
import com.gome.test.gtp.model.JMTReport;
import com.gome.test.gtp.model.JmtReportData;
import com.gome.test.gtp.utils.Constant;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ReportDao {

    @Autowired
    private MongoDBDao mongoDBDao;

    public void init(String address, int port) {
        mongoDBDao = new MongoDBDao();
        mongoDBDao.init(address, port);
    }

    public WriteResult insertList(List<? extends Object> object) {
        return mongoDBDao.insertList(object);
    }

    public WriteResult delete(String collectionName, DBObject query) {
        return mongoDBDao.delete(collectionName, query);
    }

    public WriteResult insert(Object object) {
        return mongoDBDao.insert(object);
    }

    public WriteResult insert(String json, String collectionName) {
        return mongoDBDao.insert(json, collectionName);
    }

    public <T> List<T> getAll(Class<T> entityClass) {
        return mongoDBDao.getAll(entityClass);
    }

    public List getJMTSceneNameList() {
        return mongoDBDao.getJMTSceneNameList();
    }

    public HashMap<String,List<String>> getJMTSceneNameMap(String template,Boolean isEnable)
    {
        return mongoDBDao.getJMTSceneNameMap(template,isEnable);
    }

    public List getJMTSmallSceneNameList(String sceneName, Boolean isEnable) {
        return mongoDBDao.getJMTSmallSceneNameList(sceneName, isEnable);
    }

    public List getJMTLabelNameList(String sceneName, Boolean isEnable) {
        return mongoDBDao.getJMTLabelNameList(sceneName, isEnable);
    }

    public List  getJMTenvironmentList(String sceneName, Boolean isEnable) {
        return mongoDBDao.getJMTenvironmentList(sceneName, isEnable);
    }

    public String getEndVersion(String sceneName,String smallSceneName,String labelName,String environment,Boolean isEnable) {
        return mongoDBDao.getEndVersion(sceneName,smallSceneName,labelName,environment, isEnable);
    }

    public List<JMTReport> getJMTReportList(String resultVersion,String resultEndVersion,String timeVersion, String environment, String sceneName, String smallSceneName, String labelName,String template,String grpThreads, Boolean isEnable) {
        return mongoDBDao.getJMTReportList(resultVersion,resultEndVersion,timeVersion, environment, sceneName, smallSceneName, labelName,template,grpThreads, isEnable);
    }

    public List getJmtHistroy(String taskId,String resultVersion,String resultEndVersion,String timeVersion, String environment, String sceneName, String smallSceneName, String template, Boolean isEnable)
    {
        return mongoDBDao.getJmtHistroy(taskId, resultVersion, resultEndVersion, timeVersion,  environment,  sceneName,  smallSceneName,  template,  isEnable);
    }

    public int getJmtCount(String version,String timeVersion, String environment, String sceneName, String smallSceneName, String template, Boolean isEnable)
    {
        return mongoDBDao.getJmtCount(version,timeVersion,environment,sceneName,smallSceneName,template,isEnable);
    }


    public List<JMTReport> getLabelReport(String taskid,String resultVersion,String resultEndVersion, String environment, String sceneName, String smallSceneName, String labelName,String template, Boolean isEnable) {
        return mongoDBDao.getLabelReport(taskid,resultVersion,resultEndVersion, environment, sceneName, smallSceneName, labelName,template, isEnable);
    }


    public List<JMTReport> getGrpThreads(String sceneName,String environment,String resultVersion,String timeVersion,String template,Boolean isEnable) {
        return mongoDBDao.getGrpThreads( sceneName,  environment,  resultVersion, timeVersion, template,  isEnable);
    }


    public List getJMTLabelNameList(String resultVersion,String resultEndVersion, String environment, String sceneName, String smallSceneName, String template, Boolean isEnable)
    {
        return  mongoDBDao.getJMTLabelNameList(resultVersion,resultEndVersion, environment, sceneName, smallSceneName, template, isEnable);
    }



    public <T> List<T> getByCondition(Class<T> entityClass, Query query) {
        return mongoDBDao.getByCondition(entityClass, query);
    }

    /**
     * @param dates
     * @param taskType
     * @param
     * @return [[group1], [group2]....]
     */
    public int[][] getReport(Integer dates[], Integer[] taskType, String collectionName, String groupField, Object[] groupFieldValue, int reportType) {
        if (dates.length == 0)
            return new int[][]{};

        int beinDate = dates[0];
        int endDate = dates[dates.length - 1];

        HashMap<Object, HashMap<Integer, HashMap<String, Integer>>> list = mongoDBDao.getReportResult(beinDate, endDate, taskType, collectionName, groupField, groupFieldValue);

        return convertToResult(dates, groupFieldValue, list, reportType);
    }

    public int[][] getReport(Integer[] dates, Integer[] group, String[] owner, Integer[] taskType, String collectionName, int reportType, String groupField) {
        if (dates.length == 0)
            return new int[][]{};

        int beinDate = dates[0];
        int endDate = dates[dates.length - 1];

        HashMap<Object, HashMap<Integer, HashMap<String, Integer>>> list = mongoDBDao.getReportResult(beinDate, endDate, group, owner, taskType, collectionName, groupField);

        if (groupField.equals(Constant.GROUPID))
            return convertToResult(dates, group, list, reportType);
        else
            return convertToResult(dates, owner, list, reportType);
    }

    /**
     * @param dates
     * @param taskType
     * @param
     * @return [[group1], [group2]....]
     */
    public int[][] getReport(int dates[], Integer[] taskType, String collectionName, String groupField, Object[] groupFieldValue, int reportType) {
        if (dates.length == 0)
            return new int[][]{};

        int beinDate = dates[0];
        int endDate = dates[dates.length - 1];

        HashMap<Object, HashMap<Integer, HashMap<String, Integer>>> list = mongoDBDao.getReportResult(beinDate, endDate, taskType, collectionName, groupField, groupFieldValue);

        return convertToResult(dates, groupFieldValue, list, reportType);
    }

    public String[][] getlinejmtreport(String resultVersion, String environment, String sceneName, String labelName, Boolean isEnable) {
        LinkedHashMap<String, List<String>> list = mongoDBDao.getlinejmtreport(resultVersion, environment, sceneName, labelName, isEnable);
        //排序
        list=sortSmallSceneNameHashMap(list);
        //处理规则
        HashMap<String,Integer> map=isAVG(list);
        String mapkey="";
        Integer v=0;
        for(String map_Key:map.keySet())
        {
            mapkey=map_Key;
            v= (Integer)map.get(map_Key);
        }
        //存值
        String[][] result = new String[3][list.size()];
        int index = 0;
        for (String key : list.keySet()) {
            List<String> values = list.get(key);

            result[0][index] = key;//SmallSceneName
            result[1][index] =mapkey!=""&& mapkey.equals("AVG")? (Double.valueOf(values.get(0)) /  v ) + "_ms/"+v : values.get(0)+"_ms";//avg
            result[2][index] = mapkey!=""&& mapkey.equals("TPS")? (Double.valueOf(values.get(1)) /  v ) + "_s/"+v : values.get(1)+"_s";//tps
            index++;

        }

        return result;
    }

    private HashMap<String,Integer> isAVG(LinkedHashMap<String, List<String>> list)
    {
        HashMap<String,Integer> result=new HashMap<String, Integer>();
        Double maxAVG=getAVGMaxValue(list);
        Double maxTPS=getTPSMaxValue(list);
        Double b=maxAVG>maxTPS?maxAVG/maxTPS:maxTPS/maxAVG;
        b=b/2;
        Integer v=b.intValue();
        if(v>2)
        {

            result.put((maxAVG>maxTPS?"AVG":"TPS"),v);
        }
        return  result;

    }





    private Double getAVGMaxValue(LinkedHashMap<String, List<String>> list)
    {
        return getMaxValue(list,0);
    }
    private Double getTPSMaxValue(LinkedHashMap<String, List<String>> list)
    {
        return getMaxValue(list,1);
    }

   private Double getMaxValue( LinkedHashMap<String, List<String>> list ,int i)
   {
       Double maxValue=Double.MIN_VALUE;
       for (String key : list.keySet()) {
           List<String> values = list.get(key);
           Double val=Double.valueOf(values.get(i));
           maxValue= val>maxValue? val:maxValue;
       }
       return  maxValue;
   }

    private List conventList(List<String> list,int count)
    {

        List<String> result=new ArrayList<String>();
        for(int i=list.size()-1;i>=0;i--)
        {
            count=count-1;
            result.add(list.get(i));
            if(count==0)
                break;
        }
        return result;
    }

    public LinkedHashMap<String,List<String>> sortHashMap(LinkedHashMap<String, List<String>> list)
    {
        LinkedHashMap<String, List<String>> result=new LinkedHashMap<String, List<String>>();
        String minValue="0";
        while (result.size()<list.keySet().size())
        {
            minValue=getValue(list, minValue);
            result.put(minValue,list.get(minValue));
        }
        return result;
    }

    public LinkedHashMap<String,List<String>> sortSmallSceneNameHashMap(LinkedHashMap<String, List<String>> list)
    {
        LinkedHashMap<String, List<String>> result=new LinkedHashMap<String, List<String>>();
        Integer minValue=0;
        while (result.size()<list.keySet().size())
        {
           HashMap<String,Integer> map =getSmallScenenameValue(list, minValue);
            for(String key:map.keySet()) {
                result.put(key, list.get(key));
                minValue=map.get(key);
            }
        }

        return result;
    }
    private HashMap<String,Integer>  getSmallScenenameValue(LinkedHashMap<String, List<String>> list,Integer minValue)
    {
        HashMap<String,Integer> result=new HashMap<String, Integer>();
        Integer min = Integer.MAX_VALUE;
        String keyValue="";
        for (String key : list.keySet()) {
            String keyv=key;
            key=key.replaceAll("[s^]","");
            String[] keyList=key.split("X");
            if(Integer.valueOf(keyList[0]) * Integer.valueOf(keyList[1]) < min && Integer.valueOf(keyList[0]) * Integer.valueOf(keyList[1]) > minValue ) {
                min =Integer.valueOf(keyList[0]) * Integer.valueOf(keyList[1]);
                keyValue=keyv;
            }
        }
        result.put(keyValue,min);
        return result;
    }

    private String getValue(LinkedHashMap<String, List<String>> list,String minValue)
    {
        Integer min = Integer.MAX_VALUE;
        for (String key : list.keySet()) {
            if(Integer.valueOf(key)<min && Integer.valueOf(key) > Integer.valueOf(minValue) )
                min=Integer.valueOf(key);
        }
        return min.toString();
    }

    public String[][] getHistoryJMTReport(String sceneName,String smallSceneName,String labelName,String resultVersion,String environment,Integer count,Boolean isEnable)
    {
        LinkedHashMap<String, List<String>> list = mongoDBDao.getHistoryJMTReport(sceneName,smallSceneName,labelName,resultVersion,environment,isEnable);

        list=sortHashMap(list);



       List<String> keyList=new ArrayList<String>();

        for (String key : list.keySet()) {
            keyList.add(key);
            if(key.equals(resultVersion))
            {
                keyList=  conventList(keyList,count);
                break;
            }
        }
        //处理规则
        HashMap<String,Integer> map=isAVG(list);
        String mapkey="";
        Integer v=0;
        for(String map_Key:map.keySet())
        {
            mapkey=map_Key;
            v= (Integer)map.get(map_Key);
        }

        String[][] result = new String[3][keyList.size()];
        int index = 0;
        for(int i= keyList.size()-1;i>=0;i--)
       {
             String key=keyList.get(i);
            List<String> values = list.get(key);

            result[0][index] = key;//resultVersion
           result[1][index] =mapkey!=""&& mapkey.equals("AVG")? (Double.valueOf(values.get(0)) /  v ) + "_ms/"+v : values.get(0)+"_ms";//avg
           result[2][index] = mapkey!=""&& mapkey.equals("TPS")? (Double.valueOf(values.get(1)) /  v ) + "_s/"+v : values.get(1)+"_s";//tps

           index++;

        }

        return result;
    }

    public void updateReportOwner(int minDate, int maxDate, int taskType) {
        mongoDBDao.updateReportOwner(minDate, maxDate, taskType);
    }

    public void updateReportGroup(int minDate, int maxDate, int taskType, Map<Integer, Integer> taskIdAndGroupId) {
        mongoDBDao.updateReportGroup(minDate, maxDate, taskType, taskIdAndGroupId);
    }

    public List<AGReport> getAGReportByDate(long date) {
        Query query = new Query();
        Criteria criteria = Criteria.where("date").is(date);
//        criteria.and("date").is("20160421");
        query.addCriteria(criteria);
        return mongoDBDao.getByCondition(AGReport.class, query);
    }


    public List<AGReport> getAGReportByDateType(long date, int type) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("date").is(date);
        criteria.and("taskType").is(type);
        query.addCriteria(criteria);
        return mongoDBDao.getByCondition(AGReport.class, query);
    }
    public List<AGReport> getJobReportByTaskId(String taskId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("taskId").is(taskId);
        query.addCriteria(criteria);
        return mongoDBDao.getByCondition(AGReport.class, query);
    }

    /**
     * @param dates
     * @param list
     * @return [[group1's date],[group2's date]....]
     */
    private int[][] convertToResult(Integer[] dates, Object[] groups, HashMap<Object, HashMap<Integer, HashMap<String, Integer>>> list, int reportType) {
        int[][] result = new int[groups.length][dates.length];

        for (int i = 0; i < groups.length; i++) {
            int[] data = new int[dates.length];
            if (list.containsKey(groups[i])) {
                HashMap<Integer, HashMap<String, Integer>> groupMap = list.get(groups[i]);

                for (int j = 0; j < dates.length; j++) {
                    if (groupMap.containsKey(dates[j])) {
                        switch (reportType) {
                            case Constant.REPORT_CASE_NUMBER:
                                data[j] = groupMap.get(dates[j]).get(Constant.CASE_NUM);
                                break;
                            case Constant.REPORT_PASS_NUMBER:
                                data[j] = groupMap.get(dates[j]).get(Constant.PASS_NUM);
                                break;
                            case Constant.REPORT_PASS_RATE:
                                if (groupMap.get(dates[j]).get(Constant.CASE_NUM) == 0)
                                    data[j] = 0;
                                else
                                    data[j] = (int) (((double) groupMap.get(dates[j]).get(Constant.PASS_NUM) / groupMap.get(dates[j]).get(Constant.CASE_NUM)) * 100);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            result[i] = data;
        }

        return result;
    }


    /**
     * @param dates
     * @param list
     * @return [[group1's date],[group2's date]....]
     */
    private int[][] convertToResult(int[] dates, Object[] groups, HashMap<Object, HashMap<Integer, HashMap<String, Integer>>> list, int reportType) {
        int[][] result = new int[groups.length][dates.length];

        for (int i = 0; i < groups.length; i++) {
            int[] data = new int[dates.length];
            if (list.containsKey(groups[i])) {
                HashMap<Integer, HashMap<String, Integer>> groupMap = list.get(groups[i]);

                for (int j = 0; j < dates.length; j++) {
                    if (groupMap.containsKey(dates[j])) {
                        switch (reportType) {
                            case Constant.REPORT_CASE_NUMBER:
                                data[j] = groupMap.get(dates[j]).get(Constant.CASE_NUM);
                                break;
                            case Constant.REPORT_PASS_NUMBER:
                                data[j] = groupMap.get(dates[j]).get(Constant.PASS_NUM);
                                break;
                            case Constant.REPORT_PASS_RATE:
                                if (groupMap.get(dates[j]).get(Constant.CASE_NUM) == 0)
                                    data[j] = 0;
                                else
                                    data[j] = (int) (((double) groupMap.get(dates[j]).get(Constant.PASS_NUM) / groupMap.get(dates[j]).get(Constant.CASE_NUM)) * 100);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            result[i] = data;
        }

        return result;
    }

    public List getDistinct(String collectionName, String key) {
        return mongoDBDao.getDistinct(collectionName, key);
    }


    public void update(Query query, Update update) {

        this.mongoDBDao.update(query, update);

    }

    public void updateAGGReport(Query query,Update update)
    {
        this.mongoDBDao.updateAGGReport(query, update);

    }


    public void updateAGReport(int minDate, int maxDate, int taskType, Map<Integer, Integer> taskIdAndGroupId) {
        mongoDBDao.updateAGReport(minDate, maxDate, taskType, taskIdAndGroupId);
    }

    public List<String> getCaseOwnerByGroupId(int groupId) {
        return mongoDBDao.getCaseOwnerByGroupId(groupId);
    }

    public TreeMap<String,List<String>> getResultVersion(String sceneName,String smallSceneName,String labelName,String environment,Boolean isEnable) {
        return mongoDBDao.getResultVersion(sceneName, smallSceneName, labelName, environment,"", isEnable);
    }

    public TreeMap<String,List<String>> getVersionTime(String sceneName,String smallSceneName,String labelName,String environment,String resultDate,Boolean isEnable)
    {
        return mongoDBDao.getResultVersion(sceneName, smallSceneName, labelName, environment,resultDate, isEnable);
    }

    public List<String> getJMTTaskIdList(String sceneName,String template,Boolean isEnable)
    {
        return mongoDBDao.getJMTTaskIdList(sceneName, template, isEnable);
    }


    public List<JMTReport> getLabelReportByAGGReport(String timearg, String taskid,String resultVersion,String resultEndVersion, String environment, String sceneName, String smallSceneName, String labelName,String template, Boolean isEnable) {
        return mongoDBDao.getLabelReportByAGGReport( timearg,  taskid, resultVersion, resultEndVersion,  environment,  sceneName,  smallSceneName,  labelName, template,  isEnable);
    }

    public List<JmtReportData> getJMTAGGReportList(String resultVersion,String resultEndVersion,String timeVersion, String environment, String sceneName, String smallSceneName, String labelName,String template,String grpThreads,String timeavg, Boolean isEnable) {
        return mongoDBDao.getJMTAGGReportList(resultVersion,resultEndVersion,timeVersion, environment, sceneName, smallSceneName, labelName,template,grpThreads,timeavg, isEnable);
    }


    public List<JMTReport> getJMTReportByResultVersion(String resultVersion)
    {
        return mongoDBDao.getJMTReportByResultVersion(resultVersion);
    }

}
