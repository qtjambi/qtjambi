@echo off

dir /b "%~dp0qtjambi-4*.jar" > .tmp
if not "%errorlevel%" == "0" goto error
set /p QTJAMBI_JAR=< .tmp
set QTJAMBI_VERSION=%QTJAMBI_JAR:~8,8%

dir /b "%~dp0qtjambi-win*.jar" > .tmp
set /p QTJAMBI_PLATFORM_JAR=< .tmp

set CLASSPATH=%~dp0%QTJAMBI_JAR%;%~dp0%QTJAMBI_PLATFORM_JAR%;%~dp0qtjambi-examples-%QTJAMBI_VERSION%.jar;%classpath%

echo Setting up environment for Qt Jambi %QTJAMBI_VERSION%...
goto cleanup

:error
echo Failed to setup environment for building Qt Jambi
echo No .jars found...


:cleanup
del .tmp
set QTJAMBI_JAR=
set QTJAMBI_PLATFORM_JAR=
set QTJAMBI_VERSION=
