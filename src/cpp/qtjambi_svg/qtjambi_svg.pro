TARGET = com_trolltech_qt_svg

include(../qtjambi/qtjambi_include.pri)
include($$QTJAMBI_CPP/com_trolltech_qt_svg/com_trolltech_qt_svg.pri)

# libQtSvg.so.4.7.4 is only dependant on libQtCore.so.4 libQtGui.so.4
QT = core gui svg
