#ifndef QDOC_GENERATOR
#define QDOC_GENERATOR

#include "javagenerator.h"
#include "metajava.h"

class QDocGenerator: public JavaGenerator
{
public:
    QDocGenerator();

    virtual void generate();
    virtual QString subDirectoryForClass(const MetaJavaClass *java_class) const;
    virtual QString fileNameForClass(const MetaJavaClass *java_class) const;
    virtual void write(QTextStream &s, const MetaJavaClass *java_class);
    virtual void write(QTextStream &s, const MetaJavaEnumValue *java_enum_value);
    virtual void write(QTextStream &s, const MetaJavaEnum *java_enum);
    virtual void write(QTextStream &s, const MetaJavaFunction *java_function);
    virtual void write(QTextStream &s, const MetaJavaField *java_field);
};

#endif // QDOC_GENERATOR