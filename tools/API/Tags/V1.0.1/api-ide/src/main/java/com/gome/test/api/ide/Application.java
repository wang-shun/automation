package com.gome.test.api.ide;

import com.gome.test.api.utils.DBUtils;

import java.util.concurrent.CountDownLatch;

import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableAutoConfiguration
@ImportResource("classpath*:applicationContext.xml")
public class Application implements EmbeddedServletContainerCustomizer {

    public static void main(String[] args) {
        Application app = new Application();
        try {
            app.run(args);
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
        }
    }

    public void run(String[] args) throws Exception {
        AnsiConsole.systemInstall();
        Environment env = new Environment();
        env.initLoad();
        CountDownLatch latch = new CountDownLatch(1);
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        EmbeddedWebApplicationContext embedbedContext = (EmbeddedWebApplicationContext) context;
        System.out.println(String.format(INFO, embedbedContext.getEmbeddedServletContainer().getPort()));
        String urlString = String.format(URLSTRING, embedbedContext.getEmbeddedServletContainer().getPort());
        openBrowser(urlString);
        ContextClosedHandler handler = context.getBean(ContextClosedHandler.class);
        handler.setCountDownLatch(latch);
        String registerSvnUrlSQL = context.getBean("registerSvnUrlSQL", String.class);
        String sql = String.format(registerSvnUrlSQL, env.getSvnUrl());
//        BasicDataSource dataSource = context.getBean("dataSource", BasicDataSource.class);
//        DBUtils.updateDB(dataSource, sql);
        try {
            latch.await();
        } catch (InterruptedException ex) {
            LOG.warn("Exception:", ex);
        }
    }

    public void customize(ConfigurableEmbeddedServletContainer container) {
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        mappings.add("html", "text/html;charset=utf-8");
        container.setMimeMappings(mappings);
        container.setPort(0);
    }

    //function for open browser auto added by zonglin.li
    public static void openBrowser(String url) {
        String osName = System.getProperty("os.name");
        try {
            System.out.println(osName);
            if (osName.startsWith("Mac")) {
                //Mac OS
                Runtime.getRuntime().exec("open " + url);
            } else if (osName.startsWith("Windows")) {
                //Windows  
                Runtime.getRuntime().exec(
                        "rundll32 url.dll,FileProtocolHandler " + url);
            } else {
                //assume Unix or Linux  
                String[] browsers = {"firefox", "opera", "konqueror",
                        "epiphany", "mozilla", "netscape"};
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime().exec(
                            new String[]{"which", browsers[count]})
                            .waitFor() == 0) {
                        browser = browsers[count];
                    }
                }
                if (browser != null) {
                    Runtime.getRuntime().exec(new String[]{browser, url});
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static final String INFO = "\033[32m服务已启动，请打开浏览器，输入http://127.0.0.1:%d访问\033[0m";
    private static final String URLSTRING = "http://127.0.0.1:%d";
    private static final Logger LOG = LoggerFactory.getLogger(
            Application.class);
}
