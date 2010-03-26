#!/bin/sh
export QTDIR=/home/smar/Paketit/qt/qt
#export QTDIR=/usr/lib/qt4
#export JAVA_HOME=/java/path  
export QTJAMBI=$PWD  
export PATH=$QTDIR/bin:$PATH  
export LD_LIBRARY_PATH=$QTDIR/lib
#export LD_LIBRARY_PATH=/usr/lib/qt4
ant $1
