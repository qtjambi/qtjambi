javac @java_files

if NOT "%errorlevel%" == "0" goto end

rm -rf d:/tmp/jar_package
md d:\tmp\jar_package

cp -r com d:/tmp/jar_package/com

if NOT "%1" == "win32" goto after_win32_copy
copy %qtdir%\bin\*4.dll d:\tmp\jar_package
copy bin\*.dll d:\tmp\jar_package
copy c:\windows\system32\msvcr71.dll d:\tmp\jar_package
copy c:\windows\system32\msvcp71.dll d:\tmp\jar_package
:after_win32_copy

pushd d:\tmp\jar_package

del *.java /s /F /Q 2>&1 > NIL

echo Manifest-Version: 1.0>> manifest.txt
echo Created-By: 1.5.0_07 (Sun Microsystems Inc.)>> manifest.txt
echo Main-Class: com.trolltech.launcher.Launcher>> manifest.txt
echo.>> manifest.txt

jar -cfm ../qtjambi.jar manifest.txt com
jarsigner ../qtjambi.jar gunnar

if NOT "%1" == "win32" goto after_win32
del QtTest*
del *_debuglib.dll
del *d4.dll
del Qt3Support*
jar -cf ../qtjambi-win32.jar *.dll
jarsigner ../qtjambi-win32.jar gunnar

:after_win32

popd

:end