package com.gome.test.api.verify;

import com.gome.test.api.testng.BaseConfig;
import com.gome.test.api.verify.HttpResponseVerify;
import org.testng.annotations.Test;

public class HttpResponseVerifyTest {

    public HttpResponseVerifyTest() {
        verify = new HttpResponseVerify();
        verify.setStatusCode(200);
    }

    @Test
    public void testStatusCodeEqualTo() throws Exception {
        verify.statusCodeEqualTo("200");
    }

    @Test
    public void testVerifyExecuteSQLResult() throws Exception {
        BaseConfig baseConfig = new BaseConfig();
//        baseConfig.setUpBeforeSuite("HttpMessageClient");
        verify.verifyExecuteSQLResult("sql1", "a");
        verify.verifyExecuteSQLResult("sql2", "a,b");
        verify.verifyExecuteSQLResult("sql3", "a;b");
        verify.verifyExecuteSQLResult("sql4", "a,b;c,d");
        baseConfig.tearDownAfterSuite();
    }

    private final HttpResponseVerify verify;
}
