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

#ifndef METAINFOGENERATOR_H
#define METAINFOGENERATOR_H

#include "generator.h"

class MetaInfoGenerator : public Generator
{
public:
    enum GenerationFlags {
        GeneratedJavaClasses = 0x1,
        GeneratedMetaInfo = 0x2
    };

    enum OutputDirectoryType {
        CppDirectory,
        JavaDirectory
    };

    MetaInfoGenerator();

    virtual void generate();
    virtual QString fileNameForClass(const MetaJavaClass *java_class) const;
    virtual void write(QTextStream &s, const MetaJavaClass *java_class);

    void setFilenameStub(const QString &stub) { m_filenameStub = stub; }
    QString filenameStub() const { return m_filenameStub; }

    QString headerFilename() const { return filenameStub() + ".h"; }
    QString cppFilename() const { return filenameStub() + ".cpp"; }

    virtual QString subDirectoryForClass(const MetaJavaClass *, OutputDirectoryType type) const;
    virtual QString subDirectoryForPackage(const QString &package, OutputDirectoryType type) const;
    virtual bool shouldGenerate(const MetaJavaClass *) const;

    bool generated(const MetaJavaClass *cls) const;
    bool generatedJavaClasses(const QString &package) const;
    bool generatedMetaInfo(const QString &package) const;

private:
    void writeCppFile();
    void writeHeaderFile();
    void writeLibraryInitializers();
    void writeInclude(QTextStream &s, const Include &inc);
    void writeIncludeStatements(QTextStream &s, const MetaJavaClassList &classList, const QString &package);
    void writeInitializationFunctionName(QTextStream &s);
    void writeInitialization(QTextStream &s, const TypeEntry *entry, bool registerMetaType = true);
    void writeCustomStructors(QTextStream &s, const TypeEntry *entry);
    void writeCodeBlock(QTextStream &s, const QString &code);
    bool shouldGenerate(const TypeEntry *entry) const;
    void buildSkipList();

    QHash<QString, int> m_skip_list;
    QString m_filenameStub;

    QHash<OutputDirectoryType, QString> m_out_dir;
};

#endif // METAINFOGENERATOR_H
