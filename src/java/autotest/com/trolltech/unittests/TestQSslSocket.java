package com.trolltech.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import com.trolltech.qt.core.QDateTime;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.network.QSslCertificate;
import com.trolltech.qt.network.QSslSocket;

public class TestQSslSocket {

	private QSslSocket socket;
	private List<QSslCertificate> certs = new ArrayList<QSslCertificate>();
	private QDateTime first = QDateTime.fromString("Mon Jan 18 23:59:59 2038",
			"ddd MMM d HH:mm:ss yyyy");
	private QDateTime second = QDateTime.fromString("Sat Oct 25 08:32:46 2036",
			"ddd MMM d HH:mm:ss yyyy");
	private QDateTime[] date = { first, second };

	@Before
	public void setUp() throws Exception {
		socket = new QSslSocket(new QObject());
		socket.connectToHostEncrypted("www.google.com", (short) 443);
		// block the calling thread until an encrypted connection has been
		// established.
		socket.waitForEncrypted(5000);
	}

	@After
	public void tearDown() throws Exception {
		socket.close();
	}

	@org.junit.Test
	public void testConnectToHostEncrypted() {
		assertTrue(socket.isValid());
		assertTrue(socket.isEncrypted());
	}

	@org.junit.Test
	public void testCaCertificates() {
		certs = socket.caCertificates();
		assertNotNull(certs);
		assertTrue(certs.size() > 0);

		certs = certs.subList(0, 2);
		Iterator<QSslCertificate> i = certs.iterator();
		int j = 0;
		
		while (i.hasNext()) {
			QSslCertificate cert = i.next();
			QDateTime expDate = date[j++];
			// convert local time to UTC since certification exp. date is given
			// in UTC too
			expDate.setTimeSpec(Qt.TimeSpec.UTC);

			assertTrue(cert.isValid());
			assertEquals(cert.expiryDate(), expDate);
		}
	}
}
