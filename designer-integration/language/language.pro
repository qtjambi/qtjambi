
TEMPLATE = lib
CONFIG  += qt warn_on plugin designer
TARGET = JambiLanguage

# Have to include after TARGET...
include($$PWD/../../qtjambi/qtjambi_include.pri)

HEADERS += \
    jambilanguageplugin.h \
        qtjambiintrospection_p.h \


SOURCES += \
    jambilanguageplugin.cpp \
    qtjambiintrospection.cpp \

win32-msvc.net{
    QMAKE_CXXFLAGS += -Zm1000
    QMAKE_CXXFLAGS -= -Zm200
    QMAKE_CFLAGS -= -Zm200
}

RESOURCES += resources.qrc

INCLUDEPATH += $$PWD $$QTDIR/QtDesigner $$PWD/../include

# patch up some bad things set in qtjambi_include...
DESTDIR = ../../plugins/designer
macx:QMAKE_EXTENSION_SHLIB = dylib
