
#ifndef WRAPPER_H_
#define WRAPPER_H_

#include <iostream>
#include <QMap>

#include "generatorset.h"

/**
 * Class that parses and initializes actual generator.
 * Name of this class ought to be Generator, but until someone
 * has done some refactoring to generator classes
 */
class Wrapper {

    public:
        Wrapper(int argc, char *argv[]);

        static QString include_directory;

        int runJambiGenerator();

    private:
        void displayHelp(GeneratorSet* arg1);
        QMap<QString, QString> parseArguments(int argc, char *argv[]);
        void assignVariables();
        void handleArguments();

        QString default_file;
        QString default_system;

        QString fileName;
        QString typesystemFileName;
        QString pp_file;
        QStringList rebuild_classes;
        GeneratorSet *gs;
        QMap< QString, QString > args;

};

#endif
