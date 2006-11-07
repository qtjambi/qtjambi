TARGET = com_trolltech_qt_network

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_network/com_trolltech_qt_network.pri)

win32:CONFIG += precompile_header
PRECOMPILED_HEADER = qtjambi_network_pch.h
QT = core network
