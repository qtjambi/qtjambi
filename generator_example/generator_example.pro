TARGET = com_trolltech_examples_generator

include(../qtjambi/qtjambi_include.pri)
include(../cpp/com_trolltech_examples_generator/com_trolltech_examples_generator.pri)

HEADERS += gameaction.h \
           gameanimation.h \
           gamegrammar.h \
           gamenamespace.h \
           gameobject.h \
           gamescene.h \
           lookaction.h \
           pickupaction.h \
           useaction.h \
           point3d.h \
           abstractgameobject.h \

SOURCES += gameaction.cpp \
           gameanimation.cpp \
           gamegrammar.cpp \
           gameobject.cpp \
           gamescene.cpp \
           lookaction.cpp \
           pickupaction.cpp \
           useaction.cpp \
