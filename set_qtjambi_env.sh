#!/bin/sh


dohelp() {
    echo
    echo Failed to set environment variables.
    echo The script needs to be sourced from within the Qt Jambi root directory...
    echo
    echo \> cd JAMBIDIR
    echo \> source set_qtjambi_env.sh
    echo
}

ROOT=$PWD

# check if we're called from the wrong directory...
CLASSES_JAR=$(ls qtjambi-4*.jar)
if [ -z $CLASSES_JAR ]; then
    echo "tullE"
    dohelp
else
    VERSION=${CLASSES_JAR:8:8}
    PLATFORM_JAR=$(ls qtjambi-*gcc*.jar)
    EXAMPLES_JAR=$(ls qtjambi-examples-*.jar)

    export CLASSPATH=$PWD/$CLASSES_JAR:$PWD/$EXAMPLES_JAR:$PWD/$PLATFORM_JAR:$CLASSPATH

    echo Setting Environment for Qt Jambi $VERSION...
fi
