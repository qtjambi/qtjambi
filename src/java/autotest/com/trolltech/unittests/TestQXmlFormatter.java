package com.trolltech.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.trolltech.qt.core.QAbstractFileEngine;
import com.trolltech.qt.core.QBuffer;
import com.trolltech.qt.core.QIODevice;
import com.trolltech.qt.xmlpatterns.QXmlFormatter;
import com.trolltech.qt.xmlpatterns.QXmlItem;
import com.trolltech.qt.xmlpatterns.QXmlName;
import com.trolltech.qt.xmlpatterns.QXmlQuery;
import com.trolltech.qt.xmlpatterns.QXmlResultItems;
import com.trolltech.qt.xmlpatterns.QXmlSerializer;

public class TestQXmlFormatter extends QApplicationTest {

    QXmlQuery query;
    QXmlSerializer serializer;
    QBuffer buffer;
    QXmlResultItems xmlResultItems;
    QXmlFormatter formatter;
    String samplePath1 = "doc('classpath:com/trolltech/unittests/xquerySample1.xml')";
    String samplePath2 = "doc('classpath:com/trolltech/unittests/xmlSample2.xml')";

    //@BeforeClass
    //public static void init() {
    //    QAbstractFileEngine.addSearchPathForResourceEngine(".");
    //}

    @Before
    public void setUp() throws Exception {
        query = new QXmlQuery();
        xmlResultItems = new QXmlResultItems();
        buffer = new QBuffer();
    }

    @After
    public void tearDown() throws Exception {
        query = null;
        if(buffer != null)
            buffer.close();
    }

    @Test
    public void testConstruct() {
        assertFalse(query == null);
        assertFalse(query.isValid());
    }

    @Test
    public void testValid() {
        query.setQuery("doc('')");
        assertTrue(query.isValid());
    }

    @Test
    public void testEvaluateToString() {
        query.setQuery(samplePath2 + "/persons/person/firstname[1]");
        String result = query.evaluateTo();

        String[] expected = { "<firstname>John</firstname>",
                "<firstname>Jane</firstname>", "<firstname>Baby</firstname>" };
        String[] results = result.split("\n");

        int i = 0;
        for (String str : results) {
            assertEquals(expected[i++], str);
        }
    }

    @Test
    public void testEvaluateToQXmlSerializer() {
        query.setQuery(samplePath1 + "/a/p");
        TestSerializerClass clazz = new TestSerializerClass(query, buffer);
        assertTrue(query.isValid());
        query.evaluateTo(clazz.outputDevice());
    }
    
    @Test
    public void testEvaluateToQXmlResultItems() {
        query.setQuery(samplePath1 + "/a/p");
        assertTrue(query.isValid());
        query.evaluateTo(xmlResultItems);
        
        QXmlItem item = xmlResultItems.next();
        while(!item.isNull()) {
            if (item.isNode()) {
                query.setFocus(item);
                query.setQuery(samplePath1 + "/a/p/string()");
                String s = query.evaluateTo();
                assertEquals("Some Text in p", s);
            }
            item = xmlResultItems.next();
        }
    }
    
}

class TestSerializerClass extends QXmlSerializer {

    public TestSerializerClass(QXmlQuery query, QIODevice outputDevice) {
        super(query, outputDevice);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void attribute(QXmlName name, String value) {
        System.out.println(value);
    }

    @Override
    public void atomicValue(Object value) {
        System.out.println(value.toString());
    }

    @Override
    public void startDocument() {
        System.out.println("doc start");
    }

    @Override
    public void characters(String str) {
        System.out.println(str);
    }
}
