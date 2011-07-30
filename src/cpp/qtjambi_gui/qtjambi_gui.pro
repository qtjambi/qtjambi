TARGET = com_trolltech_qt_gui


SOURCES += \
    qtreemodel.cpp \
    qguisignalmapper.cpp \
    qtjambi_platformspecificfunctions.cpp \
    qtjambiitemeditorcreator.cpp

HEADERS += \
    qtreemodel.h \
    qguisignalmapper.h \
    qtjambitextobjectinterface.h \
    qtjambi_platformspecificfunctions.h \
    qtjambi_gui_qhashes.h

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_gui/com_trolltech_qt_gui.pri)

# Some classes (such as QLine) need access to <qtjambi_core_qhashes.h>
INCLUDEPATH += $$PWD/../qtjambi_core

QT = core gui

win32:CONFIG += precompile_header
PRECOMPILED_HEADER = qtjambi_gui_pch.h
