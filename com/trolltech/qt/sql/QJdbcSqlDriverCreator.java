/****************************************************************************
 **
 ** (C) 2007-$THISYEAR$ $TROLLTECH$. All rights reserved.
 **
 ** This file is part of $PRODUCT$.
 **
 ** $JAVA_LICENSE$
 **
 ** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 ** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 **
 ****************************************************************************/

package com.trolltech.qt.sql;

class QJdbcSqlDriverCreator extends QSqlDriverCreatorBase
{
    QJdbcSqlDriverCreator()
    {
        disableGarbageCollection();
    }

    public QSqlDriver createObject()
    {
        return new QJdbcSqlDriver();
    }
}

