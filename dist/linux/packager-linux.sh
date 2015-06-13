#/bin/bash

if [ "${QTDIR}" == "" ]; then
    echo "Error: QTDIR variable is not set!"
    exit 1
fi

VERSION=$(ls qtjambi-4*.jar)
VERSION=${VERSION:8:5}

OS=$(ls build/qtjambi-native-*.jar | cut -sd / -f 2-)
OS=${OS:15:7}

RELEASE=qtjambi-$OS-community-$VERSION
COMPILER=gcc

mkdir $RELEASE
mkdir $RELEASE/bin

#cp version.properties $RELEASE

cp qtjambi-$VERSION.jar $RELEASE
cp qtjambi-designer-$VERSION.jar $RELEASE
cp qtjambi-examples-$VERSION.jar $RELEASE
cp jars/qtjambi-examples-src.jar $RELEASE
cp build/qtjambi-native-$OS-$COMPILER-$VERSION.jar $RELEASE

cp dist/linux/examples.sh $RELEASE
cp dist/linux/designer.sh $RELEASE
cp dist/linux/designer_lib_expander.sh $RELEASE

#cp dist/install.html $RELEASE
#cp dist/LGPL_EXCEPTION.TXT $RELEASE
cp dist/LICENSE.GPL3 $RELEASE
cp dist/LICENSE.LGPL $RELEASE
#cp dist/readme.txt $RELEASE

cp build/qmake-juic/juic $RELEASE/bin

cp $QTDIR/bin/designer $RELEASE/bin
cp $QTDIR/bin/linguist $RELEASE/bin
cp $QTDIR/bin/lrelease $RELEASE/bin
cp $QTDIR/bin/lupdate $RELEASE/bin


tar czf $RELEASE.tar.gz $RELEASE

rm -Rf $RELEASE
