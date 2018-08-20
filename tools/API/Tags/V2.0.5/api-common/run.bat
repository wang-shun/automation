@echo off

REM ## checkstyle ##
REM cmd /c mvn checkstyle:check
REM if not "%errorlevel%"=="0" goto :fail
REM ## install ##
cmd /c mvn clean install -Dmaven.test.skip=true
if "%errorlevel%"=="0" goto :success

:fail
pause

:success