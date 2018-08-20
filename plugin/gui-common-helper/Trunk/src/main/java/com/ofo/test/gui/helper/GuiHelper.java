package com.ofo.test.gui.helper;

import com.gome.test.context.ContextUtils;
import com.gome.test.context.IContext;
import com.gome.test.utils.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jspringbot.keyword.selenium.SeleniumHelper;
import org.openqa.selenium.*;
import org.testng.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuiHelper extends SeleniumHelper {

    private static List<String> linklist = Collections.synchronizedList(new ArrayList());
    private static List<String> imglist = Collections.synchronizedList(new ArrayList());
    private String[] includekeyword;
    private String[] excludekeyword;


    public GuiHelper(WebDriver driver) {
        super(driver);
        initLinkKeyword();
    }

    public static void killDriver() throws IOException {
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            try {
                Runtime.getRuntime().exec("taskkill -F -IM chromedriver.exe");
            } catch (Throwable ex) {
            }
            try {
                Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
            } catch (Throwable ex) {
            }
        }
//        else {
//            try {
//                //杀appium服务
//                Runtime.getRuntime().exec("lsof -i :4723 | awk 'NR>1{print$2}' |xargs kill -9");
//            } catch (Throwable ex) {
//            }
//        }
    }

    public File getScreenCaptureDir() {
        return this.screenCaptureDir;
    }

    private File captureScreenShotByPrefix(String prefix,int x, int y, int w, int h ,File screenCaptureDir) throws IOException {

        try {
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(bytes));

            BufferedImage croppedImage = originalImage.getSubimage(x, y, w, h);


            File file = new File(screenCaptureDir, String.format("%s_%d_%d.png", prefix, screenCaptureSeed, ++screenCaptureCtr));

            ImageIO.write(croppedImage, "png", file);

            return file;
        } finally {

//            ImageIO.c.closeQuietly(out);
        }
    }

    private File captureScreenShotByPrefix(String prefix) throws IOException {
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

    public File captureScreenShotByPrefix2(String prefix) {
        // this method will take screen shot ,require two parameters ,one is driver name, another is file name
        File file = new File(screenCaptureDir, String.format("%s_%d_%d.png", prefix, screenCaptureSeed, ++screenCaptureCtr));

        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        // Now you can do whatever you need to do with it, for example copy somewhere
        try {
            FileUtils.copyFile(scrFile, file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Can't save screenshot");
            e.printStackTrace();
        }
        return file;
    }


    public void scrollToElement(WebElement el) throws InterruptedException {
        executor.executeScript("arguments[0].scrollIntoView(true);", el);
        Thread.sleep(500);
    }

    public void captureScreenShot(String prefix, int width, File screenCaptureDir) throws IOException {
        this.setScreenCaptureDir(screenCaptureDir);
        captureScreenShot(prefix, width);
    }


    public static void main(String[] abc) throws Exception
    {
        String prefix="下单主流程-2_1-返回收银台-F_1464743065145_1";
        prefix = new String(prefix.getBytes("UTF-8"),"UTF-8");
        System.out.println(prefix);
    }

    public void captureScreenShot(String prefix,int width,int x, int y, int w, int h,File  screenCaptureDir) throws IOException
    {
        prefix=new String(prefix.getBytes("UTF-8"),"UTF-8") ;
        File screenShot = captureScreenShotByPrefix(prefix,x,y,w,h,screenCaptureDir);
        String imgName = screenShot.getName();
        Logger.info("<img style=\"width:%dpx;left:-160px;position:relative\" src=\"../../test-classes/screenCapture/%s\"/>", width, imgName);

    }

    public void captureScreenShot(String prefix, int width) throws IOException {

        prefix=new String(prefix.getBytes("UTF-8"),"UTF-8") ;

        File screenShot = captureScreenShotByPrefix(prefix);
        String imgName = screenShot.getName();
        Logger.info("<img style=\"width:%dpx;left:-160px;position:relative\" src=\"../../test-classes/screenCapture/%s\"/>", width, imgName);

    }

    private void initLinkKeyword() {
        String[] str;
        if (this.includekeyword == null) {
            str = IContext.compositeConfiguration.getStringArray("link.includekeyword");
            if (str != null) {
                this.includekeyword = str;
            }
        }

        if (this.excludekeyword == null) {
            str = IContext.compositeConfiguration.getStringArray("link.excludekeyword");
            if (str != null) {
                this.excludekeyword = str;
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
        String threads = ContextUtils.getString("link.checkThreads", "5");
        int maxThread = 5;
        if (threads != null || threads.isEmpty() == false)
            maxThread = Integer.valueOf(threads);

        Logger.info("maxThread: %s", maxThread);
        return maxThread;
    }

    public void getAndCheckAlllinks(WebElement webElement) throws Throwable {
        List<WebElement> links = webElement.findElements(By.tagName("a"));
        ExecutorService executorService = Executors.newFixedThreadPool(getMaxThreadCount());

        final StringBuffer stringBuffer = new StringBuffer();

        for (WebElement item : links) {
            final String href = item.getAttribute("href");
            if (href != null && href.isEmpty() == false) {
                final String textin = item.getText();

                if (checkShouldOpen(href)) {
                    executorService.submit(new Runnable() {
                        public void run() {
                            try {
                                long start = System.currentTimeMillis();

                                if (!linklist.contains(href)) {

                                    if (HttpClientUtils.checkSuccess(href) == false) {
                                        Logger.error("打开 [%s]%s 失败!", textin, href);
                                        synchronized (stringBuffer) {
                                            stringBuffer.append(String.format("%s;", href));
                                        }
                                    } else {
                                        Logger.info("打开 [%s]%s 成功! 耗时 %sms", textin, href, System.currentTimeMillis() - start);
                                        linklist.add(href);
                                    }
                                } else {
                                    Logger.info("[%s]%s 已经缓冲，本次无需测试! 耗时 %sms", textin, href, System.currentTimeMillis() - start);
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

    public void getAndCheckAllImgs(WebElement webElement) throws Throwable {
        List<WebElement> imgs = webElement.findElements(By.tagName("img"));
        ExecutorService executorService = Executors.newFixedThreadPool(getMaxThreadCount());
        final StringBuffer stringBuffer = new StringBuffer();

        for (WebElement item : imgs) {
            final String src = item.getAttribute("src");
            final String alt = item.getAttribute("alt");
            if (src != null && src.isEmpty() == false) {

                executorService.submit(new Runnable() {
                    public void run() {
                        try {
                            long start = System.currentTimeMillis();

                            if (!imglist.contains(src)) {
                                if (HttpClientUtils.checkSuccess(src) == false) {
                                    Logger.error("打开 [%s]%s 失败!", alt, src);
                                    synchronized (stringBuffer) {
                                        stringBuffer.append(String.format("[%s]%s;", alt, src));
                                    }
                                } else {
                                    Logger.info("打开 [%s]%s 成功! 耗时 %sms", alt, src, System.currentTimeMillis() - start);
                                    imglist.add(src);
                                }
                            } else {
                                Logger.info("[%s]%s 已经缓冲，本次无需测试! 耗时 %sms", alt, src, System.currentTimeMillis() - start);
                            }
                        } catch (Exception e) {
                            Logger.error("打开 [%s]%s 失败! %s", alt, src, e.getMessage());
                            synchronized (stringBuffer) {
                                stringBuffer.append(String.format("[%s]%s;", alt, src));
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
        if (BasePage.getDriverType() == BrowserType.IE)
            code = "var evObj  = document.createEventObject();arguments[0].fireEvent(\"onmouseover\",evObj);";
        this.executor.executeScript(code, new Object[]{el});
    }

    public void scrollToBottom() {
        try {
            //当前屏幕高度
            Long thisprint = (Long) BasePage.executor.executeScript("return window.screen.availHeight ;");
            //网页高度
            Long maxHeight = (Long) BasePage.executor.executeScript("return document.body.scrollHeight;");
            // 网页高度 / 屏幕高度
            Integer pictureCount = Integer.valueOf(String.valueOf(maxHeight / thisprint));
            Long pictureM = maxHeight % thisprint;
            //有余数则 +1
            pictureCount = pictureM > 0 ? pictureCount + 1 : pictureCount;

            for (int i = 0; i < pictureCount; i++) {
                Thread.sleep(5000);
                BasePage.executor.executeScript("window.scrollBy(0, " + (thisprint - 100) + ")");
                if (i == pictureCount - 1)
                    Thread.sleep(3000);

            }
        } catch (Exception e) {

        }
    }


}
