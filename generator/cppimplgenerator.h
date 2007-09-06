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
QString jni_signature(const AbstractMetaType *java_type, JNISignatureFormat format = Underscores);

class CppImplGenerator : public CppGenerator
{
    Q_OBJECT

public:
    CppImplGenerator(PriGenerator *pri)
    {
        priGenerator = pri;
    }

    virtual QString fileNameForClass(const AbstractMetaClass *cls) const;

    void write(QTextStream &s, const AbstractMetaClass *java_class);

    void writeExtraIncludes(QTextStream &s, const AbstractMetaClass *java_class);

    void writeAssignment(QTextStream &s, const QString &destName, const QString &srcName,
        const AbstractMetaType *java_type);
    void writeSignalInitialization(QTextStream &s, const AbstractMetaClass *java_class);
    void writeCodeInjections(QTextStream &s, const AbstractMetaFunction *java_function,
        const AbstractMetaClass *implementor, CodeSnip::Position position);
    void writeExtraFunctions(QTextStream &s, const AbstractMetaClass *java_class);
    void writeShellSignatures(QTextStream &s, const AbstractMetaClass *java_class);
    void writeShellConstructor(QTextStream &s, const AbstractMetaFunction *java_function);
    void writeShellDestructor(QTextStream &s, const AbstractMetaClass *java_class);
    void writeSignalFunction(QTextStream &s, const AbstractMetaFunction *java_function,
                             const AbstractMetaClass *implementor, int pos);
    void writeShellFunction(QTextStream &s, const AbstractMetaFunction *java_function,
                            const AbstractMetaClass *implementor, int pos);
    void writePublicFunctionOverride(QTextStream &s,
                                     const AbstractMetaFunction *java_function,
                                     const AbstractMetaClass *java_class);
    void writeVirtualFunctionOverride(QTextStream &s,
                                      const AbstractMetaFunction *java_function,
                                      const AbstractMetaClass *java_class);
    void writeBaseClassFunctionCall(QTextStream &s,
                                    const AbstractMetaFunction *java_function,
                                    const AbstractMetaClass *java_class,
                                    Option options = NoOption);
    void writeFinalDestructor(QTextStream &s, const AbstractMetaClass *cls);
    void writeFinalConstructor(QTextStream &s,
                               const AbstractMetaFunction *java_function,
                               const QString &qt_object_name,
                               const QString &java_object_name);
    void writeQObjectFunctions(QTextStream &s, const AbstractMetaClass *java_class);
    void writeFunctionCall(QTextStream &s,
                           const QString &variable_name,
                           const AbstractMetaFunction *java_function,
                           const QString &prefix = QString(),
                           Option option = NoOption,
                           const QStringList &extraParameters = QStringList());
    void writeFunctionCallArguments(QTextStream &s, const AbstractMetaFunction *java_function,
                                    const QString &prefix = QString(), Option option = NoOption);
    void writeFunctionName(QTextStream &s, const AbstractMetaFunction *java_function,
                           const AbstractMetaClass *java_class = 0);
    void writeJavaToQt(QTextStream &s,
                       const AbstractMetaClass *java_class,
                       const AbstractMetaType *function_return_type,
                       const QString &qt_name,
                       const QString &java_name,
                       const AbstractMetaFunction *java_function,
                       int argument_index);
    void writeJavaToQt(QTextStream &s,
                       const AbstractMetaType *java_type,
                       const QString &qt_name,
                       const QString &java_name,
                       const AbstractMetaFunction *java_function,
                       int argument_index,
                       Option option = OriginalName);

    void writeFinalFunction(QTextStream &s,
                            const AbstractMetaFunction *java_function,
                            const AbstractMetaClass *java_class);
    void writeFinalFunctionArguments(QTextStream &s,
                                     const AbstractMetaFunction *java_function,
                                     const QString &java_object_name);
    void writeFinalFunctionSetup(QTextStream &s,
                                 const AbstractMetaFunction *java_function,
                                 const QString &qt_object_name,
                                 const AbstractMetaClass *java_class);
    void writeOwnership(QTextStream &s,
                        const AbstractMetaFunction *java_function,
                        const QString &var_name,
                        int var_index,
                        const AbstractMetaClass *implementor);
    void writeQtToJava(QTextStream &s,
                       const AbstractMetaType *java_type,
                       const QString &qt_name,
                       const QString &java_name,
                       const AbstractMetaFunction *java_function,
                       int argument_index,
                       Option option = NoOption);

    bool writeConversionRule(QTextStream &s,
                             TypeSystem::Language target_language,
                             const AbstractMetaFunction *java_function,
                             int argument_index,
                             const QString &qt_name,
                             const QString &java_name);

    void writeFieldAccessors(QTextStream &s, const AbstractMetaField *java_field);

    void writeFromNativeFunction(QTextStream &s,
                                 const AbstractMetaClass *java_class);
    void writeFromArrayFunction(QTextStream &s, const AbstractMetaClass *java_class);
    void writeJavaLangObjectOverrideFunctions(QTextStream &s, const AbstractMetaClass *cls);

    void writeInterfaceCastFunction(QTextStream &s,
                                    const AbstractMetaClass *java_class,
                                    const AbstractMetaClass *interface);

    void writeQtToJavaContainer(QTextStream &s,
                                const AbstractMetaType *java_type,
                                const QString &qt_name,
                                const QString &java_name,
                                const AbstractMetaFunction *java_function,
                                int argument_index);
    void writeJavaToQtContainer(QTextStream &s,
                                const AbstractMetaType *java_type,
                                const QString &qt_name,
                                const QString &java_name,
                                const AbstractMetaFunction *java_function,
                                int argument_index);

    bool hasCustomDestructor(const AbstractMetaClass *java_class) const;

    QString translateType(const AbstractMetaType *java_type, Option option = NoOption) const;

private:
    QString fromObject(const TypeEntry *centry, const QString &var_name);


};

#endif // CPPIMPLGENERATOR_H
