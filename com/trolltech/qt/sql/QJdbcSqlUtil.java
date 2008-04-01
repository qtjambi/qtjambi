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

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.*;

import com.trolltech.qt.core.QDate;
import com.trolltech.qt.core.QDateTime;
import com.trolltech.qt.core.QTime;
import com.trolltech.qt.core.QUrl;

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

    static Object javaToQt(Object input) {
        if (input instanceof java.sql.Date) {
            java.sql.Date date = (java.sql.Date) input;
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(date);

            return new QDate(c.get(Calendar.YEAR),
                             c.get(Calendar.MONTH)+1,
                             c.get(Calendar.DAY_OF_MONTH));

        } else if (input instanceof java.sql.Time) {

            java.sql.Time time = (java.sql.Time) input;
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(time);

            return new QTime(c.get(Calendar.HOUR),
                             c.get(Calendar.MINUTE)+1,
                             c.get(Calendar.SECOND));

        } else if (input instanceof java.util.Date) {
            java.util.Date date = (java.util.Date) input;
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(date);

            return new QDateTime(new QDate(c.get(Calendar.YEAR),
                                           c.get(Calendar.MONTH)+1,
                                           c.get(Calendar.DAY_OF_MONTH)),
                                 new QTime(c.get(Calendar.HOUR_OF_DAY),
                                           c.get(Calendar.MINUTE),
                                           c.get(Calendar.SECOND),
                                           c.get(Calendar.MILLISECOND)));

        } else if (input instanceof URL) {

            URL url = (URL) input;
            return new QUrl(url.toString());

        } else {
            return input;
        }

    }

    @SuppressWarnings("deprecation")
    static Object qtToJava(Object input) {
        if (input instanceof QDateTime) {
            QDateTime dt = (QDateTime) input;

            QDate date = dt.date();
            QTime time = dt.time();

            return new java.sql.Timestamp(date.year() - 1900,
                                          date.month() - 1,
                                          date.day(),
                                          time.hour(),
                                          time.minute(),
                                          time.second(),
                                          time.msec());
        } else if (input instanceof QDate) {

            QDate date = (QDate) input;
            return new java.sql.Date(date.year() - 1900,
                                     date.month() - 1,
                                     date.day());

        } else if (input instanceof QTime) {

            QTime time = (QTime) input;
            return new java.sql.Time(time.hour(), time.minute(), time.second());

        } else if (input instanceof QUrl) {

            QUrl url = (QUrl) input;
            try {
                return new URL(url.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return input;
            }

        } else {
            return input;
        }
    }


}
