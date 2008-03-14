#include "jumptable.h"
#include "cppimplgenerator.h"
#include "reporthandler.h"


static QHash<QString, QString> shortNames;

static QString simplifyName(const QString &name)
{
    if (shortNames.size() == 0) {
        shortNames.insert("jboolean", "Z");
        shortNames.insert("jbyte", "B");
        shortNames.insert("jchar", "C");
        shortNames.insert("jshort", "S");
        shortNames.insert("jint", "I");
        shortNames.insert("jlong", "J");
        shortNames.insert("jfloat", "F");
        shortNames.insert("jdouble", "D");
        shortNames.insert("jobject", "L");
        shortNames.insert("void", "V");
    }

    QString sn = ((const QHash<QString, QString> &) shortNames).value(name);
    if (sn.isEmpty())
        printf("Failed to translate to shortname: %s\n", qPrintable(name));

    return shortNames.value(name);
}


void JumpTablePreprocessor::generate()
{
    ReportHandler::setContext("JumpTablePreprocessor");
    foreach (AbstractMetaClass *cl, m_classes) {
        process(cl);
    }
}

void JumpTablePreprocessor::process(AbstractMetaClass *cls)
{
    QString package = cls->package();

    if (!m_table.contains(package))
        m_table[package] = SignatureTable();


    SignatureTable &signatureList = m_table[package];

    AbstractMetaFunctionList funcs = cls->functionsInTargetLang();

    foreach (AbstractMetaFunction *func, funcs) {
        if (func->needsCallThrough()) {
            process(func, &signatureList);
        }
    }
}


QString JumpTablePreprocessor::signature(AbstractMetaFunction *func)
{
    QString signature;

    if (func->argumentRemoved(0))
        signature = "V";
    else
        signature = simplifyName(CppImplGenerator::jniReturnName(func));

    signature += "_";

    AbstractMetaArgumentList args = func->arguments();
    foreach (const AbstractMetaArgument *a, args) {
        if (!func->argumentRemoved(a->argumentIndex() + 1)) {
            if (!a->type()->hasNativeId())
                signature += simplifyName(CppImplGenerator::translateType(a->type(), EnumAsInts));
            else
                signature += "J";
        }
    }

    return signature;
}


void JumpTablePreprocessor::process(AbstractMetaFunction *func, SignatureTable *table)
{
    QString sig = signature(func);

    printf("'%s'\t for %s '%s'\n",
           qPrintable(sig),
           qPrintable(func->implementingClass()->name()),
           qPrintable(func->signature()));


    AbstractMetaFunctionList &list = (*table)[sig];
    list.append(func);
    func->setJumpTableId(list.size());
}


void JumpTableGenerator::generate()
{
    int total = 0;
    int totalFunctions = 0;
    for (PackageJumpTable::const_iterator it = m_preprocessor->table()->constBegin();
         it != m_preprocessor->table()->constEnd(); ++it) {
        QString package = it.key();
        printf("Package: '%s'\n", qPrintable(package));

        const SignatureTable &table = it.value();
        for (SignatureTable::const_iterator sit = table.constBegin(); sit != table.constEnd(); ++sit) {
            QString signature = sit.key();
            printf(" - '%s', count: %d %s\n", qPrintable(signature), sit.value().size(), qPrintable(sit.value().at(0)->signature()));
            totalFunctions += sit.value().size();

            ++total;
        }
    }

    printf("in total: %d out of %d\n", total, totalFunctions);
}
