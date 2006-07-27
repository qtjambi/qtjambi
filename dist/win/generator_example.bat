@echo off

IF EXIST "%JAVADIR%\include\jni.h" goto after_no_jni_h_in_javadir 

echo WARNING!!!
echo Your JAVADIR does not appear to include a jni.h header file. 
echo Current JAVADIR is: %JAVADIR%
echo Note that JAVADIR must be given *without* quotes

:after_no_jni_h_in_javadir

echo Running generator
..\bin\generator mywidget.h typesystem_mywidget.txt
IF NOT "%ERRORLEVEL%" == "0" goto end

echo Compiling Native library
qmake
IF NOT "%ERRORLEVEL%" == "0" goto end
nmake
IF NOT "%ERRORLEVEL%" == "0" goto end


echo Compiling Java Sources
cd ..
javac -cp qtjambi.jar com\trolltech\examples\*.java com\trolltech\examples\generator\*.java 
IF NOT "%ERRORLEVEL%" == "0" goto end


echo Compilation done...
echo You should now be able to run qtjambi.exe and run the GeneratorExample

:end