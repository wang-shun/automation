package com.gome.test.mock.javagroovy.invoker;

import com.gome.test.mock.javagroovy.scripts.Groovy_1;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
/**
 * Created by lizonglin on 2015/9/22/0022.
 */
public class TestService {


    /**
     * 1、使用ClassLoader加载groovy脚本的class，并调用方法（动态调用）
     *
     * @throws java.io.IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void service_1() throws IOException, IllegalAccessException, InstantiationException {
        ClassLoader parent = TestService.class.getClassLoader();

        GroovyClassLoader loader = new GroovyClassLoader(parent);

        Class groovyClass = loader.parseClass(new File("D:\\svn\\SVNCode\\Doraemon\\MOCK\\Trunk\\Sample\\JavaGroovyDemo\\java-groovy-scripts\\src\\main\\java\\com\\gome\\test\\mock\\javagroovy\\scripts\\Groovy_1.groovy"));

        GroovyObject groovyObject = (GroovyObject)groovyClass.newInstance();

        groovyObject.setProperty("name", "lzl");
        groovyObject.setProperty("age", 27);
        groovyObject.setProperty("dep", "sanyuanqiao");
        groovyObject.setProperty("address", new String[] {"1","2","3"});

        Object[] fromClass = {this.getClass().getSimpleName()};
        System.out.println("service_1() in TestService.java invoke sayHello() in Groovy_1.groovy: ");
        GroovyObject groovy_1 = (GroovyObject)groovyObject.invokeMethod("getGroovy_1", fromClass);
        System.out.println("The return groovy_1 object: ");
        System.out.println("name: "+groovy_1.getProperty("name"));
        System.out.println("age: "+groovy_1.getProperty("age"));
        System.out.println("dep: "+groovy_1.getProperty("dep"));
        System.out.println("address: "+groovy_1.getProperty("address"));
    }

    /**
     * 2、通过加载脚本，直接调用脚本中的方法（动态调用）
     */
    public void service_2() throws IOException {
        String[] args = {};
        GroovyShell shell = new GroovyShell();
        System.out.println("service_2() in TestService.java run main() in Groovy_1.groovy: ");
        shell.run(new File("D:\\svn\\SVNCode\\Doraemon\\MOCK\\Trunk\\Sample\\JavaGroovyDemo\\java-groovy-scripts\\src\\main\\java\\com\\gome\\test\\mock\\javagroovy\\scripts\\Groovy_1.groovy"),args);
    }

    /**
     * 3、通过在Java方法中直接调用Groovy类的静态方法（不支持动态调用）
     */
    public void service_3() {
        System.out.println("service_3() in TestService.java invoke static hello() in Groovy_1.groovy: ");
        Groovy_1.hello(this.getClass().getSimpleName());
    }

    /**
     * 4、（不支持动态调用）
     */
    public void service_4() {
        GroovyObject instance = new Groovy_1("lzl",27,"sanyuanqiao",new String[] {"1","2","3"});
        instance.invokeMethod("hello",new Object[] {this.getClass().getSimpleName()});
    }

}
