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

#ifndef GENERATOR_SET_JAVA_H
#define GENERATOR_SET_JAVA_H

#include "generatorset.h"
#include "metajavabuilder.h"

class GeneratorSetJava : public GeneratorSet
{
    Q_OBJECT

public:
    GeneratorSetJava();

    QString usage();
    bool readParameters(const QMap<QString, QString> args);

    void buildModel(const QString pp_file);
    void dumpObjectTree();

    QString generate();

private:

    bool no_java;
    bool no_cpp_h;
    bool no_cpp_impl;
    bool no_metainfo;
    bool build_class_list;
    bool build_qdoc_japi;
    bool docs_enabled;
    bool do_ui_convert;

    QString doc_dir;
    QString ui_file_name;
    QString custom_widgets;

    MetaJavaBuilder builder;

};

#endif // GENERATOR_SET_JAVA_H
