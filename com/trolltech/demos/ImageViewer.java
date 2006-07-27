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

package com.trolltech.demos;

import com.trolltech.demos.imageviewer.*;
import com.trolltech.qt.gui.*;

public class ImageViewer extends MainWindow {
    
    public ImageViewer() {
        setWindowIcon(new QIcon("classpath:com/trolltech/images/logo_32.png"));
    }
    
    public static void main(String[] args) {
        QApplication.initialize(args);        
        ImageViewer viewer = new ImageViewer();
        viewer.show();
        QApplication.exec();
    }
}
