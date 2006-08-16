#include <QtCore/QtCore>

#include "ast.h"
#include "tokens.h"
#include "lexer.h"
#include "parser.h"
#include "control.h"
#include "binder.h"
#include "codemodel.h"
#include "rpp/preprocessor.h"

#include <stdio.h>

QString headerPath;
QString headerPrefix;
QString forwardHeaderName;

FileModelItem dom;
QHash<QString, QString> headers;

template <typename T>
bool itemSortHelper(T i1, T i2)
{
    return i1->creationId() < i2->creationId();
}

// returns items by creation id
static QVector<CodeModelItem> sortedItems(const ScopeModelItem &item)
{
    QVector<CodeModelItem> items;
    QHash<QString, ClassModelItem> classMap = item->classMap();
    QHash<QString, EnumModelItem> enumMap = item->enumMap();
    QHash<QString, TypeAliasModelItem> typeAliasMap = item->typeAliasMap();
    QMultiHash<QString, FunctionModelItem> functionMap = item->functionMap();
    QHash<QString, VariableModelItem> variableMap = item->variableMap();

    for (QHash<QString, ClassModelItem>::const_iterator it = classMap.constBegin();
            it != classMap.constEnd(); ++it) {
        items.append(model_static_cast<CodeModelItem>(it.value()));
    }
    for (QHash<QString, EnumModelItem>::const_iterator it = enumMap.constBegin();
            it != enumMap.constEnd(); ++it) {
        items.append(model_static_cast<CodeModelItem>(it.value()));
    }
    for (QHash<QString, TypeAliasModelItem>::const_iterator it = typeAliasMap.constBegin();
            it != typeAliasMap.constEnd(); ++it) {
        items.append(model_static_cast<CodeModelItem>(it.value()));
    }
    for (QMultiHash<QString, FunctionModelItem>::const_iterator it = functionMap.constBegin();
            it != functionMap.constEnd(); ++it) {
        items.append(model_static_cast<CodeModelItem>(it.value()));
    }
    for (QHash<QString, VariableModelItem>::const_iterator it = variableMap.constBegin();
            it != variableMap.constEnd(); ++it) {
        items.append(model_static_cast<CodeModelItem>(it.value()));
    }

    // remove all alien symbols
    int i = 0;
    const QString hdrPrefix = headerPrefix + "/";
    while (i < items.count()) {
        if (!items.at(i)->fileName().contains(hdrPrefix))
            items.remove(i);
        else
            ++i;
    }

    qSort(items.begin(), items.end(), itemSortHelper<CodeModelItem>);

    return items;
}


// TODO - make include paths configurable
static QStringList includePaths()
{
    QString qtDir = QString::fromLocal8Bit(qgetenv("QTDIR"));
    QStringList iPaths;
    iPaths << qtDir + "/include"
           << qtDir + "/include/QtCore"
           << qtDir + "/include/QtGui"
           << qtDir + "/include/QtOpenGL"
           << qtDir + "/include/QtXml"
           << qtDir + "/include/QtNetwork"
           << qtDir + "/include/QtSvg"
           << qtDir + "/include/QtSql";

    return iPaths;
}

static void dumpMacros()
{
    Preprocessor pp;

    pp.addIncludePaths(includePaths());

    pp.processString("#define __cplusplus 1\n"
                     "#define __GNUC__ 3\n"
                     "#define __GNUC_MINOR__ 4\n"
                     "#define __STDC__\n");
    if (headerPath.endsWith("QtSql"))
        pp.processString("#define QT_GUI_LIB\n");
    pp.processFile(headerPath);
    if (headerPath.endsWith("QtGui"))
        // these are not in the pch but still needed
        pp.processString("#include \"qx11embed_x11.h\"\n#include \"qx11info_x11.h\"\n");

    QByteArray contents = pp.result();
    QList<Preprocessor::MacroItem> macros = pp.macros();

    const QString hdrPrefix = headerPrefix + "/";

    foreach(Preprocessor::MacroItem item, macros) {
        if (item.name.startsWith("__") || item.name.endsWith("_H"))
            continue;
        // strip out alien macros
        if (!item.fileName.contains(hdrPrefix))
            continue;

        QString out;
        out += "#define " + item.name;
        if (item.isFunctionLike) {
            out += "(" + item.parameters.join(",") + ")";
            if (!item.definition.isEmpty()) {
               out += " " + item.definition;
            }
        } else if (!item.definition.isEmpty()) {
            out += " " + item.definition;
        }
        out += "\n\n";

        headers[QFileInfo(item.fileName).fileName()] += out;
    }
}

static bool getDom()
{
    Preprocessor pp;

    pp.addIncludePaths(includePaths());

    // TODO - make implicit defines configurable
    pp.processString("#define __cplusplus 1\n"
                     "#define __STDC__\n"
                     "#define Q_DISABLE_COPY(A)\n"
                     "#define Q_CC_GNU\n"
                     "#define Q_WS_X11\n"
                     "#define QT3_SUPPORT\n"
                     "#define QT_NO_STYLE_WINDOWSXP\n"
                     "#define Q_NO_USING_KEYWORD\n"
                     "#define Q_OUTOFLINE_TEMPLATE inline\n");
    if (headerPath.endsWith("QtSql"))
        pp.processString("#define QT_GUI_LIB\n");
    pp.processFile(headerPath);
    if (headerPath.endsWith("QtGui"))
        // these are not in the pch but still needed
        pp.processString("#include \"qx11embed_x11.h\"\n#include \"qx11info_x11.h\"\n");

    QByteArray contents = pp.result();

    Control control;
    Parser p(&control);
    pool pool;

    TranslationUnitAST *ast = p.parse(contents, contents.size(), &pool);
    if (!ast) {
        return false;
    }

    CodeModel model;
    Binder binder(&model, p.location());
    dom = binder.run(ast);

    return p.problemCount() == 0;
}

QString normalizedTypeName(const QString &typeName, const QString &clName)
{
    QString tName = typeName;
    tName.replace(">>", "> >"); // avoid closing templates and >>

    // strip the leading scope if we're in the scope itself
    if (!clName.isEmpty() && tName.startsWith(clName + "::"))
        return tName.mid(clName.length() + 2);
    return tName;
}

QString defaultConstructedValue(const QString &typeName)
{
    QString tName = typeName;
    tName.replace(QRegExp("const(\\W)"), QString("\\1"));
    tName.remove("&");
    tName = tName.simplified();

    if (tName == "QBool")
        return "QBool(false)";
    else if (tName == "QLatin1String")
        return "QLatin1String(\"\")";
    else if (tName == "QTextStreamManipulator")
        return "QTextStreamManipulator(0, 0)";
    else if (tName == "QDebug")
        return "QDebug(QtDebugMsg);";
    else if (tName == "QFontMetrics")
        return "QFontMetrics(QFont())";
    else if (tName == "QFontInfo")
        return "QFontInfo(QFont())";
    else if (tName == "QLayoutIterator")
        return "QLayoutIterator(0)";
    else if (tName == "QTreeWidgetItemIterator")
        return "QTreeWidgetItemIterator(reinterpret_cast<QTreeWidget*>(0))";
    else if (tName.endsWith(" long") ||tName.endsWith("*"))
        return "0";
    else
        return tName + "()";
}

QString dumpArguments(const ArgumentList &args, bool variadics, const QString &clName)
{
    QString out;

    for (int i = 0; i < args.count(); ++i) {
        const ArgumentModelItem arg = args.at(i);
        const QString typeName = arg->type().toString();
        const QString normTypeName = normalizedTypeName(typeName, clName);
        out += normTypeName;
        if (arg->defaultValue()) {
            out += " = " + defaultConstructedValue(typeName);
        }
        out += ", ";
    }
    out.chop(2);
    if (variadics)
        out += "...";
    return out;
}

QString dumpAccessPolicy(CodeModel::AccessPolicy policy)
{
    switch (policy) {
    case CodeModel::Public:
        return "public: ";
    case CodeModel::Private:
        return "private: ";
    case CodeModel::Protected:
        return "protected: ";
    }
    Q_ASSERT(false);
    return QString();
}

QString dumpTemplateArgs(const TemplateParameterList &templateArgs)
{
    QString out;
    if (templateArgs.isEmpty())
        return out;

    out += "template<";
    for (int i = 0; i < templateArgs.count(); ++i) {
        const QString typeName = templateArgs.at(i)->type().toString();
        out += typeName.isEmpty() ? QString("typename") : typeName;
        out += " ";
        out += templateArgs.at(i)->name();
        out += ", ";
    }
    out.chop(2);
    out += ">\n";
    return out;
}

QString typenamed(const QString &typeName)
{
    // TODO: hack to get template types working. need to make it generic
    if (typeName.contains("T>") && typeName.contains("::"))
        return "typename " + typeName;
    return typeName;
}


/* this is a hacky workaround for our parser duplicating some template symbols */
static bool checkDoubleDump(const QString &scope, const QString &signature)
{
    static QHash<QString, QSet<QString> > dumpedMethods;

    QRegExp templateRx("<[\\w,]+>");
    templateRx.setMinimal(true);
    QString hackSignature = signature;
    hackSignature.remove(templateRx);
    QString hackScope = scope;
    hackScope.remove(templateRx);

    if (dumpedMethods.value(hackScope).contains(hackSignature))
        return true;

    dumpedMethods[hackScope] += hackSignature;
    return false;
}

QString dumpFunction(const FunctionModelItem &fit, const ScopeModelItem &smi = ScopeModelItem())
{
    QString out;
    QString clName;

    ClassModelItem cmi = model_dynamic_cast<ClassModelItem>(smi);
    if (cmi)
         clName = cmi->name();

    // only dump toplevel templates
    bool isTemplate = false;
    if (fit->scope().isEmpty() || !clName.contains('<')) {
        out += dumpTemplateArgs(fit->templateParameters());
        isTemplate = !fit->templateParameters().isEmpty();
    }

    bool hasDefinition = model_dynamic_cast<FunctionDefinitionModelItem>(fit);
    bool isInline = fit->isInline();

    if (isInline)
        out += "inline ";
    if (fit->isStatic())
        out += "static ";
    if (fit->isVirtual())
        out += "virtual ";
    if (fit->isExplicit())
        out += "explicit ";

    if (clName.contains('<') || hasDefinition)
        // give templates that are not inline a body
        isInline = true;

    QString retType = fit->type().toString();
    // constructors and destructors don't return anything
    bool noReturn = retType.isEmpty() || clName.left(clName.indexOf('<')) == fit->name()
                    || fit->name().startsWith('~');
    bool isConstructor = noReturn && (!fit->name().startsWith('~'))
                         && (!fit->name().contains("operator"));
    bool isCopyConstructor = isConstructor && fit->arguments().count() == 1 && fit->arguments().at(0)->type().qualifiedName().last() == clName;

    bool returnsReference = retType.endsWith('&');
    bool isCastOperator = fit->name().startsWith("operator ") && fit->name().at(9).isLetter();
    bool isAssignmentOperator = (fit->name() == "operator=");

    if (isCastOperator)
        retType = fit->name().mid(9);
    else if (noReturn)
        retType.clear();
    else
        out += typenamed(normalizedTypeName(retType, clName)) + " ";

    QString signature = fit->name();
    signature += "(";
    signature += dumpArguments(fit->arguments(), fit->isVariadics(), clName);
    signature += ")";
    if (fit->isConstant())
        signature += " const";
    if (fit->isAbstract()) {
        signature += " = 0";
        isInline = false; // hack for a bug in the parser
    }

    if (checkDoubleDump(fit->scope().join("::"), signature))
        return QString();

    out += signature;
    if (isInline && isCopyConstructor && cmi->baseClasses().count() == 1) {
        out += " : " + cmi->baseClasses().at(0) + "(*this) {}";
    } else if (isInline) {
        out += " { ";
        if (!retType.isEmpty() && retType != "void") {
            out += "return ";
            if (retType.endsWith('*')) {
                out += "0; ";
            } else if (isCastOperator) {
                if (retType.endsWith('&'))
                    out += "*new ";
                out += defaultConstructedValue(retType) + "; ";
            } else if (returnsReference) {
                out += "*reinterpret_cast<" + fit->type().qualifiedName().join("::") + " *>(0); ";
            } else if (isAssignmentOperator) {
                out += "*this; ";
            } else {
                QString defValue = defaultConstructedValue(fit->type().qualifiedName().join("::"));
                if (defValue.contains(">::iterator"))
                    defValue.prepend("typename ");
                out += defValue + "; ";
            }
        }
        out += "}";
    } else {
        out += ";";
    }
    out += "\n";

    return out;
}

QString dumpEnum(const EnumModelItem &emi)
{
    QString out;

    out += "enum ";
    if (!emi->name().startsWith("$$"))
        out += emi->name() + " ";
    out += "{ ";

    const EnumeratorList enums = emi->enumerators();
    for (int i = 0; i < enums.count(); ++i) {
        out += enums.at(i)->name() + ",";
    }
    out.chop(1);

    out += " };\n";

    return out;
}

QString dumpBaseClasses(const QStringList &baseClasses)
{
    QString out;

    // TODO - private/protected inheritance
    if (baseClasses.isEmpty())
        return out;
    out += ": ";
    for (int i = 0; i < baseClasses.count(); ++i)
        out += "public " + baseClasses.at(i) + ", ";
    out.chop(2);

    return out;
}

QString dumpClassType(CodeModel::ClassType type)
{
    switch (type) {
    case CodeModel::Class: return "class";
    case CodeModel::Struct: return "struct";
    case CodeModel::Union: return "union";
    }
    Q_ASSERT(false);
    return QString();
}

QString dumpVariable(const VariableModelItem &vmi)
{
    QString out;

    if (vmi->isStatic())
        out += "static ";
    QString typeName = vmi->type().toString();
    if (typeName.contains("T::"))
        out += "typename ";

    out += typeName;
    out += " " + vmi->name();

    if (vmi->scope().isEmpty() && vmi->type().isConstant())
        // give static globals a default value
        out += " = 0";

    out += ";\n";

    return out;
}

QString dumpTypedef(const TypeAliasModelItem &tmi, const QString &clName = QString())
{
    QString out;

    QString td = tmi->type().toString();
    if (td.contains("(*)")) {
        td.replace("(*)", "(*" + tmi->name() + ")");
        out += "typedef " + td + ";\n";
    } else {
        td = normalizedTypeName(td, clName);
        out += "typedef " + typenamed(td) + " " + tmi->name() + ";\n";
    }

    return out;
}

static QString dumpItems(const ScopeModelItem &);

// returns the class Name without the template args
// if it's not a specialization
static QString className(const ClassModelItem &cmi)
{
    QString tArgs;
    for (int i = 0; i < cmi->templateParameters().count(); ++i) {
        tArgs += cmi->templateParameters().at(i)->name() + ",";
    }
    if (!tArgs.isEmpty()) {
        tArgs.chop(1);
        tArgs.prepend('<').append('>');
    }

    QString cName = cmi->name();
    cName.remove(tArgs);

    return cName;
}

QString dumpClass(const ClassModelItem &cmi)
{
    QString out;
    // only dump templates on toplevel classes
    if (cmi->qualifiedName().count() == 1)
        out += dumpTemplateArgs(cmi->templateParameters());
    out += dumpClassType(cmi->classType());
    // no template specializations
    out += " " + className(cmi);

    if (cmi->scope().isEmpty()) {
        // add forward declarations
        headers[forwardHeaderName] += out + ";\n";
        if (cmi->templateParameters().isEmpty())
            headers[forwardHeaderName] += out + "Private;\n";
    }

    out += dumpBaseClasses(cmi->baseClasses());
    out += "\n{\n";

    out += dumpItems(model_static_cast<ScopeModelItem>(cmi));

    out += "};\n";

    return out;
}

bool isWhiteListed(const MemberModelItem &mit)
{
    FunctionModelItem fit = model_dynamic_cast<FunctionModelItem>(mit);
    if (!fit)
        return false;
    return fit->name() == "parent" || fit->name() == "hasChildren" || fit->name() == "columnCount";
}

static QString dumpItem(const ScopeModelItem &smi, const CodeModelItem &item, const QString &clName)
{
    QString out;
    CodeModel::AccessPolicy accessPolicy = CodeModel::Public;
    if (MemberModelItem mit = model_dynamic_cast<MemberModelItem>(item)) {
        accessPolicy = mit->accessPolicy();
        if (accessPolicy == CodeModel::Private && !isWhiteListed(mit))
            return QString();
    }
    if (model_dynamic_cast<ClassModelItem>(smi))
        out += dumpAccessPolicy(accessPolicy);

    if (FunctionModelItem fit = model_dynamic_cast<FunctionModelItem>(item)) {
        if (!fit->isFriend() && !fit->isExtern())
            out += dumpFunction(fit, smi);
    } else if (EnumModelItem eit = model_dynamic_cast<EnumModelItem>(item)) {
        out += dumpEnum(eit);
    } else if (VariableModelItem vit = model_dynamic_cast<VariableModelItem>(item)) {
        out += dumpVariable(vit);
    } else if (TypeAliasModelItem tit = model_dynamic_cast<TypeAliasModelItem>(item)) {
        out += dumpTypedef(tit, clName);
    } else if (ClassModelItem cit = model_dynamic_cast<ClassModelItem>(item)) {
        out += dumpClass(cit);
    }

    return out;
}

QString dumpItems(const ScopeModelItem &smi)
{
    QString out;
    QString clName;

    if (ClassModelItem cmi = model_dynamic_cast<ClassModelItem>(smi))
        clName = cmi->name();
    const QVector<CodeModelItem> children = sortedItems(smi);
    for (int i = 0; i < children.count(); ++i) {
        out += dumpItem(smi, children.at(i), clName);
    }
    return out;
}

// doesn't handle nested namespaces
void dumpNamespace(const NamespaceModelItem &nsi)
{
    // namespaces are special - every child item could end up in a different file
    const QVector<CodeModelItem> children = sortedItems(model_static_cast<ScopeModelItem>(nsi));

    for (int i = 0; i < children.count(); ++i) {
        QString out = dumpItem(model_static_cast<ScopeModelItem>(nsi), children.at(i), nsi->name());
        if (!out.isEmpty()) {
            out.prepend("namespace " + nsi->name() + "\n{ ");
            out.append("}\n");
        }
        headers[QFileInfo(children.at(i)->fileName()).fileName()] += out;
    }
}

void dumpHeaders()
{
    // TODO includes still needed...?
    headers[forwardHeaderName] += "#include <stddef.h>\n#include <stdarg.h>\n\n"
                           "typedef unsigned int uint;\ntypedef unsigned short ushort;\n\n";

    const QList<NamespaceModelItem> namespaces = dom->namespaceMap().values();
    for (int i = 0; i < namespaces.count(); ++i) {
        NamespaceModelItem ns = namespaces.at(i);
        dumpNamespace(ns);
    }

    const QVector<CodeModelItem> items = sortedItems(model_static_cast<ScopeModelItem>(dom));

    for (int i = 0; i < items.count(); ++i) {
        const CodeModelItem item = items.at(i);
        const QString fileName = QFileInfo(item->fileName()).fileName();
        if (ClassModelItem cmi = model_dynamic_cast<ClassModelItem>(item)) {
            QString classDump = dumpClass(cmi) + "\n";
            // hack for QCharRef and QByteRef to make inlines work
            if (cmi->name() == "QCharRef" || cmi->name() == "QByteRef"
                || cmi->name() == "QBitRef")
                headers[fileName].prepend(classDump);
            else
                headers[fileName].append(classDump);
        } else if (TypeAliasModelItem tmi = model_dynamic_cast<TypeAliasModelItem>(item)) {
            headers[fileName] += dumpTypedef(tmi) + "\n";
        } else if (EnumModelItem eit = model_dynamic_cast<EnumModelItem>(item)) {
            headers[fileName] += dumpEnum(eit) + "\n";
        } else if (FunctionModelItem fit = model_dynamic_cast<FunctionModelItem>(item)) {
            headers[fileName] += dumpFunction(fit) + "\n";
        } else if (VariableModelItem vit = model_dynamic_cast<VariableModelItem>(item)) {
            headers[fileName] += dumpVariable(vit) + "\n";
        }
    }
}

void writeHeaders()
{
    for (QHash<QString, QString>::const_iterator it = headers.constBegin();
         it != headers.constEnd(); ++it) {
        QFile file(headerPrefix + "/" + it.key());
        if (!file.open(QIODevice::WriteOnly))
            qFatal("unable to open file %s", qPrintable(it.key()));

        QString headerDef = it.key().toUpper();
        headerDef.replace(".H", "_H");
        file.write(QString("#ifndef %1\n#define %1\n\n").arg(headerDef).toLatin1());
        if (it.key() != forwardHeaderName)
            file.write(QByteArray("#include \"" + forwardHeaderName.toLatin1() + "\"\n\n"));
        file.write(it.value().toLatin1());
        file.write(QByteArray("#endif\n"));
        file.close();
    }
}

// well, I could also rename this to "addQtHacks"
void addExternals()
{
    if (headers.contains("qstring.h")) {
        QString &src = headers["qstring.h"];
        src.prepend("#include <string>\n\n");
        // hack for returning a forward-declared only QString
        src.replace("public: QString decomposition() const { return QString(); }\n",
                    "public: QString decomposition() const;\n");
    }
    if (headers.contains("qglobal.h")) {
        QString &src = headers["qglobal.h"];
        src.replace("public: inline QFlags(Zero = QFlags<Enum>::Zero()) { }",
                    "public: inline QFlags(Zero = typename QFlags<Enum>::Zero()) { }");
        headers[forwardHeaderName] += "class QAbstractFileEngineIterator;\nclass QPostEventList;\n";
    }
    if (headers.contains("qlist.h")) {
        QString &src = headers["qlist.h"];
        // add a forward declare
        src.replace("public: class iterator", "public: class const_iterator;\n"
                    "public: class iterator");
    }
    if (headers.contains("qvector.h")) {
        QString &src = headers["qvector.h"];

        // remove our outofline templates
        src.remove("public: iterator insert(iterator, int, T const&) { return typename QVector<T>::iterator(); }\n");
    }
    if (headers.contains("qvariant.h")) {
        QString &src = headers["qvariant.h"];

        // move this guy to the bottom because of default-constructed value
        src.remove("template<typename T>\n"
                "inline QVariant qVariantFromValue(T const&) { return QVariant(); }\n");
        src.append("template<typename T>\n"
                "inline QVariant qVariantFromValue(T const&) { return QVariant(); }\n");
    }
    if (headers.contains("qobjectdefs.h")) {
        headers["qobjectdefs.h"].replace("public: QMetaObject d;",
            "struct {\n"
            "const QMetaObject *superdata;\n"
            "const char *stringdata;\n"
            "const uint *data;\n"
            "const QMetaObject **extradata;\n"
            "} d;");
    }
    if (headers.contains("qhash.h")) {
        QString &src = headers["qhash.h"];
        src.replace("public: QHashData* detach_helper(void, int);",
           "QHashData *detach_helper(void (*)(Node *, void *), int);");
        src.replace("public: class iterator",
                "class const_iterator;\npublic: class iterator");
    }
    if (headers.contains("qmap.h")) {
        QString &src = headers["qmap.h"];
        src.prepend("#include <map>\n\n");
        src.replace("public: class iterator", "public: class const_iterator;\n"
                    "public: class iterator");
    }
    if (headers.contains("qlinkedlist.h")) {
        QString &src = headers["qlinkedlist.h"];
        src.replace("public: class iterator", "public: class const_iterator;\n"
                    "public: class iterator");
        src.replace("iterator(QLinkedList<T>::Node*) { }", "iterator(Node*) { }");
    }
    if (headers.contains("qset.h")) {
        QString &src = headers["qset.h"];
        src.replace("public: inline const_iterator(Hash::const_iterator) { }",
                    "public: inline const_iterator(typename Hash::const_iterator) { }");
    }
    if (headers.contains("qsignalmapper.h")) {
        headers[forwardHeaderName] += "class QWidget;\n";
    }
    if (headers.contains("qwindowdefs.h")) {
        QString &src = headers[forwardHeaderName];
        src += "struct QX11InfoData;\n";
        src += "class QTextEngine;\n";
        src += "struct QFileDialogArgs;\n";
        src += "struct _XDisplay;\ntypedef struct _XDisplay Display;\n"; // HACK...?
        src += "union _XEvent;\n";
        src += "struct _XGC;\n";
        src += "struct _XRegion;\n";
    }
    if (headers.contains("qevent.h")) {
        QString &src = headers["qevent.h"];
        src.replace("public: inline explicit QKeyEvent(QEvent::Type, int, int, int, QString const& = QString(), bool = bool(), ushort = ushort()) { }\n",
                "public: inline explicit QKeyEvent(QEvent::Type, int, int, int, QString const& = QString(), bool = bool(), ushort = ushort()): QInputEvent(QEvent::None) { }\n");
    }
    if (headers.contains("qaccessible.h")) {
        QString &src = headers["qaccessible.h"];
        src.replace("public: inline QAccessibleEvent(QEvent::Type, int) { }",
                    "public: inline QAccessibleEvent(QEvent::Type, int): QEvent(QEvent::None) { }");
    }
    if (headers.contains("qpalette.h")) {
        QString &src = headers["qpalette.h"];
        src.replace("public: inline QColorGroup normal() const { return QColorGroup(); }",
                "public: inline QColorGroup normal() const;");
        src.replace("public: inline QColorGroup active() const { return QColorGroup(); }",
                "public: inline QColorGroup active() const;");
        src.replace("public: inline QColorGroup disabled() const { return QColorGroup(); }",
                "public: inline QColorGroup disabled() const;");
        src.replace("public: inline QColorGroup inactive() const { return QColorGroup(); }",
                "public: inline QColorGroup inactive() const;");

        src.append("inline QColorGroup QPalette::normal() const { return QColorGroup(); }\n");
        src.append("inline QColorGroup QPalette::active() const { return QColorGroup(); }\n");
        src.append("inline QColorGroup QPalette::disabled() const { return QColorGroup(); }\n");
        src.append("inline QColorGroup QPalette::inactive() const { return QColorGroup(); }\n");
    }
    if (headers.contains("qpixmap.h")) {
        QString &src = headers["qpixmap.h"];
        src.replace("public: QPixmap(char const*);",
                    "public: QPixmap(char const* const[]);");
    }
    if (headers.contains("qimage.h")) {
        QString &src = headers["qimage.h"];
        src.replace("public: explicit QImage(char const*);",
                    "public: explicit QImage(char const* const[]);");
    }
    if (headers.contains("qhostaddress.h"))
        headers[forwardHeaderName] += "struct sockaddr;\n";
}

void addIncludes()
{
    extern QHash<QString, QStringList> includedFiles;

    const QString hdrPrefix = headerPrefix + "/";

    for (QHash<QString, QStringList>::const_iterator it = includedFiles.constBegin();
         it != includedFiles.constEnd(); ++it) {

        if (!it.key().contains(hdrPrefix))
            // not our header
            continue;

        QString out;
        const QStringList includes = it.value();

        foreach (QString inc, includes) {
            out += "#include ";
            if (inc.startsWith("/")) {
                out += '"' + QFileInfo(inc).fileName() + '"';
            } else {
                out += '<' + inc + '>';
            }
            out += "\n";
        }
        out += "\n";
        headers[QFileInfo(it.key()).fileName()].prepend(out);
    }
}

void postprocess()
{
    if (headers.contains("qlayout.h")) {
        QString &src = headers["qlayout.h"];
        src.remove("#include \"qboxlayout.h\"n");
        src.remove("#include \"qgridlayout.h\"n");
        src.append("#include \"qboxlayout.h\"n");
        src.append("#include \"qgridlayout.h\"n");
    }
}

int main(int argc, char *argv[])
{
    if (argc != 3) {
        printf("Usage: lsbhdr Header Prefix\n");
        return 1;
    }

    headerPath = QString::fromLocal8Bit(argv[1]);
    if (!QFile::exists(headerPath))
        qFatal("File %s doesn't exist", argv[1]);

    headerPrefix = QString::fromLocal8Bit(argv[2]);
    forwardHeaderName = headerPrefix + "_fwd.h";

    if (!getDom())
        qFatal("Parser reported problems");

    dumpMacros();
    dumpHeaders();
    addExternals();
    addIncludes();
    postprocess();
    writeHeaders();

    return 0;
}

