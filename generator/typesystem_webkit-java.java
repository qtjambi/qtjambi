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
import com.trolltech.qt.webkit.*;

class QWebPage___ extends QWebPage {
    protected String javaScriptPrompt(com.trolltech.qt.webkit.QWebFrame originatingFrame, java.lang.String msg, java.lang.String defaultValue) {
        com.trolltech.qt.QNativePointer result = new com.trolltech.qt.QNativePointer(QNativePointer.Type.String);
        if (javaScriptPrompt(originatingFrame, msg, defaultValue, result))
            return result.stringValue();
        else
            return null;
    }

}// class

