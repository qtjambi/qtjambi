contains(QT_CONFIG, release) {
    CONFIG -= debug
    CONFIG += release
}

# Input
HEADERS += \
        classlistgenerator.h \
        cppgenerator.h \
        cppheadergenerator.h \
        cppimplgenerator.h \
        docparser.h \
        generatorsetjava.h \
        javagenerator.h \
        jumptable.h \
        metainfogenerator.h \
        metajavabuilder.h \
        qdocgenerator.h \
        uiconverter.h \

SOURCES += \
        classlistgenerator.cpp \
        cppgenerator.cpp \
        cppheadergenerator.cpp \
        cppimplgenerator.cpp \
        docparser.cpp \
        generatorsetjava.cpp \
        javagenerator.cpp \
        jumptable.cpp \
        metainfogenerator.cpp \
        metajavabuilder.cpp \
        qdocgenerator.cpp \
        uiconverter.cpp \

include(generator.pri)
