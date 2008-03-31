#include "jumptable.h"
#include "cppimplgenerator.h"
#include "reporthandler.h"


static QHash<QString, QString> shortNames;
static QHash<char, QString> expandNames;

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

static QString expandName(const QChar &c) {
    if (expandNames.size() == 0) {
        expandNames.insert('Z', "jboolean");
        expandNames.insert('B', "jbyte");
        expandNames.insert('C', "jchar");
        expandNames.insert('S', "jshort");
        expandNames.insert('I', "jint");
        expandNames.insert('J', "jlong");
        expandNames.insert('F', "jfloat");
        expandNames.insert('D', "jdouble");
        expandNames.insert('L', "jobject");
        expandNames.insert('V', "void");
    }

    QString n = ((const QHash<char, QString> &) expandNames).value(c.toLatin1());
    if (n.isEmpty())
        printf("Failed to translate to expanded names: %c\n", c.toLatin1());

    return n;
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

    AbstractMetaFunctionList &list = (*table)[sig];
    list.append(func);
    func->setJumpTableId(list.size());
}


void JumpTableGenerator::generate()
{
    for (PackageJumpTable::const_iterator it = m_preprocessor->table()->constBegin();
         it != m_preprocessor->table()->constEnd(); ++it) {
        QString package = it.key();
        generatePackage(package, it.value());
    }
}

void JumpTableGenerator::generatePackage(const QString &packageName, const SignatureTable &table)
{
    QString tableFile = QString("%1/%2/jumptable.cpp")
                        .arg(outputDirectory())
                        .arg(CppGenerator::subDirectoryForPackage(packageName));

    QFile file(tableFile);
    if (!file.open(QFile::WriteOnly)) {
        ReportHandler::warning(QString("Failed to open file '%1' for writing, reason=%2")
                              .arg(tableFile)
                              .arg(file.errorString()));
        return;
    }


    printf("Generating jump table: %s\n", qPrintable(tableFile));

    QTextStream s(&file);

    s << "#include <qtjambi_global.h>" << endl;

    for (SignatureTable::const_iterator sit = table.constBegin(); sit != table.constEnd(); ++sit) {
        QString signature = sit.key();

        QString ret = expandName(signature.at(0));

        s << endl << endl
          << "extern \"C\" JNIEXPORT " << ret << " JNICALL QTJAMBI_FUNCTION_PREFIX(Java_"
          << QString(packageName).replace("_", "_1").replace(".", "_") << "_JTbl_" << signature << ")" << endl
          << "(JNIEnv *e, jclass, jint id";

        for (int i=1; i<signature.size(); ++i) {
            s << ", " << expandName(signature.at(i)) << " a" << i;
        }

        s << ")" << endl
          << "{" << endl
          << "    switch (id) { " << endl;

        AbstractMetaFunctionList functions = sit.value();

        foreach (AbstractMetaFunction *f, functions) {
            s << endl
              << "    // " << f->implementingClass()->name() << "::" << f->signature() << endl
              << "    case " << f->jumpTableId() << ":" << endl;
        }

        s << "    }" << endl
          << "}" << endl;
    }
}
