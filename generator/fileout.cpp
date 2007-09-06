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

#include "fileout.h"
#include "reporthandler.h"

#include <QFileInfo>
#include <QDir>

bool FileOut::dummy = false;

FileOut::FileOut(QString n):
    name(n),
    stream(&tmp),
    isDone(false)
{}

bool FileOut::done() {
    Q_ASSERT( !isDone );
    isDone = true;
    bool fileEqual = false;
    QFile fileRead(name);
    QFileInfo info(fileRead);
    stream.flush();
    if( info.exists() && info.size() == tmp.size() ) {
        if ( !fileRead.open(QIODevice::ReadOnly) ) {
            ReportHandler::warning(QString("failed to open file '%1' for reading")
                                   .arg(fileRead.fileName()));
            return false;
        }
        
        QByteArray original = fileRead.readAll();
        fileRead.close();
        fileEqual = (original == tmp);
    }
    
    if( !fileEqual ) {
        if( !FileOut::dummy ) {
            QDir dir(info.absolutePath());
            dir.mkpath(dir.absolutePath());

            QFile fileWrite(name);
            if (!fileWrite.open(QIODevice::WriteOnly)) {
                ReportHandler::warning(QString("failed to open file '%1' for writing")
                                       .arg(fileWrite.fileName()));
                return false;
            }
            stream.setDevice(&fileWrite);
            stream << tmp;
        }
        return true;
    }
    return false;
}
