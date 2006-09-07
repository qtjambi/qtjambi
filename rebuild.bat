p4 sync ...
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

