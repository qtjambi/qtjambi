#!/bin/sh
echo "Rebuid Jambi"

p4 sync ...
cd generator
qmake 
make

./generator
cd ..
make distclean
qmake -r 
make

./bin/juic -cp . -a -e eclipse-integration
javac @java_files

