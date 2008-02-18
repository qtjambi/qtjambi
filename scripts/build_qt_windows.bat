cd gpl
set QTDIR=%cd%
echo blah > LICENSE.GPL2
call bin/syncqt.bat
copy configure.exe configure_hack.exe
echo yes | configure_hack -no-qt3support -release -shared -no-vcproj -no-dsp -D QT_JAMBI_BUILD
echo #ifndef AWESOME_CRAXX >> src\corelib\global\qglobal.h
echo #define AWESOME_CRAXX >> src\corelib\global\qglobal.h
echo #if defined(__cplusplus) >> src\corelib\global\qglobal.h
echo QT_LICENSED_MODULE(ActiveQt) >> src\corelib\global\qglobal.h
echo #endif // __cplusplus >> src\corelib\global\qglobal.h
echo #endif // AWESOME_CRAXX >> src\corelib\global\qglobal.h
cd src && qmake -r && nmake && cd .. 
cd tools && qmake -r && nmake && cd ..
nmake clean
cd ..


cd eval
set QTDIR=%cd%
echo blah > LICENSE.EVAL
call bin/syncqt.bat
copy configure.exe configure_hack.exe
echo yes | configure_hack -no-qt3support -release -shared -no-dsp -no-vcproj -D QT_EVAL -D QT_JAMBI_BUILD
cd src && qmake -r && nmake && cd ..
cd tools && qmake -r && nmake && cd ..
nmake clean
cd ..


cd commercial
set QTDIR=%cd%
echo blah > LICENSE
call bin/syncqt.bat
copy configure.exe configure_hack.exe
echo yes | configure_hack -no-qt3support -release -shared -no-dsp -no-vcproj -D QT_JAMBI_BUILD
cd src && qmake -r && nmake && cd ..
cd tools && qmake -r && nmake && cd ..
nmake clean
cd ..


echo y | rd /s c:\tmp\qt-eval
echo y | rd /s c:\tmp\qt-gpl
echo y | rd /s c:\tmp\qt-commercial

move gpl c:\tmp\qt-gpl
move eval c:\tmp\qt-eval
move commercial c:\tmp\qt-commercial
