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

#ifndef JUMPTABLE_H
#define JUMPTABLE_H

#include "generator.h"
#include "abstractmetalang.h"
#include "prigenerator.h"


typedef QHash<QString, AbstractMetaFunctionList> SignatureTable;
typedef QHash<QString, SignatureTable> PackageJumpTable;


class JumpTablePreprocessor : public Generator
{
    Q_OBJECT
public:
    void generate();

    static QString signature(const AbstractMetaFunction *func);

    inline const PackageJumpTable *table() const { return &m_table; }

    bool usesJumpTable(AbstractMetaFunction *func);

private:
    void process(AbstractMetaClass *cls);
    void process(AbstractMetaFunction *cls, SignatureTable *sigList);
    PackageJumpTable m_table;
};


class JumpTableGenerator : public Generator
{
    Q_OBJECT
public:
    JumpTableGenerator(JumpTablePreprocessor *pp, PriGenerator *pri);

    void generate();
    void generatePackage(const QString &packageName, const SignatureTable &table);
    void generateNativeTable(const QString &packageName, const SignatureTable &table);
    void generateJavaTable(const QString &packageName, const SignatureTable &table);

    static bool isJumpTableActive();

private:
    JumpTablePreprocessor *m_preprocessor;
    PriGenerator *m_prigenerator;

    static bool active;
};

#endif
