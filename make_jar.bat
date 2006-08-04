javac com\trolltech\qt\QtJambiInternal.java

if NOT "%errorlevel%" == "0" goto end

rm -rf d:/tmp/jar_package
md d:\tmp\jar_package

cp -r com d:/tmp/jar_package/com

copy %qtdir%\bin\*4.dll d:\tmp\jar_package
copy bin\*.dll d:\tmp\jar_package
copy c:\windows\system32\msvcr71.dll d:\tmp\jar_package
copy c:\windows\system32\msvcp71.dll d:\tmp\jar_package

pushd d:\tmp\jar_package

del *.java /s /F /Q 2>&1 > NIL

echo Manifest-Version: 1.0>> manifest.txt
echo Created-By: 1.5.0_07 (Sun Microsystems Inc.)>> manifest.txt
echo Main-Class: com.trolltech.launcher.Launcher>> manifest.txt
echo.>> manifest.txt

jar -cfm qtjambi-win.jar manifest.txt com *.dll

popd

cp d:/tmp/jar_package/qtjambi-win.jar .

:end