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

#include "classlistgenerator.h"

QString ClassListGenerator::fileName() const { return "qtjambi-classes.qdoc"; }

static bool class_sorter(AbstractMetaClass *a, AbstractMetaClass *b)
{
    return a->name() < b->name();
}

void ClassListGenerator::generate()
{
    QFile f(fileName());
    if (f.open(QFile::WriteOnly)) {
        QTextStream s(&f);

        s << "/****************************************************************************" << endl
          << "**" << endl
          << "** This is a generated file, please don't touch." << endl
          << "**" << endl
          << "****************************************************************************/" << endl << endl;

        s << "/*!" << endl
          << "\\page qtjambi-classes.html" << endl << endl
          << "\\title Qt Jambi's classes" << endl << endl
          << "This is a list of all Qt Jambi classes." << endl << endl
          << "\\table 100%" << endl;

        AbstractMetaClassList classes = Generator::classes();
        qSort(classes.begin(), classes.end(), class_sorter);

        int numColumns = 4;
        int numRows = (classes.size() + numColumns - 1) / numColumns;

        for (int i = 0; i < numRows; ++i) {
            s << endl << "\\row ";
            for (int j=0; j<numColumns; ++j) {
                if (classes.value(i + j * numRows)) {
                    s << "\\o \\l{" << classes.value(i + j * numRows)->qualifiedCppName()
                      << "}{" << classes.value(i + j * numRows)->name() << "} ";
                }
            }

        }

        s << endl << "\\endtable" << endl
          << "*/" << endl;
    }
}
