package com.gome.test.api;

import com.gome.test.api.model.Constant;
import com.gome.test.utils.Logger;
import org.apache.maven.plugin.logging.Log;
import org.dom4j.DocumentException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PluginUtils {

    static void addGroup(Map<String, List<String>> testSuitesByGroup,
                                Set<String> groups, String testSuiteId) {
        for (String group : groups) {
            if ("".equals(group.trim())) {
                continue;
            }
            if (!testSuitesByGroup.containsKey(group)) {
                List<String> testSuites = new ArrayList<String>();
                testSuitesByGroup.put(group, testSuites);
            }
            List<String> testSuites = testSuitesByGroup.get(group);
            testSuites.add(testSuiteId);
        }
    }

    static boolean isClassFileName(String filename) {
        if ('0' <= filename.charAt(0) && '9' >= filename.charAt(0)) {
            return false;
        }

        for (int i = 0; i < filename.length(); ++i) {
            if (!('a' <= filename.charAt(i) && 'z' >= filename.charAt(i))
                    && !('A' <= filename.charAt(i) && 'Z' >= filename.charAt(i))
                    && !('0' <= filename.charAt(i) && '9' >= filename.charAt(i))
                    && '_' != filename.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    static String getClassName(String classPrefixName) {
        StringBuilder sb = new StringBuilder();
        if ('a' <= classPrefixName.charAt(0) && 'z' >= classPrefixName.charAt(0)) {
            sb.append((char) (classPrefixName.charAt(0) - 'a' + 'A'));
            sb.append(classPrefixName.substring(1));
        } else if ('A' <= classPrefixName.charAt(0) && 'Z' >= classPrefixName.charAt(0)) {
            sb.append(classPrefixName);
        } else if ('_' == classPrefixName.charAt(0)) {
            sb.append(classPrefixName);
        }
        sb.append("Test");
        return sb.toString();
    }


    static String getSourceTemplateString(Class<?> c, String template) throws IOException {
        BufferedReader reader = null;
        String line = null;

        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(c
                    .getResourceAsStream(template)));
            while (null != (line = reader.readLine())) {
                sb.append(line);
                sb.append(Constant.LINE_SEPARATOR);
            }
        } finally {
            if (null != reader) {
                reader.close();
            }
        }

        return sb.toString();
    }


    static void saveSourceFile(File sourceFile, StringBuffer strB) throws IOException {
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(sourceFile), "UTF-8");
            writer.write(strB.toString());
            Logger.info("创建 " + sourceFile.getAbsolutePath() + " 完成");
        } catch (Exception ex) {
            Logger.error(sourceFile.getAbsolutePath() + " 创建异常:" + ex.getMessage());
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

    static String getArtifactId(String jarPath) {
        int id = jarPath.lastIndexOf(File.separatorChar);
        String jarName = jarPath.substring(id + 1);
        id = jarName.lastIndexOf('.');
        jarName = jarName.substring(0, id);
        id = jarName.lastIndexOf('-');
        if (-1 == id) {
            return jarName;
        } else {
            return jarName.substring(0, id);
        }
    }

    static String getVersion(String jarPath) {
        int id = jarPath.lastIndexOf(File.separatorChar);
        String jarName = jarPath.substring(id + 1);
        id = jarName.lastIndexOf('.');
        jarName = jarName.substring(0, id);
        id = jarName.lastIndexOf('-');
        if (-1 == id || id + 1 == jarName.length()) {
            return "SNAPSHOT";
        } else {
            return jarName.substring(id + 1);
        }
    }

    static void delete(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                delete(f);
            }
        }
        file.delete();
    }

    static String toLiteral(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str.replaceAll("\\\\", "\\\\\\\\")
                .replaceAll("\\\"", "\\\\\"")
                .replaceAll("\\t", "\\\\t")
                .replaceAll("\\r", "\\\\r")
                .replaceAll("\\n", "\\\\n"));
        return sb.toString();
    }

    static String toLiteralString(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(toLiteral(str));
        sb.append("\"");
        return sb.toString();
    }

    static String toParam(String p) {
        StringBuilder sb = new StringBuilder();
        String param = p.toUpperCase();
        if (param.length() > 0) {
            if (param.charAt(0) >= '0' && param.charAt(0) <= '9') {
                sb.append("_");
            }
        }
        for (int i = 0; i < param.length(); ++i) {
            if ((param.charAt(i) >= '0' && param.charAt(i) <= '9')
                    || (param.charAt(i) >= 'A' && param.charAt(i) <= 'Z')
                    || (param.charAt(i) >= 'a' && param.charAt(i) <= 'z')) {
                sb.append(param.charAt(i));
            } else {
                sb.append("_");
            }
        }
        return sb.toString();
    }

    static String packageNameToPath(String packageName) {
        if ('\\' == File.separatorChar) {
            return packageName.replaceAll("\\.", "\\\\");
        } else {
            return packageName.replaceAll("\\.", "/");
        }
    }

    private static String[] splitByFileSeparator(String p1) {
        String[] parts = null;
        if ('\\' == File.separatorChar) {
            parts = p1.split("\\\\");
        } else {
            parts = p1.split("/");
        }
        return parts;
    }

    static Set<String> stringToSet(String s) {
        Set<String> set = new HashSet<String>();
        String[] strs = s.split(",");
        for (String str : strs) {
            set.add(str.trim());
        }
        return set;
    }

    static boolean checkParam(String str) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Za-z0-9]+$");
        java.util.regex.Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    static void copyResources(Class<?> c, String source, String dest) throws IOException {
        File destFile = new File(dest);
        InputStream is = null;
        OutputStream os = null;
        try {
            is = c.getResourceAsStream(source);
            os = new FileOutputStream(destFile);
            int n = 0;
            byte[] b = new byte[1024];
            while (-1 != (n = is.read(b))) {
                os.write(b, 0, n);
            }
        } finally {
            if (null != is) {
                is.close();
            }
            if (null != os) {
                os.close();
            }
        }
    }

    static void UpdateSuiteXmlFilesInPom(TestProjectArch arch, String testngPath) throws DocumentException, IOException {
        List<String> fileNames = new ArrayList<String>();
        for (File f : new File(testngPath).listFiles()) {
            if (f.isFile() && f.getName().endsWith(".xml")) {
                fileNames.add(String.format("testng/%s", f.getName()));
            }
        }
        String pomPath = arch.getPomPath();
        XmlDocument doc = new XmlDocument(pomPath);
        doc.updateSuiteXmlFiles(fileNames);
        doc.dumpTo(pomPath);
    }

    static  String changeTheFirstCharToUpper(String str) {
        StringBuffer strB = new StringBuffer(str.length());
        strB.append(str.substring(0, 1).toUpperCase());
        strB.append(str.substring(1));
        return strB.toString();
    }


    static  String changeTheFirstCharToLower(String str) {
        StringBuffer strB = new StringBuffer(str.length());
        strB.append(str.substring(0, 1).toLowerCase());
        strB.append(str.substring(1));
        return strB.toString();
    }

    static String getClassLastName(Class<?> cls)
    {
        return getClassLastName(cls.getName());
    }

    static String getClassLastName(String className)
    {
        return className.substring(className.lastIndexOf(".") + 1);
    }


    public static void main(String[] args)
    {
        String str ="com.gome.pangu.redcoupon.dubbo.model.ResultDO<com.gome.pangu.redcoupon.dubbo.model.RedCouponBaseResult>";
        System.out.println(str.substring(str.indexOf("<")+1,str.length()-1));


    }


}
