package com.gome.test.mock.ssl;

/**
 * Created by chaizhongbao on 2015/9/23.
 */
public class SslContextFactory {

    public static SslCentextCreater getSslCtxCreater(){
        return new SslCentextCreater();
    }
}
