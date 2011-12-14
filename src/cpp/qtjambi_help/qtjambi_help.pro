TARGET = com_trolltech_qt_help

include(../qtjambi/qtjambi_include.pri)
include($$QTJAMBI_CPP/com_trolltech_qt_help/com_trolltech_qt_help.pri)

# This looks like a bug in Qt to me (well strictly speaking QtHelp is not externally documented)
INCLUDEPATH += $$QMAKE_INCDIR_QT/QtHelp
LIBS += -L$$QMAKE_LIBDIR_QT -lQtHelp

# libQtHelp.so.4.7.4 is dependant on many, using modifiers
#  libQtCore.so.4 libQtGui.so.4 libQtNetwork.so.4 libQtSql.so.4 libQtXml.so.4 libQtCLucene.so.4
QT += help
#CONFIG += core gui help
