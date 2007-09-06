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

#ifndef PRIGENERATOR_H
#define PRIGENERATOR_H

#include "generator.h"

#include <QStringList>
#include <QHash>

struct Pri
{
    QStringList headers;
    QStringList sources;
};

class PriGenerator : public Generator
{
    Q_OBJECT

 public:
    virtual void generate();

    void addHeader(const QString &folder, const QString &header);
    void addSource(const QString &folder, const QString &source);

 private:
    QHash<QString, Pri> priHash;

};
#endif // PRIGENERATOR_H

