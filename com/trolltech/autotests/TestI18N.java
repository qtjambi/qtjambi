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
        QAbstractFileEngine.addSearchPathForResourceEngine(".");        
        QTranslator translator = new QTranslator();
        assertTrue(translator.load("classpath:com/trolltech/autotests/i18n.qm"));
        QApplication.installTranslator(translator);

        SimpleNotTranslated test = new SimpleNotTranslated();
        test.testNotTranslated();
    }

    public class SimpleNotTranslated extends QObject {
        public void testNotTranslated() {
            assertEquals(tr("test"), "test");
            assertEquals(tr("\u03c0"), "\u03c0"); // Pi
            assertEquals(tr("\u06a0"), "\u06a0"); // Arabic
            assertEquals(tr("not translated ø"), "not translated ø");
            assertEquals(tr("not translated \u00e8"), "not translated è");
            assertEquals(tr("Hello è"), "Hello \u00e8");
            assertEquals(tr("not translated \n123"), "not translated \n123");
        }
    }

    @Test
    public void TestSimpleTranslated() {
        QAbstractFileEngine.addSearchPathForResourceEngine(".");

        QTranslator translator = new QTranslator();
        assertTrue(translator.load("classpath:com/trolltech/autotests/i18n.qm"));
        QApplication.installTranslator(translator);

        SimpleTranslated test = new SimpleTranslated();
        test.testTranslated();
    }

    @Test
    public void TestSimpleTranslatedWithTranslationsLoaded() {
        QAbstractFileEngine.addSearchPathForResourceEngine(".");

        QTranslator translator = new QTranslator();
        assertTrue(translator.load("classpath:com/trolltech/autotests/i18n.qm"));
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
            assertEquals(tr("\u03c0"), "ok Pi"); // Pi
            assertEquals(tr("\u06a0"), "ok Arabisk"); // Arabic
            assertEquals(tr("Pi"), "\u03c0"); // Pi
            assertEquals(tr("Arabisk"), "\u06a0"); // Arabic
            assertEquals(tr("è"), "ok"); // e8
            assertEquals(tr("not translated abc"), "ikke oversatt æøå");
            assertEquals(tr("not translated æøå"), "ikke oversatt æøå");
            assertEquals(tr("not translated 2 \u00e8"), "ikke oversatt 2 è");
            assertEquals(tr("Hello è"), "Halloys \u00e8");
            assertEquals(tr("My mother 123"), "Min mor 123");
        }
    }

    @Test
    public void TestQTranslatorNotTranslated() {
        QTranslator translator = new QTranslator();
        assertTrue(translator.load("classpath:com/trolltech/autotests/i18n.qm"));
        assertEquals(translator.translate("my context", "do not translate this"), "");
        assertEquals(translator.translate("my context", "do not translate this æøå"), "");
        assertEquals(translator.translate("my context", "do not translate this \u06a0"), "");
    }

    @Test
    public void TestQTranslatorTranslated() {
        QTranslator translator = new QTranslator();
        assertTrue(translator.load("classpath:com/trolltech/autotests/i18n.qm"));
        assertEquals(translator.translate("my context", "translate this"), "oversett dette");
        assertEquals(translator.translate("my context", "translate this æøå"), "oversett dette æøå");
        assertEquals(translator.translate("my context", "translate this \u06a0"), "oversett dette \u06a0");
    }

    @Test
    public void TestQTranslatorTranslatedContext() {
        QTranslator translator = new QTranslator();
        assertTrue(translator.load("classpath:com/trolltech/autotests/i18n.qm"));
        assertEquals(translator.translate("øæå", "translate this"), "oversett dette");
        assertEquals(translator.translate("\u06a0", "translate this æøå"), "oversett dette æøå");
        assertEquals(translator.translate("\u03c0", "translate this \u03c0"), "oversett dette \u03c0");
    }


    @Test
    public void TestReimplementQTranslator() {
        QTranslator translator = new QTranslator(){

            @Override
            public String translate(String context, String sourceText, String comment) {
                return super.translate(context, sourceText, comment).toUpperCase();
            }

        };
        assertTrue(translator.load("classpath:com/trolltech/autotests/i18n.qm"));
        assertEquals(translator.translate("øæå", "translate this"), "OVERSETT DETTE");
        assertEquals(translator.translate("\u06a0", "translate this æøå"), "OVERSETT DETTE ÆØÅ");
        assertEquals(translator.translate("\u03c0", "translate this \u03c0", "Comment"), "OVERSETT DETTE Π");
    }

    @Test
    public void TestQTranslatorAdvancedStrings() {
        QTranslator translator = new QTranslator();
        assertTrue(translator.load("classpath:com/trolltech/autotests/i18n.qm"));
        assertEquals(translator.translate("strings", "a" + "b" + "c"), "abc");
        assertEquals(translator.translate("strings", "c"
                    +"a" + "" +
                    "b"), "cab");

    }
}
