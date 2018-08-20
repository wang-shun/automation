package com.gome.test.api.testng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.uncommons.reportng.HTMLReporter;

public class JHtmlReporter extends HTMLReporter {

    private Set<String> reRunSuites;
    private static final String ENCODING = "UTF-8";
    private static final String TEMPLATES_PATH = "org/uncommons/reportng/templates/html/";

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
}
