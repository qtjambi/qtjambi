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

#ifndef JAMBIPROPERTYSHEET_H
#define JAMBIPROPERTYSHEET_H

#include <QtCore/QVariant>
#include <QtDesigner/QtDesigner>

class JambiLanguagePlugin;

class JambiPropertySheet: public QObject, public QDesignerPropertySheetExtension
{
    Q_OBJECT
    Q_INTERFACES(QDesignerPropertySheetExtension)
public:
    JambiPropertySheet(QObject *parent);

    // Reimplement property/setProperty to do enum/flags magic and force
    // user to reimplement read/write instead...
    virtual QVariant property(int index) const;
    virtual void setProperty(int index, const QVariant &value);

    virtual QVariant readProperty(int index) const = 0;
    virtual void writeProperty(int index, const QVariant &value) = 0;

};

#endif
