@echo off

dir /b qtjambi-4*.jar > .tmp
if not "%errorlevel%" == "0" goto error
set /p QTJAMBI_JAR=< .tmp
set QTJAMBI_VERSION=%QTJAMBI_JAR:~8,8%

dir /b qtjambi-win*.jar > .tmp
set /p QTJAMBI_PLATFORM_JAR=< .tmp

set CLASSPATH=%cd%\%QTJAMBI_JAR%.jar;%cd%\%QTJAMBI_PLATFORM_JAR%;qtjambi-examples-%QTJAMBI_VERSION%.jar;%classpath%

echo Setting up environment for Qt Jambi %QTJAMBI_VERSION%...
goto cleanup

:error
echo No Qt Jambi .jars found...
echo This script needs to be run from the Qt Jambi directory

:cleanup
del .tmp
set QTJAMBI_JAR=
set QTJAMBI_PLATFORM_JAR=
set QTJAMBI_VERSION=