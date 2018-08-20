package com.ofo.test.utils;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2015/7/20.
 */
public class FileUtils {

    /**
     * 删除空目录
     *
     * @param dir 将要删除的目录路径
     */
    public void doDeleteEmptyDir(String dir) {

        boolean success = (new File(dir)).delete();
        if (success) {
            System.out.println("Successfully deleted empty directory: " + dir);
        } else {
            System.out.println("Failed to delete empty directory: " + dir);
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }


        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * @param toFile   复制的目录文件
     * @param rewrite  是否重新创建文件
     *                 <p>
     *                 </p>文件的复制操作方法
     */
    public static void copyInputStream2File(InputStream fisfrom, File toFile, Boolean rewrite) {

        try {
            if (!toFile.getParentFile().exists()) {
                toFile.getParentFile().mkdirs();
            }
            if (toFile.exists() && rewrite) {
                toFile.delete();
            }
            FileOutputStream fosto = new FileOutputStream(toFile);

            byte[] bt = new byte[1024];
            int c;
            while ((c = fisfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            //关闭输入、输出流
            fisfrom.close();
            fosto.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public static void appendContent2File(File toFile, String content) {
        try {
            if (!toFile.exists()) {
                toFile.createNewFile();
            } else {
                toFile.delete();
                toFile.createNewFile();
            }
            BufferedWriter bfWriter = new BufferedWriter(new FileWriter(toFile));
            bfWriter.write(content);
            bfWriter.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void copyTest() throws FileNotFoundException {
        File targetFile = new File("D:\\svn\\SVNCode\\NewLoadTest\\orderGroup\\ExecuteTest\\args.xml");


//        InputStream fis = ClassLoader.getSystemResourceAsStream("/args-tpl.xml");
//        copyFile(fis,targetFile,true);

        File file = new File("D:\\svn\\SVNCode\\Doraemon\\GTP\\Trunk\\gtp-maven-plugin\\src\\main\\resources\\args-tpl.xml");
        InputStream is = new FileInputStream(file);
        copyInputStream2File(is, targetFile, true);
    }


    //**
    // 递归查找某个文件夹下的符合条件的特定文件
    // *//
    public static void findFiles(String baseDirName, String targetFileName,List fileList) {
        /**
         * 算法简述：
         * 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件，
         * 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。
         * 队列不空，重复上述操作，队列为空，程序结束，返回结果。
         */
        String tempName = null;

        //判断目录是否存在
        File baseDir = new File(baseDirName);
        if (!baseDir.exists() || !baseDir.isDirectory()){
            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
        } else {
            String[] filelist = baseDir.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(baseDirName + File.separator + filelist[i]);
                //System.out.println(readfile.getName());
                if(!readfile.isDirectory()) {
                    tempName =  readfile.getName();
                    if (wildcardMatch(targetFileName, tempName)) {
                        //匹配成功，将文件名添加到结果集
                        fileList.add(readfile.getAbsoluteFile());
                    }
                } else if(readfile.isDirectory()){
                    findFiles(baseDirName + File.separator + filelist[i],targetFileName,fileList);
                }
            }
        }
    }

    /**
     * 通配符匹配
     * @param pattern    通配符模式
     * @param str    待匹配的字符串
     * @return    匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                //通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1),
                            str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                //通配符问号?表示匹配任意一个字符
                strIndex++;
                if (strIndex > strLength) {
                    //表示str中已经没有字符匹配?了。
                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return (strIndex == strLength);
    }


    public static void main(String[] a) throws IOException, ParserConfigurationException, SAXException {



        //    在此目录中找文件
        String baseDIR = "/Users/zhangjiadi/Documents/GOME/123";
        //    找扩展名为txt的文件
        String fileName = "*Data";
        List resultList = new ArrayList();
        findFiles(baseDIR, fileName, resultList);
        if (resultList.size() == 0) {
            System.out.println("No File Fount.");
        } else {
            for (int i = 0; i < resultList.size(); i++) {
                System.out.println(resultList.get(i));//显示查找结果。
            }
        }

        File targetFile = new File("D:\\svn\\SVNCode\\NewLoadTest\\orderGroup\\ExecuteTest\\args.xml");


//        InputStream fis = ClassLoader.getSystemResourceAsStream("/args-tpl.xml");
//        copyFile(fis,targetFile,true);

        File file = new File("D:\\svn\\SVNCode\\Doraemon\\GTP\\Trunk\\gtp-maven-plugin\\src\\main\\resources\\args-tpl.xml");
        InputStream is = new FileInputStream(file);
        copyInputStream2File(is,targetFile,true);

//        Document doc = XmlUtils.parseXmlFile2Doc("D:\\svn\\SVNCode\\NewLoadTest\\orderGroup\\ExecuteTest\\args.xml");
//        XmlUtils.reSaveXmlFile(doc,"D:\\svn\\SVNCode\\NewLoadTest\\orderGroup\\ExecuteTest\\args.xml");
    }

}
