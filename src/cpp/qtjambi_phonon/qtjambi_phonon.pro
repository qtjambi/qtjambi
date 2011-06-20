TARGET = com_trolltech_qt_phonon

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_phonon/com_trolltech_qt_phonon.pri)

QT = core gui phonon

linux-g++* {
	# This is for kdephonon. No-one can have both in
	# the machine, so this shouldn’t break anything...
	INCLUDEPATH += /usr/include/phonon
	# FIXME - Consider removal of this, buildpath.properties ${qtjambi.phonon.includedir}
	#  should propagate as necessary.  So hardwiring this patch will only bite people.
}
