package com.gome.test.mock.javagroovy.scripts
import com.gome.test.mock.javagroovy.interfaces.TestInterface
/**
 * Created by lizonglin on 2015/10/8/0008.
 * Groovy类实现Java接口
 */
class Groovy_2 implements TestInterface{
    @Override
    void sayName(String name) {
        println "Hello, $name";
    }

//    def foo() {
//        TestService testService = new TestService();
//        testService.service_1();
//        testService.service_2();
//    }

    static void main(args) {
        Groovy_2 invokeJava = new Groovy_2();
        invokeJava.sayName("InvokeJava");
//        invokeJava.foo();
    }
}
