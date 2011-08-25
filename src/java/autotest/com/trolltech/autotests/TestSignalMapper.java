/****************************************************************************
 **
 ** Copyright (C) 1992-2009 Nokia. All rights reserved.
 **
 ** This file is part of Qt Jambi.
 **
 ** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
** 
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
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
** 
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

 **
 ** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 ** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 **
 ****************************************************************************/

package com.trolltech.autotests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.trolltech.qt.core.QEventLoop;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QSignalMapper;
import com.trolltech.qt.gui.QGuiSignalMapper;
import com.trolltech.qt.gui.QWidget;

public class TestSignalMapper extends QApplicationTest{


    /**
     * Receiver class for the various mapped signals in this test.
     */
    private static class Receiver extends QObject {
        public int lastInteger;
        public String lastString;
        public QObject lastQObject;
        public QWidget lastQWidget;

        public void slotInteger(int i) {
            lastInteger = i;
        }

        public void slotString(String s) {
            lastString = s;
        }

        public void slotQObject(QObject o) {
            lastQObject = o;
        }

        public void slotQWidget(QWidget w) {
            lastQWidget = w;
        }
    }

    /**
     * Emitter class for triggering the vairous mapped signals...
     */
    private static class Emitter extends QObject {
        Signal0 signal = new Signal0();

        public void emitSignal() {
            signal.emit();
        }
    }

    @Test
    public void run_mappedInt() {
        QSignalMapper mapper = new QSignalMapper();
        Receiver receiver = new Receiver();
        Emitter emitters[] = new Emitter[10];

        for (int i = 0; i < emitters.length; ++i) {
            emitters[i] = new Emitter();
            emitters[i].signal.connect(mapper, "map()");
            mapper.setMapping(emitters[i], i);
        }
        mapper.mappedInteger.connect(receiver, "slotInteger(int)");

        for (int i = 0; i < 10; ++i) {
            emitters[i].emitSignal();
            assertEquals(receiver.lastInteger, i);
        }

        Thread thread = new Thread("Reciver Thread"){
            @Override
            public void run() {
                new QEventLoop().exec();
            }
        };

        receiver.moveToThread(thread);
        thread.start();


        for (int i = 0; i < 10; ++i) {
            emitters[i].emitSignal();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            assertEquals(receiver.lastInteger, i);
        }
    }

    @Test
    public void run_mappedString() {
        QSignalMapper mapper = new QSignalMapper();
        Receiver receiver = new Receiver();
        Emitter emitters[] = new Emitter[10];

        for (int i = 0; i < emitters.length; ++i) {
            emitters[i] = new Emitter();
            emitters[i].signal.connect(mapper, "map()");
            mapper.setMapping(emitters[i], "id(" + i + ")");
        }
        mapper.mappedString.connect(receiver, "slotString(String)");

        for (int i = 0; i < 10; ++i) {
            emitters[i].emitSignal();
            assertEquals(receiver.lastString, "id(" + i + ")");
        }
    }

    @Test
    public void run_mappedQObject() {
        QSignalMapper mapper = new QSignalMapper();
        Receiver receiver = new Receiver();
        Emitter emitters[] = new Emitter[10];

        for (int i = 0; i < emitters.length; ++i) {
            emitters[i] = new Emitter();
            emitters[i].signal.connect(mapper, "map()");
            mapper.setMapping(emitters[i], emitters[i]);
        }
        mapper.mappedQObject.connect(receiver, "slotQObject(QObject)");

        for (int i = 0; i < 10; ++i) {
            emitters[i].emitSignal();
            assertEquals(receiver.lastQObject, emitters[i]);
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void run_mappedQWidget() {
        QGuiSignalMapper mapper = new QGuiSignalMapper();
        Receiver receiver = new Receiver();
        Emitter emitters[] = new Emitter[10];
        QWidget widgets[] = new QWidget[10];

        for (int i = 0; i < emitters.length; ++i) {
            emitters[i] = new Emitter();
            widgets[i] = new QWidget();
            emitters[i].signal.connect(mapper, "map()");
            mapper.setMapping(emitters[i], widgets[i]);
        }
        mapper.mappedQWidget.connect(receiver, "slotQWidget(QWidget)");

        for (int i = 0; i < 10; ++i) {
            emitters[i].emitSignal();
            assertEquals(receiver.lastQWidget, widgets[i]);
        }
    }
}
