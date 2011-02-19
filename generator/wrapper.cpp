
#include "wrapper.h"
#include "reporthandler.h"
#include "fileout.h"
#include "typesystem/typedatabase.h"
#include "main.h"
#include "asttoxml.h"
#include "parser/binder.h"

QString Wrapper::include_directory = QString();

void ReportHandler_message_handler(const std::string &str) {
    ReportHandler::warning(QString::fromStdString(str));
}

Wrapper::Wrapper(int argc, char *argv[]) :
        default_file("targets/qtjambi_masterinclude.h"),
        default_system("targets/build_all.xml"),
        pp_file(".preprocessed.tmp") {

    gs = GeneratorSet::getInstance();
    args = parseArguments(argc, argv);
    handleArguments();
    assignVariables();
}

void Wrapper::handleArguments() {
    if (args.contains("no-suppress-warnings")) {
        TypeDatabase *db = TypeDatabase::instance();
        db->setSuppressWarnings(false);
    }

    if (args.contains("include-eclipse-warnings")) {
        TypeDatabase *db = TypeDatabase::instance();
        db->setIncludeEclipseWarnings(true);
    }

    if (args.contains("debug-level")) {
        QString level = args.value("debug-level");
        if (level == "sparse") {
            ReportHandler::setDebugLevel(ReportHandler::SparseDebug);
        } else if (level == "medium") {
            ReportHandler::setDebugLevel(ReportHandler::MediumDebug);
        } else if (level == "full") {
            ReportHandler::setDebugLevel(ReportHandler::FullDebug);
        } else if (level == "types") {
            ReportHandler::setDebugLevel(ReportHandler::TypeDebug);
        }
    }

    if (args.contains("dummy")) {
        FileOut::dummy = true;
    }

    if (args.contains("diff")) {
        FileOut::diff = true;
    }

    if (args.contains("rebuild-only")) {
        QStringList classes = args.value("rebuild-only").split(",", QString::SkipEmptyParts);
        TypeDatabase::instance()->setRebuildClasses(classes);
    }

    if (args.contains("qt-include-directory")) {
        include_directory = args.value("qt-include-directory");
    } else include_directory = "";
}

void Wrapper::assignVariables() {
    //set file name
    fileName = args.value("arg-1");

    //set typesystem filename
    typesystemFileName = args.value("arg-2");
    if (args.contains("arg-3"))
        displayHelp(gs);

    //set filename to default masterinclude if is empty
    if (fileName.isEmpty())
        fileName = default_file;

    //if type system filename is empty, set it to default system file...
    if (typesystemFileName.isEmpty())
        typesystemFileName = default_system;

    //if filename or typesystem filename is still empty, show help
    if (fileName.isEmpty() || typesystemFileName.isEmpty())
        displayHelp(gs);

    //if generatorset can't read arguments, show help
    if (!gs->readParameters(args))
        displayHelp(gs);
}

int Wrapper::runJambiGenerator() {
    printf("Running the Qt Jambi Generator. Please wait while source files are being generated...\n");

    //parse the type system file
    if (!TypeDatabase::instance()->parseFile(typesystemFileName))
        qFatal("Cannot parse file: '%s'", qPrintable(typesystemFileName));

    //removing file here for theoretical case of wanting to parse two master include files here
    QFile::remove(pp_file);
    //preprocess using master include, preprocessed file and command line given include paths, if any
    if (!Preprocess::preprocess(fileName, pp_file, args.value("phonon-include"))) {
        fprintf(stderr, "Preprocessor failed on file: '%s'\n", qPrintable(fileName));
        return 1;
    }

    //convert temp preprocessed file to xml
    if (args.contains("ast-to-xml")) {
        astToXML(pp_file);
        return 0;
    }

    Binder::installMessageHandler(ReportHandler_message_handler);

    gs->buildModel(pp_file);

    if (args.contains("dump-object-tree")) {
        gs->dumpObjectTree();
        return 0;
    }

    printf("%s\n", qPrintable(gs->generate()));

    printf("Done, %d warnings (%d known issues)\n", ReportHandler::warningCount(),
           ReportHandler::suppressedCount());

    return 0;
}

void Wrapper::displayHelp(GeneratorSet* generatorSet) {
#if defined(Q_OS_WIN32)
    char path_splitter = ';';
#else
    char path_splitter = ':';
#endif
    printf("Usage:\n  generator [options] header-file typesystem-file\n\n");
    printf("Available options:\n\n");
    printf("General:\n");
    printf("  --debug-level=[types|sparse|medium|full]  \n"
           "  --dump-object-tree                        \n"
           "  --help, -h or -?                          \n"
           "  --no-suppress-warnings                    \n"
           "  --include-eclipse-warnings                \n"
           "  --output-directory=[dir]                  \n"
           "  --include-paths=<path>[%c<path>%c...]     \n"
           "  --print-stdout                            \n"
           "  --qt-include-directory=[dir]              \n",
           path_splitter, path_splitter);

    printf("%s", qPrintable(generatorSet->usage()));
    exit(0);
}

QMap<QString, QString> Wrapper::parseArguments(int argc, char *argv[]) {
    QMap<QString, QString> args;

    int argNum = 0;
    for (int i = 1; i < argc; ++i) {
        QString arg(argv[i]);
        arg = arg.trimmed();

        if (arg.startsWith("--")) {
            int split = arg.indexOf("=");

            if (split > 0)
                args[arg.mid(2).left(split-2)] = arg.mid(split + 1).trimmed();
            else
                args[arg.mid(2)] = QString();

        } else if (arg.startsWith("-")) {
            args[arg.mid(1)] = QString();
        } else {
            argNum++;
            args[QString("arg-%1").arg(argNum)] = arg;
        }
    }

    return args;
}
