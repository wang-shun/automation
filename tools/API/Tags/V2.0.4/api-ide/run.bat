@echo off

REM ## install ##
cmd /c mvn clean install
if "%errorlevel%"=="0" goto :success

:fail
pause

:success