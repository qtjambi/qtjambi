TEMPLATE = app
QT = xml core
CONFIG += warn_on console no_batch
CONFIG -= app_bundle
build_all:!build_pass {
    CONFIG -= build_all
    CONFIG += release
}

unix:!contains(QT_CONFIG, zlib):LIBS        += -lz

TARGET = uic
DESTDIR = ../../../bin

DEFINES	       += QT_BOOTSTRAPPED QT_UIC QT_LITE_UNICODE QT_NO_DATASTREAM \
	          QT_NO_THREAD QT_NO_QOBJECT QT_NO_UNICODETABLES QT_NO_LIBRARY
win32:DEFINES += QT_NODLL

CONFIG -= qt
INCLUDEPATH	 = ../../corelib/arch/generic $$QT_BUILD_TREE/include . \
                   $$QT_BUILD_TREE/include/QtCore $$QT_BUILD_TREE/include/QtXml
DEPENDPATH	+= $$INCLUDEPATH ../../corelib/base ../../corelib/tools ../../corelib/io ../../corelib/codecs ../../xml

include(uic.pri)

SOURCES += main.cpp


# Qt tools needed to link rcc
SOURCES	+= ../../corelib/global/qglobal.cpp \
	   ../../corelib/io/qbuffer.cpp \
	   ../../corelib/io/qdir.cpp		\
	   ../../corelib/io/qfile.cpp		\
	   ../../corelib/io/qfileinfo.cpp	\
	   ../../corelib/io/qfsfileengine.cpp	\
	   ../../corelib/io/qiodevice.cpp	\
	   ../../corelib/io/qtemporaryfile.cpp \
	   ../../corelib/io/qtextstream.cpp \
	   ../../corelib/kernel/qinternal.cpp \
	   ../../corelib/tools/qbytearraymatcher.cpp \
	   ../../corelib/tools/qchar.cpp		\
	   ../../corelib/tools/qdatetime.cpp	\
	   ../../corelib/tools/qhash.cpp		\
	   ../../corelib/tools/qlistdata.cpp		\
	   ../../corelib/tools/qlocale.cpp \
	   ../../corelib/tools/qmap.cpp		\
	   ../../corelib/tools/qstring.cpp		\
	   ../../corelib/tools/qstringlist.cpp	\
	   ../../corelib/tools/qstringmatcher.cpp \
	   ../../corelib/tools/qvector.cpp          \
           ../../corelib/io/qabstractfileengine.cpp  \
           ../../corelib/tools/qbytearray.cpp	\
           ../../corelib/tools/qbitarray.cpp	\
           ../../corelib/tools/qunicodetables.cpp	\
           ../../corelib/tools/qvsnprintf.cpp \
           ../../corelib/tools/qregexp.cpp \
           ../../corelib/codecs/qtextcodec.cpp \
           ../../corelib/codecs/qutfcodec.cpp \
           ../../corelib/codecs/qisciicodec.cpp \
           ../../corelib/codecs/qtsciicodec.cpp \
           ../../corelib/codecs/qlatincodec.cpp \
           ../../corelib/codecs/qsimplecodec.cpp \
	   ../../corelib/codecs/qfontlaocodec.cpp \
           ../../xml/qdom.cpp \
	   ../../xml/qxml.cpp

unix:SOURCES += ../../corelib/io/qfsfileengine_unix.cpp

win32:SOURCES += ../../corelib/io/qfsfileengine_win.cpp

macx: {
   QMAKE_MACOSX_DEPLOYMENT_TARGET = 10.2 #enables weak linking for 10.2 (exported)
   SOURCES += ../../corelib/kernel/qcore_mac.cpp
   LIBS += -framework CoreServices
}

contains(QT_CONFIG, zlib) {
   INCLUDEPATH += ../../3rdparty/zlib
   SOURCES+= \
	../3rdparty/zlib/adler32.c \
	../3rdparty/zlib/compress.c \
	../3rdparty/zlib/crc32.c \
	../3rdparty/zlib/deflate.c \
	../3rdparty/zlib/gzio.c \
	../3rdparty/zlib/inffast.c \
	../3rdparty/zlib/inflate.c \
	../3rdparty/zlib/inftrees.c \
	../3rdparty/zlib/trees.c \
	../3rdparty/zlib/uncompr.c \
	../3rdparty/zlib/zutil.c
} else:!contains(QT_CONFIG, no-zlib) {
   unix:LIBS += -lz
#  win32:LIBS += libz.lib
}

target.path=$$[QT_INSTALL_BINS]
INSTALLS += target

DESTDIR = ../../../bin
include(../../qt_targets.pri)
