TARGET = com_trolltech_qt_core

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_core/com_trolltech_qt_core.pri)

win32:CONFIG += precompile_header
PRECOMPILED_HEADER = qtjambi_core_pch.h
