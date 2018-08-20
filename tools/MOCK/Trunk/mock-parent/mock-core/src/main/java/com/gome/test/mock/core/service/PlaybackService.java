package com.gome.test.mock.core.service;

import com.gome.test.mock.core.Request;
import com.gome.test.mock.core.Response;
import com.gome.test.mock.core.Service;
import com.gome.test.mock.core.record.Record;
import com.gome.test.mock.core.record.RecordPool;
import com.gome.test.mock.utils.Logger;

import java.io.IOException;




/**
 * 负责回放
 * 
 * @author wyhanyu
 *
 * @param <Q>
 * @param <R>
 */
public class PlaybackService<Q, R> implements Service<Q, R> {
    private static final Logger log = new Logger(PlaybackService.class);
    

    private int index;
    private int size;
    private RecordPool<Q, R> recordPool;
    
    public PlaybackService(String ymlPath) throws IOException{
        recordPool = new RecordPool<Q, R>(ymlPath);
        log.debug("need load records on {}.", ymlPath);
        recordPool.load();
        size = recordPool.size();
        log.debug("load {} records.", size);
        
    }


    @Override
    public void doService(Request<Q> request, Response<R> response)
            throws Exception {
        
        Record<Q, R> record = recordPool.get(index%size);
        request.pojo(record.getQ());
        response.pojo(record.getR());
        
        log.info("palyback NO.{} record.", index);
        index++;
    }

}
