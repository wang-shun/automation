package com.gome.test.api.ide.bo;

import com.gome.test.api.ide.model.MenuDescriptor;
import com.gome.test.api.annotation.SetUp;
import com.gome.test.api.annotation.TearDown;
import com.gome.test.api.annotation.Verify;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.springframework.beans.factory.annotation.Value;

public class MenuBo {

    @Value(value = "${app.classpath}")
    private String classpaths;

    public List<MenuDescriptor> getSetUpMenu(String setUpClass) throws Exception {
        List<MenuDescriptor> setUpDescriptors = getDescriptors(setUpClass, SetUp.class);
        Collections.sort(setUpDescriptors);
        return setUpDescriptors;
    }

    public List<MenuDescriptor> getVerifyMenu(String verifyClass) throws Exception {
        List<MenuDescriptor> verifyDescriptors = getDescriptors(verifyClass, Verify.class);
        Collections.sort(verifyDescriptors);
        return verifyDescriptors;
    }

    public List<MenuDescriptor> getTearDownMenu(String tearDownClass) throws Exception {
        List<MenuDescriptor> tearDownDescriptors = getDescriptors(tearDownClass, TearDown.class);
        Collections.sort(tearDownDescriptors);
        return tearDownDescriptors;
    }

    private synchronized List<MenuDescriptor> getDescriptors(String className,
                                                             Class annotationClass) throws Exception {
        List<MenuDescriptor> descriptors = new ArrayList<MenuDescriptor>();
        ClassPool pool = new ClassPool();
        pool.appendSystemPath();
        List<ClassPath> cps = new ArrayList<ClassPath>();
        for (String classPath : classpaths.split(";")) {
            if (!classPath.isEmpty()) {
                ClassPath cp = pool.insertClassPath(classPath);
                cps.add(cp);
            }
        }
        try {
            CtClass cc = pool.get(className);
            CtMethod[] methods = cc.getMethods();
            for (CtMethod method : methods) {
                Object annotation = method.getAnnotation(annotationClass);
                if (null != annotation) {
                    MethodInfo methodInfo = method.getMethodInfo();
                    CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                    LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                            .getAttribute(LocalVariableAttribute.tag);
                    List<String> params = new ArrayList<String>();
                    int offset = 0;
                    for (int i = 0; i < attr.length(); ++i) {
                        if (attr.variableName(i).equals("this")) {
                            offset = i;
                            break;
                        }
                    }
                    ++offset;
                    for (int i = 0; i < method.getParameterTypes().length; ++i) {
                        params.add(attr.variableName(i + offset));
                    }
                    Method m = annotation.getClass().getMethod("description", new Class<?>[0]);
                    String description = m.invoke(annotation, new Object[0]).toString();
                    descriptors.add(new MenuDescriptor(description,
                            method.getName(), params));
                }
            }
        } finally {
            for (ClassPath cp : cps) {
                pool.removeClassPath(cp);
            }
        }
        return descriptors;
    }
}
