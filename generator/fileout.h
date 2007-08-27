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

#ifndef FILEOUT_H
#define FILEOUT_H

#include <QObject>
#include <QFile>
#include <QTextStream>

class FileOut : public QObject
{
    Q_OBJECT

private:
    QByteArray tmp;
    QString name;

public:
    FileOut(QString name);
    ~FileOut() { done(); }

    bool done();
    
    QTextStream stream;
 
};

#endif // FILEOUT_H
