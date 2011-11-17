TARGET = com_trolltech_qt_webkit

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_webkit/com_trolltech_qt_webkit.pri)

# We manually add these include paths, instead of using "QT += module" which is bad for us
#  as it created unnecessary hardwired linkage to libraries we may never reference any symbols from.
# To be able to remove this we need to make generator better/smarter about #include directives
#  and it might not include these files at all.
INCLUDEPATH += $$QMAKE_INCDIR_QT/QtNetwork

# libQtWebKit.so.4.7.4 is dependant on many, using modifiers
#   libQtCore.so.4 libQtGui.so.4 libQtNetwork.so.4 libQtXml.so.4 libQtDBus.so.4(known optional)
QT += webkit
