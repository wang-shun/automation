package com.gome.test.mock.core.service;


import com.gome.test.mock.core.Request;
import com.gome.test.mock.core.Response;
import com.gome.test.mock.core.Service;
import com.gome.test.mock.core.record.Record;
import com.gome.test.mock.core.record.RecordPool;
import com.gome.test.mock.utils.Logger;

/**
 * 负责记录输入和输出的数据。
 * 目前只支持YAML文件。
 * 
 * @author hannyu
 *
 * @param <Q> Request
 * @param <R> Response
 */
public class RecordService<Q, R> implements Service<Q, R> {
    private static final Logger log = new Logger(RecordService.class);
    
    private RecordPool<Q, R> recordPool;

    public RecordService(String ymlPath){
        recordPool = new RecordPool<Q, R>(ymlPath);
    }


    @Override
    public void doService(Request<Q> request, Response<R> response) throws Exception {
        Record<Q, R> record = new Record<Q, R>();
        record.setQ(request.pojo());
        record.setR(response.pojo());
        recordPool.add(record);
        
        recordPool.dump();
        log.info("dump success.");
    }

}


