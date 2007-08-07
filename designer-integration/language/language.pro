
TEMPLATE = lib
CONFIG  += qt warn_on plugin designer
DESTDIR = $$PWD/../../plugins/designer
TARGET = JambiLanguage

# Have to include after TARGET...
include(../pri/jambi.pri)

HEADERS += \ 
	jambilanguageplugin.h \


SOURCES += \ 
	jambilanguageplugin.cpp \ 

CONFIG(debug, debug|release) {
    TARGET = $$member(TARGET, 0)_debuglib
}

win32-msvc.net{
    QMAKE_CXXFLAGS += -Zm1000
    QMAKE_CXXFLAGS -= -Zm200
    QMAKE_CFLAGS -= -Zm200
}

RESOURCES += resources.qrc

INCLUDEPATH += $$PWD $$QTDIR/QtDesigner
