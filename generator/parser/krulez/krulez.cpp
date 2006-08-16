#include <QtCore/QtCore>
#include <QtTest/QtTest>

#include "ast.h"
#include "tokens.h"
#include "lexer.h"
#include "parser.h"
#include "control.h"
#include "binder.h"
#include "codemodel.h"
#include "rpp/preprocessor.h"

#include <stdio.h>

static QString qtDir()
{
    static QString d = QString::fromLocal8Bit(qgetenv("QTDIR"));
    return d;
}

static QString kdeDir()
{
    static QString d = QString::fromLocal8Bit(qgetenv("KDEDIR"));
    return d;
}

static QStringList includePaths()
{
    QStringList incs;
    incs << kdeDir() + QLatin1String("/include");
    incs << qtDir() + QLatin1String("/include");
    incs << qtDir() + QLatin1String("/include/QtCore");
    incs << qtDir() + QLatin1String("/include/QtGui");
    return incs;
}

static QByteArray defines()
{
    return "#define __cplusplus 1\n"
           "#define __STDC__\n"
           "#define Q_CC_GNU\n"
           "#define Q_WS_X11\n"
           "#define QT3_SUPPORT\n"
           "#define KDE_DEPRECATED\n"
           "#define KDE_CONSTRUCTOR_DEPRECATED\n"
           "#define QT_NO_STYLE_WINDOWSXP\n";
}

static QStringList headers()
{
#if 0
    QDir dir("/home/harald/local/kde4/include", "*.h");
    QStringList hdrs = dir.entryList().mid(0, 7);

    for (int i = 0; i < hdrs.count(); ++i)
        hdrs[i].prepend("/home/harald/local/kde4/include/");

    qDebug() << hdrs;
#endif

    return QStringList(kdeDir() + "/include/kurl.h");
    //return hdrs;
}

Q_DECLARE_METATYPE(ClassModelItem)
Q_DECLARE_METATYPE(TypeAliasModelItem)

class tst_QRulez: public QObject
{
    Q_OBJECT

    FileModelItem dom;
    QList<ClassModelItem> allClasses;
    QList<FunctionModelItem> allOrOperators;
    void getAllClasses();

    void allClasses_data()
    {
        QTest::addColumn<ClassModelItem>("klass");
        foreach (ClassModelItem it, allClasses)
            QTest::newRow(it->qualifiedName().join("::").toLatin1()) << it;
    }

private slots:
    void initTestCase();

    void compareOperatorsConst_data() { allClasses_data(); }
    void compareOperatorsConst();
    void equalAndUnEqualOp_data() { allClasses_data(); }
    void equalAndUnEqualOp();
    void checkQFlags_data();
    void checkQFlags();
    void copyAndAssignment_data() { allClasses_data(); }
    void copyAndAssignment();
};

static void qGetAllClasses(QList<ClassModelItem> &list, const ScopeModelItem &it)
{
    const QHash<QString, ClassModelItem> classMap = it->classMap();
    foreach (ClassModelItem klass, classMap) {
        list << klass;
        qGetAllClasses(list, model_static_cast<ScopeModelItem>(klass));
    }
    if (NamespaceModelItem ns = model_dynamic_cast<NamespaceModelItem>(it)) {
        const QHash<QString, NamespaceModelItem> nsMap = ns->namespaceMap();
        foreach (NamespaceModelItem nsi, nsMap)
            qGetAllClasses(list, model_static_cast<ScopeModelItem>(nsi));
    }
}

static void qGetAllTypedefs(QList<TypeAliasModelItem> &list, const ScopeModelItem &nsi)
{
    list << nsi->typeAliasMap().values();
    foreach (ClassModelItem it, nsi->classMap().values())
        qGetAllTypedefs(list, model_static_cast<ScopeModelItem>(it));
    if (NamespaceModelItem ns = model_dynamic_cast<NamespaceModelItem>(nsi)) {
        const QHash<QString, NamespaceModelItem> nsMap = ns->namespaceMap();
        foreach (NamespaceModelItem cns, nsMap)
            qGetAllTypedefs(list, model_static_cast<ScopeModelItem>(cns));
    }
}

void tst_QRulez::getAllClasses()
{
    Q_ASSERT(allClasses.isEmpty());
    qGetAllClasses(allClasses, model_static_cast<ScopeModelItem>(dom));
}

void tst_QRulez::initTestCase()
{
    Preprocessor pp;

    pp.addIncludePaths(includePaths());

    pp.processString(defines());

    foreach (QString hdr, headers())
        pp.processFile(hdr);

    QByteArray contents = pp.result();
    qDebug("preprocessed");

    Control control;
    Parser p(&control);
    qDebug(" 1");
    pool pool;

    TranslationUnitAST *ast = p.parse(contents, contents.size(), &pool);
    QVERIFY(ast);
    qDebug(" 2");

    CodeModel model;
    Binder binder(&model, p.location());
    qDebug(" 3");
    dom = binder.run(ast);
    qDebug(" 4");

    QCOMPARE(p.problemCount(), 0);

    getAllClasses();
    allOrOperators = dom->functionMap().values("operator|");
}

static void stripFriendDecls(QList<FunctionModelItem> &funcs)
{
    int i = 0;
    while (i < funcs.count()) {
        if (funcs.at(i)->isFriend())
            funcs.removeAt(i);
        else
            ++i;
    }
}

void tst_QRulez::compareOperatorsConst()
{
    QFETCH(ClassModelItem, klass);

#if QT_VERSION < 0x050000
    if (klass->name() == "QFileInfo"
        || klass->name().startsWith("QFontMetrics")
        || klass->name() == "QGradient")
        return;
#endif

    QList<FunctionModelItem> funcs = klass->functionMap().values("operator==");
    funcs += klass->functionMap().values("operator!=");
    stripFriendDecls(funcs);

    foreach (FunctionModelItem func, funcs)
        QVERIFY(func->isConstant());
}

// makes sure every class with an operator== also has an operator!=
void tst_QRulez::equalAndUnEqualOp()
{
    QFETCH(ClassModelItem, klass);

    QList<FunctionModelItem> equalOperators = klass->functionMap().values("operator==");
    stripFriendDecls(equalOperators);
    QList<FunctionModelItem> unEqualOperators = klass->functionMap().values("operator!=");
    stripFriendDecls(unEqualOperators);

#if QT_VERSION < 0x040200
    QEXPECT_FAIL("QPersistentModelIndex", "QPersistentModelIndex was fixed in 4.2", Continue);
#endif
#if QT_VERSION < 0x050000
    QEXPECT_FAIL("QGradient", "QGradient will be fixed in 5.0", Continue);
#endif

    QCOMPARE(equalOperators.count(), unEqualOperators.count());
}

void tst_QRulez::checkQFlags_data()
{
    QList<TypeAliasModelItem> allTypedefs;
    qGetAllTypedefs(allTypedefs, model_static_cast<ScopeModelItem>(dom));

    QTest::addColumn<TypeAliasModelItem>("it");

    foreach (TypeAliasModelItem it, allTypedefs) {
        const QString type = it->type().qualifiedName().last();
        if (type.startsWith("QFlags<"))
            QTest::newRow(type.toLatin1()) << it;
    }
}

void tst_QRulez::checkQFlags()
{
    QFETCH(TypeAliasModelItem, it);

    const QString enumType = it->qualifiedName().join("::") + "::enum_type";
    bool hasOperators = false;
    foreach (FunctionModelItem func, allOrOperators) {
        const ArgumentList args = func->arguments();
        if (args.count() == 2
                && args.at(0)->type().qualifiedName().join("::") == enumType
                && args.at(1)->type().qualifiedName().join("::") == enumType) {
            hasOperators = true;
            break;
        }
    }
    QVERIFY(hasOperators);
}

static bool hasCopyConstructor(const ClassModelItem &klass)
{
    /* sigh - this could be made better since it isn't very namespace
     * or template specialization safe. Good enough for Qt, though ;) */
    const QString klassName = klass->name().left(klass->name().indexOf('<'));
    QList<FunctionModelItem> constrs = klass->functionMap().values(klassName);

    foreach (FunctionModelItem c, constrs) {
        const ArgumentList args = c->arguments();
        if (args.count() == 1) {
            const TypeInfo ti = args.at(0)->type();
            // hack - try with and without template arguments
            if (ti.indirections() == 0 && (ti.qualifiedName().last() == klass->name()
                        || ti.qualifiedName().last() == klassName))
                return true;
        }
    }
    return false;
}

void tst_QRulez::copyAndAssignment()
{
    QFETCH(ClassModelItem, klass);

    static QStringList whiteList;
    if (whiteList.isEmpty())
        whiteList << "QModelIndex" << "QGlobalStatic<T>" << "QGenericArgument" << "QLatin1String";

    if (klass->name().contains("Data")
        || klass->name().contains("iterator")
        || klass->name().contains("Iterator")
        || klass->name().contains("Node")
        || klass->name().contains("Dummy")
        || klass->name().contains("Helper")
        || klass->name().startsWith("QMeta")
        || klass->name().contains("Private")
        || whiteList.contains(klass->name()))
        return;

    const QHash<QString, VariableModelItem> variableMap = klass->variableMap();
    bool hasPointer = false;
    foreach (VariableModelItem var, variableMap) {
        TypeInfo ti = var->type();
        if (!var->isStatic() && ti.indirections()) {
            hasPointer = true;
            break;
        }
    }
    if (!hasPointer)
        return;

    QVERIFY(klass->functionMap().values("operator=").count() != 0);
    QVERIFY(hasCopyConstructor(klass));
}

QTEST_MAIN(tst_QRulez)
#include "qrulez.moc"
