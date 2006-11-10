
include(../pri/jambi.pri)

TEMPLATE = lib
CONFIG  += qt warn_on plugin designer
DESTDIR = $$PWD/../plugins/designer
TARGET = JambiLanguage

HEADERS += \ 
	jambilanguageplugin.h \
	jambipropertysheet.h \
	jambiresourcebrowser.h \
	jnilayer.h \


SOURCES += \ 
	jambilanguageplugin.cpp \ 
 	jambipropertysheet.cpp \
 	jambiresourcebrowser.cpp \
	jnilayer.cpp \


# Use the name table from UIC
JUIC_DIR = ../../juic
HEADERS += $$JUIC_DIR/javanametable.h
SOURCES += $$JUIC_DIR/javanametable.cpp
RESOURCES += $$JUIC_DIR/juic.qrc
INCLUDEPATH += $$JUIC_DIR