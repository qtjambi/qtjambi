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

#ifndef REPORTHANDLER_H
#define REPORTHANDLER_H

#include <QtCore/QString>
#include <QtCore/QSet>

class ReportHandler
{
public:
    enum DebugLevel { NoDebug, SparseDebug, MediumDebug, FullDebug };

    static void setContext(const QString &context) { m_context = context; }

    static DebugLevel debugLevel() { return m_debug_level; }
    static void setDebugLevel(DebugLevel level) { m_debug_level = level; }

    static int warningCount() { return m_warning_count; }

    static int suppressedCount() { return m_suppressed_count; }

    static void warning(const QString &str);

    static void debugSparse(const QString &str) {
        debug(SparseDebug, str);
    }
    static void debugMedium(const QString &str) {
        debug(MediumDebug, str);
    }
    static void debugFull(const QString &str) {
        debug(FullDebug, str);
    }
    static void debug(DebugLevel level, const QString &str);

private:
    static int m_warning_count;
    static int m_suppressed_count;
    static DebugLevel m_debug_level;
    static QString m_context;
    static QSet<QString> m_reported_warnings;
};

#endif // REPORTHANDLER_H
