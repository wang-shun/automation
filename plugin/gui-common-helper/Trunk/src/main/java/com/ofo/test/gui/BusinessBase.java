package com.ofo.test.gui;

import com.gome.test.gui.annotation.Given;
import com.gome.test.gui.annotation.When;
import com.ofo.test.gui.helper.BasePage;
import com.ofo.test.gui.helper.BrowserType;
import com.gome.test.utils.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BusinessBase {
    @Given(description = "Common.跳转至指定路径")
    public void navigateTo(String url) {
        //打开页面
        BasePage.helper.navigateTo(url);
    }

    @Given(description = "Common.清除所有Cookie")
    public void deleteAllCookies() {
        BasePage.helper.deleteAllCookies();
        if(BasePage.getDriverType()== BrowserType.IE)
        {
//            String code="var date=new Date();var expiresDays=1;date.setTime(date.getTime()+expiresDays*24*3600*1000);";
//            Set<Cookie> cookies=BasePage.driver.manage().getCookies();
            BasePage.driver.manage().deleteAllCookies();
        }
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


    @Given(description = "Common.页面截屏全屏")
    public void screenCapture() throws Exception {

        File screenCaptureDir = BasePage.getOutPutPictureFile();
        String url ="截图测试" + BasePage.executor.executeScript("return window.location.host ").toString();
        try {
            printPage(url, screenCaptureDir);
        } catch (Exception ex) {
            Logger.error("滚动截屏发生异常，原因：" + ex.getMessage());
        }

    }

    private void printPage(String pName, File screenCaptureDir) throws Exception {

        if (BasePage.getDriverType() == BrowserType.FIREFOX || BasePage.getDriverType() == BrowserType.IE) {
            BasePage.helper.scrollToBottom();
            //firefox || ie  只截一屏
            BasePage.helper.captureScreenShot(pName, 800, screenCaptureDir);
        } else {

            Long thisprint = (Long) BasePage.executor.executeScript("return window.screen.availHeight ;") ;
            Long maxHeight = (Long) BasePage.executor.executeScript("return document.body.scrollHeight;");
            Integer pictureCount = Integer.valueOf(String.valueOf(maxHeight / thisprint)) ;
            Long pictureM = maxHeight % thisprint ;
            pictureCount = pictureM > 0 ? pictureCount+1 : pictureCount;

            for(int i=0;i< pictureCount ;i ++)
            {
                Thread.sleep(3000);
                BasePage.helper.captureScreenShot(pName + "_" + (i+1) , 800, screenCaptureDir);
                if( i < (pictureCount -1)) {
                    BasePage.executor.executeScript("window.scrollBy(0, " + ( thisprint-50 ) + ")");
                }
            }

        }
    }


    int depth = 0;
    int i=0;//判断执行完所有的层
    Boolean iserror = false;
    String check = null;
    List<String> errorURL = new ArrayList<String>();
    List<String> warnigURL = new ArrayList<String>();
    List<String> allUrlList = new ArrayList<String>();
    List<String> Allgome = new ArrayList<String>();
    long c;
    @Given(description = "Common.查看当前URL所有一级连接有无死链接")
    public void openAllLink(String url_check_depth) throws Exception{

        List<String> currentallUrlList = new ArrayList<String>();
        List<String> currenterrorURL = new ArrayList<String>();
        List<String> currentwarnigURL = new ArrayList<String>();
        List<String> gome = new ArrayList<String>();
        String[] param = url_check_depth.split("_");
        String currentUrl=null;

        if (param.length>1){
            currentUrl = param[0];
            check = param[1];
            depth = (Integer.valueOf(param[2])>2)?2:Integer.valueOf(param[2]);
            depth = (depth<0)?1:depth;
            i=depth;
            c = System.currentTimeMillis();
        }else {
            currentUrl = param[0];
        }

        int begin = currentUrl.indexOf("//");
        String childUrl = currentUrl.substring(begin+2);
        int end = childUrl.indexOf("/");
        String hosts = currentUrl.substring(0,end+begin+2);

        this.navigateTo(currentUrl);
        List<WebElement> lists = BasePage.driver.findElements(By.xpath("//a[contains(@href,'//')]"));
        for (WebElement url:lists){
            System.out.println("Urls:"+url.getAttribute("href"));
        }

        for (WebElement href:lists){
            String url = href.getAttribute("href");
            if (!url.isEmpty()&&url.startsWith("http")||url.startsWith("//")){
                System.out.println(url);
                if (!url.startsWith("http")){
                    url = String.format("%s%s",hosts,url.substring(1));
                }
                allUrlList.add(url);
                currentallUrlList.add(url);

                String originalhandl = BasePage.driver.getWindowHandle();
                Set<String> winhandles = BasePage.driver.getWindowHandles();
                BasePage.executor.executeScript("window.open('"+url+"')");
                String currentWin = BasePage.getNewWindow(winhandles);
                BasePage.driver.switchTo().window(currentWin);
                BasePage.driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
                BasePage.driver.findElement(By.tagName("body"));
                String title = BasePage.driver.getTitle();

                if (!url.contains(check)){
                    BasePage.driver.switchTo().window(originalhandl);
                    BasePage.highlightElment(href,"yellow");
                    iserror=true;
                    warnigURL.add(url);
                    currentwarnigURL.add(url);
                    BasePage.driver.switchTo().window(currentWin);
                }

                if (title.contains("40")||title.contains("50")||title.contains("错")||title.contains("抱歉")||title.contains("网上选购最新款商品尽在国美在线Gome.com.cn")||title.contains("没有找到您想要进入的搜索结果页面")){
                    BasePage.driver.switchTo().window(originalhandl);
                    BasePage.highlightElment(href,"red");
                    iserror=true;
                    errorURL.add(url);
                    currenterrorURL.add(url);
                    BasePage.driver.switchTo().window(currentWin);
                }
                if(BasePage.driver.getPageSource().contains("国美在线")||title.contains("国美在线")){
                    gome.add(url);
                    Allgome.add(url);
                }

                if (depth>1){
                    depth--;
                    this.openAllLink(url);
                }
                BasePage.driver.close();
                BasePage.driver.switchTo().window(originalhandl);
            }else {
                Logger.error("关联错误"+url);
                BasePage.highlightElment(href,"red");
                errorURL.add(url);
            }

        }
        Logger.info(String.format("在"+BasePage.driver.getCurrentUrl()+"页面:"+"共发现%d个url",currentallUrlList.size()));
        Logger.info(String.format("在"+BasePage.driver.getCurrentUrl()+"页面:"+"不包含指定值%d个url",currentwarnigURL.size()));
        Logger.info(String.format("在"+BasePage.driver.getCurrentUrl()+"页面:"+"死连接%d个url",currenterrorURL.size()));
        Logger.info(String.format("在"+BasePage.driver.getCurrentUrl()+"页面:"+"包含\"国美在线\"%d个url页面",gome.size()));

        if (iserror){
            this.screenCapture();
        }

        for (String Url:currentwarnigURL){
            Logger.warn("在"+BasePage.driver.getCurrentUrl()+"页面:"+"不含指定值的URL："+Url);
        }
        for (String Url:currenterrorURL){
            Logger.error("在"+BasePage.driver.getCurrentUrl()+"页面:"+"死链接URL："+Url);
        }
        for (String Url:gome){
            Logger.error("在"+BasePage.driver.getCurrentUrl()+"页面:"+"包含\"国美在线\"："+Url);
        }

        if (i==1){
            Logger.info(String.format("本次测试共发现%d个url",allUrlList.size()));
            Logger.info(String.format("本次测试不包含指定值%d个url",warnigURL.size()));
            Logger.info(String.format("本次测试死连接%d个url",errorURL.size()));
            Logger.info(String.format("本次测试发现包含\"国美在线\"%d个url",Allgome.size()));

            Logger.info("测试结束----------------------"+"本次测试耗时"+(System.currentTimeMillis()-c)/1000+"秒------------------------------------测试结束");
            Assert.assertTrue(!iserror);
        }
        i--;
    }


    /**
     *
     * @param check_depth
     * @throws Exception
     */

    @Given(description = "Common.查看当前driver所在页面所有一级连接有无死链接")
    public void openDriverAllLink(String check_depth) throws Exception {
        Boolean iserror = false;
        List<String> currentallUrlList = new ArrayList<String>();
        List<String> currenterrorURL = new ArrayList<String>();
        List<String> currentwarnigURL = new ArrayList<String>();
        List<String> gome = new ArrayList<String>();

        String[] param = check_depth.split("_");
        String currentUrl;

        if (param.length > 1) {
            currentUrl = BasePage.driver.getCurrentUrl();
            ;
            check = param[0];
            depth = (Integer.valueOf(param[1]) > 2) ? 2 : Integer.valueOf(param[1]);
            depth = (depth < 1) ? 1 : depth;
            i = depth;
            c = System.currentTimeMillis();
        } else {
            currentUrl = param[0];
        }

        int begin = currentUrl.indexOf("//");
        String childUrl = currentUrl.substring(begin + 2);
        int end = childUrl.indexOf("/");
        String hosts = currentUrl.substring(0, end + begin + 2);
        List<WebElement> lists = BasePage.driver.findElements(By.xpath("//a[contains(@href,'//')]"));
        for (WebElement url : lists) {
            System.out.println("Urls:" + url.getAttribute("href"));
        }

        for (WebElement href : lists) {
            String url = href.getAttribute("href");
            if (!url.isEmpty() && url.startsWith("http") || url.startsWith("//")) {
                System.out.println(url);
                if (!url.startsWith("http")) {
                    url = String.format("%s%s", hosts, url.substring(1));
                }
                allUrlList.add(url);
                currentallUrlList.add(url);

                String originalhandl = BasePage.driver.getWindowHandle();
                Set<String> winhandles = BasePage.driver.getWindowHandles();
                BasePage.executor.executeScript("window.open('" + url + "')");
                String currentWin = BasePage.getNewWindow(winhandles);
                BasePage.driver.switchTo().window(currentWin);
                BasePage.driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
                BasePage.driver.findElement(By.tagName("body"));
                String title = BasePage.driver.getTitle();

                if (!url.contains(check)) {
                    BasePage.driver.switchTo().window(originalhandl);
                    BasePage.highlightElment(href, "yellow");
                    iserror = true;
                    warnigURL.add(url);
                    currentwarnigURL.add(url);
                    BasePage.driver.switchTo().window(currentWin);
                }

                if (title.contains("40") || title.contains("50") || title.contains("错") || title.contains("抱歉") || title.contains("网上选购最新款商品尽在国美在线Gome.com.cn") || title.contains("没有找到您想要进入的搜索结果页面")) {
                    BasePage.driver.switchTo().window(originalhandl);
                    BasePage.highlightElment(href, "red");
                    iserror = true;
                    errorURL.add(url);
                    currenterrorURL.add(url);
                    BasePage.driver.switchTo().window(currentWin);
                }

                if(BasePage.driver.getPageSource().contains("国美在线")||title.contains("国美在线")){
                    gome.add(url);
                    Allgome.add(url);
                }

                if (depth >1) {
                    depth--;
                    this.openDriverAllLink(url);
                }
                BasePage.driver.close();
                BasePage.driver.switchTo().window(originalhandl);
            } else {
                Logger.error("关联错误" + url);
                BasePage.highlightElment(href, "red");
                errorURL.add(url);
            }

        }
        Logger.info(String.format("driver在" + BasePage.driver.getCurrentUrl() + "页面:" + "共发现%d个url", currentallUrlList.size()));
        Logger.info(String.format("driver在" + BasePage.driver.getCurrentUrl() + "页面:" + "不包含指定值%d个url", currentwarnigURL.size()));
        Logger.info(String.format("driver在" + BasePage.driver.getCurrentUrl() + "页面:" + "死连接%d个url", currenterrorURL.size()));
        Logger.info(String.format("driver在"+  BasePage.driver.getCurrentUrl()+"页面:"+"包含\"国美在线\"%d个url页面",gome.size()));

        if (iserror) {
            this.screenCapture();
        }

        for (String Url : currentwarnigURL) {
            Logger.warn("driver在" + BasePage.driver.getCurrentUrl() + "页面:" + "不含指定值的URL：" + Url);
        }
        for (String Url : currenterrorURL) {
            Logger.error("driver在" + BasePage.driver.getCurrentUrl() + "页面:" + "死链接URL：" + Url);
        }

        for (String Url:gome){
            Logger.error("在"+BasePage.driver.getCurrentUrl()+"页面:"+"包含\"国美在线\"："+Url);
        }

        if (i == 1) {
            Logger.info(String.format("driver本次测试共发现%d个url", allUrlList.size()));
            Logger.info(String.format("driver本次测试不包含指定值%d个url", warnigURL.size()));
            Logger.info(String.format("driver本次测试死连接%d个url", errorURL.size()));
            Logger.info(String.format("本次测试发现包含\"国美在线\"%d个url",Allgome.size()));

            Logger.info("driver测试结束----------------------" + "本次测试耗时" + (System.currentTimeMillis() - c) / 1000 + "秒------------------------------------测试结束");
            Assert.assertTrue(!iserror);
        }
        i--;
    }

}
