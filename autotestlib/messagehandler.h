#ifndef MESSAGEHANDLER_H
#define MESSAGEHANDLER_H

#include <qstring.h>

class MessageHandler
{
public:
    static void sendDebug(const QString &str) { qDebug(str.toLocal8Bit()); }
    static void sendWarning(const QString &str) { qWarning(str.toLocal8Bit()); }
    static void sendCritical(const QString &str) { qCritical(str.toLocal8Bit()); }
    static void sendFatal(const QString &str) { qFatal(str.toLocal8Bit()); }
};

#endif // MESSAGEHANDLER_H
