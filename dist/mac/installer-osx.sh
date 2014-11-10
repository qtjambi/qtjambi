#/bin/bash

if [[ $EUID -ne 0 ]]; then
    echo "Error! You must run the script with sudo in order to install Qt Jambi libs"
    exit 1
fi

OS=macosx
VERSION=$(ls qtjambi-4*.jar)
VERSION=${VERSION:8:5}
RELEASE=qtjambi-$OS-community-$VERSION
COMPILER=gcc

LIBNAME=QtJambi-$VERSION
INSTALLDIR=/Library/$LIBNAME

if [ -d "$INSTALLDIR" ]; then
    echo "Removing previously installed Qt Jambi-$VERSION"
    rm -r -f $INSTALLDIR
fi

unzip -q qtjambi-native-$OS-$COMPILER-$VERSION.jar -d $INSTALLDIR

cd $INSTALLDIR/plugins-designer

# creates plugin folders
find . -type d -execdir mkdir -p $INSTALLDIR/plugins/{} \;

# copies the Jambi plugins
find . -name "*.dylib" -exec cp {} $INSTALLDIR/plugins/{} \;
find . -name "*.xml" -exec cp {} $INSTALLDIR/plugins/{} \;

cd ..

rm -r -f $INSTALLDIR/META-INF
rm -r -f $INSTALLDIR/plugins-designer
rm $INSTALLDIR/qtjambi-deployment.xml
