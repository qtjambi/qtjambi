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

#include "qtjambi_core.h"

#include <QtGui/QtGui>

#include <windows.h>

extern "C" JNIEXPORT void JNICALL Java_com_trolltech_extensions_awt_QAwtWidget_paint
(JNIEnv *env, jobject widget, jobject graphics) 
{
    JAWT awt;
    JAWT_DrawingSurface *ds;
    JAWT_Win32DrawingSurfaceInfo *windsi;
    JAWT_DrawingSurfaceInfo *dsi;
    awt.version = JAWT_VERSION_1_3;
    if (JAWT_GetAWT(env, &awt) == JNI_FALSE) {
        printf("AWT not found\n");
        return;
    }

    ds = awt.GetDrawingSurface(env, widget);
    if (ds == 0) {
        printf("NULL!!!");
        return;
    }

    jint lock = ds->Lock(ds);
    if (lock & JAWT_LOCK_ERROR) {
        printf("Error");
        return;
    }
    
    dsi = ds->GetDrawingSurfaceInfo(ds);
    windsi = (JAWT_Win32DrawingSurfaceInfo*) dsi->platformInfo;

    jclass clazz = env->GetObjectClass(widget);
    if (clazz == 0) {
        printf("NO CLASS\n");
        return;
    }

    jfieldID id = env->GetFieldID(clazz, "containedWidget", "Lcom/trolltech/qt/gui/QWidget;");
    if (id == 0) {
        printf("No id\n");
        return;
    }

    jobject qtWidget = env->GetObjectField(widget, id);
    if (qtWidget == 0) {
        printf("No Qt Widget\n");
        return;
    }

    QWidget *w = (QWidget *) qtjambi_to_qobject(env, qtWidget);
    if (w == 0) {
        printf("Couldn't convert to QWidget\n");
        return;
    }


    //QPixmap pm = QPixmap::grabWidget(w);

    jfieldID widgetAppearanceId = env->GetFieldID(clazz, "widgetAppearance", "Lcom/trolltech/qt/gui/QPixmap;");
    if (widgetAppearanceId == 0) {
        printf("Can't get widget appearance\n");
        return;
    }

    jobject widgetAppearance = env->GetObjectField(widget, widgetAppearanceId);
    if (widgetAppearance == 0) {
        printf("widget apperance not set\n");    
    }

    QPixmap *pm = (QPixmap *) qtjambi_to_object(env, widgetAppearance);
    if (pm == 0) {
        printf("Can't convert widget appearance\n");
        return;
    }

    HBITMAP hbitmap = pm->toWinHBITMAP(QPixmap::NoAlpha);
    fprintf(stderr, "Pixmap size: %d, %d\n", pm->width(), pm->height()); 

    HDC hdc = windsi->hdc;
    HDC hbitmap_hdc = CreateCompatibleDC(qt_win_display_dc());
    HGDIOBJ null_bitmap = SelectObject(hbitmap_hdc, hbitmap);
    if (!BitBlt(hdc, 0, 0, pm->width(), pm->height(), hbitmap_hdc, 0, 0, SRCCOPY)) {
        printf("Goddamn you GDI\n");
    }
    SelectObject(hdc, null_bitmap);
    DeleteObject(hbitmap);
    
}
