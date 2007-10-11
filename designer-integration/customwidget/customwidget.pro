
include(../pri/jambi.pri)

TEMPLATE = lib
CONFIG += plugin designer
DESTDIR=../../plugins/designer
TARGET=JambiCustomWidget

HEADERS += jambicustomwidget.h
SOURCES += jambicustomwidget.cpp

CONFIG(debug, debug|release) {
    TARGET = $$member(TARGET, 0)_debuglib
}

win32-msvc2005 {
    CONFIG -= embed_manifest_dll 
    QMAKE_LFLAGS += /MANIFEST:no
}
