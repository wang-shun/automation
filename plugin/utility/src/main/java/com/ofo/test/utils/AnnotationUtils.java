package com.ofo.test.utils;



import java.lang.reflect.Method;
import java.util.*;


import com.ofo.test.api.model.MenuDescriptor;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

/**
 * Created by Administrator on 2015/10/22.
 */
public class AnnotationUtils {


    public synchronized static List<MenuDescriptor> getDescriptors(String classpaths,String className, Class annotationClass, boolean paramType) throws Exception {
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
            Class cc2 = Class.forName(classpaths);
            CtClass cc = pool.get(className);
            CtMethod[] methods = cc.getMethods();
            for (CtMethod method : methods) {
                Object annotation = method.getAnnotation(annotationClass);
                if (null != annotation) {
                    MethodInfo methodInfo = method.getMethodInfo();
                    CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                    LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
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
                        if (paramType && method.getParameterTypes()[i].getName().startsWith("com.gome.test.gui"))
                            params.add(method.getParameterTypes()[i].getSimpleName());
                        else
                            params.add(attr.variableName(i + offset));
                    }
                    Method m = annotation.getClass().getMethod("description", new Class<?>[0]);
                    String description = m.invoke(annotation, new Object[0]).toString();
                    descriptors.add(new MenuDescriptor(description, method.getName(), annotationClass.getSimpleName(), params));
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
