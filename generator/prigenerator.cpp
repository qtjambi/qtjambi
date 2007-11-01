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

#include "prigenerator.h"
#include "reporthandler.h"
#include "fileout.h"

void PriGenerator::addHeader(const QString &folder, const QString &header)
{
    priHash[folder].headers << header;
}

void PriGenerator::addSource(const QString &folder, const QString &source)
{
    priHash[folder].sources << source;
}

void PriGenerator::generate()
{
    QHashIterator<QString, Pri> pri(priHash);
    while (pri.hasNext()) {
        pri.next();

        FileOut file(m_out_dir + "/cpp/" + pri.key());
        file.stream << "HEADERS += \\\n";
        QStringList list = pri.value().headers;
        qSort(list.begin(), list.end());
        foreach (const QString &entry, list) {
            file.stream << "           $$PWD/" << entry << " \\\n";
        }

        file.stream << "\n";
        file.stream << "SOURCES += \\\n";
        list = pri.value().sources;
        qSort(list.begin(), list.end());
        foreach (const QString &entry, list) {
            file.stream << "           $$PWD/" << entry << " \\\n";
        }
        file.stream << "\n\n";

        if (file.done())
            ++m_num_generated_written;
        ++m_num_generated;
    }
}
