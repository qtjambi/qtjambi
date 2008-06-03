
include($$PWD/../../qtjambi/qtjambi_include.pri)

TEMPLATE = lib
CONFIG += plugin designer
DESTDIR=../../plugins/designer
TARGET=JambiCustomWidget

HEADERS += jambicustomwidget.h
SOURCES += jambicustomwidget.cpp

INCLUDEPATH += $$PWD/../include

CONFIG(debug, debug|release) {
    TARGET = $$member(TARGET, 0)_debuglib
}

macx:QMAKE_EXTENSION_SHLIB = dylib
