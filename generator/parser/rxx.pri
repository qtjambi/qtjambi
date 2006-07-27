
isEmpty(RXXPATH):RXXPATH = $$PWD

INCLUDEPATH += $$RXXPATH

DEFINES += RXX_ALLOCATOR_INIT_0

HEADERS += \ 
           $$RXXPATH/ast.h \
           $$RXXPATH/binder.h \
           $$RXXPATH/class_compiler.h \
           $$RXXPATH/codemodel.h \
           $$RXXPATH/codemodel_finder.h \
           $$RXXPATH/codemodel_fwd.h \
           $$RXXPATH/codemodel_pointer.h \
           $$RXXPATH/compiler_utils.h \
           $$RXXPATH/control.h \
           $$RXXPATH/declarator_compiler.h \
           $$RXXPATH/default_visitor.h \
           $$RXXPATH/dumptree.h \
           $$RXXPATH/lexer.h \
           $$RXXPATH/list.h \
           $$RXXPATH/name_compiler.h \
           $$RXXPATH/parser.h \
           $$RXXPATH/rxx_allocator.h \
           $$RXXPATH/smallobject.h \
           $$RXXPATH/symbol.h \
           $$RXXPATH/tokens.h \
           $$RXXPATH/type_compiler.h \
           $$RXXPATH/visitor.h \


SOURCES += \
           $$RXXPATH/ast.cpp \           
           $$RXXPATH/binder.cpp \
           $$RXXPATH/class_compiler.cpp \
           $$RXXPATH/codemodel.cpp \
           $$RXXPATH/codemodel_finder.cpp \ 
           $$RXXPATH/compiler_utils.cpp \
           $$RXXPATH/control.cpp \
           $$RXXPATH/declarator_compiler.cpp \
           $$RXXPATH/default_visitor.cpp \
           $$RXXPATH/dumptree.cpp \
           $$RXXPATH/lexer.cpp \
           $$RXXPATH/list.cpp \
           $$RXXPATH/name_compiler.cpp \
           $$RXXPATH/parser.cpp \
           $$RXXPATH/smallobject.cpp \
           $$RXXPATH/tokens.cpp \
           $$RXXPATH/type_compiler.cpp \
           $$RXXPATH/visitor.cpp \
