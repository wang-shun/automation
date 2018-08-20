package com.gome.test.gui;

import com.gome.test.gui.annotation.Given;
import com.gome.test.gui.helper.BasePage;

public class BusinessBase {
    @Given(description = "Common.跳转至指定路径")
    public void navigateTo(String url) {
        BasePage.helper.navigateTo(url);
    }

    @Given(description = "Common.清除所有Cookie")
    public void deleteAllCookies() {
        BasePage.helper.deleteAllCookies();
    }

    @Given(description = "Common.异步加载")
    public void asyncNavigateTo(final String url) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BasePage.helper.navigateTo(url);
                } catch (Exception ex) {
                }
            }
        }).start();
    }

    @Given(description = "Common.等待指定的毫秒")
    public void wait(String millis) throws InterruptedException, NumberFormatException {
        long millisSecond = Long.valueOf(millis);
        Thread.sleep(millisSecond);
    }

    @Given(description = "Common.刷新页面")
    public void refresh() {
        BasePage.helper.reloadPage();
    }

}
