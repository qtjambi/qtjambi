#!/bin/sh

chmod -R u+rx .

# temp path must be long enough to reserve space for RPATH later on...
rm -rf /tmp/qtjambi-package-builder/qt-gpl
rm -rf /tmp/qtjambi-package-builder/qt-commercial
rm -rf /tmp/qtjambi-package-builder/qt-eval

mkdir /tmp/qtjambi-package-builder

mv gpl /tmp/qtjambi-package-builder/qt-gpl
mv commercial /tmp/qtjambi-package-builder/qt-commercial
mv eval /tmp/qtjambi-package-builder/qt-eval

cd /tmp/qtjambi-package-builder

cd qt-gpl
rm -f src/gui/kernel/qapplication_mac.cpp
rm -f src/gui/kernel/qapplication_qws.cpp
touch LICENSE.GPL2
QTDIR=$PWD perl bin/syncqt -check-includes
./configure --confirm-license=yes -no-qt3support -release -shared -prefix $PWD -no-sql-sqlite2 -no-mmx -no-3dnow -no-sse -no-sse2 -D QT_JAMBI_BUILD
cd src && make && make clean && cd ..
cd tools && make && make clean && cd ..
cd ..


cd qt-commercial
rm -f src/gui/kernel/qapplication_mac.cpp
rm -f src/gui/kernel/qapplication_qws.cpp
touch LICENSE
QTDIR=$PWD perl bin/syncqt -check-includes
./configure --confirm-license=yes -fast -no-qt3support -release -shared -prefix $PWD -no-sql-sqlite2 -no-mmx -no-3dnow -no-sse -no-sse2 -D QT_JAMBI_BUILD
cd src && make && make clean && cd ..
cd tools && make && make clean && cd ..
cd ..


cd qt-eval
rm -f src/gui/kernel/qapplication_mac.cpp
rm -f src/gui/kernel/qapplication_qws.cpp
QTDIR=$PWD perl bin/syncqt -check-includes
touch LICENSE.EVAL
./configure --confirm-license=yes -fast -no-qt3support -release -shared -prefix $PWD -no-sql-sqlite2 -no-mmx -no-3dnow -no-sse -no-sse2 -D QT_JAMBI_BUILD -D QT_EVAL
cd src && make && make clean && cd ..
cd tools && make && make clean && cd ..
cd ..


if [ ! -e /tmp/qtjambi-package-builder/qt-gpl ]; then exit 1; fi
if [ ! -e /tmp/qtjambi-package-builder/qt-commercial ]; then exit 1; fi
if [ ! -e /tmp/qtjambi-package-builder/qt-eval ]; then exit 1; fi



