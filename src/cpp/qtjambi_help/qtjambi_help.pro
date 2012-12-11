TARGET = com_trolltech_qt_help

include(../qtjambi/qtjambi_include.pri)
include($$QTJAMBI_CPP/com_trolltech_qt_help/com_trolltech_qt_help.pri)

# This looks like a bug in Qt to me (well strictly speaking QtHelp is not externally documented)
INCLUDEPATH += $$QMAKE_INCDIR_QT/QtHelp
# Need to explain why this line was added to reinstate it.  Maybe as the *.prl doesn't work
#  because the library is not public API.  When re-instating it, it is possible for
#  $$QMAKE_LIBDIR_QT to be blank which causes "-L" to be on its own and then causes the next
#  argument to be interpreted as directory name.  This causes linking of the DSO to fail.
!isEmpty(QMAKE_LIBDIR_QT) {
    # We check it is set before pre-pending -L otherwise a bare -L breaks the build.
    # This is needed on (at least) Linux when you built your own Qt SDK, only needed
    #  here if we did not use CONFIG += help
#   LIBS += -L$$QMAKE_LIBDIR_QT
}
#LIBS += -lQtHelp

# libQtHelp.so.4.7.4 is dependant on many, using modifiers
#  libQtCore.so.4 libQtGui.so.4 libQtNetwork.so.4 libQtSql.so.4 libQtXml.so.4 libQtCLucene.so.4
#QT += help
#CONFIG += help gui core
CONFIG += help
