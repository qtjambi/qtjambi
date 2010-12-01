TARGET = com_trolltech_qt_core

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_core/com_trolltech_qt_core.pri)

INCLUDEPATH += $$PWD

HEADERS += qtjambiconcurrent.h qtjambiconcurrent_p.h qtjambi_core_qhashes.h
SOURCES += qtjambiconcurrent.cpp

QT -= gui
