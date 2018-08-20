package com.gome.test.api.parameter;

import com.gome.test.api.parameter.RandStrBinder;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RandStrBinderTest {

    @Test(dataProvider = "RandStrBinder_DataProvider")
    public void testBind(String str, Integer len) {
        String s = randStrBinder.bind(str);
        Assert.assertEquals(s.length(), len.intValue());
    }

    @DataProvider(name = "RandStrBinder_DataProvider")
    public Object[][] testBindProvider() {
        return new Object[][]{
                {"${randStr(10)}", 10},
                {"${randStr(0)}", 0},
                {"${randStr(5)}", 5}
        };
    }

    private final RandStrBinder randStrBinder = new RandStrBinder();
}
