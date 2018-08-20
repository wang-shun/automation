package com.gome.test.mock.ssl;

import javax.net.ssl.SSLContext;

/**
 * Created by chaizhongbao on 2015/9/23.
 */
public interface SslCtxCreateInter {
    //创建SSL上下文对象
    public SSLContext createSSLContextInstance(String password,String keystorePath,String trutststorePath);
}
