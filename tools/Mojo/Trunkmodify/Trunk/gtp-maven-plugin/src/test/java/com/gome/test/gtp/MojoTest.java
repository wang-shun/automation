package com.gome.test.gtp;


import org.codehaus.plexus.util.FileUtils;
import org.testng.annotations.Test;

import java.io.IOException;

public class MojoTest {
    @Test
    public void test() throws IOException {
        EndMojo m = new EndMojo();
        String json = FileUtils.fileRead("/Users/zhangliang/SourceCode/sample/Branches/APITestV2.0.0/TestProject/target/surefire-reports/japi4etp.json");
 //       m.saveLastRunTime(json);
    }
}
