package com.ofo.test.gui.helper;

import com.gome.test.context.ContextUtils;
import com.gome.test.utils.ReflectionUtils;
import com.gome.test.utils.StringUtils;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class Settings {

    private static final String SELENIUM_BROWSER = "selenium.browser";
    private static final String SELENIUM_IMPLICIT_WAIT = "selenium.implicit.wait";
    private static final String SELENIUM_PAGE_WAIT = "selenium.page.wait";
    private static final String SELENIUM_SCRIPT_WAIT = "selenium.script.wait";
    private static final String SELENIUM_ISPRINT = "selenium.isprint";
    public static final String SELENIUM_PROPERTIES = "selenium.properties";

    private BrowserType browser = BrowserType.FIREFOX;
    public static int implicitWait = 50;
    public static int pageWait = 50;
    public static int scriptWait = 50;
    public static boolean isPrint = false;

    private Properties properties = new Properties();
    private String configFilePath;

    public Settings(String configFilePath) {
        loadSettings(configFilePath);
    }

    public Settings() {
        loadSettings(SELENIUM_PROPERTIES);
    }

    private void loadSettings(String configFilePath) {
        this.configFilePath = configFilePath;
        this.properties = loadPropertiesFile();


        this.browser = BrowserType.Browser(getPropertyOrThrowException(SELENIUM_BROWSER));

        System.out.println(this.browser);

        ContextUtils.addToContext(SELENIUM_BROWSER, getPropertyOrThrowException(SELENIUM_BROWSER));

        this.implicitWait = Integer.valueOf(getPropertyOrThrowException(SELENIUM_IMPLICIT_WAIT));
        System.out.println(this.implicitWait);

        this.pageWait = Integer.valueOf(getPropertyOrThrowException(SELENIUM_PAGE_WAIT));
        System.out.println(this.pageWait);

        this.scriptWait = Integer.valueOf(getPropertyOrThrowException(SELENIUM_SCRIPT_WAIT));
        System.out.println(this.scriptWait);

        this.isPrint = Boolean.valueOf(getPropertyOrThrowException(SELENIUM_ISPRINT));
        this.isPrint = this.browser == BrowserType.HTMLUNIT ? false : this.isPrint;

        System.out.println(this.isPrint);

    }

    private Properties loadPropertiesFile() {
        try {
//            // get specified property file
//            String filename = getPropertyOrNull(configFilePath);
//            // it is not defined, use default
//            if (filename == null) {
//                filename = configFilePath;
//            }
//            // try to load from classpath
            InputStream stream = getClass().getClassLoader().getResourceAsStream(configFilePath);
            // no file in classpath, look on disk
            if (stream == null) {
                stream = new FileInputStream(new File(configFilePath));
            }
            Properties result = new Properties();
            result.load(stream);
            return result;
        } catch (IOException e) {
            throw new UnknownPropertyException("Property file is not found");
        }
    }

    public String getPropertyOrNull(String name) {
        return getProperty(name, false);
    }

    public String getPropertyOrThrowException(String name) {
        return getProperty(name, false);
    }

    private String getProperty(String name, boolean forceExceptionIfNotDefined) {
        String result;
        if ((result = System.getProperty(name, null)) != null && result.length() > 0) {
            return result;
        } else if ((result = getPropertyFromPropertiesFile(name)) != null && result.length() > 0) {
            return result;
        } else if (forceExceptionIfNotDefined) {
            throw new UnknownPropertyException("Unknown property: [" + name + "]");
        }
        return result;
    }

    private String getPropertyFromPropertiesFile(String name) {
        Object result = properties.get(name);
        if (result == null) {
            return null;
        } else {
            return result.toString();
        }
    }

    public WebDriver getDriver() throws Exception {
        WebDriver driver = getDriver(browser);
        driver.manage().timeouts().implicitlyWait(this.implicitWait, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(this.pageWait, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(this.scriptWait, TimeUnit.SECONDS);
        return driver;
    }


    private void cleanIpadService() throws Exception {

        //杀appium服务
//        Runtime.getRuntime().exec("lsof -i :4723 | awk 'NR>1{print$2}' |xargs kill -9");
        //启动appium服务
        Process p = Runtime.getRuntime().exec("lsof -i :4723 ");
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        if(br.readLine() == null) {
            Runtime.getRuntime().exec("appium");
        }
        //等待10秒启动服务
        Thread.sleep(15000);

    }

    private WebDriver getDriver(BrowserType browserType) throws Exception {
        String path;
        File file;
        switch (browserType) {
            case IPAD_SAFARI:

                cleanIpadService();

                DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
                desiredCapabilities.setCapability("deviceName", "iPad Retina");

                //                desiredCapabilities.setCapability("platformVersion","9.2");
//                desiredCapabilities.setCapability("autoAcceptAlerts","true");
//                desiredCapabilities.setCapability("safariInitialUrl","http://127.0.0.1");
//                desiredCapabilities.setCapability("safariIgnoreFraudWarning","true");

                desiredCapabilities.setCapability("platformName", "iOS");
                desiredCapabilities.setCapability("browserName", "Safari");
                desiredCapabilities.setCapability("autoLaunch", "false");
                desiredCapabilities.setCapability("newCommandTimeout", 180);
                desiredCapabilities.setCapability("safariAllowPopups", "true");
                desiredCapabilities.setCapability("safariIgnoreFraudWarning", "true");
                desiredCapabilities.setCapability("safariOpenLinksInBackground", "true");
                desiredCapabilities.setCapability("screenshotWaitTimeout", 10);
                desiredCapabilities.setCapability("nativeInstrumentsLib", "true");
                desiredCapabilities.setCapability("keepKeyChains", "false");
                desiredCapabilities.setCapability("interKeyDelay", 1000);

                WebDriver driver = new AppiumDriver(new URL("http://127.0.0.1:4723/wd/hub"), desiredCapabilities);
                return driver;

            case SAFARI:
                DesiredCapabilities desiredCapabilitiesForMac = new DesiredCapabilities();
                desiredCapabilitiesForMac.setPlatform(org.openqa.selenium.Platform.extractFromSysProperty("Mac"));

//                SafariOptions options = new SafariOptions();
//                options.setUseCleanSession(true); //if you wish safari to forget session everytime
//                options.setDriverExtension(new File("/Users/zhangjiadi/library/safari/extensions"));
//                options.setSkipExtensionInstallation(false);
//                options.addExtensions(new File("/Users/zhangjiadi/library/safari/extensions/WebDriver.safariextz"));

//                System.setProperty("webdriver.safari.noinstall", "false");
//                DesiredCapabilities capability = DesiredCapabilities.safari();
//                capability.setBrowserName("safari");
//                capability.setPlatform(org.openqa.selenium.Platform.extractFromSysProperty("Mac"));
//                capability.setVersion("2.45.0");
//                System.setProperty("webdriver.safari.driver", "/Users/zhangjiadi/library/safari/extensions/WebDriver.safariextz");

                WebDriver driver2 = new SafariDriver(desiredCapabilitiesForMac);

                return driver2;

            case FIREFOX:
                return new FirefoxDriver();
            case EDGE:
                return new EdgeDriver();

            case IE:
                if (getClass().getResource("/") == null) {
                    path = String.format("%s%starget%s%s", System.getProperty("base.dir"), File.separator, File.separator, "IEDriverServer.exe");

                    file = new File(path);
                    if (file.exists() == false)
                        copyDriverFromJar("IEDriverServer.exe", path);
                } else {
                    path = String.format("%s%s%s", getClass().getResource("/").getPath(), File.separator, "IEDriverServer.exe");

                    file = new File(path);
                    if (file.exists() == false)
                        FileUtils.copyInputStreamToFile(new ClassPathResource("IEDriverServer.exe").getInputStream(), new File(path));
                }
                DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
                ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
                                true);
                System.setProperty(InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY, path);
                return new InternetExplorerDriver(ieCapabilities);
            case CHROME:
                if (getClass().getResource("/") == null) {
                    String osName = System.getProperty("os.name").toLowerCase();
                    String chromePath = osName.startsWith("windows") ? "chromedriver.exe" : "chromedriver";
                    path = String.format("%s%starget%s%s", System.getProperty("base.dir"), File.separator, File.separator, chromePath);

                    file = new File(path);
                    if (file.exists() == false)
                        copyDriverFromJar(chromePath, path);
                } else {
                    String osName = System.getProperty("os.name").toLowerCase();
                    String chromePath = osName.startsWith("windows") ? "chromedriver.exe" : "chromedriver";
                    path = String.format("%s%s%s", getClass().getResource("/").getPath(), File.separator, chromePath);

                    file = new File(path);
                    if (file.exists() == false)
                        FileUtils.copyInputStreamToFile(new ClassPathResource(chromePath).getInputStream(), new File(path));
                }

                if (!System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                    try {
                        Runtime.getRuntime().exec(String.format("chmod 777 %s", path));
                    } catch (Exception ex) {
                    }
                }
                System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, path);
                return new ChromeDriver();
//            case OPERA:
//                return new OperaDriver();
            case HTMLUNIT:
                HtmlUnitDriver driver1 = new HtmlUnitDriver();
                DesiredCapabilities.htmlUnitWithJs();
                driver1.setJavascriptEnabled(true);
                return driver1;
            default:
                throw new UnknownBrowserException("Cannot create driver for unknown browser type");
        }
    }

    public BrowserType getBrowser() {
        return browser;
    }


    private void copyDriverFromJar(String driverPath, String copytoPath) throws Exception {
        ReflectionUtils.copyJarResource(getJarFullPath(), driverPath, copytoPath);
    }

    private String getJarFullPath() throws Exception {
        StringBuffer strB = new StringBuffer();
        strB.append(System.getProperty("settings.localRepository"));
        strB.append(File.separatorChar);
        strB.append(("com.gome.test.gui").replace(".", File.separator)).append(File.separator);
        strB.append("gui-common-helper").append(File.separator);

        String version = getVersion(strB.toString());
        strB.append(version).append(File.separator);
        strB.append("gui-common-helper").append("-").append(version).append(".jar");
        return strB.toString();
    }

    private String getVersion(String path) throws Exception {
        File jarDir = new File(path);
        if (jarDir.exists() == false)
            throw new IOException(String.format("%s 不存在", jarDir.getAbsolutePath()));

        List<String> list = new ArrayList<String>();
        for (File f : jarDir.listFiles()) {
            if (f.isDirectory())
                list.add(f.getName());
        }

        if (list.size() == 0)
            throw new IOException(String.format("%s 内无文件夹", jarDir.getAbsolutePath()));

        Collections.sort(list, Collator.getInstance(java.util.Locale.US));
        return list.get(list.size() - 1);
    }
}
