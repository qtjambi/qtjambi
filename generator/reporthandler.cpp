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

#include "reporthandler.h"
#include "typesystem.h"

int ReportHandler::m_warning_count = 0;
int ReportHandler::m_suppressed_count = 0;
QString ReportHandler::m_context;
ReportHandler::DebugLevel ReportHandler::m_debug_level = NoDebug;

void ReportHandler::warning(const QString &text)
{
    QString warningText = QString("WARNING(%1) :: %2").arg(m_context).arg(text);

    TypeDatabase *db = TypeDatabase::instance();
    if (db && db->isSuppressedWarning(warningText)) {
        ++m_suppressed_count;
    } else {
        qDebug(qPrintable(warningText));
        ++m_warning_count;
    }
}

void ReportHandler::debug(DebugLevel level, const QString &text)
{
    if (m_debug_level == NoDebug)
        return;

    if (level <= m_debug_level)
        qDebug(" - DEBUG(%s) :: %s", qPrintable(m_context), qPrintable(text));
}
