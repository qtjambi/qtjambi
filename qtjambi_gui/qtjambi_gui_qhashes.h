#ifndef QTJAMBI_GUI_QHASHES_H
#define QTJAMBI_GUI_QHASHES_H

#include <QtCore/QHash>
#include <QtCore/QVector>

#include <QtGui/QColor>
#include <QtGui/QBrush>
#include <QtGui/QRegion>
#include <QtGui/QPolygon>
#include <QtGui/QPalette>
#include <QtGui/QFont>
#include <QtGui/QMatrix>
#include <QtGui/QImage>
#include <QtGui/QPen>
#include <QtGui/QTransform>
#include <QtGui/QPolygonF>

#include <qtjambi_core_qhashes.h>

inline int qHash(const QColor &color)
{
    return int(color.rgba());
}

inline int qHash(const QBrush &brush)
{
    int hashCode = int(brush.style());
    hashCode = hashCode * 31 + qHash(brush.color());
    return hashCode;
}

inline int qHash(const QRegion &region)
{
    int hashCode = 1;
    QVector<QRect> rects = region.rects();
    for (int i=0; i<rects.size(); ++i)
        hashCode = hashCode * 31 + qHash(rects.at(i));
    return hashCode;
}

inline int qHash(const QPolygon &polygon)
{
    int hashCode = 1;
    for (int i=0; i<polygon.size(); ++i)
        hashCode = hashCode * 31 + qHash(polygon.at(i));
    return hashCode;
}

inline int qHash(const QPalette &palette)
{
    int hashCode = 1;
    for (int role=0;role<int(QPalette::NColorRoles);++role) {
        for (int group=0;group<int(QPalette::NColorGroups);++group) {
            hashCode = hashCode * 31 + qHash(palette.color(QPalette::ColorGroup(group), QPalette::ColorRole(role)));
        }
    }
    return hashCode;
}

inline int qHash(const QFont &font)
{
    int hashCode = font.pixelSize();
    hashCode = hashCode * 31 + font.weight();
    hashCode = hashCode * 31 + int(font.style());
    hashCode = hashCode * 31 + font.stretch();
    hashCode = hashCode * 31 + int(font.styleHint());
    hashCode = hashCode * 31 + int(font.styleStrategy());
    hashCode = hashCode * 31 + int(font.fixedPitch());
    hashCode = hashCode * 31 + qHash(font.family());
    hashCode = hashCode * 31 + qHash(font.pointSize());
    hashCode = hashCode * 31 + int(font.underline());
    hashCode = hashCode * 31 + int(font.overline());
    hashCode = hashCode * 31 + int(font.strikeOut());
    hashCode = hashCode * 31 + int(font.kerning());
    return hashCode;
}

inline int qHash(const QMatrix &matrix)
{
    int hashCode = matrix.m11();
    hashCode = hashCode * 31 + matrix.m12();
    hashCode = hashCode * 31 + matrix.m21();
    hashCode = hashCode * 31 + matrix.m22();
    hashCode = hashCode * 31 + matrix.dx();
    hashCode = hashCode * 31 + matrix.dy();
    return hashCode;
}

inline int qHash(const QImage &image)
{
    return qHash(image.cacheKey());
}

inline int qHash(const QPen &pen)
{
    int hashCode = int(pen.style()); 
    hashCode = hashCode * 31 + int(pen.capStyle());
    hashCode = hashCode * 31 + int(pen.joinStyle());
    hashCode = hashCode * 31 + pen.width();
    hashCode = hashCode * 31 + qHash(pen.brush());
    hashCode = hashCode * 31 + int(pen.isCosmetic());
    return hashCode;
}

inline int qHash(const QTransform &transform)
{
    int hashCode = transform.m11();
    hashCode = hashCode * 31 + transform.m12();
    hashCode = hashCode * 31 + transform.m13();

    hashCode = hashCode * 31 + transform.m21();
    hashCode = hashCode * 31 + transform.m22();
    hashCode = hashCode * 31 + transform.m23();

    hashCode = hashCode * 31 + transform.m31();
    hashCode = hashCode * 31 + transform.m32();
    hashCode = hashCode * 31 + transform.m33();

    return hashCode;
}

inline int qHash(const QPolygonF &polygon)
{
    int hashCode = 1;
    for (int i=0; i<polygon.size(); ++i)
        hashCode = hashCode * 31 + qHash(polygon.at(i));
    return hashCode;
}

#endif // QTJAMBI_GUI_QHASHES_H