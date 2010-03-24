#include <qglobal.h>
#include <QtGui/QtGui>
#include <QtCore/QtCore>
#include <jni.h>
#include "qtjambi_core.h"
#include "qtjambifunctiontable.h"
#include "qtjambilink.h"
#include "qtjambi_platformspecificfunctions.h"

 void qt_set_library_config_file(const QString &fileName);
 void qt_set_sequence_auto_mnemonic(bool enable);
#ifdef Q_WS_X11
 void qt_x11_wait_for_window_manager(QWidget *widget);
#endif
#ifdef Q_WS_MAC
 void qt_mac_secure_keyboard(bool enable);
 void qt_mac_set_dock_menu(QMenu *menu);
 void qt_mac_set_menubar_icons(bool enable);
 void qt_mac_set_menubar_merge(bool enable);
 void qt_mac_set_native_menubar(bool enable);
 void qt_mac_set_press_and_hold_context(bool enable);
#endif

/*
 * Class:     com_trolltech_qt_PlatformSpecificFunctions
 * Method:    __qt_set_library_config_file
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_trolltech_qt_PlatformSpecificFunctions__1_1qt_1set_1library_1config_1file
  (JNIEnv * __jni_env, jclass, jstring strg){
    QString __qt_text =  qtjambi_to_qstring(__jni_env, (jstring) strg);
    qt_set_library_config_file(__qt_text);
  }

/*
 * Class:     com_trolltech_qt_PlatformSpecificFunctions
 * Method:    __qt_set_sequence_auto_mnemonic
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_trolltech_qt_PlatformSpecificFunctions__1_1qt_1set_1sequence_1auto_1mnemonic
  (JNIEnv *, jclass, jboolean enable){
    qt_set_sequence_auto_mnemonic((bool) enable);
  }
  
/*
 * Class:     com_trolltech_qt_PlatformSpecificFunctions
 * Method:    __qt_x11_wait_for_window_manager
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_trolltech_qt_PlatformSpecificFunctions__1_1qt_1x11_1wait_1for_1window_1manager
#ifdef Q_WS_X11
  (JNIEnv *, jclass, jlong w0){
  QWidget*  widget = (QWidget*) qtjambi_from_jlong(w0);
  qt_x11_wait_for_window_manager(widget);
#else
    (JNIEnv *, jclass, jlong){
#endif
  }

/*
 * Class:     com_trolltech_qt_PlatformSpecificFunctions
 * Method:    __qt_mac_secure_keyboard
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_trolltech_qt_PlatformSpecificFunctions__1_1qt_1mac_1secure_1keyboard
#ifdef Q_WS_MAC
  (JNIEnv *, jclass, jboolean enable){
 void qt_mac_secure_keyboard((bool) enable);
#else
  (JNIEnv *, jclass, jboolean){
#endif
  }

/*
 * Class:     com_trolltech_qt_PlatformSpecificFunctions
 * Method:    __qt_mac_set_dock_menu
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_trolltech_qt_PlatformSpecificFunctions__1_1qt_1mac_1set_1dock_1menu
#ifdef Q_WS_MAC
  (JNIEnv *, jclass, jlong w0){
 QMenu*  menu = (QMenu*) qtjambi_from_jlong(w0);
 void qt_mac_set_dock_menu(menu);
#else
  (JNIEnv *, jclass, jlong){
#endif    
  }

/*
 * Class:     com_trolltech_qt_PlatformSpecificFunctions
 * Method:    __qt_mac_set_menubar_icons
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_trolltech_qt_PlatformSpecificFunctions__1_1qt_1mac_1set_1menubar_1icons
#ifdef Q_WS_MAC
  (JNIEnv *, jclass, jboolean enable){
 void qt_mac_set_menubar_icons((bool) enable);
#else
  (JNIEnv *, jclass, jboolean){
#endif    
  }

/*
 * Class:     com_trolltech_qt_PlatformSpecificFunctions
 * Method:    __qt_mac_set_menubar_merge
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_trolltech_qt_PlatformSpecificFunctions__1_1qt_1mac_1set_1menubar_1merge
#ifdef Q_WS_MAC
  (JNIEnv *, jclass, jboolean enable){
 void qt_mac_set_menubar_merge((bool) enable);
#else
  (JNIEnv *, jclass, jboolean){
#endif    
  }

/*
 * Class:     com_trolltech_qt_PlatformSpecificFunctions
 * Method:    __qt_mac_set_native_menubar
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_trolltech_qt_PlatformSpecificFunctions__1_1qt_1mac_1set_1native_1menubar
#ifdef Q_WS_MAC
  (JNIEnv *, jclass, jboolean enable){
 void qt_mac_set_native_menubar((bool) enable);
#else
  (JNIEnv *, jclass, jboolean){
#endif    
  }

/*
 * Class:     com_trolltech_qt_PlatformSpecificFunctions
 * Method:    __qt_mac_set_press_and_hold_context
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_trolltech_qt_PlatformSpecificFunctions__1_1qt_1mac_1set_1press_1and_1hold_1context
#ifdef Q_WS_MAC
  (JNIEnv *, jclass, jboolean enable){
 void qt_mac_set_press_and_hold_context((bool) enable);
#else
  (JNIEnv *, jclass, jboolean){
#endif    
  }
