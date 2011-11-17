TARGET = com_trolltech_qt_dbus

include(../qtjambi/qtjambi_include.pri)
include($$QTJAMBI_CPP/com_trolltech_qt_dbus/com_trolltech_qt_dbus.pri)

# libQtDBus.so.4.7.4 is only dependant on libQtCore.so.4 libQtXml.so.4 (ensures removal of 'Qt -= gui')
QT = core xml dbus
