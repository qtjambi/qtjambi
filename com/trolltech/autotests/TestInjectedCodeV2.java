package com.trolltech.autotests;

import org.junit.Test;
import com.trolltech.autotests.generated.*;
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.gui.QAccessibleTableInterface.CellAtIndex;
import com.trolltech.qt.network.*;
import com.trolltech.qt.network.QAbstractSocket.SocketType;
import com.trolltech.qt.opengl.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.xml.*;
import static org.junit.Assert.*;
import org.junit.*;

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
    
}
