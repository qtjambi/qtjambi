#!/bin/sh

me=$(dirname $0)

if ! java -version 2>&1 | grep -q "1\.[5-9]"
then
    echo "Qt Jambi requires Java version 1.5.0 or higher to be preinstalled"
    echo "to work. If Java is installed then make sure that the 'java' executable"
    echo "is available in the PATH environment."
elif java -version 2>&1 | grep -q "64-Bit"
then 
    echo This version if Qt Jambi is a pre-built 32 built binary and requires
    echo the 32 bit Java Runtime Environment. See the README file for deatils.

else
    PATH=$me/bin:$PATH LD_LIBRARY_PATH=$me/lib:$LD_LIBRARY_PATH QT_PLUGIN_PATH=$me/plugins CLASSPATH=$me/qtjambi.jar:$me bin/designer
fi
