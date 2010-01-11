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

package com.trolltech.manualtests;

import org.junit.Test;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QFontDialog;
import com.trolltech.qt.gui.QInputDialog;
import com.trolltech.qt.gui.QMessageBox;

import static org.junit.Assert.*;

public class TestManualInjectedCode extends ManualTests {

    @Test
    public void testQFontDialogGetFont() {
        QFontDialog.Result result = QFontDialog.getFont();

        assertEquals(QMessageBox.StandardButton.Yes, QMessageBox.question(null, "Correct?", "Font selected: " + result.font.family() + " and ok == " + result.ok, new QMessageBox.StandardButtons(QMessageBox.StandardButton.Yes, QMessageBox.StandardButton.No)));
    }

    @Test
    public void testQInputDialogGetDouble() {
        Double d = QInputDialog.getDouble(null, "Pick a double", "Here:");

        assertEquals(QMessageBox.StandardButton.Yes, QMessageBox.question(null, "Correct?", "Is this the double you selected: " + d, new QMessageBox.StandardButtons(QMessageBox.StandardButton.Yes, QMessageBox.StandardButton.No)));

    }

    public static void main(String[] args){
        QApplication.initialize(args);
        new TestManualInjectedCode();

        QApplication.instance().dispose();
    }

}
