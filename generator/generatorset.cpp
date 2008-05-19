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

#include "generatorset.h"

GeneratorSet::GeneratorSet() :
    outDir("."),
    printStdout(false)
{}

bool GeneratorSet::readParameters(const QMap<QString, QString> args) {
    if (args.contains("output-directory")) {
        outDir = args.value("output-directory");
    }

    printStdout = args.contains("print-stdout");

    return !(args.contains("help") || args.contains("h") || args.contains("?"));
}
