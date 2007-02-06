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
        NoOption                 = 0x00000,
        BoxedPrimitive           = 0x00001,
        ExcludeConst             = 0x00002,
        ExcludeReference         = 0x00004,
        UseNativeIds             = 0x00008, 

        EnumAsInts               = 0x00010,
        SkipName                 = 0x00020,
        NoCasts                  = 0x00040,
        SkipReturnType           = 0x00080,
        OriginalName             = 0x00100,
        ShowStatic               = 0x00200,
        UnderscoreSpaces         = 0x00400,
        ForceEnumCast            = 0x00800,
        ArrayAsPointer           = 0x01000,
        VirtualCall              = 0x02000,
        SkipTemplateParameters   = 0x04000,
        SkipAttributes           = 0x08000,
        OriginalTypeDescription  = 0x10000,
        SkipRemovedArguments     = 0x20000,
        IncludeDefaultExpression = 0x40000,
        NoReturnStatement        = 0x80000,

        ForceValueType           = ExcludeReference | ExcludeConst
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
