#/bin/bash
# In order to run this script in windows you will need a console emulator like cmder (with msysgit) http://cmder.net/
# Also requires zip to be installed (e.g. http://gnuwin32.sourceforge.net/packages/zip.htm)

if [ "${QTDIR}" == "" ]; then
    echo "Error: QTDIR variable is not set!"
    exit 1
fi

VERSION=$(ls qtjambi-4*.jar)
VERSION=${VERSION:8:5}

OS=$(ls build/qtjambi-native-*.jar | cut -sd / -f 2-)
COMPILER=${OS:21:8}
OS=${OS:15:5}

RELEASE=qtjambi-community-$VERSION-$OS-$COMPILER

# Clear previous attempts
rm -Rf $RELEASE $RELEASE.zip

# Create temporary folders:
mkdir $RELEASE
mkdir $RELEASE/bin
mkdir $RELEASE/plugins

# Jars:
cp qtjambi-$VERSION.jar $RELEASE
cp qtjambi-designer-$VERSION.jar $RELEASE
cp qtjambi-examples-$VERSION.jar $RELEASE
cp jars/qtjambi-examples-src.jar $RELEASE
cp jars/qtjambi-src.jar $RELEASE
cp build/qtjambi-native-$OS-$COMPILER*-$VERSION.jar $RELEASE

# Shell scripts:
cp dist/win/examples.bat $RELEASE
cp dist/win/designer.bat $RELEASE
cp dist/win/juic.bat $RELEASE

# Licenses and misc:
#cp dist/install.html $RELEASE
cp dist/changes-$VERSION $RELEASE
cp dist/LICENSE.GPL3 $RELEASE
cp dist/LICENSE.LGPL $RELEASE

# Juic
cp build/qmake-juic/release/juic $RELEASE/bin

# Qt Designer and other utilities:
cp $QTDIR/bin/designer $RELEASE/bin
cp $QTDIR/bin/linguist $RELEASE/bin
cp $QTDIR/bin/lrelease $RELEASE/bin
cp $QTDIR/bin/lupdate $RELEASE/bin

# Libs for Qt Designer
cp -r build/platform-output/lib $RELEASE/lib
# Qt Designer Jambi plugins
cp -r build/platform-output/plugins-designer/* $RELEASE/plugins/

# Pack everything together
zip -r $RELEASE.zip $RELEASE

# Remove temporary folder
rm -Rf $RELEASE
