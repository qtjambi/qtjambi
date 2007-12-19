contains(QT_CONFIG, release) {
    CONFIG -= debug
    CONFIG += release
}

unix:CONFIG += debug_and_release

CONFIG += console
RESOURCES += generator.qrc

include(parser/rxx.pri)

include(parser/rpp/rpp.pri)

# Input
HEADERS += \
        classlistgenerator.h \
        cppgenerator.h \
        cppheadergenerator.h \
        cppimplgenerator.h \
        docparser.h \
        javagenerator.h \
        metainfogenerator.h \
        metajavabuilder.h \
        qdocgenerator.h \
        uiconverter.h \
        generatorsetjava.h \
   
SOURCES += \
        classlistgenerator.cpp \
        cppgenerator.cpp \
        cppheadergenerator.cpp \
        cppimplgenerator.cpp \
        docparser.cpp \
        javagenerator.cpp \
        metainfogenerator.cpp \
        metajavabuilder.cpp \
        qdocgenerator.cpp \
        uiconverter.cpp \
        generatorsetjava.cpp \
   
include(generator.pri)
