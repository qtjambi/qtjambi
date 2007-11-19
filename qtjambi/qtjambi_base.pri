!macx:!exists($(JAVADIR)) {
  error("Please set your JAVADIR environment variable to point to the directory of your Java SDK; Current JAVADIR: $(JAVADIR)")
}

isEmpty(TARGET) {
  error("Please specify TARGET name before including qtjambi_base.pri");
}

TEMPLATE = lib
DESTDIR = $$PWD/../lib
DLLDESTDIR = $$PWD/../bin


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
    INCLUDEPATH += $$(JAVADIR)/include
    win32 { 
        INCLUDEPATH += $$(JAVADIR)/include/win32
    } else {
        INCLUDEPATH += $$(JAVADIR)/include/linux
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
