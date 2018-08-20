package com.gome.test.mq.bo;

import java.io.*;

/**
 * Created by zhangjiadi on 16/1/7.
 */
public class XMLUitl {

    public static String repConfigFile(File file) throws Exception {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            InputStreamReader isr = new InputStreamReader(bufferedInputStream, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder resultCfgStr = new StringBuilder("");
            while ((line = br.readLine()) != null) {
                resultCfgStr.append(line.trim());
            }
            br.close();
            isr.close();
            System.out.println(resultCfgStr.toString());
            return resultCfgStr.toString();
        }catch (Exception ex)
        {
            throw  ex;
        }
    }
}
