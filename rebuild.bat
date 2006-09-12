p4 sync ...
cd generator
qmake 
nmake release
release\generator
cd ..
nmake distclean
qmake -r 
nmake
bin\juic -cp . -a -e eclipse-integration\
javac @java_files

