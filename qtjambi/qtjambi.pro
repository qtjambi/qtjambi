TARGET = qtjambi

include(qtjambi_base.pri)

SOURCES += \
	qsysinfo.cpp \
	qtinfo.cpp \
	qtjambi_cache.cpp \
	qtjambi_core.cpp \
	qtjambi_functions.cpp \
	qtjambifunctiontable.cpp \
	qtjambilink.cpp \
	qtjambitypemanager.cpp \
	qtobject.cpp \
    qnativepointer.cpp \
    qvariant.cpp 


HEADERS += \
	qtjambi_cache.h \
	qtjambi_core.h \
	qtjambifunctiontable.h \
	qtjambilink.h \
	qtjambitypemanager.h \
	qtjambi_global.h

DEFINES += QTJAMBI_EXPORT

win32:CONFIG += precompile_header
win32:PRECOMPILED_HEADER = qtjambi_core.h

macx:CONFIG -= precompile_header

QT = core gui
