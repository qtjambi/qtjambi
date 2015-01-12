Building qt-jambi
===========


Qt
---

NOTE: On Linux, If you have Qt 4 installed already, you can compile Jambi against that version of Qt.
Skip to section [Qt Jambi](#qt-jambi) to continue.

Download qt source. I used `qt-everywhere-opensource-src-4.8.6.tar.gz`.

Compile it. This will look something like:

    tar xvf qt-everywhere-opensource-src-4.8.6.tar.gz
    cd qt-everywhere-opensource-src-4.8.6
    QT_INSTALL_PATH=/home/install/qt-everywhere-opensource-src-4.8.6  #or wherever
    ./configure -prefix=${QT_INSTALL_PATH}

At this point, check the features configure has enabled (near the top of its
long output; you don't need to wait until it finishes). Install additional
development headers as necessary. One feature you probably do need is DBus
support. When you're ready:

    make -j4 # you can use an integer equal to amount of your cores
    make install

Check it works:

    cd ${QT_INSTALL_PATH}
    bin/assistant

It’s also possible to possible to compile against system Qt, if such is installed.

### Troubleshooting Qt

First try using `ldd` to see if all libraries are found:

    ldd bin/assistant

The most likely cause of a problem at this stage is a missing library, so go back
and make sure `configure` enabled all features needed. In one case I was able
to “solve” a missing library problem by un-setting `rpath` as below, only to
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

For more details on rpath, see
[http://stackoverflow.com/questions/7839360/qt-jambi-version-compatibility][this question on Stackoverflow].


Qt Jambi
---------

Download and extract the Qt Jambi source, or clone the repository:

    git clone http://git.smar.fi/qtjambi-community.git
    cd qtjambi-community

You may want to look at `build.properties` file, to see if you want any customization.
By default, Jambi builds release build using qmake found from `PATH`, querying the paths from it.

In case you built your own Qt, you should set QTDIR to installation location:

    export QTDIR=${QT_INSTALL_PATH}

Then build:

    env MAKEOPTS="-j4" ant all # you can set -j4 to be amount of cores in your CPU.

If successful, you should have following jars (assuming Qt 4.8.6 on Linux with amd64):

    qtjambi-4.8.6.jar
    qtjambi-designer-4.8.6.jar
    qtjambi-examples-4.8.6.jar
    build/qtjambi-native-linux64-gcc-4.8.6.jar

The native jar (`build/qtjambi-native-linux64-gcc-4.8.6.jar`) can be used as a jar provided to jvm
or by directly adding the libraries to library path:

    export LD_LIBRARY_PATH=${PATH TO JAMBI’S DIR}/build/platform-output/lib:${LD_LIBRARY_PATH}

In case you used own Qt build, you need to add its libraries to `LD_LIBRARY_PATH` in order
to run any Jambi programs:

    export LD_LIBRARY_PATH=${QT_INSTALL_PATH}/lib:${LD_LIBRARY_PATH}

Then you should be able to run the examples with:

    java -cp qtjambi-4.8.6.jar:qtjambi-examples-4.8.6.jar \
    com.trolltech.launcher.Launcher

Or if you wish to use the jar instead of libs directly:

    java -cp qtjambi-4.8.6.jar:qtjambi-examples-4.8.6.jar:build/qtjambi-native-linux64-gcc-4.8.6.jar \
    com.trolltech.launcher.Launcher

To use Qt Jambi in your application you'll need `qtjambi-4.8.6.jar` (or
whichever version you’re using). In addition to that, you need the libraries mentioned above
(`${QT_INSTALL_PATH}/lib`, `${JAMBI_SRC_DIR}/build/platform-output/lib`), or the native jar.
