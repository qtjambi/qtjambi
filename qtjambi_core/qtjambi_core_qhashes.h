#ifndef QTJAMBI_CORE_QHASHES_H
#define QTJAMBI_CORE_QHASHES_H

#include <QtCore/QRect>
#include <QtCore/QDate>
#include <QtCore/QTime>
#include <QtCore/QDateTime>

inline int qHash(const QRect &rect)
{
    int hashCode = rect.left();
    hashCode = hashCode * 31 + rect.top();
    hashCode = hashCode * 31 + rect.right();
    hashCode = hashCode * 31 + rect.bottom();
    return hashCode;
}

inline int qHash(const QPoint &point)
{
    int hashCode = point.x();
    hashCode = hashCode * 31 + point.y();
    return hashCode;
}

inline int qHash(const QDate &date)
{
    return date.toJulianDay();
}

inline int qHash(const QTime &time)
{
    int hashCode = time.hour();
    hashCode = hashCode * 31 + time.minute();
    hashCode = hashCode * 31 + time.second();
    hashCode = hashCode * 31 + time.msec();
    return hashCode;
}

inline int qHash(const QDateTime &dateTime)
{
    int hashCode = qHash(dateTime.date());
    hashCode = hashCode * 31 + qHash(dateTime.time());
    return hashCode;
}

#endif // QTJAMBI_CORE_QHASHES_H 
