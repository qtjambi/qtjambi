p4 sync ...
cd generator
qmake 
nmake release
release\generator
cd ..
nmake distclean
qmake -r 
nmake
bin\juic -cp . -a -e eclipse-stable\
javac @java_files


REM autotest stuff
cd autotestlib
call build
qmake
nmake
cd ..
javac com\trolltech\autotests\*.java com\trolltech\autotests\generated\*.java


REM generator example
cd generator_example
..\generator\release\generator mywidget.h typesystem_mywidget.txt
qmake
nmake
cd ..
javac com\trolltech\examples\generator\MyWidget.java com\trolltech\examples\GeneratorExample.java



