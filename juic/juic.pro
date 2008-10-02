
exists(uic.pri) {
    # This part is included for the source package...
    include(uic.pri)
} else {
    QT_SOURCE_TREE=$$fromfile($$(QTDIR)/.qmake.cache,QT_SOURCE_TREE)
    include($$QT_SOURCE_TREE/src/tools/uic/uic.pri)
}


QT = xml core
CONFIG += console no_batch
mac:CONFIG -= app_bundle
DESTDIR = ../bin

DEFINES += QT_UIC_JAVA_GENERATOR QT_UIC

HEADERS += javawritedeclaration.h \
    javawriteincludes.h \
    javawriteinitialization.h \
    javautils.h \
    $$QT_SOURCE_TREE/src/tools/uic/uic.h  

SOURCES += javawritedeclaration.cpp \
    javawriteincludes.cpp \
    javawriteinitialization.cpp \
    javautils.cpp \
    main.cpp \
    $$QT_SOURCE_TREE/src/tools/uic/uic.cpp

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
    QMAKE_LFLAGS = -Wl,--rpath,\\\$\$ORIGIN/../lib
}
