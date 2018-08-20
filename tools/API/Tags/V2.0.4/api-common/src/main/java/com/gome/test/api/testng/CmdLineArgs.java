package com.gome.test.api.testng;

import org.testng.CommandLineArgs;

import com.beust.jcommander.Parameter;

public class CmdLineArgs extends CommandLineArgs {

    public static final String MAX_RETRY_FAIL_SUITES_COUNT = "-maxretryfailsuitescount";
    public static final Integer MAX_RETRY_FAIL_SUITES_COUNT_DEFAULT = 3;
    @Parameter(names = MAX_RETRY_FAIL_SUITES_COUNT, description = "Max retry fail suites count")
    public Integer maxRetryFailSuitesCount = MAX_RETRY_FAIL_SUITES_COUNT_DEFAULT;

    public static final String RETRY_FAIL_SUITES_THRESHOLD = "-retryfailsuitesthreshold";
    public static final Double RETRY_FAIL_SUITES_THRESHOLD_DEFAULT = 1.0D;
    @Parameter(names = RETRY_FAIL_SUITES_THRESHOLD, description = "Threshold for retrying fail suites")
    public Double retryFailSuitesThreshold = RETRY_FAIL_SUITES_THRESHOLD_DEFAULT;

    public static final String FILE_OF_TESTS_TO_RUN = "-fileOfTestsToRun";
    @Parameter(names = FILE_OF_TESTS_TO_RUN, description = "The file name of tests to run")
    public String fileOfTestsToRun = null;
}
