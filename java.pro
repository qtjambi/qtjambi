TEMPLATE = subdirs
CONFIG += ordered

SUBDIRS =   src/cpp/qtjambi \
            src/cpp/qtjambi_core \
            src/cpp/qtjambi_gui \
            src/cpp/qtjambi_sql \
            src/cpp/qtjambi_network \
            src/cpp/qtjambi_xml \
            src/cpp/qtjambi_designer \
            src/cpp/qtjambi_help \
            src/cpp/designer-integration

contains(QT_CONFIG, script):            SUBDIRS += src/cpp/qtjambi_script
contains(QT_CONFIG, scripttools):       SUBDIRS += src/cpp/qtjambi_scripttools
contains(QT_CONFIG, webkit):            SUBDIRS += src/cpp/qtjambi_webkit
contains(QT_CONFIG, phonon):            SUBDIRS += src/cpp/qtjambi_phonon
contains(QT_CONFIG, xmlpatterns):       SUBDIRS += src/cpp/qtjambi_xmlpatterns
contains(QT_CONFIG, opengl):            SUBDIRS += src/cpp/qtjambi_opengl
contains(QT_CONFIG, multimedia):        SUBDIRS += src/cpp/qtjambi_multimedia
contains(QT_CONFIG, svg):               SUBDIRS += src/cpp/qtjambi_svg
contains(QT_CONFIG, dbus):              SUBDIRS += src/cpp/qtjambi_dbus
contains(QT_CONFIG, qtestlib):          SUBDIRS += src/cpp/qtjambi_test
contains(QT_CONFIG, declarative):       SUBDIRS += src/cpp/qtjambi_declarative

contains(QT_CONFIG, release):contains(QT_CONFIG, debug) {
    # Qt was configued with both debug and release libs
    CONFIG += debug_and_release build_all
}

#DEFINES += QTJAMBI_DEBUG_TOOLS

# This gives us a top level debug/release
EXTRA_DEBUG_TARGETS =
EXTRA_RELEASE_TARGETS =
for(sub, SUBDIRS) {
sub_pro = $$sub/$${basename(sub)}.pro
!exists($$sub_pro):next()
isEqual($$list($$fromfile($$sub_pro, TEMPLATE)), lib) {
    #debug
    eval(debug-$${sub}.depends = $${sub}/$(MAKEFILE) $$EXTRA_DEBUG_TARGETS)
    eval(debug-$${sub}.commands = (cd $$sub && $(MAKE) -f $(MAKEFILE) debug))
    EXTRA_DEBUG_TARGETS += debug-$${sub}
    QMAKE_EXTRA_TARGETS += debug-$${sub}
    #release
    eval(release-$${sub}.depends = $${sub}/$(MAKEFILE) $$EXTRA_RELEASE_TARGETS)
    eval(release-$${sub}.commands = (cd $$sub && $(MAKE) -f $(MAKEFILE) release))
    EXTRA_RELEASE_TARGETS += release-$${sub}
    QMAKE_EXTRA_TARGETS += release-$${sub}
} else { #do not have a real debug target/release
    #debug
    eval(debug-$${sub}.depends = $${sub}/$(MAKEFILE) $$EXTRA_DEBUG_TARGETS)
    eval(debug-$${sub}.commands = (cd $$sub && $(MAKE) -f $(MAKEFILE) first))
    EXTRA_DEBUG_TARGETS += debug-$${sub}
    QMAKE_EXTRA_TARGETS += debug-$${sub}
    #release
    eval(release-$${sub}.depends = $${sub}/$(MAKEFILE) $$EXTRA_RELEASE_TARGETS)
    eval(release-$${sub}.commands = (cd $$sub && $(MAKE) -f $(MAKEFILE) first))
    EXTRA_RELEASE_TARGETS += release-$${sub}
    QMAKE_EXTRA_TARGETS += release-$${sub}
}
}
debug.depends = $$EXTRA_DEBUG_TARGETS
release.depends = $$EXTRA_RELEASE_TARGETS
QMAKE_EXTRA_TARGETS += debug release
