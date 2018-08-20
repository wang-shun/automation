package com.gome.test.mock.core;


import com.gome.test.mock.core.util.RequestAttr;
import com.gome.test.mock.core.util.SessionAttr;

public interface Request<Q> {
    
    public long order();
    
    public boolean hasPojo();
    public Q pojo();
    public void pojo(Q pojo);
    
    public RequestAttr requestAttr();
    public SessionAttr sessionAttr();
    
}



