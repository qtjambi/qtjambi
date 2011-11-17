TARGET = com_trolltech_qt_declarative

include(../qtjambi/qtjambi_include.pri)
include($$QTJAMBI_CPP/com_trolltech_qt_declarative/com_trolltech_qt_declarative.pri)

# libQtDeclarative.so.4.7.4 is dependant on many, using modifiers
#  libQtCore.so.4 libQtGui.so.4 libQtNetwork.so.4 libQtSql.so.4 libQtXmlPatterns.so.4 libQtSvg.so.4 libQtScript.so.4
QT += declarative
#CONFIG += core gui declarative
