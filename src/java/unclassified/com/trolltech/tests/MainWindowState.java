/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** $BEGIN_LICENSE$
** GNU Lesser General Public License Usage
** This file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
**
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.tests;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

class MainWindowState extends QMainWindow {

    public MainWindowState() {
        QDockWidget w1 = new QDockWidget();
        w1.setWindowTitle("Dock 1");
        w1.setObjectName("a");

        QDockWidget w2 = new QDockWidget();
        w2.setWindowTitle("Dock 2");
        w2.setObjectName("b");

        addDockWidget(Qt.DockWidgetArea.LeftDockWidgetArea, w1);
        addDockWidget(Qt.DockWidgetArea.RightDockWidgetArea, w2);

        setCentralWidget(new QTextEdit());
    }


    @Override
    protected void hideEvent(QHideEvent e) {
        QSettings settings = new QSettings("MySoft", "testing");
        QByteArray array = saveState();
        settings.setValue("state", array);
    }


    @Override
    protected void showEvent(QShowEvent e) {
        QSettings settings = new QSettings("MySoft", "testing");
        QByteArray state = (QByteArray) settings.value("state");
        restoreState(state);
    }

    public static void main(String args[]) {
        QApplication.initialize(args);

        MainWindowState widget = new MainWindowState();
        widget.show();

        QApplication.execStatic();
        QApplication.shutdown();
    }
}
