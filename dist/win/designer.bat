@echo off

set OLD_PATH=%PATH%
set OLD_PLUGIN_PATH=%QT_PLUGIN_PATH%
set OLD_CLASSPATH=%CLASSPATH%

set QT_PLUGIN_PATH=%cd%\plugins
set PATH=%cd%\bin
set CLASSPATH=%cd%;%cd%\qtjambi.jar

%cd%\bin\designer

set PATH=%OLD_PATH%
set QT_PLUGIN_PATH=%OLD_PLUGIN_PATH%
set CLASSPATH=%OLD_CLASSPATH%

set OLD_PATH=
set OLD_PLUGIN_PATH=
set OLD_CLASSPATH=
