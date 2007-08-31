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

#include "generatorsetjava.h"
#include "reporthandler.h"

#include "javagenerator.h"
#include "cppheadergenerator.h"
#include "cppimplgenerator.h"
#include "metainfogenerator.h"
#include "classlistgenerator.h"
#include "qdocgenerator.h"
#include "uiconverter.h"

#include <QFileInfo>

GeneratorSet *GeneratorSet::getInstance() {
    return new GeneratorSetJava();
}

void dumpMetaJavaTree(const MetaJavaClassList &classes);

void generatePriFile(const QString &base_dir, const QString &sub_dir,
                     const MetaJavaClassList &classes,
                     MetaInfoGenerator *info_generator);

GeneratorSetJava::GeneratorSetJava() :
    no_java(false),
    no_cpp_h(false),
    no_cpp_impl(false),
    no_metainfo(false),
    build_class_list(false),
    build_qdoc_japi(false),
    docs_enabled(false),
    do_ui_convert(false),
    doc_dir("../../main/doc/jdoc")
{}

QString GeneratorSetJava::usage() {
    QString usage =
        "  --no-java                                 \n" 
        "  --no-metainfo                             \n" 
        "  --no-cpp-h                                \n"
        "  --no-cpp-impl                             \n"
        "  --convert-to-jui=[.ui-filename]           \n";

    return usage;
}

bool GeneratorSetJava::readParameters(const QMap<QString, QString> args) {
    no_java = args.contains("no-java");
    no_cpp_h = args.contains("no-cpp-h");
    no_cpp_impl = args.contains("no-cpp-impl");
    no_metainfo = args.contains("no-metainfo"); 
    build_class_list = args.contains("build-class-list");
    
    if (args.contains("build-qdoc-japi")) {
        no_java = true;
        no_cpp_h = true;
        no_cpp_impl = true;
        no_metainfo = true;
        build_qdoc_japi = true;
    }
         
    if (args.contains("jdoc-dir")) {
        doc_dir =  args.value("jdoc-dir");
    }

    docs_enabled = args.contains("jdoc-enabled");

    if (args.contains("convert-to-jui")) {
        ui_file_name = args.value("convert-to-jui");
        do_ui_convert = true;
        
        if (!QFileInfo(ui_file_name).exists()) {
            printf(".ui file '%s' does not exist\n", qPrintable(ui_file_name));
            return false;
        } 
    }
    return GeneratorSet::readParameters(args);
}

void GeneratorSetJava::buildModel(const QString pp_file) {
    // Building the code inforamation...
    ReportHandler::setContext("MetaJavaBuilder");
    builder.setFileName(pp_file);
    builder.build();
}

void GeneratorSetJava::dumpObjectTree() {
    dumpMetaJavaTree(builder.classes());
}

QString GeneratorSetJava::generate() {

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

        generator->setOutputDirectory(outDir);
        generator->setClasses(builder.classes());
        if (printStdout)
            generator->printClasses();
        else
            generator->generate();
    }

    no_metainfo = metainfo == 0 || metainfo->numGenerated() == 0;
    if (!no_cpp_impl || !no_cpp_h || !no_metainfo) {
        generatePriFile(outDir, "cpp", builder.classes(),
                        metainfo);
    }

    QString res;
    res = QString("Classes in typesystem: %1\n"
                  "Generated:\n"
                  "  - java......: %2 (%3)\n"
                  "  - cpp-impl..: %4 (%5)\n"
                  "  - cpp-h.....: %6 (%7)\n"
                  "  - meta-info.: %8 (%9)\n"
                  )
        .arg(builder.classes().size())
        .arg(java_generator ? java_generator->numGenerated() : 0)
        .arg(java_generator ? java_generator->numGeneratedAndWritten() : 0)
        .arg(cpp_impl_generator ? cpp_impl_generator->numGenerated() : 0)
        .arg(cpp_impl_generator ? cpp_impl_generator->numGeneratedAndWritten() : 0)
        .arg(cpp_header_generator ? cpp_header_generator->numGenerated() : 0)
        .arg(cpp_header_generator ? cpp_header_generator->numGeneratedAndWritten() : 0)
        .arg(metainfo ? metainfo->numGenerated() : 0)
        .arg(metainfo ? metainfo->numGeneratedAndWritten() : 0);
    
    return res;

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
