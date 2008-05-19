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
#include "javagenerator.h"
#include "cppgenerator.h"

class MetaInfoGenerator : public JavaGenerator
{
public:
    MetaInfoGenerator(PriGenerator *pri);

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
    virtual QString fileNameForClass(const AbstractMetaClass *java_class) const;
    virtual void write(QTextStream &s, const AbstractMetaClass *java_class);

    void setFilenameStub(const QString &stub) { m_filenameStub = stub; }
    QString filenameStub() const { return m_filenameStub; }

    QString headerFilename() const { return filenameStub() + ".h"; }
    QString cppFilename() const { return filenameStub() + ".cpp"; }

    virtual QString subDirectoryForClass(const AbstractMetaClass *, OutputDirectoryType type) const;
    virtual QString subDirectoryForPackage(const QString &package, OutputDirectoryType type) const;
    virtual bool shouldGenerate(const AbstractMetaClass *) const;

    bool generated(const AbstractMetaClass *cls) const;
    bool generatedJavaClasses(const QString &package) const;
    bool generatedMetaInfo(const QString &package) const;

private:
    void writeCppFile();
    void writeHeaderFile();
    void writeLibraryInitializers();
    void writeInclude(QTextStream &s, const Include &inc);
    void writeIncludeStatements(QTextStream &s, const AbstractMetaClassList &classList, const QString &package);
    void writeInitializationFunctionName(QTextStream &s, const QString &package, bool fullSignature);
    void writeInitialization(QTextStream &s, const TypeEntry *entry, const AbstractMetaClass *cls, bool registerMetaType = true);
    void writeCustomStructors(QTextStream &s, const TypeEntry *entry);
    void writeDestructors(QTextStream &s, const AbstractMetaClass *cls);
    void writeCodeBlock(QTextStream &s, const QString &code);
    void writeSignalsAndSlots(QTextStream &s, const QString &package);
    void writeEnums(QTextStream &s, const QString &package);
    void writeRegisterSignalsAndSlots(QTextStream &s);
    void writeRegisterEnums(QTextStream &s);
    QStringList writePolymorphicHandler(QTextStream &s, const QString &package, const AbstractMetaClassList &clss);
    bool shouldGenerate(const TypeEntry *entry) const;
    void buildSkipList();

#if defined(QTJAMBI_DEBUG_TOOLS)
    void writeNameLiteral(QTextStream &, const TypeEntry *, const QString &fileName);
#endif

    QHash<QString, int> m_skip_list;
    QString m_filenameStub;

    QHash<OutputDirectoryType, QString> m_out_dir;

    const AbstractMetaClass* lookupClassWithPublicDestructor(const AbstractMetaClass *cls);

    PriGenerator *priGenerator;
};

#endif // METAINFOGENERATOR_H
