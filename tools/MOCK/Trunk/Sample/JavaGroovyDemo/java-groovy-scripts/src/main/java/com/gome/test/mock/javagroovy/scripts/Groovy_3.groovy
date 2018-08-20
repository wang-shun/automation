package com.gome.test.mock.javagroovy.scripts
import com.gome.test.mock.javagroovy.interfaces.TestClass
/**
 * Created by lizonglin on 2015/10/8/0008.
 * Groovy类继承Java类
 */
class Groovy_3 extends TestClass{
    @Override
    void service_2() {
        println "Overrided service_2() in Groovy_3 extends TestClass:";
    }

    static void main(args) {
        Groovy_3 groovy3 = new Groovy_3();
        println("invoke overrided service_2() in Groovy_3 extends TestService:");
        groovy3.service_2();
    }
}
