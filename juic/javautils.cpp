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

QString javaFixString(const QString &str)
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
                result += QLatin1String("\\n\"+\n\""); break;
            default:
                result += QChar(cbyte);
            }
        }
    }

    return QLatin1String("\"") + result + QLatin1String("\"");
}
