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

/*
handler.cpp

Provides a handler for processing XML elements found by the reader.

The handler stores the names of elements it finds and their indentation
levels. The indentation level is initially set to zero.
When a starting element is found, the indentation level is increased;
when an ending element is found, the indentation level is decreased.
*/

#include <QDebug>
#include "handler.h"

/*!
    Reset the state of the handler to ensure that new documents are
    read correctly.

    We return true to indicate that parsing should continue.
*/

bool Handler::startDocument()
{
    elementName.clear();
    elementIndentation.clear();
    indentationLevel = 0;

    return true;
}

/*!
    Process each starting element in the XML document.

    Append the element name to the list of elements found; add its
    corresponding indentation level to the list of indentation levels.

    Increase the level of indentation by one column.

    We return true to indicate that parsing should continue.
*/

bool Handler::startElement(const QString &, const QString &,
    const QString & qName, const QXmlAttributes &)
{
    elementName.append(qName);
    elementIndentation.append(indentationLevel);
    indentationLevel += 1;

    return true;
}

/*!
    Process each ending element in the XML document.

    Decrease the level of indentation by one column.

    We return true to indicate that parsing should continue.
*/

bool Handler::endElement(const QString &, const QString &,
    const QString &)
{
    indentationLevel -= 1;

    return true;
}

/*!
    Report a fatal parsing error, and return false to indicate to the reader
    that parsing should stop.
*/

bool Handler::fatalError (const QXmlParseException & exception)
{
    qWarning() << QString("Fatal error on line %1, column %2: %3").arg(
        exception.lineNumber()).arg(exception.columnNumber()).arg(
        exception.message());

    return false;
}

/*!
    Return the list of element names found.
*/

QStringList& Handler::names ()
{
    return elementName;
}

/*!
    Return the list of indentations used for each element found.
*/

QList<int>& Handler::indentations ()
{
    return elementIndentation;
}
