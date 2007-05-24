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

#include <QtTest/QtTest>

#include "ast.h"
#include "tokens.h"
#include "lexer.h"
#include "parser.h"
#include "control.h"
#include "binder.h"
#include "codemodel.h"

class tst_Binder: public QObject
{
    Q_OBJECT

private slots:
    FileModelItem parse(QByteArray code);

    void inlineDefinitions();
    void staticInline();
    void volatileParameters();
    void overloadedConstructors();
    void variadics();
    void typedefs();
    void qualifiedArgumentName();
    void funptr();
    void templates();
    void polymorphic();
    void castOperator();
    void pureVirtualDestructor();
    void signalsSlots();
    void templateTypedefs();
    void arrays();
    void impldecl();
    void testDefArg();
    void testDefArg2();
};

FileModelItem tst_Binder::parse(QByteArray code)
{
    code.prepend("# 1 \"<stdin>\"\n");
    code.append("\n");

    FileModelItem dom;

    Control control;
    Parser p(&control);
    pool pool;

    TranslationUnitAST *ast = p.parse(code.constData(), code.size(), &pool);
    if (!ast)
        return FileModelItem();

    CodeModel model;
    Binder binder(&model, p.location());
    dom = binder.run(ast);

    return control.errorMessages().isEmpty() ? dom : FileModelItem();
}

void tst_Binder::inlineDefinitions()
{
    FileModelItem item = parse("class Foo { void bar(); }; inline void Foo::bar() {}");

    ClassModelItem klass = item->classMap().value("Foo");
    QCOMPARE(klass->name(), QString("Foo"));
    FunctionModelItem func = klass->functionMap().value("bar");
    QCOMPARE(func->name(), QString("bar"));
    QVERIFY(func->isInline());

    item = parse("template <typename T> class Foo { Foo<T>& operator=(const Foo<T>&); };"
                 "template <typename T> inline Foo<T> &Foo<T>::operator=(const Foo<T>&) {}");

    klass = item->classMap().value("Foo<T>");
    QVERIFY(klass);
    QCOMPARE(klass->name(), QString("Foo<T>"));
    QCOMPARE(klass->functionMap().count(), 1);

    //func = klass->functionMap().value("bar");
    //QVERIFY(func);

    //QCOMPARE(func->name(), QString("bar"));
    //QVERIFY(func->isInline());
}

void tst_Binder::staticInline()
{
    FileModelItem item = parse("class Foo { static inline void bar() {} };");

    ClassModelItem klass = item->classMap().value("Foo");
    QVERIFY(klass);
    QCOMPARE(klass->name(), QString("Foo"));
    FunctionModelItem func = klass->functionMap().value("bar");
    QVERIFY(func);
    QCOMPARE(func->name(), QString("bar"));
    QVERIFY(func->isInline());
    QVERIFY(func->isStatic());

    item = parse("class Foo { static void bar() {} };");

    klass = item->classMap().value("Foo");
    QVERIFY(klass);
    QCOMPARE(klass->name(), QString("Foo"));
    func = klass->functionMap().value("bar");
    QVERIFY(func);
    QCOMPARE(func->name(), QString("bar"));
    QVERIFY(!func->isInline());
    QVERIFY(func->isStatic());
}

void tst_Binder::volatileParameters()
{
    FileModelItem item = parse("void foo(volatile int *) {}");

    FunctionModelItem func = item->functionMap().value("foo");
    QVERIFY(func);
    QCOMPARE(func->arguments().count(), 1);
    QCOMPARE(func->arguments().at(0)->type().toString(), QString("int volatile*"));
}

void tst_Binder::overloadedConstructors()
{
    FileModelItem item = parse(
            "class  QString { public: "
            "    inline QString(const QLatin1String &latin1); "
            "    inline QString(const QString &) {}"
            "}; "
            "inline QString::QString(const QLatin1String &latin1) : d(fromLatin1_helper(latin1.latin1())) {} ");

    int i = 0;
    foreach(FunctionModelItem func, item->classMap().value("QString")->functionMap().values("QString")) {
        if (func->arguments().at(0)->type().toString() == "QString const&")
            QVERIFY(func->isInline());
        else if (func->arguments().at(0)->type().toString() == "QLatin1String const&")
            QVERIFY(func->isInline());
        else
            QFAIL("Parser found constructor that doesn't exist");
        ++i;
    }
    QCOMPARE(i, 2);
}

void tst_Binder::variadics()
{
    FileModelItem item = parse("void foo(...);");
    QVERIFY(item->functionMap().value("foo")->isVariadics());

    item = parse("void foo(int...);");
    QVERIFY(item->functionMap().value("foo")->isVariadics());

    item = parse("void foo(int, ...);");
    QVERIFY(item->functionMap().value("foo")->isVariadics());

    item = parse("void foo(int);");
    QVERIFY(!item->functionMap().value("foo")->isVariadics());
}

void tst_Binder::typedefs()
{
    FileModelItem item = parse("namespace Qt { typedef int *Foo; typedef void (*FunPtr)(int*); }"
            " void foo(Qt::Foo *);");

    TypeAliasModelItem ta = item->namespaceMap().value("Qt")->typeAliasMap().value("Foo");
    QVERIFY(ta);
    QCOMPARE(ta->name(), QString("Foo"));
    QCOMPARE(ta->qualifiedName().join("::"), QString("Qt::Foo"));

    TypeInfo ti = item->functionMap().value("foo")->arguments().at(0)->type();
    QCOMPARE(ti.toString(), QString("Qt::Foo*"));

    TypeInfo resolved = TypeInfo::resolveType(ti, item->toItem());
    QCOMPARE(resolved.toString(), QString("int**"));

    QCOMPARE(item->namespaceMap().value("Qt")->typeAliasMap().count(), 2);

    ta = item->namespaceMap().value("Qt")->typeAliasMap().value("FunPtr");
    QVERIFY(ta);
    QCOMPARE(ta->type().toString(), QString("void (*)(int*)"));
}

void tst_Binder::templateTypedefs()
{
    FileModelItem item = parse ("namespace Qt { enum FooEnum { X, Y }; } "
                                "class C { "
                                "typedef QFlag<Qt::FooEnum> FooFlag; "
                                "void fun(FooFlag flag); "
                                "enum MyEnum { A, B }; "
                                "typedef QFlag<MyEnum> MyFlag; "
                                "void fun2(MyFlag *flag); "
                                "};");

    EnumModelItem fooEnum = item->namespaceMap ().value ("Qt")->enumMap ().value ("FooEnum");
    QVERIFY (fooEnum);

    ClassModelItem c = item->classMap ().value ("C");
    QVERIFY (c);

    FunctionModelItem fun = c->functionMap ().value ("fun");
    QVERIFY (fun);

    TypeAliasModelItem fooFlag = c->typeAliasMap ().value("FooFlag");
    QVERIFY (fooFlag);

    QCOMPARE (fooFlag->name (), QString ("FooFlag"));
    QCOMPARE (fooFlag->type ().toString (), QString ("QFlag<Qt::FooEnum>"));

    EnumModelItem myEnum = c->enumMap ().value ("MyEnum");
    QVERIFY (myEnum);

    FunctionModelItem fun2 = c->functionMap ().value ("fun2");
    QVERIFY (fun2);

    QCOMPARE (fun2->arguments ().count (), 1);
    QCOMPARE (fun2->arguments ().at (0)->type ().toString(), QString ("C::MyFlag*"));

    TypeAliasModelItem myFlag = c->typeAliasMap ().value("MyFlag");
    QVERIFY (myFlag);

    QCOMPARE (myFlag->name (), QString ("MyFlag"));
    QCOMPARE (myFlag->type ().toString (), QString ("QFlag<C::MyEnum>"));
}


void tst_Binder::qualifiedArgumentName()
{
    FileModelItem item = parse("class Foo { class Bar {}; void foo(const Bar*); };");
    TypeInfo ti = item->classMap().value("Foo")->functionMap().value("foo")->arguments().at(0)->type();
    QCOMPARE(ti.toString(), QString("Foo::Bar const*"));

    item = parse("class Foo { class Bar{}; }; "
                 "class Foo2: public Foo { void foo(Bar*); };");
    ti = item->classMap().value("Foo2")->functionMap().value("foo")->arguments().at(0)->type();
    QCOMPARE(ti.toString(), QString("Foo::Bar*"));
}

void tst_Binder::funptr()
{
    FileModelItem item = parse("int (*ptr_fun)(int, int);");
    QVERIFY (item->variableMap().value("ptr_fun"));
    TypeInfo ti = item->variableMap().value("ptr_fun")->type();
    QVERIFY (ti.isFunctionPointer());
    QCOMPARE (ti.toString(), QString("int* (*)(int, int)"));
}

ClassList findTemplateFunctionDeclarations (const QString &__name, ScopeModelItem scope)
{
  ClassList klassList = scope->classMap ().values ();
  QMutableListIterator<ClassModelItem> it (klassList);
  while (it.hasNext())
    {
      if (! it.next()->name ().startsWith (__name + QLatin1String ("<")))
        it.remove ();
    }

  return klassList;
}

void tst_Binder::templates()
{
    FileModelItem item = parse ("template <typename _Tp> class Flags { void foo (); };\n"
                                "template <typename _Tp> void Flags<_Tp>::foo () {}\n"
                                "class Klass { enum E {a, b, c};\n"
                                "typedef E TT;\n"
                                "typedef Flags<TT> flags_type; };\n");

    QVERIFY (item->classMap ().count() == 2);

    ClassList klassList = findTemplateFunctionDeclarations ("Flags", model_dynamic_cast<ScopeModelItem> (item));
    QCOMPARE (klassList.count (), 1);

    ClassModelItem flags_klass = item->classMap ().value ("Flags<_Tp>");
    QVERIFY (flags_klass);

    QVERIFY (flags_klass->templateParameters ().count () == 1); // the class is generic
    QVERIFY (flags_klass->functionMap ().value ("foo")); // find the function declaration
    QVERIFY (flags_klass->functionDefinitionMap ().value ("foo")); // find the function definition

    ClassModelItem klass = item->classMap ().value ("Klass");
    QVERIFY (klass->templateParameters ().isEmpty ());
    QVERIFY (klass->enumMap ().value ("E"));

    QVERIFY (klass->templateParameters ().isEmpty ());

    TypeAliasModelItem typeAlias = klass->typeAliasMap ().value ("flags_type");
    QVERIFY (typeAlias);

    QCOMPARE (typeAlias->type ().toString (), QString("Flags<Klass::TT>"));
}

void tst_Binder::polymorphic()
{
    FileModelItem item = parse ("class A { virtual ~A(); };\n");

    ClassModelItem klass = item->findClass ("A");
    QVERIFY (klass);

    QCOMPARE (klass->functions ().count (), 1);
    FunctionModelItem __dtor = klass->functionMap ().value ("~A");
    QVERIFY (__dtor);
    QVERIFY (__dtor->isVirtual ());
}

void tst_Binder::castOperator()
{
    FileModelItem item = parse ("class A { operator QString (); };\n");

    ClassModelItem klass = item->findClass ("A");
    QVERIFY (klass);

    QCOMPARE (klass->functions ().count (), 1);
    FunctionModelItem __cast = klass->functions().at (0);
    QVERIFY (__cast);
    QCOMPARE (__cast->name(), QString("operator QString"));
}

void tst_Binder::pureVirtualDestructor()
{
    FileModelItem item = parse("class A { virtual ~A() = 0; };");

    ClassModelItem klass = item->findClass("A");
    QVERIFY(klass);

    QCOMPARE(klass->functions().count(), 1);
    FunctionModelItem func = klass->functions().at(0);
    QVERIFY(func->isVirtual());
    QVERIFY(!func->isInline());
    QVERIFY(func->isAbstract());
}

void tst_Binder::signalsSlots()
{
    FileModelItem item = parse("class A { void m(); signals: void signal1(); public slots: void slot1(); void slot2() {} public: void m2(); };");

    ClassModelItem klass = item->findClass("A");
    QVERIFY(klass);

    QCOMPARE(klass->functions().count(), 5);

    FunctionModelItem m = klass->functionMap().value("m");
    QVERIFY(m);
    QVERIFY(m->functionType() == CodeModel::Normal);
    QVERIFY(m->accessPolicy() == CodeModel::Private);

    FunctionModelItem signal1 = klass->functionMap().value("signal1");
    QVERIFY(signal1);
    QVERIFY(signal1->functionType() == CodeModel::Signal);
    QVERIFY(signal1->accessPolicy() == CodeModel::Protected);

    FunctionModelItem slot1 = klass->functionMap().value("slot1");
    QVERIFY(slot1);
    QVERIFY(slot1->functionType() == CodeModel::Slot);
    QVERIFY(slot1->accessPolicy() == CodeModel::Public);

    FunctionModelItem slot2 = klass->functionMap().value("slot2");
    QVERIFY(slot2);
    QVERIFY(slot2->functionType() == CodeModel::Slot);
    QVERIFY(slot2->accessPolicy() == CodeModel::Public);

    FunctionModelItem m2 = klass->functionMap().value("m2");
    QVERIFY(m2);
    QVERIFY(m2->functionType() == CodeModel::Normal);
    QVERIFY(m2->accessPolicy() == CodeModel::Public);
}

void tst_Binder::arrays ()
{
    FileModelItem item = parse ("class A { void fun (int x[8]); };");

    ClassModelItem klass = item->findClass ("A");

    QVERIFY (klass);
    QCOMPARE (klass->functions ().count (), 1);

    FunctionModelItem fun = klass->functionMap ().value ("fun");

    QVERIFY (fun);
    QCOMPARE (fun->arguments ().count (), 1);

    TypeInfo tp = fun->arguments ().at (0)->type ();
    QCOMPARE (tp.arrayElements ().count (), 1);
    QCOMPARE (tp.arrayElements ().at (0), QString ("8"));
    QCOMPARE (tp.toString (), QString ("int[8]"));
}

void tst_Binder::impldecl()
{
    FileModelItem item = parse ("class QStringList { "
                                "QStringList (const QList<QString> &elts); "
                                "QStringList (const QStringList &other); "
                                "}; "
                                "QStringList::QStringList (const QList<QString> &elts) {} "
                                "QStringList::QStringList (const QStringList &other) {} ");

    ClassModelItem klass = item->classMap ().value ("QStringList");
    QVERIFY (klass);

    QCOMPARE (klass->functions ().count (), 2);
}

void tst_Binder::testDefArg()
{
    FileModelItem item = parse ("class Zoo { "
                                "public:"
                                "void foo (const QRectF &zz = QRectF(0.4343431, 0.4434333, 4, 5));"
                                "};");

    ClassModelItem klass = item->classMap ().value ("Zoo");
    QVERIFY (klass);

    QCOMPARE (klass->functions ().count (), 1);
    FunctionModelItem foo = klass->functions ().at (0);
    QCOMPARE (foo->arguments ().count (), 1);
    ArgumentModelItem arg = foo->arguments ().at (0);
    QVERIFY (arg->type ().isConstant ());
    QVERIFY (arg->type ().isReference ());
}

void tst_Binder::testDefArg2()
{
    FileModelItem item = parse ("class Zoo { "
                                "public:"
                                "void foo ( const char * input, int size, ConverterState * state = 0 ) const {}"
                                "};");
    QVERIFY(item);

    ClassModelItem klass = item->classMap ().value ("Zoo");
    QVERIFY (klass);

    QCOMPARE (klass->functions ().count (), 1);
    FunctionModelItem foo = klass->functions ().at (0);
    QCOMPARE (foo->arguments ().count (), 3);
    ArgumentModelItem arg = foo->arguments ().at (2);
    QVERIFY (arg->type ().indirections () == 1);
    QVERIFY (arg->defaultValue ());
    QCOMPARE (arg->defaultValueExpression (), QString("0"));
}


QTEST_MAIN(tst_Binder)
#include "tst_binder.moc"
