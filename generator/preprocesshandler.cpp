
#include <QFileInfo>
#include <QStringList>
#include <QDir>
#include <QDebug>

#include <cstdio>

#include "preprocesshandler.h"
#include "wrapper.h"

PreprocessHandler::PreprocessHandler(QString sourceFile, QString targetFile, const QString &phononInclude, const QStringList &includePathList) :
        preprocess(env),
        ppconfig(":/trolltech/generator/parser/rpp/pp-qt-configuration"),
        sourceFile(sourceFile),
        targetFile(targetFile),
        phononInclude(phononInclude),
        includePathList(includePathList) {
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

    // It is important any explicitly given phonon include dir is before the main Qt include
    //  directory in the search order.  This is so that on a build system that has both a Qt
    //  Phonon and another Phonon implementation (like from KDE) it should find the ecplicitly
    //  given include location first.
    QString phonon_include_dir;
    if (!phononInclude.isEmpty()) {
        phonon_include_dir = phononInclude;
    } else {
#if defined(Q_OS_MAC)
        phonon_include_dir = "/Library/Frameworks/phonon.framework/Headers";
#endif
    }
    if (!phonon_include_dir.isNull())
        includes << phonon_include_dir;

    // Include Qt
    QString includedir;
    if (!Wrapper::include_directory.isNull()) {
        includedir = Wrapper::include_directory;
    } else {
#if defined(Q_OS_MAC)
        includedir = "/Library/Frameworks";
#else
        includedir = "/usr/include/qt4";
#endif
    }
    if (!includedir.isNull())
        includes << includedir;

    // Additional include locations from command line
    if (!includePathList.isEmpty())
        includes << includePathList;

    return includes;
}
