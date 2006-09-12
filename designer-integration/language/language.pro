
include(../pri/jambi.pri)

TEMPLATE = lib
CONFIG  += qt warn_on plugin designer
DESTDIR = $$PWD/../plugins/designer
TARGET = JambiLanguage

HEADERS += jambilanguageplugin.h jambivm.h
SOURCES += jambilanguageplugin.cpp jambivm.cpp

