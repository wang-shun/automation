package com.gome.test.api.parameter;

import com.gome.test.api.parameter.RandNumBinder;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RandNumBinderTest {

    @Test(dataProvider = "RandNumBinder_DataProvider")
    public void testBind(String str, Integer len) {
        String s = randNumBinder.bind(str);
        Assert.assertEquals(s.length(), len.intValue());
    }

    @DataProvider(name = "RandNumBinder_DataProvider")
    public Object[][] testBindProvider() {
        return new Object[][]{
                {"${randNum(10)}", 10},
                {"${randNum(0)}", 0},
                {"${randNum(5)}", 5}
        };
    }

    private final RandNumBinder randNumBinder = new RandNumBinder();
}
