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


#ifndef ASTTOXML
#define ASTTOXML

#include "codemodel.h"

#include <QString>
#include <QXmlStreamWriter>

void astToXML(const QString name);
void writeOutNamespace(QXmlStreamWriter &s, NamespaceModelItem &item);
void writeOutEnum(QXmlStreamWriter &s, EnumModelItem &item);
void writeOutFunction(QXmlStreamWriter &s, FunctionModelItem &item);
void writeOutClass(QXmlStreamWriter &s, ClassModelItem &item);


#endif // ASTTOXML
