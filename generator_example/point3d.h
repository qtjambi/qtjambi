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

#ifndef POINT3D_H
#define POINT3D_H

#include <math.h>

class Point3D
{
public:
    inline Point3D() : m_x(0.0), m_y(0.0), m_z(1.0)
    {
    }

    inline Point3D(qreal _x, qreal _y, qreal _z) : m_x(_x), m_y(_y), m_z(_z)
    {
    }

    inline Point3D operator-(const Point3D &other)
    {
        return Point3D(x() - other.x(), y() - other.y(), z() - other.z());
    }

    inline qreal length()
    {
        return sqrt(x() * x() + y() * y() + z() * z());
    }

    qreal &rx() { return m_x; }
    qreal &ry() { return m_y; }
    qreal &rz() { return m_z; }

    void setX(qreal x) { m_x = x; }
    void setY(qreal y) { m_y = y; }
    void setZ(qreal z) { m_z = z; }

    qreal x() const { return m_x; }
    qreal y() const { return m_y; }
    qreal z() const { return m_z; }

private:
    qreal m_x;
    qreal m_y;
    qreal m_z;
};


#endif
