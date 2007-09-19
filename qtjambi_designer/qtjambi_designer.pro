TARGET = com_trolltech_tools_designer

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_tools_designer/com_trolltech_tools_designer.pri)

INCLUDEPATH += $$PWD $$QTDIR/QtDesigner

CONFIG += designer

HEADERS += \
	jambimembersheet.h \
    jambiresourcebrowser.h \

SOURCES += \
	jambimembersheet.cpp \
		
	