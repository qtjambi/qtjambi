
include(../../qtjambi/qtjambi_include.pri)

SOURCES += jambi.cpp

DESTDIR = ../../bin
DLLDESTDIR = 
TEMPLATE = app

TARGET = jambi

CONFIG(debug, debug | release) {
    TARGET = jambi_debug
}

mac:CONFIG-=app_bundle

CONFIG += console
