TARGET = org_qtjambi_test

# This must set be before includes and must match the build type used with qtjambi itself
#  try the defaults below first before butchering it up to make it work.
contains(QT_CONFIG, release):contains(QT_CONFIG, debug) {
    # Qt was configued with both debug and release libs
    CONFIG += debug_and_release
} else {
    # Manually modify this
    contains(QT_CONFIG, debug) {
        CONFIG += debug
    } else {
        CONFIG += release
    }
}

include(../../../../src/cpp/qtjambi/qtjambi_include.pri)
include(../build/generator/cpp/org_qtjambi_test/org_qtjambi_test.pri)

INCLUDEPATH += $$PWD $$PWD/../src

HEADERS +=
SOURCES +=

