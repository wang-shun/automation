package com.gome.test.mock.pipes.http.pipe.impl;

import com.gome.test.mock.core.Request;
import com.gome.test.mock.core.Response;
import com.gome.test.mock.pipes.http.SimpleHttpRequest;
import com.gome.test.mock.pipes.http.SimpleHttpResponse;
import com.gome.test.mock.pipes.http.pipe.AbstractHttpPipe;

/**
 * 简单实现
 */
public final class SimpleHttpPipe extends AbstractHttpPipe<SimpleHttpRequest, SimpleHttpResponse> {
    //private static final Logger log = new Logger(SimpleHttpPipe.class);

    @Override
    public void decodeHttpRequest(SimpleHttpRequest httpReq, Request<SimpleHttpRequest> request) throws Exception {
        request.pojo(httpReq);
    }

    @Override
    public void encodeHttpRequest(Request<SimpleHttpRequest> request, SimpleHttpRequest httpReq) throws Exception {
        httpReq.copyOf(request.pojo());
    }

    @Override
    public void decodeHttpResponse(SimpleHttpResponse httpRes, Response<SimpleHttpResponse> response) {
        response.pojo(httpRes);
    }

    @Override
    public void encodeHttpResponse(Response<SimpleHttpResponse> response, SimpleHttpResponse httpRes) {
        httpRes.copyOf(response.pojo());
    }

}
