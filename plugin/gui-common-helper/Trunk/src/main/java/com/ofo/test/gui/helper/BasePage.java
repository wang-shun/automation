package com.ofo.test.gui.helper;


import com.gome.test.context.ContextUtils;
import com.gome.test.gui.model.Case;
import com.gome.test.gui.model.Step;
import com.gome.test.utils.Logger;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.File;
import java.lang.Thread;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.reflections.util.ClasspathHelper.forJavaClassPath;

public class BasePage {
    public static final String EMPTY_PAGE = "about:blank";
    public static final String START_BROWSE_ID = "START_BROWSE_ID";
    private static final String GUI_XML = "/gui.xml";
    private static final String GUI_PROP = "/gui.properties";
    private static final String PAGE_CONATINER = "pageContainer";
    public static WebDriver driver;
    public static JavascriptExecutor executor;
    public static GuiHelper helper;
    private static Settings settings;

    public static File getOutPutPictureFile() {
        if(helper.getScreenCaptureDir() == null)
            return   new File(String.format("%s%sscreenCapture", BasePage.class.getResource("/").getPath(), File.separator));
        else
            return  helper.getScreenCaptureDir() ;
    }


    public static Case getCurrentCase()
    {
        String key="currentCase";
        if(ContextUtils.getContext().containsKey(key))
            return (Case)ContextUtils.getContext().get(key);

        return null;
    }

    public static Step getCurrentStep()
    {
        String key="currentStep";
        if(ContextUtils.getContext().containsKey(key))
            return (Step)ContextUtils.getContext().get(key);

        return null;
    }


    public static void initDriver() throws Exception {
        settings = new Settings();
        init(GUI_XML, GUI_PROP);
    }

    public static BrowserType getDriverType()
    {
        return settings.getBrowser();
    }

    public static Boolean isPrint() { return  settings.isPrint ;}

    public static void initDriver(String configFilePath) throws Exception {
        settings = new Settings(configFilePath);
        File resources = new File(configFilePath).getParentFile();
        String xml = new File(resources, GUI_XML).getAbsolutePath();
        String prop = new File(resources, GUI_PROP).getAbsolutePath();
        init(xml, prop);
    }

    private static void init(String xml, String prop) throws Exception {
        initBrowser();
        ContextUtils.LoadConfig(xml, prop, forJavaClassPath());
    }

    public static void closeAllCaseWindows() {
        if(settings.getBrowser()==BrowserType.IPAD_SAFARI)
        {
            boolean isout=((AppiumDriver) driver).getContext() == null;
           while (!isout && !((AppiumDriver) driver).getContext().equals("NATIVE_APP") )
           {
               try {
                   Thread.sleep(2000);
               }catch (Exception ex)
               {

               }
               isout = ((AppiumDriver) driver).getContextHandles().size()==2 ;
               String context=((AppiumDriver) driver).getContext();
               ((AppiumDriver) driver).context(context);
               driver.close();
           }

        }else {

            Set<String> handles = driver.getWindowHandles();
            String startId = ContextUtils.getFromContext(START_BROWSE_ID);
            for (String s : handles) {
                if (startId.equals(s) == false) {
                    driver.switchTo().window(s);
                    driver.close();
                }
            }
        }
    }

    public static void initBrowser() throws Exception {
        driver = settings.getDriver();

        driver.get(EMPTY_PAGE);

        ContextUtils.addToContext(START_BROWSE_ID, settings.getBrowser()==BrowserType.IPAD_SAFARI?((AppiumDriver)driver).getContext():BasePage.driver.getWindowHandle());
    }

    public static void openAndSwitchtoCaseWindow() throws Exception {


        try {
                if(settings.getBrowser() == BrowserType.IPAD_SAFARI)
                {
                    ((AppiumDriver) driver).context(ContextUtils.getFromContext(BasePage.START_BROWSE_ID));
                }else
                    driver.switchTo().window(ContextUtils.getFromContext(BasePage.START_BROWSE_ID));
        } catch (Exception ex) {
            Logger.info("原始窗口已关闭,即将重启浏览器");

            initBrowser();
        }
        initPage(BasePage.class);

        for (int i = 0; i < 3; i++) {
            try {
                executor.executeScript(
                        String.format("var tmp = window.open('%s', '%s', 'toolbar=yes,menubar=yes,scrollbars=yes, resizable=yes,location=yes,fullscreen=0');tmp.moveTo(0, 0);tmp.resizeTo(screen.width + 20, screen.height);tmp.focus();",
                                EMPTY_PAGE, PAGE_CONATINER, EMPTY_PAGE));
                break;
            } catch (Exception ex) {
                if (i == 2)
                    throw ex;
                else
                    Thread.sleep(5000);
            }
        }
        if(settings.getBrowser() != BrowserType.IPAD_SAFARI) //ipad 自动切换到打开页面所以不设置
            driver.switchTo().window(PAGE_CONATINER);
    }

    public static <T extends BasePage> T initPage(Class<T> pageClass) {
        executor = (JavascriptExecutor) driver;
        helper = new GuiHelper(driver);
        T page = instantiatePage(driver, pageClass);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, Settings.pageWait), page);
        driver.manage().timeouts().pageLoadTimeout(Settings.pageWait, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(Settings.implicitWait, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(Settings.scriptWait, TimeUnit.SECONDS);
        return page;
    }

    public static <T extends BasePage> T initPage(SearchContext searchContext, Class<T> pageClass) {
        T page = instantiatePage(searchContext, pageClass);
        PageFactory.initElements(new AjaxElementLocatorFactory(searchContext, Settings.pageWait), page);
        driver.manage().timeouts().pageLoadTimeout(Settings.pageWait, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(Settings.implicitWait, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(Settings.scriptWait, TimeUnit.SECONDS);
        return page;
    }

    private static <T> T instantiatePage(SearchContext searchContext, Class<T> pageClassToProxy) {
        try {
            try {
                Constructor<T> constructor = pageClassToProxy.getConstructor(SearchContext.class);
                return constructor.newInstance(searchContext);
            } catch (NoSuchMethodException e) {
                return pageClassToProxy.newInstance();
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 批量把元素添加样式，类似高亮（border：red）
     * @param elements
     */
    public static void highlightElment(List<WebElement> elements ,String colr) {
        for (WebElement s :elements){
            BasePage.executor.executeScript("s = arguments[0];"+
                    "original_style=s.getAttribute('style');"+
                    "s.setAttribute('style',original_style+\";"+
                    "background:;border:4px solid "+colr+";\")",s);
        }
    }

    /**
     * 单个把元素添加样式，类似高亮（border：red）
     * @param element
     */
    public static void highlightElment(WebElement element,String colr) {
        BasePage.executor.executeScript("s = arguments[0];"+
                "original_style=s.getAttribute('style');"+
                "s.setAttribute('style',original_style+\";"+
                "background:;border:4px solid "+colr+";\")",element);
    }

    /**
     * 传打开浏览器/click 之前所有句柄
     * @param handles
     * @return click 后产生的新窗口句柄
     */
    public static String getNewWindow(Set<String> handles){
        Set<String> this_handles =BasePage.driver.getWindowHandles();
        boolean isin=false;
        for (String hand :this_handles){
            isin=false;
            for (String ori_hand :handles){
                if (hand.equals(ori_hand)){
                    isin=true;
                }
            }
            if (!isin)
                return hand;
        }
        return  null;
    }



}