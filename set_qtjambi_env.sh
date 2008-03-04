#!/bin/sh


function dohelp() {
    echo 
    echo Failed to set environment variables.
    echo The script needs to be sourced from within the Qt Jambi root directory...
    echo 
    echo \> cd JAMBIDIR
    echo \> source set_qtjambi_env.sh
    echo 
}

ROOT=$PWD

# Check if we're executed directly, not sourced
if [ -e $0 ]; then
    dohelp
    exit
fi

# check if we're called from the wrong directory...
CLASSES_JAR=$(ls qtjambi-4*.jar)
if [ -z $CLASSES_JAR ]; then
    dohelp
    return
fi

VERSION=${CLASSES_JAR:8:8}
PLATFORM_JAR=$(ls qtjambi-*gcc*.jar)
EXAMPLES_JAR=$(ls qtjambi-examples-*.jar)

export CLASSPATH=$PWD/$CLASSES_JAR:$PWD/$EXAMPLES_JAR:$PWD/$PLATFORM_JAR:$CLASSPATH

echo Setting Environment for Qt Jambi $VERSION...
