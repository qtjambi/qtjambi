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

import org.junit.Test;
import com.trolltech.autotests.generated.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.xml.QXmlInputSource;
import com.trolltech.qt.xml.QXmlEntityResolver.ResolvedEntity;
import com.trolltech.qt.QtBlockedSlot;

import static org.junit.Assert.*;

public class TestInjectedCodeV2 extends QApplicationTest {
    @Test
    public void testAbstractSocketProxyAuthenticationRequiredFromJavaToCpp()
    {
        AbstractSocketSubclass ass = new AbstractSocketSubclass(QAbstractSocket.SocketType.TcpSocket, null);

        QAbstractSocket as = new QAbstractSocket(QAbstractSocket.SocketType.TcpSocket, null);
        ass.connectProxyAuthenticationRequired(as);

        QNetworkProxy proxy = new QNetworkProxy();
        proxy.setUser("FOO");
        proxy.setPassword("BAR");

        QAuthenticator authenticator = new QAuthenticator();
        authenticator.setUser("ZIM");
        authenticator.setPassword("ZUM");

        as.proxyAuthenticationRequired.emit(proxy, authenticator);

        assertEquals("FOO", authenticator.user());
        assertEquals("BAR", authenticator.password());
    }


    private void myJavaSlot(QNetworkProxy proxy, QAuthenticator authenticator)
    {
        authenticator.setUser(proxy.password());
        authenticator.setPassword(proxy.user());
    }

    @Test
    public void testAbstractSocketProxyAuthenticationFromCppToJava()
    {
        AbstractSocketSubclass ass = new AbstractSocketSubclass(QAbstractSocket.SocketType.TcpSocket, null);

        QAbstractSocket as = new QAbstractSocket(QAbstractSocket.SocketType.TcpSocket, null);
        as.proxyAuthenticationRequired.connect(this, "myJavaSlot(QNetworkProxy, QAuthenticator)");

        QNetworkProxy proxy = new QNetworkProxy();
        proxy.setUser("FOO");
        proxy.setPassword("BAR");

        QAuthenticator authenticator = new QAuthenticator();
        authenticator.setUser("ZIM");
        authenticator.setPassword("ZUM");

        ass.emitProxyAuthenticationRequired(as, proxy, authenticator.nativePointer());

        assertEquals("ZIM", authenticator.user());
        assertEquals("ZUM", authenticator.password());
    }

    @Test
    public void testTcpSocketProxyAuthenticationFromCppToJava()
    {
        AbstractSocketSubclass ass = new AbstractSocketSubclass(QAbstractSocket.SocketType.TcpSocket, null);

        QTcpSocket as = new QTcpSocket(null);
        as.proxyAuthenticationRequired.connect(this, "myJavaSlot(QNetworkProxy, QAuthenticator)");

        QNetworkProxy proxy = new QNetworkProxy();
        proxy.setUser("FOO");
        proxy.setPassword("BAR");

        QAuthenticator authenticator = new QAuthenticator();
        authenticator.setUser("ZIM");
        authenticator.setPassword("ZUM");

        ass.emitProxyAuthenticationRequired(as, proxy, authenticator.nativePointer());

        assertEquals("ZIM", authenticator.user());
        assertEquals("ZUM", authenticator.password());
    }

    @Test
    public void testUdpSocketProxyAuthenticationFromCppToJava()
    {
        AbstractSocketSubclass ass = new AbstractSocketSubclass(QAbstractSocket.SocketType.TcpSocket, null);

        QUdpSocket as = new QUdpSocket(null);
        as.proxyAuthenticationRequired.connect(this, "myJavaSlot(QNetworkProxy, QAuthenticator)");

        QNetworkProxy proxy = new QNetworkProxy();
        proxy.setUser("FOO");
        proxy.setPassword("BAR");

        QAuthenticator authenticator = new QAuthenticator();
        authenticator.setUser("ZIM");
        authenticator.setPassword("ZUM");

        ass.emitProxyAuthenticationRequired(as, proxy, authenticator.nativePointer());

        assertEquals("ZIM", authenticator.user());
        assertEquals("ZUM", authenticator.password());
    }


    private QWidget receivedWidget = null;

    private class SenderQObjectSubclass extends SenderQObject {
        public Signal2<String, Integer> mappedJavaSignal = new Signal2<String, Integer>();

        private void receiverSlot(QWidget widget) {
            receivedWidget = widget;
        }

    }

    @Test
    public void testQGuiSignalMapperJava() {
        QGuiSignalMapper mapper = new QGuiSignalMapper();

        SenderQObjectSubclass receiverObject = new SenderQObjectSubclass();
        mapper.mappedQWidget.connect(receiverObject, "receiverSlot(QWidget)");

        SenderQObjectSubclass senderObject = new SenderQObjectSubclass();
        QWidget mappedWidget = new QWidget();

        mapper.setMapping(senderObject, mappedWidget);
        assertTrue(mapper.mapping(mappedWidget) == senderObject);

        senderObject.mappedJavaSignal.connect(mapper, "map()");
        senderObject.mappedJavaSignal.emit("foo", 0xf00);

        assertTrue(receivedWidget == mappedWidget);
    }

    @Test
    public void testQGuiSignalMapperCpp() {
        QGuiSignalMapper mapper = new QGuiSignalMapper();

        SenderQObjectSubclass receiverObject = new SenderQObjectSubclass();
        mapper.mappedQWidget.connect(receiverObject, "receiverSlot(QWidget)");

        SenderQObjectSubclass senderObject = new SenderQObjectSubclass();
        QWidget mappedWidget = new QWidget();

        mapper.setMapping(senderObject, mappedWidget);
        assertTrue(mapper.mapping(mappedWidget) == senderObject);

        senderObject.connect(mapper);
        senderObject.emitSignal();

        assertTrue(receivedWidget == mappedWidget);
    }

    private static class LookupHostQObject extends QObject
    {
        public String fromFirstSlot = "";
        public String fromSecondSlot = "";

        // You gotta love that fruity company... Their latest version
        // of Java for 32-bit, Java 1.5.0_16 introduces a bug where
        // generics innerclasses from other packages tends to lead to
        // internal compiler errors. They have of course moved on to
        // 64-bit and Java 1.6 as their new platform so this bug will
        // live forever.
        //
        // The error manifests itself in the helloObject.mySignal
        // access in the testLookupHostWithSignal function below.
        //
        // Bug ID: 6356636 on Bugparade
        public Signal1 mySignal = new Signal1<QHostInfo>(); {
            mySignal.connect(this, "secondSlot(QHostInfo)");
        }

        public void firstSlot(QHostInfo info) {
            fromFirstSlot = info.addresses().get(0).toString();
        }

        public void secondSlot(QHostInfo info) {
            fromSecondSlot = info.addresses().get(0).toString();
        }
    }

    @Test
    public void testLookupHostWithSlot()
    {
        LookupHostQObject helloObject = new LookupHostQObject();

        QHostInfo.lookupHost("ftp.trolltech.com", helloObject, "firstSlot");
        while (helloObject.fromFirstSlot.length() == 0) {
            QApplication.processEvents();
        }

        assertEquals("62.70.27.67", helloObject.fromFirstSlot);
    }

    @Test
    public void testLookupHostWithSignal()
    {
        LookupHostQObject helloObject = new LookupHostQObject();

        QHostInfo.lookupHost("ftp.trolltech.com", helloObject.mySignal);
        while (helloObject.fromSecondSlot.length() == 0) {
            QApplication.processEvents();
        }

        assertEquals("62.70.27.67", helloObject.fromSecondSlot);
    }

    @Test
    public void QStylesItemDelegateInitStyleOption() {
        QStyleOptionViewItem item = new QStyleOptionViewItem();
        StyledItemDelegateSubclass delegate = new StyledItemDelegateSubclass() {

            @Override
            protected void initStyleOption(QStyleOptionViewItem item, QModelIndex index) {
                item.setDecorationSize(new QSize(123, 456));
            }
        };

        delegate.initStyleOptionInStyledDelegate(item.nativePointer());

        assertEquals(123, item.decorationSize().width());
        assertEquals(456, item.decorationSize().height());
    }


    static class GraphicsWidgetSubclassSubclass extends GraphicsWidgetSubclass {

        @Override
        protected void initStyleOption(QStyleOption option) {
            if (option instanceof QStyleOptionGroupBox) {
                QStyleOptionGroupBox box = (QStyleOptionGroupBox) option;
                box.setLineWidth(321);
            }

            super.initStyleOption(option);
        }

    }

    @Test
    public void QGraphicsWidgetInitStyleOption() {
        GraphicsWidgetSubclassSubclass gwss = new GraphicsWidgetSubclassSubclass();
        int ret = GraphicsWidgetSubclass.callInitStyleOption(gwss);
        assertEquals(444, ret);
    }

    static class XmlEntityResolverSubclassSubclass extends XmlEntityResolverSubclass{

        @Override
        public ResolvedEntity resolveEntity(String publicId, String systemId) {
            if (publicId.equals("In java")) {
                QXmlInputSource src = new QXmlInputSource();
                src.setData("Made in Java");
                return new ResolvedEntity(systemId.equals("error"), src);
            } else {
                return super.resolveEntity(publicId, systemId);
            }
        }

        @Override
        public String errorString() {
            return null;
        }

    }


    @Test
    public void QXmlEntityResolverResolveEntityMadeInJava() {
        XmlEntityResolverSubclassSubclass xerss = new XmlEntityResolverSubclassSubclass();
        QXmlInputSource src = xerss.callResolveEntity("In java", "");
        assertEquals("Made in Java", src.data());
    }

    @Test
    public void QXmlEntityResolverResolveEntityMadeInJavaWithError() {
        XmlEntityResolverSubclassSubclass xerss = new XmlEntityResolverSubclassSubclass();
        QXmlInputSource src = xerss.callResolveEntity("In java", "error");
        assertEquals("Made in Java with error", src.data());
    }

    @Test
    public void QXmlEntityResolverResolveEntityMadeInCpp() {
        XmlEntityResolverSubclassSubclass xerss = new XmlEntityResolverSubclassSubclass();
        QXmlInputSource src = xerss.callResolveEntity("c++", "");
        assertEquals("Made in C++", src.data());
    }

    @Test
    public void QXmlEntityResolverResolveEntityMadeInCppWithError() {
        XmlEntityResolverSubclassSubclass xerss = new XmlEntityResolverSubclassSubclass();
        QXmlInputSource src = xerss.callResolveEntity("c++", "error");
        assertEquals("Made in C++ with error", src.data());
    }

    @Test
    public void QAbstractTextDocumentLayoutHandlerForObject() {
        QTextDocument document = new QTextDocument();
        QAbstractTextDocumentLayout layout = new QPlainTextDocumentLayout(document);

        QTextObjectInterface myTextObjectInterface = new QTextObjectInterface() {

            @QtBlockedSlot
            public void drawObject(QPainter painter, QRectF rect, QTextDocument doc, int posInDocument,
                                   QTextFormat format) {
            }

            @QtBlockedSlot
            public QSizeF intrinsicSize(QTextDocument doc, int posInDocument, QTextFormat format) {
                return null;
            }
        };

        layout.registerHandler(QTextFormat.ObjectTypes.UserObject.value()+1, myTextObjectInterface);
        assertEquals(myTextObjectInterface, layout.handlerForObject(QTextFormat.ObjectTypes.UserObject.value()+1));
    }

    @Test
    public void QAbstractTextDocumentLayoutRegisterHandlerWrongClass() {
        QTextDocument document = new QTextDocument();
        QAbstractTextDocumentLayout layout = new QPlainTextDocumentLayout(document);
        QObject objectWithWrongType = new QObject();

        boolean caughtException = false;
        try {
            layout.registerHandler(QTextFormat.ObjectTypes.ImageObject.value(), objectWithWrongType);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        }

        assertTrue(caughtException);
    }
}
