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

import java.sql.*;
import java.util.*;

class QJdbcSqlDriver extends QSqlDriver
{
    private static QJdbcSqlDriverCreator creator;
    public static void initialize() {
        if (creator == null) {
            creator = new QJdbcSqlDriverCreator();
            QSqlDatabase.registerSqlDriver(QJdbc.ID, creator);
        }
    }

    QJdbcSqlDriver()
    {
        super();

        // we're usually kept by QSqlDatabase on the C++ side,
        // so prevent the garbage collector from nuking us
        // will be disabled in the disposed() slot
        this.disableGarbageCollection();
    }

    public void close()
    {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                setError(e, tr("Unable to close connection"), QSqlError.ErrorType.ConnectionError);
            }
        }
        connection = null;
        setOpen(false);
    }

    public Object handle()
    {
        return connection;
    }

    public QSqlResult createResult()
    {
        return new QJdbcSqlResult(this, connection);
    }

    public List<String> tables(QSql.TableType tableType)
    {
        Vector<String> list = new Vector<String>();
        if (connection == null)
            return list;

        try {
            DatabaseMetaData metaData = connection.getMetaData();

            String[] types = new String[1];
            switch (tableType) {
            case Tables:
                types[0] = "TABLE";
                break;
            case Views:
                types[0] = "VIEWS";
                break;
            case SystemTables:
                types[0] = "SYSTEM TABLE";
                break;
            case AllTables:
            default:
                types = null;
                break;
            }

            ResultSet rs = metaData.getTables(null, null, null, types);

            while (rs.next()) {
                list.add(rs.getString("TABLE_NAME"));
            }

            rs.close();

        } catch (SQLException ex) {
            setError(ex, tr("Unable to list tables"), QSqlError.ErrorType.ConnectionError);
            return new Vector<String>();
        }

        return list;
    }

    public boolean hasFeature(QSqlDriver.DriverFeature f)
    {
        switch (f) {
        case PreparedQueries:
        case PositionalPlaceholders:
        case BLOB:
        case Unicode:
        case Transactions:
            return true;
            
        case LastInsertId:
        	try {
        		return connection != null && connection.getMetaData().supportsGetGeneratedKeys();
        	} catch (SQLException e) {
        		setError(e, "Can't determine availability of LastInsertId", QSqlError.ErrorType.UnknownError);
        		return false;
        	}

        case QuerySize: // we can only retrieve the fetch count - not the actual result set size
        case NamedPlaceholders: // JDBC supports only positional placeholders
        case BatchOperations: // JDBC can batch statements, but not a bunch of values
        case SimpleLocking: // don't know how to find that out - but shouldn't matter for JDBC
        case LowPrecisionNumbers: // Java has BigInts - no more double vs. string hassle
            return false;
        }

        return false;
    }

    public boolean open(String db, String user, String password, String host, int port, String connOpts)
    {
        Connection con = null;
        try {
            con = DriverManager.getConnection(db, user, password);
        } catch (SQLException ex) {
            setError(ex, tr("Unable to find JDBC driver."), QSqlError.ErrorType.ConnectionError);
            setOpenError(true);
            return false;
        }
        if (con == null) {
            setOpenError(true);
            return false;
        }

        try {
            // our default behavior is to commit every statement
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            setError(ex, tr("Unable to enable auto-commit"), QSqlError.ErrorType.ConnectionError);
            // this is annoying, but not fatal.
        }

        this.connection = con;

        setOpen(true);
        setOpenError(false);
        return true;
    }

    public QSqlRecord record(String tableName)
    {
        if (connection == null)
            return new QSqlRecord();

        QSqlRecord res = new QSqlRecord();

        try {

            DatabaseMetaData metaData = connection.getMetaData();
            if (metaData == null)
                return new QSqlRecord();

            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);

            while (resultSet.next()) {
                QSqlField f = new QSqlField(resultSet.getString(4),
                    QJdbcSqlUtil.javaTypeIdToVariantType(resultSet.getInt(5)));
                f.setLength(resultSet.getInt(7));
                f.setPrecision(resultSet.getInt(8));
                f.setRequiredStatus(QJdbcSqlUtil.toRequiredStatus(resultSet.getInt(11)));
                f.setSqlType(resultSet.getInt(5));
                f.setDefaultValue(resultSet.getObject(13));

                res.append(f);
            }

            resultSet.close();

        } catch (SQLException ex) {
            setError(ex, tr("Unable to retrieve database meta data"), QSqlError.ErrorType.ConnectionError);
            return new QSqlRecord();
        }

        return res;
    }

    public boolean beginTransaction()
    {
        if (connection == null)
            return false;

        try {
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            setError(ex, "Unable to disable autoCommit", QSqlError.ErrorType.ConnectionError);
            return false;
        }
        return true;
    }

    public boolean commitTransaction()
    {
        if (connection == null)
            return false;

        try {
            connection.commit();
        } catch (SQLException ex) {
            setError(ex, tr("Unable to commit transaction"), QSqlError.ErrorType.ConnectionError);
            return false;
        }
        return true;
    }

    public boolean rollbackTransaction()
    {
        if (connection == null)
            return false;

        try {
            connection.rollback();
        } catch (SQLException ex) {
            setError(ex, tr("Unable to roll back transaction"), QSqlError.ErrorType.ConnectionError);
            return false;
        }
        return true;
    }

    protected void disposed()
    {
        // the C++ object is gone - probably because QSqlDatabase::removeDatabase was called.
        // say bye bye, then
        this.reenableGarbageCollection();

        super.disposed();
    }

    private void setError(SQLException e, String driverText, QSqlError.ErrorType t)
    {
        setLastError(QJdbcSqlUtil.getError(e, driverText, t));
    }

    private Connection connection = null;
}

