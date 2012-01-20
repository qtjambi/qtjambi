TARGET = com_trolltech_qt_declarative

include(../qtjambi/qtjambi_include.pri)
include($$QTJAMBI_CPP/com_trolltech_qt_declarative/com_trolltech_qt_declarative.pri)

# We manually add these include paths, instead of using "QT += module" which is bad for us
#  as it created unnecessary hardwired linkage to libraries we may never reference any symbols from.
# To be able to remove this we need to make generator better/smarter about #include directives
#  and it might not include these files at all.
# qscriptvalue.h missing
# qnetworkaccessmanager.h missing
INCLUDEPATH += $$QMAKE_INCDIR_QT/QtScript $$QMAKE_INCDIR_QT/QtNetwork

# libQtDeclarative.so.4.7.4 is dependant on many, using modifiers
#  libQtCore.so.4 libQtGui.so.4 libQtNetwork.so.4 libQtSql.so.4 libQtXmlPatterns.so.4 libQtSvg.so.4 libQtScript.so.4
QT += declarative script
#CONFIG += core gui declarative
