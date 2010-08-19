#!/bin/sh
export QTDIR="/home/smar/Tiedostot/Projektit/qt/4.7/"
export MAKEOPTS="-j1"
export LC_ALL="C"
#export QTDIR=/usr/lib/qt4
#export JAVA_HOME=/java/path  
#export QTJAMBI=$PWD  
#export PATH=$QTDIR/bin:$PATH  
export LD_LIBRARY_PATH=$QTDIR/lib
#export LD_LIBRARY_PATH=/usr/lib/qt4
ant $1
