package com.gome.test.mock.groovy;

import groovy.lang.GroovyObject;

public class GroovyObj {
    private static GroovyObject groovyObject = null;

    public static GroovyObject getGroovyObject() {
        return groovyObject;
    }

    public static void setGroovyObject(GroovyObject groovyObject) {
        GroovyObj.groovyObject = groovyObject;
    }

}
