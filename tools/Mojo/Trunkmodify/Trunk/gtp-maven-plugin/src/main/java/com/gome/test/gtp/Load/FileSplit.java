package com.gome.test.gtp.Load;

import com.gome.test.gtp.ReportJMT;
import com.gome.test.gtp.model.JMTReport;
import com.gome.test.gtp.utils.Util;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangjiadi on 15/11/5.
 */
public class FileSplit {

    public List<String> getData() {
        return data;
    }

    private List<String> data;
    private String environment;

    public FileSplit(String environment)
    {
        this.data=new ArrayList<String>();
        this.environment=environment;
    }

    public int split(File file) throws  Exception {

        String fileName=file.getPath();

        int fileIndex = 0;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(LoadConfig.THREAD_NUMBER, LoadConfig.THREAD_NUMBER, 600, TimeUnit.SECONDS, workQueue);
        BufferedReader br = null;
        int row = 0;
        String sLine = null;
        List<String> sList = new ArrayList<String>();
        try {
            br = Util.getReader(file);
            while ((sLine = br.readLine()) != null) {
                row++;
                if(row==1) {
                    continue;
                }
                sList.add(sLine);
                ReportJMT model = new ReportJMT(sLine, environment);
                String key=model.getCheckDataKey();
                if(!data.contains(key))
                {
                    data.add(key);
                }

                if (row % LoadConfig.BATCH_ROW_COUNT == 0) {
                    new FileSplitController(pool, workQueue, sList, fileName, fileIndex).dispatchTask();
                    sList = new ArrayList<String>();
                    fileIndex++;
                }
            }
        }catch (Exception ex)
        {
            throw  ex;
        }finally {
            if(br!=null)
                br.close();
        }

        if(sList.size()>0) {
            new FileSplitController(pool, workQueue, sList, fileName, fileIndex).dispatchTask();
            fileIndex++;
        }
        while(workQueue.size()>1) {
            Thread.sleep(5000);
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

        return fileIndex;
    }


}
