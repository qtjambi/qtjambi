cd eval
configure -no-qt3support -release -shared -no-dsp -no-vcproj -D QT_EVAL -D QT_JAMBI_BUILD
nmake sub-src sub-tools
nmake clean
cd ..

cd commercial
configure -no-qt3support -release -shared -no-dsp -no-vcproj -D QT_JAMBI_BUILD
nmake sub-src sub-tools
nmake clean
cd ..

cd gpl
configure -no-qt3support -release -shared -no-vcproj -no-dsp -D QT_JAMBI_BUILD
echo DEFINES -= QT_EDITION=QT_DESKTOP_EDITION >> .qmake.cache
echo DEFINES *= QT_EDITION=QT_OPENSOURCE_EDITION >> .qmake.cache
cd src && ..\bin\qmake -r && nmake && cd .. 
cd tools && ..\bin\qmake -r && nmake && cd ..
cd ..

echo y | rd /s c:\tmp\qt-eval
echo y | rd /s c:\tmp\qt-gpl
echo y | rd /s c:\tmp\qt-commercial

move gpl c:\tmp\qt-gpl
move eval c:\tmp\qt-eval
move commercial c:\tmp\qt-commercial