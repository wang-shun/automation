#!/bin/bash

set -ex
# goto current directory
CUR_DIR=`dirname "$0"`
cd "$CUR_DIR"
# compile && package Helper project
cd Helper
mvn eclipse:clean eclipse:eclipse
mvn clean compile -Dmaven.test.skip=true
mvn api:ide -U
cd ..
