
#ifndef ASTTOXML
#define ASTTOXML

#include "codemodel.h"

#include <QString>
#include <QXmlStreamWriter>

void astToXML(const QString name);
void writeOutNamespace(QXmlStreamWriter &s, NamespaceModelItem &item);
void writeOutEnum(QXmlStreamWriter &s, EnumModelItem &item);
void writeOutFunction(QXmlStreamWriter &s, FunctionModelItem &item);
void writeOutClass(QXmlStreamWriter &s, ClassModelItem &item);


#endif // ASTTOXML
