package com.gome.test.gui.helper;


import com.gome.test.utils.ConfigurationUtils;
import com.gome.test.utils.Logger;
import org.apache.commons.configuration.CompositeConfiguration;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BasePage {
    public static WebDriver driver;
    public static JavascriptExecutor executor;
    public static GuiHelper helper;
    private static Settings settings;
    public static final String EMPTY_PAGE = "about:blank";
    public static final Map<String, String> context = new HashMap<String, String>();
    public static final String START_BROWSE_ID = "START_BROWSE_ID";
    private static final String GUI_XML = "/gui.xml";
    private static final String GUI_PROP = "/gui.properties";
    private static final String PAGE_CONATINER = "pageContainer";
    /**
     * 系统配置，gui.xml + gui.properties
     */
    public static CompositeConfiguration compositeConfiguration = new CompositeConfiguration();

    public static void initDriver() throws Exception {
        settings = new Settings();
        init(GUI_XML, GUI_PROP);
    }

    public static void initDriver(String configFilePath) throws Exception {
        settings = new Settings(configFilePath);
        File resources = new File(configFilePath).getParentFile();
        String xml = new File(resources, GUI_XML).getAbsolutePath();
        String prop = new File(resources, GUI_PROP).getAbsolutePath();
        init(xml, prop);
    }

    private static void init(String xml, String prop) throws Exception {
        initBrowser();
        ConfigurationUtils.readConfig(compositeConfiguration, xml, prop);
    }

    public static void closeAllCaseWindows() {
        Set<String> handles = driver.getWindowHandles();
        if (context.containsKey(START_BROWSE_ID)) {
            String startId = context.get(START_BROWSE_ID);
            for (String s : handles) {
                if (startId.equals(s) == false) {
                    driver.switchTo().window(s);
                    driver.close();
                }
            }
        }
    }

    public static void initBrowser() throws Exception {
        BasePage.driver = settings.getDriver();
        BasePage.driver.get(EMPTY_PAGE);
        context.put(START_BROWSE_ID, BasePage.driver.getWindowHandle());
    }

    public static void openAndSwitchtoCaseWindow() throws Exception {
        try {
            driver.switchTo().window(context.get(BasePage.START_BROWSE_ID));
        } catch (Exception ex) {
            Logger.info("原始窗口已关闭,即将重启浏览器");
            initBrowser();
        }
        initPage(BasePage.class);
        executor.executeScript(
                String.format("var tmp = window.open('%s', '%s', 'toolbar=yes,menubar=yes,scrollbars=yes, resizable=yes,location=yes,fullscreen=0');tmp.moveTo(0, 0);tmp.resizeTo(screen.width + 20, screen.height);tmp.focus();",
                        EMPTY_PAGE, PAGE_CONATINER, EMPTY_PAGE));
        driver.switchTo().window(PAGE_CONATINER);
    }

    public static <T extends BasePage> T initPage(Class<T> pageClass) {
        executor = (JavascriptExecutor) driver;
        helper = new GuiHelper(driver);
        T page = instantiatePage(driver, pageClass);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 30), page);
        return page;
    }

    public static <T extends BasePage> T initPage(SearchContext searchContext, Class<T> pageClass) {
        T page = instantiatePage(searchContext, pageClass);
        PageFactory.initElements(new AjaxElementLocatorFactory(searchContext, 30), page);
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
}