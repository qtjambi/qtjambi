TARGET = com_trolltech_qt_sql

include(../qtjambi/qtjambi_include.pri)
include($$QTJAMBI_CPP/com_trolltech_qt_sql/com_trolltech_qt_sql.pri)

# libQtSql.so.4.7.4 is only dependant on libQtCore.so.4 (ensures removal of 'Qt -= gui')
QT = core sql
