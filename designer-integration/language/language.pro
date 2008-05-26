
TEMPLATE = lib
CONFIG  += qt warn_on plugin designer
DESTDIR = ../../plugins/designer
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
