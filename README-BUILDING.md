Building qt-jambi
===========


Qt
---

Download qt source. I used `qt-everywhere-opensource-src-4.8.5.tar.gz`.

Compile it. This will look something like:

    tar xaf qt...tar.gz
    cd qt...
    QT_INSTALL_PATH=/home/install/qt-4.8.5  #or wherever
    ./configure -prefix=${QT_INSTALL_PATH}

At this point, check the features configure has enabled (near the top of its
long output; you don't need to wait until it finishes). Install additional
development headers as necessary. One feature you probably do need is DBus
support. When you're ready:

    make -j4
    make install

Check it works:

    cd ${QT_INSTALL_PATH}
    bin/assistant


### Troubleshooting Qt

First try using `ldd` to see if all libraries are found:

    ldd bin/assistant

The most likely cause of a problem at this stage is missing library, so go back
and make sure `configure` enabled all features needed. In one case I was able
to "solve" a missing library problem by un-setting `rpath` as below, only to
later discover the real problem was a missing `libQtDBus`.

Qt (at least, version 4.8) appears to set `rpath` by default. You can use
`chrpath` or `readelf -d` to check this:

    chrpath bin/assistant

This can be problematic, particularly if you have multiple versions of Qt
installed (e.g. system libraries). You can unset `rpath` on the binary with
`chrpath`, but after doing this you will need to use `LD_LIBRARY_PATH` or some
other means to get the system to find your libraries:

    chrpath -d bin/assistant
    export LD_LIBRARY_PATH=$(pwd)/lib
    bin/assistant

For more details on rpath, see:
[http://stackoverflow.com/questions/7839360/qt-jambi-version-compatibility][].


Qt Jambi
---------

Download and extract the Qt Jambi source, or clone the repository:

    git clone http://git.gitorious.org/qt-jambi/qtjambi-community.git
    cd qtjambi-community
    JAMBI_SRC_DIR=$(pwd)

Update the included `build.properties` file, in particular setting
`generator.includepaths` (this was all I had to set). Set QTDIR to your
installation location (maybe using the source directory also works):

    export QTDIR=${QT_INSTALL_PATH}

Then build:

    ant all

If successful, you should have three jars in the build directory.

To run anything using qt-jambi, you need the Qt libraries and Jambi jni
libraries. So do, for example:

    export LD_LIBRARY_PATH=${QT_INSTALL_PATH}/lib:\
    ${JAMBI_SRC_DIR}/build/platform-output/lib

Then you should be able to run the examples with:

    java -cp qtjambi-4.8.5.jar:qtjambi-examples-4.8.5.jar \
    com.trolltech.launcher.Launcher

To use Qt Jambi in your application you'll need `qtjambi-4.8.5.jar` (or
whichever version) and the libraries mentioned above (`${QT_INSTALL_PATH}/lib`,
`${JAMBI_SRC_DIR}/build/platform-output/lib`).
