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
#include <QtCore/QHash>

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


QString escapeVariableName(const QString &name)
{
    static QHash<QString, bool> escaped_names;
    if (escaped_names.isEmpty()) {
        escaped_names[QLatin1String("native")] = false;
        escaped_names[QLatin1String("boolean")] = false;
        escaped_names[QLatin1String("abstract")] = false;
        escaped_names[QLatin1String("final")] = false;
    }

    if (escaped_names.contains(name)) {
        bool &reported = escaped_names[name];

        if (!reported) {
            fprintf(stderr, "juic: Variable '%s' renamed to '%s__'\n", qPrintable(name), qPrintable(name));
            reported = true;
        }

        return name + QLatin1String("__");
    }
    return name;
}
