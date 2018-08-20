package @packageName@;

import java.util.Map;
import javax.annotation.Resource;
import com.gome.test.api.adapter.IAdapter;
import @importPackage@;
@importReturnType@
@importParameterTypes@

public class @method@Adapter extends IAdapter<@className@>{

    @Resource
    @className@ @serviceParamName@;

    @Override
    public Object adapter(Map<String, String> map) {
           //@init@
           return null;
    }

    @Override
    public void beforeClass() {

    }

    @Override
    public void beforeMethod() {

    }

    @Override
    public void afterClass() {

    }

    @Override
    public void afterMethod() {

    }
}