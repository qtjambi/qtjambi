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

package generator;

import com.trolltech.qt.*;
import com.trolltech.qt.sql.*;

class QSqlDatabase___ extends QSqlDatabase {

    public static String defaultConnection() {
        com.trolltech.qt.QNativePointer np = defaultConnection_private();

        String returned = "";
        int i = 0;
        while (np.byteAt(i) != 0)
            returned += (char) np.byteAt(i++);
        return returned;
    }

}// class
