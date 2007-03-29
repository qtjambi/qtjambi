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

package com.trolltech.tests;

import com.trolltech.qt.core.*;
import static org.junit.Assert.*;

public class Test
{
    @org.junit.Test
    public void testQDataStreamReadWriteBytes() {
        QByteArray ba = new QByteArray();

        {
            QDataStream stream = new QDataStream(ba, QIODevice.OpenModeFlag.WriteOnly);
            byte bytes[] = "abra ka dabra".getBytes();
            stream.writeInt(bytes.length);
            stream.writeBytes(bytes);
        }

        {
            QDataStream stream = new QDataStream(ba);
            byte bytes[] = new byte[stream.readInt()];
            stream.readBytes(bytes);
            String s = new String(bytes);
            assertEquals("abra ka dabra".length(), s.length());
            assertEquals("abra ka dabra", s);
        }

        System.gc();
    }

}
