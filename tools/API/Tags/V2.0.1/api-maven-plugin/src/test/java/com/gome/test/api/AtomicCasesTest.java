package com.gome.test.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

import com.gome.test.api.AtomicCase;
import com.gome.test.api.AtomicCases;
import com.gome.test.api.GlobalSettings;
import org.testng.annotations.*;

public class AtomicCasesTest {

    @Test
    public void testIdEq() {
        List<String> expectIdList = new ArrayList<String>();
        for (int i = 0; i < 7; ++i) {
            expectIdList.add("GetMemberIncludeCardFromType_00" + i);
        }
        for (int i = 11; i < 17; ++i) {
            expectIdList.add("GetMemberIncludeCardFromType_0" + i);
        }
        for (int i = 21; i < 27; ++i) {
            expectIdList.add("GetMemberIncludeCardFromType_0" + i);
        }
        for (int i = 16; i < 23; ++i) {
            expectIdList.add("requestCallback_14060210562" + i);
        }

        assertEquals(testCases.size(), expectIdList.size());
        for (int i = 0; i < testCases.size(); ++i) {
            String actualId = testCases.get(i).getId();
            String expectId = expectIdList.get(i);
            assertEquals(actualId, expectId);
        }
    }

    @Test
    public void testCaseNameEq() {
        String[] expectCaseNames = {"", "卡号输入空", "卡号输入null", "输入不正确的会员卡号", "输入普通会员卡号", "输入龙翠会员卡号", "输入VIP会员卡号",
                "卡号输入空", "卡号输入null", "输入不正确的会员卡号", "输入普通会员卡号", "输入龙翠会员卡号", "输入VIP会员卡号",
                "卡号输入空", "卡号输入null", "输入不正确的会员卡号", "输入普通会员卡号", "输入龙翠会员卡号", "输入VIP会员卡号",
                "期望预约时间校验1", "期望预约时间校验3", "期望预约时间校验10", "坐席组id校验9", "坐席组id校验10", "电话号码校验5", "电话号码校验6"};

        assertEquals(testCases.size(), expectCaseNames.length);
        for (int i = 0; i < testCases.size(); ++i) {
            String actualCaseName = testCases.get(i).getName();
            String expectCaseName = expectCaseNames[i];
            assertEquals(actualCaseName, expectCaseName);
        }
    }

    @Test
    public void testParamKeysEq() {
        List<String[]> expectParamKeysList = new ArrayList<String[]>();
        String[] expectParamKeys = {"CardNo"};
        for (int i = 0; i < 7; ++i) {
            expectParamKeysList.add(expectParamKeys);
        }
        expectParamKeys = new String[]{"expected", "CardNo"};
        for (int i = 0; i < 6; ++i) {
            expectParamKeysList.add(expectParamKeys);
        }
        expectParamKeys = new String[]{"CardName", "expected", "CardNo"};
        for (int i = 0; i < 6; ++i) {
            expectParamKeysList.add(expectParamKeys);
        }
        expectParamKeys = new String[]{"setUpClass", "setUpSteps", "httpUrl", "httpMethod", "headers", "urlParams",
                "entities", "verifyClass", "verifySteps", "tearDownClass", "tearDownSteps"};
        for (int i = 0; i < 7; ++i) {
            expectParamKeysList.add(expectParamKeys);
        }

        assertEquals(testCases.size(), expectParamKeysList.size());
        for (int i = 0; i < testCases.size(); ++i) {
            String[] actualParamKeys = testCases.get(i).getParamKeys();
            expectParamKeys = expectParamKeysList.get(i);
            assertEquals(actualParamKeys.length, expectParamKeys.length);
            for (int j = 0; j < expectParamKeys.length; ++j) {
                assertEquals(actualParamKeys[j], expectParamKeys[j]);
            }
        }
    }

    @Test
    public void testParamValuesEq() {
        List<Object[]> expectParamValuesList = new ArrayList<Object[]>();
        expectParamValuesList.add(new Object[]{123});
        expectParamValuesList.add(new Object[]{""});
        expectParamValuesList.add(new Object[]{"null"});
        expectParamValuesList.add(new Object[]{999999});
        expectParamValuesList.add(new Object[]{"hello, world"});
        expectParamValuesList.add(new Object[]{30});
        expectParamValuesList.add(new Object[]{1.02});
        expectParamValuesList.add(new Object[]{1, ""});
        expectParamValuesList.add(new Object[]{"中文", "null"});
        expectParamValuesList.add(new Object[]{3, 999999});
        expectParamValuesList.add(new Object[]{4, "hello, world"});
        expectParamValuesList.add(new Object[]{5, 30});
        expectParamValuesList.add(new Object[]{6, 1.02});
        expectParamValuesList.add(new Object[]{"卡名", 1, ""});
        expectParamValuesList.add(new Object[]{"卡名", "中文", "null"});
        expectParamValuesList.add(new Object[]{"卡名", 3, 999999});
        expectParamValuesList.add(new Object[]{"卡名", 4, "hello, world"});
        expectParamValuesList.add(new Object[]{"卡名", 5, 30});
        expectParamValuesList.add(new Object[]{"卡名", 6, 1.02});
        expectParamValuesList.add(new Object[]{"japi.environment.EnvManager", "[]",
                "http://mobile-api2011.elong.com/hotel/requestCallback",
                "POST", "{\"key\":\"pversion\",\"value\":\"1.2\"}",
                "\"{\\\"desiredTime\\\":\\\"15\\\",\\\"deviceId\\\":\\\"\\\",\\\"phoneNumber\\\":\\\"13067886580\\\",\\\"target\\\":\\\"1\\\",\\\"phoneNumber2\\\":\\\"13067886580\\\"}\"",
                "{\"type\":\"x-www-form-urlencoded\",\"data\":\"[]\"}",
                "verify.JsonVerify", "[]", "environment.EnvManager", "[]"});
        expectParamValuesList.add(new Object[]{"japi.environment.EnvManager", "[]",
                "http://mobile-api2011.elong.com/hotel/requestCallback",
                "POST", "{\"key\":\"pversion\",\"value\":\"1.2\"}",
                "\"{\\\"desiredTime\\\":\\\"\\\",\\\"deviceId\\\":\\\"\\\",\\\"phoneNumber\\\":\\\"13067886580\\\",\\\"target\\\":\\\"1\\\",\\\"phoneNumber2\\\":\\\"13067886580\\\"}\"",
                "{\"type\":\"x-www-form-urlencoded\",\"data\":\"[]\"}",
                "verify.JsonVerify", "[]", "environment.EnvManager", "[]"});
        expectParamValuesList.add(new Object[]{"japi.environment.EnvManager", "[]",
                "http://mobile-api2011.elong.com/hotel/requestCallback",
                "POST", "{\"key\":\"pversion\",\"value\":\"1.2\"}",
                "\"{\\\"desiredTime\\\":\\\"一小时\\\",\\\"deviceId\\\":\\\"\\\",\\\"phoneNumber\\\":\\\"13067886580\\\",\\\"target\\\":\\\"2\\\",\\\"phoneNumber2\\\":\\\"13067886580\\\"}\"",
                "{\"type\":\"x-www-form-urlencoded\",\"data\":\"[]\"}",
                "verify.JsonVerify", "[]", "environment.EnvManager", "[]"});
        expectParamValuesList.add(new Object[]{"japi.environment.EnvManager", "[]",
                "http://mobile-api2011.elong.com/hotel/requestCallback",
                "POST", "{\"key\":\"pversion\",\"value\":\"1.2\"}",
                "\"{\\\"desiredTime\\\":\\\"1\\\",\\\"deviceId\\\":\\\"\\\",\\\"phoneNumber\\\":\\\"13067886580\\\",\\\"target\\\":\\\"\\\",\\\"phoneNumber2\\\":\\\"13067886580\\\"}\"",
                "{\"type\":\"x-www-form-urlencoded\",\"data\":\"[]\"}",
                "verify.JsonVerify", "[]", "environment.EnvManager", "[]"});
        expectParamValuesList.add(new Object[]{"japi.environment.EnvManager", "[]",
                "http://mobile-api2011.elong.com/hotel/requestCallback",
                "POST", "{\"key\":\"pversion\",\"value\":\"1.2\"}",
                "\"{\\\"desiredTime\\\":\\\"1\\\",\\\"deviceId\\\":\\\"\\\",\\\"phoneNumber\\\":\\\"13067886580\\\",\\\"target\\\":\\\"@#4\\\\\",\\\"phoneNumber2\\\":\\\"13067886580\\\"}\"",
                "{\"type\":\"x-www-form-urlencoded\",\"data\":\"[]\"}",
                "verify.JsonVerify", "[]", "environment.EnvManager", "[]"});
        expectParamValuesList.add(new Object[]{"japi.environment.EnvManager", "[]",
                "http://mobile-api2011.elong.com/hotel/requestCallback",
                "POST", "{\"key\":\"pversion\",\"value\":\"1.2\"}",
                "\"{\\\"desiredTime\\\":\\\"1\\\",\\\"deviceId\\\":\\\"\\\",\\\"phoneNumber\\\":\\\"\\\",\\\"target\\\":\\\"1\\\",\\\"phoneNumber2\\\":\\\"\\\"}\"",
                "{\"type\":\"x-www-form-urlencoded\",\"data\":\"[]\"}",
                "verify.JsonVerify", "[]", "environment.EnvManager", "[]"});
        expectParamValuesList.add(new Object[]{"japi.environment.EnvManager", "[]",
                "http://mobile-api2011.elong.com/hotel/requestCallback",
                "POST", "{\"key\":\"pversion\",\"value\":\"1.2\"}",
                "\"{\\\"desiredTime\\\":\\\"1\\\",\\\"deviceId\\\":\\\"\\\",\\\"phoneNumber\\\":\\\"@#￥\\\",\\\"target\\\":\\\"1\\\",\\\"phoneNumber2\\\":\\\"@#￥\\\"}\"",
                "{\"type\":\"x-www-form-urlencoded\",\"data\":\"[]\"}",
                "verify.JsonVerify", "[]", "environment.EnvManager", "[]"});

        assertEquals(testCases.size(), expectParamValuesList.size());
        for (int i = 0; i < testCases.size(); ++i) {
            Object[] actualParamValues = testCases.get(i).getParamValues();
            Object[] expectParamValues = expectParamValuesList.get(i);
            assertEquals(actualParamValues.length, expectParamValues.length);
            for (int j = 0; j < expectParamValues.length; ++j) {
                if (expectParamValues[j] instanceof Integer) {
                    long temp = Long.parseLong(expectParamValues[j].toString());
                    expectParamValues[j] = temp;
                }
                assertEquals(actualParamValues[j], expectParamValues[j]);
            }
        }
    }

    @Test
    public void testOwnersEq() {
        List<String> expectOwners = new ArrayList<String>();
        for (int i = 1; i < 8; ++i) {
            expectOwners.add("shankuan.wu" + i);
        }
        for (int i = 1; i < 7; ++i) {
            expectOwners.add("吴奇隆");
        }
        for (int i = 1; i < 7; ++i) {
            expectOwners.add("Unknown");
        }
        for (int i = 0; i < 4; ++i) {
            expectOwners.add("shan.tan");
        }
        for (int i = 0; i < 3; ++i) {
            expectOwners.add("Unknown");
        }

        assertEquals(testCases.size(), expectOwners.size());
        for (int i = 0; i < testCases.size(); ++i) {
            String actualOwner = testCases.get(i).getOwner();
            String expectOwner = expectOwners.get(i);
            assertEquals(actualOwner, expectOwner);
        }
    }

    @Test
    public void testPriorityEq() {
        String[] expectPriorities = {"_None", "1", "2", "_None", "3", "4", "5",
                "1", "2", "_None", "3", "4", "5",
                "1", "2", "_None", "3", "4", "5",
                "_None", "_None", "_None",
                "_None", "_None", "_None", "_None"};

        assertEquals(testCases.size(), expectPriorities.length);
        for (int i = 0; i < testCases.size(); ++i) {
            String actualPriority = testCases.get(i).getPriority();
            String expectPriority = expectPriorities[i];
            assertEquals(actualPriority, expectPriority);
        }
    }

    @BeforeClass
    public void setUpBeforeClass() throws IOException {
        GlobalSettings settings = new GlobalSettings();
        settings.loadFrom(getClass().getResourceAsStream("/global.xlsx"));
        testCases = new AtomicCases(
                getClass().getResourceAsStream("/testcases.xlsx"),
                settings).getTestCases();
    }

    private List<AtomicCase> testCases;
}
