TEMPLATE = app

QT += xml
DEFINES += USE_QSWT

contains(CONFIG, static) {
   DEFINES += QT_DESIGNER_STATIC
}

INCLUDEPATH += . \
    ../ \
    $$QMAKE_INCDIR_QT/QtDesigner    

CONFIG(debug, debug|release) {
    unix: LIBS += -lQtDesigner_debug -lQtDesignerComponents_debug
    else: LIBS += -lQtDesignerd -lQtDesignerComponentsd
} else {
    LIBS += -lQtDesignerComponents -lQtDesigner
}

CONFIG += console

# Input
SOURCES += widgetboxw.cpp \
    formwindoww.cpp \
    formeditorw.cpp \
    propertyeditorw.cpp \
    objectinspectorw.cpp \
    actioneditorw.cpp \
    resourceeditorw.cpp \
    signalsloteditorw.cpp \
    ../qswt.cpp \
    widgethost.cpp
    
    
HEADERS += widgetboxw.h \
    formwindoww.h \
    formeditorw.h \
    propertyeditorw.h \
    objectinspectorw.h \
    actioneditorw.h \
    resourceeditorw.h \
    signalsloteditorw.h \
    ../qswt.h \
    widgethost.h
