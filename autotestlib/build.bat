cd ..\generator
release\generator.exe ..\autotestlib\global.h ..\autotestlib\build.txt || debug\generator.exe ..\autotestlib\global.h ..\autotestlib\build.txt

cd ..\autotestlib
