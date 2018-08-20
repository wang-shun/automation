package com.gome.test.mock.core;



public interface Service<Q, R> {
    

    /**
     * Do something in Service.
     * 
     * @param request
     * @param response
     * @throws Exception
     */
    public void doService(Request<Q> request, Response<R> response) throws Exception;
}
