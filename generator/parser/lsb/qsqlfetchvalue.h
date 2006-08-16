#ifndef QSQLFETCHVALUE_H
#define QSQLFETCHVALUE_H
#include <QtSql/qsqlquery.h>

template <typename T>
T qSqlFetchValue(const QString &query, const T &defaultVal = T(),
                 const QSqlDatabase &db = QSqlDatabase())
{
    QSqlQuery q(query, db);
    if (q.record().count() == 1 && q.next()) {
        T t = qvariant_cast<T>(q.value(0));
        return q.next() ? defaultVal : t;
    }
    return defaultVal;
}

#endif
