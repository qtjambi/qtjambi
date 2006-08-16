DEFINES += PP_WITH_MACRO_POSITION

RXXPATH = $$PWD/..
include($$RXXPATH/rxx.pri)
include($$RXXPATH/rpp/rpp.pri)

OBJECTS_DIR = tmp
MOC_DIR = tmp

SOURCES += main.cpp
QT = core
