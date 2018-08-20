package com.gome.test.gui.helper;

import com.gome.test.utils.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xpath.operations.Bool;
import org.jspringbot.keyword.selenium.SeleniumHelper;
import org.openqa.selenium.*;
import org.testng.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class GuiHelper extends SeleniumHelper {

    private String[] includekeyword;
    private String[] excludekeyword;

    public GuiHelper(WebDriver driver) {
        super(driver);
        initLinkKeyword();
    }

    public File captureScreenShotByPrefix(String prefix) throws IOException {
        byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        FileOutputStream out = null;
        try {
            File file = new File(screenCaptureDir, String.format("%s_%d_%d.png", prefix, screenCaptureSeed, ++screenCaptureCtr));
            out = new FileOutputStream(file);
            IOUtils.write(bytes, out);

            return file;
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public void scrollToElement(WebElement el) throws InterruptedException {
        executor.executeScript("arguments[0].scrollIntoView(true);", el);
        Thread.sleep(500);
    }

    public void captureScreenShot(String prefix, int width, File screenCaptureDir) throws IOException {
        this.setScreenCaptureDir(screenCaptureDir);
        captureScreenShot(prefix, width);
    }

    public void captureScreenShot(String prefix, int width) throws IOException {
        File screenShot = captureScreenShotByPrefix(prefix);
        Logger.info("<img style=\"width:%dpx;left:-160px;position:relative\" src=\"%s\"/>", width, screenShot.getAbsolutePath());
    }

    private void initLinkKeyword() {
        String str;
        if (this.includekeyword == null) {
            str = BasePage.compositeConfiguration.getString("link.includekeyword");
            if (str != null) {
                this.includekeyword = str.split("\\|");
            }
        }

        if (this.excludekeyword == null) {
            str = BasePage.compositeConfiguration.getString("link.excludekeyword");
            if (str != null) {
                this.excludekeyword = str.split("\\|");
            }
        }
    }

    private boolean checkContains(String[] keywords, String url) {
        boolean containsInclude = false;
        if (keywords != null) {
            if (keywords.length == 0)
                containsInclude = true;
            else {
                for (String s : keywords)
                    if (url.contains(s))
                        containsInclude = true;
            }
        }

        return containsInclude;
    }

    private boolean checkShouldOpen(String url) {
        return checkContains(this.includekeyword, url) && checkContains(this.excludekeyword, url) == false;
    }

    private int getMaxThreadCount() {
        String threads = BasePage.compositeConfiguration.getString("link.checkThreads");
        int maxThread = 5;
        if (threads != null || threads.isEmpty() == false)
            maxThread = Integer.valueOf(threads);

        Logger.info("maxThread: %s", maxThread);
        return maxThread;
    }

    public void getAndCheckAlllinks(WebElement webElement) throws Throwable {
        List<WebElement> links = webElement.findElements(By.tagName("a"));
        ExecutorService executorService = Executors.newFixedThreadPool(getMaxThreadCount());

        final Set<String> checkedLink = new HashSet<String>();
        final StringBuffer stringBuffer = new StringBuffer();

        for (WebElement item : links) {
            final String href = item.getAttribute("href");
            if (href != null && href.isEmpty() == false) {
                final String textin = item.getText();

                if (checkShouldOpen(href)) {
                    if (checkedLink.contains(href))
                        continue;

                    checkedLink.add(href);

                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (HttpClientUtils.checkSuccess(href) == false) {
                                    Logger.error("打开 [%s]%s 失败!", textin, href);
                                    synchronized (stringBuffer) {
                                        stringBuffer.append(String.format("%s;", href));
                                    }
                                } else {
                                    Logger.info("打开 [%s]%s 成功!", textin, href);
                                }
                            } catch (Exception e) {
                                Logger.error("打开 [%s]%s 失败! %s", textin, href, e.getMessage());
                                synchronized (stringBuffer) {
                                    stringBuffer.append(String.format("%s;", href));
                                }
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Logger.info("不用测试 [%s]%s!", textin, href);
                }
            }
        }

        executorService.shutdown();

        Thread.sleep(1000);
        synchronized (stringBuffer) {
            Assert.assertTrue(stringBuffer.length() == 0, String.format("检查链接失败: %s", stringBuffer));
        }
    }

    public static void killDriver() throws IOException {
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            try {
                Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
            } catch (Throwable ex) {
            }
            try {
                Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
            } catch (Throwable ex) {
            }
        }
    }

    public void getAndCheckAllImgs(WebElement webElement) throws Throwable {
        List<WebElement> imgs = webElement.findElements(By.tagName("img"));
        ExecutorService executorService = Executors.newFixedThreadPool(getMaxThreadCount());
        final Set<String> checkedLink = new HashSet<String>();
        final StringBuffer stringBuffer = new StringBuffer();

        for (WebElement item : imgs) {
            final String src = item.getAttribute("src");
            if (src != null && src.isEmpty() == false) {
                if (checkedLink.contains(src))
                    continue;

                checkedLink.add(src);

                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (HttpClientUtils.checkSuccess(src) == false) {
                                Logger.error("打开 %s 失败!", src);
                                synchronized (stringBuffer) {
                                    stringBuffer.append(String.format("%s;", src));
                                }
                            } else {
                                Logger.info("打开 %s 成功!", src);
                            }
                        } catch (Exception e) {
                            Logger.error("打开 %s 失败! %s", src, e.getMessage());
                            synchronized (stringBuffer) {
                                stringBuffer.append(String.format("%s;", src));
                            }
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        executorService.shutdown();
        Thread.sleep(1000);
        synchronized (stringBuffer) {
            Assert.assertTrue(stringBuffer.length() == 0, String.format("检查链接失败: %s", stringBuffer));
        }
    }

    public boolean waitTillElementFound(WebElement element, long pollMillis, long timeoutMillis) {
        long start = System.currentTimeMillis();
        long elapse = -1;

        do {
            if (elapse != -1) {
                try {
                    Thread.sleep(pollMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            elapse = System.currentTimeMillis() - start;
        } while (element == null && elapse < timeoutMillis);

        if (element == null) {
            return false;
        } else {
            return true;
        }
    }

    public void waitTillElementVisible(WebElement element, long pollMillis, long timeoutMillis) {

    }

    public void waitTillElementContainsRegex(WebElement element, String regex, long pollMillis, long timeoutMillis) {

    }

    public boolean waitTillElementContainsText(WebElement element, String text, long pollMillis, long timeoutMillis) {
        long start = System.currentTimeMillis();
        long elapse = -1;

        do {
            if (elapse != -1) {
                try {
                    Thread.sleep(pollMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            elapse = System.currentTimeMillis() - start;

            // only set el to none null if text is found.
            if (element != null && !StringUtils.contains(element.getText(), text)) {
                element = null;
            }
        } while (element == null && elapse < timeoutMillis);

        if (element == null) {
            return false;
        } else {
            return true;
        }
    }

    public void mouseOver(WebElement el) {
        String code = "var evObj = document.createEvent(\'MouseEvents\');evObj.initEvent( \'mouseover\', true, true );arguments[0].dispatchEvent(evObj);";
        this.executor.executeScript(code, new Object[]{el});
    }
}
