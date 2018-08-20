package com.gome.test.api.verify;

import com.gome.test.api.verify.JsonVerify;
import com.jayway.jsonpath.JsonPath;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class JsonVerifyTest {

    @Test
    public void testEqualToString() throws Exception {
        verify.equalToString("$.store.bicycle.color", "red");
        verify.equalToString("store.bicycle.color", "red");
        verify.equalToString("store.book[0].category", "reference");
        verify.equalToString("$.store.bicycle.id", "中国跑车");
    }

    @Test
    public void testEqualToNull() throws Exception {
        String a = null;
        String b = null;
        String c = "ha";
        Assert.assertEquals(a, b);
        Assert.assertNotEquals(c, a);
        Assert.assertNotEquals(a, c);
        Assert.assertEquals("null", String.format("%s", a));
        Assert.assertFalse("abc".equals(a));
    }

    @Test
    public void testEqualToStringIgnoreCase() throws Exception {
        verify.equalToStringIgnoreCase("$.store.bicycle.color", "RED");
        verify.equalToStringIgnoreCase("store.bicycle.color", "rEd");
    }

    @Test
    public void testIsEmptyCollection() throws Exception {
        verify.isEmptyCollection("$.store.book[?(@.category == 'x')]");
        verify.isEmptyCollection("store.book[?(@.category == 'x')]");
    }

    @Test
    public void testEqualToInt() throws Exception {
        verify.equalToInt("store.book[1].price", "12");
        verify.equalToInt("$.store.book[1].price", "12");
        verify.greaterThanInt("store.book[1].price", "10");
    }

    @Test
    public void testEqualToDouble() throws Exception {
        verify.equalToDouble("$.store.bicycle.price", "19.95D");
    }

    @Test
    public void testCollectionSizeEqualTo() throws Exception {
        verify.collectionSizeEqualTo("store.book", "4");
        verify.collectionSizeEqualTo("$.store.book", "4");
        verify.collectionSizeEqualTo("$..author", "4");
        verify.collectionSizeEqualTo("$..category", "4");
    }

    @Test
    public void testCollectionHasItem() throws Exception {
        verify.collectionHasItem("$..author", "Nigel Rees");
        verify.collectionHasItem("$..author", "Evelyn Waugh");
    }

    @Test
    public void testHasKey() throws Exception {
        verify.hasKey("$.store.book[0]", "author");
    }

    @Test
    public void testEqualTo() throws Exception {
        verify.equalTo("$.store.bicycle.price", "19.95");
    }

    @Test
    public void testContains() throws Exception {
        verify.contains("$.store.bicycle", "国跑");
    }

    @Test
    public void test() throws Exception {
        JsonPath.read(JSON, "$.store.book");
    }

    @Test
    public void testCollectionValueGreaterThanFloat() throws Exception {
        verify.collectionValueGreaterThanFloat("$.store.book[*].price", "8.94");
    }

    @BeforeClass
    public void setUpBeforeMethod() throws Exception {
        verify = new JsonVerify();
        verify.setResponse(JSON);
    }

    private JsonVerify verify;
    private static final String JSON = "{ \"store\": {\n"
            + "    \"book\": [ \n"
            + "      { \"category\": \"reference\",\n"
            + "        \"author\": \"Nigel Rees\",\n"
            + "        \"title\": \"Sayings of the Century\",\n"
            + "        \"price\": 8.95\n"
            + "      },\n"
            + "      { \"category\": \"fiction\",\n"
            + "        \"author\": \"Evelyn Waugh\",\n"
            + "        \"title\": \"Sword of Honour\",\n"
            + "        \"price\": \"12\"\n"
            + "      },\n"
            + "      { \"category\": \"fiction\",\n"
            + "        \"author\": \"Herman Melville\",\n"
            + "        \"title\": \"Moby Dick\",\n"
            + "        \"isbn\": \"0-553-21311-3\",\n"
            + "        \"price\": 8.99\n"
            + "      },\n"
            + "      { \"category\": \"fiction\",\n"
            + "        \"author\": \"J. R. R. Tolkien\",\n"
            + "        \"title\": \"The Lord of the Rings\",\n"
            + "        \"isbn\": \"0-395-19395-8\",\n"
            + "        \"price\": 22.99\n"
            + "      }\n"
            + "    ],\n"
            + "    \"bicycle\": {\n"
            + "      \"id\": \"中国跑车\",\n"
            + "      \"color\": \"red\",\n"
            + "      \"price\": 19.95,\n"
            + "      \"atoms\": " + Long.MAX_VALUE + ",\n"
            + "    }\n"
            + "  }\n"
            + "}";
}
