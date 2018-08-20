package com.gome.test.gtp.Load;

import com.gome.test.gtp.ReportBo;
import com.gome.test.gtp.utils.Constant;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

import java.io.File;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by zhangjiadi on 15/11/5.
 */
public class LoadSave {

    private String environment;
    private String dbTablename;
    private String dbJmtAggReportname;

    public LoadSave(String environment,String dbTablename,String dbJmtAggReportname)
    {
        this.environment=environment;
        this.dbTablename=dbTablename;
        this.dbJmtAggReportname=dbJmtAggReportname;
    }


    public void save(String json)
    {

        new ReportBo().insert(json, this.dbJmtAggReportname);
    }

    public WriteResult deleteAGReport(int taskid,int tasklistid,double grpThreads,String sceneName,String resultVersion,String timeVersion,String smallSceneName,String labelName) {

        BasicDBObject query = new BasicDBObject();
        query.put("sceneName",sceneName);
        query.put("resultVersion",resultVersion);
        query.put("timeVersion", timeVersion);
        query.put("smallSceneName", smallSceneName);
        query.put("labelName", labelName);
        query.put("tasklistid",tasklistid);
        query.put("taskid",taskid);
        query.put("grpThreads",grpThreads);

        System.out.println(String.format("db.%s.remove(%s)",this.dbJmtAggReportname, query.toString()));

        return new ReportBo().delete(this.dbJmtAggReportname,query);
    }


    public void save(File file,int tempFileLength,int taskId,int taskListId,ConcurrentLinkedQueue queue) throws Exception
    {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(LoadConfig.THREAD_NUMBER, LoadConfig.THREAD_NUMBER, 600, TimeUnit.SECONDS, workQueue);
        String filePath=file.getPath();
        for (int i=0;i< tempFileLength;i++)
        {
           new LoadSaveController(pool,workQueue,filePath,i,environment,dbTablename,taskId,taskListId,queue).dispatchTask();
        }

        pool.shutdown();

        while(!pool.isTerminated()) {
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }

        while (true)
        {
            if(pool.isTerminated())
            {
                break;
            }
        }
    }
}
