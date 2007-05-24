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

#ifndef JAMBIRESOURCEBROWSER_H
#define JAMBIRESOURCEBROWSER_H

#include <QtCore/QString>
#include <QtDesigner/QDesignerResourceBrowserInterface>

class JambiResourceBrowser: public QDesignerResourceBrowserInterface
{
    Q_OBJECT
public:
    JambiResourceBrowser(QWidget *parent) : QDesignerResourceBrowserInterface(parent) { }

public slots:
    virtual void updateRootDirs(const QString &paths) = 0;
};

#endif
