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

#ifndef CLASSLISTGENERATOR_H
#define CLASSLISTGENERATOR_H

#include "generator.h"

#include <QtCore/QString>

class ClassListGenerator: public Generator
{
public:
    virtual void generate();

    QString fileName() const;
};

#endif // CLASSLISTGENERATOR_H
