TARGET = qtjambi

include(qtjambi_base.pri)

INCLUDEPATH += $$PWD/../common
DEPENDPATH += $$PWD/../common

SOURCES += \
    qnativepointer.cpp \
    qsysinfo.cpp \
    qtinfo.cpp \
    qtjambi_cache.cpp \
    qtjambi_core.cpp \
    qtjambi_functions.cpp \
    qtjambi_utils.cpp \
    qtjambifunctiontable.cpp \
    qtjambilink.cpp \
    qtjambitypemanager.cpp \
    qtobject.cpp \
    qvariant.cpp \
    qtdynamicmetaobject.cpp \
    qtjambivariant.cpp \


HEADERS += \
    qtjambi_cache.h \
    qtjambi_core.h \
    qtjambi_global.h \
    qtjambi_utils.h \
    qtjambifunctiontable.h \
    qtjambilink.h \
    qtjambitypemanager_p.h \
    qtjambidestructorevent_p.h \
    qtdynamicmetaobject.h \
    qtjambivariant_p.h \

contains(DEFINES, QTJAMBI_DEBUG_TOOLS) {
    SOURCES += qtjambidebugtools.cpp
    HEADERS += qtjambidebugtools_p.h
}

DEFINES += QTJAMBI_EXPORT

win32:CONFIG += precompile_header
win32:PRECOMPILED_HEADER = qtjambi_core.h

macx:CONFIG -= precompile_header

QT = core
