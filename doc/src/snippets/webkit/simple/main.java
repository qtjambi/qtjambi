/****************************************************************************
**
** Copyright (C) 2004-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

import com.trolltech.qt.webkit.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class main
{
    public static void main(String args[])
    {
        QApplication.initialize(args);
        QWidget parent = null;
//! [Using QWebView]
        QWebView view = new QWebView(parent);
        view.load(new QUrl("http://www.trolltech.com/"));
        view.show();
//! [Using QWebView]
        QApplication.exec();
    }

}
