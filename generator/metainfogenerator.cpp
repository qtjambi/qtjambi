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

#include "metainfogenerator.h"
#include "reporthandler.h"
#include "cppimplgenerator.h"

#include <QDir>
#include <QMetaType>

MetaInfoGenerator::MetaInfoGenerator() : JavaGenerator()
{
    setFilenameStub("metainfo");
}

QString MetaInfoGenerator::subDirectoryForPackage(const QString &package, OutputDirectoryType type) const
{
    switch (type) {
    case CppDirectory:
        return "cpp/" + QString(package).replace(".", "_") + "/";
    case JavaDirectory:
        return QString(package).replace(".", "/");
    default:
        return QString(); // kill nonsense warnings
    }
}

QString MetaInfoGenerator::subDirectoryForClass(const MetaJavaClass *cls, OutputDirectoryType type) const
{
    Q_ASSERT(cls);
    return subDirectoryForPackage(cls->package(), type);
}

void MetaInfoGenerator::generate()
{
    buildSkipList();
    writeCppFile();
    writeHeaderFile();
    writeLibraryInitializers();
}

bool MetaInfoGenerator::shouldGenerate(const TypeEntry *entry) const
{
    return entry != 0 && !entry->isNamespace() && !entry->isEnum();
}

bool MetaInfoGenerator::shouldGenerate(const MetaJavaClass *cls) const
{
    return (!cls->isInterface() && cls->typeEntry()->isValue() && !cls->isNamespace()
            && !cls->isAbstract() && (cls->typeEntry()->codeGeneration() & TypeEntry::GenerateCpp));
}

QString MetaInfoGenerator::fileNameForClass(const MetaJavaClass *) const
{
    return filenameStub() + ".cpp";
}

void MetaInfoGenerator::write(QTextStream &, const MetaJavaClass *)
{
    // not used
}

bool MetaInfoGenerator::generated(const MetaJavaClass *cls) const
{
    return generatedMetaInfo(cls->package());
}

bool MetaInfoGenerator::generatedMetaInfo(const QString &package) const
{
    return (m_skip_list.value(package, 0x0) & GeneratedMetaInfo);
}

bool MetaInfoGenerator::generatedJavaClasses(const QString &package) const
{
    return (m_skip_list.value(package, 0x0) & GeneratedJavaClasses);
}

static void metainfo_write_name_list(QTextStream &s, char *var_name, const QList<QString> &strs,
                                     int offset)
{
    s << "static const char *" << var_name << "[] = {" << endl;
    for (int i=offset; i<strs.size(); i += 4) {
        s << "    \"" << strs.at(i).toLatin1() << "\"";
        if (i < strs.size() - 1)
            s << ",";
        s << endl;
    }
    s << "};" << endl << endl;
}

void MetaInfoGenerator::writeSignalsAndSlots(QTextStream &s, const QString &package)
{
    MetaJavaClassList classes = this->classes();

    QList<QString> strs;
    foreach (MetaJavaClass *cls, classes) {
        if (cls->package() == package) {
            MetaJavaFunctionList functions = cls->functions();
            foreach (MetaJavaFunction *f, functions) {
                if (f->implementingClass() == cls && (f->isSignal() || f->isSlot())) {
                    
                    MetaJavaArgumentList arguments = f->arguments();
                    int numOverloads = arguments.size();
                    for (int i=arguments.size()-1; i>=0; --i) {                       
                        if (arguments.at(i)->defaultValueExpression().isEmpty()) {
                            numOverloads = arguments.size() - i - 1;
                            break; 
                        }
                    }                    

                    for (int i=0; i<=numOverloads; ++i) {
                        Option option = Option(SkipAttributes | SkipReturnType | SkipName);
                        QString qtName;
                        {

                            QTextStream qtNameStream(&qtName);
                            CppGenerator::writeFunctionSignature(qtNameStream, f, 0, QString(),
                                Option(option | OriginalName | OriginalTypeDescription),
                                QString(), QStringList(), arguments.size() - i);
                        }
                        qtName = f->implementingClass()->qualifiedCppName() + "::" + qtName;
                        qtName = QMetaObject::normalizedSignature(qtName.toLatin1().constData());

                        QString javaFunctionName = functionSignature(f, 0, 0, option);
                        QString javaObjectName = f->isSignal()
                                                ? f->name()
                                                : javaFunctionName;

                        javaFunctionName = f->implementingClass()->fullName() + "." + javaFunctionName;
                        javaObjectName   = f->implementingClass()->fullName() + "." + javaObjectName;

                        QString javaSignature = "(";
                        MetaJavaArgumentList args = f->arguments();
                        foreach (MetaJavaArgument *arg, args) {
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
        metainfo_write_name_list(s, "qtNames", strs, 0);
        metainfo_write_name_list(s, "javaFunctionNames", strs, 1);
        metainfo_write_name_list(s, "javaObjectNames", strs, 2);
        metainfo_write_name_list(s, "javaSignatures", strs, 3);
    } else {
        s << "static const char **qtNames = 0;" << endl
          << "static const char **javaFunctionNames = 0;" << endl
          << "static const char **javaObjectNames = 0;" << endl
          << "static const char **javaSignatures = 0;" << endl;
    }
}

void MetaInfoGenerator::writeRegisterSignalsAndSlots(QTextStream &s)
{
    s << "    for (int i=0;i<sns_count; ++i) {" << endl
      << "        registerQtToJava(qtNames[i], javaFunctionNames[i]);" << endl
      << "        if (getQtName(javaObjectNames[i]).length() < QByteArray(qtNames[i]).size())" << endl
      << "            registerJavaToQt(javaObjectNames[i], qtNames[i]);" << endl
      << "        registerJavaSignature(qtNames[i], javaSignatures[i]);" << endl
      << "    }" << endl;
}

void MetaInfoGenerator::buildSkipList()
{
    MetaJavaClassList classList = classes();
    foreach (MetaJavaClass *cls, classList) {
        if (!m_skip_list.contains(cls->package()))
            m_skip_list[cls->package()] = 0x0;

        if (cls->typeEntry()->codeGeneration() & TypeEntry::GenerateCpp)
            m_skip_list[cls->package()] |= GeneratedMetaInfo;

        if (cls->typeEntry()->codeGeneration() & TypeEntry::GenerateJava)
            m_skip_list[cls->package()] |= GeneratedJavaClasses;
    }
}

QStringList MetaInfoGenerator::writePolymorphicHandler(QTextStream &s, const QString &package,
                                                       const MetaJavaClassList &classes)
{
    QStringList handlers;
    foreach (MetaJavaClass *cls, classes) {            
        const ComplexTypeEntry *centry = cls->typeEntry();
        if (!centry->isPolymorphicBase())
            continue;

        MetaJavaClassList classList = this->classes();
        bool first = true;
        foreach (MetaJavaClass *clazz, classList) {
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

void MetaInfoGenerator::writeCppFile()
{
    TypeEntryHash entries = TypeDatabase::instance()->entries();
    TypeEntryHash::iterator it;

    MetaJavaClassList classes_with_polymorphic_id;
    MetaJavaClassList classList = classes();
    QHash<QString, QFile *> fileHash;
    foreach (MetaJavaClass *cls, classList) {
        QTextStream s;
        QFile *f = fileHash.value(cls->package(), 0);
        if (f == 0 && generated(cls)) {
            QDir dir(outputDirectory() + "/" + subDirectoryForClass(cls, CppDirectory));
            dir.mkpath(dir.absolutePath());

            m_num_generated++;
            f = new QFile(dir.absoluteFilePath(cppFilename()));
            if (!f->open(QIODevice::WriteOnly)) {
                ReportHandler::warning(QString("failed to open file '%1' for writing")
                    .arg(f->fileName()));
                return;
            }

            s.setDevice(f);
            writeIncludeStatements(s, classList, cls->package());
            s << endl;

            fileHash.insert(cls->package(), f);
        } else {
            s.setDevice(f);
        }

        if (s.device() != 0) {
            if (cls->typeEntry()->isObject() && !cls->typeEntry()->isQObject() && !cls->isInterface())
                writeDestructors(s, cls);
            writeCustomStructors(s, cls->typeEntry());
        }

        if (cls->typeEntry()->isPolymorphicBase())
            classes_with_polymorphic_id.append(cls);
    }

    QHash<QString, QStringList> handlers_to_register;
    foreach (QString package, fileHash.keys()) {
        QFile *f = fileHash.value(package, 0);
        if (f != 0) {
            QTextStream s(f);
            writeSignalsAndSlots(s, package);
            handlers_to_register[package] = writePolymorphicHandler(s, package, classes_with_polymorphic_id);
        }
    }

    // Primitive types must be added to all packages, in case the other packages are
    // not referenced from the generated code.
    foreach (QFile *f, fileHash.values()) {
        QTextStream s(f);
        for (it=entries.begin(); it!=entries.end(); ++it) {
            TypeEntry *entry = it.value();
            if (shouldGenerate(entry) && entry->isPrimitive())
                writeCustomStructors(s, entry);
        }

        // Initialization function: Registers meta types
        writeInitializationFunctionName(s, fileHash.key(f, ""), true);
        s << endl << "{" << endl;
        for (it=entries.begin(); it!=entries.end(); ++it) {
            TypeEntry *entry = it.value();
            if (entry &&
                 ( (shouldGenerate(entry) && entry->isPrimitive())
                   || entry->isString()
                   || entry->isChar())) {
                        writeInitialization(s, entry, 0);
                   }
        }
        writeRegisterSignalsAndSlots(s);
    }

    foreach (MetaJavaClass *cls, classList) {
        QFile *f = fileHash.value(cls->package(), 0);

        if (f != 0) {
            QTextStream s(f);
            writeInitialization(s, cls->typeEntry(), cls, shouldGenerate(cls));
        }
    }

    foreach (QString package, fileHash.keys()) {
        QFile *f = fileHash.value(package, 0);
        if (f != 0) {
            QTextStream s(f);

            foreach (QString handler, handlers_to_register.value(package, QStringList())) {
                s << "    qtjambi_register_polymorphic_id(\"" << handler << "\","
                  << "polymorphichandler_" << handler << ");" << endl;
            }
            
            s << "}" << endl << endl;
            f->close();
            delete f;
        }
    }
}

void MetaInfoGenerator::writeHeaderFile()
{
    MetaJavaClassList classList = classes();
    QHash<QString, bool> fileHash;

    foreach (MetaJavaClass *cls, classList) {
        bool hasGenerated = fileHash.value(cls->package(), false);
        if (!hasGenerated && generated(cls)) {
            QDir dir(outputDirectory() + "/" + subDirectoryForClass(cls, CppDirectory));
            dir.mkpath(dir.absolutePath());

            QFile file(dir.absoluteFilePath(headerFilename()));
            if (!file.open(QIODevice::WriteOnly)) {
                ReportHandler::warning(QString("failed to open file '%1' for writing")
                                        .arg(file.fileName()));
                return;
            }

            QTextStream s(&file);
            s << "#ifndef " << filenameStub().toUpper() << "_H" << endl;
            s << "#define " << filenameStub().toUpper() << "_H" << endl << endl;
            writeInitializationFunctionName(s, cls->package(), true);
            s << ";" << endl << "#endif" << endl << endl;

            fileHash.insert(cls->package(), true);
        }
    }
}

void MetaInfoGenerator::writeCodeBlock(QTextStream &s, const QString &code)
{
    QStringList lines = code.split('\n');
    QString indent;
    foreach (QString str, lines) {
        s << "    " << indent << str.trimmed() << endl;
        if (!str.trimmed().endsWith(";") && !str.trimmed().isEmpty())
            indent = "    ";
        else
            indent = "";
    }
}

const MetaJavaClass* MetaInfoGenerator::lookupClassWithPublicDestructor(const MetaJavaClass *cls)
{
    while (cls != 0) {
        if (cls->hasPublicDestructor()) {
            return cls;
        } else {
            cls = cls->baseClass();
        }
    }
    return 0;
}

void MetaInfoGenerator::writeDestructors(QTextStream &s, const MetaJavaClass *cls)
{
    // We can only delete classes with public destructors
    const MetaJavaClass *clsWithPublicDestructor = lookupClassWithPublicDestructor(cls);
    if(clsWithPublicDestructor != 0)
    {
        const ComplexTypeEntry *entry = cls->typeEntry();
        s   << "void destructor_" << entry->javaPackage().replace(".", "_")  << "_"
            << entry->lookupName().replace(".", "_").replace("$", "_") << "(void *ptr)" << endl
            << "{" << endl
            << "    delete reinterpret_cast<" << clsWithPublicDestructor->qualifiedCppName() << " *>(ptr);" << endl
            << "}" << endl << endl;
    }
}

void MetaInfoGenerator::writeCustomStructors(QTextStream &s, const TypeEntry *entry)
{
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

static void generateInitializer(QTextStream &s, const QString &package, CodeSnip::Position pos)
{
    QList<CodeSnip> snips =
        ((TypeSystemTypeEntry *) TypeDatabase::instance()->findType(package))->snips;

    foreach (const CodeSnip &snip, snips)
        if (snip.position == pos)
            s << snip.code();
}

void MetaInfoGenerator::writeLibraryInitializers()
{
    // from cppimplgenerator.cpp
    extern QString jni_function_signature(QString package, QString class_name, const QString &function_name,
                                          const QString &return_type);

    // We need to generate a library initializer in Java for all packages
    // that have generated classes in Java, and in C++ for all packages
    // that have generated metainfo.

    QList<QString> known_packages = m_skip_list.keys();
    foreach (QString package, known_packages) {
        if (generatedMetaInfo(package)) { // write cpp file
            QDir dir(outputDirectory() + "/" + subDirectoryForPackage(package, CppDirectory));
            dir.mkpath(dir.absolutePath());

            QFile f(dir.absoluteFilePath("qtjambi_libraryinitializer.cpp"));
            if (!f.open(QIODevice::WriteOnly)) {
                ReportHandler::warning(QString("failed to open file '%1' for writing")
                                       .arg(f.fileName()));
                continue;
            }
            QString signature = jni_function_signature(package, "QtJambi_LibraryInitializer",
                                                   "__qt_initLibrary", "void");
            QTextStream s(&f);
            s << "#include \"metainfo.h\"" << endl
              << "#include \"qtjambi_global.h\"" << endl << endl
              << signature << "(JNIEnv *, jclass)" << endl
              << "{" << endl
              << "    "; 
            writeInitializationFunctionName(s, package, false);
            s << ";" << endl
              << "}" << endl << endl;
        }

        if (generatedJavaClasses(package)) {
            QDir dir(outputDirectory() + "/" + subDirectoryForPackage(package, JavaDirectory));
            dir.mkpath(dir.absolutePath());

            QFile f(dir.absoluteFilePath("QtJambi_LibraryInitializer.java"));
            if (!f.open(QIODevice::WriteOnly)) {
                ReportHandler::warning(QString("failed to open file '%1' for writing")
                                       .arg(f.fileName()));
                continue;
            }

            QTextStream s(&f);
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
        }
    }
}

void MetaInfoGenerator::writeInclude(QTextStream &s, const Include &inc)
{
    if (inc.name.isEmpty())
        return;

    s << "#include ";
    if (inc.type == Include::LocalPath)
        s << "\"" << inc.name << "\"";
    else
        s << "<" << inc.name << ">";
    s << endl;
}

void MetaInfoGenerator::writeIncludeStatements(QTextStream &s, const MetaJavaClassList &classList,
                                               const QString &package)
{
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
    s << endl;

    foreach (MetaJavaClass *cls, classList) {
        if (generated(cls) && !cls->isInterface() && cls->package() == package) {
            const ComplexTypeEntry *ctype = cls->typeEntry();

            Include inc = ctype->include();
            if (inc.name.isEmpty())
                writeInclude(s, Include(Include::IncludePath, ctype->name()));
            else
                writeInclude(s, inc);
        }
    }
}

void MetaInfoGenerator::writeInitializationFunctionName(QTextStream &s, const QString &package, bool fullSignature)
{
    if (fullSignature)
        s << "void ";
    s << "__metainfo_init_" << QString(package).replace(".", "_") << "()";
}

void MetaInfoGenerator::writeInitialization(QTextStream &s, const TypeEntry *entry, const MetaJavaClass *cls,
                                            bool registerMetaType)
{
    QString constructorName = entry->customConstructor().name;
    QString destructorName = entry->customDestructor().name;

    if (constructorName.isEmpty() != destructorName.isEmpty()) {
        ReportHandler::warning(QString("specify either no custom functions, or both "
                                       "constructor and destructor for type '%1'").arg(entry->name()));
     }

    if (!entry->preferredConversion())
        return ;

    QString javaPackage = entry->javaPackage();

    QString javaName =  entry->lookupName();
    if(!javaPackage.isEmpty()){
        javaName.prepend(javaPackage.replace(".", "/") + "/");
    }

    QString qtName = entry->name();

    s << "    registerQtToJava(\"" << qtName << "\", \"" << javaName << "\");" << endl
      << "    registerJavaToQt(\"" << javaName << "\", \"" << qtName << "\");" << endl;
    if (entry->isComplex() && entry->isObject() && !((ComplexTypeEntry *)entry)->isQObject() && !entry->isInterface()) {
        QString patchedName = QString(javaName).replace("/", "_").replace("$", "_");

        if(lookupClassWithPublicDestructor(cls))
        {
            s << "    registerDestructor(\"" << javaName << "\", destructor_" << patchedName << ");" << endl;
        }
    }

    if (!registerMetaType)
        return ;

    int metaType = QMetaType::type(entry->name().toLocal8Bit().constData());
    if (metaType != QMetaType::Void)
        return ;

    if (!constructorName.isEmpty() && !destructorName.isEmpty()) {
        s << "    QMetaType::registerType(\"" << entry->name() << "\"," << endl
          << "                            reinterpret_cast<QMetaType::Destructor>("
          << destructorName
          << ")," << endl
          << "                            reinterpret_cast<QMetaType::Constructor>("
          << constructorName
          << "));" << endl;
    } else {
        s << "    qRegisterMetaType<" << entry->qualifiedCppName() << ">(\"" << entry->name() << "\");" << endl;
    }

}
