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


#ifndef JAMBI_LANGUAGE_EXTENSION_H
#define JAMBI_LANGUAGE_EXTENSION_H

#include <QtDesigner/QtDesigner>

class JambiLanguageExtension: public QDesignerLanguageExtension
{
public:
    virtual ~JambiLanguageExtension() {}
};

Q_DECLARE_EXTENSION_INTERFACE(JambiLanguageExtension, "com.trolltech.jambi.Language")

#endif // JAMBI_LANGUAGE_EXTENSION_H
