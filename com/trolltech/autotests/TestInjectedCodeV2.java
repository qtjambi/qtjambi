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
import com.trolltech.qt.gui.*;
import com.trolltech.qt.network.*;
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
    
    
    @SuppressWarnings("unused")
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
        
        @SuppressWarnings("unused")
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
}
