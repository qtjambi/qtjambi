TEMPLATE = app

QT += xml
DEFINES += USE_QSWT

CONFIG += console

INCLUDEPATH += ../


# Input
SOURCES += explorerview.cpp \
    detailsview.cpp \
    valueview.cpp \
    scopelist.cpp \
    ../qswt.cpp
    
HEADERS += explorerview.h \
    detailsview.h \
    valueview.h \
    scopelist.h \
    ../qswt.h

include(../../../research/main/qworkbench/src/plugins/qt4projectmanager/proparser/proparser.pri)|error("proparser.pri not found!")