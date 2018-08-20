package com.ofo.test.api.verify;

import com.ofo.test.api.annotation.Verify;
import com.ofo.test.context.ContextUtils;
import com.ofo.test.utils.AESUtils;
import com.ofo.test.utils.Base64Utils;
import com.ofo.test.utils.Logger;
import com.jayway.jsonassert.JsonAssert;
import com.jayway.jsonpath.JsonPath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.testng.Assert;

public class JsonVerify extends HttpResponseVerify {

    @Verify(description = "验证[jsonPath]对应的字段是boolean值[expected]")
    public void equalToBoolean(String jsonPath,
                               String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Logger.info("得到Value："+value);
        Boolean expectBool = Boolean.parseBoolean(expected.trim());
        Boolean valueBool = Boolean.parseBoolean(value.trim());
        Assert.assertEquals(expectBool, valueBool);
    }


    @Verify(description = "对于[jsonPath]对应的字段进行BASE转码后进行AES解密赋值给response，key为[key],在上下文中使用的名称是[name]")
    public void collectionAESAndBASE64Decrypt(String jsonPath,String key,String name) throws Exception
    {
        String value=readValueFrom(jsonPath);
        value = AESUtils.decryptMessageForBASE64(Base64Utils.decodeMessage(value),key);
        Logger.info("解析后的内容为："+ value);
        setResponse(value.trim());
        ContextUtils.addToContext(name, value);
    }


    @Verify(description = "对于[jsonPath]对应的字段进行BASE转码后赋值给response，在上下文中使用的名称是[name]")
    public void collectionBASE64Decrypt(String jsonPath,String name) throws Exception
    {
        String value=readValueFrom(jsonPath);
        value =  new String(Base64Utils.decodeMessage(value),"UTF-8");
        Logger.info("BASE64解密后的内容为："+value);
        setResponse(value.trim());//替换response
        ContextUtils.addToContext(name, value);//加入到上下文中
    }

    @Verify(description = "对于[jsonPath]对应的字段进行AES解码后后赋值给response，key为[key],在上下文中使用的名称是[name]")
    public void collectionAESDecrypt(String jsonPath,String key,String name) throws Exception
    {
        String value=readValueFrom(jsonPath);
        value =  AESUtils.decryptAESOlny(value,key);
        Logger.info("解析后的内容为："+value);
        setResponse(value.trim());//替换response
        ContextUtils.addToContext(name, value);//加入到上下文中

    }



    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值与boolean值[expected]相等")
    public void collectionValueEqualToBoolean(String jsonPath,
                                              String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        for (int i = 0; i < l.size(); ++i) {
            Boolean expectBool = Boolean.parseBoolean(expected.trim());
            Boolean valueBool = Boolean.parseBoolean(l.get(i).toString().trim());
            Assert.assertEquals(expectBool, valueBool);
        }
    }

    @Verify(description = "验证[jsonPath]对应的字段是short值[expected]")
    public void equalToShort(String jsonPath,
                             String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Short expectShort = Short.parseShort(expected.trim());
        Short valueShort = Short.parseShort(value.trim());
        Assert.assertEquals(expectShort, valueShort);
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值与short值[expected]相等")
    public void collectionValueEqualToShort(String jsonPath,
                                            String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Short expectShort = Short.parseShort(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Short valueShort = Short.parseShort(l.get(i).toString().trim());
            if (!expectShort.equals(valueShort)) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueShort));
                flag = false;
            }
        }
        sb.append(String.format("与期望值[%d]不符", expectShort));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比short值[expected]大")
    public void greaterThanShort(String jsonPath,
                                 String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Short expectShort = Short.parseShort(expected.trim());
        Short valueShort = Short.parseShort(value.trim());
        assertThat(valueShort, Matchers.greaterThan(expectShort));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比short值[expected]大")
    public void collectionValueGreaterThanShort(String jsonPath,
                                                String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Short expectShort = Short.parseShort(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Short valueShort = Short.parseShort(l.get(i).toString().trim());
            if (expectShort >= valueShort) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueShort));
                flag = false;
            }
        }
        sb.append(String.format("不比[%d]大，与期望不符", expectShort));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比short值[expected]小")
    public void lessThanShort(String jsonPath,
                              String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Short expectShort = Short.parseShort(expected.trim());
        Short valueShort = Short.parseShort(value.trim());
        assertThat(valueShort, Matchers.lessThan(expectShort));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比short值[expected]小")
    public void collectionValueLessThanShort(String jsonPath,
                                             String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Short expectShort = Short.parseShort(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Short valueShort = Short.parseShort(l.get(i).toString().trim());
            if (expectShort <= valueShort) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueShort));
                flag = false;
            }
        }
        sb.append(String.format("不比[%d]小，与期望不符", expectShort));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比short值[expected]大或是相等")
    public void greaterThanOrEqualToShort(String jsonPath,
                                          String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Short expectShort = Short.parseShort(expected.trim());
        Short valueShort = Short.parseShort(value.trim());
        assertThat(valueShort, Matchers.greaterThanOrEqualTo(expectShort));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比short值[expected]大或是相等")
    public void collectionValueGreaterThanOrEqualToShort(String jsonPath,
                                                         String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Short expectShort = Short.parseShort(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Short valueShort = Short.parseShort(l.get(i).toString().trim());
            if (expectShort > valueShort) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueShort));
                flag = false;
            }
        }
        sb.append(String.format("比[%d]小，与期望不符", expectShort));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比short值[expected]小或是相等")
    public void lessThanOrEqualToShort(String jsonPath,
                                       String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Short expectShort = Short.parseShort(expected.trim());
        Short valueShort = Short.parseShort(value.trim());
        assertThat(valueShort, Matchers.lessThanOrEqualTo(expectShort));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比short值[expected]小或是相等")
    public void collectionValueLessThanOrEqualToShort(String jsonPath,
                                                      String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Short expectShort = Short.parseShort(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Short valueShort = Short.parseShort(l.get(i).toString().trim());
            if (expectShort < valueShort) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueShort));
                flag = false;
            }
        }
        sb.append(String.format("比[%d]大，与期望不符", expectShort));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段是int值[expected]")
    public void equalToInt(String jsonPath,
                           String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Integer expectInt = Integer.parseInt(expected.trim());
        Integer valueInt = Integer.parseInt(value.trim());
        Assert.assertEquals(expectInt, valueInt);
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值与int值[expected]相等")
    public void collectionValueEqualToInt(String jsonPath,
                                          String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Integer expectInt = Integer.parseInt(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Integer valueInt = Integer.parseInt(l.get(i).toString().trim());
            if (!expectInt.equals(valueInt)) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueInt));
                flag = false;
            }
        }
        sb.append(String.format("与预期值[%d]不符", expectInt));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比int值[expected]大")
    public void greaterThanInt(String jsonPath,
                               String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Integer expectInt = Integer.parseInt(expected.trim());
        Integer valueInt = Integer.parseInt(value.trim());
        assertThat(valueInt, Matchers.greaterThan(expectInt));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比int值[expected]大")
    public void collectionValueGreaterThanInt(String jsonPath,
                                              String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Integer expectInt = Integer.parseInt(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Integer valueInt = Integer.parseInt(l.get(i).toString().trim());
            if (expectInt >= valueInt) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueInt));
                flag = false;
            }
        }
        sb.append(String.format("不比[%d]大，与预期不符", expectInt));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比int值[expected]小")
    public void lessThanInt(String jsonPath,
                            String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Integer expectInt = Integer.parseInt(expected.trim());
        Integer valueInt = Integer.parseInt(value.trim());
        assertThat(valueInt, Matchers.lessThan(expectInt));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比int值[expected]小")
    public void collectionValueLessThanInt(String jsonPath,
                                           String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Integer expectInt = Integer.parseInt(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Integer valueInt = Integer.parseInt(l.get(i).toString().trim());
            if (expectInt <= valueInt) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueInt));
                flag = false;
            }
        }
        sb.append(String.format("不比[%d]小，与预期不符", expectInt));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比int值[expected]大或是相等")
    public void greaterThanOrEqualToInt(String jsonPath,
                                        String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Integer expectInt = Integer.parseInt(expected.trim());
        Integer valueInt = Integer.parseInt(value.trim());
        assertThat(valueInt, Matchers.greaterThanOrEqualTo(expectInt));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比int值[expected]大或是相等")
    public void collectionValueGreaterThanOrEqualToInt(String jsonPath,
                                                       String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Integer expectInt = Integer.parseInt(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Integer valueInt = Integer.parseInt(l.get(i).toString().trim());
            if (expectInt > valueInt) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueInt));
                flag = false;
            }
        }
        sb.append(String.format("比[%d]小，与预期不符", expectInt));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比int值[expected]小或是相等")
    public void lessThanOrEqualToInt(String jsonPath,
                                     String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Integer expectInt = Integer.parseInt(expected.trim());
        Integer valueInt = Integer.parseInt(value.trim());
        assertThat(valueInt, Matchers.lessThanOrEqualTo(expectInt));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比int值[expected]小或是相等")
    public void collectionValueLessThanOrEqualToInt(String jsonPath,
                                                    String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Integer expectInt = Integer.parseInt(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Integer valueInt = Integer.parseInt(l.get(i).toString().trim());
            if (expectInt < valueInt) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueInt));
                flag = false;
            }
        }
        sb.append(String.format("比[%d]大，与预期不符", expectInt));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段是long值[expected]")
    public void equalToLong(String jsonPath,
                            String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Long expectLong = Long.parseLong(expected.trim());
        Long valueLong = Long.parseLong(value.trim());
        Assert.assertEquals(expectLong, valueLong);
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值与long值[expected]相等")
    public void collectionValueEqualToLong(String jsonPath,
                                           String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Long expectLong = Long.parseLong(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Long valueLong = Long.parseLong(l.get(i).toString().trim());
            if (!expectLong.equals(valueLong)) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueLong));
                flag = false;
            }
        }
        sb.append(String.format("与预期值[%d]不符", expectLong));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比long值[expected]大")
    public void greaterThanLong(String jsonPath,
                                String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Long expectLong = Long.parseLong(expected.trim());
        Long valueLong = Long.parseLong(value.trim());
        assertThat(valueLong, Matchers.greaterThan(expectLong));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比long值[expected]大")
    public void collectionValueGreaterThanLong(String jsonPath,
                                               String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Long expectLong = Long.parseLong(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Long valueLong = Long.parseLong(l.get(i).toString().trim());
            if (expectLong >= valueLong) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueLong));
                flag = false;
            }
        }
        sb.append(String.format("不比[%d]大，与预期不符", expectLong));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比long值[expected]小")
    public void lessThanLong(String jsonPath,
                             String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Long expectLong = Long.parseLong(expected.trim());
        Long valueLong = Long.parseLong(value.trim());
        assertThat(valueLong, Matchers.lessThan(expectLong));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比long值[expected]小")
    public void collectionValueLessThanLong(String jsonPath,
                                            String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Long expectLong = Long.parseLong(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Long valueLong = Long.parseLong(l.get(i).toString().trim());
            if (expectLong <= valueLong) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueLong));
                flag = false;
            }
        }
        sb.append(String.format("不比[%d]小，与预期不符", expectLong));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比long值[expected]大或相等")
    public void greaterThanOrEqualToLong(String jsonPath,
                                         String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Long expectLong = Long.parseLong(expected.trim());
        Long valueLong = Long.parseLong(value.trim());
        assertThat(valueLong, Matchers.greaterThanOrEqualTo(expectLong));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比long值[expected]大或相等")
    public void collectionValueGreaterThanOrEqualToLong(String jsonPath,
                                                        String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Long expectLong = Long.parseLong(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Long valueLong = Long.parseLong(l.get(i).toString().trim());
            if (expectLong > valueLong) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueLong));
                flag = false;
            }
        }
        sb.append(String.format("比[%d]小，与预期不符", expectLong));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比long值[expected]小或是相等")
    public void lessThanOrEqualToLong(String jsonPath,
                                      String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Long expectLong = Long.parseLong(expected.trim());
        Long valueLong = Long.parseLong(value.trim());
        assertThat(valueLong, Matchers.lessThanOrEqualTo(expectLong));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比long值[expected]小或相等")
    public void collectionValueLessThanOrEqualToLong(String jsonPath,
                                                     String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Long expectLong = Long.parseLong(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Long valueLong = Long.parseLong(l.get(i).toString().trim());
            if (expectLong < valueLong) {
                sb.append(String.format("第[%d]号值为[%d]，", i, valueLong));
                flag = false;
            }
        }
        sb.append(String.format("比[%d]大，与预期不符", expectLong));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段是float值[expected]")
    public void equalToFloat(String jsonPath,
                             String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Float expectFloat = Float.parseFloat(expected.trim());
        Float valueFloat = Float.parseFloat(value.trim());
        Assert.assertEquals(expectFloat, valueFloat);
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值与float值[expected]相等")
    public void collectionValueEqualToFloat(String jsonPath,
                                            String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Float expectFloat = Float.parseFloat(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Float valueFloat = Float.parseFloat(l.get(i).toString().trim());
            if (!expectFloat.equals(valueFloat)) {
                sb.append(String.format("第[%d]号值为[%f]，", i, valueFloat));
                flag = false;
            }
        }
        sb.append(String.format("与预期值[%f]不符", expectFloat));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比float值[expected]大")
    public void greaterThanFloat(String jsonPath,
                                 String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Float expectFloat = Float.parseFloat(expected.trim());
        Float valueFloat = Float.parseFloat(value.trim());
        assertThat(valueFloat, Matchers.greaterThan(expectFloat));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比float值[expected]大")
    public void collectionValueGreaterThanFloat(String jsonPath,
                                                String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Float expectFloat = Float.parseFloat(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Float valueFloat = Float.parseFloat(l.get(i).toString().trim());
            if (expectFloat >= valueFloat) {
                sb.append(String.format("第[%d]号值为[%f]，", i, valueFloat));
                flag = false;
            }
        }
        sb.append(String.format("不比[%f]大，与预期不符", expectFloat));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比float值[expected]小")
    public void lessThanFloat(String jsonPath,
                              String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Float expectFloat = Float.parseFloat(expected.trim());
        Float valueFloat = Float.parseFloat(value.trim());
        assertThat(valueFloat, Matchers.lessThan(expectFloat));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比float值[expected]小")
    public void collectionValueLessThanFloat(String jsonPath,
                                             String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Float expectFloat = Float.parseFloat(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Float valueFloat = Float.parseFloat(l.get(i).toString().trim());
            if (expectFloat <= valueFloat) {
                sb.append(String.format("第[%d]号值为[%f]，", i, valueFloat));
                flag = false;
            }
        }
        sb.append(String.format("不比[%f]小，与预期不符", expectFloat));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比float值[expected]大或相等")
    public void greaterThanOrEqualToFloat(String jsonPath,
                                          String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Float expectFloat = Float.parseFloat(expected.trim());
        Float valueFloat = Float.parseFloat(value.trim());
        assertThat(valueFloat, Matchers.greaterThanOrEqualTo(expectFloat));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比float值[expected]大或相等")
    public void collectionValueGreaterThanOrEqualToFloat(String jsonPath,
                                                         String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Float expectFloat = Float.parseFloat(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Float valueFloat = Float.parseFloat(l.get(i).toString().trim());
            if (expectFloat > valueFloat) {
                sb.append(String.format("第[%d]号值为[%f]，", i, valueFloat));
                flag = false;
            }
        }
        sb.append(String.format("比[%f]小，与预期不符", expectFloat));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比float值[expected]小或相等")
    public void lessThanOrEqualToFloat(String jsonPath,
                                       String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Float expectFloat = Float.parseFloat(expected.trim());
        Float valueFloat = Float.parseFloat(value.trim());
        assertThat(valueFloat, Matchers.lessThanOrEqualTo(expectFloat));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比float值[expected]小或相等")
    public void collectionValueLessThanOrEqualToFloat(String jsonPath,
                                                      String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Float expectFloat = Float.parseFloat(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Float valueFloat = Float.parseFloat(l.get(i).toString().trim());
            if (expectFloat < valueFloat) {
                sb.append(String.format("第[%d]号值为[%f]，", i, valueFloat));
                flag = false;
            }
        }
        sb.append(String.format("比[%f]大，与预期不符", expectFloat));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段是double值[expected]")
    public void equalToDouble(String jsonPath,
                              String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Double expectDouble = Double.parseDouble(expected.trim());
        Double valueDouble = Double.parseDouble(value.trim());
        Assert.assertEquals(expectDouble, valueDouble);
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值与double值[expected]相等")
    public void collectionValueEqualToDouble(String jsonPath,
                                             String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Double expectDouble = Double.parseDouble(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Double valueDouble = Double.parseDouble(l.get(i).toString().trim());
            if (!expectDouble.equals(valueDouble)) {
                sb.append(String.format("第[%d]号值为[%f]，", i, valueDouble));
                flag = false;
            }
        }
        sb.append(String.format("与预期值[%f]不符", expectDouble));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段与double值[expected]在[error]范围内相等")
    public void closeTo(String jsonPath, String expected,
                        String error) throws Exception {
        String value = readValueFrom(jsonPath);
        Double expectDouble = Double.parseDouble(expected.trim());
        Double valueDouble = Double.parseDouble(value.trim());
        Double errorDouble = Double.parseDouble(error.trim());
        assertThat(valueDouble, Matchers.closeTo(expectDouble, errorDouble));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值与double值[expected]在[error]范围内相等")
    public void collectionValueCloseTo(String jsonPath,
                                       String expected, String error) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Double expectDouble = Double.parseDouble(expected.trim());
        Double errorDouble = Double.parseDouble(error.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Double valueDouble = Double.parseDouble(l.get(i).toString().trim());
            if (Math.abs(valueDouble - expectDouble) > errorDouble) {
                sb.append(String.format("第[%d]号值为[%f]，", i, valueDouble));
                flag = false;
            }
        }
        sb.append(String.format("与预期值[%f]在error范围[%f]不等", expectDouble, errorDouble));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比double值[expected]大")
    public void greaterThanDouble(String jsonPath,
                                  String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Double expectDouble = Double.parseDouble(expected.trim());
        Double valueDouble = Double.parseDouble(value.trim());
        assertThat(valueDouble, Matchers.greaterThan(expectDouble));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比double值[expected]大")
    public void collectionValueGreaterThanDouble(String jsonPath,
                                                 String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Double expectDouble = Double.parseDouble(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Double valueDouble = Double.parseDouble(l.get(i).toString().trim());
            if (expectDouble >= valueDouble) {
                sb.append(String.format("第[%d]号值为[%f]，", i, valueDouble));
                flag = false;
            }
        }
        sb.append(String.format("不比[%f]大，与预期不符", expectDouble));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比double值[expected]小")
    public void lessThanDouble(String jsonPath,
                               String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Double expectDouble = Double.parseDouble(expected.trim());
        Double valueDouble = Double.parseDouble(value.trim());
        assertThat(valueDouble, Matchers.lessThan(expectDouble));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比double值[expected]小")
    public void collectionValueLessThanDouble(String jsonPath,
                                              String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Double expectDouble = Double.parseDouble(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Double valueDouble = Double.parseDouble(l.get(i).toString().trim());
            if (expectDouble <= valueDouble) {
                sb.append(String.format("第[%d]号值为[%f]，", i, valueDouble));
                flag = false;
            }
        }
        sb.append(String.format("不比[%f]小，与预期不符", expectDouble));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比double值[expected]大或相等")
    public void greaterThanOrEqualToDouble(String jsonPath,
                                           String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Double expectDouble = Double.parseDouble(expected.trim());
        Double valueDouble = Double.parseDouble(value.trim());
        assertThat(valueDouble, Matchers.greaterThanOrEqualTo(expectDouble));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比double值[expected]大或相等")
    public void collectionValueGreaterThanOrEqualToDouble(String jsonPath,
                                                          String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Double expectDouble = Double.parseDouble(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Double valueDouble = Double.parseDouble(l.get(i).toString().trim());
            if (expectDouble > valueDouble) {
                sb.append(String.format("第[%d]号值为[%f]，", i, valueDouble));
                flag = false;
            }
        }
        sb.append(String.format("比[%f]小，与预期不符", expectDouble));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段比double值[expected]小或相等")
    public void lessThanOrEqualToDouble(String jsonPath,
                                        String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Double expectDouble = Double.parseDouble(expected.trim());
        Double valueDouble = Double.parseDouble(value.trim());
        assertThat(valueDouble, Matchers.lessThanOrEqualTo(expectDouble));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值比double值[expected]小或相等")
    public void collectionValueLessThanOrEqualToDouble(String jsonPath,
                                                       String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        Double expectDouble = Double.parseDouble(expected.trim());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            Double valueDouble = Double.parseDouble(l.get(i).toString().trim());
            if (expectDouble < valueDouble) {
                sb.append(String.format("第[%d]号值为[%f]，", i, valueDouble));
                flag = false;
            }
        }
        sb.append(String.format("比[%f]大，与预期不符", expectDouble));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段与string值[expected]一样")
    public void equalToString(String jsonPath,
                              String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        assertThat(value, Matchers.equalTo(expected));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值与string值[expected]一样")
    public void collectionValueEqualToString(String jsonPath,
                                             String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            String value = l.get(i).toString();
            if (!expected.equals(value)) {
                sb.append(String.format("第[%d]号值为[%s]，", i, value));
                flag = false;
            }
        }
        sb.append(String.format("与预期值[%s]不符", expected));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段与string值[expected]一样，大小写无关")
    public void equalToStringIgnoreCase(String jsonPath,
                                        String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        assertThat(value, Matchers.equalToIgnoringCase(expected));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值与string值[expected]一样，大小写无关")
    public void collectionValueEqualToStringIgnoreCase(String jsonPath,
                                                       String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            String value = l.get(i).toString();
            if (!expected.equalsIgnoreCase(value)) {
                sb.append(String.format("第[%d]号值为[%s]，", i, value));
                flag = false;
            }
        }
        sb.append(String.format("与预期值[%s]不符", expected));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段与string值[expected]一样，忽略空白字符")
    public void equalToStringIgnoreWhiteSpace(String jsonPath,
                                              String expected) throws Exception {
        JsonAssert.with(response).assertThat(jsonPath, Matchers.equalToIgnoringWhiteSpace(expected));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值与string值[expected]一样，忽略前后空白字符")
    public void collectionValueEqualToStringIgnoreWhiteSpace(String jsonPath,
                                                             String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            String value = l.get(i).toString();
            if (!expected.trim().equals(value.trim())) {
                sb.append(String.format("第[%d]号值为[%s]，", i, value));
                flag = false;
            }
        }
        sb.append(String.format("与预期值[%s]在忽略前后空白字符的情形下不符", expected));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段以前缀值[prefix]开头")
    public void startsWith(String jsonPath, String prefix) throws Exception {
        JsonAssert.with(response).assertThat(jsonPath, Matchers.startsWith(prefix));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值以前缀值[prefix]开头")
    public void collectionValueStartsWith(String jsonPath,
                                          String prefix) throws Exception {
        List l = readCollectionFrom(jsonPath);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            String value = l.get(i).toString();
            if (!value.startsWith(prefix)) {
                sb.append(String.format("第[%d]号值为[%s]，", i, value));
                flag = false;
            }
        }
        sb.append(String.format("不以[%s]开头", prefix));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段以后缀值[suffix]结尾")
    public void endsWith(String jsonPath, String suffix) throws Exception {
        JsonAssert.with(response).assertThat(jsonPath, Matchers.endsWith(suffix));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值以后缀值[suffix]结尾")
    public void collectionValueEndsWith(String jsonPath,
                                        String suffix) throws Exception {
        List l = readCollectionFrom(jsonPath);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            String value = l.get(i).toString();
            if (!value.endsWith(suffix)) {
                sb.append(String.format("第[%d]号值为[%s]，", i, value));
                flag = false;
            }
        }
        sb.append(String.format("不以[%s]结尾", suffix));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段包含[expected]值")
    public void contains(String jsonPath, String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Assert.assertTrue(value.contains(expected));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值包含[expected]值")
    public void collectionValueContains(String jsonPath,
                                        String expected) throws Exception {
        List l = readCollectionFrom(jsonPath);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < l.size(); ++i) {
            String value = l.get(i).toString();
            if (!value.contains(expected)) {
                sb.append(String.format("第[%d]号值为[%s]，", i, value));
                flag = false;
            }
        }
        sb.append(String.format("不包含[%s]", expected));
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的collection字段的size是[expected]")
    @SuppressWarnings("unchecked")
    public void collectionSizeEqualTo(String jsonPath,
                                      String expected) throws Exception {
        Integer expectInt = Integer.valueOf(expected.trim());
        JsonAssert.with(response).assertThat(jsonPath,
                Matchers.is(JsonAssert.collectionWithSize(Matchers.equalTo(expectInt))));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段的size比[expected]大")
    @SuppressWarnings("unchecked")
    public void collectionSizeGreaterThan(String jsonPath,
                                          String expected) throws Exception {
        Integer expectInt = Integer.valueOf(expected.trim());
        JsonAssert.with(response).assertThat(jsonPath,
                Matchers.is(JsonAssert.collectionWithSize(Matchers.greaterThan(expectInt))));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段的size比[expected]小")
    @SuppressWarnings("unchecked")
    public void collectionSizeLessThan(String jsonPath,
                                       String expected) throws Exception {
        Integer expectInt = Integer.valueOf(expected.trim());
        JsonAssert.with(response).assertThat(jsonPath,
                Matchers.is(JsonAssert.collectionWithSize(Matchers.lessThan(expectInt))));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段的size比[expected]大或相等")
    @SuppressWarnings("unchecked")
    public void collectionSizeGreaterThanOrEqualTo(String jsonPath,
                                                   String expected) throws Exception {
        Integer expectInt = Integer.valueOf(expected.trim());
        JsonAssert.with(response).assertThat(jsonPath,
                Matchers.is(JsonAssert.collectionWithSize(Matchers.greaterThanOrEqualTo(expectInt))));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段的size比[expected]小或相等")
    @SuppressWarnings("unchecked")
    public void collectionSizeLessThanOrEqualTo(String jsonPath,
                                                String expected) throws Exception {
        Integer expectInt = Integer.valueOf(expected.trim());
        JsonAssert.with(response).assertThat(jsonPath,
                Matchers.is(JsonAssert.collectionWithSize(Matchers.greaterThanOrEqualTo(expectInt))));
    }

    @Verify(description = "验证[jsonPath]对应的collection字段包含[expected]")
    public void collectionHasItem(String jsonPath, String expected)
            throws Exception {
        JsonAssert.with(response).assertThat(jsonPath, Matchers.hasItems(expected));
    }

    @Verify(description = "验证[jsonPath]对应的字段与[expected]相等")
    public void equalTo(String jsonPath, String expected) throws Exception {
        String value = readValueFrom(jsonPath);
        Assert.assertEquals(expected, value);
    }

    @Verify(description = "验证[jsonPath]对应的value字段与执行[sqlId]对应SQL语句得到的value值相等")
    public void equalToValueFromSQL(String jsonPath, String sqlId)
            throws Exception {
        String value = readValueFrom(jsonPath, false);
        List<String[]> results = DBVerifyHelper.querySQL(sqlId);
        String message = String.format("sqlId [%s]返回的不是一个单一value", sqlId);
        Assert.assertTrue((1 == results.size() && 1 == results.get(0).length), message);
        Assert.assertEquals(results.get(0)[0], value);
    }

    @Verify(description = "验证[jsonPath]对应的object字段与执行[sqlId]对应SQL语句得到的object相等")
    public void equalToObjectFromSQL(String jsonPath, String sqlId)
            throws Exception {
        Map<Integer, String> meta = new HashMap<Integer, String>();
        List<String[]> results = DBVerifyHelper.querySQL(sqlId, meta);
        String message = String.format("sqlId [%s]返回的不是一个单一object", sqlId);
        Assert.assertTrue(1 == results.size(), message);
        String[] result = results.get(0);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的object字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < result.length; ++i) {
            String key = meta.get(i);
            String newJsonPath = String.format("%s.%s", jsonPath, key);
            String value = readValueFrom(newJsonPath, false);
            if (null == result[i]) {
                if (null != value) {
                    sb.append(String.format("field [%s] 期望值是[%s]，实际值是[%s]；",
                            key, result[i], value));
                    flag = false;
                }
            } else if (!result[i].equals(value)) {
                sb.append(String.format("field [%s] 期望值是[%s]，实际值是[%s]；",
                        key, result[i], value));
                flag = false;
            }
        }
        sb.append("与预期结果不符");
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中每个value值与执行[sqlId]对应SQL语句得到的每个value值相等")
    public void collectionEqualToValueFromSQL(String jsonPath, String sqlId)
            throws Exception {
        List values = readCollectionFrom(jsonPath, false);
        List<String[]> results = DBVerifyHelper.querySQL(sqlId);
        String message = String.format("json path对应collection字段的size是%d，"
                        + "执行sqlId对应SQL语句得到返回值的size是%d，两者不符",
                results.size(), values.size());
        Assert.assertTrue(results.size() == values.size(), message);
        if (results.size() > 0) {
            message = String.format("sqlId [%s]返回的结果不是一列数据", sqlId);
            Assert.assertTrue(1 == results.get(0).length, message);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < values.size(); ++i) {
            if (null == results.get(i)[0]) {
                if (null != values.get(i)) {
                    sb.append(String.format("第[%d]号预期值为%s，实际值为%s；",
                            i, results.get(i)[0], values.get(i)));
                    flag = false;
                }
            } else if (null == values.get(i)) {
                sb.append(String.format("第[%d]号预期值为%s，实际值为%s；",
                        i, results.get(i)[0], values.get(i)));
                flag = false;
            } else if (!results.get(i)[0].equals(values.get(i).toString())) {
                sb.append(String.format("第[%d]号预期值为%s，实际值为%s；",
                        i, results.get(i)[0], values.get(i).toString()));
                flag = false;
            }
        }
        sb.append("与预期结果不符");
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的collection字段中的每个object与执行[sqlId]对应SQL语句得到的每个object值相等")
    public void collectionEqualToObjectFromSQL(String jsonPath, String sqlId)
            throws Exception {
        Map<Integer, String> meta = new HashMap<Integer, String>();
        List<String[]> results = DBVerifyHelper.querySQL(sqlId, meta);
        List l = readCollectionFrom(jsonPath);
        String message = String.format("json path对应collection字段的size是%d，"
                        + "执行sqlId对应SQL语句得到返回值的size是%d，两者不符",
                l.size(), results.size());
        Assert.assertEquals(results.size(), l.size(), message);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("在json path [%s]对应的collection字段中，", jsonPath));
        boolean flag = true;
        for (int i = 0; i < results.size(); ++i) {
            String[] result = results.get(i);
            for (int j = 0; j < result.length; ++j) {
                String key = meta.get(j);
                String newJsonPath = String.format("%s[%d].%s", jsonPath, i, key);
                String value = readValueFrom(newJsonPath, false);
                if (null == result[j]) {
                    if (null != value) {
                        sb.append(String.format("第[%d]号元素的field [%s]，期望值是[%s]，实际值是[%s]；",
                                i, key, result[j], value));
                        flag = false;
                    }
                } else if (!result[j].equals(value)) {
                    sb.append(String.format("第[%d]号元素的field [%s]，期望值是[%s]，实际值是[%s]；",
                            i, key, result[j], value));
                    flag = false;
                }
            }
        }
        sb.append("与预期结果不符");
        Assert.assertTrue(flag, sb.toString());
    }

    @Verify(description = "验证[jsonPath]对应的字段包含键值[expectKey]")
    public void hasKey(String jsonPath, String expectKey) throws Exception {
        JsonAssert.with(response).assertThat(jsonPath, Matchers.hasKey(expectKey));
    }

    @Verify(description = "验证[jsonPath]对应的字段不为null")
    public void notNull(String jsonPath) throws Exception {
        JsonAssert.with(response).assertNotNull(jsonPath);
    }

    @Verify(description = "验证[jsonPath]对应的字段为null")
    public void isNull(String jsonPath)
            throws Exception {
        JsonAssert.with(response).assertNull(jsonPath);
    }

    @Verify(description = "验证[jsonPath]对应的字段未定义")
    public void notDefined(String jsonPath)
            throws Exception {
        JsonAssert.with(response).assertNotDefined(jsonPath);
    }

    @Verify(description = "验证[jsonPath]对应的字段为空collection")
    public void isEmptyCollection(String jsonPath)
            throws Exception {
        JsonAssert.with(response).assertThat(jsonPath, JsonAssert.emptyCollection());
    }

    private List readCollectionFrom(String jsonPath) throws Exception {
        return readCollectionFrom(jsonPath, true);
    }

    private String readValueFrom(String jsonPath) throws Exception {
        return readValueFrom(jsonPath, true);
    }

    private List readCollectionFrom(String jsonPath, boolean bErrorIfNotExists) throws Exception {
        List l = null;
        try {
            l = JsonPath.read(response, jsonPath);
        } catch (Exception ex) {
            if (bErrorIfNotExists) {
                throw new Exception(String.format("json path [%s]不存在", jsonPath));
            }
        }
        return l;
    }

    private String readValueFrom(String jsonPath, boolean bErrorIfNotExists) throws Exception {
        String value = null;
        try {
            value = JsonPath.read(response, jsonPath).toString();
        } catch (Exception ex) {
            if (bErrorIfNotExists) {
                throw new Exception(String.format("json path [%s]不存在", jsonPath));
            }
        }
        return value;
    }
}
