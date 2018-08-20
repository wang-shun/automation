package com.gome.test.api.utils;

import java.util.HashMap;
import java.util.Map;

import com.gome.test.api.utils.DBUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DBUtilsTest {

    @Test
    public void testQuerySQL() throws Exception {
        Map<Integer, String> meta = new HashMap<Integer, String>();
        DBUtils.querySQL("com.mysql.jdbc.Driver",
                "jdbc:mysql://192.168.14.187:3306/ETP",
                "root", "root", "SELECT 1 AS A, 2 AS B", meta);
        Assert.assertEquals(meta.size(), 2);
        Assert.assertEquals(meta.get(0), "A");
        Assert.assertEquals(meta.get(1), "B");
    }
}
