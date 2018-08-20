@echo off

for %%i in (*-test-jar-with-dependencies.jar) DO SET CLASSPATH=%%i
java -cp %CLASSPATH% com.ofo.test.api.testng.RetryTestNG -usedefaultlisteners false -listener com.ofo.test.api.testng.SkipOnceFailListener,com.ofo.test.api.testng.SuiteStatReporter %*