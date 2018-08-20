package com.gome.test.order.process.util;

import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.com.gome.test.buess.Login.LmisLogin;
import com.gome.test.order.process.com.gome.test.buess.Login.Login;
import com.gome.test.utils.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by liangwei-ds on 2017/3/17.
 */
public class GUI {

    @Test
    public static void guiLogin(){
        Login login = new Login();
        login.doLogin("", "");
        WebDriver driver=new FirefoxDriver();
        driver.navigate().to("http://erm.atguat.com.cn/main.action");
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        WebElement name = driver.findElement(By.id("userName"));
        WebElement pwd = driver.findElement(By.id("passWord"));
        WebElement loginbut =driver.findElement(By.id("btnSub"));
        name.sendKeys("ermadmin");
        pwd.sendKeys("aAbb2e33");
        loginbut.click();

        Cookie cookie = driver.manage().getCookieNamed("_erm_sso");
        String _erm_sso = cookie.getName()+"="+cookie.getValue()+";";
        Logger.info(String.format("_erm_sso的cookie是：%s",_erm_sso));
        ContextUtils.getContext().put("Set-Cookie",_erm_sso);
        driver.quit();

    }

    public static void guiLmisLogin(){
//        LmisLogin lmisLogin = new LmisLogin();
//        lmisLogin.doLogin("fengjiangwei","123123");
        WebDriver driver=new FirefoxDriver();
        driver.navigate().to("http://10.128.11.135/gome-lmis-login/index.do");
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        WebElement name = driver.findElement(By.id("username"));
        WebElement pwd = driver.findElement(By.id("password"));
        WebElement loginbut =driver.findElement(By.xpath("//input[@alt='Submit']"));
        name.sendKeys("fengjiangwei");
        pwd.sendKeys("123123");
        loginbut.click();

        WebElement loginSession =driver.findElement(By.id("Login_SessionId"));
        String loginsess = loginSession.getAttribute("value");
        Logger.info(String.format("Login_SessionId是：%s",loginsess));
        ContextUtils.getContext().put("sessionId",loginsess);
        WebElement subsys =driver.findElement(By.id("btn_tms"));
        subsys.click();
        driver.quit();

    }
}
