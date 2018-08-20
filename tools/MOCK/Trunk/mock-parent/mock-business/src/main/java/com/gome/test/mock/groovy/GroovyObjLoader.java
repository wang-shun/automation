package com.gome.test.mock.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import com.gome.test.mock.Computer;
import com.gome.test.mock.utils.LoadUtils;
import com.gome.test.mock.utils.Logger;

public class GroovyObjLoader {
    private static final Logger logger = new Logger(GroovyObjLoader.class);

    //后缀标识
    private final static String SUFFIX = ".groovy";

    private static Map<String, Integer> errMap = new HashMap<String, Integer>();

    //递归获取文件夹下所有文件
    public static Map<String, List<String>> tree(String filePath, String suffix, Map<String, List<String>> fileMap) {
        List<String> fileList = null;
        File file = new File(filePath);
        //判断传入对象是否为一个文件夹对象
        if (!file.isDirectory()) {
            logger.info("你输入的不是一个文件夹，请检查路径是否有误！");
        } else {
            File[] t = file.listFiles();
            fileList = new ArrayList<String>();
            for (int i = 0; i < t.length; i++) {
                //判断文件列表中的对象是否为文件夹对象，如果是则执行tree递归，直到把此文件夹中所有文件输出为止
                if (t[i].isDirectory()) {
                    tree(t[i].getPath(), suffix, fileMap);
                } else {
                    if (t[i].getPath().toLowerCase().endsWith(suffix)) {
                        fileList.add(t[i].getPath());
                    }
                }
            }
            if (fileList != null && fileList.size() > 0) {
                fileMap.put(file.getPath(), fileList);
            }
        }

        return fileMap;
    }

    //递归获取文件夹所有groovy文件
    public static void queryGroovys(String filePath, List<String> groovyFiles) {
        File file = new File(filePath);
        //判断传入对象是否为一个文件夹对象
        if (!file.isDirectory()) {
            logger.info("你输入的不是一个文件夹，请检查路径是否有误！");
        } else {
            File[] t = file.listFiles();
            for (int i = 0; i < t.length; i++) {
                //判断文件列表中的对象是否为文件夹对象，如果是则执行tree递归，直到把此文件夹中所有文件输出为止
                if (t[i].isDirectory()) {
                    queryGroovys(t[i].getPath(), groovyFiles);
                } else {
                    if (t[i].getPath().endsWith(SUFFIX)) {
                        groovyFiles.add(t[i].getPath());
                    }
                }
            }
        }

    }

    //ClassLoader递归加载groovy
    public static void loadGroovys(ClassLoader parent, GroovyClassLoader loader, List<String> groovyFiles, Map<String, Object> resMap) throws InstantiationException, IllegalAccessException {
        String temp = "";
        if (groovyFiles != null && groovyFiles.size() > 0) {
            temp = groovyFiles.get(0);
            Class<?> groovyClass = null;
            try {
                groovyClass = loader.parseClass(new File(temp));
                logger.info("加载" + temp + "文件成功！");
            } catch (Exception e) {
                logger.info("加载" + temp + "文件失败:", e.getMessage());
                groovyFiles.remove(temp);
                errMap.put(temp, (errMap.get(temp) != null ? errMap.get(temp) : 0) + 1);
                List<String> files = groovyFiles;
                //文件加载失败次数小于文件个数时重新加载
                if (errMap.get(temp) <= groovyFiles.size()) {
                    files.add(temp);
                }
                loadGroovys(parent, loader, files, resMap);
            }

            if (groovyClass != null) {
                GroovyObject groovyObj = (GroovyObject) groovyClass.newInstance();
                if (groovyObj instanceof Computer) {
                    GroovyObj.setGroovyObject(groovyObj);
                    resMap.put(groovyClass.getName(), groovyObj);
                }
            }

            groovyFiles.remove(temp);
            List<String> files = groovyFiles;
            loadGroovys(parent, loader, files, resMap);

        }
    }

    public static void beforeLoad(String path, Map<String, Object> resMap) throws CompilationFailedException, IOException, InstantiationException, IllegalAccessException, ResourceException, ScriptException {
        List<String> files = new ArrayList<String>();
        queryGroovys(path, files);
        ClassLoader parent = GroovyObjLoader.class.getClassLoader();//ClassLoader.getSystemClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        loadGroovys(parent, loader, files, resMap);
    }

    public static void main(String[] args) throws CompilationFailedException, IOException, InstantiationException, IllegalAccessException, ResourceException, ScriptException {
        List<String> files = new ArrayList<String>();
        LoadUtils.initLog4jProperties();
        Map<String, Object> resMap = new HashMap<String, Object>();
        queryGroovys("D:\\mock_file\\groovy", files);
        ClassLoader parent = ClassLoader.getSystemClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        loadGroovys(parent, loader, files, resMap);

        GroovyObject groovyObject = GroovyObj.getGroovyObject();

        //GroovyObject groovyObject = (GroovyObject) object;

        //Class groovyClass = loader.parseClass(new File("D:\\mock_file\\groovy\\GroovyCommand.groovy"));

        //GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

        //System.out.println(groovyObject instanceof Computer);

        System.out.println(groovyObject.invokeMethod("compute", "qqq"));
        //System.out.println(1);
    }
}
