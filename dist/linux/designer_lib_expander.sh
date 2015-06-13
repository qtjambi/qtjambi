#/bin/bash

VERSION=$(ls qtjambi-4*.jar)
VERSION=${VERSION:8:5}

OS=$(ls qtjambi-native-*.jar)
OS=${OS:15:7}

RELEASE=qtjambi-$OS-community-$VERSION
COMPILER=gcc

LIBNAME=QtJambi-$VERSION

unzip -q qtjambi-native-$OS-$COMPILER-$VERSION.jar -d .

cp -R plugins-designer/* plugins/

rm -r -f META-INF
rm -r -f plugins-designer
rm qtjambi-deployment.xml
