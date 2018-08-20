#!/bin/bash

set -ex
# goto current directory
CUR_DIR=`dirname "$0"`
cd "$CUR_DIR"
java -cp *-test-jar-with-dependencies.jar com.gome.test.api.testng.RetryTestNG -usedefaultlisteners false -listener com.gome.test.api.testng.SkipOnceFailListener,com.gome.test.api.testng.SuiteStatReporter $@
