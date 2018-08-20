package com.ofo.test.api.verify;

import com.ofo.test.api.annotation.Verify;
import com.ofo.test.api.utils.HttpClient;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import com.ofo.test.utils.Logger;
import org.testng.Assert;

public class HttpResponseVerify {

    protected HttpClient httpClient;
    protected Map<String, String> inputParams;
    protected String response;
    protected Integer statusCode;

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setInputParams(Map<String, String> inputParams) {
        this.inputParams = inputParams;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @Verify(description = "验证返回值是expected")
    public void statusCodeEqualTo(String expected) throws Exception {
        Assert.assertEquals(statusCode.toString(), expected);
    }

    @Verify(description = "验证执行[sqlId]对应SQL语句得到的返回值与[expected]相符")
    public void verifyExecuteSQLResult(String sqlId, String expected) throws Exception {
        List<String[]> results = DBVerifyHelper.querySQL(sqlId);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < results.size(); ++i) {
            if (i != 0) {
                sb.append(";");
            }
            String[] result = results.get(i);
            for (int j = 0; j < result.length; ++j) {
                if (j != 0) {
                    sb.append(",");
                }
                sb.append(result[j]);
            }
        }
        Assert.assertEquals(sb.toString(), expected);
    }

    @Verify(description = "将返回格式化为合法的json")
    public void checkAndRemoveWrapper(String prefix) throws UnsupportedEncodingException
    {
        Assert.assertTrue(response.replaceAll("^[\t]", "").trim().startsWith(prefix+"("), MessageFormat.format("返回数据不以{0}开头", prefix));
        response = response.replaceAll("^[\t]", "").trim().substring(prefix.length()+1).replaceAll("[)]$", "").trim();
        Logger.info("格式化为:" + response);
    }
}
