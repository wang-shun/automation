package com.gome.test.api;


import com.gome.test.api.model.Constant;
import com.gome.test.utils.Logger;
import org.apache.maven.plugin.logging.Log;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;

public class RicHelperGen {
    private File helperDir;
    private String localRepository;
    private String groupId;
    private String artifactId;
    private String version;
    private Log logger;

    public void genTestProject(File helperDir, String localRepository, String groupId, String artifactId,
                               String version, Log logger) throws Exception {
        this.helperDir = helperDir;
        this.localRepository = localRepository;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.logger = logger;
        this.generateHelperSourceFile();
    }

    private void generateHelperSourceFile() throws IOException, ClassNotFoundException {
        String jarPath = getJarFullPath();
        Logger.info("load " + jarPath);
        JarFile jarFile = new JarFile(new File(jarPath));
        ClassLoader loader = new URLClassLoader(new URL[]{new URL("file:" + jarPath)});
        searchClass(jarFile.entries(), loader);
    }

    private void searchClass(Enumeration<JarEntry> jarEntries, ClassLoader loader) throws IOException, ClassNotFoundException {
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarName = jarEntry.getName();
            if (!jarEntry.isDirectory() && jarName.endsWith(".class")) {
                jarName = jarName.replace("/", ".").substring(0, jarName.length() - 6);//去掉.class后缀
                Class<?> c = loader.loadClass(jarName);
                if (null != c && c.isInterface()) {
                    generateDir(jarName, c);
                }
            }
        }
    }

    private void generateDir(String jarName, Class<?> c) throws ClassNotFoundException, IOException {
        String packageName = "com.gome.test.api." + PluginUtils.getClassLastName(c).toLowerCase();
        StringBuffer dirPath = new StringBuffer(Constant.MAIN_DIR_JAVA);
        dirPath.append(packageName.replace(".", String.valueOf(File.separatorChar)));
        File dir = new File(helperDir, dirPath.toString());

        if (dir.exists() == false) {
            if (dir.mkdirs()) {
                Logger.info("创建 " + dir.getAbsolutePath() + " 完成");
                generateSourceFile(dir, c, packageName);
            } else {
                Logger.info("创建 " + dir.getAbsolutePath() + " 失败");
            }
        } else {
            Logger.info(dir.getAbsolutePath() + " 已存在");
            generateSourceFile(dir, c, packageName);
        }
    }

    private void generateSourceFile(File dir, Class<?> c, String packageName) throws IOException {
        if (dir.exists()) {
            String classFullName = c.getName();
            String className = PluginUtils.getClassLastName(c);

            for (Method m : c.getMethods()) {
//                if (m.getName().endsWith("findRedCouponsByTicketId")) {
                    generateAdapterFile(dir, classFullName, className, m.getName(), m.getGenericReturnType(), m.getGenericParameterTypes(), packageName);
                    generateVerifyFile(dir, className, classFullName, m.getName(), m.getGenericReturnType(), packageName);
//                }
            }
        } else {
            Logger.info(dir.getAbsolutePath() + " 不存在，不能生成源码");
        }
    }

    private void generateAdapterFile(File dir, String classFullName, String className, String methodName, Type returnType,
                                     Type[] parameterTypes, String packageName) throws IOException {
        File adapterSourceFile = new File(dir, PluginUtils.changeTheFirstCharToUpper(methodName) + "Adapter.java");
        Set<String> imports = new HashSet<String>();
        if (adapterSourceFile.exists() == false) {
            StringBuffer strB = new StringBuffer();

            strB.append(PluginUtils.getSourceTemplateString(getClass(), "/adaptermethod.tpl")
                    .replaceAll("@importPackage@", Matcher.quoteReplacement(classFullName))
                    .replaceAll("@packageName@", Matcher.quoteReplacement(packageName))
                    .replaceAll("@className@", Matcher.quoteReplacement(className))
                    .replaceAll("@serviceParamName@", Matcher.quoteReplacement(PluginUtils.changeTheFirstCharToLower(className)))
                    .replaceAll("@importReturnType@", Matcher.quoteReplacement(getGenericImport(imports, returnType)))
                    .replaceAll("@importParameterTypes@", Matcher.quoteReplacement(getGenericImport(imports, parameterTypes)))
                    .replaceAll("@init@", getParamInit(methodName, parameterTypes))
                    .replaceAll("@method@", Matcher.quoteReplacement(PluginUtils.changeTheFirstCharToUpper(methodName))));
            strB.append(Constant.LINE_SEPARATOR);
            PluginUtils.saveSourceFile(adapterSourceFile, strB);
        } else {
            Logger.info(adapterSourceFile.getAbsolutePath() + " 已存在，不更新");
        }
    }

    private void generateVerifyFile(File dir, String className, String classFullName, String methodName, Type returnType, String packageName) throws IOException {
        File factorySourceFile = new File(dir, PluginUtils.changeTheFirstCharToUpper(methodName) + "Verify.java");
        Set<String> imports = new HashSet<String>();
        if (factorySourceFile.exists() == false) {
            String returnTypeName = getParameters(false, returnType);
            StringBuffer strB = new StringBuffer();

            strB.append(PluginUtils.getSourceTemplateString(getClass(), "/adapterverify.tpl")
                    .replaceAll("@importPackage@", Matcher.quoteReplacement(classFullName))
                    .replaceAll("@packageName@", Matcher.quoteReplacement(packageName))
                    .replaceAll("@className@", Matcher.quoteReplacement(className))
                    .replaceAll("@importReturnType@", Matcher.quoteReplacement(getGenericImport(imports, returnType)))
                    .replaceAll("@init@", "//TODO")//Matcher.quoteReplacement(MessageFormat.format("{0} result = ({0})obj;", returnTypeName)))
                    .replaceAll("@method@", Matcher.quoteReplacement(PluginUtils.changeTheFirstCharToUpper(methodName))));
            strB.append(Constant.LINE_SEPARATOR);
            PluginUtils.saveSourceFile(factorySourceFile, strB);
        } else {
            Logger.info(factorySourceFile.getAbsolutePath() + " 已存在，不更新");
        }
    }

    private String getParamInit(String methodName, Type[] parameterTypes) {
        return "//TODO";
//        StringBuffer strB = new StringBuffer();
//        strB.append(String.format("return service.%s(%s);", methodName, getParameters(true, parameterTypes)));
//        return strB.toString();
    }

    private String getParameters(boolean generateParam, Type... parameterTypes) {
        StringBuffer strB = new StringBuffer();
        List<String> params = getParametersMap(false, parameterTypes);
        for (int i = 0; i < params.size(); i++) {
            if (strB.length() > 0)
                strB.append(",");

            strB.append(params.get(i));
            strB.append(Constant.SPACE);

            if (generateParam)
                strB.append("param" + i);
        }
        return strB.toString();
    }

    private void appendParamInitValue(StringBuffer strB, List<String> params) {
        if(strB.length() >0)
            strB.append(Constant.LINE_SEPARATOR);

        for(String p : params) {

        }
    }

    /*获取如参列表*/
    private List<String> getParametersMap(boolean generateParam, Type... parameterTypes) {
        List<String> params = new ArrayList<String>();
        StringBuffer strB = new StringBuffer();
        for (Type type : parameterTypes) {

//            String genericMainTypeName = APIUtils.getClassLastName(getGenericMainType(type));
//            strB.append(genericMainTypeName);

//            String[] parameterizedTypes = getGenericParameterizedTypes(type);
//            if (parameterizedTypes.length > 0) {
//                strB.append("<");
//                for (int i = 0; i < parameterizedTypes.length; i++) {
//                    if (i > 0)
//                        strB.append(",");
//
//                    strB.append(parameterizedTypes[i]);
//                }
//                strB.append(">");
//            }

            if(type instanceof  Class<?>)
                strB.append(((Class<?>) type).getName().replace("class ","").replace("java.lang.",""));
            else
                strB.append(type.toString().replace("class ", "").replace("java.lang.", ""));

            params.add(strB.toString());
            strB.setLength(0);
        }

        return params;
    }

    private String getGenericImport(Set<String> imports, Type... types) {
        StringBuffer strB = new StringBuffer();
        for (Type t : types) {
            String mainType = getGenericMainType(t);
            if (mainType.startsWith("java.lang.") == false && imports.contains(mainType) == false) {
                imports.add(mainType);
                if (strB.length() > 0)
                    strB.append(Constant.LINE_SEPARATOR);

                strB.append(Constant.IMPORT);
                strB.append(Constant.SPACE);
                strB.append(mainType);
                strB.append(";");
            }
        }

        return strB.toString();
    }

    private String getGenericMainType(Type type) {
        String str = type.toString();
        if (str.contains("<"))
            return str.substring(0, str.indexOf("<"));
        else
            return ((Class<?>) type).getName();
    }

    private String[] getGenericParameterizedTypes(Type type) {
        String str = type.toString();
        if (str.contains("<")) {
            String[] types = str.substring(str.indexOf("<") + 1, str.length() - 1).split(",");
            for (int i = 0; i < types.length; i++) {
                types[i] = PluginUtils.getClassLastName(types[i]);
            }
            return types;
        } else
            return new String[]{};
    }

    private String getJarFullPath() {
        StringBuffer strB = new StringBuffer();
        strB.append(localRepository);
        strB.append(File.separatorChar);
        strB.append(groupId.replace(".", File.separator)).append(File.separator);
        strB.append(artifactId).append(File.separator);
        strB.append(version).append(File.separator);
        strB.append(artifactId).append("-").append(version).append(".jar");
        return strB.toString();
    }

    @Deprecated
    private void generateServiceFile(File dir, String className, String classFullName) throws IOException {
        File factorySourceFile = new File(dir, className + "Adapter.java");
        if (factorySourceFile.exists() == false) {
            StringBuffer strB = new StringBuffer();
            strB.append(PluginUtils.getSourceTemplateString(getClass(), "/adapterservice.tpl")
                    .replaceAll("@packageName@", Matcher.quoteReplacement(classFullName.toLowerCase()))
                    .replaceAll("@className@", Matcher.quoteReplacement(className))
                    .replaceAll("@import@", Matcher.quoteReplacement(classFullName))
                    .replaceAll("@classNameParam@", Matcher.quoteReplacement(PluginUtils.changeTheFirstCharToLower(className))));
            strB.append(Constant.LINE_SEPARATOR);
            PluginUtils.saveSourceFile(factorySourceFile, strB);
        } else {
            Logger.info(factorySourceFile.getAbsolutePath() + " 已存在，不更新");
        }
    }

}
