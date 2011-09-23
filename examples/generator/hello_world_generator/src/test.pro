
QT             += xml opengl
TEMPLATE        = subdirs
CONFIG         += qt warn_on debug ordered

DESTDIR         = out
TARGET          = testGenerator
MOC_DIR         = tmp
OBJECTS_DIR     = tmp

HEADERS        += *.h
SOURCES        += *.cpp

INCLUDEPATH    += $$PWD

#SUBDIRS         = org_qtjambi_test

