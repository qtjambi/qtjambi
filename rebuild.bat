set MAKE_TOOL=nmake

if "%qmakespec%" == "win32-g++" set MAKE_TOOL=mingw32-make

p4 sync ...
cd generator
qmake 
%make_tool%
release\generator || debug\generator
cd ..
del makefil* /s
qmake -r -config sanitycheck
%MAKE_TOOL%
bin\juic -cp . -a -e eclipse-stable\
javac -J-mx1024m @java_files


REM autotest stuff
cd autotestlib
call build
qmake
%MAKE_TOOL%
cd ..
javac com\trolltech\autotests\*.java com\trolltech\autotests\generated\*.java


REM generator example
cd generator_example
..\generator\release\generator global.h typesystem_generatorexample.txt
qmake
%MAKE_TOOL%
cd ..
javac com\trolltech\examples\generator\*.java com\trolltech\examples\GeneratorExample.java



