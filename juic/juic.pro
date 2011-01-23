
QT = xml core
CONFIG += console no_batch
mac:CONFIG -= app_bundle
DESTDIR = ../bin

include(uic/uic.pri)

INCLUDEPATH += $$PWD
DEFINES += QT_UIC_JAVA_GENERATOR QT_UIC

HEADERS += javawritedeclaration.h \
    javawriteincludes.h \
    javawriteinitialization.h \
    javautils.h \
    uic/uic.h
    
SOURCES += javawritedeclaration.cpp \
    javawriteincludes.cpp \
    javawriteinitialization.cpp \
    javautils.cpp \
    main.cpp \
    uic/uic.cpp
        
contains(QT_CONFIG, release):contains(QT_CONFIG, debug) {
    # Qt was configued with both debug and release libs
    CONFIG += debug_and_release build_all
}

# make install related...
!isEmpty(INSTALL_PREFIX) {
    target.path = $$INSTALL_PREFIX
    INSTALLS = target
}

mac {
    contains(QT_CONFIG, x86):contains(QT_CONFIG, ppc):CONFIG += x86 ppc
    CONFIG -= precompile_header
}

linux-g++* {
    QMAKE_LFLAGS += -Wl,--rpath,\\\$\$ORIGIN/../lib
}
