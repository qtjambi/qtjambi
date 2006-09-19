TARGET = com_trolltech_autotests_generated

include(../qtjambi/qtjambi_include.pri)
include(../cpp/com_trolltech_autotests_generated/com_trolltech_autotests_generated.pri)

INCLUDEPATH += ../cpp/com_trolltech_autotests_generated

HEADERS += \ 
	tulip.h \
	variants.h \
  	nativepointertester.h \
        abstractclass.h \
        signalsandslots.h \
        destruction.h \
        interfaces.h \


