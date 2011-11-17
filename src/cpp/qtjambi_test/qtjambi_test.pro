TARGET = com_trolltech_qt_test

include(../qtjambi/qtjambi_include.pri)
include($$QTJAMBI_CPP/com_trolltech_qt_test/com_trolltech_qt_test.pri)

# libQtTest.so.4.7.4 is only dependant on libQtCore.so.4 (ensures removal of 'Qt -= gui')
QT = core qtestlib
