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

#ifndef GENERATOR_SET_H
#define GENERATOR_SET_H

#include <QObject>
#include <QString>
#include <QStringList>
#include <QMap>

class GeneratorSet : public QObject
{
    Q_OBJECT

 public:
    GeneratorSet();

    virtual QString usage() = 0;
    virtual bool readParameters(const QMap<QString, QString> args) = 0;
    virtual void buildModel(const QString pp_file) = 0;
    virtual void dumpObjectTree() = 0;
    virtual QString generate() = 0;

    static GeneratorSet *getInstance();
    QString outDir;
    bool printStdout;
};

#endif // GENERATOR_SET_H
