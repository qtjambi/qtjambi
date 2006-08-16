RXXPATH = $$PWD/..
include($$RXXPATH/rxx.pri)
include($$RXXPATH/rpp/rpp.pri)

OBJECTS_DIR = tmp
MOC_DIR = tmp

SOURCES += main.cpp lsbdb.cpp
QT = core sql
