package com.trolltech.unittests;

import static org.junit.Assert.*;

import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QIODevice;
// Using com.trolltech.qt.xml.* as com.trolltech.qt.xml.QXmlStreamReader is deprecated
import com.trolltech.qt.xml.*;

// See also TestCoreQXmlStreamReader
@SuppressWarnings("deprecation")
public class TestXmlQXmlStreamReader extends QApplicationTest {

	// MacOSX version of Qt does not have this API it has only com.trolltech.qt.core.QXmlStreamReader
	QXmlStreamReader xmlr;
	QFile xmlFile;
	QIODevice iod;

	@org.junit.Before
	public void setUp() throws Exception {
		xmlFile = new QFile("classpath:com/trolltech/unittests/xmlSample1.xml");
		xmlr = new QXmlStreamReader();
	}

	@org.junit.After
	public void tearDown() throws Exception {
	}

	@org.junit.Test
	public void testQXmlStreamReader() {
		assertTrue(xmlFile.exists());
		QXmlStreamReader.TokenType token1 = xmlr.readNext();
		System.out.println(token1.name());
		QXmlStreamReader.TokenType token2 = xmlr.readNext();
		System.out.println(token2.name());
	}

}
