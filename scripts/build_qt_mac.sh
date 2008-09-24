#!/bin/sh

chmod -R u+rx .

rm -rf /tmp/qtjambi-package-builder/qt-gpl
rm -rf /tmp/qtjambi-package-builder/qt-commercial
rm -rf /tmp/qtjambi-package-builder/qt-eval

mkdir /tmp/qtjambi-package-builder

mv gpl /tmp/qtjambi-package-builder/qt-gpl
mv commercial /tmp/qtjambi-package-builder/qt-commercial
mv eval /tmp/qtjambi-package-builder/qt-eval

cd /tmp/qtjambi-package-builder

cd qt-gpl
touch LICENSE.GPL2
QTDIR=$PWD perl bin/syncqt -check-includes
./configure --confirm-license=yes -fast -universal -no-framework -no-qt3support -release -no-rpath -shared -no-dbus -prefix $PWD -sdk /Developer/SDKs/MacOSX10.4u.sdk -D QT_JAMBI_BUILD
cd src && make && make clean && cd ..
cd tools && make && make clean && cd ..
cd ..

cd qt-commercial
touch LICENSE
QTDIR=$PWD perl bin/syncqt -check-includes
./configure --confirm-license=yes -fast -universal -no-framework -no-qt3support -release -no-rpath -shared -no-dbus -prefix $PWD -sdk /Developer/SDKs/MacOSX10.4u.sdk -D QT_JAMBI_BUILD
cd src && make && make clean && cd ..
cd tools && make && make clean && cd ..
cd ..

cd qt-eval
QTDIR=$PWD perl bin/syncqt -check-includes
touch LICENSE.EVAL
./configure --confirm-license=yes -fast -universal -no-framework -no-qt3support -release -no-rpath -shared -no-dbus -prefix $PWD -sdk /Developer/SDKs/MacOSX10.4u.sdk -D QT_JAMBI_BUILD -D QT_EVAL
cd src && make && make clean && cd ..
cd tools && make && make clean && cd ..
cd ..

if [ ! -e /tmp/qtjambi-package-builder/qt-gpl ]; then return 1; fi
if [ ! -e /tmp/qtjambi-package-builder/qt-commercial ]; then return 1; fi
if [ ! -e /tmp/qtjambi-package-builder/qt-eval ]; then return 1; fi

