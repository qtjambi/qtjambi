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

#include <QDir>
#include <QMetaType>

MetaInfoGenerator::MetaInfoGenerator() : Generator()
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

void MetaInfoGenerator::writeCppFile()
{
    TypeEntryHash entries = TypeDatabase::instance()->entries();
    TypeEntryHash::iterator it;
    
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
    }

    // Primitive types must be added to all packages
    foreach (QFile *f, fileHash.values()) {
        QTextStream s(f);
        for (it=entries.begin(); it!=entries.end(); ++it) {
            TypeEntry *entry = it.value();
            if (shouldGenerate(entry) && entry->isPrimitive())
                writeCustomStructors(s, entry);
        }

        // Initialization function: Registers meta types
        writeInitializationFunctionName(s);
        s << endl << "{" << endl;
        for (it=entries.begin(); it!=entries.end(); ++it) {
            TypeEntry *entry = it.value();
            if (entry &&
                 ( (shouldGenerate(entry) && entry->isPrimitive())
                   || entry->isString()
                   || entry->isChar())) {
                writeInitialization(s, entry);
            }
        }
    }

    foreach (MetaJavaClass *cls, classList) {
        QFile *f = fileHash.value(cls->package(), 0);

        if (f != 0) {
            QTextStream s(f);
            writeInitialization(s, cls->typeEntry(), shouldGenerate(cls));
        }
    }

    foreach (QFile *f, fileHash.values()) {
        QTextStream s(f);
        s << "}" << endl << endl;
        f->close();
        delete f;
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
            writeInitializationFunctionName(s);
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

void MetaInfoGenerator::writeDestructors(QTextStream &s, const MetaJavaClass *cls) 
{
    const ComplexTypeEntry *entry = cls->typeEntry();
    s << "void destructor_" << entry->javaPackage().replace(".", "_")  << "_"  
      << entry->lookupName().replace(".", "_").replace("$", "_") << "(void *ptr)" << endl
      << "{" << endl;

    // We can only delete classes with public destructors. 
    while (cls != 0) {
        if (cls->hasPublicDestructor()) {
            s << "    delete reinterpret_cast<" << cls->qualifiedCppName() << " *>(ptr);" << endl;
            cls = 0;
        } else {
            cls = cls->baseClass();
        }
    }

    s << "}" << endl << endl;
}

void MetaInfoGenerator::writeCustomStructors(QTextStream &s, const TypeEntry *entry)
{
    CustomFunction customConstructor = entry->customConstructor();
    CustomFunction customDestructor = entry->customDestructor();

    if (!customConstructor.name.isEmpty() && !customDestructor.name.isEmpty()) {
        s << "// Custom constructor and destructor for " << entry->qualifiedCppName() << endl
          << "static void *" << customConstructor.name << "("
          << "const " << entry->qualifiedCppName() << " *" << customConstructor.param_name
          << ")" << endl
          << "{" << endl;
        writeCodeBlock(s, customConstructor.code);
        s << "}" << endl << endl;

        s << "static void " << customDestructor.name << "("
          << "const " << entry->qualifiedCppName() << " *" << customDestructor.param_name
          << ")" << endl
          << "{" << endl;
        writeCodeBlock(s, customDestructor.code);
        s << "}" << endl << endl;
    }
}

static void generateInitializer(QTextStream &s, const QString &package, CodeSnip::Position pos)
{
    QList<CodeSnip> snips =
        ((TypeSystemTypeEntry *) TypeDatabase::instance()->findType(package))->snips;

    foreach (const CodeSnip &snip, snips)
        if (snip.position == pos)
            s << snip.code;
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
              << "    __metainfo_init();" << endl
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

void MetaInfoGenerator::writeInitializationFunctionName(QTextStream &s)
{
    s << "void __metainfo_init()";
}

void MetaInfoGenerator::writeInitialization(QTextStream &s, const TypeEntry *entry,
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

    QString javaName = entry->javaPackage().replace(".", "/") + "/" + entry->lookupName();
    QString qtName = entry->name();

    s << "    registerQtToJava(\"" << qtName << "\", \"" << javaName << "\");" << endl
      << "    registerJavaToQt(\"" << javaName << "\", \"" << qtName << "\");" << endl;
    if (entry->isComplex() && entry->isObject() && !((ComplexTypeEntry *)entry)->isQObject() && !entry->isInterface()) 
      s << "    registerDestructor(\"" << javaName << "\", destructor_" << javaName.replace("/", "_").replace("$", "_") << ");" << endl;
    

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
