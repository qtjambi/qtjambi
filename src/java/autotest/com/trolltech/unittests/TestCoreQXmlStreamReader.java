package com.trolltech.unittests;

import static org.junit.Assert.*;

import com.trolltech.qt.core.QAbstractFileEngine;
import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QIODevice;
import com.trolltech.qt.core.QXmlStreamReader;
import com.trolltech.qt.core.QXmlStreamReader.TokenType;

public class TestCoreQXmlStreamReader {

	private QXmlStreamReader xmlr;
	private QFile xmlFile;
	private TokenType token;
	private String person[] = {"John", "Jane"};
	private int i = 0;

	@org.junit.BeforeClass
	public static void setUpClass() {
		QAbstractFileEngine.addSearchPathForResourceEngine(".");
	}

	@org.junit.Before
	public void setUp() throws Exception {
		xmlFile = new QFile("classpath:com/trolltech/unittests/xmlSample1.xml");
		xmlFile.open(new QIODevice.OpenMode(QIODevice.OpenModeFlag.ReadOnly));
		xmlr = new QXmlStreamReader(xmlFile);
		/*
		 * placing the first .readNext() here because the first token is XML
		 * definition the definition
		 */
		token = xmlr.readNext();
	}

	@org.junit.After
	public void tearDown() throws Exception {
		xmlFile.close();
	}

	@org.junit.Test
	public void testStartDocument() {
		assertEquals(token, TokenType.StartDocument);
	}

	@org.junit.Test
	public void testDocumentVersion() {
		assertEquals(xmlr.documentVersion(), "1.0");
	}

	@org.junit.Test
	public void testStartElement() {
		TokenType token = xmlr.readNext();
		assertEquals(TokenType.StartElement, token);
	}

	@org.junit.Test
	public void testName() {
		xmlr.readNext();
		assertEquals(xmlr.name(), "persons");
	}

	@org.junit.Test
	public void testProcessing1() {
		while (!xmlr.atEnd()) {
			token = xmlr.readNext();
			if (token == TokenType.StartElement) {
				if (token.name().equals("persons"))
					continue;
				if (xmlr.name().equals("person")) {
					//skip the text() of StartElement token
					xmlr.readNext();
					assertEquals(xmlr.text(), person[i++]);
				}
			}
		}
	}

}
