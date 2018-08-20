package com.gome.plugins;


import com.gome.util.Content;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * Created by liangwei on 2016/10/28.
 */

/**
 * @goal save
 * @Mojo (name="mongol")
 * @requiresDependencyResolution compile+runtime
 * @requiresProject false
 */


public class MongolMojo extends AbstractMojo {


    /**
     * @parameter expression="${project.basedir}"
     */
    private File basedir;

    /**
     * @parameter expression="${taskId}"
     */
    private int taskId;

    /**
     * @parameter expression="${taskType}"
     */
    private int taskType;

    /**
     * @parameter expression="${splitTime}"
     */
    private String splitTime;


    public void execute() throws MojoExecutionException,MojoFailureException {
        StringBuffer log = new StringBuffer("[Save]");
        try {
            saveTaskReport(log);
        }catch (Exception e){
            System.out.println(e.getStackTrace());
            e.printStackTrace();
        }
    }

    private void saveTaskReport(StringBuffer log) throws Exception {
        ReportBo reportBo = new ReportBo();
        System.out.println("---------------------saveTaskReport begin---------------------");

        File file = new File(basedir.getParent(),Content.JSON_REPORT);
//        File file = new File(basedir.getParent(),Content.JSON_REPORT);
        if (file.exists()) {
            String json = FileUtils.fileRead(file,"UTF-8");
            System.out.println(String.format("%s 保存开始", file.getAbsolutePath()));

            System.out.println(String.format("taskId=%d,taskType=%d",taskId,taskType));

            json = json.replace("\"taskId\": 0", String.format("\"taskId\": %s", taskId))
                    .replace("\"taskType\": 0", String.format("\"taskType\": %s", taskType))
                    .replace("\"splitTime\": 0", String.format("\"splitTime\": %d",Long.valueOf(splitTime.trim())));

            reportBo.insert(json, Content.COLLECTIONNAME);
            String info = String.format("%s 保存完毕\n", file.getAbsolutePath());

            log.append(info);
            System.out.println(info);

            info = "LastRunTime保存完毕";
            log.append(info);

            System.out.println(info);
        } else {
            throw new Exception(String.format("%s 不存在", file.getAbsolutePath()));
        }

        System.out.println("---------------------saveTaskReport end---------------------");
    }
    public static void main(String [] srgs){
        MongolMojo mojo = new MongolMojo();
        mojo.taskId = 123321;
        mojo.taskType=12;
        mojo.splitTime="12";
        try {
            mojo.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
