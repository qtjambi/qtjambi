
QT             += xml opengl
TEMPLATE        = subdirs
CONFIG         += qt warn_on debug ordered

DESTDIR         = out
TARGET          = testGenerator
MOC_DIR         = tmp
OBJECTS_DIR     = tmp

HEADERS         = src/*.h
SOURCES        += src/*.cpp

INCLUDEPATH    += src

SUBDIRS         = src/org_qtjambi_test
