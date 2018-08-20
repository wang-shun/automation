package @packageName@;

import org.testng.Assert;
import com.ofo.test.utils.Logger;
import com.ofo.test.api.adapter.IVerify;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Lazy;

@Component
@Lazy
public class @className@@method@Verify extends @className@@method@Adapter implements IVerify {

       public void verify(Object obj) {
             @init@
       }
}