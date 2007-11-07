/****************************************************************************
**
** Copyright (C) 2006-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#include <QtGui>

#include "embedwidget.h"

EmbedWidget::EmbedWidget(QWidget *parent)
    : QX11EmbedWidget(parent)
{
    gradient = QRadialGradient(100, 100, 90, 60, 60);
    gradient.setColorAt(0.0, Qt::white);
    gradient.setColorAt(0.9, QColor(192, 192, 255));
    gradient.setColorAt(1.0, QColor(0, 32, 64));
}

QSize EmbedWidget::sizeHint() const
{
    return QSize(200, 200);
}

void EmbedWidget::paintEvent(QPaintEvent *event)
{
    QPainter painter;
    painter.begin(this);
    painter.setRenderHint(QPainter::Antialiasing);
    painter.fillRect(event->rect(), QBrush(gradient));
    painter.end();
}
