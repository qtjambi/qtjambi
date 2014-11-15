#!/bin/sh

VERSION=$(ls qtjambi-4*.jar)
VERSION=${VERSION:8:5}
JAMBIDIR=/Library/QtJambi-$VERSION

if [ ! -d "$JAMBIDIR" ]; then
    echo "Qt Jambi libs don't seem to be installed. Will attempt to install them:\nsudo sh installer-osx.sh"
    sudo sh installer-osx.sh
fi

me=$(dirname $0)
DYLD_LIBRARY_PATH=$JAMBIDIR/lib ./$me/bin/juic $@
