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

#ifndef CPPIMPLGENERATOR_H
#define CPPIMPLGENERATOR_H

#include "cppgenerator.h"
#include "metajava.h"

enum JNISignatureFormat {
    Underscores,        // Used in the jni exported function names
    SlashesAndStuff     // Used for looking up functions through jni
};

QString jni_signature(const QString &full_name, JNISignatureFormat format);
QString jni_signature(const MetaJavaType *java_type, JNISignatureFormat format = Underscores);

class CppImplGenerator : public CppGenerator
{
    Q_OBJECT

public:
    virtual QString fileNameForClass(const MetaJavaClass *cls) const;

    void write(QTextStream &s, const MetaJavaClass *java_class);

    void writeExtraIncludes(QTextStream &s, const MetaJavaClass *java_class);

    void writeAssignment(QTextStream &s, const QString &destName, const QString &srcName,
        const MetaJavaType *java_type);
    void writeSignalInitialization(QTextStream &s, const MetaJavaClass *java_class);
    void writeCodeInjections(QTextStream &s, const MetaJavaFunction *java_function,
        const MetaJavaClass *implementor, CodeSnip::Position position);
    void writeExtraFunctions(QTextStream &s, const MetaJavaClass *java_class);
    void writeShellSignatures(QTextStream &s, const MetaJavaClass *java_class);
    void writeShellConstructor(QTextStream &s, const MetaJavaFunction *java_function);
    void writeShellDestructor(QTextStream &s, const MetaJavaClass *java_class);
    void writeSignalFunction(QTextStream &s, const MetaJavaFunction *java_function,
                             const MetaJavaClass *implementor, int pos);
    void writeShellFunction(QTextStream &s, const MetaJavaFunction *java_function,
                            const MetaJavaClass *implementor, int pos);
    void writePublicFunctionOverride(QTextStream &s,
                                     const MetaJavaFunction *java_function,
                                     const MetaJavaClass *java_class);
    void writeVirtualFunctionOverride(QTextStream &s,
                                      const MetaJavaFunction *java_function,
                                      const MetaJavaClass *java_class);
    void writeBaseClassFunctionCall(QTextStream &s,
                                    const MetaJavaFunction *java_function,
                                    const MetaJavaClass *java_class,
                                    Option options = NoOption);
    void writeFinalDestructor(QTextStream &s, const MetaJavaClass *cls);
    void writeFinalConstructor(QTextStream &s,
                               const MetaJavaFunction *java_function,
                               const QString &qt_object_name,
                               const QString &java_object_name);
    void writeQObjectFunctions(QTextStream &s, const MetaJavaClass *java_class);
    void writeFunctionCall(QTextStream &s,
                           const QString &variable_name,
                           const MetaJavaFunction *java_function,
                           const QString &prefix = QString(),
                           Option option = NoOption,
                           const QStringList &extraParameters = QStringList());
    void writeFunctionCallArguments(QTextStream &s, const MetaJavaFunction *java_function,
                                    const QString &prefix = QString(), Option option = NoOption);
    void writeFunctionName(QTextStream &s, const MetaJavaFunction *java_function,
                           const MetaJavaClass *java_class = 0);
    void writeJavaToQt(QTextStream &s,
                       const MetaJavaClass *java_class,
                       const MetaJavaType *function_return_type,
                       const QString &qt_name,
                       const QString &java_name,
                       const MetaJavaFunction *java_function,
                       int argument_index);
    void writeJavaToQt(QTextStream &s,
                       const MetaJavaType *java_type,
                       const QString &qt_name,
                       const QString &java_name,
                       const MetaJavaFunction *java_function,
                       int argument_index,
                       Option option = OriginalName);

    void writeFinalFunction(QTextStream &s,
                            const MetaJavaFunction *java_function,
                            const MetaJavaClass *java_class);
    void writeFinalFunctionArguments(QTextStream &s,
                                     const MetaJavaFunction *java_function,
                                     const QString &java_object_name);
    void writeFinalFunctionSetup(QTextStream &s,
                                 const MetaJavaFunction *java_function,
                                 const QString &qt_object_name,
                                 const MetaJavaClass *java_class);
    void writeOwnership(QTextStream &s,
                        const MetaJavaFunction *java_function,
                        const QString &var_name,
                        int var_index,
                        const MetaJavaClass *implementor);
    void writeQtToJava(QTextStream &s,
                       const MetaJavaType *java_type,
                       const QString &qt_name,
                       const QString &java_name,
                       const MetaJavaFunction *java_function,
                       int argument_index,
                       Option option = NoOption);

    bool writeConversionRule(QTextStream &s,
                             TypeSystem::Language target_language,
                             const MetaJavaFunction *java_function,
                             int argument_index,
                             const QString &qt_name,
                             const QString &java_name);

    void writeFieldAccessors(QTextStream &s, const MetaJavaField *java_field);

    void writeFromNativeFunction(QTextStream &s,
                                 const MetaJavaClass *java_class);
    void writeFromArrayFunction(QTextStream &s, const MetaJavaClass *java_class);
    void writeJavaLangObjectOverrideFunctions(QTextStream &s, const MetaJavaClass *cls);

    void writeInterfaceCastFunction(QTextStream &s,
                                    const MetaJavaClass *java_class,
                                    const MetaJavaClass *interface);

    void writeQtToJavaContainer(QTextStream &s,
                                const MetaJavaType *java_type,
                                const QString &qt_name,
                                const QString &java_name,
                                const MetaJavaFunction *java_function,
                                int argument_index);
    void writeJavaToQtContainer(QTextStream &s,
                                const MetaJavaType *java_type,
                                const QString &qt_name,
                                const QString &java_name,
                                const MetaJavaFunction *java_function,
                                int argument_index);

    bool hasCustomDestructor(const MetaJavaClass *java_class) const;

    QString translateType(const MetaJavaType *java_type, Option option = NoOption) const;

private:
    QString fromObject(const TypeEntry *centry, const QString &var_name);


};

#endif // CPPIMPLGENERATOR_H
