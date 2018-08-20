package com.gome.test.mock.reflex;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gome.test.mock.Computer;
import com.gome.test.mock.common.Command;

public class Reflection {
    //获取反射对象
    private Computer computer = null;
    public Class cls = null;

    public Reflection(Computer computer) {
        this.computer = computer;
        this.cls = computer.getClass();
    }

    //获取方法列表
    @SuppressWarnings({ "rawtypes", "unused" })
    public List<Method> getMethods() {
        Class cls = this.computer.getClass();
        try {
            Object obj = cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        };
        Method[] methods = cls.getMethods();
        return Arrays.asList(methods);
    }

    //获取方法名称列表
    public List<String> getMethodNames() {
        List<String> methodNames = new ArrayList<String>();
        List<Method> methodList = this.getMethods();
        for (Method mthd : methodList) {
            methodNames.add(mthd.getName());
        }
        return methodNames;
    }

    //获参数类型数组
    @SuppressWarnings("rawtypes")
    public Class[] getClasses(List<String> params) {
        if (params == null || params.size() == 0) {
            return null;
        }
        Class[] clss = new Class[params.size()];
        for (int i = 0; i < params.size(); i++) {
            clss[i] = String.class;
        }
        return clss;
    }

    //获取入参数组
    public String[] paramsToArray(List<String> params, Map<String, Object> resMap, String prefix) {
        if (params == null || params.size() == 0) {
            return null;
        }
        String regEx = "(@Steps-[0-9]*)";
        String[] strArr = new String[params.size()];
        for (int i = 0; i < params.size(); i++) {

            String param = params.get(i);
            Pattern pat = Pattern.compile(regEx);
            Matcher mat = pat.matcher(param);
            List<String> steps = new ArrayList<String>();
            while (mat.find()) {
                steps.add(mat.group());
            }
            for (String str : steps) {
                param = param.replace(str, resMap.get(str).toString());
            }
            strArr[i] = param;
        }

        return strArr;
    }

    //获取入参数组
    public Object[] paramsToObjArray(List<String> params, Map<String, Object> resMap, String prefix) {
        if (params == null || params.size() == 0) {
            return null;
        }
        Object[] strArr = new Object[params.size()];
        String regEx = "(@Steps-[0-9]*)";
        for (int i = 0; i < params.size(); i++) {
            String param = params.get(i);
            Pattern pat = Pattern.compile(regEx);
            Matcher mat = pat.matcher(param);
            List<String> steps = new ArrayList<String>();
            while (mat.find()) {
                steps.add(mat.group());
            }
            for (String str : steps) {
                param = param.replace(str, resMap.get(params.get(i)).toString());
            }
            strArr[i] = param;
        }

        return strArr;
    }

    public static void main(String[] args) throws Exception {
        Computer com = new Command();
        Reflection ref = new Reflection(com);

        for (int i = 0; i < ref.getMethodNames().size(); i++) {
            //System.out.println(ref.getMethodNames().get(i));
        }
        /* String str1 = "abc";
         Class cls1 = str1.getClass();
         Class cls2 = String.class;
         Class cls3 = Class.forName("java.lang.String");
         System.out.println(Class.forName("java.lang.String"));
         System.out.println(str1.getClass());
         */
        String regEx = "(@Steps-[0-9]*)";
        String s = "sadfasdf @Steps-1 sdfsdf @Steps-2 asdfsdf";
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(s);
        //boolean rs = mat.find();
        while (mat.find()) {
            System.out.println(mat.group());
        }
    }
}
