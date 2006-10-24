TARGET	 = qtdesigner

contains(CONFIG, static) {
    DEFINES += QT_DESIGNER_STATIC
    DEFINES += QT_NODLL
}
INCLUDEPATH += $$QMAKE_INCDIR_QT/QtDesigner \
    ../../../qworkbench/src/plugins/designer/shared

CONFIG(debug, debug|release) {
    unix: LIBS += -lQtDesigner_debug -lQtDesignerComponents_debug
    else: LIBS += -lQtDesignerd -lQtDesignerComponentsd
} else {
    LIBS += -lQtDesignerComponents -lQtDesigner
}


include(qtdesigner_inc.pri)

HEADERS += \
    ../../qswt.h \
    ../formeditorw.h \
    ../widgethost.h


SOURCES += \
    ../formeditorw.cpp \
    ../widgethost.cpp

# copy the .java files
JAVAFILES_SRC = java/*.java
JAVAFILES_DEST = ../../../com.trolltech.qtdesigner/src/com/trolltech/qtdesigner/views/

win32 {
    JAVAFILES_SRC ~= s|/|\|
    JAVAFILES_DEST ~= s|/|\|
}

QMAKE_POST_LINK += $${QMAKE_COPY} $${JAVAFILES_SRC} $${JAVAFILES_DEST}
