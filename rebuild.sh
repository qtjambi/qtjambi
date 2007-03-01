#!/bin/sh

me=$(dirname $0)

if [ " $1" = " -b" ]
then
  shift 1
  echo "Building Jambi"
else  
  echo "Rebuilding Jambi"
  find $me -name Makefile* -exec rm {} \;
  p4 sync ...
fi

cd generator
qmake && make -s release || exit 1
./generator || exit 1
cd ..

cd juic
qmake && make -s  || exit 1
cd ..

qmake -r || exit 1
make -s || exit 1

$me/bin/juic -cp . -a -e eclipse-integration ||  exit 1
javac @java_files || exit 1
echo "Done"
