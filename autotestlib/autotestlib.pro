TARGET = com_trolltech_autotests_generated

include(../qtjambi/qtjambi_include.pri)
include(../cpp/com_trolltech_autotests_generated/com_trolltech_autotests_generated.pri)

INCLUDEPATH += ../cpp/com_trolltech_autotests_generated

HEADERS += \ 
	abstractclass.h \
	destruction.h \
	injectedcode.h \
	interfaces.h \
	namespace.h \
	nativepointertester.h \
	paintengine.h \
	signalsandslots.h \
	testdialog.h \
	tulip.h \
	variants.h \


SOURCES += \
	destruction.cpp \
	injectedcode.cpp \
	namespace.cpp \
	testdialog.cpp \


QT += sql xml
