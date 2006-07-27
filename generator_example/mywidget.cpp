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

#include "mywidget.h"

#include <QtGui/QPainter>

MyWidget::MyWidget(QWidget *parent) : QWidget(parent)
{
    setMouseTracking(true);
}

void MyWidget::mouseMoveEvent(QMouseEvent *e)
{
    mouse_pos = QPoint(e->x(), e->y());
    update();
}

void MyWidget::paintEvent(QPaintEvent *)
{
    QPainter p(this);

    QPointF p1(width() / 2, height() / 2);
    QPointF p2(mouse_pos);

    QLinearGradient g(p1, p2);

    g.setColorAt(0.0f, Qt::red);
    g.setColorAt(1.0f, Qt::yellow);
    p.fillRect(rect(), g);
}
