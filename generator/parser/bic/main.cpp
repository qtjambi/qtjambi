/* Class dumper */

#include "ast.h"
#include "tokens.h"
#include "lexer.h"
#include "parser.h"
#include "control.h"
#include "binder.h"
#include "codemodel.h"

#include <QtCore/QtCore>

#include <stdlib.h>

QString qtDir;
FileModelItem dom;

QStringList classSizes;
QStringList unionSizes;

QString stripped(QString type)
{
    type.remove(QRegExp("\\[.*\\]"));
    type.remove("const");
    type.remove("volatile");
    type.remove("register");
    type.remove("mutable");
    type.remove("&");
    return type.simplified();
}

// finds enums or variables in a scope
CodeModelItem findInScope(ScopeModelItem item, const QStringList &typeName)
{
//    Q_ASSERT(!typeName.isEmpty());
    /* HACK */
    if (typeName.isEmpty())
        return CodeModelItem();
    /* ENDHACK */
    QString tp = typeName.last();

    for (int i = 0; i < typeName.count() - 1; ++i) {
        QString ns = typeName.at(i);
        // check for namespace
        if (NamespaceModelItem currentNs = model_dynamic_cast<NamespaceModelItem>(item)) {
            if (NamespaceModelItem nsItem = currentNs->findNamespace(ns)) {
                item = nsItem;
                continue;
            }
        }
        // otherwise, check for a class
        if (ClassModelItem clItem = item->findClass(ns)) {
            item = clItem;
            continue;
        }
        // bad luck - neither a class nor a namespace
        return CodeModelItem();
    }

    // check whether it's an enum or variable
    if (EnumModelItem eItem = item->findEnum(tp))
        return model_static_cast<CodeModelItem>(eItem);
    if (VariableModelItem vItem = item->findVariable(tp))
        return model_static_cast<CodeModelItem>(vItem);
    if (ClassModelItem clItem = item->findClass(tp))
        return model_static_cast<CodeModelItem>(clItem);
    return CodeModelItem();
}


bool isBasicType(const QString &type)
{
    static QStringList simpleTypes;
    if (simpleTypes.isEmpty())
        simpleTypes << "bool" << "char" << "uchar" << "short" << "ushort" << "int" << "uint"
                    << "double" << "qint64" << "quint64" << "qreal" << "qlonglong" << "qulonglong";
    if (simpleTypes.contains(type))
        return true;

    return false;
}

bool isPointer(const QString &type)
{
    return type.endsWith('*');
}

bool hasVirtuals(ClassModelItem klass)
{
    const QMultiHash<QString, FunctionModelItem> functionMap = klass->functionMap();
    for (QMultiHash<QString, FunctionModelItem>::const_iterator it = functionMap.constBegin();
         it != functionMap.constEnd(); ++it) {
        if (it.value()->isVirtual())
            return true;
    }
    return false;
}

QString unionString(ClassModelItem klass)
{
    static int N = 0;
    QString unionSizeName = QString("union_%1_%2").arg(klass->name()).arg(++N);

    QString s = QString("int %1[] = {").arg(unionSizeName);

    VariableList vars = klass->variables();
    for (int i = 0; i < vars.count(); ++i) {
        VariableModelItem item = vars.at(i);
        QString tp = item->type().toString();
        QString stp = stripped(tp);
        if (isPointer(stp)) {
            s += " sizeof(void* /* " + stp + " " + item->name() + " */),";
        } else if (isBasicType(stp)) {
            s += " sizeof(" + tp + " /* " + item->name() + " */),";
        } else {
            qFatal("unionString: don't know how to handle %s in union", qPrintable(tp));
        }
    }
    s += " -1 };";
    unionSizes.append(s);
    return QString("qMaxOf(%1) + ").arg(unionSizeName);
}

QString sizeString(ClassModelItem klass)
{
    Q_ASSERT(klass);

    QString s;
    QStringList baseClasses = klass->baseClasses();

    if (klass->classType() == CodeModel::Union) {
        if (!baseClasses.isEmpty())
            qFatal("cannot handle unions with parent classes.");
        return unionString(klass);
    }

    int i;
    for (i = 0; i < baseClasses.count(); ++i) {
        s += "sizeof(" + baseClasses.at(i) + ") + ";
    }
    if (baseClasses.isEmpty() && hasVirtuals(klass)) {
        s += "sizeof(void* /* vtable */) + ";
    }
    VariableList vars = klass->variables();
    for (i = 0; i < vars.count(); ++i) {
        VariableModelItem item = vars.at(i);
        if (item->isStatic())
            continue;
        QString tp = item->type().toString();
 //       qDebug() << item->type().qualifiedName();
        QString stp = stripped(tp);
        if (!isBasicType(stp) && !dom->classMap().contains(stp)) {
            if (isPointer(stp)) {
                // could be private - convert to void *
                tp.replace(stp, "void* /* " + stp + " */");
            } else {
                CodeModelItem localItem = findInScope(model_static_cast<ScopeModelItem>(klass), item->type().qualifiedName());
                if (model_safe_cast<EnumModelItem>(localItem)) {
                    // need to qualify
                    tp.replace(stp, klass->name() + "::" + stp);
                } else if (model_safe_cast<ClassModelItem>(localItem)) {
                    // recurse into local class
                    s += sizeString(model_static_cast<ClassModelItem>(localItem));
                    continue;
                } else {
                    CodeModelItem globalItem = findInScope(model_static_cast<ScopeModelItem>(dom), item->type().qualifiedName());
                    if (model_safe_cast<ClassModelItem>(globalItem)) {
                        // recurse into scoped class
                        s += sizeString(model_static_cast<ClassModelItem>(globalItem));
                        continue;
                    } else if (!model_safe_cast<EnumModelItem>(globalItem)) {
                        qDebug() << "unknown type in" << klass->name() << ":" << tp;
                    }
                }
            }
        }
        s += "sizeof(" + tp + " /* " + item->name() + " */) + ";
    }
    return s;
}

void printSizes()
{
    const QHash<QString, ClassModelItem> typeMap = dom->classMap();
    for (QHash<QString, ClassModelItem>::const_iterator it = typeMap.constBegin();
         it != typeMap.constEnd(); ++it) {
        if (it.key().startsWith('Q')) {
            QString s = QString("*t.newData(\"%1\") << int(sizeof(%1)) << int(").arg(it.key());
            s += sizeString(it.value());
            if (s.endsWith("int("))
                s.append("0");
            else
                s.chop(3);
            s.append(");");
            classSizes.append(s);
        }
    }
    QString s;
    foreach(s, unionSizes)
        printf("%s\n", qPrintable(s));
    printf("\n");
    foreach(s, classSizes)
        printf("%s\n", qPrintable(s));
}

bool dumpSymbols(const QString &fileName)
{
    QProcess proc;
    QStringList args;
    args << "-xc++"
         << "-I/home/harald/local/gcc4/lib/gcc/i686-pc-linux-gnu/4.0.1/include"
         << "-I" + qtDir + "/include"
         << "-include" << "../r++.macros"
         << qtDir + "/include/" + fileName;
    proc.start(QLatin1String("cpp"), args);
    if (!proc.waitForFinished())
        return false;
    if (proc.exitCode())
        qCritical() << "cpp exited with" << proc.exitCode() << "errors:"
                    << proc.readAllStandardError();

    QByteArray contents = proc.readAllStandardOutput();

    Control control;
    Parser p(&control);
    pool pool;

    TranslationUnitAST *ast = p.parse(contents, contents.size(), &pool);
    if (!ast) {
        return false;
    }

    CodeModel model;
    Binder binder(&model, &p.token_stream);
    dom = binder.run(ast);

    if (p.problemCount() == 0) {
        printSizes();
    }

    return p.problemCount() == 0;
}

int main(int argc, char * argv[])
{
    qtDir = QString::fromLocal8Bit(qgetenv("QTDIR"));
    if (qtDir.isEmpty())
        qFatal("please set your QTDIR enviroment variable");
    if (argc != 2)
        qFatal("%s: Pass a single file relative to $QTDIR/include as argument", argv[0]);

    QString fileName = QString::fromLocal8Bit(argv[1]);

    return dumpSymbols(fileName);
}

