#/bin/bash

if [ "${QTDIR}" == "" ]; then
    echo "Error: QTDIR variable is not set!"
    exit 1
fi

OS=macosx
VERSION=$(ls qtjambi-4*.jar)
VERSION=${VERSION:8:5}
RELEASE=qtjambi-$OS-community-$VERSION
COMPILER=gcc
JAMBISTUFF=build/platform-output

mkdir $RELEASE
mkdir $RELEASE/bin

cp version.properties $RELEASE

cp qtjambi-$VERSION.jar $RELEASE
cp qtjambi-designer-$VERSION.jar $RELEASE
cp qtjambi-examples-$VERSION.jar $RELEASE
cp jars/qtjambi-examples-src.jar $RELEASE
cp jars/qtjambi-util.jar $RELEASE
cp build/qtjambi-native-$OS-$COMPILER-$VERSION.jar $RELEASE

cp dist/mac/qtjambi.sh $RELEASE
cp dist/mac/designer.sh $RELEASE
cp dist/mac/juic.sh $RELEASE
cp dist/mac/installer-osx.sh $RELEASE
#cp set_qtjambi_env.sh $RELEASE
cp dist/changes-$VERSION $RELEASE
cp dist/install.html $RELEASE
cp dist/LGPL_EXCEPTION.TXT $RELEASE
cp dist/LICENSE.GPL3 $RELEASE
cp dist/LICENSE.LGPL $RELEASE
cp dist/readme.txt $RELEASE

cp build/qmake-juic/juic $RELEASE/bin
cp -R $QTDIR/bin/Designer.app $RELEASE/bin
cp -R $QTDIR/bin/Linguist.app $RELEASE/bin
cp $QTDIR/bin/lrelease $RELEASE/bin
cp $QTDIR/bin/lupdate $RELEASE/bin


tar czf $RELEASE.tar.gz $RELEASE

rm -Rf $RELEASE
