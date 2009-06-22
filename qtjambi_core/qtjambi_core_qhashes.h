#ifndef QTJAMBI_CORE_QHASHES_H
#define QTJAMBI_CORE_QHASHES_H

#include <QtCore/QRect>
#include <QtCore/QDate>
#include <QtCore/QTime>
#include <QtCore/QDateTime>
#include <QtCore/QRectF>
#include <QtCore/QDir>
#include <QtCore/QSize>
#include <QtCore/QSizeF>
#include <QtCore/QLine>
#include <QtCore/QLineF>

inline int qHash(const QSizeF &size)
{
    int hashCode = qHash(size.width());
    hashCode = hashCode * 31 + qHash(size.height());
    return hashCode;
}

inline int qHash(const QSize &size)
{
    int hashCode = qHash(size.width());
    hashCode = hashCode * 31 + qHash(size.height());
    return hashCode;
}

inline int qHash(const QPointF &point)
{
    int hashCode = qHash(point.x());
    hashCode = hashCode * 31 + qHash(point.y());
    return hashCode;
}

inline int qHash(const QFileInfo &fileInfo)
{
    return qHash(fileInfo.absoluteFilePath());
}

inline int qHash(const QDir &dir)
{
    return qHash(dir.absolutePath());
}

inline int qHash(const QRect &rect)
{
    int hashCode = rect.left();
    hashCode = hashCode * 31 + rect.top();
    hashCode = hashCode * 31 + rect.right();
    hashCode = hashCode * 31 + rect.bottom();
    return hashCode;
}

inline int qHash(const QRectF &rect)
{
    int hashCode = qHash(rect.left());
    hashCode = hashCode * 31 + qHash(rect.top());
    hashCode = hashCode * 31 + qHash(rect.right());
    hashCode = hashCode * 31 + qHash(rect.bottom());
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

inline int qHash(const QLineF &line)
{
    int hashCode = qHash(line.p1());
    hashCode = hashCode * 31 + qHash(line.p2());
}

inline int qHash(const QLine &line)
{
    int hashCode = qHash(line.p1());
    hashCode = hashCode * 31 + qHash(line.p2());
}

#endif // QTJAMBI_CORE_QHASHES_H 
