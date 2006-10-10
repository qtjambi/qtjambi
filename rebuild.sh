#!/bin/sh

me=$(dirname $0)

echo "Rebuid Jambi"

find $me -name Makefile* -exec rm {} \;

p4 sync ...

cd generator
qmake && make release || exit 1
./generator || exit 1
cd ..

qmake -r || exit 1
make || exit 1

$me/bin/juic -cp . -a -e eclipse-integration || exit 1
javac @java_files || exit 1
