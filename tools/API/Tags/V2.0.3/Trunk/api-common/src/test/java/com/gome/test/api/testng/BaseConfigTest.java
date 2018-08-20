package com.gome.test.api.testng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.gome.test.api.utils.ApiUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BaseConfigTest {

    @Test(dataProvider = "test_load_value_with_context")
    public void testLoadValueWithContext(Map<String, String> kvs,
                                         Map<String, String> expected) throws Exception {
        for (String key : kvs.keySet()) {
            String actualValue = ApiUtils.loadValueWithContext(kvs.get(key));
            String expectedValue = expected.get(key);
            Assert.assertEquals(actualValue, expectedValue);
        }
    }

    @DataProvider(name = "test_load_value_with_context")
    protected Object[][] testLoadValueWithContextDataProvider() throws Exception {
        Date d = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        String now = fmt.format(c.getTime());
        c.add(Calendar.DATE, 1);
        String now_1 = fmt.format(c.getTime());
        c.add(Calendar.DATE, 29);
        String now_30 = fmt.format(c.getTime());
        c.add(Calendar.DATE, -31);
        String now_m_1 = fmt.format(c.getTime());
        c.add(Calendar.DATE, -29);
        String now_m_30 = fmt.format(c.getTime());
        return new Object[][]{
                {
                        ApiUtils.toMap(
                                new String[]{"T_1", "2", "3", "4", "A", "B", "C"},
                                new String[]{"${now}", "${now+1}", "${now-1}", "${now+30}", "${now-30}", "${now}~${now+1}", "${now }"}
                        ),
                        ApiUtils.toMap(
                                new String[]{"T_1", "2", "3", "4", "A", "B", "C"},
                                new String[]{now, now_1, now_m_1, now_30, now_m_30, now + "~" + now_1, "${now }"}
                        )
                },
                {
                        ApiUtils.toMap(
                                new String[]{"T_1", "T_2", "T_1:T_2", "4", "5", "6"},
                                new String[]{"${T_1}", "T_1", "T_2", "${T_2}", "${T_1}~${T_2}", "${T_#}"}
                        ),
                        ApiUtils.toMap(
                                new String[]{"T_1", "T_2", "T_1:T_2", "4", "5", "6"},
                                new String[]{"A", "T_1", "T_2", "B" + now, "A~B" + now, "${T_#}"}
                        )
                },
                {
                        ApiUtils.toMap(
                                new String[]{"1", "2"},
                                new String[]{"${T_1}${now}", "${T_3}"}
                        ),
                        ApiUtils.toMap(
                                new String[]{"1", "2"},
                                new String[]{"A" + now, "W" + now_1 + "ha"}
                        )
                }
        };
    }

//    @BeforeMethod
//    public void setUpBeforeMethod() throws Exception {
//        BaseConfig baseConfig = new BaseConfig();
////        baseConfig.setUpBeforeSuite("HttpMessageClient");
//        baseConfig.tearDownAfterSuite();
//        BaseConfig.addToContext("T_1", "A");
//        BaseConfig.addToContext("T_2", "B${now}");
//        BaseConfig.addToContext("T_3", "W${now+1}ha");
//    }
//
//    @AfterMethod
//    public void tearDownAfterMethod() throws Exception {
//        BaseConfig baseConfig = new BaseConfig();
//        baseConfig.tearDownAfterSuite();
//    }
}
