/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.demos.imageviewer;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;


public class LazyPixmap extends QObject {
    
    public static final QSize SMALL_SIZE = new QSize(32, 32);
    
    public Signal0 loaded = new Signal0();
    
    public LazyPixmap(String fileName) {
        this.fileName = fileName;
    }
    
    public void loadThumbNail() {
        QImage img = new QImage(fileName);
        
        assert !img.isNull();        
        
        QImage small = (img.width() > SMALL_SIZE.width()
                        || img.height() > SMALL_SIZE.height())
                        ? img.scaled(SMALL_SIZE, Qt.AspectRatioMode.KeepAspectRatio, Qt.TransformationMode.SmoothTransformation) 
                        : img.copy();

        synchronized(this){
            this.thumbNail = small;
            this.size = img.size();
            if(nativeId()!=0)
                loaded.emit();
        }
        img.dispose();
    }   

    public synchronized QImage image() { return new QImage(fileName); }
    public synchronized QSize size() { return size; }
    public synchronized QImage thumbNail() { return thumbNail; }
    public synchronized boolean isValid() { return thumbNail != null; }
    
    private String fileName;
    private QImage thumbNail;
    private QSize size;
}
