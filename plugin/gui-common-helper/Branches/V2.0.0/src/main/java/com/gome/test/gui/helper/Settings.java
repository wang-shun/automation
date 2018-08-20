package com.gome.test.gui.helper;

import com.gome.test.utils.Logger;
import com.gome.test.utils.ReflectionUtils;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class Settings {

    private static final String SELENIUM_BROWSER = "selenium.browser";
    private static final String SELENIUM_IMPLICIT_WAIT = "selenium.implicit.wait";
    private static final String SELENIUM_PAGE_WAIT = "selenium.page.wait";
    private static final String SELENIUM_SCRIPT_WAIT = "selenium.script.wait";
    public static final String SELENIUM_PROPERTIES = "selenium.properties";

    private BrowserType browser = BrowserType.FIREFOX;
    private int implicitWait = 30;
    private int pageWait = 30;
    private int scriptWait = 30;

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
        this.implicitWait = Integer.valueOf(getPropertyOrThrowException(SELENIUM_IMPLICIT_WAIT));
        this.pageWait = Integer.valueOf(getPropertyOrThrowException(SELENIUM_PAGE_WAIT));
        this.scriptWait = Integer.valueOf(getPropertyOrThrowException(SELENIUM_SCRIPT_WAIT));
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

    private WebDriver getDriver(BrowserType browserType) throws Exception {
        String path;
        File file;
        switch (browserType) {
            case FIREFOX:
                return new FirefoxDriver();
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

                System.setProperty(InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY, path);
                return new InternetExplorerDriver();
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
                    }catch (Exception ex){}
                }
                System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, path);
                return new ChromeDriver();
//            case OPERA:
//                return new OperaDriver();
            case HTMLUNIT:
                return new HtmlUnitDriver();
            default:
                throw new UnknownBrowserException("Cannot create driver for unknown browser type");
        }
    }

    public BrowserType getBrowser() {
        return browser;
    }


    private void copyDriverFromJar(String driverPath, String copytoPath) throws Exception {
        ReflectionUtils.copyJarResource(getJarFullPath(),driverPath,copytoPath);
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
