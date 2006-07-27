TARGET = com_trolltech_qt_gui

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_gui/com_trolltech_qt_gui.pri)

QT = core gui

win32:CONFIG += precompile_header
PRECOMPILED_HEADER = qtjambi_gui_pch.h
