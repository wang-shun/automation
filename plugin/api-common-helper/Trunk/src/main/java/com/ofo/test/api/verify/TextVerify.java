package com.ofo.test.api.verify;
import com.ofo.test.api.annotation.Verify;
import com.ofo.test.context.ContextUtils;
import com.ofo.test.api.testng.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.testng.Assert;

public class TextVerify extends HttpResponseVerify {

    @Verify(description = "验证返回值与[expected]相等")
    public void equalTo(String expected) throws Exception {
        assertThat(response, Matchers.equalTo(expected));
    }

    @Verify(description = "验证返回值与[expected]相等，不区分大小写")
    public void equalToIgnoreCase(String expected) throws Exception {
        assertThat(response, Matchers.equalToIgnoringCase(expected));
    }

    @Verify(description = "验证返回值与[expected]相等，忽略前后空白字符")
    public void equalToIgnoreWhiteSpace(String expected) throws Exception {
        assertThat(response, Matchers.equalToIgnoringWhiteSpace(expected));
    }

    @Verify(description = "验证返回值以前缀值[prefix]开头")
    public void startsWith(String prefix) throws Exception {
        assertThat(response, Matchers.startsWith(prefix));
    }

    @Verify(description = "验证返回值以后缀值[suffix]结尾")
    public void endsWith(String suffix) throws Exception {
        assertThat(response, Matchers.endsWith(suffix));
    }

    @Verify(description = "验证返回值包含[expected]")
    public void contains(String expected) throws Exception {
        Assert.assertTrue(response.contains(expected));
    }

    @Verify(description = "验证返回值与执行[sqlId]对应SQL语句得到的value值相等")
    public void equalToValueFromSQL(String sqlId) throws Exception {
        List<String[]> results = DBVerifyHelper.querySQL(sqlId);
        String message = String.format("sqlId [%s]返回的不是一个单一value", sqlId);
        Assert.assertTrue((1 == results.size() && 1 == results.get(0).length), message);
        Assert.assertEquals(results.get(0)[0], response);
    }

    @Verify(description = "验证contextKey对应的用户上下文+DataBinder+Config替换后是否为expected")
    public void verifyText(String contextKey,String expected)
    {
        Assert.assertEquals(ContextUtils.loadValueWithContext(contextKey),expected);
    }

    @Verify(description = "验证sqlId执行结果是否为expected")
    public void verifyDb(String sqlId,String expected) throws Exception {
        Assert.assertEquals(DBVerifyHelper.querySQL(sqlId).get(0)[0],expected);
    }
}
