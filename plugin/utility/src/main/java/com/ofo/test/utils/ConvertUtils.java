package com.ofo.test.utils;


import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.ofo.test.context.ContextUtils;

/**
 * Created by zhangjiadi on 15/9/17.
 */
public class ConvertUtils {
    private   static  HashMap<String,String> patternmap;

    static {
        try {
            patternmap =new HashMap<String, String>();
            patternmap.put("\\$\\{Str\\}_","convertToStr");//${Str}_
            patternmap.put("\\$\\{Int\\}_","convertToInt");//${Int}_
            patternmap.put("\\$\\{Double\\}_","convertToDouble");//${Double}_
            patternmap.put("\\$\\{Date(\\([[- : \\W\\w]]*\\))?\\}_","convertToDate");//${Date(yyyy:MM:dd)}_
            patternmap.put("\\$\\{Str(\\[[[\\w][_,\\$]]{1,2}\\]){1,9}\\}_","convertToStringList");//${Str[,]}_
            patternmap.put("\\$\\{ListStr(\\[[[\\w][_,\\$]]{1,2}\\])?\\}_","convertToListString");//${ListStr[,]}_
        } catch (Exception e) {
            Logger.error("初始化配置失败：" + e.getMessage());
        }
    }

    /*
    * ${Str}_
    * */
    public static String convertToStr(Object o) {
            return o.toString();
    }

    /*
   * ${Int}_
   * */
    public static Integer convertToInt(Object o) {

            return Integer.valueOf(o.toString());

    }

    /*
   * ${Double}_
   * */
    public static Double convertToDouble(Object o) {
            return Double.valueOf(o.toString());
    }

    /*
   * ${Str[,]}_
   * */
    public static String[] convertToStringList(Object o, String splictO) {

        return o.toString().split(String.format("[%s]", splictO));

    }

    /*
   * ${ListStr[,]}_
   * */
    public static List<String> convertToListString(Object o, String splictO) {

        return Arrays.asList(convertToStringList(o, splictO)) ;
    }

    /*
  * ${Date(yyyy:MM:dd)}_
  * */
    public static Date convertToDate(Object o, String dateFormat) {
        try {
            if(o instanceof Date)
                return (Date) o;
            SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
            return fmt.parse(o.toString());
        } catch (Exception ex) {
            return null;
        }

    }

    private static String getKey(String str) {
        if (str.indexOf("_") > 0) {
            String[] keyList = str.split("_");
            if (keyList.length > 1)
                return keyList[1];
        }

        return str;
    }

    private static Boolean putResult(String key,String value,Map<String, Object> result) {
        boolean isput = false;
        try {
            Class c = Class.forName("ConvertUtils");
            Method[] methods = c.getMethods();

            for (String mapKey : patternmap.keySet()) {
                Pattern p = Pattern.compile(mapKey);
                Matcher matcher = p.matcher(key);
                if (matcher.find() && matcher.lookingAt()) {
                    String inputKey = getKey(key);

                    for (Method m : methods) {
                        if (m.getName().equals(patternmap.get(mapKey).toString())) {
                            if (matcher.groupCount() > 0) {
                                String rex = matcher.group(1).replace("(", "").replace(")", "").replace("[", "").replace("]", "");
                                result.put(inputKey, m.invoke(c, value, rex));
                                isput = true;
                            } else {
                                isput = true;
                                result.put(inputKey, m.invoke(c, value));
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {

        }
        return isput;
    }




    public static Map<String, Object> convertToHasMap(Map<String, String> map) {
        Map<String, Object> result = new HashMap<String, Object>();

        for (String key : map.keySet()) {
            String value=ContextUtils.loadValueWithContext(map.get(key));
           if(!putResult(key,value,result))
               result.put(getKey(key), value);
        }
        return result;
    }



    @Test
    public void test() throws  Exception
    {
        Map<String,String> m=new HashMap<String, String>();
        m.put("${Date(yyyy-MM-dd HH:mm)}_date","2012-11-12 11:12");
        m.put("${Str[,]}_Strlist","11,22,33,44");
        m.put("${ListStr[,]}_ListStr","11,22,33,44");
        m.put("${Int}_int","121");
        m.put("${Str}_str","121");
        m.put("${Double}_double","12.1");



        Map<String,Object> result=convertToHasMap(m);

        System.out.print( (Date) result.get("date"));
        System.out.print( (Integer) result.get("int"));
        System.out.print( (Double) result.get("double"));
    }


//    p = Pattern.compile("\\$\\{Str\\}_");//${Str}_
//    Matcher matcher = p.matcher(key);
//    isput = matcher.find();
//    if (isput && matcher.lookingAt()) {//String
//        result.put(getKey(key), ConvertToStr(value));
//        continue;
//    }
//
//    p = Pattern.compile("\\$\\{Int\\}_");//${Int}_
//    matcher = p.matcher(key);
//    isput = matcher.find();
//    if (isput && matcher.lookingAt()) {//String
//        result.put(getKey(key), ConvertToInt(value));
//        continue;
//    }
//
//    p = Pattern.compile("\\$\\{Double\\}_");//${Double}_
//    matcher = p.matcher(key);
//    isput = matcher.find();
//    if (isput && matcher.lookingAt()) {//String
//        result.put(getKey(key), ConvertToDouble(value));
//        continue;
//    }
//
//    p = Pattern.compile("\\$\\{Date(\\([[- : \\W\\w]]*\\))?\\}_");//${Date(yyyy:MM:dd)}_
//    matcher = p.matcher(key);
//    isput = matcher.find();
//    if (isput && matcher.lookingAt()) {//String
//        String rex = matcher.group(1).replace("(", "").replace(")", "");
//        result.put(getKey(key), ConvertToDate(value, rex));
//        continue;
//    }
//
//    p = Pattern.compile("\\$\\{Str(\\[[[\\w][_,\\$]]{1,2}\\])?\\}_"); //${Str[,]}_
//    matcher = p.matcher(key);
//    isput = matcher.find();
//    if (isput && matcher.lookingAt()) {//string数组
//        String rex = matcher.group(1).replace("[", "").replace("]", "");
//        result.put(getKey(key), ConvertToStringList(value, rex));
//        continue;
//    }
//
//    p = Pattern.compile("\\$\\{ListStr(\\[[[\\w][_,\\$]]{1,2}\\])?\\}_");//${ListStr[,]}_
//    matcher = p.matcher(key);
//    isput = matcher.find();
//    if (isput) {//List<String>
//        String rex = matcher.group(1).replace("[", "").replace("]", "");
//        result.put(getKey(key), ConvertToListString(value, rex));
//        continue;
//    }


}
