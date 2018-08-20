package com.gome.test.mock.dao.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;

/**
 * <h3>加载工具类</h3>
 */
public class LoadUtils {
    /**目录分隔符*/
    public final static String FILE_SEP;
    /**系统默认文件夹*/
    public final static String SYSTEM_TEMPDIR;
    static {
        FILE_SEP = "/";
        SYSTEM_TEMPDIR = System.getProperty("java.io.tmpdir");
    }

    /**
     * 读取日志配置文件
     */
    public static void initLog4jProperties() {
        //未打包时读取配置
        String file = FileUtils.class.getClassLoader().getResource("log4j.properties").getFile();
        if (new File(file).exists()) {
            PropertyConfigurator.configure(file);
            return;
        }

        //读取jar包内配置文件
        InputStream in = FileUtils.class.getClassLoader().getResourceAsStream("log4j.properties");
        Properties p = new Properties();
        try {
            p.load(in);
            PropertyConfigurator.configure(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断http链接是否存在
     *
     * @param httpPath
     * @return
     */
    public static Boolean existHttpPath(String httpPath) {
        URL httpurl = null;
        try {
            httpurl = new URL(httpPath);
            URLConnection rulConnection = httpurl.openConnection();
            rulConnection.getInputStream();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 取得类所在的ClassPath目录，比如tomcat下的classes路径
     * @param clazz
     * @return
     */
    public static File getClassRootPathFile(Class<?> clazz) {
        URL path = clazz.getResource(clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1) + ".classs");
        if (path == null) {
            String name = clazz.getName().replaceAll("[.]", "/");
            path = clazz.getResource("/" + name + ".class");
        }
        File file = new File(path.getFile());
        int count = clazz.getName().split("[.]").length;
        for (int i = 0; i < count; i++) {
            file = file.getParentFile();
        }
        if (file.getName().toUpperCase().endsWith(".JAR!")) {
            file = file.getParentFile();
        }
        return file;
    }

    /**
     * <b>获取类路径下某一个文件夹所有的文件名</b>
     *
     * @param packageName 包名
     * @param ext 扩展名
     *
     * @return List<String>
     */
    public static List<String> getPackageAllFileName(String packageName, String ext) throws Exception {
        List<String> resultList = new ArrayList<String>();
        URL url = LoadUtils.class.getClassLoader().getResource(packageName.replace(".", "/"));

        String path = url.getPath();
        if (path.indexOf(".jar!") == -1) {
            File packeageDir = null;
            try {
                packeageDir = new File(url.toURI());
            } catch (URISyntaxException e) {
                throw new Exception("扫描包" + packageName + "发生异常", e);
            }
            List<String> list = new ArrayList<String>();
            getALLClassName(packeageDir, ext, list);
            for (int i = 0; i < list.size(); i++) {
                String className = list.get(i);
                resultList.add(className.substring(className.indexOf(packageName)));
            }
        } else {
            String jarPath = path.replace("file:/", "");
            try {
                JarFile jarFile = new JarFile(jarPath.substring(0, jarPath.indexOf(".jar") + 4));
                resultList = getALLJarClassName(jarFile, packageName);
            } catch (IOException e) {
                throw new Exception("扫描包" + packageName + "发生异常", e);
            }

        }
        return resultList;
    }

    private static void getALLClassName(File dir, String ext, List<String> list) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    getALLClassName(files[i], ext, list);
                } else {
                    String fileName = files[i].getAbsolutePath().replace("/", ".").replace("\\", ".");
                    if (fileName.endsWith(ext)) {
                        list.add(fileName.substring(0, fileName.length() - ext.length()));
                    }
                }
            }
        }
    }

    private static List<String> getALLJarClassName(JarFile jarFile, String packageName) {
        List<String> list = new ArrayList<String>();
        Enumeration<JarEntry> entrys = jarFile.entries();
        while (entrys.hasMoreElements()) {
            JarEntry jar = entrys.nextElement();
            String fileName = jar.getName().replace("/", ".").replace("\\", ".");
            if (fileName.indexOf(packageName) != -1 && fileName.endsWith(".class")) {
                list.add(fileName.substring(0, fileName.indexOf(".class")));
            }
        }
        return list;
    }
}
