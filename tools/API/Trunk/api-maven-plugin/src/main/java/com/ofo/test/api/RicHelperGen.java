package com.ofo.test.api;


import com.ofo.test.Constant;
import com.ofo.test.plugin.Utils;
import com.ofo.test.utils.Logger;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RicHelperGen {
    private File helperDir;
    private String localRepository;
    private String groupId;
    private String artifactId;
    private String version;
    private ClassLoader classLoader;
    private String jarPath;
    private Log logger;
//    private File dubboConfigFile;
//    private String dubboConfigFileContent;

    public void genTestProject(File helperDir, String localRepository, ArrayList groupIdList, ArrayList artifactIdList,
                               ArrayList versionList, Map<String, List<String>> clientInterfaceMethodMap, Log logger) throws Exception {

        int urlCount = groupIdList.size() + 1;
        URL[] urls = new URL[urlCount];
        for (int i = 0; i < groupIdList.size(); i++) {

            String jarPath = getJarFullPath(localRepository,
                    groupIdList.get(i).toString(),
                    artifactIdList.get(i).toString(),
                    versionList.get(i).toString());
            urls[i] = new URL("file:" + jarPath);
        }
        this.helperDir = helperDir;
//      
//  this.dubboConfigFile = new File(helperDir, Constant.DUBBO_SPRINT_CONFIG_FILE);
//        if (this.dubboConfigFile.exists())
//            this.dubboConfigFileContent = FileUtils.readFileToString(this.dubboConfigFile);
//        else
//            Logger.error("%s 不存在", this.dubboConfigFile.getAbsolutePath());

        this.localRepository = localRepository;
        this.groupId = groupIdList.get(0).toString();
        this.artifactId = artifactIdList.get(0).toString();
        this.version = versionList.get(0).toString();
        this.logger = logger;
        this.jarPath = getJarFullPath();
        urls[urlCount - 1] = new URL("file:" + this.jarPath);
        this.classLoader = new URLClassLoader(urls);
        this.generateHelperSourceFile(clientInterfaceMethodMap);
    }


    private void generateHelperSourceFile(Map<String, List<String>> clientInterfaceMethodMap) throws IOException, ClassNotFoundException {

        Logger.info("load " + this.jarPath);
        JarFile jarFile = new JarFile(new File(this.jarPath));
        searchClass(jarFile.entries(), this.classLoader, clientInterfaceMethodMap);
    }

    private void searchClass(Enumeration<JarEntry> jarEntries, ClassLoader loader, Map<String, List<String>> clientInterfaceMethodMap) throws IOException, ClassNotFoundException {
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarName = jarEntry.getName();
            if (!jarEntry.isDirectory() && jarName.endsWith(".class")) {
                jarName = jarName.replace("/", ".").substring(0, jarName.length() - 6);//去掉.class后缀
                Class<?> c = loader.loadClass(jarName);
                if (null != c && c.isInterface() && interfaceContains(c, clientInterfaceMethodMap)) {
                    generateDir(jarName, c, clientInterfaceMethodMap);
                }
            }
        }
    }

    private void generateDir(String jarName, Class<?> c, Map<String, List<String>> clientInterfaceMethodMap) throws ClassNotFoundException, IOException {
        String packageName;
        packageName = "com.ofo.test.api." + Utils.getClassLastName(c).toLowerCase();
        StringBuffer dirPath = new StringBuffer(Constant.MAIN_DIR_JAVA);
        dirPath.append(packageName.replace(".", String.valueOf(File.separatorChar)));
        File dir = new File(helperDir, dirPath.toString());

        if (dir.exists() == false) {
            if (dir.mkdirs()) {
                Logger.info("创建 " + dir.getAbsolutePath() + " 完成");
                generateSourceFile(dir, c, packageName, clientInterfaceMethodMap);
            } else {
                Logger.info("创建 " + dir.getAbsolutePath() + " 失败");
            }
        } else {
            Logger.info(dir.getAbsolutePath() + " 已存在");
            generateSourceFile(dir, c, packageName, clientInterfaceMethodMap);
        }
    }

    private void generateSourceFile(File dir, Class<?> c, String packageName, Map<String, List<String>> clientInterfaceMethodMap) throws IOException {
        if (dir.exists()) {
            String classFullName = c.getName();
            String className = Utils.getClassLastName(c);

            for (Method m : c.getMethods()) {
                if (methodContains(c, m, clientInterfaceMethodMap)) {
                    generateAdapterFile(dir, classFullName, className, m.getName(), m.getGenericReturnType(), m.getGenericParameterTypes(), packageName);
                    generateVerifyFile(dir, className, classFullName, m.getName(), m.getGenericReturnType(), packageName);
                }

            }
        } else {
            Logger.info(dir.getAbsolutePath() + " 不存在，不能生成源码");
        }
    }

    private void generateAdapterFile(File dir, String classFullName, String className, String methodName, Type returnType,
                                     Type[] parameterTypes, String packageName) throws IOException {
        File adapterSourceFile = new File(dir, Utils.changeTheFirstCharToUpper(className) + Utils.changeTheFirstCharToUpper(methodName) + "Adapter.java");
        Set<String> imports = new HashSet<String>();
        if (adapterSourceFile.exists() == false) {
            StringBuffer strB = new StringBuffer();

            strB.append(Utils.getSourceTemplateString(getClass(), "/adaptermethod.tpl")
                    .replaceAll("@importPackage@", Matcher.quoteReplacement(classFullName))
                    .replaceAll("@packageName@", Matcher.quoteReplacement(packageName))
                    .replaceAll("@className@", Matcher.quoteReplacement(className))
                    .replaceAll("@serviceParamName@", Matcher.quoteReplacement(Utils.changeTheFirstCharToLower(className)))
                    .replaceAll("@init@", getParamInit(methodName, parameterTypes))
                    .replaceAll("@method@", Matcher.quoteReplacement(Utils.changeTheFirstCharToUpper(methodName))));
            strB.append(Constant.LINE_SEPARATOR);
            Utils.saveSourceFile(adapterSourceFile, strB);
        } else {
            Logger.info(adapterSourceFile.getAbsolutePath() + " 已存在，不更新");
        }
    }

    private void generateVerifyFile(File dir, String className, String classFullName, String methodName, Type returnType, String packageName) throws IOException {
        File factorySourceFile = new File(dir, Utils.changeTheFirstCharToUpper(className) + Utils.changeTheFirstCharToUpper(methodName) + "Verify.java");
        Set<String> imports = new HashSet<String>();
        if (factorySourceFile.exists() == false) {
            String returnTypeName = getParameters(false, returnType);
            StringBuffer strB = new StringBuffer();
            strB.append(Utils.getSourceTemplateString(getClass(), "/adapterverify.tpl")
                    .replaceAll("@importPackage@", Matcher.quoteReplacement(classFullName))
                    .replaceAll("@packageName@", Matcher.quoteReplacement(packageName))
                    .replaceAll("@className@", Matcher.quoteReplacement(className))
                    .replaceAll("@init@", "//TODO")//Matcher.quoteReplacement(MessageFormat.format("{0} result = ({0})obj;", returnTypeName)))
                    .replaceAll("@method@", Matcher.quoteReplacement(Utils.changeTheFirstCharToUpper(methodName)))
                    .replaceAll("@className@", Utils.changeTheFirstCharToUpper(className)));
            strB.append(Constant.LINE_SEPARATOR);
            Utils.saveSourceFile(factorySourceFile, strB);
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
        if (strB.length() > 0)
            strB.append(Constant.LINE_SEPARATOR);

        for (String p : params) {

        }
    }

    /*获取如参列表*/
    private List<String> getParametersMap(boolean generateParam, Type... parameterTypes) {
        List<String> params = new ArrayList<String>();
        StringBuffer strB = new StringBuffer();
        for (Type type : parameterTypes) {

//            String genericMainTypeName = ContextUtils.getClassLastName(getGenericMainType(type));
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

            if (type instanceof Class<?>)
                strB.append(((Class<?>) type).getName().replace("class ", "").replace("java.lang.", ""));
            else
                strB.append(type.toString().replace("class ", "").replace("java.lang.", ""));

            params.add(strB.toString());
            strB.setLength(0);
        }

        return params;
    }

//    private String getGenericImport(Set<String> imports, Type... types) {
//        StringBuffer strB = new StringBuffer();
//        for (Type t : types) {
//           if(t instanceof TypeVariableImpl)
//           {
//               ((TypeVariableImpl)t).getBounds();
//                t=((TypeVariableImpl)t).getBounds()[0];
//           }
//
//            if( t instanceof GenericArrayType)
//            {
//               t= ((GenericArrayType)t).getGenericComponentType();
//            }
//
//            if(t instanceof ParameterizedTypeImpl)
//            {
//                t= ((ParameterizedTypeImpl) t).getRawType();//.getActualTypeArguments()[0];
//            }
//
//            String mainType = getGenericMainType(t);
//            if (mainType.startsWith("java.lang.") == false && imports.contains(mainType) == false && mainType!="void" && mainType !="int" && mainType!="boolean") {
//                imports.add(mainType);
//                if (strB.length() > 0)
//                    strB.append(Constant.LINE_SEPARATOR);
//
//                strB.append(Constant.IMPORT);
//                strB.append(Constant.SPACE);
//                strB.append(mainType);
//                strB.append(";");
//            }
//        }
//
//        return strB.toString();
//    }

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
                types[i] = Utils.getClassLastName(types[i]);
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

    private String getJarFullPath(String localRepository, String groupId, String artifactId, String version) {
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
            strB.append(Utils.getSourceTemplateString(getClass(), "/adapterservice.tpl")
                    .replaceAll("@packageName@", Matcher.quoteReplacement(classFullName.toLowerCase()))
                    .replaceAll("@className@", Matcher.quoteReplacement(className))
                    .replaceAll("@import@", Matcher.quoteReplacement(classFullName))
                    .replaceAll("@classNameParam@", Matcher.quoteReplacement(Utils.changeTheFirstCharToLower(className))));
            strB.append(Constant.LINE_SEPARATOR);
            Utils.saveSourceFile(factorySourceFile, strB);
        } else {
            Logger.info(factorySourceFile.getAbsolutePath() + " 已存在，不更新");
        }
    }

    private boolean interfaceContains(Class<?> c, Map<String, List<String>> clientInterfaceMethodMap) {
        List<String> interfaceMethods = clientInterfaceMethodMap.get(String.format("%s-%s", groupId, artifactId));
        for (String imEx : interfaceMethods) {
            String itfEx = imEx.split(":")[0].toLowerCase();//取通配interface
            if (itfEx.length() > 0) {//interface不为空
                if (matchInterfaceMethod(Utils.getClassLastName(c), itfEx)) {
                    return true;
                }
            } else {//interface为空
                return true;
            }
        }
        return false;
    }

    private boolean methodContains(Class<?> c, Method m, Map<String, List<String>> clientInterfaceMethodMap) {
        List<String> interfaceMethods = clientInterfaceMethodMap.get(String.format("%s-%s", groupId, artifactId));
        for (String im : interfaceMethods) {
            String mthdEx = im.split(":")[1].toLowerCase();//取通配method
            String mthdName = m.getName();
            if (interfaceContains(c, clientInterfaceMethodMap) && matchInterfaceMethod(mthdName, mthdEx)) {
                return true;
            }


        }
        return false;
    }

    private boolean matchInterfaceMethod(String realName, String reg) {
        return mySimpleMatch(reg.toLowerCase(), realName.toLowerCase());
    }

    private boolean mySimpleMatch(String pattern, String str) {
        pattern = pattern.replaceAll("\\*",".*").replaceAll("\\?", ".{0,1}");
        return Pattern.matches(pattern, str);
    }

    public static void main(String[] args) {
        String im = "interface:method";
        System.out.println("|" + im.split("\\*")[0] + "|");


        String cname = "itface";
        String im1 = "itf*:mthd*";
        System.out.println(cname.startsWith(im1.split(":")[0].split("\\*")[0]));

        String im2 = "*:*";
//        System.out.println(cname.startsWith(im2.split(":")[0].split("\\*")[0]));

        RicHelperGen ricHelperGen = new RicHelperGen();
        System.out.println(Utils.getClassLastName(ricHelperGen.getClass()));

        System.out.println("redcoupon*".contains("*"));
        System.out.println("redcoupon*".split("\\*")[0]);
        System.out.println("*".split("\\*").length);
        String xing = "*";
        System.out.println(xing.equals("*"));
    }
}
