TARGET = com_trolltech_qt_phonon

exists($$(PHONON_INCLUDEPATH)) {
	# This is for kdephonon. No-one can have both in
	# the machine, so this shouldn’t break anything...
	# This is before the qtjambi_include.pri include so try
	# to be before the other things that end up in the list
	INCLUDEPATH += $$(PHONON_INCLUDEPATH)
}
exists($$(PHONON_LIBS)) {
	LIBS += -L$$(PHONON_LIBS)
}

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_phonon/com_trolltech_qt_phonon.pri)

QT = core gui phonon

