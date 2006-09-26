#include "qdocgenerator.h"

#include "reporthandler.h"
#include "typesystem.h"

#include <QtCore/QDir>

QDocGenerator::QDocGenerator() { }

QString QDocGenerator::subDirectoryForClass(const MetaJavaClass *) const
{
    return "doc/japi";
}

QString QDocGenerator::fileNameForClass(const MetaJavaClass *) const
{
    return "qdoc.japi";
}

void QDocGenerator::generate()
{
    QDir dir(outputDirectory() + "/" + subDirectoryForClass(0));
    dir.mkpath(dir.absolutePath());

    QFile f(dir.absoluteFilePath(fileNameForClass(0)));
    if (!f.open(QIODevice::WriteOnly)) {
        ReportHandler::warning(QString("failed to open file '%1' for writing")
            .arg(f.fileName()));
        return;
    }

    {
        QTextStream s(&f);
        s << "<japi>" << endl;

        MetaJavaClassList clazzes = classes();
        foreach (MetaJavaClass *cls, clazzes) {
            if (shouldGenerate(cls)) {
                write(s, cls);
            }
        }

        s << "</japi>" << endl;
    }
}

// copy-paste from linguist/shared/metatranslator.cpp
static QString numericEntity( int ch )
{
    return QString( ch <= 0x20 ? "<byte value=\"x%1\"/>" : "&#x%1;" )
           .arg( ch, 0, 16 );
}

static QString protect( const QByteArray& str )
{
    QString result;
    int len = (int) str.length();
    for ( int k = 0; k < len; k++ ) {
        switch( str[k] ) {
        case '\"':
            result += QString( "&quot;" );
            break;
        case '&':
            result += QString( "&amp;" );
            break;
        case '>':
            result += QString( "&gt;" );
            break;
        case '<':
            result += QString( "&lt;" );
            break;
        case '\'':
            result += QString( "&apos;" );
            break;
        default:
            if ( (uchar) str[k] < 0x20 && str[k] != '\n' )
                result += numericEntity( (uchar) str[k] );
            else
                result += str[k];
        }
    }
    return result;
}


void QDocGenerator::write(QTextStream &s, const MetaJavaFunction *java_function)
{
    if (java_function->isModifiedRemoved(MetaJavaFunction::JavaFunction))
        return;

    QHash<int, bool> disabled_params;
    uint included_attributes = 0;
    uint excluded_attributes = 0;
    setupForFunction(java_function, &included_attributes, &excluded_attributes, &disabled_params);
    QString signature = functionSignature(java_function, included_attributes, excluded_attributes);

    s << "<method java=\"" << protect(signature.toUtf8()) << "\"" << endl
      << "    cpp=\"" << protect(java_function->signature().toUtf8()) << "\"";

    if (disabled_params.count() > 0) {
        s << endl << "    steals-ownership-of=\"";
        MetaJavaArgumentList arguments = java_function->arguments();
        for (int i=0; i<arguments.count(); ++i) {
            if (disabled_params.value(i + 1, false)) {
                const MetaJavaArgument *arg = arguments.at(i);
                if (i > 0)
                    s << ",";
                s << protect(arg->argumentName().toUtf8());
            }
        }
        s << "\"";
    }
    s << " />" << endl;
}

void QDocGenerator::write(QTextStream &s, const MetaJavaEnumValue *java_enum_value)
{
    s << "<enum-value java=\"" << protect(java_enum_value->name().toUtf8()) << "\"" << endl
      << "    cpp=\"" << protect(java_enum_value->name().toUtf8()) << "\"/>" << endl;
}

void QDocGenerator::write(QTextStream &s, const MetaJavaEnum *java_enum)
{
    s << "<enum java=\"" << protect(java_enum->name().toUtf8()) << "\"" << " cpp=\"" << protect(java_enum->name().toUtf8()) << "\">" << endl;
    MetaJavaEnumValueList values = java_enum->values();
    foreach (MetaJavaEnumValue *value, values) {
        write(s, value);
    }
    s << "</enum>" << endl;
}

void QDocGenerator::write(QTextStream &s, const MetaJavaField *java_field)
{
    QHash<int, bool> disabled_params;
    uint included_attributes = 0;
    uint excluded_attributes = 0;
    setupForFunction(java_field->getter(), &included_attributes, &excluded_attributes, &disabled_params);
    s << "<variablegetter java=\"" << protect(functionSignature(java_field->getter(), included_attributes, excluded_attributes).toUtf8())
      << "\"" << endl
      << "    cpp=\"" << protect(java_field->name().toUtf8()) << "\" />" << endl;

    included_attributes = 0;
    excluded_attributes = 0;
    setupForFunction(java_field->setter(), &included_attributes, &excluded_attributes, &disabled_params);
    s << "<variablesetter java=\"" << protect(functionSignature(java_field->setter(), included_attributes, excluded_attributes).toUtf8())
      << "\"" << endl
      << "    cpp=\"" << protect(java_field->name().toUtf8()) << "\" />" << endl;
}

void QDocGenerator::write(QTextStream &s, const MetaJavaClass *java_class)
{
    s << "<class" << endl
      << "   java=\"" << protect(java_class->name().toUtf8()) << "\"" << endl
      << "   cpp=\"" << protect(java_class->typeEntry()->qualifiedCppName().toUtf8()) << "\"" << endl
      << "   java-extends=\"" << protect(java_class->baseClass() ? java_class->baseClass()->name().toUtf8() : "") << "\"" << endl;

    if (java_class->typeEntry()->isObject()) {
        const ObjectTypeEntry *ot = static_cast<const ObjectTypeEntry *>(java_class->typeEntry());
        s << "   memory-managed=\"" << ot->isMemoryManaged() << "\"" << endl;
    }

    MetaJavaClassList interfaces = java_class->interfaces();
    if (interfaces.count() > 0) {
        s << "   javaimplements=\"";
        for (int i=0; i<interfaces.count(); ++i) {
            const MetaJavaClass *interfaze = interfaces.at(i);
            if (i > 0)
                s << ",";
            s << protect(interfaze->name().toUtf8());
        }
        s << "\"" << endl;
    }

    s << "   type=\"";
    if (java_class->isInterface())
        s << "interface";
    else if (java_class->typeEntry()->isValue())
        s << "value";
    else
        s << "object";
    s << "\">" << endl;

    // Write functions
    MetaJavaFunctionList functions = java_class->functionsInJava();
    foreach (MetaJavaFunction *f, functions) {
        write(s, f);
    }

    // Write enums
    MetaJavaEnumList enums = java_class->enums();
    foreach (MetaJavaEnum *e, enums) {
        write(s, e);
    }

    // Write setters and getters
    MetaJavaFieldList fields = java_class->fields();
    foreach (MetaJavaField *f, fields) {
        write(s, f);
    }

    s << "</class>" << endl;
}
