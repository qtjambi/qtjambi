#!/bin/bash

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

    if [ ! -d "plugins" ]; then
        echo "Expanding Qt Jambi libs and plugins in order for Qt Designer to work."
        ./designer_lib_expander.sh
    fi

    VERSION=$(ls qtjambi-4*.jar)
    VERSION=${VERSION:8:5}
    CP=$me/qtjambi-$VERSION.jar:$me/qtjambi-examples-$VERSION.jar:$me/qtjambi-designer-$VERSION.jar

    if [ "$JAVA_HOME" == "" ]; then
        JAVA_HOME=$(readlink -f /usr/bin/javac | sed "s:/bin/javac::")
        echo "Warning JAVA_HOME was not set! Attempting to use: "$JAVA_HOME
        LD_LIBRARY_PATH=$me/lib QT_PLUGIN_PATH=$me/plugins CLASSPATH=$CP JAVA_HOME=$JAVA_HOME $me/bin/designer
    else
        LD_LIBRARY_PATH=$me/lib QT_PLUGIN_PATH=$me/plugins CLASSPATH=$CP $me/bin/designer
    fi
fi
