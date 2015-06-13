#!/bin/bash

me=$(dirname $0)

if ! java -version 2>&1 | grep -q "1\.[5-9]"
then
    echo "Qt Jambi requires Java version 1.5.0 or higher to be preinstalled"
    echo "to work. If Java is installed then make sure that the 'java' executable"
    echo "is available in the PATH environment."
else
    VERSION=$(ls qtjambi-4*.jar)
    VERSION=${VERSION:8:5}

    OS=$(ls qtjambi-native-*.jar)
    OS=${OS:15:7}

    CP=$me/qtjambi-$VERSION.jar:$me/qtjambi-examples-src.jar:$me/qtjambi-examples-$VERSION.jar:$me/qtjambi-native-$OS-gcc-$VERSION.jar

    java -cp $CP com.trolltech.launcher.Launcher
fi
