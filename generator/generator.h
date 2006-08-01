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

#ifndef GENERATOR_H
#define GENERATOR_H

#include "metajava.h"
#include "typesystem.h"

#include "codemodel.h"

#include <QObject>
#include <QFile>

class Generator : public QObject
{
    Q_OBJECT

    Q_PROPERTY(QString outputDirectory READ outputDirectory WRITE setOutputDirectory);

public:
    enum Option {
        NoOption                = 0x0000,
        BoxedPrimitive          = 0x0001,
        ExcludeConst            = 0x0002,
        ExcludeReference        = 0x0004,
        UseNativeIds            = 0x0008,

        EnumAsInts              = 0x0010,
        SkipName                = 0x0020,
        NoCasts                 = 0x0040,
        SkipReturnType          = 0x0080,
        OriginalName            = 0x0100,
        ShowStatic              = 0x0200,
        UnderscoreSpaces        = 0x0400,
        ForceEnumCast           = 0x0800,
        ArrayAsPointer          = 0x1000,
        VirtualCall             = 0x2000,
        SkipTemplateParameters  = 0x4000,

        DeclareOnly             = 0x8000,

        ForceValueType          = ExcludeReference | ExcludeConst
    };

    Generator();

    void setClasses(const MetaJavaClassList &classes) { m_java_classes = classes; }
    MetaJavaClassList classes() const { return m_java_classes; }

    QString outputDirectory() const { return m_out_dir; }
    void setOutputDirectory(const QString &outDir) { m_out_dir = outDir; }
    virtual void generate();
    void printClasses();

    int numGenerated() { return m_num_generated; }

    virtual bool shouldGenerate(const MetaJavaClass *) const { return true; }
    virtual QString subDirectoryForClass(const MetaJavaClass *java_class) const;
    virtual QString fileNameForClass(const MetaJavaClass *java_class) const;
    virtual void write(QTextStream &s, const MetaJavaClass *java_class);

protected:
    void verifyDirectoryFor(const QFile &file);

    MetaJavaClassList m_java_classes;
    int m_num_generated;
    QString m_out_dir;
};

#endif // GENERATOR_H
