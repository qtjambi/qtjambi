TARGET = com_trolltech_qt_multimedia

include(../qtjambi/qtjambi_include.pri)
include($$QTJAMBI_CPP/com_trolltech_qt_multimedia/com_trolltech_qt_multimedia.pri)

# libQtMultimedia.so.4.7.4 is only dependant on libQtCore.so.4 libQtGui.so.4
QT = core gui multimedia
