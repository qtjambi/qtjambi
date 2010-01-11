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

package com.trolltech.examples;

import com.trolltech.qt.gui.*;

@QtJambiExample(name = "Custom Widget")
public class CustomWidgetExample extends QWidget {
    private Ui_CustomWidget ui = new Ui_CustomWidget();

    public CustomWidgetExample(QWidget parent) {
        super(parent);

        ui.setupUi(this);
        QMessageBox.information(this, "Just a hint!",
                "This is an example of how you can make your own custom widgets\n"
              + "and import them in Qt Designer. Try running Qt Designer and look\n"
              + "for Custom Widget in the widget box.");
    }

    public static void main(String[] args) {
        QApplication.initialize(args);

        CustomWidgetExample widget = new CustomWidgetExample(null);
        widget.show();

        QApplication.execStatic();
        QApplication.shutdown();
    }

}
