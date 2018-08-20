package com.gome.test.api;

import static org.testng.Assert.assertEquals;

import java.io.File;

import com.gome.test.api.APIUtils;
import org.junit.Assert;
import org.testng.annotations.*;

public class JAPIUtilsTest {

    @Test(dataProvider = "testToLiteralString")
    public void testToLiteralString(String str, String expected) {
        String actual = APIUtils.toLiteralString(str);
        Assert.assertEquals(expected, actual);
    }

    @DataProvider(name = "testToLiteralString")
    private Object[][] testToLiteralStringDataProvider() {
        return new Object[][]{
                {"\\", "\"\\\\\""},
                {"\"", "\"\\\"\""},
                {"\t", "\"\\t\""},
                {"\r", "\"\\r\""},
                {"\n", "\"\\n\""}
        };
    }

    @Test
    public void testPackageNameToPath() {
        assertEquals("com\\elong\\Hotel", "com.elong.Hotel".replaceAll("\\.", "\\\\"));
        assertEquals("com/elong/Hotel", "com.elong.Hotel".replaceAll("\\.", "/"));
    }

    @Test
    public void testToParam() {
        String param = "abc D\te\nf\rAZ\r$9";
        String expected = "ABC_D_E_F_AZ__9";
        String actual = APIUtils.toParam(param);
        assertEquals(actual, expected);
    }

    private String toPath(String path) {
        if ('\\' == File.separatorChar) {
            return path.replaceAll("@", "\\\\");
        } else {
            return path.replaceAll("@", "/");
        }
    }
}
