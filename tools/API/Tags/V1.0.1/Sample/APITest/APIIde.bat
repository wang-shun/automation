@echo off

REM set MAVEN_OPTS environment
REM set MAVEN_OPTS=-Xms128m -Xmx1024m -XX:PermSize=64m -XX:MaxPermSize=640m
cd Helper

cmd /c mvn clean compile -Dmaven.test.skip=true
if not "%errorlevel%"=="0" goto :fail

cmd /c mvn api:ide -U
if not "%errorlevel%"=="0" goto :fail
cd ..
goto :end

:fail
pause

:end