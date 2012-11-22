/****************************************************************************
**
** Copyright (C) 2012 Darryl L. Miles.  All rights reserved.
** Copyright (C) 2012 D L Miles Consulting Ltd.  All rights reserved.
**
** This file is part of Qt Jambi.
**
**
** $BEGIN_LICENSE$
** GNU Lesser General Public License Usage
** This file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
**
** In addition, as a special exception, the copyright holders grant you
** certain additional rights. These rights are described in the Nokia Qt
** LGPL Exception version 1.0, included in the file LGPL_EXCEPTION.txt in
** this package.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 2.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL2 included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 2.0 requirements will be
** met: http://www.gnu.org/licenses/gpl-2.0.html
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL3 included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html
** $END_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.autotests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.trolltech.autotests.generated.GenFeatQMultiHash;
import com.trolltech.autotests.generated.GenFeatTemplate7;
import com.trolltech.autotests.generated.GenFeatTemplate8;
import com.trolltech.qt.QPair;

public class TestCppTemplate extends QApplicationTest {
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testGenFeatTemplate7() {
        GenFeatTemplate7 t7 = new GenFeatTemplate7();
        List list = t7.methodGet();
        assertNotNull(list);
        assertEquals(0, list.size());

        List newList1 = new ArrayList();
        newList1.add(Integer.valueOf(42));

        List newList2 = new ArrayList();
        newList2.add(newList1);

        List newList3 = new ArrayList();
        newList3.add(newList2);

        List newList4 = new ArrayList();
        newList4.add(newList3);

        List newList5 = new ArrayList();
        newList5.add(newList4);

        // QList<QList<QList<QList<QList<int> > > > >
        t7.methodSet(newList5);

        boolean bf = t7.doCppThing();
        assertTrue(bf);

        List oldList5 = t7.methodGet();
        assertNotNull(oldList5);
        assertEquals(1, oldList5.size());
        assertTrue(oldList5.get(0) instanceof List);

        List oldList4 = (List) oldList5.get(0);
        assertNotNull(oldList4);
        assertEquals(1, oldList4.size());
        assertTrue(oldList4.get(0) instanceof List);

        List oldList3 = (List) oldList4.get(0);
        assertNotNull(oldList3);
        assertEquals(1, oldList3.size());
        assertTrue(oldList3.get(0) instanceof List);

        List oldList2 = (List) oldList3.get(0);
        assertNotNull(oldList2);
        assertEquals(1, oldList2.size());
        assertTrue(oldList2.get(0) instanceof List);

        List oldList1 = (List) oldList2.get(0);
        assertNotNull(oldList1);
        assertEquals(1, oldList1.size());
        assertTrue(oldList1.get(0) instanceof Integer);
        assertEquals(Integer.valueOf(42), oldList1.get(0));
    }

    @Test
    public void testGenFeatTemplate8() {
        GenFeatTemplate8 t8 = new GenFeatTemplate8();
        @SuppressWarnings("rawtypes")
        QPair qp = t8.methodTest();
        assertNotNull(qp.first);
        assertTrue(qp.first instanceof Integer);
        assertEquals(Integer.valueOf(1), qp.first);

        assertNotNull(qp.second);
        assertTrue(qp.second instanceof QPair);
        @SuppressWarnings("rawtypes")
        QPair qp2 = (QPair) qp.second;

        assertNotNull(qp2.first);
        assertTrue(qp2.first instanceof Integer);
        assertEquals(Integer.valueOf(2), qp2.first);

        assertNotNull(qp2.second);
        assertTrue(qp2.second instanceof Integer);
        assertEquals(Integer.valueOf(3), qp2.second);
    }

    @Test
    public void testQMultiHash() {
        GenFeatQMultiHash multiHash = new GenFeatQMultiHash();
        assertEquals(0, multiHash.hashStringInt_count());
        assertEquals(0, multiHash.hashIntString_count());
        // Add some values
        multiHash.hashStringInt_insert("key1", 99);
        multiHash.hashStringInt_insert("key2", 32);
        multiHash.hashStringInt_insert("key3", 43);
        multiHash.hashStringInt_insert("key4", 83);
        multiHash.hashStringInt_insert("key4", 81);
        // get count
        assertEquals(5, multiHash.hashStringInt_count());
        // readback values
        assertEquals(99, multiHash.hashStringInt_getAtIndex("key1", 0));
        assertEquals(43, multiHash.hashStringInt_getAtIndex("key3", 0));
        assertEquals(-1, multiHash.hashStringInt_getAtIndex("key3", 1));	// does not exist
        // remove some values
        int i = multiHash.hashStringInt_remove("key3");
        assertEquals(1, i);
        i = multiHash.hashStringInt_remove("key3");	// try again
        assertEquals(0, i);	// should be 0 now
        // get count
        assertEquals(4, multiHash.hashStringInt_count());
        // readback removed item
        assertEquals(-1, multiHash.hashStringInt_getAtIndex("key3", 0));	// should now be removed

        // do full conversion of map
        i = 0;
        Set<String> seenSet = new HashSet<String>();
        Map<String,List<Integer>> mapStringInteger = multiHash.hashStringInt_instance();
        for(Entry<String, List<Integer>> e : mapStringInteger.entrySet()) {
            String k = e.getKey();
            List<Integer> v = e.getValue();

            assertFalse("seen key already " + k, seenSet.contains(k));
            seenSet.add(k);

            if("key1".equals(k)) {
                assertEquals(1, v.size());
                assertEquals(Integer.valueOf(99), v.get(0));
            } else if("key2".equals(k)) {
                assertEquals(1, v.size());
                assertEquals(Integer.valueOf(32), v.get(0));
            } else if("key4".equals(k)) {
                assertEquals(2, v.size());
                assertEquals(Integer.valueOf(81), v.get(0));
                assertEquals(Integer.valueOf(83), v.get(1));
            }

            i++;
        }
        assertEquals(3, i);
        //Map<Integer,String> mapIntegerString;
    }

    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main(TestCppTemplate.class.getName());
    }
}
