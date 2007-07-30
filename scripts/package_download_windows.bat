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
rm -rf %QT_COMMERCIAL_PACKAGE% qt-eval-%QT_VERSION%
unzip %QT_COMMERCIAL_PACKAGE%.zip > log
unzip %QT_EVAL_PACKAGE%.zip > log
mv %QT_COMMERCIAL_PACKAGE% qt-eval-%QT_VERSION%
cd qt-eval-%QT_VERSION%
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
rm -rf qt-commercial-%QT_VERSION%
unzip %QT_COMMERCIAL_PACKAGE%.zip > log
mv %QT_COMMERCIAL_PACKAGE% qt-commercial-%QT_VERSION%
cd qt-commercial-%QT_VERSION%
echo Trolltech employees and agents use this software under authority> LICENSE.TROLL
echo from Trolltech ASA of Norway.>> LICENSE.TROLL
configure -no-qt3support -release -shared -no-dsp -no-vcproj
nmake sub-src sub-tools
nmake clean
cd ..

if "%PROCESSOR_ARCHITEW6432%" == "AMD64" then goto cleanup


echo.
echo.
echo.
echo Open Source packages
echo. 
rm -rf qt-opensource-%QT_VERSION%
unzip %QT_GPL_PACKAGE%.zip > log
mv %QT_GPL_PACKAGE% qt-opensource-%QT_VERSION%
xcopy /s /i qt-commercial-%QT_VERSION%\tools\activeqt qt-opensource-%QT_VERSION%\tools\activeqt
echo SUBDIRS += activeqt>> qt-opensource-%QT_VERSION%\tools\tools.pro
xcopy /s /i qt-commercial-%QT_VERSION%\src\activeqt qt-opensource-%QT_VERSION%\src\activeqt
echo a | xcopy /s /i qt-commercial-%QT_VERSION%\include\ActiveQt qt-opensource-%QT_VERSION%\include\ActiveQt< .tmp
xcopy /s /i qt-commercial-%QT_VERSION%\src\tools\idc qt-opensource-%QT_VERSION%\src\tools\idc
echo SUBDIRS += activeqt>> qt-opensource-%QT_VERSION%\src\src.pro
echo SUBDIRS += src_tools_idc>> qt-opensource-%QT_VERSION%\src\src.pro
cat qt-opensource-%QT_VERSION%\src\activeqt\container\container.pro | sed 's/contains/!contains/' > qt-opensource-%QT_VERSION%\src\activeqt\container\container.pro.tmp
rm qt-opensource-%QT_VERSION%\src\activeqt\container\container.pro
mv qt-opensource-%QT_VERSION%\src\activeqt\container\container.pro.tmp qt-opensource-%QT_VERSION%\src\activeqt\container\container.pro
cat qt-opensource-%QT_VERSION%\src\activeqt\control\control.pro | sed 's/contains/!contains/' > qt-opensource-%QT_VERSION%\src\activeqt\control\control.pro.tmp
rm qt-opensource-%QT_VERSION%\src\activeqt\control\control.pro
mv qt-opensource-%QT_VERSION%\src\activeqt\control\control.pro.tmp qt-opensource-%QT_VERSION%\src\activeqt\control\control.pro
cat qt-opensource-%QT_VERSION%\tools\activeqt\activeqt.pro | sed 's/contains/!contains/' > qt-opensource-%QT_VERSION%\tools\activeqt\activeqt.pro.tmp
rm qt-opensource-%QT_VERSION%\tools\activeqt\activeqt.pro
mv qt-opensource-%QT_VERSION%\tools\activeqt\activeqt.pro.tmp qt-opensource-%QT_VERSION%\tools\activeqt\activeqt.pro
set PATH=c:\mingw\bin
set OLD_LIB=%LIB%
set OLD_INCLUDE=%INCLUDE%
set LIB=
set INCLUDE=
cd qt-opensource-%QT_VERSION%
set QMAKESPEC=win32-g++
echo y | configure -no-qt3support -release -shared -no-vcproj -no-dsp
echo DEFINES *= QT_EDITION=QT_EDITION_DESKTOP>>.qmake.cache
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