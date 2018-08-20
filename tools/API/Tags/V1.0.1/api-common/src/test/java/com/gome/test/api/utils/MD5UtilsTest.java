package com.gome.test.api.utils;

import com.gome.test.api.utils.MD5Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MD5UtilsTest {

    @Test(dataProvider = "testEncryptDataProvider")
    public void testEncrypt(String text) throws Exception {
        Assert.assertEquals(MD5Utils.encrypt(text), DigestUtils.md5Hex(text));
    }

    @DataProvider(name = "testEncryptDataProvider")
    public Object[][] testEncyrptDataProvider() {
        return new Object[][]{
                {"hello=54&world=38"},
                {"\"中文"}
        };
    }
}
