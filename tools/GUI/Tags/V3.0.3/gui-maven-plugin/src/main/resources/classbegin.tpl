package @packageName@;

import com.gome.test.gui.annotation.CaseOwner;
import com.gome.test.gui.execute.GuiExecutor;
import com.gome.test.gui.helper.TestBase;
import com.gome.test.utils.Logger;
import org.testng.annotations.*;

public class @className@ extends TestBase {

    GuiExecutor executor;

    @BeforeClass(alwaysRun = true)
    public void loadCase() throws Exception {
        Logger.info("execute BeforeClass : [@casefile@]");
        executor = new GuiExecutor("@casefile@");
    }
