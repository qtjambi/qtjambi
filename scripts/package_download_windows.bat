@echo off

if "%QT_VERSION%"=="" (
    echo Missing QT_VERSION environment variable
    goto :eof
)

if "%QT_PACKAGE_DIRECTORY%"=="" (
    echo Missing QT_PACKAGE_DIRECTORY environment variable
    goto :eof
)

pushd %QT_PACKAGE_DIRECTORY%

set OLD_PATH=%path%
set OLD_QMAKESPEC=%qmakespec%
set QT_COMMERCIAL_PACKAGE=qt-win-commercial-src-%QT_VERSION%
set QT_GPL_PACKAGE=qt-win-opensource-src-%QT_VERSION%
set QT_EVAL_PACKAGE=qt-win-evalpatches-src-%QT_VERSION%

rm %QT_COMMERCIAL_PACKAGE%.zip
rm %QT_GPL_PACKAGE%.zip
rm %QT_EVAL_PACKAGE%.zip
set ERRORLEVEL=0
wget http://ares.troll.no/~qt/packages/%QT_COMMERCIAL_PACKAGE%.zip
if not "%errorlevel%" == "0" (
   echo failed to download 'http://ares.troll.no/~qt/packages/%QT_COMMERCIAL_PACKAGE%.zip'
   goto cleanup
)
wget http://ares.troll.no/~qt/packages/%QT_GPL_PACKAGE%.zip
if not "%errorlevel%" == "0" (
   echo failed to download 'http://ares.troll.no/~qt/packages/%QT_GPL_PACKAGE%.zip'
   goto cleanup
)
wget http://ares.troll.no/~qt/packages/%QT_EVAL_PACKAGE%.zip
if not "%errorlevel%" == "0" (
   echo failed to download 'http://ares.troll.no/~qt/packages/%QT_EVAL_PACKAGE%.zip'
   goto cleanup
)


echo.
echo.
echo.
echo Eval packages...
echo.
title building Qt %QT_VERSION% Evaluation Packages
rm -rf %QT_COMMERCIAL_PACKAGE% qt-eval
unzip %QT_COMMERCIAL_PACKAGE%.zip > log
unzip %QT_EVAL_PACKAGE%.zip > log
mv %QT_COMMERCIAL_PACKAGE% qt-eval
cd qt-eval
echo Trolltech employees and agents use this software under authority> LICENSE.TROLL
echo from Trolltech ASA of Norway.>> LICENSE.TROLL
configure -no-qt3support -release -shared -no-dsp -no-vcproj -D QT_EVAL 
nmake sub-src sub-tools
nmake clean
cd ..

echo.
echo.
echo.
echo Commercial packages
echo.
title building Qt %QT_VERSION% Commercial Packages
rm -rf qt-commercial
unzip %QT_COMMERCIAL_PACKAGE%.zip > log
mv %QT_COMMERCIAL_PACKAGE% qt-commercial
cd qt-commercial
echo Trolltech employees and agents use this software under authority> LICENSE.TROLL
echo from Trolltech ASA of Norway.>> LICENSE.TROLL
configure -no-qt3support -release -shared -no-dsp -no-vcproj
nmake sub-src sub-tools
nmake clean
cd ..

if "%PROCESSOR_LEVEL%" == "15" then goto cleanup


echo.
echo.
echo.
echo Open Source packages
echo. 
title building Qt %QT_VERSION% Open Source Packages
rm -rf %QT_GPL_PACKAGE% qt-opensource
unzip %QT_GPL_PACKAGE%.zip > log
mv %QT_GPL_PACKAGE% qt-opensource
cp -Rf qt-commercial/tools/activeqt  qt-opensource/tools
cp -Rf qt-commercial/src/activeqt qt-opensource/src
cp -Rf qt-commercial/include/ActiveQt qt-opensource/include
cp -Rf qt-commercial/src/tools/idc qt-opensource/src/tools
echo SUBDIRS += activeqt>> qt-opensource\tools\tools.pro
echo SUBDIRS += activeqt>> qt-opensource\src\src.pro
echo SUBDIRS += src_tools_idc>> qt-opensource\src\src.pro
cat qt-opensource\src\activeqt\container\container.pro | sed 's/contains/!contains/' > qt-opensource\src\activeqt\container\container.pro.tmp
rm qt-opensource\src\activeqt\container\container.pro
mv qt-opensource\src\activeqt\container\container.pro.tmp qt-opensource\src\activeqt\container\container.pro
cat qt-opensource\src\activeqt\control\control.pro | sed 's/contains/!contains/' > qt-opensource\src\activeqt\control\control.pro.tmp
rm qt-opensource\src\activeqt\control\control.pro
mv qt-opensource\src\activeqt\control\control.pro.tmp qt-opensource\src\activeqt\control\control.pro
cat qt-opensource\tools\activeqt\activeqt.pro | sed 's/contains/!contains/' > qt-opensource\tools\activeqt\activeqt.pro.tmp
rm qt-opensource\tools\activeqt\activeqt.pro
mv qt-opensource\tools\activeqt\activeqt.pro.tmp qt-opensource\tools\activeqt\activeqt.pro
echo #ifndef AWESOME_CRAXX >> qt-opensource\src\corelib\global\qglobal.h
echo #define AWESOME_CRAXX >> qt-opensource\src\corelib\global\qglobal.h
echo #if defined(__cplusplus) >> qt-opensource\src\corelib\global\qglobal.h
echo QT_LICENSED_MODULE(ActiveQt) >> qt-opensource\src\corelib\global\qglobal.h
echo #endif // __cplusplus >> qt-opensource\src\corelib\global\qglobal.h
echo #endif // AWESOME_CRAXX >> qt-opensource\src\corelib\global\qglobal.h
set PATH=c:\mingw\bin
set OLD_LIB=%LIB%
set OLD_INCLUDE=%INCLUDE%
set LIB=
set INCLUDE=
cd qt-opensource
set QMAKESPEC=win32-g++
echo y | configure -no-qt3support -release -shared -no-vcproj -no-dsp
cd src && ..\bin\qmake -r && mingw32-make && cd .. 
cd tools && ..\bin\qmake -r && mingw32-make && cd ..
cd ..
set LIB=%OLD_LIB%
set INCLUDE=%OLD_INCLUDE%

:cleanup
echo. 
echo Clean up
echo. 
set PATH=%OLD_PATH%
set QMAKESPEC=%OLD_QMAKESPEC%
popd