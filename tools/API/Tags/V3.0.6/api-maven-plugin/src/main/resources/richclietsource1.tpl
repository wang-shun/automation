package @packageName@;

import java.util.*;
import com.gome.test.api.testng.*;
import com.gome.test.api.@servicename@.*;
import javax.annotation.Resource;
import org.testng.annotations.*;
import com.gome.test.context.*;
import com.gome.test.api.annotation.*;

public class @className@ extends RichClientContext {

    @Resource
    @verifyClass@Verify verify;

    @BeforeClass
    void beforeClass() {
         verify.beforeClass();
    }

     @AfterClass
     void afterClass() {
         verify.afterClass();
     }

     @BeforeMethod
     void beforeMethod() {
         verify.beforeMethod();
     }

     @AfterMethod
     void afterMethod() {
         verify.afterMethod();
     }


