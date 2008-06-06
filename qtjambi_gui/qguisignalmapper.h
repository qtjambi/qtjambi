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

#ifndef QGUISIGNALMAPPER_H
#define QGUISIGNALMAPPER_H

#include <QtCore/QObject>
#include <QtCore/QSignalMapper>

class QGuiSignalMapper: public QSignalMapper
{
    Q_OBJECT
public:
    QGuiSignalMapper();
    QGuiSignalMapper(QObject *parent);

    void setMapping(QObject *sender, QWidget *widget);
    QObject *mapping(QWidget *widget) const;

private slots:
    void emitMapped(QWidget *);
    void emitMappedQWidget(QWidget *);

signals:
    void mappedQWidget(QWidget *widget);

private:
    void init();

    uint emittingMapped : 1;
    uint emittingMappedQWidget : 1;

};

#endif // QGUISIGNALMAPPER_H
