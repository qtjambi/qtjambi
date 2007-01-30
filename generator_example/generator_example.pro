TARGET = com_trolltech_examples_generator

!macx:!exists($(JAVADIR)) {
  error("Please set your JAVADIR environment variable to point to the directory of your Java SDK:\nCurrent JAVADIR: $(JAVADIR)")
}

isEmpty(TARGET) {
  error("Please specify TARGET name before including qtjambi_base.pri");
}

TEMPLATE = lib
DESTDIR = ../lib
DLLDESTDIR = ../bin


CONFIG(debug, debug|release) {
    TARGET = $$member(TARGET, 0)_debuglib
}

INCLUDEPATH += ../qtjambi ../common ../include

macx:{
    LIBS += -framework JavaVm
    QMAKE_EXTENSION_SHLIB = jnilib
} else {
    INCLUDEPATH += $(JAVADIR)/include
    win32 { 
        INCLUDEPATH += $(JAVADIR)/include/win32
    } else {
        INCLUDEPATH += $(JAVADIR)/include/linux
    }
}

# make install related...
!isEmpty(INSTALL_PREFIX) {
    target.path = $$INSTALL_PREFIX
    INSTALLS = target
}

win32:CONFIG += precompile_header

macx{
    QMAKE_MAC_SDK=/Developer/SDKs/MacOSX10.4u.sdk
    CONFIG += ppc x86
    CONFIG -= precompile_header
}


QTJAMBI_LIB_NAME = qtjambi
CONFIG(debug, debug|release) {
    QTJAMBI_LIB_NAME = $$member(QTJAMBI_LIB_NAME, 0)_debuglib
}

macx:{
    LIBS += $$DESTDIR/lib$$member(QTJAMBI_LIB_NAME, 0).jnilib
} else {
    LIBS += -L$$DESTDIR -l$$QTJAMBI_LIB_NAME 
}

QTJAMBI_CPP = ../cpp

DEFINES += QT_QTJAMBI_IMPORT

win32-msvc2005:CONFIG += embed_manifest_dll
include(../cpp/com_trolltech_examples_generator/com_trolltech_examples_generator.pri)



HEADERS += \ 
         mywidget.h

SOURCES += gameaction.cpp \
           gameanimation.cpp \
           gamegrammar.cpp \
           gameobject.cpp \
           gamescene.cpp \
           lookaction.cpp \
           pickupaction.cpp \
           useaction.cpp \

mac { 
    CONFIG += ppc x86
    CONFIG -= precompile_header
}