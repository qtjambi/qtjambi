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

import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import com.trolltech.autotests.generated.CustomEvent;
import com.trolltech.autotests.generated.CustomStyleOption;
import com.trolltech.autotests.generated.PolymorphicType;
import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QPaintEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QStyleOption;
import com.trolltech.qt.gui.QStyleOptionButton;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QWindowsStyle;

class CustomStyle extends QWindowsStyle
{
    public static QStyleOption m_option = null;

    @Override
    public void drawControl(ControlElement element, QStyleOption opt, QPainter p, QWidget w) {
        m_option = opt;
    }

}

public class TestPolymorphicTypes extends QWidget
{
    @BeforeClass
    public static void testInitialize() throws Exception {
        QApplication.initialize(new String[] {});
    }

    @Test
    public void testGetPaintEvent()
    {
        QEvent event = PolymorphicType.getPaintEvent();
        assertEquals(event.type(), QEvent.Type.Paint);
        assertTrue(event instanceof QPaintEvent);
    }

    @Test
    public void testGetCustomEvent()
    {
        QEvent event = PolymorphicType.getCustomEvent(10);
        assertEquals(event.type(), QEvent.Type.resolve(QEvent.Type.User.value() + 1));
        assertTrue(event instanceof CustomEvent);

        CustomEvent customEvent = (CustomEvent) event;
        assertEquals(customEvent.m_something(), 10);
    }

    @Test
    public void testSendPaintEvent()
    {
        PolymorphicType.sendPaintEvent(this);
        assertEquals(QEvent.Type.Paint, eventType);
        assertEquals(QPaintEvent.class, eventClass);
        assertEquals(0, m_event.nativeId());
    }

    @Test
    public void testSendCustomEvent()
    {
        PolymorphicType.sendCustomEvent(this, 20);
        assertEquals(QEvent.Type.resolve(QEvent.Type.User.value() + 1), eventType);
        assertEquals(CustomEvent.class, eventClass);
        assertEquals(0, m_event.nativeId());
        assertEquals(20, customEventSomething);
    }

    @Test
    public void testGetButtonStyleOption()
    {
        QStyleOption opt = PolymorphicType.getButtonStyleOption();
        assertEquals(QStyleOption.OptionType.SO_Button.value(), opt.type());
        assertTrue(opt instanceof QStyleOptionButton);
    }

    @Test
    public void testGetCustomStyleOption()
    {
        QStyleOption opt = PolymorphicType.getCustomStyleOption(30);
        assertTrue(opt instanceof CustomStyleOption);
        assertEquals(QStyleOption.OptionType.SO_CustomBase.value() + 1, opt.type());

        CustomStyleOption customOpt = (CustomStyleOption) opt;
        assertEquals(customOpt.m_something(), 30);
    }

    @Test
    public void testGetUnmappedCustomStyleOption()
    {
        QStyleOption opt = PolymorphicType.getUnmappedCustomStyleOption();
        assertEquals(opt.type(), QStyleOption.OptionType.SO_Default.value());
        assertTrue(opt instanceof QStyleOption);
        assertEquals(opt.getClass().getName(), "com.trolltech.qt.gui.QStyleOption");
    }

    @Test
    public void testSendButtonStyleOption()
    {
        this.setStyle(new CustomStyle());
        PolymorphicType.sendButtonStyleOption(this);
        assertTrue(CustomStyle.m_option != null);
        assertEquals(CustomStyle.m_option.type(), QStyleOption.OptionType.SO_Button.value());
        assertTrue(CustomStyle.m_option instanceof QStyleOptionButton);
    }

    @Test
    public void testSendCustomStyleOption()
    {
        this.setStyle(new CustomStyle());
        PolymorphicType.sendCustomStyleOption(this, 40);
        assertTrue(CustomStyle.m_option != null);
        assertEquals(CustomStyle.m_option.type(), QStyleOption.OptionType.SO_CustomBase.value() + 1);
        assertTrue(CustomStyle.m_option instanceof CustomStyleOption);

        CustomStyleOption customOpt = (CustomStyleOption) CustomStyle.m_option;
        assertEquals(customOpt.m_something(), 40);
    }

    @Test
    public void testSendUnmappedCustomStyleOption()
    {
        this.setStyle(new CustomStyle());
        PolymorphicType.sendUnmappedCustomStyleOption(this);
        assertTrue(CustomStyle.m_option != null);
        assertEquals(CustomStyle.m_option.type(), QStyleOption.OptionType.SO_Default.value());
        assertTrue(CustomStyle.m_option instanceof QStyleOption);
        assertEquals(CustomStyle.m_option.getClass().getName(), "com.trolltech.qt.gui.QStyleOption");
    }

    private int customEventSomething = 0;
    private QEvent.Type eventType = null;
    private Class<?> eventClass = null;
    private QEvent m_event = null;
    @Override
    public boolean event(QEvent arg__1) {
        m_event = arg__1;
        eventClass = m_event.getClass();
        eventType = m_event.type();
        if (m_event instanceof CustomEvent)
            customEventSomething = ((CustomEvent)m_event).m_something();
        else
            customEventSomething = -1;

        return super.event(arg__1);
    }

    @AfterClass
    public static void testDispose() throws Exception {
        QApplication.quit();
    }

}
