/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $CPP_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#include <jawt.h>
#include <jawt_md.h>
#define private public
#include <QtCore/QEvent>
#undef private

#include "qtjambi_core.h"


#include <QtGui/QtGui>

#include <windows.h>

char *qtjambi_awt_title = "Qt Jambi/AWT";


typedef QSet<void *> PendingEventSet;
Q_GLOBAL_STATIC(PendingEventSet, g_pending_events)

static bool qtjambi_awt_event_notify_callback(void **args) 
{
    QEvent *event = reinterpret_cast<QEvent *>(args[1]);
    if (g_pending_events()->remove(event)) {
        event->spont = true;
    }

    return false;
}

extern "C" JNIEXPORT jobject JNICALL Java_com_trolltech_extensions_awt_QAwtWidget_makeEventSpontaneous
(JNIEnv *, jclass, jobject javaObject, jlong nativeId)
{
    static bool hasRegisteredCallback = false;
    if (!hasRegisteredCallback) {
        QInternal::registerCallback(QInternal::EventNotifyCallback, qtjambi_awt_event_notify_callback);
        hasRegisteredCallback = true;
    }

    QEvent *event = reinterpret_cast<QEvent *>(qtjambi_from_jlong(nativeId));
    if (event != 0) g_pending_events()->insert(event);

    return javaObject;
}

extern "C" JNIEXPORT void JNICALL Java_com_trolltech_extensions_awt_QAwtWidget_paintIt
(JNIEnv *env, jobject widget, jobject ) 
{
    JAWT awt;
    awt.version = JAWT_VERSION_1_3;
    if (JAWT_GetAWT(env, &awt) == JNI_FALSE) {
        fprintf(stderr, "%s [%s:%d]: Couldn't get JAWT interface\n", qtjambi_awt_title, __FILE__, __LINE__);
        return;
    }

    jclass clazz = env->GetObjectClass(widget);
    if (clazz == 0) {
        fprintf(stderr, "%s [%s:%d]: Couldn't get JNI class interface of widget\n", qtjambi_awt_title, __FILE__, __LINE__);
        return;
    }

    jfieldID widgetAppearanceId = env->GetFieldID(clazz, "widgetAppearance", "Lcom/trolltech/qt/gui/QPixmap;");
    if (widgetAppearanceId == 0) {
        fprintf(stderr, "%s [%s:%d]: Couldn't get JNI field ID of field 'widgetAppearance'\n", qtjambi_awt_title, __FILE__, __LINE__);
        return;
    }

    jobject widgetAppearance = env->GetObjectField(widget, widgetAppearanceId);
    if (widgetAppearance == 0) {
        fprintf(stderr, "%s [%s:%d]: Unexpected null value in field 'widgetAppearance'\n", qtjambi_awt_title, __FILE__, __LINE__);
        return;
    }

    QPixmap *pm = (QPixmap *) qtjambi_to_object(env, widgetAppearance);
    if (pm == 0) {
        fprintf(stderr, "%s [%s:%d]: Couldn't convert widget appearance to C++ object\n", qtjambi_awt_title, __FILE__, __LINE__);
        return;
    }

    {
        JAWT_DrawingSurface *ds;
        ds = awt.GetDrawingSurface(env, widget);
        if (ds == 0) {
            fprintf(stderr, "%s [%s:%d]: Couldn't get drawing surface\n", qtjambi_awt_title, __FILE__, __LINE__);
            return;
        }

        jint lock = ds->Lock(ds);
        if (lock & JAWT_LOCK_ERROR) {
            fprintf(stderr, "%s [%s:%d]: Couldn't lock drawing surface\n", qtjambi_awt_title, __FILE__, __LINE__);
            return;
        }

        {
            HDC hdc = 0;
            JAWT_DrawingSurfaceInfo *dsi = ds->GetDrawingSurfaceInfo(ds);
            if (dsi == 0) {
                fprintf(stderr, "%s [%s:%d]: Couldn't get drawing surface info\n", qtjambi_awt_title, __FILE__, __LINE__);
                return;
            }

            hdc = reinterpret_cast<JAWT_Win32DrawingSurfaceInfo *>(dsi->platformInfo)->hdc;
        
            HDC hbitmap_hdc = CreateCompatibleDC(qt_win_display_dc());
            HBITMAP bm = pm->toWinHBITMAP(QPixmap::NoAlpha);
            HGDIOBJ null_bitmap = SelectObject(hbitmap_hdc, bm);

            if (!BitBlt(hdc, 0, 0, pm->width(), pm->height(), hbitmap_hdc, 0, 0, SRCCOPY)) {
                fprintf(stderr, "%s [%s:%d]: Unable to blit to HDC: %p\n", qtjambi_awt_title, __FILE__, __LINE__, hdc);
            }

            SelectObject(hdc, null_bitmap);
            DeleteObject(bm);
            DeleteDC(hbitmap_hdc);
            ds->FreeDrawingSurfaceInfo(dsi);
        }

        ds->Unlock(ds);
        awt.FreeDrawingSurface(ds);
    }    
}
