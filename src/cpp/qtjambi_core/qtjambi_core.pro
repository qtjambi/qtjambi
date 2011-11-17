TARGET = com_trolltech_qt_core

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_core/com_trolltech_qt_core.pri)

HEADERS += qtjambiconcurrent.h qtjambiconcurrent_p.h qtjambi_core_qhashes.h
SOURCES += qtjambiconcurrent.cpp

# libQtCore.so.4.7.4 is not dependant any other libQt*.so.* (ensures removal of 'Qt -= gui')
QT = core
