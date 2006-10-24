TARGET	 = qtproparser
unix:DESTDIR  = ../../../com.trolltech.qtproject

include(../../../../research/main/qworkbench/src/plugins/qt4projectmanager/proparser/proparser.pri)|error("proparser.pri not found!")
include(qtproparser_inc.pri)

# copy the .java files
JAVAFILES_SRC = java/*.java
JAVAFILES_DEST = ../../../com.trolltech.qtproject/src/com/trolltech/qtproject/pages/

win32 {
    JAVAFILES_SRC ~= s|/|\|
    JAVAFILES_DEST ~= s|/|\|
}

QMAKE_POST_LINK += $${QMAKE_COPY} $${JAVAFILES_SRC} $${JAVAFILES_DEST}
