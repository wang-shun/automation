package com.ofo.test.utils;

import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarFile;

public class ReflectionUtils {
    /**
     * 从jar包中获取资源文件并复制到指定的路径
     *
     * @param jarPath     jar文件路径
     * @param resoucePath 资源文件路径
     * @param copytoPath  复制到
     * @throws Exception
     */
    public static void copyJarResource(String jarPath, String resoucePath, String copytoPath) throws Exception {
        File jarFile = new File(jarPath);
        if (jarFile.exists() == false)
            throw new Exception(String.format("%s 不存在", jarFile.getAbsolutePath()));

        JarFile jar = null;
        try {
            jar = new JarFile(jarFile);
            FileUtils.copyInputStreamToFile(jar.getInputStream(jar.getJarEntry(resoucePath)), new File(copytoPath));
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException ioe) {

                }
            }
        }
    }

    /**
     * 在指定的包中搜索指定基类的派生类(不包含baseClass本身)
     *
     * @param packageName
     * @param baseClass
    // * @throws Exception
     */
    public static List<Class> scanSubclassInPackage(String packageName, Class<?> baseClass) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addUrls(ClasspathHelper.forPackage(packageName))
                .setScanners(new ResourcesScanner(), new TypeAnnotationsScanner(), new SubTypesScanner()));

        Set subTypes = reflections.getSubTypesOf(baseClass);
        Iterator i$ = subTypes.iterator();

        List<Class> result = new ArrayList<Class>();
        while (i$.hasNext()) {
            Class subType = (Class) i$.next();
            if (subType.getName().equals(baseClass.getName()) == false)
                result.add(subType);
        }

        return result;
    }

}