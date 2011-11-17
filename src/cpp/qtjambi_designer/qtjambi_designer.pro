TARGET = com_trolltech_tools_designer

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_tools_designer/com_trolltech_tools_designer.pri)

# libQtDesigner.so.4.7.4 is dependant on many, using modifiers
#  libQtCore.so.4 libQtGui.so.4 libQtXml.so.4 libQtScript.so.4
CONFIG += designer

HEADERS += jambiresourcebrowser.h
