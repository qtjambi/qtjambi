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

#include "main.h"
#include "asttoxml.h"
#include "cppheadergenerator.h"
#include "cppimplgenerator.h"
#include "javagenerator.h"
#include "metainfogenerator.h"
#include "reporthandler.h"
#include "metajavabuilder.h"
#include "typesystem.h"
#include "classlistgenerator.h"
#include "qdocgenerator.h"
#include "uiconverter.h"


#include <QDir>

void generatePriFile(const QString &base_dir, const QString &sub_dir,
                     const MetaJavaClassList &classes,
                     MetaInfoGenerator *info_generator);

void dumpMetaJavaTree(const MetaJavaClassList &classes);
void test_typeparser(const QString &signature);

int main(int argc, char *argv[])
{
    QString default_file = "qtjambi_masterinclude.h";
    QString default_system = "build_all.txt";

    QString fileName;
    QString typesystemFileName;
    QString out_dir = "..";
    QString pp_file = ".preprocessed.tmp";
    QStringList rebuild_classes;
    QString doc_dir = "../../main/doc/jdoc";
    QString ui_file_name;
    QString include_paths;
    bool print_stdout = false;

    bool no_java = false;
    bool no_cpp_h = false;
    bool no_cpp_impl = false;
    bool no_metainfo = false;
    bool display_help = false;
    bool dump_object_tree = false;
    bool ast_to_xml = false;
    bool build_class_list = false;
    bool build_qdoc_japi = false;
    bool docs_enabled = false;
    bool do_ui_convert = false;

    for (int i=1; i<argc; ++i) {
        QString arg(argv[i]);
        if (arg.startsWith("--no-suppress-warnings")) {
            TypeDatabase *db = TypeDatabase::instance();
            db->setSuppressWarnings(false);
        } else if (arg.startsWith("--output-directory=")) {
            out_dir = arg.mid(19);
        } else if (arg.startsWith("--print-stdout")) {
            print_stdout = true;
        } else if (arg.startsWith("--debug-level=")) {
            QString level = arg.mid(14);
            if (level == "sparse")
                ReportHandler::setDebugLevel(ReportHandler::SparseDebug);
            else if (level == "medium")
                ReportHandler::setDebugLevel(ReportHandler::MediumDebug);
            else if (level == "full")
                ReportHandler::setDebugLevel(ReportHandler::FullDebug);
        } else if (arg.startsWith("--no-java")) {
            no_java = true;
        } else if (arg.startsWith("--no-cpp-h")) {
            no_cpp_h = true;
        } else if (arg.startsWith("--no-cpp-impl")) {
            no_cpp_impl = true;
        } else if (arg.startsWith("--build-class-list")) {
            build_class_list = true;
        } else if (arg.startsWith("--build-qdoc-japi")) {
            no_java = true;
            no_cpp_h = true;
            no_cpp_impl = true;
            no_metainfo = true;
            build_qdoc_japi = true;
        } else if (arg.startsWith("--no-metainfo")) {
            no_metainfo = true;
        } else if (arg.startsWith("--help") || arg.startsWith("-h") || arg.startsWith("-?")) {
            display_help = true;
        } else if (arg.startsWith("--include-paths")) {
            include_paths = arg.mid(16);
        } else if (arg.startsWith("--dump-object-tree")) {
            dump_object_tree = true;
	} else if (arg.startsWith("--ast-to-xml")) {
	    ast_to_xml = true;
        } else if (arg.startsWith("--rebuild-only")) {
            Q_ASSERT(argc > i);
            QStringList classes = QString(argv[i+1]).split(",", QString::SkipEmptyParts);
            TypeDatabase::instance()->setRebuildClasses(classes);
            ++i;
        } else if (arg.startsWith("--jdoc-dir")) {
            Q_ASSERT(argc > i);
            doc_dir = argv[++i];
        } else if (arg.startsWith("--jdoc-enabled")) {
            docs_enabled = true;
        } else if (arg.startsWith("--convert-to-jui=")) {
            ui_file_name = arg.mid(17);
            do_ui_convert = true;

            if (!QFileInfo(ui_file_name).exists()) {
                printf(".ui file '%s' does not exist\n", qPrintable(ui_file_name));
                display_help = true;
            }

        } else {
            if (fileName.isEmpty())
                fileName = QString(argv[i]);
            else if (typesystemFileName.isEmpty())
                typesystemFileName = QString(argv[i]);
            else {
                display_help = true;
            }
        }
    }

    if (fileName.isEmpty())
        fileName = default_file;

    if (typesystemFileName.isEmpty())
        typesystemFileName = default_system;

    display_help = display_help || fileName.isEmpty() || typesystemFileName.isEmpty();

#if defined(Q_OS_WIN32)
    char path_splitter = ';';
#else
    char path_splitter = ':';
#endif

    if (display_help) {
        printf("Usage:\n  %s [options] header-file typesystem-file\n", argv[0]);
        printf("Available options:\n");
        printf("  --debug-level=[sparse|medium|full]        \n"
               "  --dump-object-tree                        \n"
               "  --help, -h or -?                          \n"
               "  --no-cpp-h                                \n"
               "  --no-cpp-impl                             \n"
               "  --no-java                                 \n"
               "  --no-metainfo                             \n"
               "  --no-suppress-warnings                    \n"
               "  --output-directory=[dir]                  \n"
               "  --include-paths=<path>[%c<path>%c...]   \n"
               "  --print-stdout                            \n"
               "  --convert-to-jui=[.ui-filename]           \n",
               path_splitter, path_splitter);
        return 0;
    }

    if (!TypeDatabase::instance()->parseFile(typesystemFileName))
        qFatal("Cannot parse file: '%s'", qPrintable(typesystemFileName));

    if (!Preprocess::preprocess(fileName, pp_file, include_paths)) {
        fprintf(stderr, "Preprocessor failed on file: '%s'\n", qPrintable(fileName));
        return 1;
    }

    if (ast_to_xml) {
	astToXML(pp_file);
	return 0;
    }

    // Building the code inforamation...
    ReportHandler::setContext("MetaJavaBuilder");
    MetaJavaBuilder builder;
    builder.setFileName(pp_file);
    builder.build();

    if (dump_object_tree) {
        dumpMetaJavaTree(builder.classes());
        return 0;
    }

    // Ui conversion...
    if (do_ui_convert) {
        UiConverter converter;
        converter.setClasses(builder.classes());
        converter.convertToJui(ui_file_name);
        return 0;
    }

    // Code generation
    QList<Generator *> generators;
    JavaGenerator *java_generator = 0;
    CppHeaderGenerator *cpp_header_generator = 0;
    CppImplGenerator *cpp_impl_generator = 0;
    MetaInfoGenerator *metainfo = 0;

    QStringList contexts;
    if (build_qdoc_japi) {
        generators << new QDocGenerator;
        contexts << "QDocGenerator";
    }

    if (!no_java) {
        java_generator = new JavaGenerator;
        java_generator->setDocumentationDirectory(doc_dir);
        java_generator->setDocumentationEnabled(docs_enabled);
        generators << java_generator;
        contexts << "JavaGenerator";
    }

    if (!no_cpp_h) {
        cpp_header_generator = new CppHeaderGenerator;
        generators << cpp_header_generator;
        contexts << "CppHeaderGenerator";
    }

    if (!no_cpp_impl) {
        cpp_impl_generator = new CppImplGenerator;
        generators << cpp_impl_generator;
        contexts << "CppImplGenerator";
    }

    if (!no_metainfo) {
        metainfo = new MetaInfoGenerator;
        generators << metainfo;
        contexts << "MetaInfoGenerator";
    }

    if (build_class_list) {
        generators << new ClassListGenerator;
        contexts << "ClassListGenerator";
    }

    for (int i=0; i<generators.size(); ++i) {
        Generator *generator = generators.at(i);
        ReportHandler::setContext(contexts.at(i));

        generator->setOutputDirectory(out_dir);
        generator->setClasses(builder.classes());
        if (print_stdout)
            generator->printClasses();
        else
            generator->generate();
    }

    no_metainfo = metainfo == 0 || metainfo->numGenerated() == 0;
    if (!no_cpp_impl || !no_cpp_h || !no_metainfo) {
        generatePriFile(out_dir, "cpp", builder.classes(),
                        metainfo);
    }

    printf("Classes in typesystem: %d\n"
           "Generated:\n"
           "  - java......: %d\n"
           "  - cpp-impl..: %d\n"
           "  - cpp-h.....: %d\n"
           "  - meta-info.: %d\n",
           builder.classes().size(),
           java_generator ? java_generator->numGenerated() : 0,
           cpp_impl_generator ? cpp_impl_generator->numGenerated() : 0,
           cpp_header_generator ? cpp_header_generator->numGenerated() : 0,
           metainfo ? metainfo->numGenerated() : 0);

    printf("Done, %d warnings (%d known issues)\n", ReportHandler::warningCount(),
           ReportHandler::suppressedCount());
}

QFile *openPriFile(const QString &base_dir, const QString &sub_dir, const MetaJavaClass *cls)
{
    QString pro_file_name = cls->package().replace(".", "_") + "/" + cls->package().replace(".", "_") + ".pri";

    if (!sub_dir.isEmpty())
        pro_file_name = sub_dir + "/" + pro_file_name;

    if (!base_dir.isEmpty())
        pro_file_name = base_dir + "/" + pro_file_name;

    QFile *pro_file = new QFile(pro_file_name);
    if (!pro_file->open(QIODevice::WriteOnly)) {
        ReportHandler::warning(QString("failed to open %1 for writing...") .arg(pro_file_name));
        return 0;
    }

    return pro_file;
}

void generatePriFile(const QString &base_dir, const QString &sub_dir,
                     const MetaJavaClassList &classes,
                     MetaInfoGenerator *info_generator)
{
    QHash<QString, QFile *> fileHash;

    foreach (const MetaJavaClass *cls, classes) {
        if (!(cls->typeEntry()->codeGeneration() & TypeEntry::GenerateCpp))
            continue;

        QString meta_info_stub = info_generator->filenameStub();
        if (info_generator == 0 || info_generator->generated(cls) == 0)
            meta_info_stub = QString();

        QTextStream s;

        QFile *f = fileHash.value(cls->package(), 0);
        if (f == 0) {
            f = openPriFile(base_dir, sub_dir, cls);
            fileHash.insert(cls->package(), f);

            s.setDevice(f);
            if (!meta_info_stub.isEmpty()) {
                s << "HEADERS += $$PWD/" << meta_info_stub << ".h" << endl;
            }

            s << "SOURCES += \\" << endl;
            if (!meta_info_stub.isEmpty()) {
                s << "        " << "$$PWD/" << meta_info_stub << ".cpp \\" << endl;
                s << "     $$PWD/qtjambi_libraryinitializer.cpp \\" << endl;
            }
        } else {
            s.setDevice(f);
        }

        if (!cls->isNamespace() && !cls->isInterface() && !cls->typeEntry()->isVariant())
            s << "        " << "$$PWD/qtjambishell_" << cls->name() << ".cpp \\" << endl;
    }

    foreach (QFile *f, fileHash.values()) {
        QTextStream s(f);
        s << endl << "HEADERS += \\" << endl;
    }

    foreach (const MetaJavaClass *cls, classes) {
        if (!(cls->typeEntry()->codeGeneration() & TypeEntry::GenerateCpp))
            continue;

        QFile *f = fileHash.value(cls->package(), 0);
        Q_ASSERT(f);

        QTextStream s(f);
        bool shellfile = (cls->generateShellClass() || cls->queryFunctions(MetaJavaClass::Signals).size() > 0)
                && !cls->isNamespace() && !cls->isInterface() && !cls->typeEntry()->isVariant();
            /*bool shellfile = (!cls->isNamespace() && !cls->isInterface() && cls->hasVirtualFunctions()
                          && !cls->typeEntry()->isVariant()) */
        if (shellfile)
            s << "        $$PWD/qtjambishell_" << cls->name() << ".h \\" << endl;
    }

    foreach (QFile *f, fileHash.values()) {
        f->close();
        delete f;
    }
}

void dumpMetaJavaAttributes(const MetaJavaAttributes *attr)
{
    if (attr->isNative()) printf(" native");
    if (attr->isAbstract()) printf(" abstract");
    if (attr->isFinalInJava()) printf(" final(java)");
    if (attr->isFinalInCpp()) printf(" final(cpp)");
    if (attr->isStatic()) printf(" static");
    if (attr->isPrivate()) printf(" private");
    if (attr->isProtected()) printf(" protected");
    if (attr->isPublic()) printf(" public");
    if (attr->isFriendly()) printf(" friendly");
}

void dumpMetaJavaType(const MetaJavaType *type)
{
    if (!type) {
        printf("[void]");
    } else {
        printf("[type: %s", qPrintable(type->typeEntry()->qualifiedCppName()));
        if (type->isReference()) printf(" &");
        int indirections = type->indirections();
        if (indirections) printf(" %s", qPrintable(QString(indirections, '*')));

        printf(", %s", qPrintable(type->typeEntry()->qualifiedJavaName()));

        if (type->isPrimitive()) printf(" primitive");
        if (type->isEnum()) printf(" enum");
        if (type->isQObject()) printf(" q_obj");
        if (type->isNativePointer()) printf(" n_ptr");
        if (type->isJavaString()) printf(" java_string");
        if (type->isConstant()) printf(" const");
        printf("]");
    }
}

void dumpMetaJavaArgument(const MetaJavaArgument *arg)
{
    printf("        ");
    dumpMetaJavaType(arg->type());
    printf(" %s", qPrintable(arg->argumentName()));
    if (!arg->defaultValueExpression().isEmpty())
        printf(" = %s", qPrintable(arg->defaultValueExpression()));
    printf("\n");
}

void dumpMetaJavaFunction(const MetaJavaFunction *func)
{
    printf("    %s() - ", qPrintable(func->name()));
    dumpMetaJavaType(func->type());
    dumpMetaJavaAttributes(func);
    if (func->isConstant()) printf(" const");
    printf("\n      arguments:\n");
    foreach (MetaJavaArgument *arg, func->arguments())
        dumpMetaJavaArgument(arg);
}

void dumpMetaJavaClass(const MetaJavaClass *cls)
{
    printf("\nclass: %s, package: %s\n", qPrintable(cls->name()), qPrintable(cls->package()));
    if (cls->hasVirtualFunctions())
        printf("    shell based\n");
    printf("  baseclass: %s %s\n", qPrintable(cls->baseClassName()), cls->isQObject() ? "'QObject-type'" : "'not a QObject-type'");
    printf("  interfaces:");
    foreach (MetaJavaClass *iface, cls->interfaces())
        printf(" %s", qPrintable(iface->name()));
    printf("\n");
    printf("  attributes:");
    dumpMetaJavaAttributes(cls);

    printf("\n  functions:\n");
    foreach (const MetaJavaFunction *func, cls->functions())
        dumpMetaJavaFunction(func);

    //     printf("\n  fields:\n");
    //     foreach (const MetaJavaField *field, cls->fields())
    //         dumpMetaJavaField(field);

    //     printf("\n  enums:\n");
    //     foreach (const MetaJavaEnum *e, cls->enums())
    //         dumpMetaJavaEnum(e);
}

void dumpMetaJavaTree(const MetaJavaClassList &classes)
{
    foreach (MetaJavaClass *cls, classes) {
        dumpMetaJavaClass(cls);
    }
}
