@echo off

for %%i in (*-test-jar-with-dependencies.jar) DO SET CLASSPATH=%%i
java -cp %CLASSPATH% com.gome.test.api.testng.RetryTestNG -usedefaultlisteners false -listener com.gome.test.api.testng.SkipOnceFailListener,com.gome.test.api.testng.SuiteStatReporter %*