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
        assertEquals(m_event.type(), QEvent.Type.Paint); 
        assertTrue(m_event instanceof QPaintEvent);        
    }
    
    @Test
    public void testSendCustomEvent() 
    {
        PolymorphicType.sendCustomEvent(this, 20);
        assertEquals(m_event.type(), QEvent.Type.resolve(QEvent.Type.User.value() + 1));
        assertTrue(m_event instanceof CustomEvent);
        
        CustomEvent customEvent = (CustomEvent) m_event;
        assertEquals(customEvent.m_something(), 20);
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
    
    private QEvent m_event = null;
    @Override   
    public boolean event(QEvent arg__1) {
        m_event = arg__1;
        return super.event(arg__1);
    }

    @AfterClass
    public static void testDispose() throws Exception {
        QApplication.quit();
    }
    
}
