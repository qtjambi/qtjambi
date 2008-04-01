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

import java.util.StringTokenizer;

import com.trolltech.qt.core.*;
import com.trolltech.qt.core.QIODevice.OpenModeFlag;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.xml.QDomDocument.Result;

import static org.junit.Assert.*;

import org.junit.*;

public class TestXml extends QApplicationTest {

    private void compare(String correct, String test) {
        StringTokenizer tokTest = new StringTokenizer(test, "\n");
        StringTokenizer tokCorrect = new StringTokenizer(correct, "\n");

        assertEquals(tokTest.countTokens(), tokCorrect.countTokens());

        while (tokTest.hasMoreTokens()) {
            assertEquals(tokTest.nextToken().trim(), tokCorrect.nextToken().trim());
        }
    }

    @Test
    public void makeDocument() {
        QDomDocument doc = new QDomDocument("MyML");
        QDomElement root = doc.createElement("MyML");
        doc.appendChild(root);

        QDomElement tag = doc.createElement("Greeting");
        root.appendChild(tag);

        QDomText t = doc.createTextNode("Hello World");
        tag.appendChild(t);

        QDomComment c = doc.createComment("comment");
        root.appendChild(c);

        compare("<!DOCTYPE MyML>\n" + "<MyML>\n" + "<Greeting>Hello World</Greeting>\n" + "<!--comment-->\n" + "</MyML>\n",

        doc.toString());
    }

    @Test
    public void readDocument() {
        QDomDocument doc = new QDomDocument("mydocument");
        QFile file = new QFile("classpath:generator/typesystem_core.txt");
        if (!file.open(OpenModeFlag.ReadOnly))
            return;
        Result res = doc.setContent(file);
        if (!res.success) {
            file.close();
            assertTrue("Failed to open file", false);
            return;
        }

        QDomNodeList list = doc.elementsByTagName("access");

        boolean found = false;

        for (int i = 0; i < list.size(); i++) {
            QDomElement element = list.at(i).toElement();

            found = found || "private".equals(element.attribute("modifier"));
        }

        assertTrue(found);

        file.close();

    }
}
