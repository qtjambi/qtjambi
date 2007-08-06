package com.trolltech.autotests;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.*;

import org.junit.*;

import com.trolltech.qt.core.QSettings;

public class TestQSettings extends QApplicationTest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Test
    public void writeSettingsSimple() {
        QSettings settings = new QSettings("Trolltech", "Test");
        settings.sync();

        settings.setValue("int", 5);
        settings.setValue("double", 5.000001d);
        settings.setValue("String", "String");

        settings.sync();
    }

    @Test
    public void readSettingsSimple() {
        QSettings settings = new QSettings("Trolltech", "Test");
        settings.sync();

        assertEquals(5, Integer.parseInt((String) settings.value("int")));
        assertEquals(5.000001d, Double.parseDouble((String) settings.value("double")));
        assertEquals("String", settings.value("String"));
    }

    @Test
    public void writeSettingsCollection() {
        QSettings settings = new QSettings("Trolltech", "Test");
        settings.sync();

        List<String> list = new Vector<String>();
        for (int i = 0; i < 10; i++) {
            list.add("entry-" + i);
        }
        settings.setValue("test", list);
        settings.sync();
    }

    @Test
    public void readSettingsCollection() {
        QSettings settings = new QSettings("Trolltech", "Test");
        settings.sync();

        List<String> list = (List<String>) settings.value("test", new Vector<String>());

        for (int i = 0; i < 10; i++) {
            assertEquals("entry-" + i, list.get(i));
        }
    }

    @Test
    public void readSettingsEmpty() {
        QSettings settings = new QSettings("Trolltech", "Test");
        settings.sync();

        String res = (String) settings.value("empty", "ok");
        assertEquals("ok", res);

        res = (String) settings.value("empty");
        assertNull(res);
    }

    public class Custom implements Serializable {
        private static final long serialVersionUID = 1L;

        String name;
        int integer;
        Custom object;
    }

    @Test
    public void writeSettingsCustomClass() {
        QSettings settings = new QSettings("Trolltech", "Test");
        settings.sync();

        Custom custom = new Custom();
        custom.name = "abc";
        custom.integer = 123;
        custom.object = new Custom();

        settings.setValue("custom", custom);
        settings.sync();
    }

    @Test
    public void readSettingsCustomClass() {
        QSettings settings = new QSettings("Trolltech", "Test");
        settings.sync();

        Custom custom = (Custom) settings.value("custom");
        assertNotNull(custom);
        assertEquals(custom.name, "abc");
        assertEquals(custom.integer, 123);
        assertEquals(custom.object.getClass(), Custom.class);
    }
}
