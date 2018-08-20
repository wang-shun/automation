package com.gome.test.gtp.Load;

import com.gome.test.gtp.ReportJMT;
import com.gome.test.gtp.model.LoadLogDetail;
import org.reflections.util.Utils;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhangjiadi on 16/1/22.
 */
public class LoadLogUtil {

    static {
        errorStrList=new ArrayList<String>();
        logDetailHashMap=new HashMap<String, LoadLogDetail>();
        lock = new ReentrantLock();// 锁对象
    }

    public static HashMap<String, LoadLogDetail> getLogDetailHashMap() {
        return logDetailHashMap;
    }

    private static HashMap<String,LoadLogDetail> logDetailHashMap;

    public static void setlogDetailHashMap(List<String> kesList)
    {


        for(String key : kesList)
        {
            logDetailHashMap.put(key,new LoadLogDetail());
        }
    }

    public static Lock lock ;

    public static List<String> errorStrList ;

    public static void saveErrorMess(String mess)
    {
        errorStrList.add(mess);
    }

    public static String getErrorMess()
    {
        StringBuffer errMess=new StringBuffer();
        for(String err: errorStrList)
        {
            errMess.append(err+"\n");
        }
        return errMess.toString();
    }

    //读取全部场景文件，和已经生成的Details进行比对
    public static List<LoadLogDetail> createAllLoadLogDetail(File file,List<LoadLogDetail> allDetails) throws Exception
    {
        List<LoadLogDetail> details=new ArrayList<LoadLogDetail>();

        BufferedReader br = null;
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        InputStreamReader inputStreamReader =null;
        String sLine = null;
        try {
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            inputStreamReader = new InputStreamReader(bufferedInputStream, "UTF-8");
            br = new BufferedReader(inputStreamReader, 20 * 1024 * 1024);
            while ((sLine = br.readLine()) != null) {
                sLine = sLine.trim();
                String [] messList=sLine.split("-");
                if(messList.length==5)
                {
                    LoadLogDetail detail=new LoadLogDetail();
                    detail.setSceneName(messList[3]);
                    detail.setSmallSceneName(messList[2]);
                    int isHasTime=messList[1].indexOf("_");


                    detail.setResultVersion(isHasTime>0?messList[1].substring(0,isHasTime):messList[1]);
                    detail.setTimeVersion(isHasTime>0?messList[1].substring(isHasTime+1):messList[1]);
                    details.add(detail);
                }else
                {
                    LoadLogUtil.saveErrorMess(String.format("生成全部场景明细日志失败，文件路局：%s,当前内容:%s ", file.getPath(),sLine));
                }
            }
        }catch (Exception ex)
        {
            LoadLogUtil.saveErrorMess(String.format("读取异常日志文件失败，文件路径%s,原因 %s ", file.getPath(), ex.getMessage()));

        }finally {
            if(fileInputStream != null)
                fileInputStream.close();
            if(bufferedInputStream!=null)
                bufferedInputStream.close();
            if(inputStreamReader!=null)
                inputStreamReader.close();
            if(br!=null)
                br.close();
        }

        for(LoadLogDetail detail: details)
        {
            for(LoadLogDetail logdetail : allDetails )
            {
                if(detail.getSceneName().equals(logdetail.getSceneName()) && detail.getSmallSceneName().equals(logdetail.getSmallSceneName()) && detail.getResultVersion().equals(logdetail.getResultVersion()))
                    detail.setTotal(logdetail.getTotal());
            }
        }

        return details;
    }

    public static void createLogDetail(ReportJMT model)
    {
        try
        {
            lock.lock();
            String key=model.getCheckDataKey();
            if(logDetailHashMap.containsKey(key))
            {
                LoadLogDetail detail=logDetailHashMap.get(key);
                if(detail.getStartTime()==null)
                    detail.setStartTime(Long.MAX_VALUE);
                if(detail.getEndTime()==null)
                    detail.setEndTime(Long.MIN_VALUE);


                if(Utils.isEmpty(detail.getSceneName()) || Utils.isEmpty(model.getSmallSceneName()) || Utils.isEmpty(model.getLabelName()) || Utils.isEmpty(model.getResultVersion()) ) {
                    detail.setLabelName(model.getLabelName());
                    detail.setSceneName(model.getSceneName());
                    detail.setSmallSceneName(model.getSmallSceneName());
                    detail.setResultVersion(model.getResultVersion());
                    detail.setTimeVersion(model.getTimeVersion());
                }
                int totalcount=Integer.valueOf(model.getElapsedCount());
                int errorcount = Integer.valueOf(model.getErrorCount());

                Long timeStamp =Long.valueOf(model.gettimeStamp());
                detail.setTotal(totalcount+detail.getTotal());
                detail.setError(detail.getError()+errorcount);
                detail.setSuccess(detail.getSuccess() + (totalcount - errorcount));
                detail.setDuration(detail.getDuration()+Long.valueOf(model.getSumElapsed()));
                detail.setStartTime(detail.getStartTime()< timeStamp ? detail.getStartTime() : timeStamp);
                detail.setEndTime(detail.getEndTime()> timeStamp ? detail.getEndTime() : timeStamp);
            }

        }catch (Exception ex)
        {
            errorStrList.add(String.format("转换详细日志失败，原内容%s,原因%s ",model.toString(),ex.getMessage()) );
        }finally {
            lock.unlock();
        }
    }

}
