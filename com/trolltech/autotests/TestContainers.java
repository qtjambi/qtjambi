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

import java.util.*;

import com.trolltech.autotests.generated.*;
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import static org.junit.Assert.*;

import org.junit.*;

public class TestContainers extends QApplicationTest {

    private Tulip tulip;

    @Before
    public void setUp() throws Exception {
        tulip = new Tulip();
    }

    @Test
    public void run_writeReadVectorOfPairs() {
        // Generate my data...
        List<QPair<Double, QColor>> write_stops = new ArrayList<QPair<Double, QColor>>();
        for (int i = 0; i < 11; ++i) {
            QPair<Double, QColor> p = new QPair<Double, QColor>(i / 10.0, new QColor(i * 255 / 10, 0, 0));
            write_stops.add(p);
        }

        // Set data..
        QLinearGradient gradient = new QLinearGradient(0, 0, 100, 100);
        gradient.setStops(write_stops);

        // Read back...
        List<QPair<Double, QColor>> read_stops = gradient.stops();

        assertEquals(write_stops.size(), read_stops.size());
        for (int i = 0; i < write_stops.size(); ++i) {
            QPair<Double, QColor> wstop = write_stops.get(i), rstop = read_stops.get(i);
            assertEquals(wstop.first, rstop.first);
            assertEquals(wstop.second.red(), rstop.second.red());
            assertEquals(wstop.second.green(), rstop.second.green());
            assertEquals(wstop.second.blue(), rstop.second.blue());
        }
    }

    @Test
    public void run_objectChildren() {
        QObject root = new QObject();
        QObject child1 = new QObject(root);
        QObject child2 = new QObject(root);
        QObject child3 = new QObject(root);
        child1.setObjectName("child1");
        child2.setObjectName("child2");
        child3.setObjectName("child3");

        List<QObject> children = root.children();
        assertEquals(children.size(), 3);
        assertEquals(children.get(0).objectName(), "child1");
        assertEquals(children.get(1).objectName(), "child2");
        assertEquals(children.get(2).objectName(), "child3");

        assertEquals(children.get(0), child1);
        assertEquals(children.get(1), child2);
        assertEquals(children.get(2), child3);
    }

    @Test
    public void run_QStringList() {
        List<String> items = new ArrayList<String>();
        for (int i = 0; i < 10; ++i)
            items.add("" + i);

        // Set the paths.
        QCoreApplication.setLibraryPaths(items);

        // Get the paths back..
        List<String> read_items = QCoreApplication.libraryPaths();
        assertEquals(items.size(), read_items.size());
        for (int i = 0; i < 10; ++i) {
            assertEquals(items.get(i), read_items.get(i));
        }
    }

    @Test
    public void run_testLinkedLists() {
        LinkedList<Integer> l = new LinkedList<Integer>();
        for (int i = 0; i < 10; ++i)
            l.add(i);

        LinkedList<Integer> l2 = tulip.do_QLinkedList_of_int(l);
        assertTrue(l2 != null);
        assertEquals(l, l2);
    }

    @Test
    public void run_testLists() {
        List<Integer> l = new ArrayList<Integer>();
        for (int i = 0; i < 10; ++i)
            l.add(i);

        List<Integer> l2 = tulip.do_QList_of_int(l);
        assertTrue(l2 != null);
        assertEquals(l, l2);
    }

    @Test
    public void run_testVectors() {
        List<Integer> l = new ArrayList<Integer>();
        for (int i = 0; i < 10; ++i)
            l.add(i);

        List<Integer> l2 = tulip.do_QVector_of_int(l);
        assertTrue(l2 != null);
        assertEquals(l, l2);
    }

    @Test
    public void run_testHash() {
        HashMap<String, String> h = new HashMap<String, String>();
        for (int i = 0; i < 10; ++i)
            h.put("key_" + String.valueOf(i), "value_" + String.valueOf(i));
        HashMap<String, String> h2 = tulip.do_QHash_of_strings(h);
        assertTrue(h2 != null);
        assertEquals(h, h2);
    }

    @Test
    public void run_testMap() {
        SortedMap<String, String> s = new TreeMap<String, String>();
        for (int i = 0; i < 10; ++i)
            s.put("key_" + String.valueOf(i), "value_" + String.valueOf(i));
        SortedMap<String, String> s2 = tulip.do_QMap_of_strings(s);
        assertTrue(s2 != null);
        assertEquals(s, s2);
    }

    @Test
    public void run_testStringList() {
        List<String> s = new ArrayList<String>();
        for (int i = 0; i < 10; ++i)
            s.add("value_" + String.valueOf(i));

        List<String> s2 = tulip.do_QStringList(s);
        assertTrue(s2 != null);
        assertEquals(s, s2);
    }

    @Test
    public void run_testQPair() {
        QPair<Integer, Integer> p1 = new QPair<Integer, Integer>(1, 2);
        QPair<Integer, Integer> p2 = tulip.do_QPair_of_ints(p1);

        assertTrue(p1 != null);
        assertTrue(p1.first != null);
        assertTrue(p1.second != null);
        assertTrue(p2 != null);
        assertTrue(p2.first != null);
        assertTrue(p2.second != null);

        assertEquals(p1, p2);
    }

    @Test
    public void run_testStack() {
        Stack<Integer> s = new Stack<Integer>();
        for (int i = 0; i < 10; ++i)
            s.push(i);
        Stack<Integer> s2 = tulip.do_QStack_of_int(s);
        assertTrue(s2 != null);
        assertEquals(s, s2);
    }

    @Test
    public void run_testSet() {
        Set<Integer> s = new HashSet<Integer>();
        for (int i = 0; i < 10; ++i)
            s.add(i);
        Set<Integer> s2 = tulip.do_QSet_of_int(s);
        assertTrue(s2 != null);
        assertEquals(s, s2);
    }

    @Test
    public void run_testQueue() {
        Queue<Integer> q = new LinkedList<Integer>();
        for (int i = 0; i < 10; ++i)
            q.add(i);
        Queue<Integer> q2 = tulip.do_QQueue_of_int(q);
        assertTrue(q2 != null);
        assertEquals(q, q2);
    }


    @Test
    public void run_testQVector_outofbounds() {
        QVector_int vector = new QVector_int();

        boolean got;

        got = false; try { vector.at(10); } catch (Exception e) { got = true; } assertTrue(got);
        got = false; try { vector.replace(23, 14); } catch (Exception e) { got =true; } assertTrue(got);
        got = false; try { vector.pop_back(); } catch (Exception e) { got =true; } assertTrue(got);
        got = false; try { vector.pop_front(); } catch (Exception e) { got =true; } assertTrue(got);
        got = false; try { vector.remove(23); } catch (Exception e) { got =true; } assertTrue(got);
        got = false; try { vector.remove(23, 14); } catch (Exception e) { got =true; } assertTrue(got);
    }

    @Test
    public void run_testQList_outofbounds() {
        QList_int list = new QList_int();

        boolean got;

        got = false; try { list.at(10); } catch (Exception e) { got = true; } assertTrue(got);
        got = false; try { list.replace(23, 14); } catch (Exception e) { got =true; } assertTrue(got);
        got = false; try { list.pop_back(); } catch (Exception e) { got =true; } assertTrue(got);
        got = false; try { list.pop_front(); } catch (Exception e) { got =true; } assertTrue(got);
        got = false; try { list.move(14, 15); } catch (Exception e) { got =true; } assertTrue(got);
        got = false; try { list.swap(15, 14); } catch (Exception e) { got =true; } assertTrue(got);
        got = false; try { list.removeAt(14); } catch (Exception e) { got =true; } assertTrue(got);
        got = false; try { list.takeAt(14); } catch (Exception e) { got =true; } assertTrue(got);
    }

}
