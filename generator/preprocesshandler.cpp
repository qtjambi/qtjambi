
#include <QFileInfo>
#include <QStringList>
#include <QDir>
#include <QDebug>

#include <cstdio>

#include "preprocesshandler.h"
#include "wrapper.h"

PreprocessHandler::PreprocessHandler(QString sourceFile, QString targetFile, const QString& phononinclude) :
        preprocess(env),
        ppconfig(":/trolltech/generator/parser/rpp/pp-qt-configuration"),
        sourceFile(sourceFile),
        targetFile(targetFile),
        phononinclude(phononinclude) {
    //empty space for useless comments
}

bool PreprocessHandler::handler() {
    QFile file(ppconfig);
    if (!file.open(QFile::ReadOnly)) {
        std::fprintf(stderr, "Preprocessor configuration file not found '%s'\n", ppconfig);
        return false;
    }

    QByteArray ba = file.readAll();
    file.close();
    preprocess.operator()(ba.constData(), ba.constData() + ba.size(), null_out);

    QStringList includes = setIncludes();

    foreach(QString include, includes)
    preprocess.push_include_path(QDir::convertSeparators(include).toStdString());

    QString currentDir = QDir::current().absolutePath();

    writeTargetFile(sourceFile, targetFile, currentDir);

    return true;
}

void PreprocessHandler::writeTargetFile(QString sourceFile, QString targetFile, QString currentDir) {

    QFileInfo sourceInfo(sourceFile);
    QDir::setCurrent(sourceInfo.absolutePath());

    std::string result;
    result.reserve(20 * 1024);  // 20K

    result += "# 1 \"builtins\"\n";
    result += "# 1 \"";
    result += sourceFile.toStdString();
    result += "\"\n";

    qDebug() << "Processing source" << sourceInfo.absolutePath() << sourceInfo.fileName();
    preprocess.file(sourceInfo.fileName().toStdString(),
                    rpp::pp_output_iterator<std::string> (result));

    QDir::setCurrent(currentDir);

    QFile f(targetFile);
    if (!f.open(QIODevice::Append | QIODevice::Text)) {
        fprintf(stderr, "Failed to write preprocessed file: %s\n", qPrintable(targetFile));
    }
    f.write(result.c_str(), result.length());
}

QStringList PreprocessHandler::setIncludes() {

    QStringList includes;
    includes << QString(".");

    // Include Qt
    QString includedir;
    if (Wrapper::include_directory != "") {
        includedir = Wrapper::include_directory;
#if defined(Q_OS_MAC)
    } else includedir = "/Library/Frameworks";
#else
    }
    else includedir = "/usr/include/qt4";
#endif

    QString phonon_include_dir;
    if (!phononinclude.isEmpty()) {
        phonon_include_dir = phononinclude;
    } else {
#if defined(Q_OS_MAC)
        phonon_include_dir = "/Library/Frameworks/phonon.framework/Headers";
#else
        phonon_include_dir = includedir;
#endif
    }
    includes << phonon_include_dir;
    includes << includedir;

    return includes;
}
