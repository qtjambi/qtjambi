TARGET = com_trolltech_qt_script

include(../qtjambi/qtjambi_include.pri)
include($$QTJAMBI_CPP/com_trolltech_qt_script/com_trolltech_qt_script.pri)

# libQtScript.so.4.7.4 is only dependant on libQtCore.so.4 (ensures removal of 'Qt -= gui')
QT = core script
#CONFIG += core gui script
