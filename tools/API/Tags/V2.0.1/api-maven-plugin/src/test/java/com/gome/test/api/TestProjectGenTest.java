package com.gome.test.api;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.gome.test.api.TestProjectGen;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestProjectGenTest {
//
//    @Test(dataProvider = "testGetClassName", enabled = false)
//    public void testGetClassName(String filename, String expect)
//            throws NoSuchMethodException, SecurityException,
//            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//        TestProjectGen pg = new TestProjectGen();
//        Method m = TestProjectGen.class.getDeclaredMethod("getClassName", new Class[]{String.class});
//        m.setAccessible(true);
//        String actual = m.invoke(pg, filename).toString();
//        m.setAccessible(false);
//        assertTrue(expect.equals(actual));
//    }
//
//    @DataProvider(name = "testGetClassName")
//    private Object[][] testGetClassNameDataProvider() {
//        return new Object[][]{
//                {"hello.xlsx", "HelloTest"},
//                {"Hello.xlsx", "HelloTest"},
//                {"_09hello.xlsx", "_09helloTest"}
//        };
//    }
}
