TARGET = com_trolltech_qt_phonon

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_phonon/com_trolltech_qt_phonon.pri)

QT = core gui phonon
# This is for kdephonon. No-one can have both in
# the machine, so this shouldn’t break anything...
INCLUDEPATH += /usr/include/phonon
