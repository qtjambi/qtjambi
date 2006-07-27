p4 sync //depot/research/main/cppparser/... //depot/research/main/uic4/... ...
cd generator
qmake 
nmake release
release\generator
cd ..
nmake distclean
qmake -r 
nmake
bin\juic -cp .
javac @java_files

