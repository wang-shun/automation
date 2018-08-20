package @packageName@;

import org.testng.Assert;
import com.gome.test.utils.Logger;
import com.gome.test.api.adapter.IVerify;
import org.springframework.stereotype.Component;
@importReturnType@;

@Component
public class @method@Verify extends @method@Adapter implements IVerify {

       public void verify(Object obj) {
             @init@
       }
}