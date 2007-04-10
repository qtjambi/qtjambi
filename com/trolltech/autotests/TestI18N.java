package com.trolltech.autotests;

import static org.junit.Assert.*;
import org.junit.*;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.QApplication;

public class TestI18N extends QApplicationTest {

	@Test
	public void TestSimpleNotTranslated() {
		QApplication.installTranslator(null);
		SimpleNotTranslated test = new SimpleNotTranslated();
		test.testNotTranslated();
	}
	
	@Test
	public void TestSimpleNotTranslatedWithTranslationsLoaded() {
		QTranslator translator = new QTranslator();
		assertTrue( translator.load("classpath:com/trolltech/autotests/i18n.qm"));
	    QApplication.installTranslator(translator);
		
		SimpleNotTranslated test = new SimpleNotTranslated();
		test.testNotTranslated();
	}
	
	public class SimpleNotTranslated extends QObject {
		public void testNotTranslated() {
			assertEquals(tr("test"), "test");
			assertEquals(tr("\u03c0"), "\u03c0"); //  Pi
			assertEquals(tr("\u06a0"), "\u06a0"); //  Arabic
			assertEquals(tr("not translated ø"), "not translated ø");
			assertEquals(tr("not translated \u00e8"), "not translated è");
			assertEquals(tr("Hello è"), "Hello \u00e8");
			assertEquals(tr("not translated \n123"), "not translated \n123");
		}
	}
	
	@Test
	public void TestSimpleTranslated() {
		QApplication.installTranslator(null);
		SimpleTranslated test = new SimpleTranslated();
		test.testTranslated();
	}
	
	@Test
	public void TestSimpleTranslatedWithTranslationsLoaded() {
		QTranslator translator = new QTranslator();
		
		assertTrue( translator.load("classpath:com/trolltech/autotests/i18n.qm"));
	    QApplication.installTranslator(translator);
	    	    
		SimpleTranslated test = new SimpleTranslated();
		test.testTranslated();
	}
	
	public class SimpleTranslated extends QObject {
		public void testTranslated() {
			assertEquals(tr("test"), "test");
			assertEquals(tr("one\ntwo"), "en\nto");
			assertEquals(tr("abc-ø-abc"), "abc-ø-abc"); // e8
			assertEquals(tr("\u00e8"), "ok"); // e8
			assertEquals(tr("\u03c0"), "ok Pi"); //  Pi
			assertEquals(tr("\u06a0"), "ok Arabisk"); //  Arabic
			assertEquals(tr("è"), "ok"); // e8
			assertEquals(tr("not translated abc"), "ikke oversatt æøå");
			assertEquals(tr("not translated æøå"), "ikke oversatt æøå");
			assertEquals(tr("not translated 2 \u00e8"), "ikke oversatt 2 è");
			assertEquals(tr("Hello è"), "Halloys \u00e8");
			assertEquals(tr("My mother 123"), "Min mor 123");
		}
	}
}
