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

#ifndef NATIVEPOINTERTESTER_H
#define NATIVEPOINTERTESTER_H

class NativePointerTester
{
public:
    int testInt(int &i, int to) {
        int tmp = i;
        i = to;
        return tmp;
    }

    QString testString(QString &s, const QString &to) {
        QString tmp = s;
        s = to;
        return tmp;
    }
};

#endif // NATIVEPOINTERTESTER_H
