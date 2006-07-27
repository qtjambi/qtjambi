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

#include "javawriteincludes.h"
#include "driver.h"
#include "ui4.h"
#include "uic.h"
#include "databaseinfo.h"

#include <QTextStream>

namespace Java {

WriteIncludes::WriteIncludes(Uic *uic)
    : driver(uic->driver()), option(uic->option())
{
    this->uic = uic;
}

void WriteIncludes::acceptUI(DomUI *)
{
}

} // namespace Java
