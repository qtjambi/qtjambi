TARGET = qtjambi

include(qtjambi_base.pri)

SOURCES += \
	qnativepointer.cpp \
	qsysinfo.cpp \
	qtinfo.cpp \
	qtjambi_cache.cpp \
	qtjambi_core.cpp \
	qtjambi_functions.cpp \
	qtjambi_utils.cpp \
	qtjambifunctiontable.cpp \
	qtjambilink.cpp \
	qtjambitypemanager.cpp \
	qtobject.cpp \
	qvariant.cpp \
        qdynamicmetaobject.cpp \
        qjambivariant.cpp \


HEADERS += \
	qtjambi_cache.h \
	qtjambi_core.h \
	qtjambi_global.h \
	qtjambi_utils.h \
	qtjambifunctiontable.h \
	qtjambilink.h \
	qtjambitypemanager.h \
        qtjambidestructorevent.h \
        qdynamicmetaobject.h \

DEFINES += QTJAMBI_EXPORT

win32:CONFIG += precompile_header
win32:PRECOMPILED_HEADER = qtjambi_core.h

macx:CONFIG -= precompile_header

QT = core

sanitycheck:{
	DEFINES += QTJAMBI_SANITY_CHECK
}
