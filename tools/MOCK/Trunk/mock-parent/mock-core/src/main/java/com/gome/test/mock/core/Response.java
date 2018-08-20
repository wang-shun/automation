package com.gome.test.mock.core;


import com.gome.test.mock.core.util.RequestAttr;
import com.gome.test.mock.core.util.SessionAttr;

public interface Response<R> {
    
    
    public boolean hasPojo();
    public R pojo();
    public void pojo(R pojo);

    public RequestAttr requestAttr();
    public SessionAttr sessionAttr();
    
    public void disconnectOnComplete();
    public void done();
    
}
