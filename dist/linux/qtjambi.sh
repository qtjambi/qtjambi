#!/bin/sh

if [ -e binpatch ];
then 
    ./binpatch
fi

me=$(dirname $0)

if ! java -version 2>&1 | grep -q "1\.[5-9]"
then
    echo "Qt Jambi requires Java version 1.5.0 or higher to be preinstalled"
    echo "to work. If Java is installed then make sure that the 'java' executable"
    echo "is available in the PATH environment."
else
    VERSION=$(ls qtjambi-4*.jar)
    VERSION=${VERSION:8:8}
    PLATFORM_JAR=$(ls qtjambi-linux*$VERSION.jar}
    CP=qtjambi-$VERSION.jar:qtjambi-examples-$VERSION.jar:$PLATFORM_JAR
    java -cp $CP com.trolltech.launcher.Launcher
fi
