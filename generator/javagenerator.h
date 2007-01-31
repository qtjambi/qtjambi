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

#ifndef JAVAGENERATOR_H
#define JAVAGENERATOR_H

#include "generator.h"
#include "metajava.h"

#include <QTextStream>

class DocParser;

class JavaGenerator : public Generator
{
    Q_OBJECT

public:
    JavaGenerator();

    QString translateType(const MetaJavaType *java_type, Option option = NoOption);

    void writeArgument(QTextStream &s, 
                       const MetaJavaFunction *java_function,
                       const MetaJavaArgument *java_argument,
                       uint options = 0);
    void writeEnum(QTextStream &s, const MetaJavaEnum *java_enum);
    void writeIntegerEnum(QTextStream &s, const MetaJavaEnum *java_enum);
    void writeSignal(QTextStream &s, const MetaJavaFunction *java_function);
    void writeFunction(QTextStream &s, const MetaJavaFunction *java_function,
                       uint included_attributes = 0, uint excluded_attributes = 0);
    void writeFieldAccessors(QTextStream &s, const MetaJavaField *field);
    void write(QTextStream &s, const MetaJavaClass *java_class);

    void writeFunctionOverloads(QTextStream &s, const MetaJavaFunction *java_function,
                                uint included_attributes, uint excluded_attributes);
    void writeEnumOverload(QTextStream &s, const MetaJavaFunction *java_function,
                           uint include_attributes, uint exclude_attributes);
    void writeExtraFunctions(QTextStream &s, const MetaJavaClass *java_class);
    void writeFunctionAttributes(QTextStream &s, const MetaJavaFunction *java_function,
                                 uint included_attributes = 0, uint excluded_attributes = 0,
                                 uint options = 0);
    void writeConstructorContents(QTextStream &s, const MetaJavaFunction *java_function);
    void writeFunctionArguments(QTextStream &s, const MetaJavaFunction *java_function,
        int count = -1, uint options = 0);
    void writeJavaCallThroughContents(QTextStream &s, const MetaJavaFunction *java_function);
    void writeOwnershipForContainer(QTextStream &s, TypeSystem::Ownership ownership, MetaJavaArgument *arg,
                                    const QString &indent);
    void writePrivateNativeFunction(QTextStream &s, const MetaJavaFunction *java_function);
    void writeJavaLangObjectOverrideFunctions(QTextStream &s, const MetaJavaClass *cls);
    bool hasDefaultConstructor(const MetaJavaType *type);

    void retrieveModifications(const MetaJavaFunction *f, const MetaJavaClass *java_class,
         uint *exclude_attributes, uint *include_attributes) const;
    QString functionSignature(const MetaJavaFunction *java_function,
                              uint included_attributes,
                              uint excluded_attributes,
                              Option option = NoOption,
                              int arg_count = -1);
    void setupForFunction(const MetaJavaFunction *java_function, 
       uint *included_attributes, uint *excluded_attributes) const;

    virtual QString subDirectoryForClass(const MetaJavaClass *java_class) const
    { return subDirectoryForPackage(java_class->package()); }

    virtual QString fileNameForClass(const MetaJavaClass *java_class) const;

#if 0
    void write1_dot_5_enum(QTextStream &s, const MetaJavaEnum *java_enum);
#endif

    bool shouldGenerate(const MetaJavaClass *java_class) const {
        return !java_class->typeEntry()->isContainer() && !java_class->typeEntry()->isVariant()
               && (java_class->typeEntry()->codeGeneration() & TypeEntry::GenerateJava);
    }

    QString documentationDirectory() const { return m_doc_directory; }
    void setDocumentationDirectory(const QString &docDir) { m_doc_directory = docDir; }

    bool documentationEnabled() const { return m_docs_enabled; }
    void setDocumentationEnabled(bool e) { m_docs_enabled = e; }
    void generate();

private:
    QString subDirectoryForPackage(const QString &package) const { return QString(package).replace(".", "/"); }

protected:
    QString m_package_name;
    QString m_doc_directory;
    DocParser *m_doc_parser;
    bool m_docs_enabled;
    QList<const MetaJavaFunction *> m_nativepointer_functions;
};

#endif // JAVAGENERATOR_H
