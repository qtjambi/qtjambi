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

#ifndef DOCPARSER_H
#define DOCPARSER_H

#include <QtCore/QString>


class AbstractMetaClass;
class AbstractMetaFunction;
class AbstractMetaEnum;
class AbstractMetaEnumValue;
class QDomDocument;

class DocParser
{
public:
    DocParser(const QString &docFile);
    ~DocParser();

    QString documentation(const AbstractMetaClass *meta_class) const;
    QString documentation(const AbstractMetaEnum *meta_enum) const;
    QString documentation(const AbstractMetaEnumValue *meta_enum) const;
    QString documentationForFunction(const QString &signature) const;
    QString documentationForSignal(const QString &signature) const;

private:
    QString documentationForFunction(const QString &signature, const QString &tag) const;
    void build();

    QString m_doc_file;
    QDomDocument *m_dom;
};

#endif // DOCPARSER_H
