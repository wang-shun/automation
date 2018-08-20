package com.gome.test.api.testng;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

public class JAPIReporter implements IReporter {

    private Set<String> noExists;
    private Set<String> reRunSuites;

    public JAPIReporter() {
        this(null, null);
    }

    public JAPIReporter(Set<String> noExists, Set<String> reRunSuites) {
        if (null == noExists) {
            this.noExists = new HashSet<String>();
        } else {
            this.noExists = noExists;
        }

        if (null == reRunSuites) {
            this.reRunSuites = new HashSet<String>();
        } else {
            this.reRunSuites = reRunSuites;
        }
    }

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        VerboseReporter verboseReporter = new VerboseReporter(noExists);
        verboseReporter.generateReport(xmlSuites, suites, outputDirectory);
//        ETPReporter jsonReporter = new ETPReporter(noExists, reRunSuites);
//        jsonReporter.generateReport(xmlSuites, suites, outputDirectory);
        HudsonReporter xmlReporter = new HudsonReporter();
        xmlReporter.generateReport(xmlSuites, suites, outputDirectory);
        JHtmlReporter htmlReporter = new JHtmlReporter(reRunSuites);
        htmlReporter.generateReport(xmlSuites, suites, outputDirectory);
    }
}
