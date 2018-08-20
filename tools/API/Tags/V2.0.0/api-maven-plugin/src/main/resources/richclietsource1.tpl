package @packageName@;

import java.util.*;
import com.gome.test.api.redcouponservice.*;
import com.gome.test.api.testng.*;
import javax.annotation.Resource;
import org.testng.annotations.*;
import org.testng.annotations.*;
import com.gome.test.api.utils.ApiUtils;

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


