#!/bin/bash

if  [ "$QTJAMBI_VERSION" = "" ]; then
     export QTJAMBI_VERSION=4.4.0_01
fi

if [ "$QDOC" = "" ]; then
    if [ "$QTDIR" = "" ]; then
        echo "Unable to find qdoc3. Set the QDOC or QTDIR environment variables."
        exit 1
    fi
    export LOCAL_QDOC=$QTDIR/tools/qdoc3
else
    export LOCAL_QDOC=$QDOC
fi

echo "qdoc3 found in: $LOCAL_QDOC"

export JAMBI=`echo $PWD | sed s,/scripts,,g`
echo using jambi dir $JAMBI

# Clean the directory
echo Cleaning up the old directory...
if [ -d $JAMBI/doc/html ]; then
    rm -Rf $JAMBI/doc/html
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
echo $PWD
cd $JAMBI/generator
./generator --jdoc-enabled --jdoc-dir ../doc/html/com/trolltech/qt

# Creating header for HtmlDoclet

if [ "$DOCHOME" = "" ] ; then
    DOCHOME="http://doc.trolltech.com/qtjambi-$QTJAMBI_VERSION"
fi

HEADER="<table align='right'><tr><td nowrap><a target='_top' href='$DOCHOME/com/trolltech/qt/qtjambi-index.html'>Qt Jambi Home</a></td>"
HEADER="$HEADER<td><img src='$DOCHOME/com/trolltech/qt/images/qt-logo.png' width='32' height='32'></td></tr></table>"

# Generating the Javadoc
cd $JAMBI/doc/html

javadoc -doclet jambidoc.JambiDoclet -header "$HEADER" -J-Xmx500m -sourcepath $JAMBI com.trolltech.qt com.trolltech.qt.core com.trolltech.qt.gui com.trolltech.qt.opengl com.trolltech.qt.sql com.trolltech.qt.opengl com.trolltech.qt.svg com.trolltech.qt.network com.trolltech.qt.xml com.trolltech.qt.designer com.trolltech.qt.webkit com.trolltech.qt.phonon

find . -name "qt jambi.dcf" -exec rm {} \;
find . -name "qt jambi.index" -exec rm {} \;
find . -name "*.jdoc" -exec rm {} \;

jar -cf qtjambi-javadoc-$QTJAMBI_VERSION.jar *

cd $JAMBI/scripts
cp $JAMBI/doc/html/qtjambi-javadoc-$QTJAMBI_VERSION.jar .

export LOCAL_QDOC=
