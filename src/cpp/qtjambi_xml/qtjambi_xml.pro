TARGET = com_trolltech_qt_xml

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_xml/com_trolltech_qt_xml.pri)

win32:CONFIG += precompile_header
PRECOMPILED_HEADER = qtjambi_xml_pch.h

# libQtXml.so.4.7.4 is only dependant on libQtCore.so.4 (ensures removal of 'Qt -= gui')
QT = core xml
