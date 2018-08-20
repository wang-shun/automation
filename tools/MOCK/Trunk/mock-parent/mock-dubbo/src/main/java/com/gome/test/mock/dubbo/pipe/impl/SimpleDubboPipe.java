package com.gome.test.mock.dubbo.pipe.impl;


import com.gome.test.mock.core.Request;
import com.gome.test.mock.core.Response;
import com.gome.test.mock.dubbo.DubboData;
import com.gome.test.mock.dubbo.pipe.AbstractDubboPipe;

/**
 * 简单Dubbo协议
 * DubboHeader + body(byte[])
 * 
 */
public final class SimpleDubboPipe extends AbstractDubboPipe<DubboData, DubboData> {

    @Override
    public void decodeDubboRequest(DubboData dd, Request<DubboData> request)
            throws Exception {
        request.pojo(dd);
    }

    @Override
    public void encodeDubboRequest(Request<DubboData> request, DubboData dd) throws Exception {
        dd.setDubboHeader(request.pojo().getDubboHeader());
        dd.setBody(request.pojo().getBody());
    }

    @Override
    public void decodeDubboResponse(DubboData dd, Response<DubboData> response)
            throws Exception {
        response.pojo(dd);
    }

    @Override
    public void encodeDubboResponse(Response<DubboData> response, DubboData dd)
            throws Exception {
        dd.setDubboHeader(response.pojo().getDubboHeader());
        dd.setBody(response.pojo().getBody());
    }

}
