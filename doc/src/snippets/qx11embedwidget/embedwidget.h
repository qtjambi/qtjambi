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

#ifndef EMBEDWIDGET_H
#define EMBEDWIDGET_H

#include <QRadialGradient>
#include <QSize>
#include <QX11EmbedWidget>

class QPaintEvent;

class EmbedWidget : public QX11EmbedWidget
{
    Q_OBJECT

public:
    EmbedWidget(QWidget *parent = 0);
    QSize sizeHint() const;

protected:
    void paintEvent(QPaintEvent *event);

private:
    QRadialGradient gradient;
};

#endif
