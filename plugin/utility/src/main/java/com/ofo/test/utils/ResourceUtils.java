package com.ofo.test.utils;


import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ResourceUtils {
    /**
     * 获取指定资源文件的内容 读取成字符串
     */
    public static String getSourceTemplateString(Class<?> c, String template) throws IOException {
        BufferedReader reader = null;
        String line;

        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(c.getResourceAsStream(template)));
            while (null != (line = reader.readLine())) {
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
            }
        } finally {
            if (null != reader) {
                reader.close();
            }
        }

        return sb.toString();
    }

    /**
     * 保存content到descFile
     */
    public static void saveSourceFile(File descFile, String content, boolean override) throws IOException {
        if (descFile.getParentFile().exists() == false)
            descFile.getParentFile().mkdirs();

        if (descFile.exists()) {
            if (override == false)
                return;
            else
                descFile.delete();
        }

        FileUtils.writeStringToFile(descFile, content, "UTF-8");
    }
}
