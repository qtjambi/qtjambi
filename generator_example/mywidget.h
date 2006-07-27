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

#ifndef MYWIDGET_H
#define MYWIDGET_H

#include <QtGui/QWidget>
#include <QtGui/QPaintEvent>
#include <QtGui/QMouseEvent>
#include <QtCore/QPoint>

class MyWidget: public QWidget
{
public:
    MyWidget(QWidget *parent);

    virtual QSize sizeHint() const { return QSize(320, 200); }

protected:
    virtual void paintEvent(QPaintEvent *e);
    virtual void mouseMoveEvent(QMouseEvent *e);

private:
    QPoint mouse_pos;
};

#endif
