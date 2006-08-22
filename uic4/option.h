/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ Trolltech AS. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef OPTION_H
#define OPTION_H

#include <QString>

struct Option
{
    enum Generator
    {
        CppGenerator,
        JavaGenerator
    };

    unsigned int headerProtection : 1;
    unsigned int copyrightHeader : 1;
    unsigned int generateImplemetation : 1;
    unsigned int generateNamespace : 1;
    unsigned int autoConnection : 1;
    unsigned int dependencies : 1;
    Generator generator;

    QString inputFile;
    QString outputFile;
    QString indent;
    QString prefix;
    QString postfix;
    QString translateFunction;
    QString uic3;
#ifdef QT_UIC_JAVA_GENERATOR
    QString javaPackage;
    QString javaOutputDirectory;
#endif

    Option()
        : headerProtection(1),
          copyrightHeader(1),
          generateImplemetation(0),
          generateNamespace(1),
          autoConnection(1),
          dependencies(0),
          generator(CppGenerator),
          prefix(QLatin1String("Ui_"))
    { indent.fill(QLatin1Char(' '), 4); }
};

#endif // OPTION_H
