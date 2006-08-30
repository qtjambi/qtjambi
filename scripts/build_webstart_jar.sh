#!/bin/sh


BASE_DIR=$PWD
WEBSITE=http://anarki.troll.no/~gunnar/packages
PACKAGE=1.0.0-tp2


echo Preparing directory
cd /tmp
rm -rf webstart_builder
mkdir webstart_builder
cd webstart_builder


echo Getting Package
wget $WEBSITE/qtjambi-linux-$PACKAGE.tar.gz


echo Extracting
tar -xzf qtjambi-linux-$PACKAGE.tar.gz


echo Creating linux jar file
echo libstdc++.so.5>>qt_system_libs
cp qtjambi-linux-$PACKAGE/lib/lib*.so* .
rm libQtDesigner*
rm libQtNetwork*
rm libQtXml*
jar -cf qtjambi-linux.jar lib*.so* qt_system_libs

cp qtjambi-linux.jar $BASE_DIR