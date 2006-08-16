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

#ifndef LSBDB_H
#define LSBDB_H

#include <QtCore/QtCore>

class QSqlRecord;

class LsbDb
{
public:
    LsbDb();

    bool connect();
    void disconnect();

    bool addLibrary(const QString &libName, const QString &soName,
                    const QString &standard = QLatin1String("Yes"));
    int addStandard(const QString &standardName, const QString &longDescription,
                    const QString &shortDescription, const QString &url);

    inline int currentLibraryId() const { return libId; }
    inline void setCurrentLibraryId(int lid) { libId = lid; }

    inline int currentStandardId() const { return stdId; }
    inline void setCurrentStandardId(int sid) { stdId = sid; }

    inline int currentLibraryGroupId() const { return libGroupId; }
    inline void setCurrentLibraryGroupId(int gid) { libGroupId = gid; }

    int getHeaderGroupId(const QString &header, const QString &headerGroupName = QString(),
                      int order = 0);
    int getHeaderId(const QString &header);
    int libraryId(const QString &libName);
    int standardId(const QString &name);
    int libraryGroupId(int libId);
    int moduleId(const QString &name);
    int addModule(const QString &name, const QString &desc);
    inline void setCurrentModuleId(int mid) { modId = mid; }

    int addClass(const QString &type, const QString &className, const QList<int> &baseTypeIds,
                 int vTableId, const QStringList &vTable, int typeInfoId,
                 const QString &baseVType,
                 const QString &header, const QString &headerGroupName = QString());
    bool addVtable(int classInfoId, const QStringList &vTable);
    int addMember(const QString &memberName, int typeId, int position, int memberOf);
    int addReadOnlyData(const QString &name, const QString &header);
    int addFunction(const QString &name, const QString &returnType, const QString &header,
                    int architecture, bool srcOnly = false);
    int addVariable(const QString &name, const QString &type, const QString &header);
    int addTypeInfo(const QString &name, bool data);
    int addEnum(const QString &name, const QString &header);
    int addEnumValue(const QString &name, int value, int parentId);
    int addTypedef(const QString &name, int typeId, const QString &header,
            const QString &headerGroupName = QString());
    QString unTypedef(const QString &name);

    int interfaceId(const QString &interfaceName);

    int getTypeId(const QString &typeName);
    int typeId(const QString &typeName);
    QString typeName(int typeId);

    bool initCppIntrinsics();

    QMap<int, int> typeSize(int typeId);
    bool setTypeSize(int typeId, int arch, int size);

    QHash<QString, QString> typeDefsForArch(int architecture);

    void fixVirtualThunks();
    void addArchDependendSizes(int tid, int size32, int size64);

private:
    void addTypeRedirection(const QString &, const QString &);
    void fillTypedefCache();
    int addLGInt(int iid);
    QHash<int, QVariant> fixArchInterface(const QSqlRecord &rec, const QString &name64);

    int libId;
    int libGroupId;
    int stdId;
    int modId;
    QHash<QString, int> typeCache;
    QHash<QString, int> headerCache;
    QHash<QString, QString> typedefCache;
};

#endif

