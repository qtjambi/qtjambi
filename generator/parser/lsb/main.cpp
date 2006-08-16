/* This file is part of the KDE project
   Copyright (C) 2005 Harald Fernengel <harald@trolltech.com>

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; see the file COPYING.  If not, write to
   the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA 02110-1301, USA.
*/

#include <QtCore/QtCore>
#include <QtSql/QtSql>
#include "qsqlfetchvalue.h"

#include "ast.h"
#include "tokens.h"
#include "lexer.h"
#include "parser.h"
#include "control.h"
#include "binder.h"
#include "codemodel.h"
#include "rpp/preprocessor.h"

#include "lsbdb.h"

#include <stdio.h>

LsbDb lsbDb;

QString libraryName;
QString libraryPath;
QString headerPath;
QDir headerDir;

QHash<QString, QString> typedefs;

// unmangled 2 mangled symbol names
QMultiHash<QString, QString> symbols;
QStringList readOnlyData;
QStringList functions;
QStringList allEnums;
QStringList allTypeInfos;
QStringList allThunks;


QHash<QString, QString> flagTypeCache;
QHash<QString, QString> premangledNames;

QHash<QString, QString> baseVTables;

struct VTableInfo
{
    QStringList lines;
    QStringList functions() const;
    int numEntries;
};

QHash<QString, VTableInfo> vTables;

QList<QPair<int, QStringList> > classVTables;

QHash<QString, int> symbolSizes;

FileModelItem dom;

static void getSymbolSizes()
{
    QProcess p;
    QStringList args;
    args << "-D" << "-S" << libraryPath;

    p.start("nm", args);
    if (!p.waitForFinished() || p.exitCode() != 0)
        qFatal("nm failed");

    QStringList out = QString::fromLocal8Bit(p.readAllStandardOutput()).split("\n");
    QRegExp re("^[0-f]+ ([0-f]+) \\w (\\w+)$");

    foreach (QString line, out) {
        if (re.exactMatch(line)) {
            bool isOk = false;
            symbolSizes[re.cap(2)] = re.cap(1).toInt(&isOk, 16);
            Q_ASSERT(isOk);
        }
    }
}

static QStringList extractSyms(const QString &out, bool mangled)
{
    int pos = 0;
    QStringList res;
    QRegExp rx("[0-9a-f]* (\\w) (.*)\n");
    rx.setMinimal(true);

    while ((pos = rx.indexIn(out, pos)) != -1) {
        const QString symName = rx.cap(2);
        if (mangled) {
            const QString section = rx.cap(1);
            if (section == QLatin1String("R"))
                readOnlyData.append(rx.cap(2));
            else if (section == QLatin1String("T"))
                functions.append(rx.cap(2));
            if (section != QLatin1String("U")) {
                if ((symName.startsWith(QLatin1String("_ZTI"))
                    || symName.startsWith(QLatin1String("_ZTV"))))
                    // typeinfos, vtables
                    allTypeInfos.append(symName);
                else if (symName.startsWith(QLatin1String("_ZThn")))
                    allThunks.append(symName);
            }
        }
        res.append(symName);
        pos += rx.matchedLength();
    }

    return res;
}

static void getSymbolHash()
{
    QProcess p;
    QStringList args;
    args << "-D" << libraryPath;
    p.start("nm", args);
    if (!p.waitForFinished() || p.exitCode() != 0)
        qFatal("nm failed");

    QStringList msyms = extractSyms(QString::fromLocal8Bit(p.readAllStandardOutput()),
            true);

    args.prepend("-C");
    p.start("nm", args);
    if (!p.waitForFinished() || p.exitCode() != 0)
        qFatal("nm failed");

    QStringList syms = extractSyms(QString::fromLocal8Bit(p.readAllStandardOutput()), false);

    Q_ASSERT(syms.count() == msyms.count());
    for (int i = 0; i < syms.count(); ++i)
        symbols.insert(syms.at(i), msyms.at(i));

    getSymbolSizes();
}

static QString getSOName()
{
    QProcess proc;
    proc.start("readelf", QStringList() << "-d" << libraryPath);
    if (!proc.waitForFinished() || proc.exitCode() != 0)
        qFatal("readelf failed");

    QString out = QString::fromLocal8Bit(proc.readAllStandardOutput());

    QRegExp re("Library soname:\\s+\\[(.*)\\]");
    re.setMinimal(true);
    re.indexIn(out);
    return re.cap(1);
}

static bool parseArgs(int argc, char *argv[])
{
    if (argc != 4)
        return false;
    libraryName = QString::fromLocal8Bit(argv[1]);
    libraryPath = QString::fromLocal8Bit(argv[2]);
    headerPath = QString::fromLocal8Bit(argv[3]);
    headerDir = QDir(QFileInfo(headerPath).canonicalPath() + "/..");

    return !libraryName.isEmpty() && !libraryPath.isEmpty() && !headerPath.isEmpty();
}

static void showHelp(char *appName)
{
    printf("Usage:\n");
    printf("    %s name library header\n", appName);
}

static void addLibrary()
{
    int lId = lsbDb.libraryId(libraryName);
    if (lId) {
        lsbDb.setCurrentLibraryId(lId);
        int glId = lsbDb.libraryGroupId(lId);
        if (!glId)
            qFatal("Unable to figure out library group id for library %d", lId);
        lsbDb.setCurrentLibraryGroupId(glId);
    } else {
        lsbDb.addLibrary(libraryName, getSOName());
    }

    int sId = lsbDb.standardId(libraryName);
    if (sId)
        lsbDb.setCurrentStandardId(sId);
    else
        qFatal("No standard for '%s' found, please update the 'Standard' db table", qPrintable(libraryName));
}

// TODO - make include paths configurable
static QStringList includePaths()
{
    QString qtDir = QString::fromLocal8Bit(qgetenv("QTDIR"));
    QStringList iPaths;
    iPaths << qtDir + "/include" << qtDir + "/include/QtCore" << qtDir + "/include/QtGui"
           << qtDir + "/include/QtNetwork" << qtDir + "/include/QtXml"
           << qtDir + "/include/QtOpenGL" << qtDir + "/include/QtSql"
           << qtDir + "/include/QtSvg";

    return iPaths;
}

static bool getDom()
{
    Preprocessor pp;

    pp.addIncludePaths(includePaths());

    // TODO - make implicit defines a resource
//    pp.processFile("parser/rpp/pp-qt-configuration");
    pp.processString("#define __cplusplus 1\n"
                     "#define __STDC__\n"
                     "#define Q_DISABLE_COPY(A)\n"
                     "#define Q_CC_GNU\n"
                     "#define Q_WS_X11\n"
                     "#define QT3_SUPPORT\n"
                     "#define QT_NO_STYLE_WINDOWSXP\n");
    pp.processFile(headerPath);

    QByteArray contents = pp.result();

//    qDebug() << pp.macroNames();

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

static QString unTypedef(const TypeInfo &ti)
{
    TypeInfo resolved = TypeInfo::resolveType(ti, dom->toItem());
    QString name = resolved.toString();

//    qDebug() << "untypedef for" << ti.toString() << resolved.toString() << ti.qualifiedName() << resolved.qualifiedName();

    // va_list is mangled to char*
    if (name == QLatin1String("va_list"))
        return "char*";
    else if (name == QLatin1String("FILE*"))
        return "_IO_FILE*";
    else if (name == QLatin1String("GLint"))
        return "int";
    else if (name == QLatin1String("GLenum") || name == QLatin1String("GLuint"))
        return "unsigned int";
    /* singed int -> int and friends, TODO - skip "char" in a meaningful way */
    else if (name.startsWith("signed ") && !name.contains("char"))
        return name.mid(7);
    else if (name.contains("<qreal>"))
        name.replace("<qreal>", "<double>");

    if (name.startsWith("QFlag"))
        name = flagTypeCache.value(name, name);

    // special hack for templates, our parser has some own naming conventions
    if (name.contains("<")) {
        name.replace(">>", "> >");
        name.replace(QRegExp(",(\\w)"), QString(", \\1"));
    }

    // passing by value ignores the "const" modifier
    if (name.endsWith(" const"))
        name.chop(6);
    return name;
}


// some manual translations that the parser doesn't do
static void translateSignature(QString &symName)
{
    static QHash<QString, QString> trans;
    if (trans.isEmpty()) {
        trans["QThreadStorageData::QThreadStorageData(void)"] = "QThreadStorageData::QThreadStorageData(void (*)(void*))";
        trans["QHashData::detach_helper(void, int)"] = "QHashData::detach_helper(void (*)(QHashData::Node*, void*), int)";
        trans["QMapData::node_create(QMapData::Node*, int)"] = "QMapData::node_create(QMapData::Node**, int)";
        trans["QMapData::node_delete(QMapData::Node*, int, QMapData::Node*)"] = "QMapData::node_delete(QMapData::Node**, int, QMapData::Node*)";
        trans["QSettings::registerFormat(QString const&, bool (*)(QIODevice&, SettingsMap&), bool (*)(QIODevice&, SettingsMap const&), Qt::CaseSensitivity)"] = "QSettings::registerFormat(QString const&, bool (*)(QIODevice&, QMap<QString, QVariant>&), bool (*)(QIODevice&, QMap<QString, QVariant> const&), Qt::CaseSensitivity)";
        trans["QPersistentModelIndex::operator const QModelIndex&() const"] = "QPersistentModelIndex::operator QModelIndex const&() const";
        trans["QAccessible::installUpdateHandler(void (*)(QObject*, int, Event))"] = "QAccessible::installUpdateHandler(void (*)(QObject*, int, QAccessible::Event))";
        trans["QGradient::setStops(QVector<QGradientStop> const&)"] = "QGradient::setStops(QVector<QPair<double, QColor> > const&)";
        trans["QInputMethodEvent::QInputMethodEvent(QString const&, QList<Attribute> const&)"] = "QInputMethodEvent::QInputMethodEvent(QString const&, QList<QInputMethodEvent::Attribute> const&)";
        trans["QTextLayout::setAdditionalFormats(QList<FormatRange> const&)"] = "QTextLayout::setAdditionalFormats(QList<QTextLayout::FormatRange> const&)";
        trans["QTextLayout::draw(QPainter*, QPointF const&, QVector<FormatRange> const&, QRectF const&) const"] = "QTextLayout::draw(QPainter*, QPointF const&, QVector<QTextLayout::FormatRange> const&, QRectF const&) const";
        trans["QImage::QImage(char const*)"] = "QImage::QImage(char const* const*)";
        trans["QImage::convertToFormat(QImage::Format, QVector<QRgb> const&, QFlags<Qt::ImageConversionFlag>) const"] = "QImage::convertToFormat(QImage::Format, QVector<unsigned int> const&, QFlags<Qt::ImageConversionFlag>) const";
        trans["QImage::setColorTable(QVector<QRgb>)"] = "QImage::setColorTable(QVector<unsigned int>)";
        trans["QPixmap::QPixmap(char const*)"] = "QPixmap::QPixmap(char const* const*)";
    }

    symName = trans.value(symName, symName);
}

/* TODO - typedefs */
static QString functionWithSignature(const FunctionModelItem &item)
{
    QString symName = item->qualifiedName().join("::") + "(";
    ArgumentList list = item->arguments();
    foreach (ArgumentModelItem a, list) {
//        symName.append(unTypedef(a->type().toString()));
        symName.append(unTypedef(a->type()));
        symName.append(", ");
    }
    if (item->isVariadics())
        symName.append("..., ");
    if (!list.isEmpty())
        symName.chop(2);
    symName += ")";

    if (item->isConstant())
        symName += " const";

    translateSignature(symName);

    return symName;
}

static bool isQFlag(const TypeAliasModelItem &titem)
{
    const QStringList qualName = titem->type().qualifiedName();
    return qualName.count() == 1 && qualName.at(0).startsWith("QFlags<")
           && (QRegExp("QFlags<\\w+>").exactMatch(qualName.at(0)));
}

static void getQFlagName(const TypeAliasModelItem &titem, QString &mangledName,
                         QString &unmangledName)
{
    QString enumName = titem->type().qualifiedName().at(0).mid(7);
    enumName.chop(1);

    // QFlags<QIODevice::OpenModeFlag> mangled -> 6QFlagsIN9QIODevice12OpenModeFlagEE

    mangledName = "6QFlagsIN";
    unmangledName = "QFlags<";
    const QStringList scope = titem->scope();
    foreach (QString scopeItem, scope) {
        mangledName += QString::number(scopeItem.length()) + scopeItem;
        unmangledName += scopeItem + "::";
    }
    mangledName += QString::number(enumName.length()) + enumName + "EE";
    unmangledName += enumName + ">";
}

// returns the header relative to the master header
template <typename T>
static QString header(const T &item)
{
    return headerDir.relativeFilePath(item->fileName());
}

static bool itemSortHelper(CodeModelItem i1, CodeModelItem i2)
{
    return i1->creationId() < i2->creationId();
}

static void addToFlagCache(const TypeAliasModelItem &titem)
{
    if (!isQFlag(titem))
        return;

    QString mangledName, unmangledName;
    getQFlagName(titem, mangledName, unmangledName);
    flagTypeCache[titem->type().qualifiedName().at(0)] = unmangledName;
}

static void fillFlagCache(const QVector<CodeModelItem> &items)
{
    if (libraryName != "QtCore") {
        for (int i = 0; i < items.count(); ++i) {
            if (header(items.at(i)).contains("QtCore")) {
                CodeModelItem oitem = items.at(i);
                /* Dump all toplevel and toplevel-classes flags */
                if (TypeAliasModelItem titem = model_dynamic_cast<TypeAliasModelItem>(oitem)) {
                    addToFlagCache(titem);
                } else if (ClassModelItem citem = model_dynamic_cast<ClassModelItem>(oitem)) {
                    foreach(TypeAliasModelItem titem, citem->typeAliasMap().values())
                        addToFlagCache(titem);
                }
            }
        }
    }
}

// hack to remove symbols from other Qt libraries
static void removeAlienSymbols(QVector<CodeModelItem> &items)
{
    if (libraryName == "QtCore")
        return;
    bool removeGui = (libraryName == "QtOpenGL" || libraryName == "QtSvg");
    bool removeXML = (libraryName == "QtSvg");

    int i = 0;
    while (i < items.count()) {
        const QString hdr = header(items.at(i));
        if (hdr.contains("QtCore/")
            || (removeGui && hdr.contains("QtGui/"))
            || (removeXML && hdr.contains("QtXml/")))
            items.remove(i);
        else
            ++i;
    }
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

    fillFlagCache(items);
    removeAlienSymbols(items);

    qSort(items.begin(), items.end(), itemSortHelper);

    return items;
}

QString mangledName(const QStringList &qualName)
{
    if (qualName.count() == 1 && premangledNames.contains(qualName.at(0)))
        return premangledNames.value(qualName.at(0));
    if (qualName.count() <= 1)
        return qualName.value(0);

    QString mName = QLatin1String("N");
    foreach (QString section, qualName) {
        mName += QString::number(section.length());
        mName += section;
    }
    mName += QLatin1Char('E');

    return mName;
}

static QString classTypeName(CodeModel::ClassType type)
{
    switch (type) {
    case CodeModel::Class:
        return QLatin1String("Class");
    case CodeModel::Struct:
        return QLatin1String("Struct");
    case CodeModel::Union:
        return QLatin1String("Union");
    }
    qFatal("Unknown class type %d", type);
    return QString();
}

static bool dumpClass(const ClassModelItem &citem);

static int qDumpClass(const QString &mangledName, const ClassModelItem &citem)
{
    QString symName = mangledName;
    if (citem->qualifiedName().count() == 1 && mangledName == citem->qualifiedName().at(0))
        symName.prepend(QString::number(mangledName.length()));

    int typeInfoId = lsbDb.interfaceId("_ZTI" + symName);
    int vTableId = lsbDb.interfaceId("_ZTV" + symName);

    QStringList vTable;
    if (vTableId) {
        VTableInfo info = vTables.value(citem->qualifiedName().join("::"));
        if (info.lines.isEmpty())
            qWarning() << "Unable to find Vtable for " + citem->qualifiedName().join("::");
        vTable = info.functions();
    }

    QList<int> baseIds;
    for (int i = 0; i < citem->baseClasses().count(); ++i) {
        int parentId = lsbDb.typeId(citem->baseClasses().at(i));
        if (!parentId) {
            ClassModelItem it = dom->classMap().value(citem->baseClasses().at(i));
            if (!it) {
                qWarning() << "Unable to dump baseclass" << citem->baseClasses().at(i);
            } else {
                dumpClass(dom->classMap().value(citem->baseClasses().at(i)));
                parentId = lsbDb.typeId(citem->baseClasses().at(i));
            }
        }
        if (!parentId)
            qWarning("Cannot figure out baseclass '%s' for '%s'",
                     qPrintable(citem->baseClasses().at(i)), qPrintable(mangledName));
        else
            baseIds.append(parentId);
    }

    int cId = lsbDb.addClass(classTypeName(citem->classType()), mangledName, baseIds,
                         vTableId, vTable, typeInfoId,
                         baseVTables.value(citem->qualifiedName().join("::")), header(citem));

    if (cId && !vTable.isEmpty())
        classVTables += QPair<int, QStringList>(cId, vTable);

    return cId;
}

bool dumpClass(const ClassModelItem &citem)
{
    const QString className = mangledName(citem->qualifiedName());
    int cId = lsbDb.typeId(className);

    if (!cId) {
        cId = qDumpClass(className, citem);

//        qDebug() << "Added Class" << className << cId << citem->baseClasses().count();
    }

    if (!cId)
        qFatal("Unable to insert class into db");

    return true;
}

static QString getReturnType(const FunctionModelItem &fitem)
{
    QString retType = mangledName(fitem->type().qualifiedName());
    if (retType.endsWith("&"))
        retType = "void"; // TODO - what to do with references?

    // in case a return type is forward declared, we have to register
    // the class first
    if (!retType.isEmpty()) {
        int retId = lsbDb.getTypeId(retType);
        if (!retId) {
            CodeModelItem it = fitem->model()->findItem(fitem->type().qualifiedName(), model_static_cast<CodeModelItem>(dom));
            if (ClassModelItem cit = model_dynamic_cast<ClassModelItem>(it)) {
                // TODO - pre-register class dumpClass(cit);
            } else {
                qWarning() << "unable to find return type" << fitem->type().toString()
                    << "for" << functionWithSignature(fitem);
            }
        }
    }
    return retType;
}

static bool dumpFunctions(const QString &func, const QString &retType, const QString &hdr,
                          int architecture = 1)
{
    bool isOk = true;
    QList<QString> syms = symbols.values(func);
    foreach(QString sym, syms)
        isOk = isOk && (lsbDb.addFunction(sym, retType, hdr, architecture) != 0);
    return isOk;
}

static bool dumpFunctionForArch(const FunctionModelItem &fitem, int architecture)
{
    const QString origFunc = functionWithSignature(fitem);
    QString func = origFunc;
    const QHash<QString, QString> tdefs = lsbDb.typeDefsForArch(architecture);
    for (QHash<QString, QString>::const_iterator it = tdefs.constBegin();
            it != tdefs.constEnd(); ++it) {

        // TODO - string replacing is not very nice, replace tokens instead
        func.replace(it.key(), it.value());
    }
    if (origFunc == func) {
        qWarning() << "unable to dump function" << origFunc << "for arch" << architecture;
        return false;
    }

    if (!symbols.contains(func)) {
        qWarning() << "unable to find mangled name for" << func << "for arch" << architecture;
        return false;
    }

    QString retType = getReturnType(fitem);
    return dumpFunctions(func, retType, header(fitem), architecture);
}

static bool dumpFunction(const FunctionModelItem &fitem)
{
    QString func = functionWithSignature(fitem);
    if (!symbols.contains(func)) {

        // TODO - other platforms
        if (dumpFunctionForArch(fitem, 2))
            return true;

        qDebug() << "cannot find" << func;
        QString funcWithoutSignature = func.left(func.indexOf('('));
        for (QMultiHash<QString, QString>::const_iterator it = symbols.constBegin();
                it != symbols.constEnd(); ++it) {
            if (it.key().startsWith(funcWithoutSignature))
                qDebug() << " possible overload:" << it.key();
        }
        return false;
    }

    return dumpFunctions(func, getReturnType(fitem), header(fitem));
}

static int handleQFlag(const TypeAliasModelItem &titem)
{
    QString mangledName;
    QString unmangledName;

    getQFlagName(titem, mangledName, unmangledName);

    int tid = lsbDb.addClass("Class", mangledName, QList<int>(), 0, QStringList(),
                             0, false, header(titem));
    flagTypeCache[titem->type().qualifiedName().at(0)] = unmangledName;
    return tid;
}

static void dumpTypedef(const TypeAliasModelItem &titem)
{
    const QStringList qualName = titem->type().qualifiedName();
    int tid = 0;
    if (isQFlag(titem)) {
        tid = handleQFlag(titem);
    } else {
        tid = lsbDb.typeId(mangledName(qualName));
    }
    if (!tid) {
        qWarning() << "cannot resolve type" << titem->type().qualifiedName().join("::")
                   << "for" << titem->name();
        return;
    }
    if (lsbDb.addTypedef(mangledName(titem->qualifiedName()), tid,
                     header(titem))) {
//        qDebug() << "added typedef for" << mangledName(titem->qualifiedName()) << "to" << tid;
    }
}

/* TODO - extend to dump sizeof any type */
static bool dumpVariable(const VariableModelItem &vitem)
{
    QString qualName = vitem->qualifiedName().join("::");
    QString mangName = symbols.value(qualName);
    if (mangName.isEmpty()) {
        qWarning() << "unable to find variable" << qualName;
        return false;
    }
    QString mangTypeName = mangledName(vitem->type().qualifiedName());
    if (mangTypeName == "uint") // small hack
        mangTypeName = "unsigned int";
    else if (mangTypeName == "ushort")
        mangTypeName = "unsigned short";
    if (vitem->type().indirections())
        mangTypeName += " " + QString(vitem->type().indirections(), QLatin1Char('*'));

    return lsbDb.addVariable(mangName, mangTypeName, header(vitem));
}

static void dump(const ScopeModelItem &scopeItem)
{
    QVector<CodeModelItem> items = sortedItems(scopeItem);

    bool isGlobal = !model_dynamic_cast<ClassModelItem>(scopeItem);

    for (int i = 0; i < items.count(); ++i) {

        CodeModelItem item = items.at(i);
        if (ClassModelItem citem = model_dynamic_cast<ClassModelItem>(item)) {
            // skip template classes
            if (citem->name().contains("<") || citem->name() == "QUpdateLaterEvent")
                continue;
            dumpClass(citem);
        } else if (TypeAliasModelItem titem = model_dynamic_cast<TypeAliasModelItem>(item)) {
            dumpTypedef(titem);
        } else if (EnumModelItem eitem = model_dynamic_cast<EnumModelItem>(item)) {
            if (!eitem->name().startsWith("$$")) {
                int id = lsbDb.addEnum(mangledName(eitem->qualifiedName()), header(item));
                if (id) {
                    EnumeratorList enums = eitem->enumerators();
                    foreach(EnumeratorModelItem enumerator, enums) {
                        allEnums += QString::number(id) + " " +
                                    eitem->scope().join("::") + "::" + enumerator->name();
                    }
                    //qDebug() << "Added enum" << eitem->qualifiedName().join("::") << id;
                }
            } else {
                /* TODO - anon enums
                qWarning() << "encountered anonymous enum with the following entries:";
                EnumeratorList enums = eitem->enumerators();
                foreach (EnumeratorModelItem e, enums)
                    qWarning() << " " << e->name();
                    */
            }
        } else if (FunctionModelItem fitem = model_dynamic_cast<FunctionModelItem>(item)) {

            if (!fitem->isInline() && !fitem->isAbstract() && !fitem->isFriend()
                && fitem->accessPolicy() != CodeModel::Private
                && !model_dynamic_cast<FunctionDefinitionModelItem>(fitem)
                && fitem->templateParameters().isEmpty())
                    // inlines, templates, friends and abstracts don't generate symbols
                    dumpFunction(fitem);
            continue; // do not recurse into functions
        } else if (VariableModelItem vitem = model_dynamic_cast<VariableModelItem>(item)) {

            // only dump public variables that generate a symbol, that is non-static globals and
            // static variables in class scope
            if (!vitem->isFriend()
                && ((isGlobal && !vitem->isStatic())
                    || (!isGlobal && vitem->isStatic()
                        && vitem->accessPolicy() != CodeModel::Private)))
                dumpVariable(vitem);
        } else {
            qFatal("Internal error - received unknown ModelItem");
        }

        // recurse
        if (ScopeModelItem sitem = model_dynamic_cast<ScopeModelItem>(item))
            dump(sitem);
    }
}

static void dumpQtNamespace(const NamespaceModelItem &nsi)
{
    NamespaceModelItem qtNs = nsi->namespaceMap().value("Qt");
    Q_ASSERT(qtNs);

    dump(model_static_cast<ScopeModelItem>(qtNs));

    qtNs = nsi->namespaceMap().value("QtPrivate");
    Q_ASSERT(qtNs);

    dump(model_static_cast<ScopeModelItem>(qtNs));

    if (libraryName == "QtSql")
        dump(model_static_cast<ScopeModelItem>(nsi->namespaceMap().value("QSql")));

    if (libraryName == "QtOpenGL")
        dump(model_static_cast<ScopeModelItem>(nsi->namespaceMap().value("QGL")));
}

/* TODO: BIIIG HACK */
void removeQtSymbols()
{
    if (libraryName != "QtCore")
        return;

    QSqlQuery q;
    if (!q.exec("delete from ClassVtab where CVcid in (select CIid from ClassInfo where CIlibg in (select LGid from LibGroup where LGname like 'Qt%'))"))
        qWarning() << "unable to remove Qt ClassVTables" << q.lastError();
    if (!q.exec("delete from ArchClass where ACcid in (select CIid from ClassInfo where CIlibg in (select LGid from LibGroup where LGname like 'Qt%'))"))
        qWarning() << "unable to remove Qt ArchClass" << q.lastError();
    if (!q.exec("delete from BaseTypes where BTcid in (select CIid from ClassInfo where CIlibg in (select LGid from LibGroup where LGname like 'Qt%'))"))
        qWarning() << "unable to remove Qt BaseTypes" << q.lastError();
    if (!q.exec("delete from ClassInfo where CIlibg in (select LGid from LibGroup where LGname like 'Qt%')"))
        qWarning() << "unable to remove Qt ClassInfo" << q.lastError();
    if (!q.exec("delete from ArchType where ATtid in (select Tid from Type where Theadergroup in (select HGid from HeaderGroup where HGheader in (select Hid from Header where Hlib in (select Lid from Library where Lname like '%Qt%'))))"))
        qWarning() << "unable to remove Qt ArchTypes" << q.lastError();
    if (!q.exec("delete from Type where Theadergroup in (select HGid from HeaderGroup where HGheader in (select Hid from Header where Hlib in (select Lid from Library where Lname like '%Qt%')))"))
        qWarning() << "unable to remove Qt types" << q.lastError();
    if (!q.exec("delete from Interface where Istandard in (select Sid from Standard where Sname like '%Qt%')"))
        qWarning() << "unable to remove Qt interfaces" << q.lastError();
    if (!q.exec("delete from HeaderGroup where HGid in (select Hid from Header where Hlib in (select Lid from Library where Lname like '%Qt%'))"))
        qWarning() << "unable to remove HeaderGroups" << q.lastError();

    if (!q.exec("delete from Header where Hlib in (select Lid from Library where Lname like '%Qt%')"))
        qWarning() << "unable to remove Headers" << q.lastError();
}

void addQtStandard()
{
    int modId = lsbDb.moduleId("LSB_Toolkit_Qt");
    if (!modId) {
        modId = lsbDb.addModule("LSB_Toolkit_Qt", "Qt Toolkit module");
        if (!modId)
            qFatal("Unable to add module for Qt");
    }
    lsbDb.setCurrentModuleId(modId);
    if (!lsbDb.standardId("QtCore")) {
        if (!lsbDb.addStandard("QtCore", "Qt 4.1.0 Reference Manual", "QtCore 4.1.0",
                     "http://doc.trolltech.com/4.1/qtcore.html"))
            qFatal("Unable to register QtCore as Standard");
    }
    if (!lsbDb.standardId("QtGui")) {
        if (!lsbDb.addStandard("QtGui", "Qt 4.1.0 Reference Manual", "QtGui 4.1.0",
                    "http://doc.trolltech.com/4.1/qtgui.html"))
            qFatal("Unable to register QtGui as Standard");
    }
    if (!lsbDb.standardId("QtXml")) {
        if (!lsbDb.addStandard("QtXml", "Qt 4.1.0 Reference Manual", "QtXml 4.1.0",
                    "http://doc.trolltech.com/4.1/qtxml.html"))
            qFatal("Unable to register QtXml as Standard");
    }
    if (!lsbDb.standardId("QtNetwork")) {
        if (!lsbDb.addStandard("QtNetwork", "Qt 4.1.0 Reference Manual", "QtNetwork 4.1.0",
                    "http://doc.trolltech.com/4.1/qtnetwork.html"))
            qFatal("Unable to register QtNetwork as Standard");
    }
    if (!lsbDb.standardId("QtOpenGL")) {
        if (!lsbDb.addStandard("QtOpenGL", "Qt 4.1.0 Reference Manual", "QtOpenGL 4.1.0",
                    "http://doc.trolltech.com/4.1/qtopengl.html"))
            qFatal("Unable to register QtOpenGL as Standard");
    }
    if (!lsbDb.standardId("QtSql")) {
        if (!lsbDb.addStandard("QtSql", "Qt 4.1.0 Reference Manual", "QtSql 4.1.0",
                    "http://doc.trolltech.com/4.1/qtsql.html"))
            qFatal("Unable to register QtSql as Standard");
    }
    if (!lsbDb.standardId("QtSvg")) {
        if (!lsbDb.addStandard("QtSvg", "Qt 4.1.0 Reference Manual", "QtSvg 4.1.0",
                    "http://doc.trolltech.com/4.1/qtsvg.html"))
            qFatal("Unable to register QtSvg as Standard");
    }
}

void addTypedef(const QString &name, const QString &type)
{
    int pId = lsbDb.typeId(type);
    if (!pId)
        qFatal("Unable to find '%s' in the database", qPrintable(type));
    lsbDb.addTypedef(name, pId, "QtCore/QtCore");
}

void dumpEnumerators()
{
    QProcess proc;
    QStringList args;
    args << "-xc++";
    foreach (QString inc, includePaths()) {
        args << "-I" << inc;
    }
    args << "-DQT3_SUPPORT";
    args << "-o" << "/tmp/lsbcpp.tmp";
    args << "-";

    proc.start("g++", args);
    if (!proc.waitForStarted())
        qFatal("unable to execute g++");
    proc.write("#define protected public\n");
    proc.write("#define private public\n");
    proc.write("#include \"" + headerPath.toLatin1() + "\"\n");
    proc.write("#include <stdio.h>\nint main(int, char**) {\n");
    foreach (QString enumerator, allEnums) {
        proc.write("printf(\"%s %d\", \"\\n" + enumerator.toLatin1() + "\", "
                + enumerator.mid(enumerator.indexOf(" ")).toLatin1() + ");\n");
    }
    proc.write("return 0;\n}\n");
    proc.closeWriteChannel();

    if (!proc.waitForFinished())
        qFatal("unable to run g++");

    if (!proc.exitCode() == 0) {
        qDebug() << proc.readAllStandardError();
        qFatal("error running g++");
    }

    QProcess child;
    child.start("/tmp/lsbcpp.tmp", QIODevice::ReadOnly);
    if (!child.waitForFinished())
        qFatal("unable to launch child process");

    QStringList results = QString::fromLatin1(child.readAll()).split("\n", QString::SkipEmptyParts);
    foreach (QString result, results) {
        QStringList einfo = result.split(" ");
        Q_ASSERT(einfo.size() == 3);
        int enumId = lsbDb.addEnumValue(einfo.at(1).mid(einfo.at(1).lastIndexOf("::") + 2),
                einfo.at(2).toInt(), einfo.at(0).toInt());
        if (!enumId)
            qFatal("Unable to add enum %s", qPrintable(einfo.at(1)));
    }
}

void dumpQtObjectSizePtr(const QString &symName)
{
    static const QMap<int, int> ptrSize = lsbDb.typeSize(lsbDb.typeId("char *"));

    int mtid = lsbDb.getTypeId(symName);
    Q_ASSERT(mtid);

    for (QMap<int, int>::const_iterator it = ptrSize.constBegin(); it != ptrSize.constEnd(); ++it) {
        if (!lsbDb.typeSize(mtid).contains(it.key()))
            lsbDb.setTypeSize(mtid, it.key(), it.value());
    }
}

void dumpQtObjectSizes()
{
    const QMap<int, int> ptrSize = lsbDb.typeSize(lsbDb.typeId("char *"));
    if (ptrSize.isEmpty()) {
        qWarning() << "unable to figure out the size of a char *";
        return;
    }

    dumpQtObjectSizePtr("QCoreApplication *");
    dumpQtObjectSizePtr("QTextCodec *");
    if (libraryName.contains("QtOpenGL"))
        dumpQtObjectSizePtr("QGLContext *");

    QStringList s;
    s << "QVariant" << "Handler";
    dumpQtObjectSizePtr(mangledName(s) + " *");
}

static void dumpQtMethod(const QString &className, const QString &methodName)
{
    ClassModelItem citem = dom->classMap().value(className);
    if (!citem)
        qFatal("Unable to find class: %s", qPrintable(className));
    QList<FunctionModelItem> funcs = citem->functionMap().values(methodName);
    if (funcs.isEmpty())
        qFatal("Unable to find method: %s", qPrintable(methodName));

    foreach (FunctionModelItem fitem, funcs) {
        if (!fitem || !dumpFunction(fitem))
            qFatal("unable to dump %s::%s", qPrintable(className), qPrintable(methodName));
    }
}

void dumpAdditionalQtSymbols()
{
    if (libraryName == "QtGui") {
        dumpQtMethod("QColor", "invalidate");
        dumpQtMethod("QWidget", "testAttribute_helper");
        dumpQtMethod("QListWidget", "setModel");
        dumpQtMethod("QTableWidget", "setModel");
        dumpQtMethod("QTreeWidget", "setModel");
        dumpQtMethod("QLayout", "setGeometry");
        dumpQtMethod("QMenu", "insertAny");

        dumpQtMethod("QColorDialog", "~QColorDialog");
        dumpQtMethod("QFontDialog", "~QFontDialog");
        dumpQtMethod("QInputDialog", "~QInputDialog");
        dumpQtMethod("QClipboard", "~QClipboard");
        dumpQtMethod("QSessionManager", "~QSessionManager");
        dumpQtMethod("QFontDialog", "eventFilter");

        lsbDb.addFunction("_ZThn8_N24QAbstractPageSetupDialogD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N24QAbstractPageSetupDialogD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N20QAbstractPrintDialogD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N20QAbstractPrintDialogD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N16QPageSetupDialogD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N16QPageSetupDialogD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N9QCheckBoxD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N9QCheckBoxD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N8QSpinBoxD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N8QSpinBoxD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N14QDoubleSpinBoxD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N14QDoubleSpinBoxD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N13QDateTimeEditD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N13QDateTimeEditD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N9QTimeEditD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N9QTimeEditD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N9QDateEditD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N9QDateEditD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N12QProgressBarD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N12QProgressBarD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N12QRadioButtonD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N12QRadioButtonD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N15QSplitterHandleD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N15QSplitterHandleD0Ev", "", "", 1, true);
    }

    if (libraryName == "QtXml") {
        lsbDb.addFunction("_ZThn4_N18QXmlDefaultHandlerD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn4_N18QXmlDefaultHandlerD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N18QXmlDefaultHandlerD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn8_N18QXmlDefaultHandlerD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn12_N18QXmlDefaultHandlerD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn12_N18QXmlDefaultHandlerD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn16_N18QXmlDefaultHandlerD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn16_N18QXmlDefaultHandlerD0Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn20_N18QXmlDefaultHandlerD1Ev", "", "", 1, true);
        lsbDb.addFunction("_ZThn20_N18QXmlDefaultHandlerD0Ev", "", "", 1, true);
    }

    if (libraryName != "QtCore")
        return;

    // these are virtuals that are reimplemented private
    dumpQtMethod("QAbstractListModel", "parent");
    dumpQtMethod("QAbstractListModel", "hasChildren");
    dumpQtMethod("QAbstractListModel", "columnCount");
    dumpQtMethod("QAbstractTableModel", "parent");
    dumpQtMethod("QAbstractTableModel", "hasChildren");
    dumpQtMethod("QTextCodecPlugin", "keys");
    dumpQtMethod("QTextCodecPlugin", "create");

    // private symbols called from inlines
    dumpQtMethod("QString", "free");
    dumpQtMethod("QString", "expand");
    dumpQtMethod("QString", "realloc");
    dumpQtMethod("QString", "fromLatin1_helper");
    dumpQtMethod("QString", "multiArg");
    dumpQtMethod("QByteArray", "realloc");
    dumpQtMethod("QByteArray", "expand");

    ClassModelItem qStringItem = dom->classMap().value("QString");
    Q_ASSERT(qStringItem);

    VariableModelItem vitem = qStringItem->variableMap().value("shared_null");
    if (!vitem || !dumpVariable(vitem))
        qFatal("Unable to dump QString::shared_null");

    vitem = qStringItem->variableMap().value("codecForCStrings");
    if (!vitem || !dumpVariable(vitem))
        qFatal("Unable to dump QString::codecForCStrings");

    ClassModelItem qByteArrayItem = dom->classMap().value("QByteArray");
    Q_ASSERT(qByteArrayItem);

    vitem = qByteArrayItem->variableMap().value("shared_null");
    if (!vitem || !dumpVariable(vitem))
        qFatal("Unable to dump QByteArray::shared_null");

    vitem = dom->classMap().value("QCoreApplication")->variableMap().value("self");
    if (!vitem || !dumpVariable(vitem))
        qFatal("Unable to dump QCoreApplication::self");


    // these are symbols in private headers or in .cpp files (which we don't parse)
    // but they are extern'd and referenced from the outside world

    if (!lsbDb.addFunction("_Z21qRegisterResourceDataiPKhS0_S0_", "bool", "", 1))
        qFatal("Unable to dump qRegisterResourceData()");

    if (!lsbDb.addFunction("_Z23qUnregisterResourceDataiPKhS0_S0_", "bool", "", 1))
        qFatal("Unable to dump qUnregisterResourceData()");

    if (!lsbDb.addFunction("_Z32qt_register_signal_spy_callbacksRK21QSignalSpyCallbackSet", "void", "", 1))
        qFatal("Unable to dump qt_register_signal_spy_callbacks");
    if (!lsbDb.addFunction("_ZN14QUnicodeTables5lowerEj", "int", "", 1))
        qFatal("Unable to dump QUnicodeTables::lower()");
    if (!lsbDb.addFunction("_Z37qRegisterStaticPluginInstanceFunctionPFP7QObjectvE", "void", "", 1))
        qFatal("Unable to dump qRegisterStaticPluginInstanceFunction()");
}

static void dumpTemplateClass(const QString &mangledName, const QString &unmangledName,
                              const ClassModelItem &cit)
{
    premangledNames[unmangledName] = mangledName;

    if (libraryName != "QtCore")
        return;

    if (!qDumpClass(mangledName, cit))
        qFatal("Unable to dump class: %s", qPrintable(unmangledName));
}

void dumpQtTemplateInstanciations()
{
    ClassModelItem cit = dom->classMap().value("QList<T>");
    Q_ASSERT(cit);

    dumpTemplateClass("N5QListI10QByteArrayEE", "QList<QByteArray>", cit);
    dumpTemplateClass("N5QListI9QFileInfoEE", "QList<QFileInfo>", cit);
    dumpTemplateClass("N5QListI8QVariantEE", "QList<QVariant>", cit);
    dumpTemplateClass("N5QListI4QUrlEE", "QList<QUrl>", cit);
    dumpTemplateClass("N5QListI11QModelIndexEE", "QList<QModelIndex>", cit);
    dumpTemplateClass("N5QListI12QHostAddressEE", "QList<QHostAddress>", cit);
    dumpTemplateClass("N5QListIiEE", "QList<int>", cit);
    dumpTemplateClass("N5QListIP7QObjectEE", "QList<QObject*>", cit);
    dumpTemplateClass("N5QListIP7QWidgetEE", "QList<QWidget*>", cit);
    dumpTemplateClass("N5QListI5QPairI7QStringS1_EEE", "QList<QPair<QString,QString> >", cit);

    cit = dom->classMap().value("QVector<T>");
    Q_ASSERT(cit);

    dumpTemplateClass("N7QVectorI8QVariantEE", "QVector<QVariant>", cit);

    cit = dom->classMap().value("QMap<Key,T>");
    Q_ASSERT(cit);

    dumpTemplateClass("N4QMapIi8QVariantEE", "QMap<int,QVariant>", cit);
    dumpTemplateClass("N4QMapI7QString8QVariantEE", "QMap<QString,QVariant>", cit);


    cit = dom->classMap().value("QPair<T1,T2>");
    Q_ASSERT(cit);

    dumpTemplateClass("N5QPairIiiEE", "QPair<int,int>", cit);


    // small hack for the different spacing of our parser
    premangledNames["QList<QPair<QString,QString>>"] = "N5QListI5QPairI7QStringS1_EEE";
}

void dumpTypeInfo()
{
    foreach (QString typeInfo, allTypeInfos)
        lsbDb.addTypeInfo(typeInfo, true);

    foreach (QString thunk, allThunks)
        lsbDb.addTypeInfo(thunk, false);
}

void getVTables()
{
    QProcess proc;
    QStringList args;
    args << "-xc++";
    foreach (QString inc, includePaths()) {
        args << "-I" << inc;
    }
    args << "-DQT3_SUPPORT";
    args << "-o" << "/tmp/lsbvtable.tmp";
    args << "-fdump-class-hierarchy";
    args << "-";

    proc.start("g++", args);
    if (!proc.waitForStarted())
        qFatal("unable to execute g++");
    proc.write("#include \"" + headerPath.toLatin1() + "\"\n");
    proc.write("int main(int, char**) {\nreturn 0;\n}\n");
    proc.closeWriteChannel();

    if (!proc.waitForFinished())
        qFatal("unable to run g++");

    if (!proc.exitCode() == 0) {
        qDebug() << proc.readAllStandardError();
        qFatal("error running g++");
    }

    QFile f("-.t01.class");
    if (!f.open(QIODevice::ReadOnly))
        qFatal("Unable to get vtables");

    QStringList blocks = QString::fromLatin1(f.readAll()).split("\n\n");
    f.close();

    foreach (QString block, blocks) {
        if (block.startsWith("Vtable for ")) {
            VTableInfo info;
            QStringList lines = block.split("\n");

            QRegExp re("(\\d+)u entries");
            if (re.indexIn(lines.at(1)) == -1)
                qFatal("cannot parse vtable entry: %s", qPrintable(lines.join("\n")));

            info.numEntries = re.cap(1).toInt();
            info.lines = lines;
            vTables[lines.at(0).mid(11)] = info;
        }
    }

    QFile::remove("/tmp/lsbvtable.tmp");
    QFile::remove("-.t01.class");
}

#if 0
static void stripNonVirtual(QList<FunctionModelItem> &funcs)
{
    int funcIdx = 0;
    while (funcIdx < funcs.count()) {
        if (funcs.at(funcIdx)->isVirtual())
            ++funcIdx;
        else
            funcs.removeAt(funcIdx);
    }
}
#endif

// swap const around, not template-safe
QString normalizedVTableEntry(const QString &entry)
{
    QRegExp re("\\bconst\\b\\s+(\\w+)");
    QString sym = entry;
    sym.replace(re, "\\1 const");

    return sym;
}

// not namespace safe
static QString addDestructor(const QString &className, const ScopeModelItem &scope)
{
    const QString destr0 = "_ZN" + QString::number(className.length()) + className + "D0Ev";
    const QString destr1 = "_ZN" + QString::number(className.length()) + className + "D1Ev";

    if (!lsbDb.addFunction(destr0, "", header(scope), 1, true))
        qFatal("Unable to add destructor %s", qPrintable(destr0));
    if (!lsbDb.addFunction(destr1, "", header(scope), 1, true))
        qFatal("Unable to add destructor %s", qPrintable(destr1));

    symbols.insert(className + "::~" + className + "()", destr0);
    symbols.insert(className + "::~" + className + "()", destr1);

    return destr1;
}

static QString mangledDestructor(const QString unmangled, int dupe)
{
    Q_ASSERT(dupe == 0 || dupe == 1); // dupe must be 0 or 1
    QList<QString> destrs = symbols.values(unmangled);
    foreach(QString destr, destrs) {
        if (destr.endsWith("D" + QString::number(1 - dupe) + "Ev"))
            return destr;
    }
    return QString();
}

QStringList VTableInfo::functions() const
{
    QStringList res;
    QRegExp funcRx("\\d+\\s+(.*)");
    int i;
    for (i = 2; i < lines.count(); ++i) {
        if (funcRx.indexIn(lines.at(i)) == -1)
            qFatal("cannot parse vtable entry at %d: %s", i, qPrintable(lines.join("\n")));
        QString match = funcRx.cap(1).trimmed();
        if (!match.startsWith("(int (*)(...))"))
            res += match;
    }

    int vDestructorIdx = 0;
    for (i = 0; i < res.count(); ++i) {
        QStringList qualName = res.at(i).left(res.at(i).indexOf('(')).split("::");
        QString funcName = qualName.takeLast();
        QString normalizedSym = normalizedVTableEntry(res.at(i));
        if (funcName == "__cxa_pure_virtual") {
            res[i] = "__cxa_pure_virtual";
            continue;
        } else if (funcName.startsWith("_ZThn")) {
            res[i] = funcName;
            continue;
        } else if (funcName.startsWith("~")) {
            QString destr = mangledDestructor(normalizedSym, vDestructorIdx);
            if (!destr.isEmpty()) {
                res[i] = destr;
                ++vDestructorIdx;
                continue;
            }
            // else fall through and add it later
        } else if (symbols.contains(normalizedSym)) {
            res[i] = symbols.value(normalizedSym);
            continue;
        }

        ScopeModelItem scope = model_dynamic_cast<ScopeModelItem>(
                dom->model()->findItem(qualName, model_static_cast<CodeModelItem>(dom)));
        if (!scope) {
            qWarning() << "unable to find scope for" << res.at(i);
            continue;
        }

        QList<FunctionModelItem> funcs = scope->functionMap().values(funcName.left(
                    funcName.indexOf('(')));
 //       stripNonVirtual(funcs);

        if (funcs.isEmpty()) {
            if (funcName.startsWith('~')) {
                // add the implicit destructors
                res[i] = addDestructor(funcName.left(funcName.indexOf('(')).mid(1), scope);
                ++vDestructorIdx;
            } else {
                qWarning() << "unable to find function in vtable" << res.at(i) << funcName.left(funcName.indexOf('('));
            }
            continue;
        }

        if (funcs.count() > 1) {
            qWarning() << "overloaded virtual found for" << res.at(i);
            // TODO - handle overloaded virtuals
        }
        QString func = functionWithSignature(funcs.at(0));
        if (!symbols.contains(func)) {
            if (funcName.startsWith('~')) {
                res[i] = addDestructor(funcName.left(funcName.indexOf('(')).mid(1), scope);
                ++vDestructorIdx;
            } else {
                qWarning() << "Unable to find vtable function" << res.at(i) << func;
                res[i] = QString();
            }
        } else if (funcs.at(0)->name().startsWith('~')) {
            res[i] = mangledDestructor(func, vDestructorIdx++);
        } else {
            res[i] = symbols.value(func);
        }
    }

    return res;
}

static void dumpVTables()
{
    for (int i = 0; i < classVTables.count(); ++i) {
        QPair<int, QStringList> entry = classVTables.at(i);
        lsbDb.addVtable(entry.first, entry.second);
    }
}

static void getVTT()
{
    QProcess proc;

    QStringList args;
    args << "-R" << libraryPath;
    proc.start("objdump", args, QIODevice::ReadOnly);

    if (!proc.waitForFinished() || proc.exitCode() != 0)
        qFatal("objdump failed");

    QStringList out = QString::fromLocal8Bit(proc.readAllStandardOutput()).split("\n");
    out.sort();

    QRegExp re(".*(_ZTVN\\d+.*E)$");
    QRegExp symRe("_ZTS\\d+(\\D+\\w*)");
    int idx = out.indexOf(re);
    while (idx > 0) {

        QString line = out.value(idx + 1);
        QString symName = line.mid(line.lastIndexOf(' ') + 1);

        if (symRe.exactMatch(symName))
            baseVTables[symRe.cap(1)] = re.cap(1);

        idx = out.indexOf(re, idx + 1);
    }
}

static void postprocess()
{
    lsbDb.fixVirtualThunks();

    if (libraryName == "QtCore") {
        lsbDb.addArchDependendSizes(lsbDb.typeId("QMetaObject"), 16, 32);
        lsbDb.addArchDependendSizes(lsbDb.typeId("N10QByteArray4DataE"), 20, 32);
        lsbDb.addArchDependendSizes(lsbDb.typeId("N7QString4NullE"), 1, 1);
        lsbDb.addArchDependendSizes(lsbDb.typeId("N7QString4DataE"), 20, 32);
        lsbDb.addArchDependendSizes(lsbDb.typeId("N9QListData4DataE"), 24, 32);
        lsbDb.addArchDependendSizes(lsbDb.typeId("QHashData"), 32, 40);
        lsbDb.addArchDependendSizes(lsbDb.typeId("QMapData"), 72, 128);
        lsbDb.addArchDependendSizes(lsbDb.typeId("QVectorData"), 16, 16);
        lsbDb.addArchDependendSizes(lsbDb.typeId("QLinkedListData"), 20, 32);
    }
}

int main(int argc, char *argv[])
{
    QCoreApplication app(argc, argv);

    if (!parseArgs(argc, argv)) {
        showHelp(argv[0]);
        return 1;
    }

    getVTT();

    if (!getDom())
        qFatal("Parser reported problems");
    getSymbolHash();
    if (!lsbDb.connect() || !lsbDb.initCppIntrinsics())
        qFatal("Unable to initialize DB - exiting");

    // debug only
    removeQtSymbols();

    addQtStandard();

    getVTables();

    addLibrary();
    dumpTypeInfo();
    dumpQtTemplateInstanciations();

    dumpQtNamespace(model_static_cast<NamespaceModelItem>(dom));
    dump(model_static_cast<ScopeModelItem>(dom));
    dumpAdditionalQtSymbols();
    dumpVTables();
    dumpEnumerators();
    dumpQtObjectSizes();

    postprocess();

    lsbDb.disconnect();

    return 0;
}

