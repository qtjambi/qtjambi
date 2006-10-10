#ifndef DOCPARSER_H
#define DOCPARSER_H

#include <QtCore/QString>


class MetaJavaClass;
class MetaJavaFunction;
class QDomDocument;

class DocParser
{
public:
    DocParser(const QString &docFile);
    ~DocParser();

    QString documentation(const MetaJavaClass *meta_class) const;
    QString documentation(const MetaJavaFunction *meta_function) const;

private:
    void build();

    QString m_doc_file;
    QDomDocument *m_dom;
};

#endif // DOCPARSER_H
