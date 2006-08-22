/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ Trolltech AS. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef UTILS_H
#define UTILS_H

#include "ui4.h"
#include <QString>
#include <QList>
#include <QHash>

inline bool toBool(const QString &str)
{ return str.toLower() == QLatin1String("true"); }

inline QString toString(const DomString *str)
{ return str ? str->text() : QString(); }

inline QString fixString(const QString &str)
{
	QByteArray utf8 = str.toUtf8();
	uchar cbyte;
    QString result;

    for (int i = 0; i < utf8.length(); ++i) {
		cbyte = utf8.at(i);
		if (cbyte >= 0x80) {
			result += QLatin1String("\\") + QString::number(cbyte, 8);
		} else {
			switch(cbyte) {
			case '\\':
				result += QLatin1String("\\\\"); break;
			case '\"':
				result += QLatin1String("\\\""); break;
			case '\r':
				break;
			case '\n':
				result += QLatin1String("\\n\"\n\""); break;
			default:
				result += QChar(cbyte);
			}
		}
    }

	return QLatin1String("\"") + result + QLatin1String("\"");
}

inline QHash<QString, DomProperty *> propertyMap(const QList<DomProperty *> &properties)
{
    QHash<QString, DomProperty *> map;

    for (int i=0; i<properties.size(); ++i) {
        DomProperty *p = properties.at(i);
        map.insert(p->attributeName(), p);
    }

    return map;
}

inline QStringList unique(const QStringList &lst)
{
    QHash<QString, bool> h;
    for (int i=0; i<lst.size(); ++i)
        h.insert(lst.at(i), true);
    return h.keys();
}

#endif // UTILS_H
