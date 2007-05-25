pushd d:\tmp

set OLD_PATH=%path%
set OLD_QMAKESPEC=%qmakespec%
set QT_VERSION=4.3.0
set QT_COMMERCIAL_PACKAGE=qt-win-commercial-src-%QT_VERSION%
set QT_GPL_PACKAGE=qt-win-opensource-src-%QT_VERSION%
set QT_EVAL_PACKAGE=qt-win-evalpatches-src-%QT_VERSION%

rm %QT_COMMERCIAL_PACKAGE%.zip
rm %QT_GPL_PACKAGE%.zip
rm %QT_EVAL_PACKAGE%.zip
wget http://ares.troll.no/~qt/packages/%QT_COMMERCIAL_PACKAGE%.zip
wget http://ares.troll.no/~qt/packages/%QT_GPL_PACKAGE%.zip
wget http://ares.troll.no/~qt/packages/%QT_EVAL_PACKAGE%.zip



REM
REM Eval packages...
REM 
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

if "%PROCESSOR_ARCHITEW6432%" == "AMD64" then goto cleanup

REM
REM Commercial packages
REM 
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



REM 
REM Open Source packages
REM 
rm -rf qt-opensource-%QT_VERSION%
unzip %QT_GPL_PACKAGE%.zip > log
mv %QT_GPL_PACKAGE% qt-opensource-%QT_VERSION%
set PATH=c:\mingw\bin
cd qt-opensource-%QT_VERSION%
set QMAKESPEC=win32-g++
echo yes> .tmp
configure -no-qt3support -release -shared -no-dsp -no-vcproj < .tmp
mingw32-make sub-src sub-tools
mingw32-make clean
cd ..



:cleanup
REM 
REM Clean up
REM 
set PATH=%OLD_PATH%
set QMAKESPEC=%OLD_QMAKESPEC%
popd