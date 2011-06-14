TARGET = com_trolltech_autotests_generated

include(../../src/cpp/qtjambi/qtjambi_include.pri)
include(./cpp/com_trolltech_autotests_generated/com_trolltech_autotests_generated.pri)

INCLUDEPATH += ./cpp/com_trolltech_autotests_generated

HEADERS += \
    abstractclass.h \
    destruction.h \
    global.h \
    injectedcode.h \
    interfaces.h \
    messagehandler.h \
    namespace.h \
    nativepointertester.h \
    paintengine.h \
    signalsandslots.h \
    testdialog.h \
    tulip.h \
    variants.h \
    general.h \
    memorymanagement.h \


SOURCES += \
    destruction.cpp \
    global.cpp \
    injectedcode.cpp \
    testdialog.cpp \
    memorymanagement.cpp \

win32 {
   PRECOMPILED_HEADER = global.h
   CONFIG += precompile_header
}

QT += sql xml network
