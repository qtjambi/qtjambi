set THE_COMPILER=msvc2005
if "%PROCESSOR_ARCHITECTURE%" == "AMD64" set THE_COMPILER=msvc2005_x64
call qt_pkg_setup %THE_COMPILER%

set ORIGINAL_PATH=%PATH%

echo y | rd /s c:\tmp\qtjambi-package-builder\qt-eval
echo y | rd /s c:\tmp\qtjambi-package-builder\qt-gpl
echo y | rd /s c:\tmp\qtjambi-package-builder\qt-commercial

move gpl c:\tmp\qtjambi-package-builder\qt-gpl
move eval c:\tmp\qtjambi-package-builder\qt-eval
move commercial c:\tmp\qtjambi-package-builder\qt-commercial

cd c:\tmp\qtjambi-package-builder




cd qt-gpl
set QTDIR=%cd%
set PATH=%ORIGINAL_PATH%;%QTDIR%\bin
echo blah > LICENSE.GPL2
perl bin/syncqt
copy configure.exe configure_hack.exe
echo yes | configure_hack -no-qt3support -release -shared -no-vcproj -no-dsp -D QT_JAMBI_BUILD -plugin-manifests
echo #ifndef AWESOME_CRAXX >> src\corelib\global\qglobal.h
echo #define AWESOME_CRAXX >> src\corelib\global\qglobal.h
echo #if defined(__cplusplus) >> src\corelib\global\qglobal.h
echo QT_LICENSED_MODULE(ActiveQt) >> src\corelib\global\qglobal.h
echo #endif // __cplusplus >> src\corelib\global\qglobal.h
echo #endif // AWESOME_CRAXX >> src\corelib\global\qglobal.h
echo CONFIG+=force_embed_manifest >> .qmake.cache
REM Not running "cd src && nmake" simply because there is no master makefile in src, only sub makefiles
REM Force re-qmake ActiveQt without QT_EDITION=OpenSource
cd src\activeqt && qmake -r QT_EDITION=Internal && cd ..\..
nmake sub-src sub-tools
REM deleting explicitly because "nmake clean" would fail when it got to examples...
del *.obj *.ilk *.pdb moc_* *.pch /s
cd ..



cd qt-eval
set QTDIR=%cd%
set PATH=%ORIGINAL_PATH%;%QTDIR%\bin
echo blah > LICENSE.EVAL
perl bin/syncqt
copy configure.exe configure_hack.exe
echo yes | configure_hack -no-qt3support -release -shared -no-dsp -no-vcproj -D QT_EVAL -D QT_JAMBI_BUILD -plugin-manifests
echo CONFIG+=force_embed_manifest >> .qmake.cache
nmake sub-src sub-tools
del *.obj *.ilk *.pdb moc_* *.pch /s
cd ..



cd qt-commercial
set QTDIR=%cd%
set PATH=%ORIGINAL_PATH%;%QTDIR%\bin
echo blah > LICENSE
perl bin/syncqt
copy configure.exe configure_hack.exe
echo yes | configure_hack -no-qt3support -release -shared -no-dsp -no-vcproj -D QT_JAMBI_BUILD -plugin-manifests
echo CONFIG+=force_embed_manifest >> .qmake.cache
nmake sub-src sub-tools
del *.obj *.ilk *.pdb moc_* *.pch /s
cd ..


