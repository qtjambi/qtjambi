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

#ifndef JAVAWRITEINCLUDES_H
#define JAVAWRITEINCLUDES_H

#include "treewalker.h"
#include <QMap>
#include <QString>

class QTextStream;
class Driver;
class Uic;

struct Option;

namespace Java {

struct WriteIncludes : public TreeWalker
{
    WriteIncludes(Uic *uic);

    void acceptUI(DomUI *node);

private:
    Uic *uic;
    Driver *driver;
    const Option &option;
};

} // namespace Java

#endif // JAVAWRITEINCLUDES_H
