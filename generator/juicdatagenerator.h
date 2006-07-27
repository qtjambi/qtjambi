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

#ifndef JUICDATAGENERATOR_H
#define JUICDATAGENERATOR_H

#include "metajava.h"

class QDomDocument;
class QDomElement;

class JuicDataGenerator
{
public:
    void setClasses(const MetaJavaClassList &classes) { m_classes = classes; }
    MetaJavaClassList classes() const { return m_classes; }

    void setFileName(const QString &outputFile) { m_file_name = outputFile; }
    QString fileName() const { return m_file_name; }

    void generate();
    void generateSignatures(QDomDocument *doc_node, QDomElement *signatures_node);
    void generateHierarchy(QDomDocument *doc_node, QDomElement *hierarchy_node);
    void generateModifications(QDomDocument *doc_nod, QDomElement *mods_node);

private:
    MetaJavaClassList m_classes;
    QString m_file_name;
};

#endif // JUICDATAGENERATOR_H
