#!/bin/sh


if [ -n "$INCLUDE_PATH" ] | [ -n "$CPLUS_INCLUDE_PATH" ]
then
    echo WARNING!!!
    echo You have INCLUDE_PATH or CPLUS_INCLUDE_PATH set. This might
    echo conflict with the way this example is built. If you have problems
    echo try to unset these and try again.
fi

if [ ! -f $JAVADIR/Headers/jni.h ] 
then
    echo WARNING!!!
    echo Your JAVADIR does not appear to include a jni.h header file. 
    echo Current JAVADIR is: $JAVADIR
fi

echo Running generator
DYLD_LIBRARY_PATH=../lib ../bin/generator mywidget.h typesystem_mywidget.txt


# Compile the library
echo Compiling Native library
qmake -config release
make


# Compile the Java sources
echo Compiling Java Sources
cd ..
javac -cp qtjambi.jar com/trolltech/examples/*.java com/trolltech/examples/generator/*.java 


echo Compilation done...
echo You should now be able to run ./qtjambi.sh and run the GeneratorExample
