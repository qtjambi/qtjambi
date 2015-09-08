#/bin/bash

if [ "${QTDIR}" == "" ]; then
    echo "Error: QTDIR variable is not set!"
    exit 1
fi

VERSION=$(ls qtjambi-4*.jar)
VERSION=${VERSION:8:5}

OS=macosx
COMPILER=gcc

RELEASE=qtjambi-$OS-community-$VERSION

# Clear previous attempts
rm -Rf $RELEASE $RELEASE.tar.gz

# Create temporary folders:
mkdir $RELEASE
mkdir $RELEASE/bin

# Jars:
cp qtjambi-$VERSION.jar $RELEASE
cp qtjambi-designer-$VERSION.jar $RELEASE
cp qtjambi-examples-$VERSION.jar $RELEASE
cp jars/qtjambi-examples-src.jar $RELEASE
cp jars/qtjambi-src.jar $RELEASE
cp build/qtjambi-native-$OS-$COMPILER-$VERSION.jar $RELEASE

# Shell scripts:
cp dist/mac/qtjambi.sh $RELEASE
cp dist/mac/designer.sh $RELEASE
cp dist/mac/juic.sh $RELEASE
cp dist/mac/installer-osx.sh $RELEASE

# Licenses and misc:
#cp dist/install.html $RELEASE
cp dist/changes-$VERSION $RELEASE
cp dist/LICENSE.GPL3 $RELEASE
cp dist/LICENSE.LGPL $RELEASE

# Juic
cp build/qmake-juic/juic $RELEASE/bin

# Qt Designer and other utilities:
cp -R $QTDIR/bin/Designer.app $RELEASE/bin
cp -R $QTDIR/bin/Linguist.app $RELEASE/bin
cp $QTDIR/bin/lrelease $RELEASE/bin
cp $QTDIR/bin/lupdate $RELEASE/bin

# Pack everything together
tar czf $RELEASE.tar.gz $RELEASE

# Remove temporary folder
rm -Rf $RELEASE
