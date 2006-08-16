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

#include "lsbdb.h"

#include <QtSql/QtSql>
#include "qsqlfetchvalue.h"

LsbDb::LsbDb()
    : libId(0), libGroupId(0), stdId(0), modId(0)
{
}

void LsbDb::addTypeRedirection(const QString &from, const QString &to)
{
    int tid = typeId(to);
    if (!tid)
        qFatal("Unable to find type %s in database", qPrintable(to));
    typeCache[from] = tid;
}

bool LsbDb::connect()
{
    QSqlDatabase db = QSqlDatabase::addDatabase("QMYSQL");
    db.setHostName(qgetenv("LSBDBHOST"));
    db.setUserName(qgetenv("LSBUSER"));
    db.setPassword(qgetenv("LSBDBPASSWD"));
    db.setDatabaseName(qgetenv("LSBDB"));
    if (!db.open()) {
        qWarning() << "Unable to open database: " << db.lastError();
        return false;
    }

    fillTypedefCache();

    // some redirections
    addTypeRedirection("long", "long int");
    addTypeRedirection("unsigned long", "unsigned long int");
    addTypeRedirection("long long", "long long int");
    addTypeRedirection("unsigned long long", "unsigned long long int");

    return true;
}

void LsbDb::fillTypedefCache()
{
    QSqlQuery q;
    q.exec("select A.Tname, B.Tname from Type A, Type B where A.Ttype = 'Typedef' "
            "and A.Tbasetype = B.Tid "
            "and A.Tstatus != 'Conly' "
            "and Tarch <= 1");
    while (q.next())
        typedefCache[q.value(0).toString()] = q.value(1).toString();
    typedefCache.remove("bool");
}

void LsbDb::disconnect()
{
    QSqlDatabase::database().close();
    QSqlDatabase::removeDatabase(QSqlDatabase::defaultConnection);
}

bool LsbDb::addLibrary(const QString &libName, const QString &soName, const QString &standard)
{
    QSqlQuery q;
    q.prepare("insert into Library (Lname, Lrunname, Lstd) values (?, ?, ?)");
    q.addBindValue("lib" + libName);
    q.addBindValue(soName);
    q.addBindValue(standard);
    if (!q.exec()) {
        qWarning() << "Unable to add library" << q.lastError();
        return false;
    }

    libId = q.lastInsertId().toInt();

    q.prepare("insert into ArchLib (ALlid, ALaid, ALrunname) values (?, ?, ?)");
    q.addBindValue(libId);
    q.addBindValue(1);
    q.addBindValue(soName);
    if (!q.exec()) {
        qWarning() << "Unable to add library architecture" << q.lastError();
        return false;
    }

    q.prepare("insert into LibGroup (LGname, LGlib, LGarch, LGorder, LGdescription) "
              "values (?, ?, ?, ?, ?)");
    q.addBindValue(libName);
    q.addBindValue(libId);
    q.addBindValue(1);
    q.addBindValue(1);
    q.addBindValue("");
    if (!q.exec()) {
        qWarning() << "Unable to add library group" << q.lastError();
        return false;
    }
    libGroupId = q.lastInsertId().toInt();

    q.prepare("insert into ModLib (MLmid, MLlid) values (?, ?)");
    q.addBindValue(modId);
    q.addBindValue(libId);
    if (!q.exec()) {
        qWarning() << "Unable to add module lib" << q.lastError();
        return false;
    }

    return true;
}

int LsbDb::libraryId(const QString &libName)
{
    return qSqlFetchValue<int>(QString::fromLatin1("select Lid from Library where Lname = 'lib%1'").arg(libName));
}

int LsbDb::standardId(const QString &name)
{
    return qSqlFetchValue<int>(QString::fromLatin1("select Sid from Standard where Sname = '%1'").arg(name));
}

int LsbDb::getHeaderId(const QString &header)
{
    if (header.isEmpty())
        return 0;

    int id = headerCache.value(header, 0);
    if (id)
        return id;

    id = qSqlFetchValue<int>(QString::fromLatin1("select Hid from Header where Hname = '%1'").arg(header));

    if (!id) {
        QSqlQuery q;
        q.prepare("insert into Header (Hname, Hstd, Hlib) values (?, ?, ?)");
        q.addBindValue(header);
        q.addBindValue("yes");
        q.addBindValue(currentLibraryId());
        if (!q.exec())
            qWarning() << "Unable to insert header" << q.lastError();
        else
            id = q.lastInsertId().toInt();
    }

    if (id)
        headerCache[header] = id;

    return id;
}

int LsbDb::getHeaderGroupId(const QString &header, const QString &headerGroupName, int order)
{
    int hId = getHeaderId(header);
    if (!hId)
        return 0;

    QString hgName = headerGroupName;
    if (hgName.isEmpty())
        hgName = QLatin1String("Default HeaderGroup for ") + header;

    int id = qSqlFetchValue<int>(QString::fromLatin1("select HGid from HeaderGroup where "
                "HGHeader = %1 and HGname = '%2'").arg(hId).arg(hgName));
    if (!id) {
        QSqlQuery q;
        q.prepare("insert into HeaderGroup (HGname, HGheader, HGorder) values (?, ?, ?)");
        q.addBindValue(hgName);
        q.addBindValue(hId);
        q.addBindValue(order);
        if (!q.exec())
            qWarning() << "Unable to insert header group" << q.lastError();
        else
            id = q.lastInsertId().toInt();
    }

    return id;
}

struct ClassInfo
{
    inline ClassInfo(): id(0), rtti(0), vtable(0) {}
    int id;
    int rtti;
    int vtable;
};

static QList<ClassInfo> getClassInfos(const QList<int> &typeIds)
{
    QList<ClassInfo> infoList;

    QSqlQuery q;
    q.prepare("select CIvtable, CIrtti from ClassInfo where CItid = ?");

    foreach (int id, typeIds) {
        ClassInfo inf;
        q.addBindValue(id);
        if (!q.exec())
            qWarning() << "Unable to get ClassInfo" << q.lastError();
        if (q.next()) {
            inf.vtable = q.value(0).toInt();
            inf.rtti = q.value(1).toInt();
        } else {
            qWarning() << "unable to find ClassInfo for type" << id;
        }
        inf.id = id;
        infoList.append(inf);
    }

    return infoList;
}

int LsbDb::addClass(const QString &type, const QString &className, const QList<int> &baseTypeIds,
              int vTableId, const QStringList &vTable, int typeInfoId, const QString &baseVType,
              const QString &header, const QString &headerGroupName)
{
    static int vmiTypeId = interfaceId("_ZTVN10__cxxabiv121__vmi_class_type_infoE");
    int baseVTypeId = interfaceId(baseVType);
    bool vmi = baseVTypeId == vmiTypeId;

    int hgId = getHeaderGroupId(header, headerGroupName);
    if (!hgId) {
        qWarning() << "Unable to get header group id";
        return 0;
    }

    QSqlQuery q;
    q.prepare("insert into Type (TName, Ttype, Tbasetype, Theadergroup, Tstatus) "
              "values (?, ?, ?, ?, ?)");
    q.addBindValue(className);
    q.addBindValue(type);
    q.addBindValue(baseTypeIds.value(0));
    q.addBindValue(hgId);
    q.addBindValue("Referenced");
    if (!q.exec())
        qWarning() << "Unable to insert class" << q.lastError();
    QVariant classId = q.lastInsertId();

    QList<ClassInfo> baseClassInfo = getClassInfos(baseTypeIds);

    q.prepare("insert into ClassInfo(CIName, CItid, CIvtable, CInumvfunc, CInumvtab, CIrtti, "
        "CInumbasetype, CIbase, CIbasevtable, CIlibg, CInumvmitypes) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    if (className.startsWith("N") && className.length() > 3 && className.at(1).isDigit())
        q.addBindValue("_Z" + className);
    else
        q.addBindValue("_Z" + QString::number(className.length()) + className);
    q.addBindValue(classId);
    q.addBindValue(vTableId);
    q.addBindValue(vTable.count());
    q.addBindValue(vTableId ? 1 : 0);
    q.addBindValue(typeInfoId); // CIrtti
    q.addBindValue(vmi ? 0 : baseTypeIds.count());
    q.addBindValue(baseClassInfo.value(0).rtti); // CIbase
    q.addBindValue(baseVTypeId);
    q.addBindValue(libGroupId);
    q.addBindValue(vmi ? baseTypeIds.count() : 0);
    if (!q.exec())
        qWarning() << "Unable to insert class info" << q.lastError();
    int classInfoId = q.lastInsertId().toInt();

    // TODO - multiple inheritance, platform specific vtables
    q.prepare("insert into ArchClass(ACcid, ACaid) values (?, ?)");
    q.addBindValue(classInfoId);
    q.addBindValue(1);
    if (!q.exec())
        qWarning() << "unable to add ArchClass" << q.lastError();

    if (vmi) {
        q.prepare("insert into VMIBaseTypes(VBTcid, VBTpos, VBTbasetype, VBTaid) "
                  "values (?, ?, ?, ?)");
        for (int i = 0; i < baseClassInfo.count(); ++i) {
            q.addBindValue(classInfoId);
            q.addBindValue(i);
            q.addBindValue(baseClassInfo.at(i).rtti);
            q.addBindValue(1);
            if (!q.exec())
                qWarning() << "Unable to add VMIBaseTypes" << q.lastError();
        }
    } else {
        q.prepare("insert into BaseTypes(BTcid, BTpos, BTrttiid) values (?, ?, ?)");
        for (int i = 0; i < baseClassInfo.count(); ++i) {
            q.addBindValue(classInfoId);
            q.addBindValue(i);
            q.addBindValue(baseClassInfo.at(i).rtti);
            if (!q.exec())
                qWarning() << "Unable to add BaseTypes" << q.lastError();
        }
    }

    // dump the vtable if we have one
    if (vTableId) {
        q.prepare("insert into ClassVtab (CVcid, CVclass, CVpos, CVrtti, CVnumvtfuncs) "
                  "values (?, ?, ?, ?, ?)");
        q.addBindValue(classInfoId);
        q.addBindValue(1);
        q.addBindValue(0);
        q.addBindValue(typeInfoId);
        q.addBindValue(vTable.count());
        if (!q.exec())
            qWarning() << "Unable to dump ClassVTab" << q.lastError();
    }

    return classInfoId;
}

bool LsbDb::addVtable(int classInfoId, const QStringList &vTable)
{
    QSqlQuery q;
    q.prepare("select Iid from Interface where Iname = ?");

    static int pureVirtual = qSqlFetchValue<int>("select Iid from Interface where Iname = '__cxa_pure_virtual'");

    QList<int> typeIds;
    int i;
    for (i = 0; i < vTable.count(); ++i) {
        q.addBindValue(vTable.at(i));
        if (!q.exec() || !q.next()) {
            qWarning() << "Unable to find vtable method" << vTable.at(i) << q.lastError();

            // HACK - can't find the interface - assume pure virtual
            typeIds.append(pureVirtual);
        } else {
            typeIds.append(q.value(0).toInt());
        }
    }

    q.prepare("insert into Vtable (VTcid, VTvtpos, VTpos, VTviid, VTarch) "
              "values (?, ?, ?, ?, ?)");

    for (i = 0; i < typeIds.count(); ++i) {
        q.addBindValue(classInfoId);
        q.addBindValue(0);
        q.addBindValue(i);
        q.addBindValue(typeIds.at(i));
        q.addBindValue(1);
        if (!q.exec()) {
            qWarning() << "Unable to dump Vtable for" << vTable.at(i) << q.lastError();
            return false;
        }
    }

    return true;
}

int LsbDb::addMember(const QString &memberName, int typeId, int position, int memberOf)
{
    QSqlQuery q;
    q.prepare("insert into TypeMember (TMname, TMtypeid, TMposition, TMmemberof) "
              "values (?, ?, ?, ?)");
    q.addBindValue(memberName);
    q.addBindValue(typeId);
    q.addBindValue(position);
    q.addBindValue(memberOf);
    if (!q.exec())
        qWarning() << "Unable to insert member" << q.lastError();
    return q.lastInsertId().toInt();
}

int LsbDb::typeId(const QString &typeName)
{
    int tId = typeCache.value(typeName, 0);
    if (tId)
        return tId;

    tId = qSqlFetchValue<int>(QString::fromLatin1("select Tid from Type where Tname = '%1'").arg(typeName));
    if (tId) {
        typeCache[typeName] = tId;
        return tId;
    }

    QString normalized = typeName;
    normalized.replace(QRegExp("(\\w+) const"), "const \\1");
    normalized.replace(QRegExp("\\s*\\*\\s*"), "*");
    normalized.replace(QLatin1Char('*'), QLatin1String("( )*\\\\*"));

    tId = typeCache.value(normalized);
    if (tId) {
        typeCache[typeName] = tId;
        return tId;
    }

    tId = qSqlFetchValue<int>(QString::fromLatin1("select Tid from Type where Tname REGEXP '^%1$'").arg(normalized));
    if (tId) {
        typeCache[typeName] = tId;
        return tId;
    }

    return 0;
}

int LsbDb::getTypeId(const QString &typeName)
{
    int tId = typeId(typeName);
    if (tId)
        return tId;

    if (!typeName.endsWith("*")) {
//        qWarning() << "getTypeId: unknown type" << typeName;
        return 0;
    }

    int pId = typeId(typeName.left(typeName.length() - 1).simplified());
    if (!pId) {
//        qWarning() << "getTypeId: unknown base type for " << typeName;
        return 0;
    }

    QSqlQuery q;
    q.prepare("insert into Type (Tname, Ttype, Tbasetype, Tstatus) values (?, ?, ?, ?)");
    q.addBindValue(typeName);
    q.addBindValue("Pointer");
    q.addBindValue(pId);
    q.addBindValue("Referenced");

    if (!q.exec())
        qWarning() << "Unable to insert type" << q.lastError();


    tId = q.lastInsertId().toInt();
    Q_ASSERT(tId);

    typeCache[typeName] = tId;
    return tId;
}

bool LsbDb::initCppIntrinsics()
{
    int bId = qSqlFetchValue<int>("Select Tid from Type where Tname = 'bool' and Ttype = 'Intrinsic'");
    if (!bId) {
        QSqlQuery q;
        if (!q.exec("insert into Type (Tname, Ttype, Tbasetype, Tstatus) values ('bool', 'Intrinsic', 0, 'Indirect')"))
            qWarning() << "unable to insert bool" << q.lastError();
        bId = q.lastInsertId().toInt();
    }

    if (!bId) {
        qWarning("Unable to register C++ intrinsic types");
        return false;
    }
    typeCache["bool"] = bId;
    return true;
}


int LsbDb::addEnum(const QString &name, const QString &header)
{
    QSqlQuery q;
    q.prepare("insert into Type (Tname, Ttype, Theadergroup, Tstatus) values (?, ?, ?, ?)");
    q.addBindValue(name);
    q.addBindValue("Enum");
    q.addBindValue(getHeaderGroupId(header));
    q.addBindValue("Referenced");

    if (!q.exec())
        qWarning() << "Unable to add Enum" << q.lastError();

    return q.lastInsertId().toInt();
}

int LsbDb::addStandard(const QString &standardName, const QString &longDescription,
                       const QString &shortDescription, const QString &url)
{
    QSqlQuery q;
    q.prepare("insert into Standard (Sname, Sfull, Surl, Stype, Sarch, Sshort) "
            "values (?, ?, ?, ?, ?, ?)");
    q.addBindValue(standardName);
    q.addBindValue(longDescription);
    q.addBindValue(url);
    q.addBindValue("Standard");
    q.addBindValue(1);
    q.addBindValue(shortDescription);
    if (!q.exec()) {
        qWarning() << "Unable to add Standard" << q.lastError();
        return 0;
    }
    stdId = q.lastInsertId().toInt();
    return stdId;
}

int LsbDb::addReadOnlyData(const QString &name, const QString &header)
{
    QSqlQuery q;
    q.prepare("insert into Interface (Iname, Istatus, Itype, Istandard, Iheader) "
            "values (?, ?, ?, ?, ?)");
    q.addBindValue(name);
    q.addBindValue("Included");
    q.addBindValue("Data");
    q.addBindValue(stdId);
    q.addBindValue(getHeaderId(header));

    if (!q.exec())
        qWarning() << "Unable to add ReadOnly Data" << q.lastError();

    return addLGInt(q.lastInsertId().toInt());
}

// adds the mapping into LGInt, returns the iid on success, 0 on error
int LsbDb::addLGInt(int iid)
{
    if (!iid)
        return 0;

    QSqlQuery q;
    q.prepare("insert into LGInt (LGIint, LGIlibg) values (?, ?)");
    q.addBindValue(iid);
    q.addBindValue(libGroupId);
    if (!q.exec()) {
        qWarning() << "Unable to insert mapping into LGInt table" << q.lastError();
        return 0;
    }
    return iid;
}

int LsbDb::addFunction(const QString &name, const QString &returnType, const QString &header,
                       int architecture, bool srcOnly)
{
    int typeId = 0;

    // constructors have no return value
    if (!returnType.isEmpty()) {
        typeId = getTypeId(returnType);
        if (!typeId) {
            qWarning() << "Unable to figure out the return type" << returnType;
// TODO            return 0;
        }
    }

    QSqlQuery q;
    q.prepare("insert into Interface (Iname, Istatus, Itype, Istandard, Iarch, Iheader, Ireturn) "
            "values (?, ?, ?, ?, ?, ?, ?)");
    q.addBindValue(name);
    q.addBindValue(srcOnly ? "SrcOnly" : "Included");
    q.addBindValue("Function");
    q.addBindValue(stdId);
    q.addBindValue(architecture);
    q.addBindValue(getHeaderId(header));
    q.addBindValue(typeId);

    if (!q.exec())
        qWarning() << "Unable to add Function" << q.lastError();

    return addLGInt(q.lastInsertId().toInt());
}

int LsbDb::addVariable(const QString &name, const QString &type, const QString &header)
{
    int typeId = getTypeId(type);
    if (!typeId) {
        qWarning() << "Unable to figure out the variable type" << type;
        return 0;
    }

    QSqlQuery q;
    q.prepare("insert into Interface (Iname, Istatus, Itype, Istandard, Iheader, Ireturn) "
            "values (?, ?, ?, ?, ?, ?)");
    q.addBindValue(name);
    q.addBindValue("Included");
    q.addBindValue("Data");
    q.addBindValue(stdId);
    q.addBindValue(getHeaderId(header));
    q.addBindValue(typeId);

    if (!q.exec())
        qWarning() << "Unable to add Variable" << q.lastError();

    return addLGInt(q.lastInsertId().toInt());
}

int LsbDb::addTypeInfo(const QString &name, bool data)
{
    QSqlQuery q;
    q.prepare("insert into Interface (Iname, Istatus, Itype, Istandard, Iheader, Ireturn) "
            "values (?, ?, ?, ?, ?, ?)");
    q.addBindValue(name);
    q.addBindValue("Included");
    q.addBindValue(data ? "Data" : "Function");
    q.addBindValue(stdId);
    q.addBindValue("");
    q.addBindValue(0);

    if (!q.exec())
        qWarning() << "Unable to add TypeInfo" << q.lastError();

    return addLGInt(q.lastInsertId().toInt());
}

int LsbDb::addEnumValue(const QString &name, int value, int parentId)
{
    QSqlQuery q;
    q.prepare("insert into TypeMember (TMname, TMtypeid, TMposition, TMmemberof) "
              "values (?, ?, ?, ?)");
    q.addBindValue(name);
    q.addBindValue(parentId);
    q.addBindValue(value);
    q.addBindValue(parentId);

    if (!q.exec())
        qWarning() << "Unable to add Enum Value" << q.lastError();

    return q.lastInsertId().toInt();
}

int LsbDb::addTypedef(const QString &name, int typeId, const QString &header,
        const QString &headerGroupName)
{
    int hgId = getHeaderGroupId(header, headerGroupName);
    if (!hgId)
        return 0;

    QSqlQuery q;
    q.prepare("insert into Type (Tname, Ttype, Tbasetype, Theadergroup, Tstatus) "
            "values (?, ?, ?, ?, ?)");
    q.addBindValue(name);
    q.addBindValue("Typedef");
    q.addBindValue(typeId);
    q.addBindValue(hgId);
    q.addBindValue("Referenced");

    if (!q.exec())
        qWarning() << "Unable to add Typedef" << q.lastError();

    typedefCache[name] = typeName(typeId);

    return q.lastInsertId().toInt();
}

QString LsbDb::unTypedef(const QString &name)
{
    QString ut = typedefCache.value(name);
    if (!ut.isEmpty())
        return ut;

    // swap "foo const" to "const foo"
    QString sname = name;
    sname.replace(QRegExp("(\\w+) const"), "const \\1");
    sname.replace(QRegExp("(\\w)\\*"), "\\1 *");

    return typedefCache.value(sname);
}

QString LsbDb::typeName(int typeId)
{
    return qSqlFetchValue<QString>("select Tname from Type where Tid = " + QString::number(typeId));
}

/* returns the default library group id for a library, or 0 if there's more than one
   group or in case of an error.
*/
int LsbDb::libraryGroupId(int libId)
{
    QSqlQuery q("select LGid from LibGroup where LGlib = " + QString::number(libId));
    if (!q.next())
        return 0;
    int gId = q.value(0).toInt();
    return q.next() ? 0 : gId;
}


// returns the size of a type per architecture
QMap<int, int> LsbDb::typeSize(int typeId)
{
    QMap<int, int> sizes;

    QSqlQuery q;
    if (!q.exec("select ATaid, ATsize from ArchType where ATtid = " + QString::number(typeId)))
        qWarning() << "unable to figure out size for type" << typeId << q.lastError();
    while (q.next())
        sizes[q.value(0).toInt()] = q.value(1).toInt();
    return sizes;
}

bool LsbDb::setTypeSize(int typeId, int arch, int size)
{
    QSqlQuery q;
    q.prepare("insert into ArchType (ATaid, ATtid, ATsize) values (?, ?, ?)");
    q.addBindValue(arch);
    q.addBindValue(typeId);
    q.addBindValue(size);
    if (!q.exec()) {
        qWarning() << "unable to insert into ArchType" << typeId << q.lastError();
        return false;
    }
    return true;
}

QHash<QString, QString> LsbDb::typeDefsForArch(int architecture)
{
    QHash<QString, QString> tdefs;

    QSqlQuery q;
    q.prepare("select A.TName, B.TName from Type A, Type B "
              "where A.Ttype = 'Typedef' and A.Tarch = ? "
              "and B.Tid = A.Tbasetype");
    q.addBindValue(architecture);
    if (!q.exec()) {
        qWarning() << "unable to retrieve typedefs for arch" << architecture << q.lastError();
        return tdefs;
    }
    while (q.next())
        tdefs[q.value(0).toString()] = q.value(1).toString();

    return tdefs;
}

int LsbDb::interfaceId(const QString &interfaceName)
{
    QSqlQuery q;
    q.prepare("select Iid from Interface where Iname = ?");
    q.addBindValue(interfaceName);

    if (!q.exec())
        qWarning() << "unable to retrieve interfaceId for" << interfaceName << q.lastError();
    if (!q.next())
        return 0;

    return q.value(0).toInt();
}

int LsbDb::moduleId(const QString &name)
{
    return qSqlFetchValue<int>(QString::fromLatin1("select Mid from Module where Mname = '%1'").arg(name));
}

int LsbDb::addModule(const QString &name, const QString &desc)
{
    QSqlQuery q;
    q.prepare("insert into Module (Mname, Mdesc) values (?, ?)");
    q.addBindValue(name);
    q.addBindValue(desc);
    if (!q.exec())
        qWarning() << "unable to add module" << q.lastError();

    return q.lastInsertId().toInt();
}

QHash<int, QVariant> LsbDb::fixArchInterface(const QSqlRecord &rec, const QString &name64)
{
    // 2 - i386
    // 3 - ia64
    // 6 - ppc32
    // 9 - ppc64
    // 10 - s390
    // 11 - x86-64
    // 12 - s390x

    QHash<int, QVariant> res;

    int arch32[3] = {6, 10};
    int arch64[4] = {3, 9, 11, 12};

    QSqlQuery ins;
    ins.prepare("update Interface set Iarch = 2 where Iid = ?");
    ins.addBindValue(rec.value("Iid"));
    if (!ins.exec())
        qWarning() << "unable to update Interface" << ins.lastError();

    ins.prepare("insert into Interface (Iname, Istatus, Itype, Istandard, Iarch, Iheader, "
                 "Ireturn) values (?, ?, ?, ?, ?, ?, ?)");

    for (int i = 0; i < 2; ++i) {
        ins.addBindValue(rec.value("Iname"));
        ins.addBindValue(rec.value("Istatus"));
        ins.addBindValue(rec.value("Itype"));
        ins.addBindValue(rec.value("Istandard"));
        ins.addBindValue(arch32[i]);
        ins.addBindValue(rec.value("Iheader"));
        ins.addBindValue(rec.value("Ireturn"));

        if (!ins.exec())
            qWarning() << "cannot insert into Interface" << ins.lastError();

        QVariant insId = ins.lastInsertId();
        res[arch32[i]] = insId;
        addLGInt(insId.toInt());
    }

    for (int i = 0; i < 4; ++i) {
        ins.addBindValue(name64);
        ins.addBindValue(rec.value("Istatus"));
        ins.addBindValue(rec.value("Itype"));
        ins.addBindValue(rec.value("Istandard"));
        ins.addBindValue(arch64[i]);
        ins.addBindValue(rec.value("Iheader"));

        if (!ins.exec())
            qWarning() << "cannot insert into Interface" << ins.lastError();

        QVariant insId = ins.lastInsertId();
        res[arch64[i]] = insId;
        addLGInt(insId.toInt());
    }

    return res;
}

static void fixArchVtable(const QHash<int, QVariant> archIds, const QSqlRecord &rec)
{
    QSqlQuery q;
    q.prepare("update Vtable set VTarch = 2 "
              "where VTcid = ? and VTvtpos = ? and VTviid = ? and VTarch = 1");

    q.addBindValue(rec.value("VTcid"));
    q.addBindValue(rec.value("VTvtpos"));
    q.addBindValue(rec.value("VTviid"));

    if (!q.exec())
        qWarning() << "unable to fix arch vtable" << q.lastError();

    q.prepare("insert into Vtable (VTcid, VTvtpos, VTpos, VTviid, VTarch) "
              "values (?, ?, ?, ?, ?)");

    for (QHash<int, QVariant>::const_iterator it = archIds.constBegin();
         it != archIds.constEnd(); ++it) {
        q.addBindValue(rec.value("VTcid"));
        q.addBindValue(rec.value("VTvtpos"));
        q.addBindValue(rec.value("VTpos"));
        q.addBindValue(it.value());
        q.addBindValue(it.key());

        if (!q.exec())
            qWarning() << "unable to fix arch vtable" << q.lastError();
    }
}

static void fixExistingArchMismatch()
{
    QSqlQuery ins;
    ins.prepare("insert into Vtable (VTcid, VTvtpos, VTpos, VTviid, VTarch) "
                "values (?, ?, ?, ?, ?)");

    QSqlQuery q;
    // these thunks were already in the database, fix them
    if (!q.exec("select VTcid, VTvtpos, VTpos, VTviid, VTarch, Iname from Vtable join Interface on VTviid = Iid where VTarch = 1 and Iarch = 2")) {
        qWarning() << "unable to fix mismatching interfaces" << q.lastError();
        return;
    }

    QSqlQuery syms;
    syms.prepare("select Iid, Iarch from Interface where Iname = ? and Iarch > 2");

    while (q.next()) {
        QSqlRecord rec = q.record();
        syms.addBindValue(rec.value("Iname"));
        if (!syms.exec())
            qWarning() << "unable to get arch-dependent interfaces" << q.lastError();
        while (syms.next()) {
            ins.addBindValue(rec.value("VTcid"));
            ins.addBindValue(rec.value("VTvtpos"));
            ins.addBindValue(rec.value("VTpos"));
            ins.addBindValue(syms.value(0));
            ins.addBindValue(syms.value(1));
            if (!ins.exec())
                qWarning() << "Unable to insert arch-dependent vtable entry" << ins.lastError();
        }
    }

    if (!q.exec("update Vtable set VTarch = 2 where VTarch = 1 and VTviid in (select Iid from Interface where Iarch = 2)")) {
        qWarning() << "Unable to fix arch-dependent vtable entries" << q.lastError();
    }
}

static void fixArchInt(int stdId)
{
    QSqlQuery q;
    q.prepare("select Iid, Iarch from Interface where Iarch != 1 and Iid not in (select AIint from ArchInt) and Istandard = ?");
    q.addBindValue(stdId);

    if (!q.exec())
        qWarning() << "Unable to get ArchIds" << q.lastError();

    QSqlQuery ins;
    ins.prepare("insert into ArchInt (AIarch, AIint) values (?, ?)");
    while (q.next()) {
        ins.addBindValue(q.value(1));
        ins.addBindValue(q.value(0));
        if (!ins.exec())
            qWarning() << "Unable to insert into ArchInt" << ins.lastError();
    }
}

void LsbDb::fixVirtualThunks()
{
    QSqlQuery q;
    if (!q.exec("select * from Interface where Iname like '_ZThn%' and Iarch = 1"))
        qWarning() << "unable to get interfaces" << q.lastError();

    QSqlQuery vtab;
    vtab.prepare("select VTcid, VTvtpos, VTpos, VTviid, VTarch from Vtable "
                 "where VTarch = 1 and VTviid = ?");

    QRegExp sizeRx("^_ZThn(\\d+)_");
    QRegExp thnRx("^(_ZThn\\d+_)");

    while (q.next()) {
        QSqlRecord rec = q.record();
        int iid = rec.value("Iid").toInt();
        Q_ASSERT(iid);

        QString val64 = rec.value("Iname").toString();
        if (sizeRx.indexIn(val64) == -1)
            qFatal("invalid symbol %s", qPrintable(val64));
        int size32 = sizeRx.cap(1).toInt();

        val64.replace(thnRx, "_ZThn" + QString::number(size32 * 2) + "_");

        QHash<int, QVariant> archIds = fixArchInterface(rec, val64);

        vtab.addBindValue(iid);
        if (!vtab.exec())
            qWarning() << "unable to get vtables" << q.lastError();

        while (vtab.next()) {
            fixArchVtable(archIds, vtab.record());
        }
    }

    fixExistingArchMismatch();
    fixArchInt(stdId);
}

void LsbDb::addArchDependendSizes(int tid, int size32, int size64)
{
    const int archIds[7] = {2, 3, 6, 9, 10, 11, 12};
    const int is64[7]    = {0, 1, 0, 1,  0,  1,  1};

    Q_ASSERT(tid);

    QSqlQuery q;
    q.prepare("insert into ArchType (ATaid, ATtid, ATsize) values (?, ?, ?)");

    for (int i = 0; i < 7; ++i) {
        q.addBindValue(archIds[i]);
        q.addBindValue(tid);
        q.addBindValue(is64[i] ? size64 : size32);
        if (!q.exec())
            qWarning("Unable to dump arch-dependend type size");
    }
}

