#ifndef DOCPARSER_H
#define DOCPARSER_H

#include <QtCore/QString>


class MetaJavaClass;
class MetaJavaFunction;
class MetaJavaEnum;
class MetaJavaEnumValue;
class QDomDocument;

class DocParser
{
public:
    DocParser(const QString &docFile);
    ~DocParser();

    QString documentation(const MetaJavaClass *meta_class) const;
    QString documentation(const MetaJavaEnum *meta_enum) const;
    QString documentation(const MetaJavaEnumValue *meta_enum) const;
    QString documentationForFunction(const QString &signature) const;
    QString documentationForSignal(const QString &signature) const;

private:
    QString documentationForFunction(const QString &signature, const QString &tag) const;
    void build();

    QString m_doc_file;
    QDomDocument *m_dom;
};

#endif // DOCPARSER_H
