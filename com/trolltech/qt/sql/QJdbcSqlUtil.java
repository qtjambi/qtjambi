/****************************************************************************
 **
 ** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
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

import java.sql.*;
import java.util.*;

class QJdbcSqlUtil
{
    private static HashMap<String, Integer> javaToVariant = new HashMap<String, Integer>();

    static {
                                                        // QVariant::
        javaToVariant.put(null, 0);                     // Invalid
        javaToVariant.put("java.lang.String", 10);      // String
        javaToVariant.put("java.lang.Integer", 2);      // Int
        javaToVariant.put("java.lang.Short", 2);        // Int
        javaToVariant.put("java.lang.Byte", 2);         // Int
        javaToVariant.put("java.lang.Boolean", 1);      // Bool
        javaToVariant.put("java.lang.Byte[]", 12);      // ByteArray
        javaToVariant.put("java.util.Date", 14);        // Date
        javaToVariant.put("java.sql.Date", 14);         // Date
        javaToVariant.put("java.lang.Float", 6);        // Double
        javaToVariant.put("java.lang.Double", 6);       // Double
        javaToVariant.put("java.lang.Long", 4);         // LongLong
        javaToVariant.put("java.sql.Time", 15);         // Time
        javaToVariant.put("java.sql.TimeStamp", 16);    // DateTime
        javaToVariant.put("java.net.Url", 17);          // Url
    }

    static int javaTypeToVariantType(String javaType)
    {
        Integer variantType = javaToVariant.get(javaType);
        if (variantType == null)
            return 127;
        return variantType;
    }

    static int javaTypeIdToVariantType(int variantType)
    {
        // can't use QVariant types directly, since we don't want a dependency to QtGui
        switch (variantType) {
        case Types.VARCHAR:
        case Types.CHAR:
            return 10; // String
        case Types.INTEGER:
        case Types.SMALLINT:
        case Types.TINYINT:
            return 2; // Int
        case Types.BOOLEAN:
        case Types.BIT:
            return 1; // Bool
        case Types.BIGINT:
        case Types.DECIMAL:
            return 4; // LongLong
        case Types.DATE:
            return 14; // Date
        case Types.DOUBLE:
        case Types.FLOAT:
        case Types.NUMERIC:
        case Types.REAL:
            return 6; // Double
        case Types.TIME:
            return 15; // Time
        case Types.TIMESTAMP:
            return 16; // DateTime
        }

        // System.out.println("Unknown type id: " + variantType);

        return 127; // QVariant.UserType
    }

    static QSqlField.RequiredStatus toRequiredStatus(int isNullable)
    {
        switch (isNullable) {
        case ResultSetMetaData.columnNoNulls:
            return QSqlField.RequiredStatus.Required;
        case ResultSetMetaData.columnNullable:
            return QSqlField.RequiredStatus.Optional;
        case ResultSetMetaData.columnNullableUnknown:
        default:
            return QSqlField.RequiredStatus.Unknown;
        }
    }

    static QSqlError getError(SQLException ex, String driverText, QSqlError.ErrorType t)
    {
        StringBuilder dbText = new StringBuilder();
        SQLException e = ex;
        int errorCode = 0;

        while (e != null) {
            if (dbText.length() != 0)
                dbText.append(", ");
            dbText.append(e.getMessage()).append(" (").append(e.getSQLState()).append(")");
            errorCode = e.getErrorCode(); // last one wins
            e = e.getNextException();
        }

        return new QSqlError(driverText, dbText.toString(), t, errorCode);
    }

}
