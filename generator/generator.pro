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

# Profiling generator.exe
#
#CONFIG += DEBUG
#QMAKE_CXXFLAGS_DEBUG += -pg
#QMAKE_LFLAGS_DEBUG += -pg
#
# Each sample counts as 0.01 seconds.
#  %   cumulative   self              self     total           
# time   seconds   seconds    calls   s/call   s/call  name    
# 34.99    218.69   218.69 4638987900     0.00     0.00  QBasicAtomicInt::ref()
# 34.66    435.31   216.62 4796821756     0.00     0.00  QBasicAtomicInt::deref()
#  4.29    462.12    26.81 2773082225     0.00     0.00  QString::QString(QString const&)
#  2.47    477.54    15.43 2943952981     0.00     0.00  QString::~QString()
#
