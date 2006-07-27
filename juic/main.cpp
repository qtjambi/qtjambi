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

#define _CRT_SECURE_NO_DEPRECATE

#include "uic.h"
#include "option.h"
#include "driver.h"
#include "javanametable.h"

#include <QDateTime>
#include <QDir>
#include <QFile>
#include <QFileInfo>
#include <QTextStream>
#include <QTextCodec>

#include <QDebug>

static const char *error = 0;

#ifdef Q_WS_WIN
static char ENV_SPLITTER = ';';
#else
static char ENV_SPLITTER = ':';
#endif



void showHelp(const char *appName);
bool ensurePath(const QString &path);
void traverseAll();
QString findClassName(const QFileInfo &file);
void traverseClassPath(const QString &rootPath, const QDir &dir);

bool runJuic(const QFileInfo &uiFile, const QString &baseDir, const QString &package);

static int num_processed_files;
static int num_updated_files;


int main(int argc, char *argv[])
{
    QString fileName;
    QString package;
    QString outDir;

    bool process_directory = false;

    int arg = 1;
    while (arg < argc) {
        QString opt = QString::fromLocal8Bit(argv[arg]);
        if (opt == QLatin1String("-h")
            || opt == QLatin1String("-help")
            || opt == QLatin1String("--help")) {
            showHelp(argv[0]);
            return 0;
        } else if (opt == QLatin1String("-v") || opt == QLatin1String("-version")) {
            fprintf(stderr, "Qt Jambi user interface compiler %s.\n", QT_VERSION_STR);
            return 0;
        } else if (opt == QLatin1String("-p")) {
            ++arg;
            if (!argv[arg]) {
                showHelp(argv[0]);
                return 1;
            }
            package = QString::fromLocal8Bit(argv[arg]);

        } else if (opt == QLatin1String("-d")) {
            ++arg;
            if (!argv[arg]) {
                showHelp(argv[0]);
                return 1;
            }
            outDir = QString::fromLocal8Bit(argv[arg]);

        } else if (opt == QLatin1String("-x")) {
            ++arg;
            if (!argv[arg]) {
                showHelp(argv[0]);
                return 1;
            }
            JavaNameTable::instance()->loadXmlFile(QString::fromLocal8Bit(argv[arg]));

        } else if (opt == QLatin1String("-cp")) {
            process_directory = true;

        } else if (fileName.isEmpty()) {
            fileName = QString::fromLocal8Bit(argv[arg]);

        } else {
            showHelp(argv[0]);
            return 1;
        }
        ++arg;
    }

    fileName = QDir::cleanPath(fileName);

    if (process_directory) {
        if (fileName.isEmpty()) {
            traverseAll();
        } else {
            traverseClassPath(QFileInfo(fileName).absoluteFilePath(), QDir(fileName));
        }

        if (num_processed_files == 0) {
            fprintf(stdout, "juic: no .ui files found in CLASSPATH\n");
        } else if (num_updated_files == 0) {
            fprintf(stdout, "juic: all files up to date\n");
        } else {
            fprintf(stdout, "juic: updated %d files\n", num_updated_files);
        }

        return 0;
    }

    if (fileName.isEmpty()) {
        showHelp(argv[0]);
        return 1
            ;
    }

    return runJuic(QFileInfo(fileName), outDir, package);
}


bool ensurePath(const QString &path)
{
    if (!QFileInfo(path).exists()) {
        QDir dir;
        if (!dir.mkpath(path)) {
            fprintf(stderr, "Failed to create output directory: %s\n", qPrintable(path));
            return false;
        }
    }
    return true;
}


QString findClassName(const QFileInfo &file)
{
    QFile f(file.absoluteFilePath());
    if (!f.open(QIODevice::ReadOnly)) {
        fprintf(stderr, "juic: failed to read file: %s\n",
                qPrintable(file.absoluteFilePath()));
        return false;
    }
    QByteArray content = f.readAll();

    int start = content.indexOf("<class>") + 7;
    int end = content.indexOf("</class>");

    if (start == -1 || end == -1 || end < start) {
        fprintf(stdout, content.constData());
        fprintf(stderr, "Invalid input: %s\n",
                qPrintable(file.absoluteFilePath()));
        return false;
    }

    return content.mid(start, end - start).trimmed();
}


void showHelp(const char *appName)
{
    fprintf(stderr, "Qt Jambi user interface compiler %s.\n", QT_VERSION_STR);
    if (error)
        fprintf(stderr, "%s: %s\n", appName, error);

    fprintf(stderr, "Usage: %s [OPTION]... <UIFILE>\n\n"
            "  -h, -help                display this help and exit\n"
            "  -v, -version             display version\n"
            "  -d <dir>                 output directory\n"
            "  -x <xml file>            load custom configuration file\n"
            "  -p <package>             package of generated class file, relative to output dir\n"
            "  -tr <func>               use func() for i18n\n"
            "  -cp <optional path>      updates all .ui files based on the input path. $CLASSPATH\n"
            "                           is used if no argument is specified.\n"
            "\n", appName);
}


bool runJuic(const QFileInfo &uiFile, const QString &baseDir, const QString &package)
{
    Driver driver;
    driver.option().generator = Option::JavaGenerator;
    driver.option().javaOutputDirectory = baseDir;
    driver.option().javaPackage = package;
    driver.option().prefix = "Ui_";

    QString className = findClassName(uiFile);
    if (className.isEmpty())
        return false;

    QString javaFileName = driver.option().prefix + className + ".java";

    // Verify that we have the output directory.
    if (!ensurePath(driver.option().javaOutputDirectory)) {
        return false;
    }

    // Verify that the package subdirectory is ok
    if (!driver.option().javaPackage.isEmpty()) {
        QDir outDir(driver.option().javaOutputDirectory);
        QString subDir = QString(driver.option().javaPackage).replace(".", "/");
        if (!ensurePath(outDir.filePath(subDir)))
            return false;
    }

    ++num_processed_files;

    QString outFileName = baseDir + "/" + QString(package).replace(".", "/") + "/" + javaFileName;
    QFileInfo outFileInfo(outFileName);

    // File already generated
    if (outFileInfo.exists()
        && uiFile.lastModified() < QFileInfo(outFileName).lastModified()) {
        return true;
    }


    // Open the output file.
    QFile f(outFileName);
    if (!f.open(QIODevice::WriteOnly | QIODevice::Text)) {
        fprintf(stderr, "Failed to open output file: %s\n", qPrintable(f.fileName()));
        return false;
    }

    // Run UIC
    QTextStream stream(&f);
    if (!driver.uic(uiFile.absoluteFilePath(), &stream)) {
        fprintf(stderr, "Failed on input file: '%s'\n", qPrintable(uiFile.absoluteFilePath()));
        return false;
    }

    ++num_updated_files;

    fprintf(stdout, "updated: ");
    if (!package.isEmpty())
        fprintf(stdout, "%s.", qPrintable(package));
    fprintf(stdout, "%s%s\n", qPrintable(driver.option().prefix), qPrintable(className));

    return true;
}


bool process(const QString &rootPath, const QFileInfo &file)
{
    QString absFilePath = QDir::convertSeparators(file.absoluteFilePath());
    Q_ASSERT(absFilePath.length() > rootPath.length());
    QString relFilePath = absFilePath.mid(rootPath.length() + 1);

    QString package = QDir::convertSeparators(QFileInfo(relFilePath).path())
                      .replace(QDir::separator(), ".");
    if (package == QLatin1String("."))
        package = QString();

    return runJuic(file, rootPath, package);
}


void traverseClassPath(const QString &rootPath, const QDir &dir)
{
    QFileInfoList uiFiles = dir.entryInfoList(QStringList() << "*.ui", QDir::Files);

    for (int i=0; i<uiFiles.size(); ++i) {
        process(rootPath, uiFiles.at(i));
    }

    QFileInfoList subDirs = dir.entryInfoList(QDir::Dirs | QDir::NoDotAndDotDot);
    for (int i=0; i<subDirs.size(); ++i) {
        traverseClassPath(rootPath, QDir(subDirs.at(i).filePath()));
    }
}


void traverseAll()
{

    QString classPath = QString(getenv("CLASSPATH"));
    QStringList paths = classPath.split(ENV_SPLITTER);

    for (int i=0; i<paths.size(); ++i) {
        traverseClassPath(QFileInfo(paths.at(i)).absoluteFilePath(), QDir(paths.at(i)));
    }
}
