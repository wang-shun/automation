package com.gome.test.mock.javagroovy.scripts

import com.gome.test.mock.javagroovy.interfaces.GroovyInterface

class Groovy_1 extends GroovyInterface{

    Groovy_1(def name, def age, def dep, def address){
        this.name = name;
        this.age = age;
        this.dep = dep;
        this.address = address;
    }

    Groovy_1(){}



    static String hello(className) {
        println "Groovy_1: Hello12, " + className;
    }

    String toString() {
        this.class.name
    }

    @Override
    static void sayHello(args) {
        println "invoke sayHello()21 in Groovy_1 $args";
    }

    Groovy_1 getGroovy_1(args) {
        return this;
    }

    static void main(args) {
        Groovy_1 groovy1 = new Groovy_1();
        groovy1.hello("Groovy_1");
        groovy1.sayHello();
    }
}