TARGET = com_trolltech_autotests_generated

# Messing with this config setting is what messes up building for Linux and debug_and_release for windows/macosx.
# So before reinstating this section explain why it is required and the purpose.
#contains(QT_CONFIG, release):contains(QT_CONFIG, debug) {
#    # Qt was configued with both debug and release libs
#    CONFIG += debug_and_release
#} else {
#    contains(QT_CONFIG, debug) {
#        CONFIG += debug
#    }
#    contains(QT_CONFIG, release) {
#        CONFIG += release
#    }
#}

include(../../../src/cpp/qtjambi/qtjambi_include.pri)
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
    memorymanagement.h


SOURCES += \
    destruction.cpp \
    global.cpp \
    injectedcode.cpp \
    testdialog.cpp \
    qtjambiunittesttools.cpp \
    memorymanagement.cpp

win32 {
    PRECOMPILED_HEADER = global.h
    CONFIG += precompile_header
}

linux-g++* | freebsd-g++* {
    QMAKE_LFLAGS_NOUNDEF   += -Wl,--no-undefined
    QMAKE_LFLAGS += $$QMAKE_LFLAGS_NOUNDEF
}

QT += sql xml network
CONFIG += warn_on
