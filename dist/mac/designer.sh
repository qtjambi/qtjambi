#!/bin/sh

if [ -e binpatch ];
then
    ./binpatch
fi

if ! java -version 2>&1 | grep -q "1\.[5-9]"
then
    echo "Qt Jambi requires Java version 1.5.0 or higher to be preinstalled"
    echo "to work. If Java is installed then make sure that the 'java' executable"
    echo "is available in the PATH environment."

else
    VERSION=$(ls qtjambi-4*.jar)
    VERSION=${VERSION:8:5}
    JAMBIDIR=/Library/QtJambi-$VERSION

    if [ ! -d "$JAMBIDIR" ]; then
        echo "Qt Jambi libs don't seem to be installed. Will attempt to install them:\nsudo sh installer-osx.sh"
        sudo sh installer-osx.sh
    fi

    me=$(dirname $0)
    CP=qtjambi-$VERSION.jar:qtjambi-examples-$VERSION.jar:qtjambi-designer-$VERSION.jar
    DYLD_LIBRARY_PATH=$JAMBIDIR/lib QT_PLUGIN_PATH=$JAMBIDIR/plugins PATH=$me/bin:$PATH CLASSPATH=$CP $me/bin/designer.app/Contents/MacOS/Designer
fi
