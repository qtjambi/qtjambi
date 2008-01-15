/****************************************************************************
**
** Copyright (C) 2007-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the Qt Jambi / JDBC project on Trolltech Labs.
**
** Licensees holding valid Trolltech Technology Preview licenses may
** use this file in accordance with the Trolltech Technology Preview
** License Agreement provided with the Software.
**
** See http://www.trolltech.com/pricing.html or email
** sales@trolltech.com for information about the Qt Commercial License
** Agreements.
** Contact info@trolltech.com if any conditions of this licensing are
** not clear to you.
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

