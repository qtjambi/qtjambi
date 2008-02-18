#!/bin/sh

chmod -R u+rx .


cd gpl
touch LICENSE.GPL2
QTDIR=$PWD perl bin/syncqt -check-includes
./configure --confirm-license=yes -fast -no-qt3support -release -no-rpath -shared -prefix $PWD -D QT_JAMBI_BUILD
cd src && ../bin/qmake -r && make && cd ..
cd tools && ../bin/qmake -r && make && cd ..
cd ..


cd commercial 
touch LICENSE
QTDIR=$PWD perl bin/syncqt -check-includes
./configure --confirm-license=yes -fast -no-qt3support -release -no-rpath -shared -prefix $PWD -D QT_JAMBI_BUILD
cd src && ../bin/qmake -r && make && cd ..
cd tools && ../bin/qmake -r && make && cd ..
cd ..


cd eval
QTDIR=$PWD perl bin/syncqt -check-includes
touch LICENSE.EVAL
./configure --confirm-license=yes -fast -no-qt3support -release -no-rpath -shared -prefix $PWD -D QT_JAMBI_BUILD -D QT_EVAL
cd src && ../bin/qmake -r && make && cd ..
cd tools && ../bin/qmake -r && make && cd ..
cd ..


rm -rf /tmp/qt-gpl
rm -rf /tmp/qt-commercial
rm -rf /tmp/qt-eval

mv gpl /tmp/qt-gpl
mv commercial /tmp/qt-commercial
mv eval /tmp/qt-eval

if [ ! -e /tmp/qt-gpl ]; then exit 1; fi
if [ ! -e /tmp/qt-commercial ]; then exit 1; fi
if [ ! -e /tmp/qt-eval ]; then exit 1; fi



