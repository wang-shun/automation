package com.gome.test.mock.reflex;

import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import com.gome.test.mock.groovy.GroovyObjLoader;
import com.gome.test.mock.utils.Logger;

public class ReflexObject {
    private static final Logger logger = new Logger(ReflexObject.class);

    public static Map<String, Object> reflexObjMap;
    public final static String DEFAULT_OBJ = "default";
    public final static String GROOVY_FILE_PATH = "D:\\mock_file\\groovy";

    static {
        reflexObjMap = new HashMap<String, Object>();
        Object obj = null;
        try {
            obj = Class.forName("com.gome.test.mock.common.Command").newInstance();
            if (obj != null) {
                reflexObjMap.put(DEFAULT_OBJ, obj);
            } else {
                logger.error("反射加载默认commond失败！");
            }
        } catch (InstantiationException e) {
            logger.error("反射加载默认commond报错:", e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error("反射加载默认commond报错:", e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error("反射加载默认commond报错:", e.getMessage());
        }
        try {
            GroovyObjLoader.beforeLoad(GROOVY_FILE_PATH, reflexObjMap);
        } catch (CompilationFailedException e) {
            logger.error("反射加载GroovyObject报错:", e.getMessage());
        } catch (InstantiationException e) {
            logger.error("反射加载GroovyObject报错:", e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error("反射加载GroovyObject报错:", e.getMessage());
        } catch (IOException e) {
            logger.error("反射加载GroovyObject报错:", e.getMessage());
        } catch (ResourceException e) {
            logger.error("反射加载GroovyObject报错:", e.getMessage());
        } catch (ScriptException e) {
            logger.error("反射加载GroovyObject报错:", e.getMessage());
        }
    }
}
