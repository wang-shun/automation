package com.gome.test.api.verify;

import com.gome.test.api.verify.TextVerify;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TextVerifyTest {

    @Test
    public void testEqualTo() throws Exception {
        verify.equalTo("中文haha123");
    }

    @Test
    public void testEqualToIgnoreCase() throws Exception {
        verify.equalToIgnoreCase("中文HahA123");
    }

    @Test
    public void testStartsWith() throws Exception {
        verify.startsWith("中文haha");
    }

    @Test
    public void testEndsWith() throws Exception {
        verify.endsWith("文haha123");
    }

    @Test
    public void testContains() throws Exception {
        verify.contains("文ha");
    }

    @BeforeClass
    public void setUpBeforeMethod() throws Exception {
        verify = new TextVerify();
        verify.setResponse(RESPONSE);
    }

    private TextVerify verify;
    private static final String RESPONSE = "中文haha123";
}
