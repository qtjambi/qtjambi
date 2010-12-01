include(qtjambi_base.pri)

QTJAMBI_LIB_NAME = qtjambi
CONFIG(debug, debug|release) {
    QTJAMBI_LIB_NAME = $$member(QTJAMBI_LIB_NAME, 0)_debuglib
}

macx:{
    LIBS += $$PWD/../../../build/src/cpp/lib/lib$$member(QTJAMBI_LIB_NAME, 0).jnilib
} else {
    LIBS += -L$$PWD/../../../build/src/cpp/lib -l$$QTJAMBI_LIB_NAME
}

QTJAMBI_CPP = ../../../build/generator/out/cpp/

win32-msvc2005:CONFIG += embed_manifest_dll force_embed_manifest