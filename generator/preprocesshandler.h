
#ifndef PREPROCESSHANDLER_H_
#define PREPROCESSHANDLER_H_

#include <QString>

#include "parser/rpp/pp-iterator.h"
#include "parser/rpp/pp-engine-bits.h"
#include "parser/rpp/pp-environment.h"

class PreprocessHandler {

    public:
        PreprocessHandler(QString sourceFile, QString targetFile, const QString &phononInclude, const QStringList &includePathList);

        bool handler();

    private:
        rpp::pp_environment env;
        rpp::pp preprocess;
        rpp::pp_null_output_iterator null_out;

        const char *ppconfig;
        QString sourceFile;
        QString targetFile;
        QString phononInclude;
        QStringList includePathList;

        QStringList setIncludes();

        /**
         * Reads through master include file and writes preprocessed file for actual
         * processing of headers.
         *
         * TODO: more indepth description of this system somewhere
         */
        void writeTargetFile(QString sourceFile, QString targetFile, QString currentDir);
};

#endif
