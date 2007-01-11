TARGET = com_trolltech_tools_designer

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_tools_designer/com_trolltech_tools_designer.pri)

CONFIG += designer

HEADERS += \
	jambimembersheet.h \
	jambipropertysheet.h \
    jambiresourcebrowser.h \

SOURCES += \
	jambimembersheet.cpp \
	jambipropertysheet.cpp \
		
	