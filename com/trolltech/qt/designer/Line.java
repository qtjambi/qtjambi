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

package com.trolltech.qt.designer;

import com.trolltech.qt.gui.*;
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;

public class Line extends QFrame {

    public Line() {
        this(null);
    }

    public Line(QWidget parent) {
        super(parent);
        setFrameShadow(QFrame.Shadow.Sunken);
    }

    @QtPropertyWriter(name="orientation")
    public void setOrientation(Qt.Orientation orient) {
        if (orient == Qt.Orientation.Horizontal)
            setFrameShape(QFrame.Shape.HLine);
        else
            setFrameShape(QFrame.Shape.VLine);
    }
}
