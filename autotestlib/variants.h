/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $CPP_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef VARIANTS_H
#define VARIANTS_H

#include <QtCore/QLine>
#include <QtCore/QLineF>
#include <QtCore/QVariant>
#include <QtGui/QPixmap>
#include <QtGui/QImage>

class Variants
{
public:
    QVariant pushThrough(const QVariant &variant) {
        current = variant;
        return current;
    }

    qint64 currentQInt64() const { return *(qint64 *) current.constData(); }
    quint64 currentQUInt64() const { return *(quint64 *) current.constData(); }

    qint32 currentQInt32() const { return *(qint32 *) current.constData(); }
    quint32 currentQUInt32() const { return *(quint32 *) current.constData(); }

    qint16 currentQInt16() const { return *(qint16 *) current.constData(); }
    quint16 currentQUInt16() const { return *(quint16 *) current.constData(); }

    qint8 currentQInt8() const { return *(char *) current.constData(); }
    quint8 currentQUInt8() const { return *(char *) current.constData(); }

    float currentFloat() const { return *(float *) current.constData(); }
    double currentDouble() const { return *(double *) current.constData(); }

    bool currentBool() const { return *(bool *) current.constData(); }

    QString currentString() const { return *(QString *) current.constData(); }

    QVariantList currentQVariantList() const { return *(QVariantList *) current.constData(); }
    QVariantMap currentQVariantMap() const { return *(QVariantMap *) current.constData(); }

    QPixmap currentQPixmap() const { return *(QPixmap *) current.constData(); }
    QImage currentQImage() const { return *(QImage *) current.constData(); }

    QSize currentQSize() const { return current.toSize(); }
    QSizeF currentQSizeF() const { return current.toSizeF(); }

    QPoint currentQPoint() const { return current.toPoint(); }
    QPointF currentQPointF() const { return current.toPointF(); }

    QLine currentQLine() const { return current.toLine(); }
    QLineF currentQLineF() const { return current.toLineF(); }

    QRect currentQRect() const { return current.toRect(); }
    QRectF currentQRectF() const { return current.toRectF(); }

    int currentType() const { return current.type(); }

    bool isValid() const { return current.isValid(); }

    QString currentToString() { return current.toString(); }

    int currentToInt() { return current.toInt(); }

private:
    QVariant current;
};

#endif // VARIANTS_H
