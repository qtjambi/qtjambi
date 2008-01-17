TARGET = com_trolltech_qt_core

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_core/com_trolltech_qt_core.pri)

HEADERS += qtjambiconcurrent.h
SOURCES += qtjambiconcurrent.cpp

QT -= gui
