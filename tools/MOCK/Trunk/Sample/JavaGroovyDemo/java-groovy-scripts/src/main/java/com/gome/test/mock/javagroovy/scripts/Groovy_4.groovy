package com.gome.test.mock.javagroovy.scripts

/**
 * Created by lizonglin on 2015/10/8/0008.
 */
class Groovy_4 implements GroovyInterceptable{
    def hello(String name) {
        return "Directly invoke hello() by $name";
    }

//    def methodMissing(String name, args) {
//        return  "You called $name with arguments: ${args.join(', ')}";
//    }

    def invokeMethod(String name, args) {
        return  "You called $name with arguments: ${args.join(', ')}";
    }

    static void main(args) {
        Groovy_4 groovy4 = new Groovy_4();
        println groovy4.hello();
        println groovy4.foo("main",11,14);
    }
}
