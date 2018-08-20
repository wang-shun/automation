package com.gome.test.api.model;

import com.gome.test.api.model.Steps;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StepsTest {

    @Test
    public void testLoadFrom() {
        Steps steps = Steps.loadFrom(verifySteps);
        Assert.assertEquals(steps.size(), 4);
        // verify keyword
        Assert.assertEquals(steps.get(0).getKeyword(), "Verifyorder_list");
        Assert.assertEquals(steps.get(1).getKeyword(), "collectionEqualToObjectFromSQL");
        Assert.assertEquals(steps.get(2).getKeyword(), "equalToString");
        Assert.assertEquals(steps.get(3).getKeyword(), "equalToInt");
        // verify doc
        for (int i = 0; i < 4; ++i) {
            Assert.assertNull(steps.get(i).getDoc());
        }
        // verify args
        Assert.assertEquals(steps.get(0).getArgs().size(), 2);
        Assert.assertEquals(steps.get(1).getArgs().size(), 2);
        Assert.assertEquals(steps.get(2).getArgs().size(), 2);
        Assert.assertEquals(steps.get(3).getArgs().size(), 2);
        // verify args(0)
        Assert.assertEquals(steps.get(0).getArgs().get(0), "$.orderList");
        Assert.assertEquals(steps.get(1).getArgs().get(0), "$.orderList");
        Assert.assertEquals(steps.get(2).getArgs().get(0), "retDesc");
        Assert.assertEquals(steps.get(3).getArgs().get(0), "retCode");
        // verify args(1)
        Assert.assertEquals(steps.get(0).getArgs().get(1), "getTrainOrderMisc");
        Assert.assertEquals(steps.get(1).getArgs().get(1), "getTrainOrderList");
        Assert.assertEquals(steps.get(2).getArgs().get(1), "操作成功");
        Assert.assertEquals(steps.get(3).getArgs().get(1), "200");
    }

    private static final String verifySteps = "[[\"Verifyorder_list\",\"$.orderList\",\"getTrainOrderMisc\"],[\"collectionEqualToObjectFromSQL\",\"$.orderList\",\"getTrainOrderList\"],[\"equalToString\",\"retDesc\",\"操作成功\"],[\"equalToInt\",\"retCode\",\"200\"]]";
}
