package com.gome.test.gtp.Load;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Created by zhangjiadi on 15/11/5.
 */
public class FileSplitProcessor implements Runnable{

    private List<String> sList;
    private String fileName;
    private int fileIndex;

    public FileSplitProcessor(List<String> sList, String fileName,
                              int fileIndex) {
        super();
        this.sList = sList;
        this.fileName = fileName;
        this.fileIndex = fileIndex;
    }

    public void run() {
        try{
            write();
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }


    }

    private void write() throws Exception
    {
        String outputFile = fileName + ".tmp" + fileIndex;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile),LoadConfig.CHARSET_CODE));
            for(String sData : sList) {
                bw.write(sData + "\n");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bw!=null)
            {
                bw.flush();
                bw.close();
            }

            sList.clear();
        }
    }
}
