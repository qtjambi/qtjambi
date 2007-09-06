

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
        prigenerator.h \
   
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
        prigenerator.cpp \
   
include(generator.pri)
