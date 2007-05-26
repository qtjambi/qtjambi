#!/bin/sh
set -v

cd ~

QT_VERSION=4.3.0
QT_PACKAGE=qt-x11-commercial-src-$QT_VERSION
QT_EVAL=qt-win-evalpatches-src-$QT_VERSION
QT_EVAL_DIR=qt-win-commercial-src-$QT_VERSION



#
# Download files again...
#
rm $QT_PACKAGE.tar.gz $QT_EVAL.zip
wget http://ares.troll.no/~qt/packages/$QT_PACKAGE.tar.gz
wget http://ares.troll.no/~qt/packages/$QT_EVAL.zip



#
# The commercial version
#
rm -rf $QT_PACKAGE qt-commercial-$QT_VERSION
tar xzf $QT_PACKAGE.tar.gz > /dev/null 2>&1
mv $QT_PACKAGE qt-commercial-$QT_VERSION
cd qt-commercial-$QT_VERSION
find . -exec touch \{\} \;
touch LICENSE.TROLL
./configure -no-qt3support -fast -release -no-rpath -no-xfixes -no-xcursor -shared -prefix $PWD
make sub-src sub-tools
make clean
cd ..



#
# The eval version...
#

rm -rf $QT_PACKAGE $QT_EVAL_DIR qt-eval-$QT_VERSION
tar xzf $QT_PACKAGE.tar.gz > /dev/null 2>&1
unzip $QT_EVAL.zip
cp -R $QT_EVAL_DIR/* $QT_PACKAGE
mv $QT_PACKAGE qt-eval-$QT_VERSION
cd qt-eval-$QT_VERSION
find . -exec touch \{\} \;
touch LICENSE.TROLL
./configure -no-qt3support -fast -release -no-rpath -no-xfixes -no-xcursor -shared -prefix $PWD  -DQT_EVAL
make sub-src sub-tools
make clean
cd ..
