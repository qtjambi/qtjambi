package com.trolltech.unittests;

import static org.junit.Assert.*;

import com.trolltech.qt.core.QAbstractFileEngine;
import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QIODevice;
import com.trolltech.qt.core.QXmlStreamAttributes;
import com.trolltech.qt.core.QXmlStreamNamespaceDeclaration;
import com.trolltech.qt.core.QXmlStreamReader;
import com.trolltech.qt.core.QXmlStreamReader.TokenType;

public class TestCoreQXmlStreamReader {

	private QXmlStreamReader xmlr;
	private QXmlStreamReader xmlrNoDevice;
	private QFile xmlFile;
	private TokenType token;
	private String person[] = { "John", "Jane" };
	private String namespace[] = { "c", "d" };
	private String namespaceuri[] = { "http://qt-jambi.org", "http://en.wikipedia.org" };
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
		xmlrNoDevice = new QXmlStreamReader();
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
		assertTrue(xmlr.isStartDocument());
	}

	@org.junit.Test
	public void testTokenType() {
		assertEquals(xmlr.tokenType(), TokenType.StartDocument);
		assertTrue(xmlr.isStartDocument());
		while (!xmlr.atEnd()) {
			token = xmlr.readNext();
			if (token == TokenType.StartElement) {
				assertEquals(xmlr.tokenType(), TokenType.StartElement);
				assertTrue(xmlr.isStartElement());
				if (token.name().equals("persons"))
					continue;
				if (xmlr.name().equals("person")) {
					// skip the text() of StartElement token
					xmlr.readNext();
					assertEquals(xmlr.tokenType(), TokenType.Characters);
					assertTrue(xmlr.isCharacters());
				}
			} else if (token == TokenType.EndElement) {
				assertEquals(xmlr.tokenType(), TokenType.EndElement);
				assertTrue(xmlr.isEndElement());
			}
		}
		assertEquals(xmlr.tokenType(), TokenType.EndDocument);
		assertTrue(xmlr.isEndDocument());
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
					// skip the text() of StartElement token
					xmlr.readNext();
					assertEquals(xmlr.text(), person[i++]);
				}
			}
		}
	}

	@org.junit.Test
	public void testAttributes() {
		QXmlStreamAttributes attr;
		while (!xmlr.atEnd()) {
			token = xmlr.readNext();
			if (token == TokenType.StartElement) {
				if (token.name().equals("persons"))
					continue;
				if (xmlr.name().equals("person")) {
					// attributes extracted from <person></person>
					attr = xmlr.attributes();
					assertTrue(attr.count() == 1);
					assertEquals(attr.value("id"), person[i++]);
				}
			}
		}
	}

	@org.junit.Test
	public void testSkipCurrentElement() {
		while (!xmlr.atEnd()) {
			token = xmlr.readNext();
			if (token == TokenType.StartElement) {
				if (token.name().equals("persons"))
					continue;
				if (xmlr.name().equals("person")) { //	 [<person id="John">]John</person>
					xmlr.skipCurrentElement(); //	 <person id="John">John[</person>]
					xmlr.readNext(); 		   //[	]<person id="Jane">Jane</person>
					xmlr.readNext(); 		   //	[<person id="Jane">]Jane</person>
					xmlr.readNext(); 		   //	 <person id="Jane">[Jane]</person>
					assertEquals(xmlr.text(), person[1]);
				}
			}
		}
	}

	@org.junit.Test
	public void testReadElementText() {
		while (!xmlr.atEnd()) {
			token = xmlr.readNext();
			if (token == TokenType.StartElement) {
				if (token.name().equals("persons"))
					continue;
				if (xmlr.name().equals("person")) {
					// 
					assertEquals(xmlr.tokenType(), TokenType.StartElement);
					//consume the start element completely
					assertEquals(xmlr.readElementText(), person[i++]);
					assertEquals(xmlr.tokenType(), TokenType.EndElement);
				}
			}
		}
	}
	
	@org.junit.Test
	public void testLineNumber() {
		//StartDocument
		assertEquals(xmlr.lineNumber(), 1);
		xmlr.readNext();
		//StartElement - <persons></persons>
		assertEquals(xmlr.lineNumber(), 2);
		//StartElement - <person></person>
		xmlr.readNextStartElement();
		assertEquals(xmlr.lineNumber(), 3);
		assertEquals(xmlr.readElementText(), "John");
	}
	
	@org.junit.Test
	public void testIsWhitespace() {
		while (!xmlr.atEnd()) {
			token = xmlr.readNext();
			if (token == TokenType.StartElement) {
				if (token.name().equals("persons"))
					continue;
				if (xmlr.name().equals("person")) { //	[<person id="John">]John</person>
					xmlr.skipCurrentElement(); 		//	 <person id="John">John[</person>]
					xmlr.readNext(); 		   		//[	]<person id="Jane">Jane</person>
					assertTrue(xmlr.isWhitespace());// ^
					xmlr.readNext(); 		   		//	[<person id="Jane">]Jane</person>
					xmlr.readNext(); 		   		//	 <person id="Jane">[Jane]</person>
					assertEquals(xmlr.text(), person[1]);
				}
			}
		}
	}
	
	@org.junit.Test
	public void testAddData() {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<persons>");
		sb.append("<person>");
		sb.append("John");
		sb.append("</person>");
		sb.append("<person>");
		sb.append("Jane");
		sb.append("</person>");
		sb.append("</persons>");
		xmlrNoDevice.addData(sb.toString());
		assertEquals(TokenType.StartDocument, xmlrNoDevice.readNext());
		assertEquals(xmlrNoDevice.documentVersion(), "1.0");
		//xmlrNoDevice.skipCurrentElement();
		while (!xmlrNoDevice.atEnd()) {
			token = xmlrNoDevice.readNext();
			if (token == TokenType.StartElement) {
				if (token.name().equals("persons"))
					continue;
				if (xmlrNoDevice.name().equals("person")) {
					// 
					assertEquals(xmlrNoDevice.tokenType(), TokenType.StartElement);
					//consume the start element completely
					assertEquals(xmlrNoDevice.readElementText(), person[i++]);
					assertEquals(xmlrNoDevice.tokenType(), TokenType.EndElement);
				}
			}
		}
	}
	
	@org.junit.Test
	public void testRaiseError() {
		while (!xmlr.atEnd() && !xmlr.hasError()) {
			token = xmlr.readNext();
			if (token == TokenType.StartElement) {
				if (token.name().equals("persons"))
					continue;
				if (xmlr.name().equals("person")) {
					assertEquals(xmlr.readElementText(), "John");
					xmlr.raiseError("An error occurred...");
				}
			}
		}
		assertEquals(xmlr.errorString(), "An error occurred...");
		assertEquals(xmlr.tokenType(), TokenType.Invalid);
	}
	
	@org.junit.Test
	public void testNamespace() {
		while (!xmlr.atEnd() && !xmlr.hasError()) {
			token = xmlr.readNext();
			if (token == TokenType.StartElement) {
				if (token.name().equals("persons"))
					continue;
				if (xmlr.name().equals("person")) {
					assertEquals(xmlr.qualifiedName(), xmlr.prefix() + ":" + xmlr.name());
					assertEquals(xmlr.namespaceUri(), namespaceuri[i++]);
				}
			}
		}
	}
	
	
	
}
