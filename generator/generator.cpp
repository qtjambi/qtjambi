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
    if (m_java_classes.size() == 0) {
        ReportHandler::warning(QString("%1: no java classes, skipping")
                               .arg(metaObject()->className()));
        return;
    }


    foreach (MetaJavaClass *cls, m_java_classes) {
        QDir dir(outputDirectory() + "/" + subDirectoryForClass(cls));
        dir.mkpath(dir.absolutePath());

        if (!shouldGenerate(cls))
            continue;

        QString fileName = fileNameForClass(cls);
        ReportHandler::debugSparse(QString("generating: %1").arg(fileName));

        QByteArray tmp;
        QTextStream s(&tmp);
        write(s, cls);
         
        QFile fileRead(dir.absoluteFilePath(fileName));
        bool fileEqual = false;
        QFileInfo info(fileRead);
        if( info.exists() && info.size() == tmp.size() ) {
            if ( !fileRead.open(QIODevice::ReadOnly) ) {
                ReportHandler::warning(QString("failed to open file '%1' for reading")
                                       .arg(fileRead.fileName()));
                continue;
            }
            
            QByteArray original = fileRead.readAll();
            fileEqual = (original == tmp);
        }
        if( !fileEqual ) {        
            QFile fileWrite(dir.absoluteFilePath(fileName));
            if (!fileWrite.open(QIODevice::WriteOnly)) {
                ReportHandler::warning(QString("failed to open file '%1' for writing")
                                       .arg(fileWrite.fileName()));
                continue;
            }
            s.setDevice(&fileWrite);
            s << tmp;
            ++m_num_generated_written;
        }
                 
        ++m_num_generated;
    }
}


void Generator::printClasses()
{
    QTextStream s(stdout);

    MetaJavaClassList classes = m_java_classes;
    qSort(classes);

    foreach (MetaJavaClass *cls, classes) {
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

QString Generator::subDirectoryForClass(const MetaJavaClass *) const
{
    Q_ASSERT(false);
    return QString();
}

QString Generator::fileNameForClass(const MetaJavaClass *) const
{
    Q_ASSERT(false);
    return QString();
}

void Generator::write(QTextStream &, const MetaJavaClass *)
{
    Q_ASSERT(false);
}

