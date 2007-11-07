/****************************************************************************
**
** Copyright (C) 2004-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of an example program for Qt.
** EDITIONS: NOLIMITS
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef HANDLER_H
#define HANDLER_H

#include <qstring.h>
#include <qxml.h>

class Handler : public QXmlDefaultHandler
{
public:
    bool startDocument();
    bool startElement(const QString &, const QString &, const QString &qName,
                       const QXmlAttributes &);
    bool endElement(const QString &, const QString &, const QString &);

    bool fatalError(const QXmlParseException &exception);

    QStringList& names();
    QList<int>& indentations();

private:
    int indentationLevel;
    QStringList elementName;
    QList<int> elementIndentation;
};

#endif

