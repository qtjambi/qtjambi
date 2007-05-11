TARGET = qtjambi_jawt

include(../qtjambi/qtjambi_include.pri)

SOURCES += qawtwidget.cpp
LIBS += -lgdi32 -ljawt
LIBPATH += $$(JAVADIR)/lib