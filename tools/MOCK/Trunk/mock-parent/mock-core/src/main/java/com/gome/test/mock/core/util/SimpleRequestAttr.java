package com.gome.test.mock.core.util;


public class SimpleRequestAttr extends Attributes implements RequestAttr {
    
    private long order;
    public SimpleRequestAttr(long order){
        super();
        this.order = order;
    }
    
    @Override
    public long order(){
        return order;
    }   
}
