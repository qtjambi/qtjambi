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

#include "generator.h"
#include "reporthandler.h"
#include "fileout.h"

#include <QDir>
#include <QFile>
#include <QFileInfo>

Generator::Generator()
{
    m_num_generated = 0;
    m_num_generated_written = 0;
    m_out_dir = ".";
}

void Generator::generate()
{
    if (m_classes.size() == 0) {
        ReportHandler::warning(QString("%1: no java classes, skipping")
                               .arg(metaObject()->className()));
        return;
    }


    foreach (AbstractMetaClass *cls, m_classes) {
        if (!shouldGenerate(cls))
            continue;

        QString fileName = fileNameForClass(cls);
        ReportHandler::debugSparse(QString("generating: %1").arg(fileName));

        FileOut fileOut(outputDirectory() + "/" + subDirectoryForClass(cls) + "/" + fileName);
        write(fileOut.stream, cls);

        if( fileOut.done() )
            ++m_num_generated_written;
        ++m_num_generated;
    }
}


void Generator::printClasses()
{
    QTextStream s(stdout);

    AbstractMetaClassList classes = m_classes;
    qSort(classes);

    foreach (AbstractMetaClass *cls, classes) {
        if (!shouldGenerate(cls))
            continue;
        write(s, cls);
        s << endl << endl;
    }
}

void Generator::verifyDirectoryFor(const QFile &file)
{
    QDir dir = QFileInfo(file).dir();
    if (!dir.exists()) {
        if (!dir.mkpath(dir.absolutePath()))
            ReportHandler::warning(QString("unable to create directory '%1'")
                                   .arg(dir.absolutePath()));
    }
}

QString Generator::subDirectoryForClass(const AbstractMetaClass *) const
{
    Q_ASSERT(false);
    return QString();
}

QString Generator::fileNameForClass(const AbstractMetaClass *) const
{
    Q_ASSERT(false);
    return QString();
}

void Generator::write(QTextStream &, const AbstractMetaClass *)
{
    Q_ASSERT(false);
}

