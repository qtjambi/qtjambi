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

    static QString signature(AbstractMetaFunction *func);

    inline const PackageJumpTable *table() const { return &m_table; }

private:
    void process(AbstractMetaClass *cls);
    void process(AbstractMetaFunction *cls, SignatureTable *sigList);
    PackageJumpTable m_table;
};


class JumpTableGenerator : public Generator
{
    Q_OBJECT
public:
    JumpTableGenerator(JumpTablePreprocessor *pp, PriGenerator *pri)
        : m_preprocessor(pp),
          m_prigenerator(pri)
    {
    }

    void generate();
    void generatePackage(const QString &packageName, const SignatureTable &table);

private:
    JumpTablePreprocessor *m_preprocessor;
    PriGenerator *m_prigenerator;
};

#endif
