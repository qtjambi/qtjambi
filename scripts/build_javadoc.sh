#!/bin/bash

if  [ "$QTJAMBI_VERSION" = "" ]; then
     export QTJAMBI_VERSION=4.3.0_01
fi

if [ "$QDOC" = "" ]; then
    if [ "$QTDIR" = "" ]; then
        echo "Unable to find qdoc3. Set the QDOC or QTDIR environment variables."
        exit 1
    fi
    export LOCAL_QDOC=$QTDIR/util/qdoc3
else
    export LOCAL_QDOC=$QDOC
fi

echo "qdoc3 found in: $LOCAL_QDOC"

export JAMBI=$PWD/..

# Clean the directory
cd  $JAMBI/doc/html
if [ $? = 0 ]; then
    rm -Rf *
else
    echo "Cannot find $JAMBI/doc/html. The script must be run in the scripts folder."
    exit 1
fi

# Generating the QDoc JAPI file
cd $JAMBI/generator
./generator --build-qdoc-japi

# Running QDoc
cd $LOCAL_QDOC/test
../qdoc3 qt-for-jambi.qdocconf jambi.qdocconf

cd $JAMBI/doc/html/com/trolltech/qt
jar -cf $JAMBI/scripts/qtjambi-jdoc-$QTJAMBI_VERSION.jar *.jdoc

# Generating the sourcecode
# cd $JAMBI/generator
cd $JAMBI/generator
./generator --jdoc-enabled --jdoc-dir ../doc/html/com/trolltech/qt

# Generating the Javadoc
cd $JAMBI/doc/html
javadoc -doclet jambidoc.JambiDoclet -J-Xmx500m -sourcepath $JAMBI com.trolltech.qt com.trolltech.qt.core com.trolltech.qt.gui com.trolltech.qt.opengl com.trolltech.qt.sql com.trolltech.qt.opengl com.trolltech.qt.svg com.trolltech.qt.network com.trolltech.qt.xml com.trolltech.qt.designer

jar -cf qtjambi-javadoc-$QTJAMBI_VERSION.jar *

cd $JAMBI/scripts
cp $JAMBI/doc/html/qtjambi-javadoc-$QTJAMBI_VERSION.jar .

export LOCAL_QDOC=
