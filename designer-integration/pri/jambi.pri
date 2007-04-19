

!macx:!exists($$(JAVADIR)) {
  error("Please set your JAVADIR environment variable to point to the directory of your Java SDK:\nCurrent JAVADIR: $$(JAVADIR)")
}

isEmpty(TARGET) {
  error("Please specify TARGET name before including qtjambi_base.pri");
}

macx:{
    LIBS += -framework JavaVm
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
    QMAKE_MAC_SDK=/Developer/SDKs/MacOSX10.4u.sdk
    CONFIG += ppc x86
    CONFIG -= precompile_header
}

INCLUDEPATH += $$PWD/../include


LIB_QTJAMBI = qtjambi
CONFIG(debug, debug|release) {
    LIB_QTJAMBI = $$join(LIB_QTJAMBI,,,_debuglib)
}


INCLUDEPATH += $$PWD/../../qtjambi

macx:{
    LIBS += ../../lib/lib$${LIB_QTJAMBI}.jnilib
} else {
    LIBS += -L$$PWD/../../lib -l$${LIB_QTJAMBI}
}
        
