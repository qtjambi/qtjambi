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

#ifndef MAIN_H
#define MAIN_H

#include "pp.h"

#include <QFile>
#include <QDir>

struct Preprocess
{
    static bool preprocess(const QString &sourceFile, const QString &targetFile, const QString &commandLineIncludes = QString())
    {
        rpp::pp_environment env;
        rpp::pp preprocess(env);

        rpp::pp_null_output_iterator null_out;

        const char *ppconfig = ":/trolltech/generator/parser/rpp/pp-qt-configuration";

        QFile file(ppconfig);
        if (!file.open(QFile::ReadOnly)) {
            fprintf(stderr, "Preprocessor configuration file not found '%s'\n", ppconfig);
            return false;
        }

        QByteArray ba = file.readAll();
        file.close();
        preprocess.operator() (ba.constData(), ba.constData() + ba.size(), null_out);

        QStringList includes;
        includes << QString(".");

#if defined(Q_OS_WIN32)
        char *path_splitter = ";";
#else
        char *path_splitter = ":";
#endif

        // Environment INCLUDE
        QString includePath = getenv("INCLUDE");
        if (!includePath.isEmpty())
            includes += includePath.split(path_splitter);

        // Includes from the command line
        if (!commandLineIncludes.isEmpty())
            includes += commandLineIncludes.split(path_splitter);

        // Include Qt
        QString qtdir = getenv ("QTDIR");
        if (qtdir.isEmpty()) {
            qWarning("QTDIR environment variable not set. This may cause problems with finding the necessary include files.");
        } else {
            qtdir += "/include";
            includes << (qtdir + "/QtXml");
            includes << (qtdir + "/QtNetwork");
            includes << (qtdir + "/QtCore");
            includes << (qtdir + "/QtGui");
            includes << (qtdir + "/QtOpenGL");
            includes << qtdir;
        }

        foreach (QString include, includes)
            preprocess.push_include_path(QDir::convertSeparators(include).toStdString());

        QString currentDir = QDir::current().absolutePath();
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

        return true;
    }
};

#endif // MAIN_H
