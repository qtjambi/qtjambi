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

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.QPolygonF;

import static org.junit.Assert.*;
import org.junit.*;

public class TestQTextStream {

    @Test public void testQPolygonF() {
        QPolygonF p = new QPolygonF();
        p.add(new QPointF(10, 11));
        p.add(new QPointF(12.2, 13.3));
        p.add(new QPointF(14, 15));

        QFile f = new QTemporaryFile();

        {
            f.open(QFile.OpenModeFlag.WriteOnly);
            QDataStream stream = new QDataStream(f);
            p.writeTo(stream);
            f.close();
        }

        {
            f.open(QFile.OpenModeFlag.ReadOnly);
            QDataStream stream = new QDataStream(f);
            QPolygonF p2 = new QPolygonF();
            p2.readFrom(stream);

            assertEquals(10.0, p2.at(0).x());
            assertEquals(11.0, p2.at(0).y());
            assertEquals(12.2, p2.at(1).x());
            assertEquals(13.3, p2.at(1).y());
            assertEquals(14.0, p2.at(2).x());
            assertEquals(15.0, p2.at(2).y());
        }
    }

    @Test public void testStringStream()
    {
        {
            QTextStream stream = QTextStream.createStringStream("TestString\n55", new QIODevice.OpenMode(QIODevice.OpenModeFlag.ReadWrite));
            stream.writeString(" ");
            stream.writeString("Hei");
            stream.seek(0);

            Assert.assertEquals(stream.readString(), "TestString");
            Assert.assertEquals(stream.readInt(), 55);
            Assert.assertEquals(stream.readString(), "Hei");
            Assert.assertEquals(stream.string(), "TestString\n55 Hei");
        }

        String daString = "";
        {
            QTextStream stream = QTextStream.createStringStream(null, new QIODevice.OpenMode(QIODevice.OpenModeFlag.WriteOnly));

            stream.writeBoolean(true);
            stream.writeString(" ");
            stream.writeByte((byte) 'u');
            stream.writeString(" ");
            stream.writeShort((short) 24);
            stream.writeString(" ");
            stream.writeInt(25);
            stream.writeString(" ");
            stream.writeLong(26);
            stream.writeString(" ");
            stream.writeFloat(24.5f);
            stream.writeString(" ");
            stream.writeDouble(26.4);
            daString = stream.string();
        }
 
        {
            QTextStream stream = QTextStream.createStringStream(daString, new QIODevice.OpenMode(QIODevice.OpenModeFlag.ReadOnly));

            assertTrue(stream.readInt() == 1);
            stream.skipWhiteSpace();

            assertEquals((byte) 'u', stream.readByte());
            stream.skipWhiteSpace();

            assertEquals((short) 24, stream.readShort());
            stream.skipWhiteSpace();

            assertEquals(25, stream.readInt());
            stream.skipWhiteSpace();

            assertEquals(26l, stream.readLong());
            stream.skipWhiteSpace();

            assertTrue((float) 24.5 == stream.readFloat());
            stream.skipWhiteSpace();

            assertTrue(26.4 == stream.readDouble());
        }
    }

    @Test public void testString() {
        QFile f = new QTemporaryFile();
        {
            f.open(QFile.OpenModeFlag.WriteOnly);
            QTextStream stream = new QTextStream(f);
            stream.writeString("Hello World\nHow is life today?");
            stream.flush();
            f.close();
        }

        {
            f.open(QFile.OpenModeFlag.ReadOnly);
            QTextStream stream = new QTextStream(f);

            assertEquals("Hello", stream.readString());
            assertEquals("World", stream.readString());
            assertEquals("How", stream.readString());
            assertEquals("is", stream.readString());
            assertEquals("life", stream.readString());
            assertEquals("today?", stream.readString());
            assertTrue(stream.atEnd());
            f.close();
        }
        f.dispose();
    }

    @Test public void testNumbers() {
        QFile f = new QTemporaryFile();

        // Write out...
        {
            f.open(QFile.OpenModeFlag.WriteOnly);
            QTextStream stream = new QTextStream(f);

            stream.writeBoolean(true);
            stream.writeString(" ");
            stream.writeByte((byte) 'u');
            stream.writeString(" ");
            stream.writeShort((short) 24);
            stream.writeString(" ");
            stream.writeInt(25);
            stream.writeString(" ");
            stream.writeLong(26);
            stream.writeString(" ");
            stream.writeFloat(24.5f);
            stream.writeString(" ");
            stream.writeDouble(26.4);
            f.close();
        }

        {
            f.open(QFile.OpenModeFlag.ReadOnly);
            QTextStream stream = new QTextStream(f);

            assertTrue(stream.readInt() == 1);
            stream.skipWhiteSpace();

            assertEquals((byte) 'u', stream.readByte());
            stream.skipWhiteSpace();

            assertEquals((short) 24, stream.readShort());
            stream.skipWhiteSpace();

            assertEquals(25, stream.readInt());
            stream.skipWhiteSpace();

            assertEquals(26l, stream.readLong());
            stream.skipWhiteSpace();

            assertEquals((float) 24.5, stream.readFloat());
            stream.skipWhiteSpace();

            assertEquals(26.4, stream.readDouble());
            stream.skipWhiteSpace();
        }
    }
}

