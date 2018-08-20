package com.gome.test.api.parameter;

import com.gome.test.api.parameter.RandCnBinder;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.Assert;

public class RandCnBinderTest {

    @Test(dataProvider = "RandCnBinder_DataProvider")
    public void testBind(String str, Integer len) {
        String s = randCnBinder.bind(str);
        Assert.assertEquals(s.length(), len.intValue());
    }

    @DataProvider(name = "RandCnBinder_DataProvider")
    public Object[][] testBindProvider() {
        return new Object[][]{
                {"${randCn(10)}", 10},
                {"${randCn(0)}", 0},
                {"${randCn(5)}", 5}
        };
    }

    private final RandCnBinder randCnBinder = new RandCnBinder();
}
