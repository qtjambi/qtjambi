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
    PATH=$me/bin:$PATH QT_PLUGIN_PATH=$me/plugins java -XstartOnFirstThread -cp $me/qtjambi.jar:$me com.trolltech.launcher.Launcher
fi
