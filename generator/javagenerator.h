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

class JavaGenerator : public Generator
{
    Q_OBJECT

public:
    JavaGenerator();

    QString translateType(const MetaJavaType *java_type, Option option = NoOption);

    void writeArgument(QTextStream &s, const MetaJavaVariable *java_variable,
                       uint options = 0);
    void writeEnum(QTextStream &s, const MetaJavaEnum *java_enum);
    void writeSignal(QTextStream &s, const MetaJavaFunction *java_function);
    void writeFunction(QTextStream &s, const MetaJavaFunction *java_function,
                       uint included_attributes = 0, uint excluded_attributes = 0);
    void writeFieldAccessors(QTextStream &s, const MetaJavaField *field);
    void write(QTextStream &s, const MetaJavaClass *java_class);

    void writeFunctionOverloads(QTextStream &s, const MetaJavaFunction *java_function,
                                uint included_attributes, uint excluded_attributes,
                                uint options);
    void writeExtraFunctions(QTextStream &s, const MetaJavaClass *java_class);
    void writeFunctionAttributes(QTextStream &s, const MetaJavaFunction *java_function,
                                 uint included_attributes = 0, uint excluded_attributes = 0,
                                 uint options = 0);
    void writeConstructorContents(QTextStream &s, const MetaJavaFunction *java_function,
                                  const QHash<int, bool> &);
    void writeFunctionArguments(QTextStream &s, const MetaJavaFunction *java_function,
        int count = -1, uint options = 0);
    void writeJavaCallThroughContents(QTextStream &s, const MetaJavaFunction *java_function,
                                      const QHash<int, bool> &);
    void writeDisableGCForContainer(QTextStream &s, MetaJavaArgument *arg,
                                    const QString &indent);
    void writePrivateNativeFunction(QTextStream &s, const MetaJavaFunction *java_function);

    bool hasDefaultConstructor(const MetaJavaType *type);

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

    virtual void generate();

private:
    QString subDirectoryForPackage(const QString &package) const { return QString(package).replace(".", "/"); }

protected:
    QString m_package_name;
};

#endif // JAVAGENERATOR_H
