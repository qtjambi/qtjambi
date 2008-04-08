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

import org.junit.Test;
import com.trolltech.autotests.generated.*;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QSize;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.xml.QXmlInputSource;
import com.trolltech.qt.xml.QXmlEntityResolver.ResolvedEntity;

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

        assertEquals("BAR", authenticator.user());
        assertEquals("FOO", authenticator.password());
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

        assertEquals("BAR", authenticator.user());
        assertEquals("FOO", authenticator.password());
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

        assertEquals("BAR", authenticator.user());
        assertEquals("FOO", authenticator.password());
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

        public Signal1<QHostInfo> mySignal = new Signal1<QHostInfo>(); {
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

}
