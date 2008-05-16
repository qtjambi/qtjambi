#!/bin/sh

chmod -R u+rx .


rm -rf /tmp/qt-gpl
rm -rf /tmp/qt-commercial
rm -rf /tmp/qt-eval

mv gpl /tmp/qt-gpl
mv commercial /tmp/qt-commercial
mv eval /tmp/qt-eval

cd /tmp

cd qt-gpl
rm -f src/gui/kernel/qapplication_mac.cpp
rm -f src/gui/kernel/qapplication_qws.cpp
touch LICENSE.GPL2
QTDIR=$PWD perl bin/syncqt -check-includes
./configure --confirm-license=yes -no-qt3support -release -shared -prefix $PWD -no-sql-sqlite2 -D QT_JAMBI_BUILD
cd src && make && make clean && cd ..
cd tools && make && make clean && cd ..
cd ..


cd qt-commercial
rm -f src/gui/kernel/qapplication_mac.cpp
rm -f src/gui/kernel/qapplication_qws.cpp
touch LICENSE
QTDIR=$PWD perl bin/syncqt -check-includes
./configure --confirm-license=yes -fast -no-qt3support -release -shared -prefix $PWD -no-sql-sqlite2 -D QT_JAMBI_BUILD
cd src && make && make clean && cd ..
cd tools && make && make clean && cd ..
cd ..


cd qt-eval
rm -f src/gui/kernel/qapplication_mac.cpp
rm -f src/gui/kernel/qapplication_qws.cpp
QTDIR=$PWD perl bin/syncqt -check-includes
touch LICENSE.EVAL
./configure --confirm-license=yes -fast -no-qt3support -release -shared -prefix $PWD -no-sql-sqlite2 -D QT_JAMBI_BUILD -D QT_EVAL
cd src && make && make clean && cd ..
cd tools && make && make clean && cd ..
cd ..


if [ ! -e /tmp/qt-gpl ]; then exit 1; fi
if [ ! -e /tmp/qt-commercial ]; then exit 1; fi
if [ ! -e /tmp/qt-eval ]; then exit 1; fi



