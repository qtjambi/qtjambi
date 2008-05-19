!macx {
      JAVA=$(JAVA_HOME)
      !exists($$JAVA):JAVA=$(JAVADIR)
      !exists($$JAVA) {
          error("Qt Jambi needs the path to the Java SDK. Please set your JAVA_HOME environment variable to point to it...");
      }
}

isEmpty(TARGET) {
  error("Please specify TARGET name before including qtjambi_base.pri");
}

TEMPLATE = lib
DESTDIR = ../lib
DLLDESTDIR = ../bin


CONFIG(debug, debug|release) {
    TARGET = $$member(TARGET, 0)_debuglib
}

INCLUDEPATH += $$PWD/../qtjambi $$PWD/../common
DEPENDPATH += $$PWD/../qtjambi $$PWD/../common

macx:{
    QMAKE_MAC_SDK=/Developer/SDKs/MacOSX10.4u.sdk
    QMAKE_MACOSX_DEPLOYMENT_TARGET = 10.4
    LIBS += -framework JavaVM
    QMAKE_EXTENSION_SHLIB = jnilib
    QMAKE_MACOSX_DEPLOYMENT_TARGET=10.4
} else {
    INCLUDEPATH += $$JAVA/include
    win32 {
        INCLUDEPATH += $$JAVA/include/win32
    } else {
    solaris-g++ | solaris-cc {
        INCLUDEPATH += $$JAVA/include/solaris
    } else {
            INCLUDEPATH += $$JAVA/include/linux
        }
    }
}

contains(QT_CONFIG, release):contains(QT_CONFIG, debug) {
    # Qt was configued with both debug and release libs
    CONFIG += debug_and_release build_all
}

# make install related...
!isEmpty(INSTALL_PREFIX) {
    target.path = $$INSTALL_PREFIX
    INSTALLS = target
}

win32:CONFIG += precompile_header

macx{
    contains(QT_CONFIG, x86):contains(QT_CONFIG, ppc):CONFIG += x86 ppc
    CONFIG -= precompile_header
}

# gcc reports some functions as unused when they are not.
linux-g++:QMAKE_CXXFLAGS_WARN_ON += -Wno-unused-function

# Extra options to be set when using jump tables...
jumptable{
    CONFIG += hide_symbols

    # Tell the linker to strip the binaries..
    macx:QMAKE_LFLAGS += -Wl,-x
}

linux-g++* {
    QMAKE_LFLAGS = -Wl,--rpath,\\\$\$ORIGIN/../lib
}
