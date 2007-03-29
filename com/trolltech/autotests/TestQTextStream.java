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
import static org.junit.Assert.*;
import org.junit.*;

public class TestQTextStream {
    
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
