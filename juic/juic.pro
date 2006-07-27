include($(QTDIR)/src/tools/uic/uic.pri)

QT = xml core
CONFIG += console no_batch
mac:CONFIG -= app_bundle
DESTDIR = ../bin

DEFINES += QT_UIC_JAVA_GENERATOR QT_UIC

HEADERS += javawritedeclaration.h \
    javawriteincludes.h \
    javawriteinitialization.h \
    javanametable.h \
    javautils.h

SOURCES += javawritedeclaration.cpp \
    javawriteincludes.cpp \
    javawriteinitialization.cpp \
    javanametable.cpp \
    javautils.cpp \
    main.cpp

RESOURCES += juic.qrc

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
    CONFIG += x86 ppc
    CONFIG -= precompile_header
}
	

