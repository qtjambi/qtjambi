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

package com.trolltech.launcher;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class CustomListView extends QListView
{
    CustomListView(QWidget parent) { super(parent); }

    public QSize sizeHint() {
	QSize s = new QSize(sizeHintForColumn(0), sizeHintForRow(0) * model().rowCount());
	return s;
    }
}
