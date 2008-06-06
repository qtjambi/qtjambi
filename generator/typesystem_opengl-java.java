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

package generator;

import com.trolltech.qt.*;
import com.trolltech.qt.opengl.*;

class QGLColormap___ extends QGLColormap {

    public final void setEntries(int colors[], int base) {
        setEntries(colors.length, com.trolltech.qt.internal.QtJambiInternal.intArrayToNativePointer(colors), base);
    }

    public final void setEntries(int colors[]) {
        setEntries(colors.length, com.trolltech.qt.internal.QtJambiInternal.intArrayToNativePointer(colors));
    }

}// class
