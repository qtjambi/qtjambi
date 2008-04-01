#!/bin/sh

function failure {
    echo $1
    exit
}

function download {
    echo Downloading $1
    if [ -e $1 ]
    then
	echo - deleting old file...
	rm $1
    fi
    curl -O http://ares.troll.no/~qt/packages/$1 || failure "download of package $1 failed..."
}

function unpack_and_build {
    PACKAGE_NAME=$1
    DIRECTORY_NAME=qt-$2
    echo Building $DIRECTORY_NAME
    echo - removing old contents...
    rm -rf $PACKAGE_NAME $DIRECTORY_NAME
    echo - Unpacking
    tar xzf $PACKAGE_NAME.tar.gz > /dev/null 2>&1
    mv $PACKAGE_NAME $DIRECTORY_NAME

    if [ "$2" == "eval" ]
    then
	echo - copying eval contents...
	rm -rf $4 > /dev/null 2>&1
	unzip $3.zip > /dev/null
	cp -R $4/* $DIRECTORY_NAME
	CONFIGURE_EXTRA=-DQT_EVAL
    else
	CONFIGURE_EXTRA=
    fi

    cd $DIRECTORY_NAME
    find . -exec touch \{\} \;
    touch LICENSE.TROLL

    echo yes>input

    echo - configuring
    ./configure -universal -no-framework -no-qt3support -release -no-rpath -shared -prefix -sdk /Developer/SDKs/MacOSX10.4u.sdk -D QT_JAMBI_BUILD $PWD $CONFIGURE_EXTRA<input
    echo - building
    cd src
      pwd
      make || failure "failed to build source $DIRECTORY_NAME"
      cd ..
    cd tools
      pwd
      make || failure "failed to build tools in $DIRECTORY_NAME"
      cd ..
    make clean
    cd ..
}

if [ -z "$QT_VERSION" ]
then
    echo Missing QT_VERSION environment variable
    exit
fi

if [ -z "$QT_PACKAGE_DIRECTORY" ]
then
    echo Missing QT_PACKAGE_DIRECTORY variable
    exit
fi

QT_COMMERCIAL=qt-mac-commercial-src-$QT_VERSION
QT_EVAL=qt-win-evalpatches-src-$QT_VERSION
QT_GPL=qt-mac-opensource-src-$QT_VERSION
QT_EVAL_DIR=qt-win-commercial-src-$QT_VERSION

cd $QT_PACKAGE_DIRECTORY

echo Downloading packages...
download $QT_COMMERCIAL.tar.gz
download $QT_EVAL.zip
rm -rf $QT_EVAL_DIR
download $QT_GPL.tar.gz

unpack_and_build $QT_COMMERCIAL eval $QT_EVAL $QT_EVAL_DIR
unpack_and_build $QT_GPL gpl
unpack_and_build $QT_COMMERCIAL commercial
