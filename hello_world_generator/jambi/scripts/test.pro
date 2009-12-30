
TARGET = com_poseidon_test

DESTDIR = ../target

INCLUDEPATH += $$PWD ../../src

HEADERS        += ./../../src/TestItem.h ./../../src/TestSuper.h ./../../src/TestView.h
SOURCES        += ./../../src/TestItem.cpp ./../../src/TestSuper.cpp ./../../src/TestView.cpp

include($(JAMBIDIR)/qtjambi/qtjambi_include.pri)
include(../cpp/com_poseidon_test/com_poseidon_test.pri)

