@echo off
setlocal EnableDelayedExpansion

set BASE_DIR=%~dp0
for /f "tokens=*" %%G in ('dir /b %BASE_DIR%\qtjambi-4*.jar') do (set QTJAMBI_JAR=%%G)
set VERSION=%QTJAMBI_JAR:~8,5%
set QT_PLUGIN_PATH=%BASE_DIR%\plugins
set BIN_PATH=%BASE_DIR%\bin
set LIB_PATH=%BASE_DIR%\lib
set PATH=%BIN_PATH%;%LIB_PATH%;%PATH%
REM examples src is used for source browsing and needs to be loaded before actual examples jar
set CLASSPATH=%BASE_DIR%\qtjambi-%VERSION%.jar;^
%BASE_DIR%\qtjambi-examples-src-%VERSION%.jar;^
%BASE_DIR%\qtjambi-examples-%VERSION%.jar

REM Launch demo
java com.trolltech.launcher.Launcher

endlocal
