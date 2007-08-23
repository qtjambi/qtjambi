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

#include "javautils.h"
#include <QtCore/QSet>

QString javaFixString(const QString &str)
{
    const ushort *chars = str.utf16();
    QString result;

    for (int i = 0; i < str.length(); ++i) {
        ushort c = chars[i];
        if (c >= 0x0080) {
            QString num = QString::number(c, 16);
            int padding = 4 - num.length();
            if (padding <= 0) {
                qWarning("juic: bad unicode character %x became %s\n", c, qPrintable(num));
                return "";
            }

            result += QLatin1String("\\u") + QString(padding, '0') + QString::number(c, 16);
        } else {
            switch(c) {
            case '\\':
                result += QLatin1String("\\\\"); break;
            case '\"':
                result += QLatin1String("\\\""); break;
            case '\r':
                break;
            case '\n':
                result += QLatin1String("\\n\"+\n\""); break;
            default:
                result += QChar(c);
            }
        }
    }

    return QLatin1String("\"") + result + QLatin1String("\"");
}

QSet<QString> escaped_names;

QString escapeVariableName(const QString &name)
{
    if (name == QLatin1String("native")) {
        if (!escaped_names.contains(name)) {
            fprintf(stderr, "juic: Variable 'native' renamed to 'native__'\n");
            escaped_names << name;
        }
        return name + QLatin1String("__");
    }
    return name;
}
