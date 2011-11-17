TARGET = com_trolltech_qt_xmlpatterns

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_xmlpatterns/com_trolltech_qt_xmlpatterns.pri)

# libQtXmlPatterns.so.4.7.4 is only dependant on libQtCore.so.4 libQtNetwork.so.4 (ensures removal of 'Qt -= gui')
QT = core network xmlpatterns
