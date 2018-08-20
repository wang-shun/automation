package com.gome.test.gui.testng;

import com.gome.test.utils.JsonUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.uncommons.reportng.HTMLReporter;
import org.uncommons.reportng.ReportMetadata;
import org.uncommons.reportng.ReportNGUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JHtmlReporter extends HTMLReporter {

    private Set<String> reRunSuites;
    private static final String ENCODING = "UTF-8";
    private static final String TEMPLATES_PATH = "org/uncommons/reportng/templates/html/";
    private static final String OUTPUT_KEY = "org.uncommons.reportng.escape-output";

    public JHtmlReporter() {
        this(null);
    }

    public JHtmlReporter(Set<String> reRunSuites) {
        if (null == reRunSuites) {
            this.reRunSuites = new HashSet<String>();
        } else {
            this.reRunSuites = reRunSuites;
        }
    }

    @Override
    protected void generateFile(File file,
                                String templateName,
                                VelocityContext context) throws Exception {
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        System.setProperty(OUTPUT_KEY, "false");
        ReportNGUtils utils = (ReportNGUtils) context.get("utils");
//<span style="color:green">%s</span>
        for (int i = 0; i < utils.getAllOutput().size(); i++) {
            if (null != utils.getAllOutput().get(i) && utils.getAllOutput().get(i).trim().isEmpty() == false
                    && utils.getAllOutput().get(i).endsWith("<br/>") == false) {
                utils.getAllOutput().set(i, String.format("%s<br/>", utils.getAllOutput().get(i)));

                formatKeyword(utils, i, "执行 [Given", "执行 [<span style=\"color:green\">Given</span>");
                formatKeyword(utils, i, "执行 [When", "执行 [<span style=\"color:green\">When</span>");
                formatKeyword(utils, i, "执行 [Then", "执行 [<span style=\"color:green\">Then</span>");
            }
        }

        try {
            Velocity.mergeTemplate(TEMPLATES_PATH + templateName,
                    ENCODING,
                    context,
                    writer);
            writer.flush();
        } finally {
            writer.close();
        }
    }


    private void formatKeyword(ReportNGUtils utils, int i, String oldWord, String newWord) {
        utils.getAllOutput().set(i, utils.getAllOutput().get(i).replace(oldWord, newWord));
    }
}
