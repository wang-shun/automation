package com.gome.test.gtp.Load;

import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by zhangjiadi on 15/11/5.
 */
public class LoadSaveController implements  IThreadController {
    private ThreadPoolExecutor pool;
    private BlockingQueue<Runnable> workQueue;
    private String fileName;
    private int fileIndex;
    private String environment;
    private String dbTablename;
    private int taskId;
    private int taskListId;
    private ConcurrentLinkedQueue queue;
    public LoadSaveController(ThreadPoolExecutor pool,BlockingQueue<Runnable> workQueue,
                               String fileName, int fileIndex,String environment,String dbTablename,
                               int taskId,int taskListId,ConcurrentLinkedQueue queue) {
        super();
        this.pool = pool;
        this.workQueue = workQueue;
        this.fileName = fileName;
        this.fileIndex = fileIndex;
        this.environment=environment;
        this.dbTablename=dbTablename;
        this.taskId=taskId;
        this.taskListId=taskListId;
        this.queue= queue;
    }

    public void dispatchTask() throws InterruptedException {

        int maxTaskCount = LoadConfig.BATCH_ROW_COUNT;

        while(workQueue.size() > maxTaskCount) {
            Thread.sleep(5000);
        }
        pool.submit(
                new LoadSaveProcessor(fileName,fileIndex,environment,dbTablename,taskId,taskListId,this.queue));
    }
}
