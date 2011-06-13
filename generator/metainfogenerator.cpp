/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
**
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
**
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
**
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#include "metainfogenerator.h"
#include "reporthandler.h"
#include "cppimplgenerator.h"
#include "fileout.h"

#include <QDir>
#include <QMetaType>
#include "typesystem/typedatabase.h"

MetaInfoGenerator::MetaInfoGenerator(PriGenerator *pri):
        JavaGenerator(),
        priGenerator(pri)

{
    setFilenameStub("metainfo");
}

QString MetaInfoGenerator::subDirectoryForPackage(const QString &package, OutputDirectoryType type) const {
    switch (type) {
        case CppDirectory:
            return "cpp/" + QString(package).replace(".", "_") + "/";
        case JavaDirectory:
            return QString(package).replace(".", "/");
        default:
            return QString(); // kill nonsense warnings
    }
}

QString MetaInfoGenerator::subDirectoryForClass(const AbstractMetaClass *cls, OutputDirectoryType type) const {
    Q_ASSERT(cls);
    return subDirectoryForPackage(cls->package(), type);
}

void MetaInfoGenerator::generate() {
    buildSkipList();
    writeCppFile();
    writeHeaderFile();
    writeLibraryInitializers();
}

bool MetaInfoGenerator::shouldGenerate(const TypeEntry *entry) const {
    return entry != 0 && !entry->isNamespace() && !entry->isEnum() && (entry->codeGeneration() & TypeEntry::GenerateCpp);
}

bool MetaInfoGenerator::shouldGenerate(const AbstractMetaClass *cls) const {
    return (!cls->isInterface() && cls->typeEntry()->isValue() && !cls->isNamespace()
            && !cls->isAbstract() && (cls->typeEntry()->codeGeneration() & TypeEntry::GenerateCpp));
}

QString MetaInfoGenerator::fileNameForClass(const AbstractMetaClass *) const {
    return filenameStub() + ".cpp";
}

void MetaInfoGenerator::write(QTextStream &, const AbstractMetaClass *) {
    // not used
}

bool MetaInfoGenerator::generated(const AbstractMetaClass *cls) const {
    return generatedMetaInfo(cls->package());
}

bool MetaInfoGenerator::generatedMetaInfo(const QString &package) const {
    return (m_skip_list.value(package, 0x0) & GeneratedMetaInfo);
}

bool MetaInfoGenerator::generatedJavaClasses(const QString &package) const {
    return (m_skip_list.value(package, 0x0) & GeneratedJavaClasses);
}

static void metainfo_write_name_list(QTextStream &s, const char *var_name, const QList<QString> &strs,
                                     int offset, int skip) {
    s << "static const char *" << var_name << "[] = {" << endl;
    for (int i = offset; i < strs.size(); i += skip) {
        s << "    \"" << strs.at(i).toLatin1() << "\"";
        if (i < strs.size() - 1)
            s << ",";
        s << endl;
    }
    s << "};" << endl << endl;
}

void MetaInfoGenerator::writeEnums(QTextStream &s, const QString &package) {
    TypeEntryHash entries = TypeDatabase::instance()->allEntries();
    TypeEntryHash::iterator it;

    QList<QString> strs;
    for (it = entries.begin(); it != entries.end(); ++it) {
        QList<TypeEntry *> entries = it.value();
        foreach(TypeEntry *entry, entries) {
            if ((entry->isFlags() || entry->isEnum()) && entry->javaPackage() == package) {
                EnumTypeEntry *eentry = entry->isEnum() ? static_cast<EnumTypeEntry *>(entry) : static_cast<FlagsTypeEntry *>(entry)->originator();

                // The Qt flags names should map to the enum names, this is
                // required for the designer plugin to find the enum type of
                // a flags type since this functionality is not available in
                // Qt. This may be a little bit inconsistent, but it saves
                // us making yet another hash table for lookups. If it causes
                // problems, make a new one for this particular purpose.
                strs.append((eentry->javaPackage().isEmpty() ? QString() : eentry->javaPackage().replace('.', '/')  + "/")
                            + eentry->javaQualifier() + "$" + eentry->targetLangName());
                strs.append(entry->isFlags() ? static_cast<FlagsTypeEntry *>(entry)->originalName() : entry->qualifiedCppName());
            }
        }
    }

    Q_ASSERT(strs.size() % 2 == 0);

    s << "static int enum_count = " << (strs.size() / 2) << ";" << endl;
    if (strs.size() > 0) {
        metainfo_write_name_list(s, "enumJavaNames", strs, 0, 2);
        metainfo_write_name_list(s, "enumCppNames", strs, 1, 2);
    } else {
        s << "static const char **enumCppNames = 0;" << endl
        << "static const char **enumJavaNames = 0;" << endl;
    }
}

void MetaInfoGenerator::writeSignalsAndSlots(QTextStream &s, const QString &package) {
    AbstractMetaClassList classes = this->classes();

    QList<QString> strs;
    foreach(AbstractMetaClass *cls, classes) {
        if (cls->package() == package) {
            AbstractMetaFunctionList functions = cls->functions();
            foreach(AbstractMetaFunction *f, functions) {
                if (f->implementingClass() == cls && (f->isSignal() || f->isSlot())) {

                    AbstractMetaArgumentList arguments = f->arguments();
                    int numOverloads = arguments.size();
                    for (int i = arguments.size() - 1; i >= 0; --i) {
                        if (arguments.at(i)->defaultValueExpression().isEmpty()) {
                            numOverloads = arguments.size() - i - 1;
                            break;
                        }
                    }

                    for (int i = 0; i <= numOverloads; ++i) {
                        Option option = Option(SkipAttributes | SkipReturnType | SkipName);
                        QString qtName;
                        {

                            QTextStream qtNameStream(&qtName);
                            CppGenerator::writeFunctionSignature(qtNameStream, f, 0, QString(),
                                                                 Option(option | OriginalName | NormalizeAndFixTypeSignature | OriginalTypeDescription),
                                                                 QString(), QStringList(), arguments.size() - i);
                        }
                        qtName = f->implementingClass()->qualifiedCppName() + "::" + qtName;
                        qtName = QMetaObject::normalizedSignature(qtName.toLatin1().constData());

                        QString javaFunctionName = functionSignature(f, 0, 0, option, arguments.size() - (f->isSignal() ? 0 : i));
                        QString javaObjectName = f->isSignal()
                                                 ? f->name()
                                                 : javaFunctionName;

                        javaFunctionName = f->implementingClass()->fullName() + "." + javaFunctionName;
                        javaObjectName   = f->implementingClass()->fullName() + "." + javaObjectName;

                        QString javaSignature = "(";
                        for (int j = 0; j < (arguments.size() - (f->isSignal() ? 0 : i)); ++j)  {
                            AbstractMetaArgument *arg = arguments.at(j);
                            javaSignature += jni_signature(arg->type(), SlashesAndStuff);
                        }
                        javaSignature += ")" + jni_signature(f->type(), SlashesAndStuff);

                        strs.append(qtName);
                        strs.append(javaFunctionName);
                        strs.append(javaObjectName);
                        strs.append(javaSignature);
                    }
                }
            }
        }
    }

    Q_ASSERT(strs.size() % 4 == 0);

    s << "static int sns_count = " << (strs.size() / 4) << ";" << endl;
    if (strs.size() > 0) {
        metainfo_write_name_list(s, "qtNames", strs, 0, 4);
        metainfo_write_name_list(s, "javaFunctionNames", strs, 1, 4);
        metainfo_write_name_list(s, "javaObjectNames", strs, 2, 4);
        metainfo_write_name_list(s, "javaSignatures", strs, 3, 4);
    } else {
        s << "static const char **qtNames = 0;" << endl
        << "static const char **javaFunctionNames = 0;" << endl
        << "static const char **javaObjectNames = 0;" << endl
        << "static const char **javaSignatures = 0;" << endl;
    }
}

void MetaInfoGenerator::writeRegisterSignalsAndSlots(QTextStream &s) {
    s << "    for (int i=0;i<sns_count; ++i) {" << endl
    << "        registerQtToJava(qtNames[i], javaFunctionNames[i]);" << endl
    << "        if (getQtName(javaObjectNames[i]).length() < QByteArray(qtNames[i]).size())" << endl
    << "            registerJavaToQt(javaObjectNames[i], qtNames[i]);" << endl
    << "        registerJavaSignature(qtNames[i], javaSignatures[i]);" << endl
    << "    }" << endl;
}

void MetaInfoGenerator::writeRegisterEnums(QTextStream &s) {
    s << "    for (int i=0;i<enum_count; ++i) {" << endl
    << "        registerQtToJava(enumCppNames[i], enumJavaNames[i]);" << endl
    << "        registerJavaToQt(enumJavaNames[i], enumCppNames[i]);" << endl
    << "    }" << endl;
}

/**
 * Builds a skip list of classes that shouldn't be built.
 */
void MetaInfoGenerator::buildSkipList() {
    AbstractMetaClassList classList = classes();
    foreach(AbstractMetaClass *cls, classList) {
        if (!m_skip_list.contains(cls->package()))
            m_skip_list[cls->package()] = 0x0;

        if (cls->typeEntry()->codeGeneration() & TypeEntry::GenerateCpp)
            m_skip_list[cls->package()] |= GeneratedMetaInfo;

        if (cls->typeEntry()->codeGeneration() & TypeEntry::GenerateTargetLang)
            m_skip_list[cls->package()] |= GeneratedJavaClasses;
    }
}

QStringList MetaInfoGenerator::writePolymorphicHandler(QTextStream &s, const QString &package,
        const AbstractMetaClassList &classes) {
    QStringList handlers;
    foreach(AbstractMetaClass *cls, classes) {
        const ComplexTypeEntry *centry = cls->typeEntry();
        if (!centry->isPolymorphicBase())
            continue;

        AbstractMetaClassList classList = this->classes();
        bool first = true;
        foreach(AbstractMetaClass *clazz, classList) {
            if (clazz->package() == package && clazz->inheritsFrom(cls)) {
                if (!clazz->typeEntry()->polymorphicIdValue().isEmpty()) {
                    // On first find, open the function
                    if (first) {
                        first = false;

                        QString handler = jni_signature(cls->fullName(), Underscores);
                        handlers.append(handler);

                        s << "static bool polymorphichandler_" << handler
                        << "(const void *ptr, char **class_name, char **package)" << endl
                        << "{" << endl
                        << "    Q_ASSERT(ptr != 0);" << endl
                        << "    " << cls->qualifiedCppName() << " *object = ("
                        << cls->qualifiedCppName() << " *)ptr;" << endl;
                    }

                    // For each, add case label
                    s << "    if ("
                    << clazz->typeEntry()->polymorphicIdValue().replace("%1", "object")
                    << ") {" << endl
                    << "        *class_name = \"" << clazz->name() << "\";" << endl
                    << "        *package    = \"" << clazz->package().replace(".", "/") << "/\";" << endl
                    << "        return true;" << endl
                    << "    }" << endl;
                } else {
                    QString warning = QString("class '%1' inherits from polymorphic class '%2', but has no polymorphic id set")
                                      .arg(clazz->name())
                                      .arg(cls->name());

                    ReportHandler::warning(warning);
                }
            }
        }

        // Close the function if it has been opened
        if (!first) {
            s << "    return false;" << endl
            << "}" << endl;
        }
    }

    return handlers;
}

#if defined(QTJAMBI_DEBUG_TOOLS)
void MetaInfoGenerator::writeNameLiteral(QTextStream &s, const TypeEntry *entry, const QString &fileName) {
    static QSet<QString> used;

    if (!used.contains(fileName + ":" + entry->name())) {
        s << "char __name_" << QString(entry->name()).replace(':', '_').replace(' ', '_') << "[] = \"" << entry->name() << "\";" << endl;
        used.insert(fileName + ":" + entry->name());
    }
}
#endif

void MetaInfoGenerator::writeCppFile() {
    TypeEntryHash entries = TypeDatabase::instance()->allEntries();
    TypeEntryHash::iterator it;

    AbstractMetaClassList classes_with_polymorphic_id;
    AbstractMetaClassList classList = classes();
    QHash<QString, FileOut *> fileHash;

    // Seems continue is not supported by our foreach loop, so
    foreach(AbstractMetaClass *cls, classList) {

        FileOut *f = fileHash.value(cls->package(), 0);
        if (f == 0 && generated(cls)) {
            f = new FileOut(cppOutputDirectory() + "/" + subDirectoryForClass(cls, CppDirectory) + "/" + cppFilename());

            writeIncludeStatements(f->stream, classList, cls->package());
            f->stream << endl;

#if defined(QTJAMBI_DEBUG_TOOLS)
            // Write the generic destructors and constructors
            f->stream << "template <typename T, const char *NAME>" << endl
            << "void genericDestructor(void *t)" << endl
            << "{" << endl
            << "    delete (T *) t;" << endl
            << "    qtjambi_increase_destructorFunctionCalledCount(QString::fromLatin1(NAME));" << endl
            << "}" << endl << endl
            << "template <typename T>" << endl
            << "void *genericConstructor(const void *t)" << endl
            << "{" << endl
            << "    if (!t)" << endl
            << "        return new T;" << endl
            << "    return new T(*reinterpret_cast<const T *>(t));" << endl
            << "}" << endl;
#endif


            fileHash.insert(cls->package(), f);

            QString pro_file_name = cls->package().replace(".", "_") + "/" + cls->package().replace(".", "_") + ".pri";
            priGenerator->addSource(pro_file_name, cppFilename());
        }

        if (!(cls->attributes() & AbstractMetaAttributes::Fake)) {
            if (f != 0) {
                if (cls->typeEntry()->isObject()
                        && !cls->typeEntry()->isQObject()
                        && !cls->isInterface()) {
                    writeDestructors(f->stream, cls);
                }
                writeCustomStructors(f->stream, cls->typeEntry());
            }

            if (cls->typeEntry()->isPolymorphicBase())
                classes_with_polymorphic_id.append(cls);
        }

#if defined(QTJAMBI_DEBUG_TOOLS)
        if (cls->typeEntry()->isValue() && shouldGenerate(cls->typeEntry()))
            writeNameLiteral(f->stream, cls->typeEntry(), f->name());
#endif
    }

    QHash<QString, QStringList> handlers_to_register;
    foreach(QString package, fileHash.keys()) {
        FileOut *f = fileHash.value(package, 0);
        if (f != 0) {
            writeSignalsAndSlots(f->stream, package);
            writeEnums(f->stream, package);
            handlers_to_register[package] = writePolymorphicHandler(f->stream, package, classes_with_polymorphic_id);
        }
    }

    // Primitive types must be added to all packages, in case the other packages are
    // not referenced from the generated code.
    foreach(FileOut *f, fileHash.values()) {
        for (it = entries.begin(); it != entries.end(); ++it) {
            QList<TypeEntry *> entries = it.value();
            foreach(TypeEntry *entry, entries) {
                if (shouldGenerate(entry) && entry->isPrimitive()) {
                    writeCustomStructors(f->stream, entry);
#if defined(QTJAMBI_DEBUG_TOOLS)
                    writeNameLiteral(f->stream, entry, f->name());
#endif
                }
            }
        }

        // Initialization function: Registers meta types
        writeInitializationFunctionName(f->stream, fileHash.key(f, ""), true);
        f->stream << endl << "{" << endl;
        for (it = entries.begin(); it != entries.end(); ++it) {
            QList<TypeEntry *> entries = it.value();
            foreach(TypeEntry *entry, entries) {
                if (entry &&
                        ((shouldGenerate(entry) && entry->isPrimitive())
                         || entry->isString()
                         || entry->isChar())) {
                    writeInitialization(f->stream, entry, 0);
                }
            }
        }
        writeRegisterSignalsAndSlots(f->stream);
        writeRegisterEnums(f->stream);
    }

    foreach(AbstractMetaClass *cls, classList) {
        FileOut *f = fileHash.value(cls->package(), 0);

        if (f != 0) {
            writeInitialization(f->stream, cls->typeEntry(), cls, shouldGenerate(cls));
        }
    }

    foreach(QString package, fileHash.keys()) {
        FileOut *f = fileHash.value(package, 0);
        if (f != 0) {
            foreach(QString handler, handlers_to_register.value(package, QStringList())) {
                f->stream << "    qtjambi_register_polymorphic_id(\"" << handler << "\","
                << "polymorphichandler_" << handler << ");" << endl;
            }

            f->stream << "}" << endl << endl;
            if (f->done())
                ++m_num_generated_written;
            ++m_num_generated;

            delete f;
        }
    }
}

void MetaInfoGenerator::writeHeaderFile() {
    AbstractMetaClassList classList = classes();
    QHash<QString, bool> fileHash;

    foreach(AbstractMetaClass *cls, classList) {
        bool hasGenerated = fileHash.value(cls->package(), false);
        if (!hasGenerated && generated(cls)) {
            FileOut file(cppOutputDirectory() + "/" + subDirectoryForClass(cls, CppDirectory) + "/" + headerFilename());
            file.stream << "#ifndef " << filenameStub().toUpper() << "_H" << endl;
            file.stream << "#define " << filenameStub().toUpper() << "_H" << endl << endl;
            writeInitializationFunctionName(file.stream, cls->package(), true);
            file.stream << ";" << endl << "#endif" << endl << endl;

            fileHash.insert(cls->package(), true);

            QString pro_file_name = cls->package().replace(".", "_") + "/" + cls->package().replace(".", "_") + ".pri";
            priGenerator->addHeader(pro_file_name, headerFilename());

            if (file.done())
                ++m_num_generated_written;
            ++m_num_generated;
        }
    }
}

void MetaInfoGenerator::writeCodeBlock(QTextStream &s, const QString &code) {
    QStringList lines = code.split('\n');
    QString indent;
    foreach(QString str, lines) {
        s << "    " << indent << str.trimmed() << endl;
        if (!str.trimmed().endsWith(";") && !str.trimmed().isEmpty())
            indent = "    ";
        else
            indent = "";
    }
}

const AbstractMetaClass* MetaInfoGenerator::lookupClassWithPublicDestructor(const AbstractMetaClass *cls) {
    while (cls != 0) {
        if (cls->hasPublicDestructor()) {
            return cls;
        } else {
            cls = cls->baseClass();
        }
    }
    return 0;
}

void MetaInfoGenerator::writeDestructors(QTextStream &s, const AbstractMetaClass *cls) {
    // We can only delete classes with public destructors
    const AbstractMetaClass *clsWithPublicDestructor = lookupClassWithPublicDestructor(cls);
    if (clsWithPublicDestructor != 0) {
        const ComplexTypeEntry *entry = cls->typeEntry();
        if ((entry->codeGeneration() & TypeEntry::GenerateCode) != 0) {
            s   << "void destructor_" << entry->javaPackage().replace(".", "_")  << "_"
            << entry->lookupName().replace(".", "_").replace("$", "_") << "(void *ptr)" << endl
            << "{" << endl
            << "    delete reinterpret_cast<" << clsWithPublicDestructor->qualifiedCppName() << " *>(ptr);" << endl;

#if defined(QTJAMBI_DEBUG_TOOLS)
            s   << "    qtjambi_increase_destructorFunctionCalledCount(QString::fromLatin1(\"" << cls->name() << "\"));" << endl;
#endif

            s   << "}" << endl << endl;
        }
    }
}

void MetaInfoGenerator::writeCustomStructors(QTextStream &s, const TypeEntry *entry) {
    if (!entry->preferredConversion())
        return ;

    CustomFunction customConstructor = entry->customConstructor();
    CustomFunction customDestructor = entry->customDestructor();

    if (!customConstructor.name.isEmpty() && !customDestructor.name.isEmpty()) {
        s << "// Custom constructor and destructor for " << entry->qualifiedCppName() << endl
        << "static void *" << customConstructor.name << "("
        << "const " << entry->qualifiedCppName() << " *" << customConstructor.param_name
        << ")" << endl
        << "{" << endl;
        writeCodeBlock(s, customConstructor.code());
        s << "}" << endl << endl;

        s << "static void " << customDestructor.name << "("
        << "const " << entry->qualifiedCppName() << " *" << customDestructor.param_name
        << ")" << endl
        << "{" << endl;
        writeCodeBlock(s, customDestructor.code());
        s << "}" << endl << endl;
    }
}

static void generateInitializer(QTextStream &s, const QString &package, CodeSnip::Position pos) {
    QList<CodeSnip> snips =
        ((TypeSystemTypeEntry *) TypeDatabase::instance()->findType(package))->snips;

    foreach(const CodeSnip &snip, snips)
    if (snip.position == pos)
        s << snip.code();
}

void MetaInfoGenerator::writeLibraryInitializers() {
    // from cppimplgenerator.cpp
    extern QString jni_function_signature(QString package,
                                              QString class_name,
                                              const QString &function_name,
                                              const QString &return_type,
                                              const QString &mangled_arguments = QString(),
                                              uint options = CppImplGenerator::StandardJNISignature);

    // We need to generate a library initializer in Java for all packages
    // that have generated classes in Java, and in C++ for all packages
    // that have generated metainfo.

    QList<QString> known_packages = m_skip_list.keys();
    foreach(QString package, known_packages) {
        if (generatedMetaInfo(package)) { // write cpp file

            FileOut fileOut(cppOutputDirectory() + "/" + subDirectoryForPackage(package, CppDirectory) + "/qtjambi_libraryinitializer.cpp");

            QString signature = jni_function_signature(package, "QtJambi_LibraryInitializer",
                                "__qt_initLibrary", "void");
            QTextStream &s = fileOut.stream;
            s << "#include \"metainfo.h\"" << endl
            << "#include \"qtjambi_global.h\"" << endl << endl
            << signature << "(JNIEnv *, jclass)" << endl
            << "{" << endl
            << "    ";
            writeInitializationFunctionName(s, package, false);
            s << ";" << endl
            << "}" << endl << endl;

            QString pro_file_name = QString(package).replace(".", "_");

            priGenerator->addSource(pro_file_name + "/" + pro_file_name + ".pri", "qtjambi_libraryinitializer.cpp");

            if (fileOut.done())
                ++m_num_generated_written;
            ++m_num_generated;
        }

        if (generatedJavaClasses(package)) {

            FileOut fileOut(javaOutputDirectory() + "/" + subDirectoryForPackage(package, JavaDirectory) + "/QtJambi_LibraryInitializer.java");

            QTextStream &s = fileOut.stream;
            s << "package " << package << ";" << endl << endl
            << "class QtJambi_LibraryInitializer" << endl
            << "{" << endl
            << "    static {" << endl;

            generateInitializer(s, package, CodeSnip::Beginning);

            s << "        com.trolltech.qt.Utilities.loadJambiLibrary(\""
            << QString(package).replace(".", "_") << "\");" << endl;

            if (generatedMetaInfo(package))
                s << "        __qt_initLibrary();" << endl;

            generateInitializer(s, package, CodeSnip::End);

            s << "    }" << endl;

            if (generatedMetaInfo(package))
                s << "    private native static void __qt_initLibrary();" << endl;

            s << "    static void init() { };" << endl
            << "}" << endl << endl;

            if (fileOut.done())
                ++m_num_generated_written;
            ++m_num_generated;
        }
    }
}

void MetaInfoGenerator::writeInclude(QTextStream &s, const Include &inc) {
    if (inc.name.isEmpty())
        return;

    s << "#include ";
    if (inc.type == Include::LocalPath)
        s << "\"" << inc.name << "\"";
    else
        s << "<" << inc.name << ">";
    s << endl;
}

void MetaInfoGenerator::writeIncludeStatements(QTextStream &s, const AbstractMetaClassList &classList,
        const QString &package) {
    writeInclude(s, Include(Include::LocalPath, headerFilename()));
    writeInclude(s, Include(Include::IncludePath, "QMetaType"));
    writeInclude(s, Include(Include::IncludePath, "QString"));
    writeInclude(s, Include(Include::IncludePath, "QLatin1String"));
    writeInclude(s, Include(Include::IncludePath, "QHash"));
    writeInclude(s, Include(Include::IncludePath, "QReadWriteLock"));
    writeInclude(s, Include(Include::IncludePath, "QReadLocker"));
    writeInclude(s, Include(Include::IncludePath, "QWriteLocker"));
    writeInclude(s, Include(Include::IncludePath, "qtjambi_cache.h"));
    writeInclude(s, Include(Include::IncludePath, "qtjambi_core.h"));

#if defined(QTJAMBI_DEBUG_TOOLS)
    writeInclude(s, Include(Include::IncludePath, "qtjambidebugtools_p.h"));
#endif

    s << endl;

    foreach(AbstractMetaClass *cls, classList) {
        if (generated(cls) && !cls->isInterface() && cls->package() == package) {
            const ComplexTypeEntry *ctype = cls->typeEntry();

            Include inc = ctype->include();
            writeInclude(s, inc);
        }
    }
}

void MetaInfoGenerator::writeInitializationFunctionName(QTextStream &s, const QString &package, bool fullSignature) {
    if (fullSignature)
        s << "void ";
    s << "__metainfo_init_" << QString(package).replace(".", "_") << "()";
}

void MetaInfoGenerator::writeInitialization(QTextStream &s, const TypeEntry *entry, const AbstractMetaClass *cls,
        bool registerMetaType) {
    if (entry->codeGeneration() == TypeEntry::GenerateForSubclass)
        return;

    if (cls && cls->attributes() & AbstractMetaAttributes::Fake)
        return;


    QString constructorName = entry->customConstructor().name;
    QString destructorName = entry->customDestructor().name;
#if defined(QTJAMBI_DEBUG_TOOLS)

    if (constructorName.isEmpty())
        constructorName = "genericConstructor<" + entry->qualifiedCppName() + ">";

    if (destructorName.isEmpty())
        destructorName = "genericDestructor<" + entry->qualifiedCppName() + ", __name_" + entry->name() + ">";

#endif


    if (constructorName.isEmpty() != destructorName.isEmpty()) {
        ReportHandler::warning(QString("specify either no custom functions, or both "
                                       "constructor and destructor for type '%1'").arg(entry->name()));
    }

    QString javaPackage = entry->javaPackage();

    QString javaName =  entry->lookupName();
    if (!javaPackage.isEmpty()) {
        javaName.prepend(javaPackage.replace(".", "/") + "/");
    }


    if (entry->isComplex()) {
        const ComplexTypeEntry *centry = static_cast<const ComplexTypeEntry *>(entry);
        if (centry->typeFlags() & ComplexTypeEntry::DeleteInMainThread)
            s << "    registerDeletionPolicy(\"" << javaName << "\", DeletionPolicyDeleteInMainThread);" << endl;
    }

    QString qtName = entry->qualifiedCppName();
    if ((!entry->isInterface())
            && (!entry->isPrimitive() || ((PrimitiveTypeEntry *) entry)->preferredTargetLangType()))
        s << "    registerQtToJava(\"" << qtName << "\", \"" << javaName << "\");" << endl;

    if (!entry->preferredConversion())
        return ;

    s << "    registerJavaToQt(\"" << javaName << "\", \"" << qtName << "\");" << endl;
    if (entry->isComplex() && entry->isObject() && !((ComplexTypeEntry *)entry)->isQObject() && !entry->isInterface()) {
        QString patchedName = QString(javaName).replace("/", "_").replace("$", "_");

        if (lookupClassWithPublicDestructor(cls))
            s << "    registerDestructor(\"" << javaName << "\", destructor_" << patchedName << ");" << endl;
    }

    if (!registerMetaType)
        return ;

    int metaType = QMetaType::type(entry->name().toLocal8Bit().constData());
    if (metaType != QMetaType::Void)
        return ;


    if (!constructorName.isEmpty() && !destructorName.isEmpty()) {
        s << "    QMetaType::registerType(\"" << entry->qualifiedCppName() << "\"," << endl
        << "                            reinterpret_cast<QMetaType::Destructor>("
        << destructorName
        << ")," << endl
        << "                            reinterpret_cast<QMetaType::Constructor>("
        << constructorName
        << "));" << endl;
    } else {
        // Look for default constructor, required for qRegisterMetaType
        if (cls != 0) {
            AbstractMetaFunctionList functions = cls->queryFunctions(AbstractMetaClass::WasPublic | AbstractMetaClass::Constructors);

            bool hasDefaultConstructor = false;
            foreach(AbstractMetaFunction *function, functions) {
                // Default constructor has to be present
                if (function->wasPublic() && function->actualMinimumArgumentCount() == 0)
                    hasDefaultConstructor = true;
            }

            if (!hasDefaultConstructor) {
                ReportHandler::warning(QString("Value type '%1' is missing a default constructor. "
                                               "The resulting C++ code will not compile. If necessary, use <custom-constructor> and "
                                               "<custom-destructor> tags to provide the constructors.").arg(cls->fullName()));
            }

        }
        s << "    qRegisterMetaType<" << entry->qualifiedCppName() << ">(\"" << entry->qualifiedCppName() << "\");" << endl;
    }

}
