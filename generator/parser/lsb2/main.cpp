#include <QtSql/QtSql>

#if 0
gcc -I/home/harald/troll/qt-3.3/include -DQT_NO_STL -c -fdump-class-hierarchy foo.cpp
#endif

static const int cxxStandardId = 87;
static const int standardId = 120;
static const int libraryId = 262;
static const int libGroupId = 492;
static const QString libRoot = "/home/harald/troll/qt-3.3/";
static const QString libName = "/home/harald/troll/qt-3.3/lib/libqt-mt.so";

struct Sym
{
    QString name;
    QString type;
    int size;
};

static QStringList getBlackList();
static QStringList getSymBlackList();

static QHash<QString, Sym> syms;
static QSet<QString> exportedSymbols;
static QHash<QString, QString> reverseSymHash; // mangled 2 unmangled, same as c++filt
static QHash<QString, QString> symHash; // unmangled 2 mangled
static QHash<QString, QStringList> vTables;
static QHash<QString, int> dumpedFunctions;
static QHash<QString, QString> baseVTables;
static QHash<QString, QStringList> baseClasses;
static const QStringList blackList = getBlackList();
static const QStringList symbolBlackList = getSymBlackList();

// architectures - first int is the arch id from the db, second int tells whether its 32 or 64
enum { ArchCount = 6 };
static int archs[ArchCount][2] = { {3, 2}, {6, 1}, {9, 2}, {10, 1}, {11, 2}, {12, 2} };

QStringList getSymBlackList()
{
    QStringList black;

    black << "_ZN12QDragManager13createCursorsEv";
    black << "_ZN16QXmlSimpleReader11stringClearEv";
    black << "_ZN16QXmlSimpleReader24setUndefEntityInAttrHackEb";
    black << "qt_ftp_filename_codec";

    return black;
}

QStringList getBlackList()
{
    QStringList black;

    black << "QSqlExtension" << "QSqlDriverExtension" << "QSqlOpenExtension";
    black << "QSqlCursorManager" << "QSqlFormManager" << "QDataManager";
    black << "QSvgDevice" << "QMutexPool";
    black << "QComponentFactory" << "QComLibrary" << "QPSPrinter";
    black << "QTextStringChar" << "QMemArray<QTextStringChar>" << "QTextString";
    black << "QValueStack<int>" << "QValueStack<QTextParagraph*>" << "QValueStack<bool>";
    black << "QTextCursor" << "QTextCommand" << "QPtrList<QTextCommand>";
    black << "QTextCommandHistory" << "QTextCustomItem" << "QTextImage";
    black << "QTextHorizontalLine" << "QPtrList<QTextCustomItem>" << "QTextFlow";
    black << "QTextTableCell" << "QPtrList<QTextTableCell>" << "QMap<QTextCursor* << int>";
    black << "QTextTable" << "QMap<int << QColor>" << "QMap<int << QTextDocumentSelection>";
    black << "QPtrList<QTextDocument>" << "QTextDocument" << "QTextDeleteCommand";
    black << "QTextInsertCommand" << "QTextFormatCommand" << "QTextStyleCommand";
    black << "QMap<int << QTextParagraphSelection>" << "QMap<int << QTextLineStart*>";
    black << "QTextParagraph" << "QTextFormatter" << "QTextFormatterBreakInWords";
    black << "QTextFormatterBreakWords" << "QTextIndent" << "QTextPreProcessor";
    black << "QTextFormat" << "QDict<QTextFormat>" << "QTextFormatCollection";
    black << "QTextParagraphPseudoDocument" << "QTextItem" << "QTextLayout";
    black << "QSharedDoubleBuffer" << "QMembuf" << "QTitleBar";
    black << "QWidgetResizeHandler" << "QTextParagraphData" << "QSignalVec";
    black << "QPlatinumStyle" << "QSGIStyle" << "QMotifPlusStyle" << "QWindowsStyle"
          << "QMotifStyle" << "QCDEStyle";
    black << "QSqlDriverCreatorBase" << "QAuBucket" << "QAuServer"
          << "QBig5Codec" << "QDnsSocket";


    return black;
}

static void sqlconnect()
{
    QSqlDatabase db = QSqlDatabase::addDatabase("QMYSQL");
    db.setHostName(qgetenv("LSBDBHOST"));
    db.setUserName(qgetenv("LSBUSER"));
    db.setPassword(qgetenv("LSBDBPASSWD"));
    db.setDatabaseName(qgetenv("LSBDB"));
    if (!db.open()) {
        qWarning() << "Unable to open database: " << db.lastError();
        qFatal("bummer");
    }
}

static void setStandard()
{
    QSqlQuery q;
    q.prepare("insert into Standard (Sname, Sfull, Surl, Stype, Sarch, Sshort) "
            "values (?, ?, ?, ?, ?, ?)");
    q.addBindValue("qt-mt");
    q.addBindValue("Qt 3.3.6 Reference Manual");
    q.addBindValue("http://doc.trolltech.com/3.3/index.html");
    q.addBindValue("Standard");
    q.addBindValue(1);
    q.addBindValue("Qt 3.3.6");
    if (!q.exec()) {
        qWarning() << "Unable to add Standard" << q.lastError();
        qFatal("bummer");
    }
    qDebug() << "Standard id" << q.lastInsertId().toInt();

    q.prepare("insert into Library (Lname, Lrunname, Lstd, Larch) values (?, ?, ?, ?)");
    q.addBindValue("libqt-mt");
    q.addBindValue("libqt-mt.so.3");
    q.addBindValue("Yes");
    q.addBindValue("1");
    if (!q.exec()) {
        qWarning() << "Unable to add Library" << q.lastError();
        qFatal("bummer");
    }
    int libId = q.lastInsertId().toInt();
    qDebug() << "Library id" << q.lastInsertId().toInt();

    q.prepare("insert into ModLib (MLmid, MLlid) values (?, ?)");
    q.addBindValue(5); // hardcoded Module_Qt
    q.addBindValue(libId);
    if (!q.exec()) {
        qWarning() << "Unable to add ModLib" << q.lastError();
        qFatal("bummer");
    }

    q.prepare("insert into LibGroup (LGname, LGlib, LGarch, LGorder) values (?, ?, ?, ?)");
    q.addBindValue("Qt");
    q.addBindValue(libId);
    q.addBindValue(1);
    q.addBindValue(1);
    if (!q.exec()) {
        qWarning() << "Unable to add LibGroup" << q.lastError();
        qFatal("bummer");
    }
    qDebug() << "LibGroup id" << q.lastInsertId().toInt();

    q.prepare("insert into ArchLib(ALlid, ALaid, ALrunname) values (?, ?, ?)");
    q.addBindValue(libId);
    q.addBindValue(1);
    q.addBindValue("libqt-mt.so.3");
    if (!q.exec()) {
        qWarning() << "Unable to add ArchLib" << q.lastError();
        qFatal("bummer");
    }
}

static void addLGInt(int iid, int libg = libGroupId)
{
    QSqlQuery q;

    q.prepare("insert into LGInt (LGIint, LGIlibg) values (?, ?)");
    q.addBindValue(iid);
    q.addBindValue(libg);

    if (!q.exec()) {
        qWarning() << "Unable to add to LibGroup" << q.lastError();
        qFatal("bummer");
    }
}

static void addArchInt(int iid, int arch)
{
    QSqlQuery q;

    q.prepare("insert into ArchInt (AIarch, AIint) values (?, ?)");
    q.addBindValue(arch);
    q.addBindValue(iid);
    if (!q.exec())
        qFatal("unable to add to ArchInt: %s", qPrintable(q.lastError().text()));
}

static void addFunction(const QString &name, const QString &type = QLatin1String("Function"),
        const QString &status = QLatin1String("Included"))
{
    QSqlQuery q;
    q.prepare("insert into Interface (Iname, Istatus, Itype, Istandard, Iarch) "
              "values (?, ?, ?, ?, ?)");
    q.addBindValue(name);
    q.addBindValue(status);
    q.addBindValue(type);
    q.addBindValue(standardId);
    q.addBindValue(1);

    if (!q.exec()) {
        qWarning() << "Unable to add Function" << q.lastError();
        qFatal("bummer");
    }

    int iid = q.lastInsertId().toInt();

    addLGInt(iid);

    dumpedFunctions[name] = iid;
}

static void clear()
{
    QSqlQuery q;
    if (!q.exec("delete from LGInt where LGIlibg = " + QString::number(libGroupId)))
        qFatal("clear LGInt - bummer");
    if (!q.exec("delete from Interface where Istandard = " + QString::number(standardId)))
        qFatal("clear - bummer");
    if (!q.exec("delete from Type where Tid > 16245"))
        qFatal("clear - type bummer");
    if (!q.exec("delete from ClassInfo where CIid > 721"))
        qFatal("clear - bummer ClassInfo");
    if (!q.exec("delete from VMIBaseTypes where VBTcid  > 721"))
        qFatal("clear - bummer VMIBaseTypes");
    if (!q.exec("delete from BaseTypes where BTcid  > 721"))
        qFatal("clear - bummer BaseTypes");
    if (!q.exec("delete from ClassVtab where CVcid  > 721"))
        qFatal("clear - bummer ClassVtab");
    if (!q.exec("delete from Vtable where VTcid  > 721"))
        qFatal("clear - bummer Vtable");
}

static void getSyms()
{
    QStringList args;
    args << "-D" << "-S" << libName;

    QProcess p;
    p.start("nm", args);
    if (!p.waitForFinished() || p.exitCode() != 0)
        qFatal("nm 1 failed");

    QStringList out = QString::fromLocal8Bit(p.readAllStandardOutput()).split("\n");
    QRegExp re("^[0-f]+ ([0-f]+) (\\w) (.*)$");

    foreach (QString line, out) {
        if (re.exactMatch(line)) {
            bool isOk = false;
            Sym s;
            s.name = re.cap(3);
            s.type = re.cap(2);
            s.size = re.cap(1).toInt(&isOk, 16);
            Q_ASSERT(isOk);
            if (s.name != QLatin1String("_init") && s.name != QLatin1String("_fini")) {
                syms[s.name] = s;
            }
        }
    }

    args.removeAll("-D");
    p.start("nm", args);
    if (!p.waitForFinished() || p.exitCode() != 0)
        qFatal("nm 2 failed");
    out = QString::fromLocal8Bit(p.readAllStandardOutput()).split("\n");

    args.prepend("-C");
    p.start("nm", args);
    if (!p.waitForFinished() || p.exitCode() != 0)
        qFatal("nm 3 failed");

    QStringList out2 = QString::fromLocal8Bit(p.readAllStandardOutput()).split("\n");
    Q_ASSERT(out.count() == out2.count());

    for (int i = 0; i < out.count(); ++i) {
        if (!re.exactMatch(out.at(i)))
            continue;
        QString mangledSym = re.cap(3);
        if (!re.exactMatch(out2.at(i)))
            qFatal("symbol mismatch");
        symHash[re.cap(3)] = mangledSym;
        reverseSymHash[mangledSym] = re.cap(3);
    }
}

static void getVTT()
{
    QProcess proc;

    QStringList args;
    args << "-R" << libName;
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

static bool isBlackListedSymbol(const QString &mangledSym)
{
    if (symbolBlackList.contains(mangledSym))
        return true;

    QString sym = reverseSymHash.value(mangledSym);
    Q_ASSERT(!sym.isEmpty());

    QString className;
    QRegExp clRe(" for ([\\w<>]+)$");
    if (clRe.indexIn(sym) != -1)
        className = clRe.cap(1);
    else
        className = sym.section("::", 0, 0);

    if (className.isEmpty())
        return false;

    // return true for blacklisted classes and for "vtable for xxx" and friends
    return blackList.contains(className);
}

static void writeSyms()
{
    QStringList symNames = syms.keys();
    for (int i = 0; i < symNames.count(); ++i) {
        const Sym sym = syms.value(symNames.at(i));

        if (isBlackListedSymbol(sym.name))
            continue;

        if (sym.type == QLatin1String("T"))
            addFunction(sym.name);
        else if (sym.type == QLatin1String("D"))
            addFunction(sym.name, QLatin1String("Data"));
        else if (sym.type == QLatin1String("V") &&
                 (sym.name.startsWith("_ZTI") || sym.name.startsWith("_ZTV")))
            addFunction(sym.name, QLatin1String("Data"));
        else if (sym.type == QLatin1String("B"))
            addFunction(sym.name, QLatin1String("Data"));
        else if (sym.type == QLatin1String("W") && sym.name.startsWith("_ZThn"))
            addFunction(sym.name);
    }
}

static QStringList mangledVTable(const QStringList &vtable)
{
    QRegExp vfuncRe("\\d+\\s+(\\S.*)");
    QRegExp thunkRe(".*::(_ZThn.*)\\(.*");
    QRegExp constRe("const ([\\w<>]+)(&|\\*)");
    QRegExp mapRe("const (QMap<\\w+, \\w+>)&");
    QRegExp uintRe("(\\W)uint(\\W)");
    QRegExp widRe("(\\W)WId(\\W)");
    QRegExp templArgRe(" \\[with type = (.*)\\]");
    constRe.setMinimal(true);

    QStringList mangledTable;
    bool firstConstr = true;

    for (int i = 2; i < vtable.count(); ++i) {
        if (!vfuncRe.exactMatch(vtable.at(i)))
            qDebug() << "unknown vtable entry:" << vtable.at(i);
        QString func = vfuncRe.cap(1);
        if (func.startsWith("(int (*)(...))") || func.startsWith("__cxa_pure_virtual")) {
            mangledTable += func;
            continue;
        }

        if (thunkRe.exactMatch(func)) {
            func = thunkRe.cap(1);
            mangledTable += func;
            continue;
        }
        if (!symHash.contains(func)) {
            func.replace(constRe, "\\1 const\\2");
            func.replace(mapRe, "\\1 const&");
            func.replace(uintRe, "\\1unsigned int\\2");
            func.replace(widRe, "\\1unsigned long\\2");
            func.replace("Q_ULONG", "unsigned long");
            func.replace("Q_UINT16", "unsigned short");
            func.replace("QCOORD", "int");
            func.replace("long unsigned int", "unsigned long");
            if (templArgRe.indexIn(func) != -1) {
                const QString templType = templArgRe.cap(1).prepend("<").append(">");
                func.remove(templArgRe);
                func.replace("<type>", templType);
            }
        }
        if (!symHash.contains(func)) {
            qDebug() << "dropping" << vtable.first();
            return QStringList();
        }
        QString mangFunc = symHash.value(func);
        if (func.contains("::~")) {
            if (firstConstr)
                mangFunc.replace(mangFunc.count() - 4, 4, "D1Ev");
            else
                mangFunc.replace(mangFunc.count() - 4, 4, "D0Ev");

            firstConstr = !firstConstr;
        }
        if (mangFunc.contains("D2Ev"))
            qDebug() << mangFunc << func;
        mangledTable += mangFunc;
    }

    return mangledTable;
}

static void getVTables()
{
    QStringList vTableBlackList;
    vTableBlackList << "QPlatinumStyle" << "QSGIStyle" << "QMotifPlusStyle" << "QWindowsStyle"
          << "QMotifStyle" << "QCDEStyle"
          << "QSqlDriverCreatorBase" << "QAuBucket" << "QAuServer"
              << "QBig5Codec" << "QDnsSocket";

    QProcess proc;
    QStringList args;
    args << "-xc++" << "-c";
    args << "-I" + libRoot + "include";
    args << "-o" << "/tmp/lsbvtable.tmp";
    args << "-fdump-class-hierarchy";
    args << "-";

    proc.start("/home/harald/local/gcc4/bin/g++", args);
    if (!proc.waitForStarted())
        qFatal("unable to execute g++");
    proc.write("#define QT_SHARED\n");
    proc.write("#define QT_NO_DEBUG\n");
    proc.write("#define QT_THREAD_SUPPORT\n");
    proc.write("#define QT_THREAD_SUPPORT\n");
    proc.write("#define QT_NO_IMAGEIO_MNG\n");
    proc.write("#define QT_NO_IMAGEIO_JPEG\n");
    proc.write("#define QT_NO_STYLE_MAC\n");
    proc.write("#define QT_NO_STYLE_AQUA\n");
    proc.write("#define QT_NO_STYLE_INTERLACE\n");
    proc.write("#define QT_NO_STYLE_WINDOWSXP\n");
    proc.write("#define QT_NO_STYLE_COMPACT\n");
    proc.write("#define QT_NO_STYLE_POCKETPC\n");
    proc.write("#include \"" + libRoot.toLatin1() + "/include/qt.h\"\n");
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
            QStringList lines = block.split("\n");
            QString className = lines.first().mid(11);
            if (!className.startsWith("Q") || vTableBlackList.contains(className))
                continue;

            QString vTableSym = symHash.value("vtable for " + className);
            if (vTableSym.isEmpty()) {
                qDebug() << "Library doesn't contain vtable for " << className;
                continue;
            }
            if (!syms.contains(vTableSym)) {
                qDebug() << "Library doesn't export vtable for " << className;
                continue;
            }

            QRegExp re("(\\d+)u entries");
            if (re.indexIn(lines.at(1)) == -1)
                qFatal("cannot parse vtable entry: %s", qPrintable(lines.join("\n")));

            QStringList mangVTable = mangledVTable(lines);
            if (!mangVTable.isEmpty())
                vTables[className] = mangVTable;
        } else if (block.startsWith("Class Q")) {
            QStringList lines = block.split("\n");
            QString className = lines.first().mid(6);

            if (vTableBlackList.contains(className))
                continue;

            QRegExp baseClassRx("^  ([\\w<>]+) \\(.*\\) (\\d+)( nearly-)?( )?(empty)?$");
            QStringList bases = lines.filter(baseClassRx);
            for (int i = 0; i < bases.count(); ++i) {
                if (baseClassRx.indexIn(bases.at(i)) == -1)
                    qFatal("regexp failed");
                bases[i] = baseClassRx.cap(1);
            }

            baseClasses[className] = bases;
        }
    }

    QFile::remove("/tmp/lsbvtable.tmp");
    QFile::remove("-.t01.class");
}

static QString mangledClassName(const QString &name)
{
    QString mangName = symHash.value("vtable for " + name);
    if (mangName.isEmpty())
        qFatal("cannot find mangled name for %s", qPrintable(name));
    Q_ASSERT(mangName.startsWith("_ZTV"));

    // example: _ZTV8QPtrListI15QTableSelectionE -> _Z8QPtrListI15QTableSelectionE
    mangName.remove(2, 2);

    return mangName;
}

static bool writeWeakVTableAndTypeInfo(const QString &klass)
{
    QString vtableSym = symHash.value("vtable for " + klass);
    if (vtableSym.isEmpty())
        return false;
    if (!dumpedFunctions.contains(vtableSym))
        addFunction(vtableSym, "Data", "SrcOnly");

    QString tiSym = symHash.value("typeinfo for " + klass);
    if (tiSym.isEmpty())
        return false;
    if (!dumpedFunctions.contains(tiSym))
        addFunction(tiSym, "Data", "SrcOnly");

    return true;
}

static void writeVTableSyms()
{
    QList<QStringList> syms = vTables.values();

    foreach (QStringList list, syms) {
        foreach (QString sym, list) {
            if (sym.startsWith(QLatin1String("__")) || sym.startsWith(QLatin1String("(")))
                continue;
            if (!dumpedFunctions.contains(sym))
                addFunction(sym, "Function", "SrcOnly");
        }
    }

    // dump weak vtables and typeinfos
    QStringList classes = vTables.keys();
    foreach (QString klass, classes) {
        if (!writeWeakVTableAndTypeInfo(klass))
            qFatal("Unable to write vtable/typeinfo for %s", qPrintable(klass));
    }

    // also do it for all baseclasses
    foreach (QStringList bases, baseClasses) {
        foreach (QString base, bases) {
            if (!blackList.contains(base))
                writeWeakVTableAndTypeInfo(base);
        }
    }
}

static int numVFuncs(const QStringList &vTable)
{
    int i = 0;
    foreach (QString func, vTable) {
        if (!func.startsWith("("))
            ++i;
    }
    return i;
}

static int numVTables(const QStringList &vTable)
{
    int i = 0;
    foreach (QString func, vTable) {
        if (func.startsWith("("))
            ++i;
    }

    return i / 2;
}

static int interfaceId(const QString &symbol, int standard = standardId)
{
    QSqlQuery q;
    q.prepare("select Iid from Interface where Iname = ? and IStandard = ?");
    q.addBindValue(symbol);
    q.addBindValue(standard);
    if (!q.exec()) {
        qWarning() << "unable to find Interface id for" << symbol << q.lastError();
        qFatal("bummer");
    }
    if (!q.next())
        qFatal("No interface id for %s", qPrintable(symbol));

    return q.value(0).toInt();
}

// Foo<Bar> -> Foo
static QString stripTemplateArgs(const QString &name)
{
    return name.mid(0, name.indexOf(QLatin1Char('<')));
}

static int classRtti(const QString &className)
{
    QString rttiName = symHash.value("typeinfo for " + className);
    if (rttiName.isEmpty())
        qFatal("unable to find typeinfo for %s", qPrintable(className));

    int rttiId = dumpedFunctions.value(rttiName);
    if (!rttiId)
        qFatal("unable to find id for %s", qPrintable(rttiName));

    return rttiId;
}

static int baseClassRtti(const QString &className, int pos = 0)
{
    const QString baseClassName = baseClasses.value(className).value(pos);
    if (baseClassName.isEmpty())
        return 0;

    return classRtti(baseClassName);
}

static int numVMIBaseTypes(const QString &className)
{
    int count = baseClasses.value(className).count();
    if (!count)
        qFatal("unable to find vmi baseclasses for %s", qPrintable(className));
    return count;
}

static void splitVTable(const QStringList &vTable, QList<QStringList> &res, QList<int> &offsets)
{
    QStringList current;
    QRegExp offsetRx("-0x([\\da-f]+)$");

    for (int i = 0; i < vTable.count(); ++i) {
        if (vTable.at(i).startsWith(QLatin1Char('('))) {
            if (!current.isEmpty())
                res.append(current);
            current.clear();

            if (offsetRx.indexIn(vTable.at(i)) != -1) {
                bool ok = false;
                offsets.append(offsetRx.cap(1).toInt(&ok, 16) * -1);
                Q_ASSERT(ok);
            }
        } else {
            current.append(vTable.at(i));
        }
    }
    res.append(current);
    offsets.prepend(0);

    if (res.count() != offsets.count())
        qDebug() << vTable << offsets;
    Q_ASSERT(res.count() == offsets.count());
}

static void dumpVTables(const QString &className, const QStringList &vTable, int classId)
{
    static int pureVirtualId = interfaceId("__cxa_pure_virtual", cxxStandardId);
    Q_ASSERT(pureVirtualId);

    int rtti = classRtti(className);
    QList<QStringList> vTabs;
    QList<int> offsets;
    splitVTable(vTable, vTabs, offsets);

    QSqlQuery q;
    q.prepare("insert into ClassVtab (CVcid, CVclass, CVpos, CVrtti, CVnumvtfuncs) "
              "values(?, ?, ?, ?, ?)");
    for (int i = 0; i < vTabs.count(); ++i) {
        q.addBindValue(classId);
        q.addBindValue(1);
        q.addBindValue(i);
        q.addBindValue(rtti);
        q.addBindValue(vTabs.at(i).count());

        if (!q.exec()) {
            qWarning() << "unable to insert into ClassVTab" << q.lastError();
            qFatal("bummer");
        }
    }

    q.prepare("insert into ArchClass (ACcid, ACaid, ACpos, ACbaseoffset) values (?, ?, ?, ?)");
    for (int i = 0; i < vTabs.count(); ++i) {
        q.addBindValue(classId);
        q.addBindValue(1);
        q.addBindValue(i);
        q.addBindValue(offsets.at(i));

        if (!q.exec()) {
            qWarning() << "Unable to insert into ArchClass" << q.lastError();
            qFatal("bummer");
        }
    }

    q.prepare("insert into Vtable (VTcid, VTvtpos, VTpos, VTviid, VTarch) "
              "values (?, ?, ?, ?, ?)");
    for (int i = 0; i < vTabs.count(); ++i) {
        QStringList syms = vTabs.at(i);
        for (int vtpos = 0; vtpos < syms.count(); ++vtpos) {
            int symId = 0;
            if (syms.at(vtpos) == QLatin1String("__cxa_pure_virtual()"))
                symId = pureVirtualId;
            else
                symId = dumpedFunctions.value(syms.at(vtpos));
            if (!symId)
                qFatal("can't find function %s", qPrintable(syms.at(vtpos)));

            q.addBindValue(classId);
            q.addBindValue(i);
            q.addBindValue(vtpos);
            q.addBindValue(symId);
            q.addBindValue(1);

            if (!q.exec()) {
                qWarning() << "unable to insert into Vtable" << q.lastError();
                qFatal("bummer");
            }
        }
    }
}

static void writeVTable(const QString &className)
{
    const QString mangClassName = mangledClassName(className);
    const QStringList vTable = vTables.value(className);
    const QString nakedClassName = mangClassName.mid(2); //without _Z

    Q_ASSERT(!vTable.isEmpty());
    Q_ASSERT(!mangClassName.isEmpty());

    QSqlQuery q;
    // TODO - header group
    q.prepare("insert into Type (TName, Ttype, Tstatus) "
            "values (?, ?, ?)");
    QString lsbConformedMangline = mangClassName;
    q.addBindValue(mangClassName.mid(2));
    q.addBindValue("Class");
    q.addBindValue("Referenced");
    if (!q.exec()) {
        qWarning() << "Unable to insert class into Type table" << q.lastError();
        qFatal("bummer");
    }
    QVariant classTypeId = q.lastInsertId();

    bool vmi = false; // virtual or multiple inheritance
    QString baseVTable = baseVTables.value(stripTemplateArgs(className));
    if (baseVTable.isEmpty())
        qDebug() << "Unable to figure out base vtable for" << className;
    else if (baseVTable == QLatin1String("_ZTVN10__cxxabiv121__vmi_class_type_infoE"))
        vmi = true;

    const QStringList bases = baseClasses.value(className);

    int baseVTableId = interfaceId(baseVTable, cxxStandardId);
    if (!baseVTableId)
        qFatal("unable to figure out interface id for base vtable %s", qPrintable(baseVTable));

    q.prepare("insert into ClassInfo (CIname, CItid, CIvtable, CInumvfunc, CInumvtab, "
              "CIrtti, CInumbasetype, CIbasevtable, CIbase, CInumvmitypes, CIlibg) "
              "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    QString lsbHack = "_Y" + mangClassName.mid(2);
    q.addBindValue(lsbHack);
    q.addBindValue(classTypeId);
    q.addBindValue(interfaceId("_ZTV" + nakedClassName));
    q.addBindValue(numVFuncs(vTable)); // CInumvfunc
    q.addBindValue(numVTables(vTable));
    q.addBindValue(interfaceId("_ZTI" + nakedClassName));
    q.addBindValue(vmi || bases.isEmpty() ? 0 : 1);
    q.addBindValue(baseVTableId); // CIbasevtable
    q.addBindValue(baseClassRtti(className));
    q.addBindValue(vmi ? numVMIBaseTypes(className) : 0);
    q.addBindValue(libGroupId);

    if (!q.exec()) {
        qWarning() << "Unable to insert ClassInfo" << q.lastError();
        qFatal("bummer");
    }

    int classId = q.lastInsertId().toInt();

    if (vmi) {
        q.prepare("insert into VMIBaseTypes(VBTcid, VBTpos, VBTbasetype, VBTaid) "
                "values (?, ?, ?, ?)");
        for (int i = 0; i < bases.count(); ++i) {
            q.addBindValue(classId);
            q.addBindValue(i);
            q.addBindValue(baseClassRtti(className, i));
            q.addBindValue(1);
            if (!q.exec())
                qWarning() << "Unable to add VMIBaseTypes" << q.lastError();
        }
    } else {
        q.prepare("insert into BaseTypes(BTcid, BTpos, BTrttiid) values (?, ?, ?)");
        for (int i = 0; i < bases.count(); ++i) {
            q.addBindValue(classId);
            q.addBindValue(i);
            q.addBindValue(baseClassRtti(className, i));
            if (!q.exec())
                qWarning() << "Unable to add BaseTypes" << q.lastError();
        }
    }

    dumpVTables(className, vTable, classId);
}

struct BaseFlags
{
    QString className;
    QList<int> flags;
};

static QVector<BaseFlags> vmiFlags;

QList<int> &addVmiFlag(const QString &className)
{
    BaseFlags f;
    f.className = className;
    vmiFlags.append(f);
    return vmiFlags.last().flags;
}

// hack - postprocess vmi base type flags. I have no clue how to find them
// out doing static analysis, so I just pasted the result from libchk and
// add them manually
static void addVMIBaseTypeFlags()
{
    vmiFlags.clear();

    addVmiFlag("QSqlCursor") << 0x2 << 0x802;
    addVmiFlag("QDragObject") << 0x2 << 0x2802;
    addVmiFlag("QWidget") << 0x2 << 0x2802;
    addVmiFlag("QGuardedPtrPrivate") << 0x2 << 0x2802;
    addVmiFlag("QSqlResultShared") << 0x2 << 0x2802;
    addVmiFlag("QGLayoutIterator") << 0x402;
    addVmiFlag("QXmlDefaultHandler") << 0x2 << 0x402 << 0x802 << 0xc02 << 0x1002 << 0x1402;
    addVmiFlag("QDropEvent") << 0x2 << 0xc02;
    addVmiFlag("QSlider") << 0x2 << 0x7402;
    addVmiFlag("QPixmap") << 0x2 << 0x2;
    addVmiFlag("QSpinBox") << 0x2 << 0x7402;
    addVmiFlag("QIconFactory") << 0x400;
    addVmiFlag("QScrollBar") << 0x2 << 0x7402;
    addVmiFlag("QDial") << 0x2 << 0x7402;
    addVmiFlag("QLayout") << 0x2 << 0x2802;
    addVmiFlag("QSocket") << 0x2 << 0x2802;
    addVmiFlag("QUrlOperator") << 0x2 << 0x2802;
    addVmiFlag("QMenuBar") << 0x2 << 0x9402;
    addVmiFlag("QPopupMenu") << 0x2 << 0x9402;

    QSqlQuery q;
    q.prepare("update VMIBaseTypes set VBTflags = ? where VBTpos = ? and VBTcid = "
              "(select CIid from ClassInfo where CIname = ?)");
    for (int i = 0; i < vmiFlags.count(); ++i) {
        BaseFlags f = vmiFlags.at(i);
        QString lsbHack = "_Y" + QString::number(f.className.length()) + f.className;
        for (int j = 0; j < f.flags.count(); ++j) {
            q.addBindValue(f.flags.at(j));
            q.addBindValue(j);
            q.addBindValue(lsbHack);
            if (!q.exec() || q.numRowsAffected() != 1) {
                qWarning() << "Unable to set VMIFlags" << q.lastError();
                qFatal("bummer");
            }
        }
    }
}

static void fixArchVmiFlags(const QSqlRecord &rec)
{
    QSqlQuery q;
    q.prepare("update VMIBaseTypes set VBTaid = 2 where VBTcid = ? and VBTpos = ?");
    q.addBindValue(rec.value("VBTCid"));
    q.addBindValue(rec.value("VBTpos"));
    if (!q.exec())
        qFatal("unable to fix vmibasetype arch 2: %s", qPrintable(q.lastError().text()));

    q.prepare("insert into VMIBaseTypes (VBTcid,VBTPos,VBTbasetype,VBTflags,VBTaid)"
              "values (?, ?, ?, ?, ?)");

    for (int i = 0; i < ArchCount; ++i) {
        q.addBindValue(rec.value("VBTcid"));
        q.addBindValue(rec.value("VBTPos"));
        q.addBindValue(rec.value("VBTbasetype"));
        if (archs[i][1] == 1)
            q.addBindValue(rec.value("VBTflags"));
        else
            q.addBindValue(rec.value("VBTflags").toInt() * 2 - 2);
        q.addBindValue(archs[i][0]);

        if (!q.exec())
            qFatal("unable to fix vmi flags: %s", qPrintable(q.lastError().text()));
    }
}

static void fixArchClass(const QSqlRecord &rec)
{
    QSqlQuery q;
    q.prepare("update ArchClass set ACaid = 2 where ACAid = 1 and ACcid = ? and ACpos = ?");
    q.addBindValue(rec.value("ACcid"));
    q.addBindValue(rec.value("ACpos"));
    if (!q.exec())
        qFatal("unable to fix archclass, arch 2: %s", qPrintable(q.lastError().text()));

    q.prepare("insert into ArchClass (ACcid, ACaid, ACpos, ACbaseoffset) "
              "values (?, ?, ?, ?)");

    for (int i = 0; i < ArchCount; ++i) {
        q.addBindValue(rec.value("ACcid"));
        q.addBindValue(archs[i][0]);
        q.addBindValue(rec.value("ACpos"));
        q.addBindValue(rec.value("ACbaseoffset").toInt() * archs[i][1]);
        if (!q.exec())
            qFatal("unable to add archclass: %s", qPrintable(q.lastError().text()));
    }
}

static void fixVirtualThunk(const QSqlRecord &rec)
{
    QList<QPair<int, int> > archIds;

    QSqlQuery q;
    q.prepare("update Interface set Iarch = 2 where Iid = ?");
    q.addBindValue(rec.value("Iid"));
    if (!q.exec())
        qFatal("unable to fix vthunk, arch 2: %s", qPrintable(q.lastError().text()));

    QRegExp thunkRe("^_ZThn(\\d+)_(.*)$");
    if (!thunkRe.exactMatch(rec.value("Iname").toString()))
        qFatal("thunk regexp failed for %s", qPrintable(rec.value("Iname").toString()));
    QString thunkName64 = "_ZThn" + QString::number(thunkRe.cap(1).toInt() * 2) + "_"
                          + thunkRe.cap(2);

    q.prepare("insert into Interface (Iname, Istatus, Itype, Istandard, Iarch)"
              "values (?, ?, ?, ?, ?)");
    for (int i = 0; i < ArchCount; ++i) {
        q.addBindValue(archs[i][1] == 2 ? thunkName64 : rec.value("Iname").toString());
        q.addBindValue(rec.value("Istatus"));
        q.addBindValue(rec.value("Itype"));
        q.addBindValue(rec.value("Istandard"));
        q.addBindValue(archs[i][0]);
        if (!q.exec())
            qFatal("unable to fix vthunk: %s", qPrintable(q.lastError().text()));

        int iid = q.lastInsertId().toInt();
        archIds += QPair<int, int>(archs[i][0], iid);
    }

    q.prepare("insert into ArchInt (AIarch, AIint) values (?, ?)");
    for (int i = 0; i < archIds.count(); ++i) {
        QPair<int, int> sym = archIds.at(i);
        addArchInt(sym.second, sym.first);
        addLGInt(sym.second);
    }
    addArchInt(rec.value("Iid").toInt(), 2);

    q.prepare("select * from Vtable where VTviid = ? and VTarch = 1");
    q.addBindValue(rec.value("Iid"));
    if (!q.exec())
        qFatal("unable to find arch syms in Vtable: %s", qPrintable(q.lastError().text()));
    QSqlQuery ins;
    ins.prepare("insert into Vtable (VTcid, VTvtpos, VTpos, VTviid, VTarch) "
                "values (?, ?, ?, ?, ?)");
    while (q.next()) {
        QSqlRecord vt = q.record();
        for (int i = 0; i < archIds.count(); ++i) {
            ins.addBindValue(vt.value("VTcid"));
            ins.addBindValue(vt.value("VTvtpos"));
            ins.addBindValue(vt.value("VTpos"));
            ins.addBindValue(archIds.at(i).second);
            ins.addBindValue(archIds.at(i).first);
            if (!ins.exec())
                qFatal("unable to update arch Vtable: %s", qPrintable(ins.lastError().text()));
        }
    }

    q.prepare("update Vtable set VTarch = 2 where VTviid = ?");
    q.addBindValue(rec.value("Iid"));
    if (!q.exec())
        qFatal("unable to update Vtable: %s", qPrintable(q.lastError().text()));
}

static void fixArchDepThunks()
{
    static const char *archThunks[] = {

    "_ZThn24_N10QDropEventD1Ev", "_ZThn16_N10QDropEventD1Ev",
    "_ZThn24_N10QDropEventD0Ev", "_ZThn16_N10QDropEventD0Ev",
    "_ZThn24_NK10QDropEvent6formatEi", "_ZThn16_NK10QDropEvent6formatEi",
    "_ZThn24_NK10QDropEvent8providesEPKc", "_ZThn16_NK10QDropEvent8providesEPKc",
    "_ZThn24_NK10QDropEvent11encodedDataEPKc", "_ZThn16_NK10QDropEvent11encodedDataEPKc",
    "_ZThn24_N14QDragMoveEventD1Ev", "_ZThn16_N14QDragMoveEventD1Ev",
    "_ZThn24_N14QDragMoveEventD0Ev", "_ZThn16_N14QDragMoveEventD0Ev",
    "_ZThn24_NK10QDropEvent6formatEi", "_ZThn16_NK10QDropEvent6formatEi",
    "_ZThn24_NK10QDropEvent8providesEPKc", "_ZThn16_NK10QDropEvent8providesEPKc",
    "_ZThn24_NK10QDropEvent11encodedDataEPKc", "_ZThn16_NK10QDropEvent11encodedDataEPKc",
    "_ZThn24_N15QDragEnterEventD1Ev", "_ZThn16_N15QDragEnterEventD1Ev",
    "_ZThn24_N15QDragEnterEventD0Ev", "_ZThn16_N15QDragEnterEventD0Ev",
    "_ZThn24_NK10QDropEvent6formatEi", "_ZThn16_NK10QDropEvent6formatEi",
    "_ZThn24_NK10QDropEvent8providesEPKc", "_ZThn16_NK10QDropEvent8providesEPKc",
    "_ZThn24_NK10QDropEvent11encodedDataEPKc", "_ZThn16_NK10QDropEvent11encodedDataEPKc",
    "_ZThn232_N7QSliderD1Ev", "_ZThn200_N7QSliderD1Ev",
    "_ZThn232_N7QSliderD0Ev", "_ZThn200_N7QSliderD0Ev",
    "_ZThn232_N7QSlider11valueChangeEv", "_ZThn200_N7QSlider11valueChangeEv",
    "_ZThn232_N7QSlider11rangeChangeEv", "_ZThn200_N7QSlider11rangeChangeEv",
    "_ZThn232_N8QSpinBoxD1Ev", "_ZThn200_N8QSpinBoxD1Ev",
    "_ZThn232_N8QSpinBoxD0Ev", "_ZThn200_N8QSpinBoxD0Ev",
    "_ZThn232_N8QSpinBox11valueChangeEv", "_ZThn200_N8QSpinBox11valueChangeEv",
    "_ZThn232_N8QSpinBox11rangeChangeEv", "_ZThn200_N8QSpinBox11rangeChangeEv",
    "_ZThn232_N10QScrollBarD1Ev", "_ZThn200_N10QScrollBarD1Ev",
    "_ZThn232_N10QScrollBarD0Ev", "_ZThn200_N10QScrollBarD0Ev",
    "_ZThn232_N10QScrollBar11valueChangeEv", "_ZThn200_N10QScrollBar11valueChangeEv",
    "_ZThn232_N10QScrollBar11rangeChangeEv", "_ZThn200_N10QScrollBar11rangeChangeEv",
    "_ZThn232_N10QScrollBar10stepChangeEv", "_ZThn200_N10QScrollBar10stepChangeEv",
    "_ZThn232_N5QDialD1Ev", "_ZThn200_N5QDialD1Ev",
    "_ZThn232_N5QDialD0Ev", "_ZThn200_N5QDialD0Ev",
    "_ZThn232_N5QDial11valueChangeEv", "_ZThn200_N5QDial11valueChangeEv",
    "_ZThn232_N5QDial11rangeChangeEv", "_ZThn200_N5QDial11rangeChangeEv",
    "_ZThn296_N8QMenuBarD1Ev", "_ZThn240_N8QMenuBarD1Ev",
    "_ZThn296_N8QMenuBarD0Ev", "_ZThn240_N8QMenuBarD0Ev",
    "_ZThn296_N8QMenuBar10updateItemEi", "_ZThn240_N8QMenuBar10updateItemEi",
    "_ZThn296_N8QMenuBar14activateItemAtEi", "_ZThn240_N8QMenuBar14activateItemAtEi",
    "_ZThn296_N8QMenuBar19menuContentsChangedEv", "_ZThn240_N8QMenuBar19menuContentsChangedEv",
    "_ZThn296_N8QMenuBar16menuStateChangedEv", "_ZThn240_N8QMenuBar16menuStateChangedEv",
    "_ZThn296_N8QMenuBar12menuInsPopupEP10QPopupMenu", "_ZThn240_N8QMenuBar12menuInsPopupEP10QPopupMenu",
    "_ZThn296_N8QMenuBar12menuDelPopupEP10QPopupMenu", "_ZThn240_N8QMenuBar12menuDelPopupEP10QPopupMenu",
    "_ZThn296_N10QPopupMenuD1Ev", "_ZThn240_N10QPopupMenuD1Ev",
    "_ZThn296_N10QPopupMenuD0Ev", "_ZThn240_N10QPopupMenuD0Ev",
    "_ZThn296_N10QPopupMenu10updateItemEi", "_ZThn240_N10QPopupMenu10updateItemEi",
    "_ZThn296_N10QPopupMenu14activateItemAtEi", "_ZThn240_N10QPopupMenu14activateItemAtEi",
    "_ZThn296_N10QPopupMenu19menuContentsChangedEv", "_ZThn240_N10QPopupMenu19menuContentsChangedEv",
    "_ZThn296_N10QPopupMenu16menuStateChangedEv", "_ZThn240_N10QPopupMenu16menuStateChangedEv",
    "_ZThn296_N10QPopupMenu12menuInsPopupEPS_", "_ZThn240_N10QPopupMenu12menuInsPopupEPS_",
    "_ZThn296_N10QPopupMenu12menuDelPopupEPS_", "_ZThn240_N10QPopupMenu12menuDelPopupEPS_",
    0, 0
    };

    QSqlQuery q;
    q.prepare("update Interface set Iname = ? where Iname = ? and "
              "(Iarch = 3 or Iarch = 9 or Iarch = 11 or Iarch = 12)");

    int i = 0;
    while (archThunks[i]) {
        q.addBindValue(QString::fromLatin1(archThunks[i + 1]));
        q.addBindValue(QString::fromLatin1(archThunks[i]));
        if (!q.exec())
            qFatal("unable to postprocess thunks: %s", qPrintable(q.lastError().text()));
        i += 2;
    }

    static const char *archOffsets[] = {
        "2", "-296", "-240", "_Y10QPopupMenu",
        "2", "-296", "-240", "_Y8QMenuBar",
        "2", "-232", "-200", "_Y5QDial",
        "2", "-232", "-200", "_Y10QScrollBar",
        "2", "-232", "-200", "_Y8QSpinBox",
        "2", "-232", "-200", "_Y7QSlider",
        "1", "-24" , "-16",  "_Y15QDragEnterEvent",
        "1", "-24" , "-16",  "_Y14QDragMoveEvent",
        "1", "-24" , "-16",  "_Y10QDropEvent",
        0
    };

    q.prepare("update ArchClass set ACbaseoffset = ? "
              "where ACcid = (select CIid from ClassInfo where CIname = ?) and ACpos = ? "
              "and (ACaid = 3 or ACaid = 9 or ACaid = 11 or ACaid = 12)");

    i = 0;
    while (archOffsets[i]) {
        q.addBindValue(QString::fromLatin1(archOffsets[i + 2]).toInt());
        q.addBindValue(QString::fromLatin1(archOffsets[i + 3]));
        q.addBindValue(QString::fromLatin1(archOffsets[i]).toInt());
        if (!q.exec())
            qFatal("unable to postprocess offsets: %s", qPrintable(q.lastError().text()));
        i += 4;
    }

    static const char *archFlags[] = {
        "1", "f002", "_Y10QPopupMenu",
        "1", "f002", "_Y8QMenuBar",
        "1", "c802", "_Y5QDial",
        "1", "c802", "_Y10QScrollBar",
        "0", "800",  "_Y12QIconFactory",
        "1", "c802", "_Y8QSpinBox",
        "1", "c802", "_Y7QSlider",
        "1", "1002", "_Y10QDropEvent",
        0
    };

    q.prepare("update VMIBaseTypes set VBTflags = ? "
              "where VBTpos = ? and VBTCid = (select CIid from ClassInfo where CIname = ?) "
              "and (VBTaid = 3 or VBTaid = 9 or VBTaid = 11 or VBTaid = 12)");

    i = 0;
    while (archFlags[i]) {
        q.addBindValue(QString::fromLatin1(archFlags[i + 1]).toInt(0, 16));
        q.addBindValue(QString::fromLatin1(archFlags[i]).toInt());
        q.addBindValue(QString::fromLatin1(archFlags[i + 2]));
        if (!q.exec())
            qFatal("unable to postprocess flags: %s", qPrintable(q.lastError().text()));
        i += 3;
    }
}

static void archPostprocess()
{
    QSqlQuery q;

    if (!q.exec("select VBTcid,VBTPos,VBTbasetype,VBTflags from VMIBaseTypes "
                "where VBTaid = 1 and VBTflags != 2"))
        qFatal("unable to fix vbtflags: %s", qPrintable(q.lastError().text()));
    while (q.next())
        fixArchVmiFlags(q.record());

    if (!q.exec("select * from ArchClass where ACbaseoffset != 0 and ACaid = 1"))
        qFatal("unable to fix archclass: %s", qPrintable(q.lastError().text()));
    while (q.next())
        fixArchClass(q.record());

    if (!q.exec("select * from Interface where Iname like '_ZThn%' and Iarch = 1"))
        qFatal("unable to fix thunks: %s", qPrintable(q.lastError().text()));
    while (q.next())
        fixVirtualThunk(q.record());

    fixArchDepThunks();
}

int main(int argc, char *argv[])
{
    QCoreApplication app(argc, argv);

    sqlconnect();

    if (argc > 2)
        qFatal("takes only 'init' or 'post'");
    if (argc == 2) {
        if (argv[1] == QString::fromLatin1("init")) {
            setStandard();
            return 0;
        } else if (argv[1] == QString::fromLatin1("post")) {
            archPostprocess();
            return 0;
        }
    }

    clear();

    getVTT();
    getSyms();
    getVTables();

    writeSyms();
    writeVTableSyms();

    foreach (QString cl, vTables.keys())
        writeVTable(cl);

    addVMIBaseTypeFlags();

    archPostprocess();

    return 0;
}


