#!/bin/sh

cd gpl
chmod -R u+rx .
touch LICENSE.GPL2
QTDIR=$PWD perl bin/syncqt -check-includes
./configure --confirm-license=yes -fast -universal -no-framework -no-qt3support -release -no-rpath -shared -prefix $PWD -sdk /Developer/SDKs/MacOSX10.4u.sdk -D QT_JAMBI_BUILD
cd src && ../bin/qmake -r && make && cd ..
cd tools && ../bin/qmake -r && make && cd ..
cd ..

cd commercial 
chmod -R u+rx .
touch LICENSE
QTDIR=$PWD perl bin/syncqt -check-includes
./configure --confirm-license=yes -fast -universal -no-framework -no-qt3support -release -no-rpath -shared -prefix $PWD -sdk /Developer/SDKs/MacOSX10.4u.sdk -D QT_JAMBI_BUILD
cd src && ../bin/qmake -r && make && cd ..
cd tools && ../bin/qmake -r && make && cd ..
cd ..

cd eval
chmod -R u+rx .
QTDIR=$PWD perl bin/syncqt -check-includes
touch LICENSE.EVAL
./configure --confirm-license=yes -fast -universal -no-framework -no-qt3support -release -no-rpath -shared -prefix $PWD -sdk /Developer/SDKs/MacOSX10.4u.sdk -D QT_JAMBI_BUILD -D QT_EVAL
cd src && ../bin/qmake -r && make && cd ..
cd tools && ../bin/qmake -r && make && cd ..
cd ..

rm -rf /tmp/qt-gpl
rm -rf /tmp/qt-commercial
rm -rf /tmp/qt-eval

mv gpl /tmp/qt-gpl
mv commercial /tmp/qt-commercial
mv eval /tmp/qt-eval

if [ ! -e /tmp/qt-gpl ]; then return 1; fi
if [ ! -e /tmp/qt-commercial ]; then return 1; fi
if [ ! -e /tmp/qt-eval ]; then return 1; fi
