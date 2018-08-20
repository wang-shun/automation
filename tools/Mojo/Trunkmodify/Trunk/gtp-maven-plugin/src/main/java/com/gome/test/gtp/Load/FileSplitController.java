package com.gome.test.gtp.Load;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by zhangjiadi on 15/11/5.
 */
public class FileSplitController implements IThreadController {
    private ThreadPoolExecutor pool;
    private BlockingQueue<Runnable> workQueue;
    private List<String> sList;
    private String fileName;
    private int fileIndex;

    public FileSplitController(ThreadPoolExecutor pool,
                               BlockingQueue<Runnable> workQueue, List<String> sList,
                               String fileName, int fileIndex) {
        super();
        this.pool = pool;
        this.workQueue = workQueue;
        this.sList = sList;
        this.fileName = fileName;
        this.fileIndex = fileIndex;
    }

    public void dispatchTask() throws InterruptedException {

        int maxTaskCount = LoadConfig.BATCH_ROW_COUNT;

        while(workQueue.size() > maxTaskCount) {
            Thread.sleep(5000);
        }
        pool.submit(new FileSplitProcessor(sList,fileName,fileIndex));
    }
}
