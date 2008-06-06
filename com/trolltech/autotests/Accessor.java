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

package com.trolltech.autotests;

import com.trolltech.qt.core.QObject;

class Accessor extends QObject
{
    @SuppressWarnings("unchecked")
    public static void emit_signal(AbstractSignal signal, Object ... args)
    {
        if (signal instanceof Signal0)
            ((Signal0) signal).emit();
        else if (signal instanceof Signal1)
            ((Signal1) signal).emit(args[0]);
        else
            throw new RuntimeException("Implement more classes");
    }
}
