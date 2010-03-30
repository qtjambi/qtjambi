
#include <QFileInfo>
#include <QStringList>
#include <QDir>

#include <cstdio>

#include "preprocesshandler.h"
#include "wrapper.h"

PreprocessHandler::PreprocessHandler(QString sourceFile, QString targetFile) :
        preprocess(env),
        ppconfig(":/trolltech/generator/parser/rpp/pp-qt-configuration")
{
    QFile file(ppconfig);
    if (!file.open(QFile::ReadOnly)) {
        std::fprintf(stderr, "Preprocessor configuration file not found '%s'\n", ppconfig);
        return;
    }
    
    QByteArray ba = file.readAll();
    file.close();
    preprocess.operator() (ba.constData(), ba.constData() + ba.size(), null_out);

    QStringList includes = setIncludes();

    foreach (QString include, includes)
        preprocess.push_include_path(QDir::convertSeparators(include).toStdString());

    QString currentDir = QDir::current().absolutePath();

    writeTargetFile(sourceFile, targetFile, currentDir);
}

void PreprocessHandler::writeTargetFile(QString sourceFile, QString targetFile, QString currentDir) {
    
    QFileInfo sourceInfo(sourceFile);
    QDir::setCurrent(sourceInfo.absolutePath());
    
    std::string result;
    result.reserve (20 * 1024); // 20K

    result += "# 1 \"builtins\"\n";
    result += "# 1 \"";
    result += sourceFile.toStdString();
    result += "\"\n";

    preprocess.file (sourceInfo.fileName().toStdString(),
                    rpp::pp_output_iterator<std::string> (result));

    QDir::setCurrent(currentDir);

    QFile f(targetFile);
    if (!f.open(QIODevice::WriteOnly | QIODevice::Text)) {
        fprintf(stderr, "Failed to write preprocessed file: %s\n", qPrintable(targetFile));
    }
    f.write(result.c_str(), result.length());
}

QStringList PreprocessHandler::setIncludes() {

#if defined Q_OS_WIN32
    const char *path_splitter = ";";
#else
    const char *path_splitter = ":";
#endif

    QStringList includes;
    includes << QString(".");

    // Includes from the command line
    //if (!commandLineIncludes.isEmpty())
        //includes += commandLineIncludes.split(path_splitter);

    // Include Qt
    QString libdir;
    if(Wrapper::library_dir != "") {
        libdir = Wrapper::library_dir;
    } else libdir = "/usr/include/qt4";
    
    includes << (libdir + "/QtXml");
    includes << (libdir + "/QtNetwork");
    includes << (libdir + "/QtCore");
    includes << (libdir + "/QtGui");
    includes << (libdir + "/QtOpenGL");
    includes << (libdir + "/phonon");
    includes << libdir;
    
    return includes;
}
