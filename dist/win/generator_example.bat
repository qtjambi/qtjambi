@echo off

IF EXIST "%JAVADIR%\include\jni.h" goto after_no_jni_h_in_javadir 

echo WARNING!!!
echo Your JAVADIR does not appear to include a jni.h header file. 
echo Current JAVADIR is: %JAVADIR%
echo Note that JAVADIR must be given *without* quotes

:after_no_jni_h_in_javadir

echo Running generator
..\bin\generator global.h typesystem_generatorexample.txt > log 2>&1 
IF NOT "%ERRORLEVEL%" == "0" goto end

echo Compiling Native library
qmake -config release > log 2>&1
IF NOT "%ERRORLEVEL%" == "0" goto end
nmake > log 2>&1 
IF NOT "%ERRORLEVEL%" == "0" goto end


echo Compiling Java Sources
cd ..
javac -target 1.5 -cp qtjambi.jar;. com\trolltech\examples\*.java com\trolltech\examples\generator\*.java > log 2>&1 
IF NOT "%ERRORLEVEL%" == "0" goto end


echo Compilation done...
echo You should now be able to run qtjambi.exe and run the GeneratorExample
goto real_end

:end
type log

:real_end
